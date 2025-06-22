package com.d01.simplebank.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Component
public class TransactionHashUtil {
    
    @Value("${transaction.signing.private-key-path:keys/transaction-signing-private-key.pem}")
    private String privateKeyPath;
    
    /**
     * Compute SHA-256 hash of the given string
     * @param input the input string to hash
     * @return SHA-256 hash as hex string
     */
    public String computeSha256Hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
    
    /**
     * Compute transaction hash by concatenating previous hash with current transaction data
     * @param previousHash the hash of the previous transaction (null if first transaction)
     * @param accountNo the account number
     * @param type the transaction type
     * @param channel the transaction channel
     * @param dateUtcTimestamp the UTC timestamp in milliseconds
     * @param amount the transaction amount
     * @return the computed hash
     */
    public String computeTransactionHash(String previousHash, String accountNo, String type, 
                                       String channel, long dateUtcTimestamp, int amount) {
        // Create the transaction data string
        String transactionData = String.format("%s%s%s%d%d", accountNo, type, channel, dateUtcTimestamp, amount);
        
        // If there's a previous hash, concatenate it with the transaction data
        String inputForHash = (previousHash != null) ? previousHash + transactionData : transactionData;
        
        // Compute SHA-256 hash
        return computeSha256Hash(inputForHash);
    }
    
    /**
     * Sign the hash using RSA private key
     * @param hash the hash to sign
     * @return Base64 encoded signature
     */
    public String signHash(String hash) {
        try {
            PrivateKey privateKey = loadPrivateKey();
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(hash.getBytes(StandardCharsets.UTF_8));
            byte[] signedBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to sign hash", e);
        }
    }
    
    /**
     * Load RSA private key from file
     * @return PrivateKey object
     */
    private PrivateKey loadPrivateKey() throws Exception {
        // For now, we'll use a simple approach. In production, you might want to use
        // a more secure key management system
        String privateKeyPEM = loadPrivateKeyFromFile();
        privateKeyPEM = privateKeyPEM.replace("-----BEGIN PRIVATE KEY-----", "")
                                   .replace("-----END PRIVATE KEY-----", "")
                                   .replaceAll("\\s", "");
        
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }
    
    /**
     * Load private key from file
     * @return private key content as string
     */
    private String loadPrivateKeyFromFile() {
        try {
            // This is a simplified implementation. In production, you should use
            // proper file reading with error handling
            java.nio.file.Path path = java.nio.file.Paths.get(privateKeyPath);
            return new String(java.nio.file.Files.readAllBytes(path), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key from: " + privateKeyPath, e);
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