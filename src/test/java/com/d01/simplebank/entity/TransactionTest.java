package com.d01.simplebank.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class TransactionTest {
    
    private Account testAccount;
    private User testUser;
    private LocalDateTime testDate;
    
    @BeforeEach
    public void setUp() {
        testAccount = new Account("1234567890123", "ทดสอบ", "Test Account");
        
        testUser = new User("test@example.com", "password", "USER");
        
        testDate = LocalDateTime.now();
    }
    
    @Test
    public void testTransactionCreationWithRequiredFields() {
        Integer amount = 10050; // 100.50
        String type = "DEPOSIT";
        String channel = "MOBILE_APP";
        
        Transaction transaction = new Transaction(testAccount, testDate, amount, type, channel, testUser);
        
        assertNotNull(transaction);
        assertEquals(testAccount, transaction.getAccount());
        assertEquals(testDate, transaction.getTransactionDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals("100.50", transaction.getDisplayAmount());
        assertEquals(type, transaction.getType());
        assertEquals(channel, transaction.getChannel());
        assertEquals(testUser, transaction.getCreatedBy());
        assertNotNull(transaction.getCreatedDate());
        assertNull(transaction.getRemark());
        assertNull(transaction.getMetadata());
    }
    
    @Test
    public void testTransactionCreationWithAllFields() {
        Integer amount = -5025; // -50.25
        String type = "WITHDRAWAL";
        String channel = "ATM";
        String remark = "ATM withdrawal";
        String metadata = "{\"atm_id\":\"ATM001\",\"location\":\"Bangkok\"}";
        
        Transaction transaction = new Transaction(testAccount, testDate, amount, type, channel, remark, testUser, metadata);
        
        assertNotNull(transaction);
        assertEquals(testAccount, transaction.getAccount());
        assertEquals(testDate, transaction.getTransactionDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals("-50.25", transaction.getDisplayAmount());
        assertEquals(type, transaction.getType());
        assertEquals(channel, transaction.getChannel());
        assertEquals(remark, transaction.getRemark());
        assertEquals(testUser, transaction.getCreatedBy());
        assertEquals(metadata, transaction.getMetadata());
        assertNotNull(transaction.getCreatedDate());
    }
    
    @Test
    public void testTransactionSettersAndGetters() {
        Transaction transaction = new Transaction();
        String id = "transaction-123";
        Integer amount = 20000; // 200.00
        String type = "TRANSFER";
        String channel = "ONLINE_BANKING";
        String remark = "Transfer to savings";
        String metadata = "{\"recipient\":\"savings-account\"}";
        
        transaction.setId(id);
        transaction.setAccount(testAccount);
        transaction.setTransactionDate(testDate);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setChannel(channel);
        transaction.setRemark(remark);
        transaction.setCreatedBy(testUser);
        transaction.setMetadata(metadata);
        
        assertEquals(id, transaction.getId());
        assertEquals(testAccount, transaction.getAccount());
        assertEquals(testDate, transaction.getTransactionDate());
        assertEquals(amount, transaction.getAmount());
        assertEquals("200.00", transaction.getDisplayAmount());
        assertEquals(type, transaction.getType());
        assertEquals(channel, transaction.getChannel());
        assertEquals(remark, transaction.getRemark());
        assertEquals(testUser, transaction.getCreatedBy());
        assertEquals(metadata, transaction.getMetadata());
    }
    
    @Test
    public void testTransactionDefaultConstructor() {
        Transaction transaction = new Transaction();
        
        assertNotNull(transaction);
        assertNull(transaction.getId());
        assertNull(transaction.getAccount());
        assertNull(transaction.getTransactionDate());
        assertNull(transaction.getAmount());
        assertNull(transaction.getDisplayAmount());
        assertNull(transaction.getType());
        assertNull(transaction.getChannel());
        assertNull(transaction.getRemark());
        assertNull(transaction.getCreatedBy());
        assertNull(transaction.getMetadata());
        assertNull(transaction.getCreatedDate());
    }
    
    @Test
    public void testTransactionWithNegativeAmount() {
        Integer negativeAmount = -7500; // -75.00
        String type = "WITHDRAWAL";
        String channel = "ATM";
        
        Transaction transaction = new Transaction(testAccount, testDate, negativeAmount, type, channel, testUser);
        
        assertEquals(negativeAmount, transaction.getAmount());
        assertEquals("-75.00", transaction.getDisplayAmount());
    }
    
    @Test
    public void testTransactionWithZeroAmount() {
        Integer zeroAmount = 0;
        String type = "ADJUSTMENT";
        String channel = "SYSTEM";
        
        Transaction transaction = new Transaction(testAccount, testDate, zeroAmount, type, channel, testUser);
        
        assertEquals(zeroAmount, transaction.getAmount());
        assertEquals("0.00", transaction.getDisplayAmount());
    }
} 