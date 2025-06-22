package com.d01.simplebank.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class TransactionHashUtilTest {
    
    private TransactionHashUtil transactionHashUtil;
    
    @BeforeEach
    public void setUp() {
        transactionHashUtil = new TransactionHashUtil();
    }
    
    @Test
    public void testComputeSha256Hash() {
        String input = "1234567A1ATS175059864615798700";
        String expectedHash = computeExpectedSha256Hash(input);
        String actualHash = transactionHashUtil.computeSha256Hash(input);
        
        assertEquals(expectedHash, actualHash);
        assertEquals(64, actualHash.length()); // SHA-256 produces 64 hex characters
    }
    
    @Test
    public void testComputeTransactionHash_FirstTransaction() {
        String accountNo = "1234567";
        String type = "A1";
        String channel = "ATS";
        long dateUtcTimestamp = 1750598646157L;
        int amount = 98700; // 987.00
        
        String hash = transactionHashUtil.computeTransactionHash(
                null, accountNo, type, channel, dateUtcTimestamp, amount);
        
        // The hash should be the SHA-256 of "1234567A1ATS175059864615798700"
        String expectedInput = "1234567A1ATS175059864615798700";
        String expectedHash = computeExpectedSha256Hash(expectedInput);
        
        assertEquals(expectedHash, hash);
    }
    
    @Test
    public void testComputeTransactionHash_WithPreviousHash() {
        String previousHash = "a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456";
        String accountNo = "1234567";
        String type = "A1";
        String channel = "ATS";
        long dateUtcTimestamp = 1750598646157L;
        int amount = 98700; // 987.00
        
        String hash = transactionHashUtil.computeTransactionHash(
                previousHash, accountNo, type, channel, dateUtcTimestamp, amount);
        
        // The hash should be the SHA-256 of previousHash + "1234567A1ATS175059864615798700"
        String expectedInput = previousHash + "1234567A1ATS175059864615798700";
        String expectedHash = computeExpectedSha256Hash(expectedInput);
        
        assertEquals(expectedHash, hash);
    }
    
    @Test
    public void testComputeTransactionHash_ExampleFromRequirements() {
        // Test the exact example from requirements
        String accountNo = "1234567";
        String type = "A1";
        String channel = "ATS";
        long dateUtcTimestamp = 1750598646157L;
        int amount = 98700; // 987.00
        
        String hash = transactionHashUtil.computeTransactionHash(
                null, accountNo, type, channel, dateUtcTimestamp, amount);
        
        // Verify the input string format matches the example
        String expectedInput = "1234567A1ATS175059864615798700";
        String expectedHash = computeExpectedSha256Hash(expectedInput);
        
        assertEquals(expectedHash, hash);
        
        // Also verify that the input string is exactly as specified in requirements
        String actualInput = String.format("%s%s%s%d%d", accountNo, type, channel, dateUtcTimestamp, amount);
        assertEquals("1234567A1ATS175059864615798700", actualInput);
    }
    
    @Test
    public void testComputeTransactionHash_ChainOfTransactions() {
        String accountNo = "1234567";
        String type1 = "A1";
        String channel1 = "ATS";
        long dateUtcTimestamp1 = 1750598646157L;
        int amount1 = 98700;
        
        String type2 = "W1";
        String channel2 = "ATM";
        long dateUtcTimestamp2 = 1750598647000L;
        int amount2 = 50000;
        
        // First transaction
        String hash1 = transactionHashUtil.computeTransactionHash(
                null, accountNo, type1, channel1, dateUtcTimestamp1, amount1);
        
        // Second transaction (should use hash1 as previous hash)
        String hash2 = transactionHashUtil.computeTransactionHash(
                hash1, accountNo, type2, channel2, dateUtcTimestamp2, amount2);
        
        // Verify hash1 is not null and has correct length
        assertNotNull(hash1);
        assertEquals(64, hash1.length());
        
        // Verify hash2 is not null, has correct length, and is different from hash1
        assertNotNull(hash2);
        assertEquals(64, hash2.length());
        assertNotEquals(hash1, hash2);
        
        // Verify hash2 is computed correctly
        String expectedInput2 = hash1 + "1234567W1ATM175059864700050000";
        String expectedHash2 = computeExpectedSha256Hash(expectedInput2);
        assertEquals(expectedHash2, hash2);
    }
    
    /**
     * Compute SHA-256 hash manually for comparison
     * @param input the input string
     * @return SHA-256 hash as hex string
     */
    private String computeExpectedSha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute expected hash", e);
        }
    }
    
    /**
     * Convert byte array to hex string
     * @param bytes the byte array
     * @return hex string
     */
    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
} 