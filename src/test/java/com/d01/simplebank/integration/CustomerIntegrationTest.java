package com.d01.simplebank.integration;

import com.d01.simplebank.dto.CreateUserRequest;
import com.d01.simplebank.dto.UserResponse;
import com.d01.simplebank.exception.UserAlreadyExistsException;
import com.d01.simplebank.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CustomerIntegrationTest {
    
    @Autowired
    private UserService userService;
    
    @Test
    public void testCreateUserWithCustomerRole_ShouldCreateUserWithCustomerData() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setCid("1234567890123");
        request.setNameTh("ทดสอบ ระบบ");
        request.setNameEn("Test System");
        request.setPin("123456");
        
        // When
        UserResponse userResponse = userService.createUser(request);
        
        // Then
        assertNotNull(userResponse);
        assertEquals("test@example.com", userResponse.getEmail());
        assertEquals("USER", userResponse.getRole());
        assertEquals("1234567890123", userResponse.getCid());
        assertEquals("ทดสอบ ระบบ", userResponse.getNameTh());
        assertEquals("Test System", userResponse.getNameEn());
    }
    
    @Test
    public void testCreateUserWithAdminRole_ShouldNotRequireCustomerData() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("admin@example.com");
        request.setPassword("password123");
        request.setRole("ADMIN");
        // No customer data provided
        
        // When
        UserResponse userResponse = userService.createUser(request);
        
        // Then
        assertNotNull(userResponse);
        assertEquals("admin@example.com", userResponse.getEmail());
        assertEquals("ADMIN", userResponse.getRole());
        assertNull(userResponse.getCid());
        assertNull(userResponse.getNameTh());
        assertNull(userResponse.getNameEn());
    }
    
    @Test
    public void testCreateAdminUserWithoutCustomerData_ShouldSucceed() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("admin2@example.com");
        request.setPassword("password123");
        request.setRole("ADMIN");
        // No customer data provided
        
        // When
        UserResponse userResponse = userService.createUser(request);
        
        // Then
        assertNotNull(userResponse);
        assertEquals("admin2@example.com", userResponse.getEmail());
        assertEquals("ADMIN", userResponse.getRole());
        assertNotNull(userResponse.getId());
        assertNotNull(userResponse.getCreatedDate());
        assertNotNull(userResponse.getUpdatedDate());
    }
    
    @Test
    public void testCreateUserWithInvalidCID_ShouldThrowException() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setCid("123456"); // Invalid CID (less than 13 digits)
        request.setNameTh("ทดสอบ ระบบ");
        request.setNameEn("Test System");
        request.setPin("123456");
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }
    
    @Test
    public void testCreateUserWithInvalidPIN_ShouldThrowException() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setCid("1234567890123");
        request.setNameTh("ทดสอบ ระบบ");
        request.setNameEn("Test System");
        request.setPin("123"); // Invalid PIN (less than 6 digits)
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }
    
    @Test
    public void testVerifyPin_ShouldReturnTrueForValidPin() {
        // Given
        String cid = "1234567890123";
        String pin = "123456";
        
        // Create a user first
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole("USER");
        userRequest.setCid(cid);
        userRequest.setNameTh("ทดสอบ ระบบ");
        userRequest.setNameEn("Test System");
        userRequest.setPin(pin);
        
        userService.createUser(userRequest);
        
        // When
        boolean isValid = userService.verifyPin(cid, pin);
        
        // Then
        assertTrue(isValid);
    }
    
    @Test
    public void testVerifyPin_ShouldReturnFalseForInvalidPin() {
        // Given
        String cid = "1234567890123";
        String pin = "123456";
        
        // Create a user first
        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setEmail("test@example.com");
        userRequest.setPassword("password123");
        userRequest.setRole("USER");
        userRequest.setCid(cid);
        userRequest.setNameTh("ทดสอบ ระบบ");
        userRequest.setNameEn("Test System");
        userRequest.setPin(pin);
        
        userService.createUser(userRequest);
        
        // When
        boolean isValid = userService.verifyPin(cid, "654321");
        
        // Then
        assertFalse(isValid);
    }
    
    @Test
    public void testCreateUserWithUserRoleWithoutCustomerData_ShouldThrowException() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        // No customer data provided - this should cause an exception
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }
    
    @Test
    public void testCreateUserWithUserRoleWithPartialCustomerData_ShouldThrowException() {
        // Given
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("user@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setCid("1234567890123");
        request.setNameTh("ทดสอบ ระบบ");
        // Missing nameEn and pin - this should cause an exception
        
        // When & Then
        assertThrows(IllegalArgumentException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    public void testCreateUserWithDuplicateCID_ShouldThrowException() {
        // Given
        String cid = "1234567890123";
        String pin = "123456";
        
        // Create first user with customer data
        CreateUserRequest request1 = new CreateUserRequest();
        request1.setEmail("user1@example.com");
        request1.setPassword("password123");
        request1.setRole("USER");
        request1.setCid(cid);
        request1.setNameTh("ทดสอบ ระบบ");
        request1.setNameEn("Test System");
        request1.setPin(pin);
        
        userService.createUser(request1);
        
        // When & Then - Try to create another user with the same CID
        CreateUserRequest request2 = new CreateUserRequest();
        request2.setEmail("user2@example.com");
        request2.setPassword("password123");
        request2.setRole("USER");
        request2.setCid(cid);
        request2.setNameTh("ทดสอบ อื่น");
        request2.setNameEn("Test Other");
        request2.setPin("654321");
        
        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(request2);
        });
    }
    
    @Test
    public void testGetUserByCid_ShouldReturnUser() {
        // Given
        String cid = "1234567890123";
        CreateUserRequest request = new CreateUserRequest();
        request.setEmail("test@example.com");
        request.setPassword("password123");
        request.setRole("USER");
        request.setCid(cid);
        request.setNameTh("ทดสอบ ระบบ");
        request.setNameEn("Test System");
        request.setPin("123456");
        
        userService.createUser(request);
        
        // When
        UserResponse userResponse = userService.getUserByCid(cid);
        
        // Then
        assertNotNull(userResponse);
        assertEquals(cid, userResponse.getCid());
        assertEquals("test@example.com", userResponse.getEmail());
    }
} 