package com.eaglebank.api.dto.auth;

public record LoginResponse(
        String token,
        String userId
) {
}

