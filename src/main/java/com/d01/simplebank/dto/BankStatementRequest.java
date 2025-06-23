package com.d01.simplebank.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class BankStatementRequest {
    
    @NotNull(message = "PIN is required")
    @Pattern(regexp = "^[0-9]{6}$", message = "PIN must be exactly 6 numeric digits")
    private String pin;
    
    @NotNull(message = "Since timestamp is required")
    private Long since;
    
    @NotNull(message = "Until timestamp is required")
    private Long until;
    
    // Default constructor
    public BankStatementRequest() {}
    
    // Constructor with required fields
    public BankStatementRequest(String pin, Long since, Long until) {
        this.pin = pin;
        this.since = since;
        this.until = until;
    }
    
    // Getters and Setters
    public String getPin() {
        return pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public Long getSince() {
        return since;
    }
    
    public void setSince(Long since) {
        this.since = since;
    }
    
    public Long getUntil() {
        return until;
    }
    
    public void setUntil(Long until) {
        this.until = until;
    }
} 