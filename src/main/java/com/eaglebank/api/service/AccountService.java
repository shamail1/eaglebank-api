package com.eaglebank.api.service;

import com.eaglebank.api.constants.ApplicationConstants;
import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;
import com.eaglebank.api.enums.AccountType;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.UserRepository;
import com.eaglebank.api.util.EntityMapper;
import com.eaglebank.api.util.IdGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AccountService {
    
    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    
    public AccountService(BankAccountRepository bankAccountRepository, 
                         UserRepository userRepository) {
        this.bankAccountRepository = bankAccountRepository;
        this.userRepository = userRepository;
    }
    
    public BankAccountResponse createAccount(CreateBankAccountRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        
        AccountType accountType = AccountType.fromString(request.accountType());
        if (accountType == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account type");
        }
        
        String accountNumber = IdGenerator.generateAccountNumber();

        while (bankAccountRepository.existsById(accountNumber)) {
            accountNumber = IdGenerator.generateAccountNumber();
        }
        
        BankAccount account = new BankAccount();
        account.setAccountNumber(accountNumber);
        account.setSortCode(ApplicationConstants.ACCOUNT_SORT_CODE);
        account.setName(request.name());
        account.setAccountType(accountType.getValue());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(ApplicationConstants.ACCOUNT_DEFAULT_CURRENCY);
        account.setUser(user);
        
        BankAccount savedAccount = bankAccountRepository.save(account);
        return EntityMapper.toBankAccountResponse(savedAccount);
    }
    
    public ListBankAccountsResponse listAccounts(String userId) {
        List<BankAccount> accounts = bankAccountRepository.findByUserId(userId);
        List<BankAccountResponse> accountResponses = accounts.stream()
                .map(EntityMapper::toBankAccountResponse)
                .toList();
        return new ListBankAccountsResponse(accountResponses);
    }
    
    public BankAccountResponse getAccountByAccountNumber(String accountNumber, String userId) {
        BankAccount account = bankAccountRepository.findByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> {
                    if (bankAccountRepository.existsById(accountNumber)) {
                        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found");
                });
        
        return EntityMapper.toBankAccountResponse(account);
    }
    
    public BankAccountResponse updateAccount(String accountNumber, UpdateBankAccountRequest request, String userId) {
        BankAccount account = bankAccountRepository.findByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> {
                    if (bankAccountRepository.existsById(accountNumber)) {
                        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found");
                });
        
        if (request.name() != null) {
            account.setName(request.name());
        }
        if (request.accountType() != null) {
            AccountType accountType = AccountType.fromString(request.accountType());
            if (accountType == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid account type");
            }
            account.setAccountType(accountType.getValue());
        }
        
        BankAccount updatedAccount = bankAccountRepository.save(account);
        return EntityMapper.toBankAccountResponse(updatedAccount);
    }
    
    public void deleteAccount(String accountNumber, String userId) {
        BankAccount account = bankAccountRepository.findByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> {
                    if (bankAccountRepository.existsById(accountNumber)) {
                        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found");
                });
        
        bankAccountRepository.delete(account);
    }
    
    public BankAccount getAccountEntity(String accountNumber, String userId) {
        return bankAccountRepository.findByAccountNumberAndUserId(accountNumber, userId)
                .orElseThrow(() -> {
                    if (bankAccountRepository.existsById(accountNumber)) {
                        return new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
                    }
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "Bank account not found");
                });
    }
}

