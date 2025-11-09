package com.eaglebank.api.dto.transaction;

import java.math.BigDecimal;
import java.time.Instant;

public record TransactionResponse(
        String id,
        BigDecimal amount,
        String currency,
        String type,
        String reference,
        String userId,
        Instant createdTimestamp
) {
}

