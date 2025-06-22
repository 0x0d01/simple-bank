package com.d01.simplebank.dto;

import com.d01.simplebank.entity.Transaction;

import java.time.LocalDateTime;

public class TransactionResponse {
    
    private String id;
    private Long accountId;
    private String accountNo;
    private LocalDateTime transactionDate;
    private Integer amount;
    private String displayAmount;
    private String type;
    private String channel;
    private String remark;
    private String metadata;
    private String hash;
    private String signature;
    private String createdBy;
    private LocalDateTime createdDate;
    
    // Default constructor
    public TransactionResponse() {}
    
    // Constructor from Transaction entity
    public TransactionResponse(Transaction transaction) {
        this.id = transaction.getId();
        this.accountId = transaction.getAccount().getId();
        this.accountNo = String.format("%07d", transaction.getAccount().getId());
        this.transactionDate = transaction.getTransactionDate();
        this.amount = transaction.getAmount();
        this.displayAmount = transaction.getDisplayAmount();
        this.type = transaction.getType();
        this.channel = transaction.getChannel();
        this.remark = transaction.getRemark();
        this.metadata = transaction.getMetadata();
        this.hash = transaction.getHash();
        this.signature = transaction.getSignature();
        this.createdBy = transaction.getCreatedBy().getEmail();
        this.createdDate = transaction.getCreatedDate();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Long getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }
    
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
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
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
} 