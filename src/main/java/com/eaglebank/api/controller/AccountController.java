package com.eaglebank.api.controller;

import com.eaglebank.api.dto.account.BankAccountResponse;
import com.eaglebank.api.dto.account.CreateBankAccountRequest;
import com.eaglebank.api.dto.account.ListBankAccountsResponse;
import com.eaglebank.api.dto.account.UpdateBankAccountRequest;
import com.eaglebank.api.service.AccountService;
import com.eaglebank.api.util.SecurityContextUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/accounts")
@Validated
public class AccountController {
    
    private final AccountService accountService;
    
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    
    @PostMapping
    public ResponseEntity<BankAccountResponse> createAccount(@Valid @RequestBody CreateBankAccountRequest request) {
        String userId = SecurityContextUtil.getCurrentUserId();
        BankAccountResponse response = accountService.createAccount(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping
    public ResponseEntity<ListBankAccountsResponse> listAccounts() {
        String userId = SecurityContextUtil.getCurrentUserId();
        ListBankAccountsResponse response = accountService.listAccounts(userId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> getAccountByAccountNumber(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber) {
        String userId = SecurityContextUtil.getCurrentUserId();
        BankAccountResponse response = accountService.getAccountByAccountNumber(accountNumber, userId);
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{accountNumber}")
    public ResponseEntity<BankAccountResponse> updateAccount(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber,
            @Valid @RequestBody UpdateBankAccountRequest request) {
        String userId = SecurityContextUtil.getCurrentUserId();
        BankAccountResponse response = accountService.updateAccount(accountNumber, request, userId);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<Void> deleteAccount(
            @PathVariable @Pattern(regexp = "^01\\d{6}$") String accountNumber) {
        String userId = SecurityContextUtil.getCurrentUserId();
        accountService.deleteAccount(accountNumber, userId);
        return ResponseEntity.noContent().build();
    }
}

