package com.d01.simplebank.integration;

import com.d01.simplebank.dto.CreateTransactionRequest;
import com.d01.simplebank.dto.TransactionResponse;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.repository.UserRepository;
import com.d01.simplebank.security.JwtTokenProvider;
import com.d01.simplebank.security.CustomUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TransactionIntegrationTest {
    
    @Autowired
    private WebApplicationContext webApplicationContext;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    
    private Account testAccount;
    private User testUser;
    private String adminToken;
    
    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Create test account
        testAccount = new Account("1234567890123", "ทดสอบ", "Test Account");
        testAccount = accountRepository.save(testAccount);
        
        // Create test user
        testUser = new User("admin@test.com", "password", "ADMIN");
        testUser.setCid("1234567890123");
        testUser = userRepository.save(testUser);
        
        // Generate admin token
        adminToken = jwtTokenProvider.generateToken(testUser.getEmail());

        // Set CustomUserDetails in SecurityContext for admin
        setCustomUserDetails(testUser);
    }
    
    private void setCustomUserDetails(User user) {
        CustomUserDetails userDetails = new CustomUserDetails(user);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
            userDetails, user.getPassword(), userDetails.getAuthorities()
        );
        auth.setDetails(userDetails);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
    
    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testCreateTransaction_WithHashAndSignature() throws Exception {
        // Create transaction request
        CreateTransactionRequest request = new CreateTransactionRequest(
                testAccount.getId(),
                LocalDateTime.now(),
                98700, // 987.00
                "A1",
                "ATS",
                "Test transaction",
                "{\"test\": \"metadata\"}"
        );
        
        // Perform POST request
        String response = mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.accountId").value(testAccount.getId()))
                .andExpect(jsonPath("$.accountNo").value(String.format("%07d", testAccount.getId())))
                .andExpect(jsonPath("$.amount").value(98700))
                .andExpect(jsonPath("$.displayAmount").value("987.00"))
                .andExpect(jsonPath("$.type").value("A1"))
                .andExpect(jsonPath("$.channel").value("ATS"))
                .andExpect(jsonPath("$.remark").value("Test transaction"))
                .andExpect(jsonPath("$.metadata").value("{\"test\": \"metadata\"}"))
                .andExpect(jsonPath("$.hash").exists())
                .andExpect(jsonPath("$.signature").exists())
                .andExpect(jsonPath("$.createdBy").value("admin@test.com"))
                .andExpect(jsonPath("$.createdDate").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        // Parse response and verify hash and signature
        TransactionResponse transactionResponse = objectMapper.readValue(response, TransactionResponse.class);
        
        // Verify hash is not null and has correct length (SHA-256 = 64 hex characters)
        assertNotNull(transactionResponse.getHash());
        assertEquals(64, transactionResponse.getHash().length());
        
        // Verify signature is not null and is Base64 encoded
        assertNotNull(transactionResponse.getSignature());
        assertTrue(transactionResponse.getSignature().length() > 0);
        
        // Verify the transaction was saved in the database
        assertTrue(transactionRepository.findById(transactionResponse.getId()).isPresent());
    }
    
    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testCreateTransaction_ChainOfHashes() throws Exception {
        // Create first transaction
        CreateTransactionRequest request1 = new CreateTransactionRequest(
                testAccount.getId(),
                LocalDateTime.now(),
                100000, // 1000.00
                "A1",
                "ATS",
                "First transaction",
                null
        );
        
        String response1 = mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        TransactionResponse transaction1 = objectMapper.readValue(response1, TransactionResponse.class);
        String hash1 = transaction1.getHash();
        
        // Create second transaction
        CreateTransactionRequest request2 = new CreateTransactionRequest(
                testAccount.getId(),
                LocalDateTime.now().plusMinutes(1),
                50000, // 500.00
                "W1",
                "ATM",
                "Second transaction",
                null
        );
        
        String response2 = mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        TransactionResponse transaction2 = objectMapper.readValue(response2, TransactionResponse.class);
        String hash2 = transaction2.getHash();
        
        // Verify hashes are different (chain of hashes)
        assertNotEquals(hash1, hash2);
        
        // Verify both hashes have correct length
        assertEquals(64, hash1.length());
        assertEquals(64, hash2.length());
        
        // Verify both signatures exist
        assertNotNull(transaction1.getSignature());
        assertNotNull(transaction2.getSignature());
    }
    
    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testCreateTransaction_InvalidAccount() throws Exception {
        CreateTransactionRequest request = new CreateTransactionRequest(
                99999L, // Non-existent account ID
                LocalDateTime.now(),
                100000,
                "A1",
                "ATS",
                "Test transaction",
                null
        );
        
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testCreateTransaction_AccessDenied() throws Exception {
        // Create user with different CID
        User userWithDifferentCid = new User("user@test.com", "password", "USER");
        userWithDifferentCid.setCid("9876543210987"); // Different CID
        userRepository.save(userWithDifferentCid);
        
        // Set CustomUserDetails for this user
        setCustomUserDetails(userWithDifferentCid);
        
        CreateTransactionRequest request = new CreateTransactionRequest(
                testAccount.getId(),
                LocalDateTime.now(),
                100000,
                "A1",
                "ATS",
                "Test transaction",
                null
        );
        
        mockMvc.perform(post("/transactions")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
} 