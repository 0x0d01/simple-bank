-- Setup test database
-- This script creates the test database and all required tables

-- Create test database if not exists
CREATE DATABASE IF NOT EXISTS simplebank_test;

-- Grant access to simplebank user for the test database
GRANT ALL PRIVILEGES ON simplebank_test.* TO 'simplebank'@'%';
FLUSH PRIVILEGES;

-- Use test database
USE simplebank_test;

-- Create users table for test database
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'USER',
    cid VARCHAR(13) UNIQUE,
    name_th VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci,
    name_en VARCHAR(255),
    pin VARCHAR(255),
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,
    INDEX idx_email (email),
    INDEX idx_cid (cid)
);

-- Create accounts table for test database
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cid VARCHAR(13) NOT NULL,
    name_th VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,
    INDEX idx_cid (cid)
);

-- Create transactions table for test database
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    account_no BIGINT NOT NULL,
    transaction_date DATETIME NOT NULL,
    amount INT NOT NULL,
    display_amount VARCHAR(20) NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(50) NOT NULL,
    remark TEXT,
    created_by VARCHAR(36) NOT NULL,
    metadata JSON,
    hash VARCHAR(64) NOT NULL,
    signature TEXT NOT NULL,
    created_date DATETIME NOT NULL,
    FOREIGN KEY (account_no) REFERENCES accounts(id),
    FOREIGN KEY (created_by) REFERENCES users(id),
    INDEX idx_account_no (account_no),
    INDEX idx_transaction_date (transaction_date),
    INDEX idx_type (type),
    INDEX idx_hash (hash)
);

-- Create balances table for test database
CREATE TABLE IF NOT EXISTS balances (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    account_no BIGINT NOT NULL UNIQUE,
    latest_transaction_id VARCHAR(36) NOT NULL,
    balance INT NOT NULL CHECK (balance >= 0),
    display_balance VARCHAR(20) NOT NULL,
    updated_date DATETIME NOT NULL,
    FOREIGN KEY (account_no) REFERENCES accounts(id),
    FOREIGN KEY (latest_transaction_id) REFERENCES transactions(id),
    INDEX idx_account_no (account_no)
);

-- Create test admin user
INSERT INTO users (id, email, password, role, created_date, updated_date) 
SELECT 
    UUID(), 
    'admin@example.com', 
    '$2a$12$/buim0EFl5h4WiJNjOuLyuK2JLl3FAvNi0KnXVWoWn7qs1JU/8tY.',
    'ADMIN', 
    NOW(), 
    NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM users WHERE email = 'admin@example.com'
);

SELECT 'Test database setup completed' as status; 