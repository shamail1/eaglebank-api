package com.eaglebank.api.dto.account;

public record UpdateBankAccountRequest(
        String name,
        String accountType
) {
}

