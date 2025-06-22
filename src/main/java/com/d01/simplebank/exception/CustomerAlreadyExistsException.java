package com.d01.simplebank.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    
    public CustomerAlreadyExistsException(String message) {
        super(message);
    }
    
    public CustomerAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
} 