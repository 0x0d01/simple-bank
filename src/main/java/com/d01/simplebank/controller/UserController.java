package com.d01.simplebank.controller;

import com.d01.simplebank.dto.CreateUserRequest;
import com.d01.simplebank.dto.UserResponse;
import com.d01.simplebank.exception.UserAlreadyExistsException;
import com.d01.simplebank.security.CustomUserDetails;
import com.d01.simplebank.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    // POST /users - Create a new user
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody CreateUserRequest request) {
        try {
            UserResponse user = userService.createUser(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // GET /users/:id - Get user by ID (ADMIN or owner only)
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            CustomUserDetails currentUserDetails = (CustomUserDetails)authentication.getDetails();
            
            // Check if user has ADMIN role
            boolean isAdmin = authentication.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
            
            // If not admin, check if current user is the owner
            if (!isAdmin && currentUserDetails.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            // Get the requested user
            UserResponse requestedUser = userService.getUserById(id);
            
            return ResponseEntity.ok(requestedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // GET /users/cid/:cid - Get user by CID
    @GetMapping("/cid/{cid}")
    public ResponseEntity<UserResponse> getUserByCid(@PathVariable String cid) {
        try {
            UserResponse user = userService.getUserByCid(cid);
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /users/cid/:cid/verify-pin - Verify PIN for a user
    @PostMapping("/cid/{cid}/verify-pin")
    public ResponseEntity<Boolean> verifyPin(@PathVariable String cid, @RequestBody String pin) {
        try {
            boolean isValid = userService.verifyPin(cid, pin);
            return ResponseEntity.ok(isValid);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 