package com.d01.simplebank.repository;

import com.d01.simplebank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Find user by email
    User findByEmail(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
} 