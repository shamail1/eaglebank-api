package com.eaglebank.api.dto.account;

import java.util.List;

public record ListBankAccountsResponse(
        List<BankAccountResponse> accounts
) {
}

