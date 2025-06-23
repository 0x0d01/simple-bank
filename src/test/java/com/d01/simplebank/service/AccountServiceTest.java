package com.d01.simplebank.service;

import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.AccessDeniedException;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;
    private User testUser;
    private Transaction testTransaction1;
    private Transaction testTransaction2;
    private CustomUserDetails userDetails;

    @BeforeEach
    public void setUp() {
        // Create test account
        testAccount = new Account("1234567890123", "ทดสอบ", "Test Account");

        // Create test user
        testUser = new User("test@example.com", "password", "USER", "1234567890123", "ทดสอบ", "Test User", "123456");

        // Create test transactions
        testTransaction1 = new Transaction(testAccount, LocalDateTime.of(2024, 1, 15, 10, 30), 10000, "DEPOSIT", "MOBILE_APP", testUser);
        testTransaction1.setId("transaction-1");
        testTransaction1.setRemark("Initial deposit");

        testTransaction2 = new Transaction(testAccount, LocalDateTime.of(2024, 1, 20, 14, 45), -5000, "WITHDRAWAL", "ATM", testUser);
        testTransaction2.setId("transaction-2");
        testTransaction2.setRemark("ATM withdrawal");

        // Create user details
        userDetails = new CustomUserDetails(testUser);
    }

    @Test
    public void testGenerateStatement_Success() throws Exception {
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock repository calls - use a test ID since we can't predict the generated ID
        Long testAccountId = 1L;
        when(accountRepository.findById(testAccountId)).thenReturn(Optional.of(testAccount));
        
        // Mock PIN verification
        when(userService.verifyPin("1234567890123", "123456")).thenReturn(true);
        
        // Convert test dates to Unix timestamps (January 2024)
        long since = LocalDateTime.of(2024, 1, 1, 0, 0).toEpochSecond(java.time.ZoneOffset.UTC);
        long until = LocalDateTime.of(2024, 1, 31, 23, 59, 59).toEpochSecond(java.time.ZoneOffset.UTC);
        
        when(transactionRepository.findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(testAccountId, 
                LocalDateTime.of(2024, 1, 1, 0, 0), LocalDateTime.of(2024, 1, 31, 23, 59, 59)))
                .thenReturn(Arrays.asList(testTransaction1, testTransaction2));

        // Execute
        InputStream result = accountService.generateStatement(testAccountId, since, until, "123456");

        // Verify
        assertNotNull(result);
        String csvContent = new String(result.readAllBytes());
        assertTrue(csvContent.contains("Date,Time,Code,Channel,Debit/Credit,Balance,Remark"));
        assertTrue(csvContent.contains("15/01/24,10:30,DEPOSIT,MOBILE_APP,+100.00,100.00"));
        assertTrue(csvContent.contains("20/01/24,14:45,WITHDRAWAL,ATM,-50.00,50.00"));
    }

    @Test
    public void testGenerateStatement_AdminAccessDenied() {
        // Create admin user and user details
        User adminUser = new User("admin@example.com", "password", "ADMIN");
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);
        
        // Mock security context with ADMIN role
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(adminDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification (should pass first, then fail on role check)
        when(userService.verifyPin(adminDetails.getCid(), "123456")).thenReturn(true);

        // Execute and verify
        assertThrows(AccessDeniedException.class, () -> {
            accountService.generateStatement(1L, 1640995200L, 1643673600L, "123456");
        });
    }

    @Test
    public void testGenerateStatement_AccountNotFound() {
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification (should pass first)
        when(userService.verifyPin("1234567890123", "123456")).thenReturn(true);

        // Mock repository call to return empty
        when(accountRepository.findById(999L)).thenReturn(Optional.empty());

        // Execute and verify
        assertThrows(AccountNotFoundException.class, () -> {
            accountService.generateStatement(999L, 1640995200L, 1643673600L, "123456");
        });
    }

    @Test
    public void testGenerateStatement_InvalidTimestamps() {
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification (should pass first)
        when(userService.verifyPin("1234567890123", "123456")).thenReturn(true);

        // Execute and verify - since >= until
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.generateStatement(1L, 1643673600L, 1640995200L, "123456");
        });
    }

    @Test
    public void testGenerateStatement_NegativeTimestamps() {
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification (should pass first)
        when(userService.verifyPin("1234567890123", "123456")).thenReturn(true);

        // Execute and verify - negative timestamps
        assertThrows(IllegalArgumentException.class, () -> {
            accountService.generateStatement(1L, -1L, 1643673600L, "123456");
        });
    }

    @Test
    public void testGenerateStatement_NotAccountOwner() {
        // Create user with different CID
        User differentUser = new User("different@example.com", "password", "USER", "9876543210987", "ต่าง", "Different User", "654321");
        CustomUserDetails differentDetails = new CustomUserDetails(differentUser);
        
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(differentDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification (should pass first)
        when(userService.verifyPin("9876543210987", "123456")).thenReturn(true);

        // Mock repository call
        when(accountRepository.findById(1L)).thenReturn(Optional.of(testAccount));

        // Execute and verify
        assertThrows(AccessDeniedException.class, () -> {
            accountService.generateStatement(1L, 1640995200L, 1643673600L, "123456");
        });
    }

    @Test
    public void testGenerateStatement_InvalidPin() {
        // Mock security context
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        SecurityContextHolder.setContext(securityContext);

        // Mock PIN verification to fail (should fail first, before any other checks)
        when(userService.verifyPin("1234567890123", "654321")).thenReturn(false);

        // Execute and verify - should fail on PIN verification before any other validations
        assertThrows(com.d01.simplebank.exception.InvalidPinException.class, () -> {
            accountService.generateStatement(1L, 1640995200L, 1643673600L, "654321");
        });
    }
} 