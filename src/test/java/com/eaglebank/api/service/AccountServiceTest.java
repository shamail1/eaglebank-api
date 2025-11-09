package com.eaglebank.api.service;

import com.eaglebank.api.constants.ApplicationConstants;
import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;
import com.eaglebank.api.repository.BankAccountRepository;
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
class AccountServiceTest {

    @Mock
    private BankAccountRepository bankAccountRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AccountService accountService;

    private User testUser;
    private BankAccount testAccount;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("usr-123abc");
        testUser.setName("Test User");
        testUser.setEmail("test@example.com");

        testAccount = new BankAccount();
        testAccount.setAccountNumber("01234567");
        testAccount.setSortCode(ApplicationConstants.ACCOUNT_SORT_CODE);
        testAccount.setName("Test Account");
        testAccount.setAccountType("personal");
        testAccount.setBalance(BigDecimal.ZERO);
        testAccount.setCurrency(ApplicationConstants.ACCOUNT_DEFAULT_CURRENCY);
        testAccount.setUser(testUser);
        testAccount.setCreatedTimestamp(Instant.now());
        testAccount.setUpdatedTimestamp(Instant.now());
    }

    @Test
    void createAccount_ShouldReturnBankAccountResponse_WhenValidRequest() {
        CreateBankAccountRequest request = new CreateBankAccountRequest("Test Account", "personal");

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));
        when(bankAccountRepository.existsById(anyString())).thenReturn(false);
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(testAccount);

        BankAccountResponse response = accountService.createAccount(request, "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("01234567");
        assertThat(response.name()).isEqualTo("Test Account");
        assertThat(response.accountType()).isEqualTo("personal");
        assertThat(response.balance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(response.currency()).isEqualTo(ApplicationConstants.ACCOUNT_DEFAULT_CURRENCY);
        assertThat(response.sortCode()).isEqualTo(ApplicationConstants.ACCOUNT_SORT_CODE);

        verify(userRepository).findById("usr-123abc");
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void createAccount_ShouldThrowException_WhenUserNotFound() {
        CreateBankAccountRequest request = new CreateBankAccountRequest("Test Account", "personal");

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createAccount(request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(userRepository).findById("usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void createAccount_ShouldThrowException_WhenInvalidAccountType() {
        CreateBankAccountRequest request = new CreateBankAccountRequest("Test Account", "invalid");

        when(userRepository.findById("usr-123abc")).thenReturn(Optional.of(testUser));

        assertThatThrownBy(() -> accountService.createAccount(request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(userRepository).findById("usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void listAccounts_ShouldReturnListOfAccounts_WhenAccountsExist() {
        List<BankAccount> accounts = Arrays.asList(testAccount);
        when(bankAccountRepository.findByUserId("usr-123abc")).thenReturn(accounts);

        ListBankAccountsResponse response = accountService.listAccounts("usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.accounts()).hasSize(1);
        assertThat(response.accounts().get(0).accountNumber()).isEqualTo("01234567");

        verify(bankAccountRepository).findByUserId("usr-123abc");
    }

    @Test
    void listAccounts_ShouldReturnEmptyList_WhenNoAccountsExist() {
        when(bankAccountRepository.findByUserId("usr-123abc")).thenReturn(List.of());

        ListBankAccountsResponse response = accountService.listAccounts("usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.accounts()).isEmpty();

        verify(bankAccountRepository).findByUserId("usr-123abc");
    }

    @Test
    void getAccountByAccountNumber_ShouldReturnAccount_WhenAccountExistsAndBelongsToUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.of(testAccount));

        BankAccountResponse response = accountService.getAccountByAccountNumber("01234567", "usr-123abc");

        assertThat(response).isNotNull();
        assertThat(response.accountNumber()).isEqualTo("01234567");

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
    }

    @Test
    void getAccountByAccountNumber_ShouldThrowForbidden_WhenAccountBelongsToDifferentUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.existsById("01234567")).thenReturn(true);

        assertThatThrownBy(() -> accountService.getAccountByAccountNumber("01234567", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository).existsById("01234567");
    }

    @Test
    void getAccountByAccountNumber_ShouldThrowNotFound_WhenAccountDoesNotExist() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.existsById("01234567")).thenReturn(false);

        assertThatThrownBy(() -> accountService.getAccountByAccountNumber("01234567", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.NOT_FOUND);

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository).existsById("01234567");
    }

    @Test
    void updateAccount_ShouldReturnUpdatedAccount_WhenValidRequest() {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest("Updated Name", null);

        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.of(testAccount));
        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(testAccount);

        BankAccountResponse response = accountService.updateAccount("01234567", request, "usr-123abc");

        assertThat(response).isNotNull();
        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository).save(any(BankAccount.class));
    }

    @Test
    void updateAccount_ShouldThrowException_WhenInvalidAccountType() {
        UpdateBankAccountRequest request = new UpdateBankAccountRequest(null, "invalid");

        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.of(testAccount));

        assertThatThrownBy(() -> accountService.updateAccount("01234567", request, "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.BAD_REQUEST);

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository, never()).save(any(BankAccount.class));
    }

    @Test
    void deleteAccount_ShouldDeleteAccount_WhenAccountExistsAndBelongsToUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.of(testAccount));
        doNothing().when(bankAccountRepository).delete(any(BankAccount.class));

        accountService.deleteAccount("01234567", "usr-123abc");

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository).delete(testAccount);
    }

    @Test
    void deleteAccount_ShouldThrowForbidden_WhenAccountBelongsToDifferentUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.existsById("01234567")).thenReturn(true);

        assertThatThrownBy(() -> accountService.deleteAccount("01234567", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
        verify(bankAccountRepository, never()).delete(any(BankAccount.class));
    }

    @Test
    void getAccountEntity_ShouldReturnAccount_WhenAccountExistsAndBelongsToUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.of(testAccount));

        BankAccount result = accountService.getAccountEntity("01234567", "usr-123abc");

        assertThat(result).isNotNull();
        assertThat(result.getAccountNumber()).isEqualTo("01234567");
        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
    }

    @Test
    void getAccountEntity_ShouldThrowForbidden_WhenAccountBelongsToDifferentUser() {
        when(bankAccountRepository.findByAccountNumberAndUserId("01234567", "usr-123abc"))
                .thenReturn(Optional.empty());
        when(bankAccountRepository.existsById("01234567")).thenReturn(true);

        assertThatThrownBy(() -> accountService.getAccountEntity("01234567", "usr-123abc"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting("statusCode")
                .isEqualTo(HttpStatus.FORBIDDEN);

        verify(bankAccountRepository).findByAccountNumberAndUserId("01234567", "usr-123abc");
    }
}

