package com.d01.simplebank.controller;

import com.d01.simplebank.dto.AccountResponse;
import com.d01.simplebank.dto.BankStatementRequest;
import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/accounts")
public class AccountController {
    
    @Autowired
    private AccountService accountService;
    
    /**
     * Create a new account
     * Only ADMIN users can create accounts
     * @param request the account creation request (amount field is optional for initial deposit)
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
     * @param id the account ID (7-digit zero-padded string)
     * @return ResponseEntity with account details
     */
    @GetMapping("/{id}")
    public ResponseEntity<AccountResponse> getAccountById(@PathVariable String id) {
        // Validate 7-digit format
        if (id == null || id.length() != 7 || !id.matches("\\d{7}")) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Long accountId = Long.parseLong(id);
            AccountResponse account = accountService.getAccountById(accountId);
            return ResponseEntity.ok(account);
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        }
        // Let AccountNotFoundException and AccessDeniedException propagate to GlobalExceptionHandler
    }
    
    /**
     * Generate bank statement in CSV format
     * Only USER role and account owner can access this
     * PIN verification is required
     * @param id the account ID (7-digit zero-padded string)
     * @param request the bank statement request containing PIN, since, and until timestamps
     * @return ResponseEntity with CSV file attachment
     */
    @PostMapping("/{id}/statement")
    public ResponseEntity<byte[]> generateStatement(
            @PathVariable String id,
            @Valid @RequestBody BankStatementRequest request) {
        
        // Validate 7-digit format
        if (id == null || id.length() != 7 || !id.matches("\\d{7}")) {
            return ResponseEntity.badRequest().build();
        }
        
        try {
            Long accountId = Long.parseLong(id);
            InputStream csvStream = accountService.generateStatement(accountId, request.getSince(), request.getUntil(), request.getPin());
            byte[] csvBytes = csvStream.readAllBytes();
            
            // Create filename
            String filename = String.format("bank_statement_%s_%d_%d.csv", id, request.getSince(), request.getUntil());
            
            // Set response headers for file download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"");
            headers.setContentLength(csvBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvBytes);
                    
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().build();
        } catch (java.io.IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        // Let AccountNotFoundException, AccessDeniedException, InvalidPinException, and IllegalArgumentException propagate to GlobalExceptionHandler
    }
} 