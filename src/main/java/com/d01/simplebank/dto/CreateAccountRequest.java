package com.d01.simplebank.dto;

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
    
    // Default constructor
    public CreateAccountRequest() {}
    
    // Constructor with fields
    public CreateAccountRequest(String cid, String nameTh, String nameEn) {
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
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
} 