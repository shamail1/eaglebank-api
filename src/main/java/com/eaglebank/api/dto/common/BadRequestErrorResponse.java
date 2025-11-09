package com.eaglebank.api.dto.common;

import java.util.List;

public record BadRequestErrorResponse(
        String message,
        List<ValidationErrorDetail> details
) {
    public record ValidationErrorDetail(
            String field,
            String message,
            String type
    ) {
    }
}

