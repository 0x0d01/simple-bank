package com.d01.simplebank.controller;

import com.d01.simplebank.dto.DepositRequest;
import com.d01.simplebank.dto.TransferRequest;
import com.d01.simplebank.dto.SuccessResponse;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.AccessDeniedException;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.exception.InvalidPinException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.UserRepository;
import com.d01.simplebank.service.TransactionService;
import com.d01.simplebank.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tx")
public class TxController {
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Process a deposit transaction (ADMIN only)
     * @param request the deposit request
     * @return ResponseEntity with success status and transaction ID
     */
    @PostMapping("/deposit")
    public ResponseEntity<SuccessResponse> processDeposit(@Valid @RequestBody DepositRequest request) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Check if user is ADMIN
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
            throw new AccessDeniedException("Only ADMIN users can perform deposits");
        }
        
        // Find the user creating the transaction
        User createdBy = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Process the deposit
        Transaction transaction = transactionService.processDeposit(
                request.getId(),
                request.getAccountNo(),
                request.getAmount(),
                createdBy
        );
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse(true, transaction.getId()));
    }
    
    /**
     * Process a transfer transaction (USER only)
     * PIN verification is required
     * @param request the transfer request
     * @return ResponseEntity with success status and transaction ID of the sender account
     */
    @PostMapping("/transfer")
    public ResponseEntity<SuccessResponse> processTransfer(@Valid @RequestBody TransferRequest request) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Check if user is USER
        if (!authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_USER"))) {
            throw new AccessDeniedException("Only USER users can perform transfers");
        }
        
        // Find the sender account to verify ownership
        Long senderAccountId = Long.parseLong(request.getSenderAccountNo());
        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found with account number: " + request.getSenderAccountNo()));

        // Find the user creating the transaction
        User currentUser = userRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if the sender account belongs to the current user
        if (!currentUser.getCid().equals(senderAccount.getCid())) {
            throw new AccessDeniedException("Access denied: You can only transfer from your own accounts");
        }
        
        // Verify PIN
        boolean isPinValid = userService.verifyPin(currentUser.getCid(), request.getPin());
        if (!isPinValid) {
            throw new InvalidPinException("Invalid PIN provided");
        }
        
        // Process the transfer
        List<Transaction> transactions = transactionService.processTransfer(
                request.getSenderAccountNo(),
                request.getReceiverAccountNo(),
                request.getAmount(),
                currentUser
        );
        
        // Return the transaction ID of the sender's transaction (first transaction in the list)
        Transaction senderTransaction = transactions.stream()
                .filter(t -> t.getAccount().getId().toString().equals(request.getSenderAccountNo()))
                .findFirst()
                .orElse(transactions.get(0)); // fallback to first transaction if filter fails
        
        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessResponse(true, senderTransaction.getId()));
    }
} 