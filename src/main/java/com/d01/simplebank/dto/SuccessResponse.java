package com.d01.simplebank.dto;

public class SuccessResponse {
    private boolean success;
    private String id;

    public SuccessResponse(boolean success, String id) {
        this.success = success;
        this.id = id;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
} 