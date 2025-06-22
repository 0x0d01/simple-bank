package com.d01.simplebank.repository;

import com.d01.simplebank.entity.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
    
    /**
     * Find balance by account ID
     * @param accountId the account ID
     * @return optional balance for the account
     */
    Optional<Balance> findByAccountId(Long accountId);
    
    /**
     * Check if balance exists for account
     * @param accountId the account ID
     * @return true if balance exists, false otherwise
     */
    boolean existsByAccountId(Long accountId);
} 