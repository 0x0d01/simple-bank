package com.d01.simplebank.dto;

import com.d01.simplebank.entity.Account;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public class AccountResponse {
    
    private String id;
    private String cid;
    private String nameTh;
    private String nameEn;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdDate;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedDate;
    
    // Default constructor
    public AccountResponse() {}
    
    // Constructor from Account entity
    public AccountResponse(Account account) {
        this.id = String.format("%07d", account.getId());
        this.cid = account.getCid();
        this.nameTh = account.getNameTh();
        this.nameEn = account.getNameEn();
        this.createdDate = account.getCreatedDate();
        this.updatedDate = account.getUpdatedDate();
    }
    
    // Constructor with fields
    public AccountResponse(Long id, String cid, String nameTh, String nameEn, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = String.format("%07d", id);
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
} 