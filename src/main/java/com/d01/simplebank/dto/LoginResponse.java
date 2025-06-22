package com.d01.simplebank.dto;

public class LoginResponse {
    private String token;
    private String type;

    // Default constructor
    public LoginResponse() {}

    // Constructor with fields
    public LoginResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
} 