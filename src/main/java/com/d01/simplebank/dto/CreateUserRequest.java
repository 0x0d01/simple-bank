package com.d01.simplebank.dto;

public class CreateUserRequest {
    private String email;
    private String password;
    private String role = "USER"; // Default role
    
    // Default constructor
    public CreateUserRequest() {}
    
    // Constructor with fields
    public CreateUserRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Constructor with fields including role
    public CreateUserRequest(String email, String password, String role) {
        this.email = email;
        this.password = password;
        this.role = role;
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
} 