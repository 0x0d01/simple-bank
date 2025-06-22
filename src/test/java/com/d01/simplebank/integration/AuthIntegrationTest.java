package com.d01.simplebank.integration;

import com.d01.simplebank.dto.LoginRequest;
import com.d01.simplebank.dto.LoginResponse;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.repository.UserRepository;
import com.d01.simplebank.security.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class AuthIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        objectMapper = new ObjectMapper();
        
        // Don't clear the database - let Hibernate create the schema automatically
        // The @Transactional annotation will handle test isolation
    }

    @Test
    void login_WithValidCredentials_ShouldReturnJwtToken() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole("USER");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Act & Assert
        String responseJson = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verify the response
        LoginResponse loginResponse = objectMapper.readValue(responseJson, LoginResponse.class);
        assertNotNull(loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getType());
        
        // Verify the token is valid and contains the correct email
        assertTrue(jwtTokenProvider.validateToken(loginResponse.getToken()));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(loginResponse.getToken()));
    }

    @Test
    void login_WithInvalidCredentials_ShouldReturn401() throws Exception {
        // Arrange
        String email = "test@example.com";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole("USER");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, "wrongpassword");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithNonExistentUser_ShouldReturn401() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_WithEmptyEmail_ShouldReturn400() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("", "password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithInvalidEmail_ShouldReturn400() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("invalid-email", "password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithEmptyPassword_ShouldReturn400() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithNullEmail_ShouldReturn400() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest(null, "password123");

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithNullPassword_ShouldReturn400() throws Exception {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", null);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithAdminUser_ShouldReturnJwtToken() throws Exception {
        // Arrange
        String email = "admin@example.com";
        String password = "admin123";
        String encodedPassword = passwordEncoder.encode(password);
        
        User user = new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);
        user.setRole("ADMIN");
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest(email, password);

        // Act & Assert
        String responseJson = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.type").value("Bearer"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Verify the response
        LoginResponse loginResponse = objectMapper.readValue(responseJson, LoginResponse.class);
        assertNotNull(loginResponse.getToken());
        assertEquals("Bearer", loginResponse.getType());
        
        // Verify the token is valid and contains the correct email
        assertTrue(jwtTokenProvider.validateToken(loginResponse.getToken()));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(loginResponse.getToken()));
    }

    @Test
    void login_WithMalformedJson_ShouldReturn400() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"email\": \"test@example.com\", \"password\":}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_WithMultipleUsers_ShouldReturnCorrectToken() throws Exception {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        
        User user1 = new User();
        user1.setEmail(email1);
        user1.setPassword(encodedPassword);
        user1.setRole("USER");
        userRepository.save(user1);

        User user2 = new User();
        user2.setEmail(email2);
        user2.setPassword(encodedPassword);
        user2.setRole("USER");
        userRepository.save(user2);

        // Test login for user1
        LoginRequest loginRequest1 = new LoginRequest(email1, password);
        String responseJson1 = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest1)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponse loginResponse1 = objectMapper.readValue(responseJson1, LoginResponse.class);
        assertEquals(email1, jwtTokenProvider.getEmailFromToken(loginResponse1.getToken()));

        // Test login for user2
        LoginRequest loginRequest2 = new LoginRequest(email2, password);
        String responseJson2 = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest2)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginResponse loginResponse2 = objectMapper.readValue(responseJson2, LoginResponse.class);
        assertEquals(email2, jwtTokenProvider.getEmailFromToken(loginResponse2.getToken()));

        // Verify tokens are different
        assertNotEquals(loginResponse1.getToken(), loginResponse2.getToken());
    }
} 