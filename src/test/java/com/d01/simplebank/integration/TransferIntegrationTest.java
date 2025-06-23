package com.d01.simplebank.integration;

import com.d01.simplebank.dto.DepositRequest;
import com.d01.simplebank.dto.TransferRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class TransferIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mockMvc;
    private Account senderAccount;
    private Account receiverAccount;
    private User user;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        // Create user with encrypted PIN
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encryptedPin = passwordEncoder.encode("123456");
        
        user = new User("user@test.com", "password", "USER", "1234567890123", "ผู้ใช้", "Test User", encryptedPin);
        user = userRepository.save(user);

        // Create sender account (belongs to the user)
        senderAccount = new Account(user.getCid(), user.getNameTh(), user.getNameEn());
        senderAccount = accountRepository.save(senderAccount);

        // Create receiver account (different user)
        String receiverEncryptedPin = passwordEncoder.encode("654321");
        User receiverUser = new User("receiver@test.com", "password", "USER", "9876543210987", "ผู้รับ", "Receiver User", receiverEncryptedPin);
        receiverUser = userRepository.save(receiverUser);
        receiverAccount = new Account(receiverUser.getCid(), receiverUser.getNameTh(), receiverUser.getNameEn());
        receiverAccount = accountRepository.save(receiverAccount);
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_Success() throws Exception {
        // First, deposit some money into the sender account using admin
        // We need to switch to admin user for deposit
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String adminEncryptedPin = passwordEncoder.encode("111111");
        User adminUser = new User("admin@test.com", "password", "ADMIN", "1111111111111", "แอดมิน", "Admin User", adminEncryptedPin);
        adminUser = userRepository.save(adminUser);
        
        // Create deposit request to add balance to sender account
        DepositRequest depositRequest = new DepositRequest(
                "initial-deposit",
                String.format("%07d", senderAccount.getId()),
                10000 // 100.00
        );

        // Perform deposit as admin
        mockMvc.perform(post("/tx/deposit")
                .with(user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

        // Now perform the transfer as the regular user
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                5000, // 50.00
                "123456" // PIN
        );

        // Perform POST request
        mockMvc.perform(post("/tx/transfer")
                .with(user("user@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @WithMockUser(username = "admin@test.com", roles = "ADMIN")
    public void testProcessTransfer_NonUserRole_ShouldReturnForbidden() throws Exception {
        // Create transfer request
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                5000,
                "123456"
        );

        // Perform POST request - should fail for non-user role
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_InvalidSenderAccount_ShouldReturnNotFound() throws Exception {
        // Create transfer request with non-existent sender account
        TransferRequest request = new TransferRequest(
                "9999999",
                String.format("%07d", receiverAccount.getId()),
                5000,
                "123456"
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_UnauthorizedSenderAccount_ShouldReturnForbidden() throws Exception {
        // Create a different user and account
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String differentEncryptedPin = passwordEncoder.encode("111111");
        User differentUser = new User("different@test.com", "password", "USER", "1111111111111", "ต่าง", "Different User", differentEncryptedPin);
        differentUser = userRepository.save(differentUser);
        Account differentAccount = new Account(differentUser.getCid(), differentUser.getNameTh(), differentUser.getNameEn());
        differentAccount = accountRepository.save(differentAccount);

        // Create transfer request with account that doesn't belong to current user
        TransferRequest request = new TransferRequest(
                String.format("%07d", differentAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                5000,
                "123456"
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_InsufficientFunds_ShouldReturnBadRequest() throws Exception {
        // Create transfer request with amount larger than available balance
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                1000000, // 10000.00 (assuming account has no balance)
                "123456"
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_InvalidPin_ShouldReturnBadRequest() throws Exception {
        // Create transfer request with invalid PIN
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                5000,
                "654321" // Wrong PIN
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid PIN"));
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_MissingFields_ShouldReturnBadRequest() throws Exception {
        // Create transfer request without required fields
        String requestJson = "{\"senderAccountNo\":\"" + String.format("%07d", senderAccount.getId()) + "\"}";

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_ZeroAmount_ShouldReturnBadRequest() throws Exception {
        // Create transfer request with amount 0 (should be rejected as minimum is 1)
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                0, // Zero amount - should be rejected
                "123456"
        );

        // Perform POST request - should fail
        mockMvc.perform(post("/tx/transfer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "user@test.com", roles = "USER")
    public void testProcessTransfer_MinimumAmount_ShouldSucceed() throws Exception {
        // First, deposit some money into the sender account using admin
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String adminEncryptedPin = passwordEncoder.encode("111111");
        User adminUser = new User("admin@test.com", "password", "ADMIN", "1111111111111", "แอดมิน", "Admin User", adminEncryptedPin);
        adminUser = userRepository.save(adminUser);
        
        // Create deposit request to add balance to sender account
        DepositRequest depositRequest = new DepositRequest(
                "minimum-amount-test-deposit",
                String.format("%07d", senderAccount.getId()),
                1000 // 10.00
        );

        // Perform deposit as admin
        mockMvc.perform(post("/tx/deposit")
                .with(user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(depositRequest)))
                .andExpect(status().isCreated());

        // Now perform the transfer with minimum amount (1) as the regular user
        TransferRequest request = new TransferRequest(
                String.format("%07d", senderAccount.getId()),
                String.format("%07d", receiverAccount.getId()),
                1, // Minimum valid amount (1 stang)
                "123456" // PIN
        );

        // Perform POST request - should succeed
        mockMvc.perform(post("/tx/transfer")
                .with(user("user@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.id").exists());
    }
} 