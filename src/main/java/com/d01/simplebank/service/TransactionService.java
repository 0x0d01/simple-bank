package com.d01.simplebank.service;

import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.util.TransactionHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Autowired
    private AccountRepository accountRepository;
    
    @Autowired
    private TransactionHashUtil transactionHashUtil;
    
    /**
     * Create a new transaction with hash and signature computation
     * @param accountId the account ID
     * @param transactionDate the transaction date
     * @param amount the transaction amount (in cents)
     * @param type the transaction type
     * @param channel the transaction channel
     * @param remark the transaction remark
     * @param createdBy the user creating the transaction
     * @param metadata the transaction metadata
     * @return the created transaction
     */
    @Transactional
    public Transaction createTransaction(Long accountId, LocalDateTime transactionDate, Integer amount,
                                       String type, String channel, String remark, User createdBy, String metadata) {
        // Find the account
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with id: " + accountId));
        
        // Find the latest transaction for this account to get the previous hash
        Optional<Transaction> latestTransactionOpt = transactionRepository
                .findFirstByAccountIdOrderByTransactionDateDesc(accountId);
        
        String previousHash = null;
        if (latestTransactionOpt.isPresent()) {
            previousHash = latestTransactionOpt.get().getHash();
        }
        
        // Convert transaction date to UTC timestamp in milliseconds
        long dateUtcTimestamp = transactionDate.toInstant(ZoneOffset.UTC).toEpochMilli();
        
        // Format account number as 7-digit zero-padded string
        String accountNo = String.format("%07d", account.getId());
        
        // Compute the transaction hash
        String hash = transactionHashUtil.computeTransactionHash(
                previousHash, accountNo, type, channel, dateUtcTimestamp, amount);
        
        // Sign the hash
        String signature = transactionHashUtil.signHash(hash);
        
        // Create and save the transaction
        Transaction transaction = new Transaction(account, transactionDate, amount, type, channel, 
                                                remark, createdBy, metadata, hash, signature);
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Create a new transaction with required fields only
     * @param accountId the account ID
     * @param transactionDate the transaction date
     * @param amount the transaction amount (in cents)
     * @param type the transaction type
     * @param channel the transaction channel
     * @param createdBy the user creating the transaction
     * @return the created transaction
     */
    @Transactional
    public Transaction createTransaction(Long accountId, LocalDateTime transactionDate, Integer amount,
                                       String type, String channel, User createdBy) {
        return createTransaction(accountId, transactionDate, amount, type, channel, null, createdBy, null);
    }
} 