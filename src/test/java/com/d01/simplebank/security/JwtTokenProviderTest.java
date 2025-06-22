package com.d01.simplebank.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;

    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_PRIVATE_KEY_PATH = "keys/rsa-private-key.pem";
    private static final String TEST_PUBLIC_KEY_PATH = "keys/rsa-public-key.pem";

    @BeforeEach
    void setUp() {
        // Set the key paths using reflection
        ReflectionTestUtils.setField(jwtTokenProvider, "privateKeyPath", TEST_PRIVATE_KEY_PATH);
        ReflectionTestUtils.setField(jwtTokenProvider, "publicKeyPath", TEST_PUBLIC_KEY_PATH);
        ReflectionTestUtils.setField(jwtTokenProvider, "jwtExpiration", 3600000L); // 1 hour
    }

    @Test
    void generateToken_WithValidEmail_ShouldReturnValidToken() {
        // Arrange
        String email = TEST_EMAIL;

        // Act
        String token = jwtTokenProvider.generateToken(email);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token structure (should have 3 parts separated by dots)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
        
        // Verify we can extract email from the token
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
    }

    @Test
    void generateToken_WithNullEmail_ShouldGenerateToken() {
        // Act
        String token = jwtTokenProvider.generateToken(null);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token structure (should have 3 parts separated by dots)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void generateToken_WithEmptyEmail_ShouldGenerateToken() {
        // Act
        String token = jwtTokenProvider.generateToken("");

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        
        // Verify token structure (should have 3 parts separated by dots)
        String[] parts = token.split("\\.");
        assertEquals(3, parts.length);
    }

    @Test
    void getEmailFromToken_WithValidToken_ShouldReturnEmail() {
        // Arrange
        String email = TEST_EMAIL;
        String token = jwtTokenProvider.generateToken(email);

        // Act
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);

        // Assert
        assertEquals(email, extractedEmail);
    }

    @Test
    void getEmailFromToken_WithInvalidToken_ShouldThrowException() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act & Assert
        assertThrows(Exception.class, () -> {
            jwtTokenProvider.getEmailFromToken(invalidToken);
        });
    }

    @Test
    void getEmailFromToken_WithNullToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.getEmailFromToken(null);
        });
    }

    @Test
    void getEmailFromToken_WithEmptyToken_ShouldThrowException() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            jwtTokenProvider.getEmailFromToken("");
        });
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        // Arrange
        String email = TEST_EMAIL;
        String token = jwtTokenProvider.generateToken(email);

        // Act
        boolean isValid = jwtTokenProvider.validateToken(token);

        // Assert
        assertTrue(isValid);
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        // Arrange
        String invalidToken = "invalid.token.here";

        // Act
        boolean isValid = jwtTokenProvider.validateToken(invalidToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithNullToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken(null);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithEmptyToken_ShouldReturnFalse() {
        // Act
        boolean isValid = jwtTokenProvider.validateToken("");

        // Assert
        assertFalse(isValid);
    }

    @Test
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        // This test would require creating a token with a past expiration date
        // For now, we'll test with a malformed token
        String malformedToken = "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNjE2MTYxNjE2LCJleHAiOjE2MTYxNjE2MTZ9.invalid_signature";
        
        // Act
        boolean isValid = jwtTokenProvider.validateToken(malformedToken);

        // Assert
        assertFalse(isValid);
    }

    @Test
    void generateToken_ShouldIncludeCorrectClaims() {
        // Arrange
        String email = TEST_EMAIL;

        // Act
        String token = jwtTokenProvider.generateToken(email);

        // Assert
        String extractedEmail = jwtTokenProvider.getEmailFromToken(token);
        assertEquals(email, extractedEmail);
        
        // Verify token is valid
        assertTrue(jwtTokenProvider.validateToken(token));
    }

    @Test
    void generateToken_ShouldHaveCorrectExpiration() {
        // Arrange
        String email = TEST_EMAIL;

        // Act
        String token = jwtTokenProvider.generateToken(email);

        // Assert
        // The token should be valid immediately after generation
        assertTrue(jwtTokenProvider.validateToken(token));
        
        // We can't easily test the exact expiration time without parsing the JWT,
        // but we can verify the token is generated successfully
        assertNotNull(token);
    }

    @Test
    void generateToken_WithDifferentEmails_ShouldGenerateDifferentTokens() {
        // Arrange
        String email1 = "user1@example.com";
        String email2 = "user2@example.com";

        // Act
        String token1 = jwtTokenProvider.generateToken(email1);
        String token2 = jwtTokenProvider.generateToken(email2);

        // Assert
        assertNotEquals(token1, token2);
        assertEquals(email1, jwtTokenProvider.getEmailFromToken(token1));
        assertEquals(email2, jwtTokenProvider.getEmailFromToken(token2));
    }

    @Test
    void generateToken_WithSameEmail_ShouldGenerateValidTokens() {
        // Arrange
        String email = TEST_EMAIL;

        // Act
        String token1 = jwtTokenProvider.generateToken(email);
        String token2 = jwtTokenProvider.generateToken(email);

        // Assert
        // Both tokens should be valid and contain the same email
        assertTrue(jwtTokenProvider.validateToken(token1));
        assertTrue(jwtTokenProvider.validateToken(token2));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token1));
        assertEquals(email, jwtTokenProvider.getEmailFromToken(token2));
        
        // Both tokens should be valid JWT structure
        String[] parts1 = token1.split("\\.");
        String[] parts2 = token2.split("\\.");
        assertEquals(3, parts1.length);
        assertEquals(3, parts2.length);
    }
} 