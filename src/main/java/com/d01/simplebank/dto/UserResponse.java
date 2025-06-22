package com.d01.simplebank.dto;

import com.d01.simplebank.entity.User;

import java.time.LocalDateTime;

public class UserResponse {
    private String id;
    private String email;
    private String role;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    
    // Default constructor
    public UserResponse() {}
    
    // Constructor from User entity
    public UserResponse(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.createdDate = user.getCreatedDate();
        this.updatedDate = user.getUpdatedDate();
    }
    
    // Constructor with fields
    public UserResponse(String id, String email, String role, LocalDateTime createdDate, LocalDateTime updatedDate) {
        this.id = id;
        this.email = email;
        this.role = role;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
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