-- Create transactions table
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