package com.d01.simplebank.controller;

import com.d01.simplebank.dto.AccountResponse;
import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * Create a new account
     * Only ADMIN users can create accounts
     * @param request the account creation request
     * @return ResponseEntity with created account details
     */
    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        AccountResponse account = accountService.createAccount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }
    
    /**
     * Get account by ID
     * - ADMIN users can access any account
     * - USER users can only access accounts with matching CID
     * @param id the account ID
     * @return ResponseEntity with account details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable Long id) {
        AccountResponse account = accountService.getAccountById(id);
        return ResponseEntity.ok(account);
    }
} 