package com.d01.simplebank.entity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

public class BalanceTest {
    private Account testAccount;
    private Transaction testTransaction;

    @BeforeEach
    public void setUp() {
        testAccount = new Account("1234567890123", "ทดสอบ", "Test Account");
        testAccount.setId(1L);
        User testUser = new User("test@example.com", "password", "USER");
        testUser.setId("user-123");
        testTransaction = new Transaction(testAccount, LocalDateTime.now(), 10000, "DEPOSIT", "MOBILE_APP", testUser);
        testTransaction.setId("transaction-123");
    }

    @Test
    public void testBalanceCreation() {
        Integer balance = 100050; // 1000.50
        Balance balanceEntity = new Balance(testAccount, testTransaction, balance);
        assertNotNull(balanceEntity);
        assertEquals(testAccount, balanceEntity.getAccount());
        assertEquals(testTransaction, balanceEntity.getLatestTransaction());
        assertEquals(balance, balanceEntity.getBalance());
        assertEquals("1000.50", balanceEntity.getDisplayBalance());
        assertNotNull(balanceEntity.getUpdatedDate());
    }

    @Test
    public void testBalanceSettersAndGetters() {
        Balance balance = new Balance();
        Integer balanceAmount = 250075; // 2500.75
        LocalDateTime now = LocalDateTime.now();
        balance.setAccount(testAccount);
        balance.setLatestTransaction(testTransaction);
        balance.setBalance(balanceAmount);
        balance.setUpdatedDate(now);
        assertEquals(testAccount, balance.getAccount());
        assertEquals(testTransaction, balance.getLatestTransaction());
        assertEquals(balanceAmount, balance.getBalance());
        assertEquals("2500.75", balance.getDisplayBalance());
        assertEquals(now, balance.getUpdatedDate());
    }

    @Test
    public void testBalanceDefaultConstructor() {
        Balance balance = new Balance();
        assertNotNull(balance);
        assertNull(balance.getAccount());
        assertNull(balance.getLatestTransaction());
        assertNull(balance.getBalance());
        assertNull(balance.getDisplayBalance());
        assertNull(balance.getUpdatedDate());
    }

    @Test
    public void testBalanceWithZeroAmount() {
        Integer zeroBalance = 0;
        Balance balance = new Balance(testAccount, testTransaction, zeroBalance);
        assertEquals(zeroBalance, balance.getBalance());
        assertEquals("0.00", balance.getDisplayBalance());
    }

    @Test
    public void testBalanceWithPositiveAmount() {
        Integer positiveBalance = 500000; // 5000.00
        Balance balance = new Balance(testAccount, testTransaction, positiveBalance);
        assertEquals(positiveBalance, balance.getBalance());
        assertEquals("5000.00", balance.getDisplayBalance());
    }

    @Test
    public void testBalanceWithNegativeAmountThrowsException() {
        Integer negativeBalance = -10000; // -100.00
        Balance balance = new Balance();
        assertThrows(IllegalArgumentException.class, () -> {
            balance.setBalance(negativeBalance);
        }, "Balance cannot be negative");
    }

    @Test
    public void testBalanceWithNegativeAmountInConstructor() {
        Integer negativeBalance = -5000; // -50.00
        assertThrows(IllegalArgumentException.class, () -> {
            new Balance(testAccount, testTransaction, negativeBalance);
        });
    }

    @Test
    public void testBalanceWithLargeAmount() {
        Integer largeBalance = 999999999; // 9999999.99
        Balance balance = new Balance(testAccount, testTransaction, largeBalance);
        assertEquals(largeBalance, balance.getBalance());
        assertEquals("9999999.99", balance.getDisplayBalance());
    }

    @Test
    public void testBalanceWithDecimalAmount() {
        Integer decimalBalance = 123456; // 1234.56
        Balance balance = new Balance(testAccount, testTransaction, decimalBalance);
        assertEquals(decimalBalance, balance.getBalance());
        assertEquals("1234.56", balance.getDisplayBalance());
    }
} 