package com.d01.simplebank.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleUserAlreadyExists(UserAlreadyExistsException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "User already exists");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad request");
        errorResponse.put("message", "Invalid JSON format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 