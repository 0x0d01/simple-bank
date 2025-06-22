package com.d01.simplebank.integration;

import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.repository.UserRepository;
import com.d01.simplebank.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AccountIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private MockMvc mockMvc;
    private Account testAccount;
    private User testUser;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create test user
        testUser = new User("test@example.com", "password", "USER", "1234567890123", "ทดสอบ", "Test User", "123456");
        testUser = userRepository.save(testUser);

        // Create test account (let JPA generate the ID)
        testAccount = new Account(testUser.getCid(), testUser.getNameTh(), testUser.getNameEn());
        testAccount = accountRepository.save(testAccount);

        // Create some test transactions for statement generation
        LocalDateTime now = LocalDateTime.now();
        
        Transaction transaction1 = new Transaction();
        transaction1.setId("TXN001");
        transaction1.setAccount(testAccount);
        transaction1.setAmount(10000); // 100.00
        transaction1.setDisplayAmount("100.00");
        transaction1.setType("DEPOSIT");
        transaction1.setChannel("ATM");
        transaction1.setTransactionDate(now.minusDays(2));
        transaction1.setCreatedDate(now.minusDays(2));
        transaction1.setCreatedBy(testUser);
        transaction1.setRemark("Test deposit");
        // Add hash and signature for the first transaction (no previous hash)
        transaction1.setHash("a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456");
        transaction1.setSignature("dGVzdC1zaWduYXR1cmUtZm9yLXRyYW5zYWN0aW9uLTE=");
        transactionRepository.save(transaction1);
        
        Transaction transaction2 = new Transaction();
        transaction2.setId("TXN002");
        transaction2.setAccount(testAccount);
        transaction2.setAmount(-5000); // -50.00
        transaction2.setDisplayAmount("-50.00");
        transaction2.setType("WITHDRAWAL");
        transaction2.setChannel("ATM");
        transaction2.setTransactionDate(now.minusDays(1));
        transaction2.setCreatedDate(now.minusDays(1));
        transaction2.setCreatedBy(testUser);
        transaction2.setRemark("Test withdrawal");
        // Add hash and signature for the second transaction (different from first)
        transaction2.setHash("b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef12345678");
        transaction2.setSignature("dGVzdC1zaWduYXR1cmUtZm9yLXRyYW5zYWN0aW9uLTI=");
        transactionRepository.save(transaction2);

        // Set up authentication context
        CustomUserDetails userDetails = new CustomUserDetails(testUser);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        auth.setDetails(userDetails);
        
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void testCreateAccount_AdminUser_Success() throws Exception {
        // Set up ADMIN authentication for this test
        User adminUser = new User("admin@example.com", "password", "ADMIN");
        adminUser.setId("admin-123");
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);
        UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, adminDetails.getAuthorities());
        adminAuth.setDetails(adminDetails);
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        // Given - Use a different CID to avoid conflict
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890124", // Different CID
                "บัญชีทดสอบ",
                "Test Account"
        );

        // When & Then
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cid").value("1234567890124"))
                .andExpect(jsonPath("$.nameTh").value("บัญชีทดสอบ"))
                .andExpect(jsonPath("$.nameEn").value("Test Account"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.updatedDate").exists());
    }

    @Test
    public void testCreateAccount_UserRole_Forbidden() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest(
                "1234567890123",
                "บัญชีทดสอบ",
                "Test Account"
        );

        // When & Then
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testCreateAccount_InvalidCid_BadRequest() throws Exception {
        // Set up ADMIN authentication for this test
        User adminUser = new User("admin@example.com", "password", "ADMIN");
        adminUser.setId("admin-123");
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);
        UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, adminDetails.getAuthorities());
        adminAuth.setDetails(adminDetails);
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        // Given
        CreateAccountRequest request = new CreateAccountRequest(
                "123", // Invalid CID (too short)
                "บัญชีทดสอบ",
                "Test Account"
        );

        // When & Then
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testCreateAccount_MissingFields_BadRequest() throws Exception {
        // Set up ADMIN authentication for this test
        User adminUser = new User("admin@example.com", "password", "ADMIN");
        adminUser.setId("admin-123");
        CustomUserDetails adminDetails = new CustomUserDetails(adminUser);
        UsernamePasswordAuthenticationToken adminAuth = new UsernamePasswordAuthenticationToken(
            adminDetails, null, adminDetails.getAuthorities());
        adminAuth.setDetails(adminDetails);
        SecurityContextHolder.getContext().setAuthentication(adminAuth);

        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccountById_Valid7DigitId_Success() throws Exception {
        // When & Then - Test with valid 7-digit zero-padded ID
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetAccountById_InvalidIdTooShort_BadRequest() throws Exception {
        // When & Then - Test with ID that's too short
        mockMvc.perform(get("/accounts/123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccountById_InvalidIdTooLong_BadRequest() throws Exception {
        // When & Then - Test with ID that's too long
        mockMvc.perform(get("/accounts/12345678"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccountById_InvalidIdNonNumeric_BadRequest() throws Exception {
        // When & Then - Test with ID containing non-numeric characters
        mockMvc.perform(get("/accounts/123456a"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAccountById_UserRole_Success() throws Exception {
        // When & Then - Test with USER role
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenerateStatement_Valid7DigitId_Success() throws Exception {
        // When & Then - Test with valid 7-digit zero-padded ID
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr + "/statement")
                .param("since", "1640995200")
                .param("until", "1643673600"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "text/csv"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"bank_statement_" + accountIdStr + "_1640995200_1643673600.csv\""));
    }

    @Test
    public void testGenerateStatement_InvalidIdTooShort_BadRequest() throws Exception {
        // When & Then - Test with ID that's too short
        mockMvc.perform(get("/accounts/123/statement")
                .param("since", "1640995200")
                .param("until", "1643673600"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateStatement_InvalidIdTooLong_BadRequest() throws Exception {
        // When & Then - Test with ID that's too long
        mockMvc.perform(get("/accounts/12345678/statement")
                .param("since", "1640995200")
                .param("until", "1643673600"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateStatement_InvalidIdNonNumeric_BadRequest() throws Exception {
        // When & Then - Test with ID containing non-numeric characters
        mockMvc.perform(get("/accounts/123456a/statement")
                .param("since", "1640995200")
                .param("until", "1643673600"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateStatement_UserRole_Success() throws Exception {
        // When & Then - Test with USER role
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr + "/statement")
                .param("since", "1640995200")
                .param("until", "1643673600"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGenerateStatement_MissingSinceParam_BadRequest() throws Exception {
        // When & Then - Test with missing since parameter
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr + "/statement")
                .param("until", "1643673600"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGenerateStatement_MissingUntilParam_BadRequest() throws Exception {
        // When & Then - Test with missing until parameter
        String accountIdStr = String.format("%07d", testAccount.getId());
        mockMvc.perform(get("/accounts/" + accountIdStr + "/statement")
                .param("since", "1640995200"))
                .andExpect(status().isBadRequest());
    }
} 