package com.d01.simplebank.dto;

public class CreateUserRequest {
    private String email;
    private String password;
    
    // Default constructor
    public CreateUserRequest() {}
    
    // Constructor with fields
    public CreateUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
} 