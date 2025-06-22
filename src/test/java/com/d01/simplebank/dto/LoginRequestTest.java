package com.d01.simplebank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginRequestTest {

    @Test
    void testDefaultConstructor() {
        // Act
        LoginRequest loginRequest = new LoginRequest();

        // Assert
        assertNotNull(loginRequest);
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        String email = "test@example.com";
        String password = "password123";

        // Act
        LoginRequest loginRequest = new LoginRequest(email, password);

        // Assert
        assertEquals(email, loginRequest.getEmail());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String email = "test@example.com";
        String password = "password123";

        // Act
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        // Assert
        assertEquals(email, loginRequest.getEmail());
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void testSetEmail() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String email = "new@example.com";

        // Act
        loginRequest.setEmail(email);

        // Assert
        assertEquals(email, loginRequest.getEmail());
    }

    @Test
    void testSetPassword() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();
        String password = "newpassword123";

        // Act
        loginRequest.setPassword(password);

        // Assert
        assertEquals(password, loginRequest.getPassword());
    }

    @Test
    void testSetEmailToNull() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // Act
        loginRequest.setEmail(null);

        // Assert
        assertNull(loginRequest.getEmail());
    }

    @Test
    void testSetPasswordToNull() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // Act
        loginRequest.setPassword(null);

        // Assert
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testSetEmailToEmptyString() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // Act
        loginRequest.setEmail("");

        // Assert
        assertEquals("", loginRequest.getEmail());
    }

    @Test
    void testSetPasswordToEmptyString() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        // Act
        loginRequest.setPassword("");

        // Assert
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testConstructorWithNullValues() {
        // Act
        LoginRequest loginRequest = new LoginRequest(null, null);

        // Assert
        assertNull(loginRequest.getEmail());
        assertNull(loginRequest.getPassword());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Act
        LoginRequest loginRequest = new LoginRequest("", "");

        // Assert
        assertEquals("", loginRequest.getEmail());
        assertEquals("", loginRequest.getPassword());
    }

    @Test
    void testMultipleSetOperations() {
        // Arrange
        LoginRequest loginRequest = new LoginRequest();

        // Act & Assert
        loginRequest.setEmail("first@example.com");
        assertEquals("first@example.com", loginRequest.getEmail());

        loginRequest.setEmail("second@example.com");
        assertEquals("second@example.com", loginRequest.getEmail());

        loginRequest.setPassword("firstpassword");
        assertEquals("firstpassword", loginRequest.getPassword());

        loginRequest.setPassword("secondpassword");
        assertEquals("secondpassword", loginRequest.getPassword());
    }
} 