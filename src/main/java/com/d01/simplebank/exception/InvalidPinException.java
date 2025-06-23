package com.d01.simplebank.exception;

public class InvalidPinException extends RuntimeException {
    
    public InvalidPinException(String message) {
        super(message);
    }
    
    public InvalidPinException(String message, Throwable cause) {
        super(message, cause);
    }
} 