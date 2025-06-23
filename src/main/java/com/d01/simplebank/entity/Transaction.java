package com.d01.simplebank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction {
    
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_no", nullable = false)
    private Account account;
    
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;
    
    @Column(name = "amount", nullable = false)
    private Integer amount; // stored as integer, e.g. 12345 means 123.45
    
    @Column(name = "display_amount", nullable = false)
    private String displayAmount; // always 2 decimal places, e.g. "123.45"
    
    @Column(name = "type", nullable = false)
    private String type;
    
    @Column(name = "channel", nullable = false)
    private String channel;
    
    @Column(name = "remark", columnDefinition = "TEXT")
    private String remark;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata;
    
    @Column(name = "hash", nullable = false, length = 64)
    private String hash; // SHA-256 hash of transaction data
    
    @Column(name = "signature", nullable = false, columnDefinition = "TEXT")
    private String signature; // RSA signature of the hash
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    // Default constructor
    public Transaction() {}
    
    // Constructor with required fields
    public Transaction(Account account, LocalDateTime transactionDate, Integer amount, 
                      String type, String channel, User createdBy) {
        this.account = account;
        this.transactionDate = transactionDate;
        setAmount(amount);
        this.type = type;
        this.channel = channel;
        this.createdBy = createdBy;
        this.createdDate = LocalDateTime.now();
    }
    
    // Constructor with all fields
    public Transaction(Account account, LocalDateTime transactionDate, Integer amount, 
                      String type, String channel, String remark, User createdBy, String metadata) {
        this.account = account;
        this.transactionDate = transactionDate;
        setAmount(amount);
        this.type = type;
        this.channel = channel;
        this.remark = remark;
        this.createdBy = createdBy;
        this.metadata = metadata;
        this.createdDate = LocalDateTime.now();
    }
    
    // Constructor with hash and signature
    public Transaction(Account account, LocalDateTime transactionDate, Integer amount, 
                      String type, String channel, String remark, User createdBy, String metadata,
                      String hash, String signature) {
        this.account = account;
        this.transactionDate = transactionDate;
        setAmount(amount);
        this.type = type;
        this.channel = channel;
        this.remark = remark;
        this.createdBy = createdBy;
        this.metadata = metadata;
        this.hash = hash;
        this.signature = signature;
        this.createdDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
        this.displayAmount = formatDisplayAmount(amount);
    }
    
    public String getDisplayAmount() {
        return displayAmount;
    }
    
    public void setDisplayAmount(String displayAmount) {
        this.displayAmount = displayAmount;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getChannel() {
        return channel;
    }
    
    public void setChannel(String channel) {
        this.channel = channel;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public User getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getHash() {
        return hash;
    }
    
    public void setHash(String hash) {
        this.hash = hash;
    }
    
    public String getSignature() {
        return signature;
    }
    
    public void setSignature(String signature) {
        this.signature = signature;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    private String formatDisplayAmount(Integer amount) {
        if (amount == null) return null;
        boolean negative = amount < 0;
        int abs = Math.abs(amount);
        int major = abs / 100;
        int minor = abs % 100;
        String formatted = String.format("%s%d.%02d", negative ? "-" : "", major, minor);
        return formatted;
    }
} 