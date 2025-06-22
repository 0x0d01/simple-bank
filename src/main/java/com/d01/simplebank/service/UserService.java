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
        
        // Validate that USER role users must provide customer data
        if ("USER".equals(request.getRole())) {
            if (request.getCid() == null || request.getNameTh() == null || 
                request.getNameEn() == null || request.getPin() == null) {
                throw new IllegalArgumentException("Customer data (cid, nameTh, nameEn, pin) is required for USER role");
            }
            
            // Validate CID format (13 digits)
            if (!request.getCid().matches("\\d{13}")) {
                throw new IllegalArgumentException("CID must be exactly 13 numeric digits");
            }
            
            // Validate PIN format (6 digits)
            if (!request.getPin().matches("\\d{6}")) {
                throw new IllegalArgumentException("PIN must be exactly 6 numeric digits");
            }
            
            // Check if customer already exists
            if (userRepository.existsByCid(request.getCid())) {
                throw new UserAlreadyExistsException("User with CID " + request.getCid() + " already exists");
            }
        }
        
        // Encrypt password
        String encryptedPassword = passwordEncoder.encode(request.getPassword());
        
        // Encrypt PIN if provided
        String encryptedPin = null;
        if (request.getPin() != null) {
            encryptedPin = passwordEncoder.encode(request.getPin());
        }
        
        // Create new user with all fields
        User user = new User(
            request.getEmail(), 
            encryptedPassword, 
            request.getRole(),
            request.getCid(),
            request.getNameTh(),
            request.getNameEn(),
            encryptedPin
        );
        
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
    
    // Get user by CID
    public UserResponse getUserByCid(String cid) {
        User user = userRepository.findByCid(cid)
                .orElseThrow(() -> new RuntimeException("User not found with CID: " + cid));
        
        return new UserResponse(user);
    }
    
    // Verify PIN for a user
    public boolean verifyPin(String cid, String pin) {
        User user = userRepository.findByCid(cid)
                .orElseThrow(() -> new RuntimeException("User not found with CID: " + cid));
        
        if (user.getPin() == null) {
            throw new RuntimeException("User does not have a PIN");
        }
        
        return passwordEncoder.matches(pin, user.getPin());
    }
} 