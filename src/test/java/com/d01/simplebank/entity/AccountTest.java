package com.d01.simplebank.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class AccountTest {
    
    @Test
    public void testAccountCreation() {
        // Given
        String cid = "1234567890123";
        String nameTh = "ทดสอบ";
        String nameEn = "Test Account";
        
        // When
        Account account = new Account(cid, nameTh, nameEn);
        
        // Then
        assertNotNull(account);
        assertEquals(cid, account.getCid());
        assertEquals(nameTh, account.getNameTh());
        assertEquals(nameEn, account.getNameEn());
        assertNotNull(account.getCreatedDate());
        assertNotNull(account.getUpdatedDate());
    }
    
    @Test
    public void testAccountSettersAndGetters() {
        // Given
        Account account = new Account();
        Long id = 1234567L;
        String cid = "9876543210987";
        String nameTh = "บัญชีทดสอบ";
        String nameEn = "Test Account";
        LocalDateTime now = LocalDateTime.now();
        
        // When
        account.setId(id);
        account.setCid(cid);
        account.setNameTh(nameTh);
        account.setNameEn(nameEn);
        account.setCreatedDate(now);
        account.setUpdatedDate(now);
        
        // Then
        assertEquals(id, account.getId());
        assertEquals(cid, account.getCid());
        assertEquals(nameTh, account.getNameTh());
        assertEquals(nameEn, account.getNameEn());
        assertEquals(now, account.getCreatedDate());
        assertEquals(now, account.getUpdatedDate());
    }
    
    @Test
    public void testAccountDefaultConstructor() {
        // When
        Account account = new Account();
        
        // Then
        assertNotNull(account);
        assertNull(account.getId());
        assertNull(account.getCid());
        assertNull(account.getNameTh());
        assertNull(account.getNameEn());
        assertNull(account.getCreatedDate());
        assertNull(account.getUpdatedDate());
    }
} 