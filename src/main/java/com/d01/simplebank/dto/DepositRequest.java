package com.d01.simplebank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DepositRequest {
    
    @NotNull(message = "ID is required")
    private String id;
    
    @NotNull(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{7}$", message = "Account number must be exactly 7 digits")
    private String accountNo;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be 1 or more")
    private Integer amount;
    
    // Default constructor
    public DepositRequest() {}
    
    // Constructor with required fields
    public DepositRequest(String id, String accountNo, Integer amount) {
        this.id = id;
        this.accountNo = accountNo;
        this.amount = amount;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getAccountNo() {
        return accountNo;
    }
    
    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
} 