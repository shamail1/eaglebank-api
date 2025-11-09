package com.eaglebank.api.dto.account;

import java.math.BigDecimal;
import java.time.Instant;

public record BankAccountResponse(
        String accountNumber,
        String sortCode,
        String name,
        String accountType,
        BigDecimal balance,
        String currency,
        Instant createdTimestamp,
        Instant updatedTimestamp
) {
}

