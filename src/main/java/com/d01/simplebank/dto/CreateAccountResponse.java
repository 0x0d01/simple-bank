package com.d01.simplebank.dto;

public class CreateAccountResponse {
    private String id;

    public CreateAccountResponse() {}

    public CreateAccountResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
} 