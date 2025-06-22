package com.d01.simplebank.service;

import com.d01.simplebank.dto.CreateUserRequest;
import com.d01.simplebank.dto.UserResponse;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.CustomerAlreadyExistsException;
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
    
    @Autowired
    private CustomerService customerService;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    // Create a new user
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // Check if user already exists (optimistic check)
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + request.getEmail() + " already exists");
        }
        
        // Validate that USER role users must provide customer data
        if ("USER".equals(request.getRole())) {
            if (request.getCid() == null || request.getNameTh() == null || 
                request.getNameEn() == null || request.getPin() == null) {
                throw new IllegalArgumentException("Customer data (cid, nameTh, nameEn, pin) is required for USER role");
            }
        }
        
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        
        // Create new user with role
        User user = new User(request.getEmail(), encryptedPassword, request.getRole());
        
        try {
            User savedUser = userRepository.save(user);
            
            // If user role is "USER", create customer record in the same transaction
            if ("USER".equals(request.getRole())) {
                try {
                    customerService.createCustomer(
                        request.getCid(),
                        request.getNameTh(),
                        request.getNameEn(),
                        request.getPin(),
                        savedUser.getId()
                    );
                } catch (CustomerAlreadyExistsException e) {
                    // If customer creation fails, the entire transaction will be rolled back
                    throw e;
                }
            }
            
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