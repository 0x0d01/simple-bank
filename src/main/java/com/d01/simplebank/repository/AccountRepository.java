package com.d01.simplebank.repository;

import com.d01.simplebank.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    
    /**
     * Find all accounts by CID
     * @param cid the 13-digit CID
     * @return list of accounts with the given CID
     */
    List<Account> findByCid(String cid);
} 