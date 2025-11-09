package com.eaglebank.api.dto.transaction;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateTransactionRequest(
        @NotNull @DecimalMin(value = "0.00") @DecimalMax(value = "10000.00") BigDecimal amount,
        @NotBlank String currency,
        @NotBlank String type,
        String reference
) {
}

