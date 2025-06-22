package com.d01.simplebank.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "balances")
public class Balance {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_no", nullable = false, unique = true)
    private Account account;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "latest_transaction_id", nullable = false)
    private Transaction latestTransaction;
    
    @Min(value = 0, message = "Balance cannot be negative")
    @Column(name = "balance", nullable = false)
    private Integer balance; // stored as integer, e.g. 12345 means 123.45
    
    @Column(name = "display_balance", nullable = false)
    private String displayBalance; // always 2 decimal places, e.g. "123.45"
    
    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false)
    private LocalDateTime updatedDate;
    
    // Default constructor
    public Balance() {}
    
    // Constructor with required fields
    public Balance(Account account, Transaction latestTransaction, Integer balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.account = account;
        this.latestTransaction = latestTransaction;
        setBalance(balance);
        this.updatedDate = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Account getAccount() {
        return account;
    }
    
    public void setAccount(Account account) {
        this.account = account;
    }
    
    public Transaction getLatestTransaction() {
        return latestTransaction;
    }
    
    public void setLatestTransaction(Transaction latestTransaction) {
        this.latestTransaction = latestTransaction;
    }
    
    public Integer getBalance() {
        return balance;
    }
    
    public void setBalance(Integer balance) {
        if (balance < 0) {
            throw new IllegalArgumentException("Balance cannot be negative");
        }
        this.balance = balance;
        this.displayBalance = formatDisplayBalance(balance);
    }
    
    public String getDisplayBalance() {
        return displayBalance;
    }
    
    public void setDisplayBalance(String displayBalance) {
        this.displayBalance = displayBalance;
    }
    
    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }
    
    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }
    
    private String formatDisplayBalance(Integer balance) {
        if (balance == null) return null;
        int major = balance / 100;
        int minor = balance % 100;
        return String.format("%d.%02d", major, minor);
    }
} 