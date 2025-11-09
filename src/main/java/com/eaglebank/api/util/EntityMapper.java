package com.eaglebank.api.util;

import com.eaglebank.api.domain.Address;
import com.eaglebank.api.domain.BankAccount;
import com.eaglebank.api.domain.Transaction;
import com.eaglebank.api.domain.User;
import com.eaglebank.api.dto.user.AddressDto;
import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.dto.user.UserResponse;

public class EntityMapper {
    
    public static AddressDto toAddressDto(Address address) {
        if (address == null) {
            return null;
        }
        return new AddressDto(
                address.getLine1(),
                address.getLine2(),
                address.getLine3(),
                address.getTown(),
                address.getCounty(),
                address.getPostcode()
        );
    }
    
    public static Address toAddress(AddressDto dto) {
        if (dto == null) {
            return null;
        }
        return new Address(
                dto.line1(),
                dto.line2(),
                dto.line3(),
                dto.town(),
                dto.county(),
                dto.postcode()
        );
    }
    
    public static UserResponse toUserResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponse(
                user.getId(),
                user.getName(),
                toAddressDto(user.getAddress()),
                user.getPhoneNumber(),
                user.getEmail(),
                user.getCreatedTimestamp(),
                user.getUpdatedTimestamp()
        );
    }
    
    public static BankAccountResponse toBankAccountResponse(BankAccount account) {
        if (account == null) {
            return null;
        }
        return new BankAccountResponse(
                account.getAccountNumber(),
                account.getSortCode(),
                account.getName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCurrency(),
                account.getCreatedTimestamp(),
                account.getUpdatedTimestamp()
        );
    }
    
    public static TransactionResponse toTransactionResponse(Transaction transaction) {
        if (transaction == null) {
            return null;
        }
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAmount(),
                transaction.getCurrency(),
                transaction.getType(),
                transaction.getReference(),
                transaction.getUser().getId(),
                transaction.getCreatedTimestamp()
        );
    }
}

