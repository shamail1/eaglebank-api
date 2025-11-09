package com.eaglebank.api.controller;

import com.eaglebank.api.dto.transaction.CreateTransactionRequest;
import com.eaglebank.api.dto.transaction.ListTransactionsResponse;
import com.eaglebank.api.dto.transaction.TransactionResponse;
import com.eaglebank.api.service.TransactionService;
import com.eaglebank.api.util.SecurityContextUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts/{accountNumber}/transactions")
@Validated
public class TransactionController {
    
    private final TransactionService transactionService;
    
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @Valid @RequestBody CreateTransactionRequest request) {
        String userId = SecurityContextUtil.getCurrentUserId();
        TransactionResponse response = transactionService.createTransaction(accountNumber, request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<ListTransactionsResponse> listTransactions(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber) {
        String userId = SecurityContextUtil.getCurrentUserId();
        ListTransactionsResponse response = transactionService.listTransactions(accountNumber, userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @PathVariable @Pattern(regexp = "^tan-[A-Za-z0-9]+$") String transactionId) {
        String userId = SecurityContextUtil.getCurrentUserId();
        TransactionResponse response = transactionService.getTransactionById(accountNumber, transactionId, userId);
        return ResponseEntity.ok(response);
    }
}

