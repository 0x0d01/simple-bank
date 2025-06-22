package com.d01.simplebank.exception;

public class AccountAlreadyExistsException extends RuntimeException {
    
    public AccountAlreadyExistsException(String message) {
        super(message);
    }
    
    public AccountAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 