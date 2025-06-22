package com.d01.simplebank.service;

import com.d01.simplebank.dto.CreateUserRequest;
import com.d01.simplebank.dto.UserResponse;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.UserAlreadyExistsException;
import com.d01.simplebank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Create a new user
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check if user already exists (optimistic check)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create new user with role
        User user = new User(request.getEmail(), encryptedPassword, request.getRole());
        
        try {
            User savedUser = userRepository.save(user);
            return new UserResponse(savedUser);
        } catch (DataIntegrityViolationException e) {
            // Handle concurrent registration attempts
            String errorMessage = e.getMessage();

            // Check for MySQL duplicate entry errors
            if (errorMessage.contains("Duplicate entry")) {
                throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists", e);
            }
            throw e; // Re-throw if it's a different constraint violation
        }
    }
    
    // Get all users
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }
    
    // Get user by ID
    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        return new UserResponse(user);
    }
    
    // Get user by email
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
        
        return new UserResponse(user);
    }
} 