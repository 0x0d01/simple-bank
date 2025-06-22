package com.d01.simplebank.dto;

public class CreateUserRequest {
    private String email;
    private String password;
    private String role = "USER"; // Default role
    
    // Customer fields
    private String cid;
    private String nameTh;
    private String nameEn;
    private String pin;
    
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
    
    // Constructor with all fields including customer information
    public CreateUserRequest(String email, String password, String role, String cid, String nameTh, String nameEn, String pin) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.cid = cid;
        this.nameTh = nameTh;
        this.nameEn = nameEn;
        this.pin = pin;
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
} 