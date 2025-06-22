package com.d01.simplebank.service;

import com.d01.simplebank.dto.AccountResponse;
import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Customer;
import com.d01.simplebank.exception.AccessDeniedException;
import com.d01.simplebank.exception.AccountAlreadyExistsException;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.CustomerRepository;
import com.d01.simplebank.security.CustomUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private CustomerRepository customerRepository;
    
    /**
     * Create a new account - Only ADMIN users can create accounts
     * @param request the account creation request
     * @return AccountResponse with the created account details
     */
    @Transactional
    public AccountResponse createAccount(CreateAccountRequest request) {
        // Check if account already exists with the same CID
        if (accountRepository.existsByCid(request.getCid())) {
            throw new AccountAlreadyExistsException("Account with CID " + request.getCid() + " already exists");
        }
        
        // Create new account
        Account account = new Account(request.getCid(), request.getNameTh(), request.getNameEn());
        
        try {
            Account savedAccount = accountRepository.save(account);
            return new AccountResponse(savedAccount);
        } catch (DataIntegrityViolationException e) {
            // Handle concurrent creation attempts
            String errorMessage = e.getMessage();
            if (errorMessage.contains("Duplicate entry")) {
                throw new AccountAlreadyExistsException("Account with CID " + request.getCid() + " already exists", e);
            }
            throw e;
        }
    }
    
    /**
     * Get account by ID with access control
     * - ADMIN users can access any account
     * - USER users can only access accounts with matching CID in their customer record
     * @param id the account ID
     * @return AccountResponse with account details
     */
    public AccountResponse getAccountById(Long id) {
        // Get current user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails currentUserDetails = (CustomUserDetails)authentication.getDetails();
        
        // Find the account
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + id));
        
        // Check access permissions
        if ("USER".equals(currentUserDetails.getRole())) {
            // USER can only access accounts with matching CID
            Customer customer = customerRepository.findByUserId(currentUserDetails.getId())
                    .orElseThrow(() -> new AccessDeniedException("Customer record not found for current user"));
            
            if (customer.getCid().equals(account.getCid())) {
                return new AccountResponse(account);
            } else {
                throw new AccessDeniedException("Access denied: CID mismatch");
            }
        } else {
            throw new AccessDeniedException("Invalid user role");
        }
    }
} 