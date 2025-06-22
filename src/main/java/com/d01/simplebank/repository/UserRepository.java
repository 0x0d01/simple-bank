package com.d01.simplebank.repository;

import com.d01.simplebank.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    
    // Find user by email
    Optional<User> findByEmail(String email);
    
    // Check if user exists by email
    boolean existsByEmail(String email);
} 