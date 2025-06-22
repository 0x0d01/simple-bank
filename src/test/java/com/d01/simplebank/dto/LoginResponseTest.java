package com.d01.simplebank.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LoginResponseTest {

    @Test
    void testDefaultConstructor() {
        // Act
        LoginResponse loginResponse = new LoginResponse();

        // Assert
        assertNotNull(loginResponse);
        assertNull(loginResponse.getToken());
        assertNull(loginResponse.getType());
    }

    @Test
    void testParameterizedConstructor() {
        // Arrange
        String token = "eyJhbGciOiJSUzI1NiJ9.test.token";
        String type = "Bearer";

        // Act
        LoginResponse loginResponse = new LoginResponse(token, type);

        // Assert
        assertEquals(token, loginResponse.getToken());
        assertEquals(type, loginResponse.getType());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse();
        String token = "eyJhbGciOiJSUzI1NiJ9.test.token";
        String type = "Bearer";

        // Act
        loginResponse.setToken(token);
        loginResponse.setType(type);

        // Assert
        assertEquals(token, loginResponse.getToken());
        assertEquals(type, loginResponse.getType());
    }

    @Test
    void testSetToken() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse();
        String token = "new.jwt.token";

        // Act
        loginResponse.setToken(token);

        // Assert
        assertEquals(token, loginResponse.getToken());
    }

    @Test
    void testSetType() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse();
        String type = "JWT";

        // Act
        loginResponse.setType(type);

        // Assert
        assertEquals(type, loginResponse.getType());
    }

    @Test
    void testSetTokenToNull() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse("test.token", "Bearer");

        // Act
        loginResponse.setToken(null);

        // Assert
        assertNull(loginResponse.getToken());
    }

    @Test
    void testSetTypeToNull() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse("test.token", "Bearer");

        // Act
        loginResponse.setType(null);

        // Assert
        assertNull(loginResponse.getType());
    }

    @Test
    void testSetTokenToEmptyString() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse("test.token", "Bearer");

        // Act
        loginResponse.setToken("");

        // Assert
        assertEquals("", loginResponse.getToken());
    }

    @Test
    void testSetTypeToEmptyString() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse("test.token", "Bearer");

        // Act
        loginResponse.setType("");

        // Assert
        assertEquals("", loginResponse.getType());
    }

    @Test
    void testConstructorWithNullValues() {
        // Act
        LoginResponse loginResponse = new LoginResponse(null, null);

        // Assert
        assertNull(loginResponse.getToken());
        assertNull(loginResponse.getType());
    }

    @Test
    void testConstructorWithEmptyStrings() {
        // Act
        LoginResponse loginResponse = new LoginResponse("", "");

        // Assert
        assertEquals("", loginResponse.getToken());
        assertEquals("", loginResponse.getType());
    }

    @Test
    void testMultipleSetOperations() {
        // Arrange
        LoginResponse loginResponse = new LoginResponse();

        // Act & Assert
        loginResponse.setToken("first.token");
        assertEquals("first.token", loginResponse.getToken());

        loginResponse.setToken("second.token");
        assertEquals("second.token", loginResponse.getToken());

        loginResponse.setType("Bearer");
        assertEquals("Bearer", loginResponse.getType());

        loginResponse.setType("JWT");
        assertEquals("JWT", loginResponse.getType());
    }

    @Test
    void testConstructorWithLongToken() {
        // Arrange
        String longToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MTYxNjE2LCJleHAiOjE2MTYxNjE2MTZ9.signature";
        String type = "Bearer";

        // Act
        LoginResponse loginResponse = new LoginResponse(longToken, type);

        // Assert
        assertEquals(longToken, loginResponse.getToken());
        assertEquals(type, loginResponse.getType());
    }

    @Test
    void testConstructorWithSpecialCharacters() {
        // Arrange
        String tokenWithSpecialChars = "eyJhbGciOiJSUzI1NiJ9.test-token_with.special+chars";
        String typeWithSpecialChars = "Bearer-Token";

        // Act
        LoginResponse loginResponse = new LoginResponse(tokenWithSpecialChars, typeWithSpecialChars);

        // Assert
        assertEquals(tokenWithSpecialChars, loginResponse.getToken());
        assertEquals(typeWithSpecialChars, loginResponse.getType());
    }
} 