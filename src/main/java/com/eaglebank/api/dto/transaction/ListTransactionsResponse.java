package com.eaglebank.api.dto.transaction;

import java.util.List;

public record ListTransactionsResponse(
        List<TransactionResponse> transactions
) {
}

