package com.eaglebank.api.dto.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateUserRequest(
        @NotBlank String name,
        @Valid AddressDto address,
        @NotBlank @Pattern(regexp = "^\\+[1-9]\\d{1,14}$") String phoneNumber,
        @NotBlank @Email String email,
        @NotBlank String password
) {
}

