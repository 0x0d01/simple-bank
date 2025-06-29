package com.d01.simplebank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class TransferRequest {
    
    @NotNull(message = "Sender account number is required")
    @Pattern(regexp = "^[0-9]{7}$", message = "Sender account number must be exactly 7 digits")
    private String senderAccountNo;
    
    @NotNull(message = "Receiver account number is required")
    @Pattern(regexp = "^[0-9]{7}$", message = "Receiver account number must be exactly 7 digits")
    private String receiverAccountNo;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be 1 or more")
    private Integer amount;
    
    @NotNull(message = "PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN must be exactly 6 numeric digits")
    private String pin;
    
    // Default constructor
    public TransferRequest() {}
    
    // Constructor with required fields
    public TransferRequest(String senderAccountNo, String receiverAccountNo, Integer amount, String pin) {
        this.senderAccountNo = senderAccountNo;
        this.receiverAccountNo = receiverAccountNo;
        this.amount = amount;
        this.pin = pin;
    }
    
    // Getters and Setters
    public String getSenderAccountNo() {
        return senderAccountNo;
    }
    
    public void setSenderAccountNo(String senderAccountNo) {
        this.senderAccountNo = senderAccountNo;
    }
    
    public String getReceiverAccountNo() {
        return receiverAccountNo;
    }
    
    public void setReceiverAccountNo(String receiverAccountNo) {
        this.receiverAccountNo = receiverAccountNo;
    }
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
    
    public String getPin() {
        return pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }
} 