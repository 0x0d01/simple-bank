package com.d01.simplebank.repository;

import com.d01.simplebank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Check if account exists by CID
     * @param cid the 13-digit CID
     * @return true if account exists, false otherwise
     */
    boolean existsByCid(String cid);
} 