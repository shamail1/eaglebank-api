package com.eaglebank.api.util;

import com.eaglebank.api.constants.ApplicationConstants;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class IdGeneratorTest {

    @Test
    void generateUserId_ShouldStartWithCorrectPrefix() {
        String userId = IdGenerator.generateUserId();
        
        assertThat(userId).startsWith(ApplicationConstants.USER_ID_PREFIX);
    }

    @Test
    void generateUserId_ShouldHaveCorrectFormat() {
        String userId = IdGenerator.generateUserId();
        
        assertThat(userId).matches("^" + ApplicationConstants.USER_ID_PREFIX + "[A-Za-z0-9]+$");
        assertThat(userId.length()).isGreaterThan(ApplicationConstants.USER_ID_PREFIX.length());
    }

    @RepeatedTest(10)
    void generateUserId_ShouldGenerateUniqueIds() {
        Set<String> userIds = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            String userId = IdGenerator.generateUserId();
            assertThat(userIds).doesNotContain(userId);
            userIds.add(userId);
        }
    }

    @Test
    void generateTransactionId_ShouldStartWithCorrectPrefix() {
        String transactionId = IdGenerator.generateTransactionId();
        
        assertThat(transactionId).startsWith(ApplicationConstants.TRANSACTION_ID_PREFIX);
    }

    @Test
    void generateTransactionId_ShouldHaveCorrectFormat() {
        String transactionId = IdGenerator.generateTransactionId();
        
        assertThat(transactionId).matches("^" + ApplicationConstants.TRANSACTION_ID_PREFIX + "[A-Za-z0-9]+$");
        assertThat(transactionId.length()).isGreaterThan(ApplicationConstants.TRANSACTION_ID_PREFIX.length());
    }

    @RepeatedTest(10)
    void generateTransactionId_ShouldGenerateUniqueIds() {
        Set<String> transactionIds = new HashSet<>();
        
        for (int i = 0; i < 100; i++) {
            String transactionId = IdGenerator.generateTransactionId();
            assertThat(transactionIds).doesNotContain(transactionId);
            transactionIds.add(transactionId);
        }
    }

    @Test
    void generateAccountNumber_ShouldStartWithCorrectPrefix() {
        String accountNumber = IdGenerator.generateAccountNumber();
        
        assertThat(accountNumber).startsWith(ApplicationConstants.ACCOUNT_NUMBER_PREFIX);
    }

    @Test
    void generateAccountNumber_ShouldHaveCorrectFormat() {
        String accountNumber = IdGenerator.generateAccountNumber();
        
        assertThat(accountNumber).matches("^" + ApplicationConstants.ACCOUNT_NUMBER_PREFIX + "\\d{6}$");
        assertThat(accountNumber.length()).isEqualTo(8);
    }

    @Test
    void generateAccountNumber_ShouldHaveCorrectLength() {
        String accountNumber = IdGenerator.generateAccountNumber();
        
        assertThat(accountNumber).hasSize(8);
    }

    @RepeatedTest(10)
    void generateAccountNumber_ShouldGenerateValidAccountNumbers() {
        for (int i = 0; i < 100; i++) {
            String accountNumber = IdGenerator.generateAccountNumber();
            assertThat(accountNumber).matches("^01\\d{6}$");
        }
    }
}

