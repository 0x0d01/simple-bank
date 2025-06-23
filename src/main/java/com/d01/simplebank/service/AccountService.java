package com.d01.simplebank.service;

import com.d01.simplebank.dto.AccountResponse;
import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.exception.AccessDeniedException;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.exception.InvalidPinException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create a new account - Only ADMIN users can create accounts
     * @param request the account creation request
     * @return AccountResponse with the created account details
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        // Create new account
        Account account = new Account(request.getCid(), request.getNameTh(), request.getNameEn());
        
        Account savedAccount = accountRepository.save(account);
        return new AccountResponse(savedAccount);
    }
    
    /**
     * Get account by ID with access control
     * - ADMIN users can access any account
     * - USER users can only access accounts with matching CID in their user record
     * @param id the account ID
     * @return AccountResponse with account details
     */
    public AccountResponse getAccountById(Long id) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails)authentication.getPrincipal();
        
        // Find the account
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        
        // Check access permissions
        if ("USER".equals(currentUserDetails.getRole())) {
            // USER can only access accounts with matching CID
            if (currentUserDetails.getCid().equals(account.getCid())) {
                return new AccountResponse(account);
            } else {
                throw new AccessDeniedException("Access denied: CID mismatch");
            }
        } else if ("ADMIN".equals(currentUserDetails.getRole())) {
            // ADMIN can access any account
            return new AccountResponse(account);
        } else {
            throw new AccessDeniedException("Invalid user role");
        }
    }
    
    /**
     * Generate bank statement in CSV format
     * Only USER role and account owner can access this
     * PIN verification is required
     * @param accountId the account ID
     * @param since the start timestamp (Unix timestamp in seconds)
     * @param until the end timestamp (Unix timestamp in seconds)
     * @param pin the user's PIN for verification
     * @return InputStream containing CSV data
     */
    public InputStream generateStatement(Long accountId, long since, long until, String pin) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails)authentication.getPrincipal();
        
        // Verify PIN first
        boolean isPinValid = userService.verifyPin(currentUserDetails.getCid(), pin);
        if (!isPinValid) {
            throw new InvalidPinException("Invalid PIN provided");
        }
        
        // Check if user has USER role
        if (!"USER".equals(currentUserDetails.getRole())) {
            throw new AccessDeniedException("Only USER role can generate statements");
        }
        
        // Validate timestamps
        if (since < 0 || until < 0) {
            throw new IllegalArgumentException("Timestamps must be positive");
        }
        if (since >= until) {
            throw new IllegalArgumentException("Since timestamp must be less than until timestamp");
        }
        
        // Find the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        
        // Check if user is the account owner
        if (!currentUserDetails.getCid().equals(account.getCid())) {
            throw new AccessDeniedException("Access denied: Only account owner can generate statements");
        }
        
        // Convert Unix timestamps to LocalDateTime
        LocalDateTime startDate = LocalDateTime.ofEpochSecond(since, 0, java.time.ZoneOffset.UTC);
        LocalDateTime endDate = LocalDateTime.ofEpochSecond(until, 0, java.time.ZoneOffset.UTC);
        
        // Get transactions for the specified date range
        List<Transaction> transactions = transactionRepository
                .findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(accountId, startDate, endDate);
        
        // Generate CSV content
        StringBuilder csvContent = new StringBuilder();
        
        // Add header
        csvContent.append("Date,Time,Code,Channel,Debit/Credit,Balance,Remark\n");
        
        // Calculate running balance
        int runningBalance = 0;
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Transaction transaction : transactions) {
            // Update running balance
            runningBalance += transaction.getAmount();
            
            // Format date and time
            String date = transaction.getTransactionDate().format(dateFormatter);
            String time = transaction.getTransactionDate().format(timeFormatter);
            
            // Format debit/credit (prepend + or -)
            String debitCredit = (transaction.getAmount() > 0 ? "+" : "") + transaction.getDisplayAmount();
            
            // Format balance (convert integer to display format)
            String balance = formatDisplayAmount(runningBalance);
            
            // Escape remark for CSV (handle commas and quotes)
            String remark = escapeCsvField(transaction.getRemark() != null ? transaction.getRemark() : "");
            
            // Add row
            csvContent.append(String.format("%s,%s,%s,%s,%s,%s,%s\n",
                    date, time, transaction.getType(), transaction.getChannel(),
                    debitCredit, balance, remark));
        }
        
        return new ByteArrayInputStream(csvContent.toString().getBytes());
    }
    
    /**
     * Format integer amount to display format with 2 decimal places
     * @param amount the integer amount
     * @return formatted string with 2 decimal places
     */
    private String formatDisplayAmount(Integer amount) {
        if (amount == null) return "0.00";
        boolean negative = amount < 0;
        int abs = Math.abs(amount);
        int major = abs / 100;
        int minor = abs % 100;
        return String.format("%s%d.%02d", negative ? "-" : "", major, minor);
    }
    
    /**
     * Escape CSV field to handle commas and quotes
     * @param field the field to escape
     * @return escaped field
     */
    private String escapeCsvField(String field) {
        if (field == null) return "";
        if (field.contains(",") || field.contains("\"") || field.contains("\n")) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }
        return field;
    }
} 