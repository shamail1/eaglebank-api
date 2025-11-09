package com.eaglebank.api.util;

import com.eaglebank.api.domain.Address;
import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.Transaction;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.user.AddressDto;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.dto.user.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class EntityMapperTest {

    private Address testAddress;
    private AddressDto testAddressDto;
    private User testUser;
    private BankAccount testAccount;
    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testAddress = new Address("123 Main St", "Apt 4", "Building A", "London", "Greater London", "SW1A 1AA");
        testAddressDto = new AddressDto("123 Main St", "Apt 4", "Building A", "London", "Greater London", "SW1A 1AA");
        
        testUser = new User();
        testUser.setId("usr-123abc");
        testUser.setName("Test User");
        testUser.setAddress(testAddress);
        testUser.setPhoneNumber("+441234567890");
        testUser.setEmail("test@example.com");
        testUser.setPassword("password");
        testUser.setCreatedTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        testUser.setUpdatedTimestamp(Instant.parse("2024-01-02T00:00:00Z"));

        testAccount = new BankAccount();
        testAccount.setAccountNumber("01234567");
        testAccount.setSortCode("10-10-10");
        testAccount.setName("Test Account");
        testAccount.setAccountType("personal");
        testAccount.setBalance(new BigDecimal("1000.00"));
        testAccount.setCurrency("GBP");
        testAccount.setUser(testUser);
        testAccount.setCreatedTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
        testAccount.setUpdatedTimestamp(Instant.parse("2024-01-02T00:00:00Z"));

        testTransaction = new Transaction();
        testTransaction.setId("tan-123abc");
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setCurrency("GBP");
        testTransaction.setType("deposit");
        testTransaction.setReference("Test reference");
        testTransaction.setAccount(testAccount);
        testTransaction.setUser(testUser);
        testTransaction.setCreatedTimestamp(Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void toAddressDto_ShouldMapAddressToDto_WhenAddressIsNotNull() {
        AddressDto result = EntityMapper.toAddressDto(testAddress);

        assertThat(result).isNotNull();
        assertThat(result.line1()).isEqualTo("123 Main St");
        assertThat(result.line2()).isEqualTo("Apt 4");
        assertThat(result.line3()).isEqualTo("Building A");
        assertThat(result.town()).isEqualTo("London");
        assertThat(result.county()).isEqualTo("Greater London");
        assertThat(result.postcode()).isEqualTo("SW1A 1AA");
    }

    @Test
    void toAddressDto_ShouldReturnNull_WhenAddressIsNull() {
        AddressDto result = EntityMapper.toAddressDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toAddress_ShouldMapDtoToAddress_WhenDtoIsNotNull() {
        Address result = EntityMapper.toAddress(testAddressDto);

        assertThat(result).isNotNull();
        assertThat(result.getLine1()).isEqualTo("123 Main St");
        assertThat(result.getLine2()).isEqualTo("Apt 4");
        assertThat(result.getLine3()).isEqualTo("Building A");
        assertThat(result.getTown()).isEqualTo("London");
        assertThat(result.getCounty()).isEqualTo("Greater London");
        assertThat(result.getPostcode()).isEqualTo("SW1A 1AA");
    }

    @Test
    void toAddress_ShouldReturnNull_WhenDtoIsNull() {
        Address result = EntityMapper.toAddress(null);

        assertThat(result).isNull();
    }

    @Test
    void toAddress_ShouldHandleNullOptionalFields() {
        AddressDto dtoWithNulls = new AddressDto("123 Main St", null, null, "London", "Greater London", "SW1A 1AA");
        
        Address result = EntityMapper.toAddress(dtoWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.getLine1()).isEqualTo("123 Main St");
        assertThat(result.getLine2()).isNull();
        assertThat(result.getLine3()).isNull();
        assertThat(result.getTown()).isEqualTo("London");
    }

    @Test
    void toUserResponse_ShouldMapUserToResponse_WhenUserIsNotNull() {
        UserResponse result = EntityMapper.toUserResponse(testUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("usr-123abc");
        assertThat(result.name()).isEqualTo("Test User");
        assertThat(result.phoneNumber()).isEqualTo("+441234567890");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.createdTimestamp()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
        assertThat(result.updatedTimestamp()).isEqualTo(Instant.parse("2024-01-02T00:00:00Z"));
        assertThat(result.address()).isNotNull();
        assertThat(result.address().line1()).isEqualTo("123 Main St");
    }

    @Test
    void toUserResponse_ShouldReturnNull_WhenUserIsNull() {
        UserResponse result = EntityMapper.toUserResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void toUserResponse_ShouldHandleNullAddress() {
        testUser.setAddress(null);
        
        UserResponse result = EntityMapper.toUserResponse(testUser);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("usr-123abc");
        assertThat(result.address()).isNull();
    }

    @Test
    void toBankAccountResponse_ShouldMapAccountToResponse_WhenAccountIsNotNull() {
        BankAccountResponse result = EntityMapper.toBankAccountResponse(testAccount);

        assertThat(result).isNotNull();
        assertThat(result.accountNumber()).isEqualTo("01234567");
        assertThat(result.sortCode()).isEqualTo("10-10-10");
        assertThat(result.name()).isEqualTo("Test Account");
        assertThat(result.accountType()).isEqualTo("personal");
        assertThat(result.balance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(result.currency()).isEqualTo("GBP");
        assertThat(result.createdTimestamp()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
        assertThat(result.updatedTimestamp()).isEqualTo(Instant.parse("2024-01-02T00:00:00Z"));
    }

    @Test
    void toBankAccountResponse_ShouldReturnNull_WhenAccountIsNull() {
        BankAccountResponse result = EntityMapper.toBankAccountResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void toTransactionResponse_ShouldMapTransactionToResponse_WhenTransactionIsNotNull() {
        TransactionResponse result = EntityMapper.toTransactionResponse(testTransaction);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("tan-123abc");
        assertThat(result.amount()).isEqualByComparingTo(new BigDecimal("100.00"));
        assertThat(result.currency()).isEqualTo("GBP");
        assertThat(result.type()).isEqualTo("deposit");
        assertThat(result.reference()).isEqualTo("Test reference");
        assertThat(result.userId()).isEqualTo("usr-123abc");
        assertThat(result.createdTimestamp()).isEqualTo(Instant.parse("2024-01-01T00:00:00Z"));
    }

    @Test
    void toTransactionResponse_ShouldReturnNull_WhenTransactionIsNull() {
        TransactionResponse result = EntityMapper.toTransactionResponse(null);

        assertThat(result).isNull();
    }

    @Test
    void toTransactionResponse_ShouldHandleNullReference() {
        testTransaction.setReference(null);
        
        TransactionResponse result = EntityMapper.toTransactionResponse(testTransaction);

        assertThat(result).isNotNull();
        assertThat(result.reference()).isNull();
    }
}

