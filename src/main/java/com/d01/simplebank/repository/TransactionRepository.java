package com.d01.simplebank.repository;

import com.d01.simplebank.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    
    /**
     * Find all transactions by account
     * @param accountId the account ID
     * @return list of transactions for the account
     */
    List<Transaction> findByAccountId(Long accountId);
    
    /**
     * Find all transactions by account ordered by transaction date descending
     * @param accountId the account ID
     * @return list of transactions for the account ordered by date descending
     */
    List<Transaction> findByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    /**
     * Find the latest transaction for an account
     * @param accountId the account ID
     * @return Optional containing the latest transaction, or empty if no transactions exist
     */
    Optional<Transaction> findFirstByAccountIdOrderByTransactionDateDesc(Long accountId);
    
    /**
     * Find all transactions by created user
     * @param userId the user ID
     * @return list of transactions created by the user
     */
    List<Transaction> findByCreatedById(String userId);
    
    /**
     * Find transactions by account and date range
     * @param accountId the account ID
     * @param startDate the start date
     * @param endDate the end date
     * @return list of transactions in the date range
     */
    List<Transaction> findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(
            Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find transactions by account and month/year, ordered by transaction date ascending
     * @param accountId the account ID
     * @param month the month (1-12)
     * @param year the year
     * @return list of transactions for the specified month/year ordered by date ascending
     */
    @Query("SELECT t FROM Transaction t WHERE t.account.id = :accountId " +
           "AND MONTH(t.transactionDate) = :month " +
           "AND YEAR(t.transactionDate) = :year " +
           "ORDER BY t.transactionDate ASC")
    List<Transaction> findByAccountIdAndMonthAndYearOrderByTransactionDateAsc(
            @Param("accountId") Long accountId, 
            @Param("month") int month, 
            @Param("year") int year);
    
    /**
     * Find transactions by type
     * @param type the transaction type
     * @return list of transactions with the specified type
     */
    List<Transaction> findByType(String type);
    
    /**
     * Find transactions by channel
     * @param channel the transaction channel
     * @return list of transactions with the specified channel
     */
    List<Transaction> findByChannel(String channel);
} 