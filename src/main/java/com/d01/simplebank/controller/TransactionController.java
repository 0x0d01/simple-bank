package com.d01.simplebank.controller;

import com.d01.simplebank.dto.CreateTransactionRequest;
import com.d01.simplebank.dto.TransactionResponse;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.AccessDeniedException;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.UserRepository;
import com.d01.simplebank.security.CustomUserDetails;
import com.d01.simplebank.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transactions")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Create a new transaction
     * - ADMIN users can create transactions for any account
     * - USER users can only create transactions for accounts with matching CID
     * @param request the transaction creation request
     * @return ResponseEntity with created transaction details
     */
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@Valid @RequestBody CreateTransactionRequest request) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails)authentication.getPrincipal();
        
        // Find the account
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + request.getAccountId()));
        
        // Check access permissions
        if ("USER".equals(currentUserDetails.getRole())) {
            // USER can only create transactions for accounts with matching CID
            if (!currentUserDetails.getCid().equals(account.getCid())) {
                throw new AccessDeniedException("Access denied: CID mismatch");
            }
        } else if (!"ADMIN".equals(currentUserDetails.getRole())) {
            throw new AccessDeniedException("Invalid user role");
        }
        
        // Find the user creating the transaction
        User createdBy = userRepository.findByEmail(currentUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Create the transaction
        Transaction transaction = transactionService.createTransaction(
                request.getAccountId(),
                request.getTransactionDate(),
                request.getAmount(),
                request.getType(),
                request.getChannel(),
                request.getRemark(),
                createdBy,
                request.getMetadata()
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new TransactionResponse(transaction));
    }
} 