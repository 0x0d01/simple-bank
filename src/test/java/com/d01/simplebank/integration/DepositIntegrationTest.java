package com.d01.simplebank.integration;

import com.d01.simplebank.dto.DepositRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DepositIntegrationTest {

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
    private User adminUser;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create admin user
        adminUser = new User("admin@test.com", "password", "ADMIN", "1234567890123", "แอดมิน", "Admin User", "123456");
        adminUser = userRepository.save(adminUser);

        // Create test account
        testAccount = new Account(adminUser.getCid(), adminUser.getNameTh(), adminUser.getNameEn());
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testProcessDeposit_Success() throws Exception {
        // Create deposit request
        DepositRequest request = new DepositRequest(
                "deposit-123",
                String.format("%07d", testAccount.getId()),
                10000 // 100.00
        );

        // Perform POST request
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").value("deposit-123"));
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testProcessDeposit_DuplicateId_ShouldReturnError() throws Exception {
        // Create first deposit request
        DepositRequest request1 = new DepositRequest(
                "duplicate-id",
                String.format("%07d", testAccount.getId()),
                10000
        );

        // Perform first POST request
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request1)))
                .andExpect(status().isCreated());

        // Create second deposit request with same ID
        DepositRequest request2 = new DepositRequest(
                "duplicate-id",
                String.format("%07d", testAccount.getId()),
                20000
        );

        // Perform second POST request - should fail
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request2)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessDeposit_NonAdminUser_ShouldReturnForbidden() throws Exception {
        // Create deposit request
        DepositRequest request = new DepositRequest(
                "user-deposit",
                String.format("%07d", testAccount.getId()),
                10000
        );

        // Perform POST request - should fail for non-admin user
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testProcessDeposit_InvalidAccount_ShouldReturnNotFound() throws Exception {
        // Create deposit request with non-existent account
        DepositRequest request = new DepositRequest(
                "invalid-account",
                "9999999",
                10000
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testProcessDeposit_MissingId_ShouldReturnBadRequest() throws Exception {
        // Create deposit request without ID
        String requestJson = "{\"accountNo\":\"" + String.format("%07d", testAccount.getId()) + "\",\"amount\":10000}";

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/deposit")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }
} 