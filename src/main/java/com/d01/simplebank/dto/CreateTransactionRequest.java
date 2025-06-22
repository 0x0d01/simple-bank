package com.d01.simplebank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CreateTransactionRequest {
    
    @NotNull(message = "Account ID is required")
    private Long accountId;
    
    @NotNull(message = "Transaction date is required")
    private LocalDateTime transactionDate;
    
    @NotNull(message = "Amount is required")
    private Integer amount; // stored as integer, e.g. 12345 means 123.45
    
    @NotNull(message = "Type is required")
    @Size(min = 1, max = 10, message = "Type must be between 1 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Type must contain only uppercase letters and numbers")
    private String type;
    
    @NotNull(message = "Channel is required")
    @Size(min = 1, max = 20, message = "Channel must be between 1 and 20 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Channel must contain only uppercase letters, numbers, and underscores")
    private String channel;
    
    @Size(max = 500, message = "Remark must not exceed 500 characters")
    private String remark;
    
    @Size(max = 1000, message = "Metadata must not exceed 1000 characters")
    private String metadata;
    
    // Default constructor
    public CreateTransactionRequest() {}
    
    // Constructor with required fields
    public CreateTransactionRequest(Long accountId, LocalDateTime transactionDate, Integer amount, 
                                  String type, String channel) {
        this.accountId = accountId;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.type = type;
        this.channel = channel;
    }
    
    // Constructor with all fields
    public CreateTransactionRequest(Long accountId, LocalDateTime transactionDate, Integer amount, 
                                  String type, String channel, String remark, String metadata) {
        this.accountId = accountId;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.type = type;
        this.channel = channel;
        this.remark = remark;
        this.metadata = metadata;
    }
    
    // Getters and Setters
    public Long getAccountId() {
        return accountId;
    }
    
    public void setAccountId(Long accountId) {
        this.accountId = accountId;
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
} 