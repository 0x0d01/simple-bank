package com.d01.simplebank.dto;

public class CreateUserResponse {
    private String id;

    public CreateUserResponse() {}

    public CreateUserResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
} 