-- Create users table
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