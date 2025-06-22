package com.d01.simplebank.dto;

import com.d01.simplebank.entity.Customer;

import java.time.LocalDateTime;

public class CustomerResponse {
    private String id;
    private String cid;
    private String nameTh;
    private String nameEn;
    private String userId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Default constructor
    public CustomerResponse() {}
    
    // Constructor from Customer entity
    public CustomerResponse(Customer customer) {
        this.id = customer.getId();
        this.cid = customer.getCid();
        this.nameTh = customer.getNameTh();
        this.nameEn = customer.getNameEn();
        this.userId = customer.getUserId();
        this.createdDate = customer.getCreatedDate();
        this.updatedDate = customer.getUpdatedDate();
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
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
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