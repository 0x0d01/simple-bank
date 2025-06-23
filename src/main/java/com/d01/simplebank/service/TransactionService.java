package com.d01.simplebank.service;

import com.d01.simplebank.entity.Account;
import com.d01.simplebank.entity.Transaction;
import com.d01.simplebank.entity.User;
import com.d01.simplebank.exception.AccountNotFoundException;
import com.d01.simplebank.exception.DuplicateTransactionException;
import com.d01.simplebank.exception.InsufficientFundsException;
import com.d01.simplebank.repository.AccountRepository;
import com.d01.simplebank.repository.TransactionRepository;
import com.d01.simplebank.util.TransactionHashUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        // Set a random UUID as the transaction ID if not already set
        if (transaction.getId() == null) {
            transaction.setId(UUID.randomUUID().toString());
        }
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
    
    /**
     * Process a deposit transaction (ADMIN only)
     * @param id the transaction ID to check for duplicates
     * @param accountNo the account number (7 digits)
     * @param amount the deposit amount (must be >= 1)
     * @param createdBy the user creating the transaction
     * @return the created transaction
     */
    @Transactional
    public Transaction processDeposit(String id, String accountNo, Integer amount, User createdBy) {
        // Check if transaction with this ID already exists
        if (transactionRepository.findById(id).isPresent()) {
            throw new DuplicateTransactionException("Transaction with ID " + id + " already exists. Transaction already processed.");
        }
        
        // Find the account by account number
        Long accountId = Long.parseLong(accountNo);
        accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNo));
        
        LocalDateTime now = LocalDateTime.now();
        
        // Create the deposit transaction
        String remark = "Deposit " + createdBy.getNameEn();
        
        // Create transaction with the provided ID
        Transaction transaction = createTransactionWithId(accountId, now, amount, "A0", "OTC", remark, createdBy, null, id);
        
        return transaction;
    }
    
    /**
     * Create a new transaction with a specific ID
     * @param accountId the account ID
     * @param transactionDate the transaction date
     * @param amount the transaction amount (in cents)
     * @param type the transaction type
     * @param channel the transaction channel
     * @param remark the transaction remark
     * @param createdBy the user creating the transaction
     * @param metadata the transaction metadata
     * @param id the transaction ID
     * @return the created transaction
     */
    @Transactional
    public Transaction createTransactionWithId(Long accountId, LocalDateTime transactionDate, Integer amount,
                                             String type, String channel, String remark, User createdBy, String metadata, String id) {
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
        
        // Create and save the transaction with the provided ID
        Transaction transaction = new Transaction(account, transactionDate, amount, type, channel, 
                                                remark, createdBy, metadata, hash, signature);
        transaction.setId(id);
        
        return transactionRepository.save(transaction);
    }
    
    /**
     * Process a transfer transaction (USER only)
     * @param senderAccountNo the sender account number (7 digits)
     * @param receiverAccountNo the receiver account number (7 digits)
     * @param amount the transfer amount (must be >= 0)
     * @param createdBy the user creating the transaction
     * @return list of created transactions (sender debit, receiver credit)
     */
    @Transactional
    public List<Transaction> processTransfer(String senderAccountNo, String receiverAccountNo, Integer amount, User createdBy) {
        // Find the sender account
        Long senderAccountId = Long.parseLong(senderAccountNo);
        Account senderAccount = accountRepository.findById(senderAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Sender account not found with account number: " + senderAccountNo));
        
        // Find the receiver account
        Long receiverAccountId = Long.parseLong(receiverAccountNo);
        Account receiverAccount = accountRepository.findById(receiverAccountId)
                .orElseThrow(() -> new AccountNotFoundException("Receiver account not found with account number: " + receiverAccountNo));
        
        // Calculate sender's current balance
        Integer senderBalance = calculateAccountBalance(senderAccountId);
        
        // Check if sender has sufficient funds
        if (senderBalance < amount) {
            throw new InsufficientFundsException("Insufficient funds. Current balance: " + senderBalance + ", Required: " + amount);
        }
        
        // Check for duplicate transaction (same sender, receiver, amount, and recent timestamp)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minusMinutes(5);
        
        List<Transaction> recentSenderTransactions = transactionRepository.findByAccountIdAndTransactionDateBetweenOrderByTransactionDateAsc(
                senderAccountId, fiveMinutesAgo, now);
        
        boolean duplicateExists = recentSenderTransactions.stream()
                .anyMatch(t -> t.getAmount().equals(-amount) && 
                              "A1".equals(t.getType()) && 
                              "ATS".equals(t.getChannel()) &&
                              createdBy.getId().equals(t.getCreatedBy().getId()) &&
                              t.getRemark().contains("Transfer to x" + receiverAccountNo.substring(3)));
        
        if (duplicateExists) {
            throw new DuplicateTransactionException("Duplicate transfer transaction detected. Transaction already processed.");
        }
        
        // Create sender debit transaction
        String senderRemark = "Transfer to x" + receiverAccountNo.substring(3) + " " + receiverAccount.getNameEn();
        Transaction senderTransaction = createTransaction(senderAccountId, now, -amount, "A1", "ATS", senderRemark, createdBy, null);
        
        // Create receiver credit transaction
        String receiverRemark = "Receive from x" + senderAccountNo.substring(3) + " " + senderAccount.getNameEn();
        Transaction receiverTransaction = createTransaction(receiverAccountId, now, amount, "A3", "ATS", receiverRemark, createdBy, null);
        
        return List.of(senderTransaction, receiverTransaction);
    }
    
    /**
     * Calculate the current balance of an account by summing all transactions
     * @param accountId the account ID
     * @return the current balance
     */
    public Integer calculateAccountBalance(Long accountId) {
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .mapToInt(Transaction::getAmount)
                .sum();
    }
} 