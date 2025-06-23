-- Create balances table
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