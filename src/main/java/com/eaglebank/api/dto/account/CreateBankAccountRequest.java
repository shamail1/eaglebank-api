package com.eaglebank.api.dto.account;

import jakarta.validation.constraints.NotBlank;

public record CreateBankAccountRequest(
        @NotBlank String name,
        @NotBlank String accountType
) {
}

