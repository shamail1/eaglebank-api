package com.eaglebank.api.service;

import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.Transaction;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.enums.Currency;
import com.eaglebank.api.enums.TransactionType;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;
import com.eaglebank.api.repository.UserRepository;
import com.eaglebank.api.util.EntityMapper;
import com.eaglebank.api.util.IdGenerator;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Transactional
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountService accountService;
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    
    public TransactionService(TransactionRepository transactionRepository,
                             AccountService accountService,
                             BankAccountRepository bankAccountRepository,
                             UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }
    
    public TransactionResponse createTransaction(String accountNumber, CreateTransactionRequest request, String userId) {
        BankAccount account = accountService.getAccountEntity(accountNumber, userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        Currency currency = Currency.fromString(request.currency());
        if (currency == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid currency");
        }
        
        TransactionType transactionType = TransactionType.fromString(request.type());
        if (transactionType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid transaction type");
        }
        
        BigDecimal amount = request.amount().setScale(2, RoundingMode.HALF_UP);
        if (transactionType == TransactionType.WITHDRAWAL) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, 
                        "Insufficient funds to process transaction");
            }
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }
        
        BankAccount persistedAccount;
        try {
            persistedAccount = bankAccountRepository.save(account);
        } catch (OptimisticLockingFailureException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, 
                    "Concurrent account update detected. Please retry the transaction.", ex);
        }
        
        String transactionId = IdGenerator.generateTransactionId();

        while (transactionRepository.existsById(transactionId)) {
            transactionId = IdGenerator.generateTransactionId();
        }
        
        Transaction transaction = new Transaction();
        transaction.setId(transactionId);
        transaction.setAmount(amount);
        transaction.setCurrency(currency.getCode());
        transaction.setType(transactionType.getValue());
        transaction.setReference(request.reference());
        transaction.setAccount(persistedAccount);
        transaction.setUser(user);
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return EntityMapper.toTransactionResponse(savedTransaction);
    }
    
    public ListTransactionsResponse listTransactions(String accountNumber, String userId) {

        accountService.getAccountEntity(accountNumber, userId);
        
        List<Transaction> transactions = transactionRepository.findByAccountAccountNumberOrderByCreatedTimestampDesc(accountNumber);
        List<TransactionResponse> transactionResponses = transactions.stream()
                .map(EntityMapper::toTransactionResponse)
                .toList();
        return new ListTransactionsResponse(transactionResponses);
    }
    
    public TransactionResponse getTransactionById(String accountNumber, String transactionId, String userId) {

        accountService.getAccountEntity(accountNumber, userId);
        
        Transaction transaction = transactionRepository.findByIdAndAccountAccountNumber(transactionId, accountNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Transaction not found"));
        
        return EntityMapper.toTransactionResponse(transaction);
    }
}

