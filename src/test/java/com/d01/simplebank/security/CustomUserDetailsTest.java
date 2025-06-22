package com.d01.simplebank.security;

import com.d01.simplebank.entity.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class CustomUserDetailsTest {
    
    @Test
    public void testCustomUserDetails_ShouldContainAllUserFields() {
        // Given
        User user = new User();
        user.setId("test-id");
        user.setEmail("test@example.com");
        user.setPassword("encrypted-password");
        user.setRole("USER");
        user.setCid("1234567890123");
        user.setNameTh("ทดสอบ ระบบ");
        user.setNameEn("Test System");
        user.setPin("encrypted-pin");
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        
        // When
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
        // Then
        assertEquals("test-id", userDetails.getId());
        assertEquals("test@example.com", userDetails.getUsername());
        assertEquals("encrypted-password", userDetails.getPassword());
        assertEquals("USER", userDetails.getRole());
        assertEquals("1234567890123", userDetails.getCid());
        assertEquals("ทดสอบ ระบบ", userDetails.getNameTh());
        assertEquals("Test System", userDetails.getNameEn());
        assertEquals("encrypted-pin", userDetails.getPin());
        assertNotNull(userDetails.getCreatedDate());
        assertNotNull(userDetails.getUpdatedDate());
        assertTrue(userDetails.isEnabled());
        assertTrue(userDetails.isAccountNonExpired());
        assertTrue(userDetails.isAccountNonLocked());
        assertTrue(userDetails.isCredentialsNonExpired());
        
        // Test authorities
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_USER")));
    }
    
    @Test
    public void testCustomUserDetails_WithAdminRole_ShouldHaveCorrectAuthorities() {
        // Given
        User user = new User();
        user.setId("admin-id");
        user.setEmail("admin@example.com");
        user.setPassword("encrypted-password");
        user.setRole("ADMIN");
        // Admin users don't have customer fields
        user.setCreatedDate(LocalDateTime.now());
        user.setUpdatedDate(LocalDateTime.now());
        
        // When
        CustomUserDetails userDetails = new CustomUserDetails(user);
        
        // Then
        assertEquals("admin-id", userDetails.getId());
        assertEquals("admin@example.com", userDetails.getUsername());
        assertEquals("ADMIN", userDetails.getRole());
        assertNull(userDetails.getCid());
        assertNull(userDetails.getNameTh());
        assertNull(userDetails.getNameEn());
        assertNull(userDetails.getPin());
        
        // Test authorities
        assertEquals(1, userDetails.getAuthorities().size());
        assertTrue(userDetails.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN")));
    }
} 