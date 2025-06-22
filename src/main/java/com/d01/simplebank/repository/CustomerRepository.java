package com.d01.simplebank.repository;

import com.d01.simplebank.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    
    /**
     * Find customer by CID
     */
    Optional<Customer> findByCid(String cid);
    
    /**
     * Check if customer exists by CID
     */
    boolean existsByCid(String cid);
    
    /**
     * Check if customer exists by user ID
     */
    boolean existsByUserId(String userId);
    
    /**
     * Find customer by user ID
     */
    Optional<Customer> findByUserId(String userId);
} 