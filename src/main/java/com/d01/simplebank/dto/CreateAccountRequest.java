package com.d01.simplebank.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class CreateAccountRequest {
    
    @NotBlank(message = "CID is required")
    @Pattern(regexp = "^\\d{13}$", message = "CID must be exactly 13 numeric digits")
    private String cid;
    
    @NotBlank(message = "Thai name is required")
    private String nameTh;
    
    @NotBlank(message = "English name is required")
    private String nameEn;
    
    @Min(value = 1, message = "Amount must be 1 or more if provided")
    private Integer amount; // Optional initial deposit amount
    
    // Default constructor
    public CreateAccountRequest() {}
    
    // Constructor with fields
    public CreateAccountRequest(String cid, String nameTh, String nameEn) {
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
    }
    
    // Constructor with all fields including amount
    public CreateAccountRequest(String cid, String nameTh, String nameEn, Integer amount) {
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.amount = amount;
    }
    
    // Getters and Setters
    public String getCid() {
        return cid;
    }
    
    public void setCid(String cid) {
        this.cid = cid;
    }
    
    public String getNameTh() {
        return nameTh;
    }
    
    public void setNameTh(String nameTh) {
        this.nameTh = nameTh;
    }
    
    public String getNameEn() {
        return nameEn;
    }
    
    public void setNameEn(String nameEn) {
        this.nameEn = nameEn;
    }
    
    public Integer getAmount() {
        return amount;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
} 