package com.d01.simplebank.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(name = "customers")
public class Customer {
    
    @Id
    @UuidGenerator
    private String id;
    
    @Column(name = "cid", unique = true, nullable = false, length = 13)
    private String cid; // 13-digit numeric string
    
    @Column(name = "name_th", nullable = false, columnDefinition = "VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci")
    private String nameTh; // Thai name with UTF-8 support
    
    @Column(name = "name_en", nullable = false)
    private String nameEn; // English name
    
    @Column(nullable = false)
    private String pin; // Encrypted 6-digit PIN using bcrypt
    
    @Column(name = "user_id", nullable = false)
    private String userId; // Foreign key to users table
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    private User user;
    
    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    // Default constructor
    public Customer() {}
    
    // Constructor with required fields
    public Customer(String cid, String nameTh, String nameEn, String pin, String userId) {
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.pin = pin;
        this.userId = userId;
        this.createdDate = java.time.LocalDateTime.now();
        this.updatedDate = java.time.LocalDateTime.now();
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
    
    public String getPin() {
        return pin;
    }
    
    public void setPin(String pin) {
        this.pin = pin;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
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