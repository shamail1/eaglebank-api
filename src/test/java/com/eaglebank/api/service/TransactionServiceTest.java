package com.eaglebank.api.service;

import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.Transaction;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.repository.BankAccountRepository;
import com.eaglebank.api.repository.TransactionRepository;
import com.eaglebank.api.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountService accountService;

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User testUser;
    private BankAccount testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("usr-123abc");
        testUser.setName("Test User");

        testAccount = new BankAccount();
        testAccount.setAccountNumber("01234567");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCurrency("GBP");
        testAccount.setUser(testUser);

        testTransaction = new Transaction();
        testTransaction.setId("tan-123abc");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setCurrency("GBP");
        testTransaction.setType("deposit");
        testTransaction.setAccount(testAccount);
        testTransaction.setUser(testUser);
        testTransaction.setCreatedTimestamp(Instant.now());
    }

    @Test
    void createTransaction_ShouldCreateDeposit_WhenValidRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("100.00"),
                "GBP",
                "deposit",
                "Test deposit"
        );

        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(transactionRepository.existsById(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createTransaction("01234567", request, "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("tan-123abc");
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(response.type()).isEqualTo("deposit");

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(bankAccountRepository).save(any(BankAccount.class));
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldCreateWithdrawal_WhenValidRequest() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("50.00"),
                "GBP",
                "withdrawal",
                "Test withdrawal"
        );

        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(transactionRepository.existsById(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(testAccount);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createTransaction("01234567", request, "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.type()).isEqualTo("deposit");
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenInsufficientFunds() {
        testAccount.setBalance(new BigDecimal("50.00"));
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("100.00"),
                "GBP",
                "withdrawal",
                "Test withdrawal"
        );

        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> transactionService.createTransaction("01234567", request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenInvalidCurrency() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("100.00"),
                "USD",
                "deposit",
                "Test deposit"
        );

        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> transactionService.createTransaction("01234567", request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void createTransaction_ShouldThrowException_WhenInvalidTransactionType() {
        CreateTransactionRequest request = new CreateTransactionRequest(
                new BigDecimal("100.00"),
                "GBP",
                "invalid",
                "Test transaction"
        );

        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> transactionService.createTransaction("01234567", request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void listTransactions_ShouldReturnListOfTransactions_WhenTransactionsExist() {
        List<Transaction> transactions = Arrays.asList(testTransaction);
        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(transactionRepository.findByAccountAccountNumberOrderByCreatedTimestampDesc("01234567")).thenReturn(transactions);

        ListTransactionsResponse response = transactionService.listTransactions("01234567", "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.transactions()).hasSize(1);
        assertThat(response.transactions().get(0).id()).isEqualTo("tan-123abc");

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(transactionRepository).findByAccountAccountNumberOrderByCreatedTimestampDesc("01234567");
    }

    @Test
    void listTransactions_ShouldReturnEmptyList_WhenNoTransactionsExist() {
        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(transactionRepository.findByAccountAccountNumberOrderByCreatedTimestampDesc("01234567")).thenReturn(List.of());

        ListTransactionsResponse response = transactionService.listTransactions("01234567", "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.transactions()).isEmpty();

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(transactionRepository).findByAccountAccountNumberOrderByCreatedTimestampDesc("01234567");
    }

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(transactionRepository.findByIdAndAccountAccountNumber("tan-123abc", "01234567"))
                .thenReturn(Optional.of(testTransaction));

        TransactionResponse response = transactionService.getTransactionById("01234567", "tan-123abc", "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo("tan-123abc");
        assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("100.00"));

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(transactionRepository).findByIdAndAccountAccountNumber("tan-123abc", "01234567");
    }

    @Test
    void getTransactionById_ShouldThrowNotFound_WhenTransactionDoesNotExist() {
        when(accountService.getAccountEntity("01234567", "usr-123abc")).thenReturn(testAccount);
        when(transactionRepository.findByIdAndAccountAccountNumber("tan-123abc", "01234567"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> transactionService.getTransactionById("01234567", "tan-123abc", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(accountService).getAccountEntity("01234567", "usr-123abc");
        verify(transactionRepository).findByIdAndAccountAccountNumber("tan-123abc", "01234567");
    }
}

