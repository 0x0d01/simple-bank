package com.d01.simplebank.integration;

import com.d01.simplebank.dto.CreateAccountRequest;
import com.d01.simplebank.entity.Account;
import com.d01.simplebank.repository.AccountRepository;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class AccountIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testCreateAccount_AdminUser_Success() throws Exception {
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
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cid").value("1234567890123"))
                .andExpect(jsonPath("$.nameTh").value("บัญชีทดสอบ"))
                .andExpect(jsonPath("$.nameEn").value("Test Account"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.createdDate").exists())
                .andExpect(jsonPath("$.updatedDate").exists());
    }

    @Test
    @WithMockUser(roles = "USER")
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
    @WithMockUser(roles = "ADMIN")
    public void testCreateAccount_InvalidCid_BadRequest() throws Exception {
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
    @WithMockUser(roles = "ADMIN")
    public void testCreateAccount_MissingFields_BadRequest() throws Exception {
        // Given
        CreateAccountRequest request = new CreateAccountRequest();
        // Missing required fields

        // When & Then
        mockMvc.perform(post("/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 