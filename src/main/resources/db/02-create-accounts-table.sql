-- Create accounts table
CREATE TABLE IF NOT EXISTS accounts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cid VARCHAR(13) NOT NULL,
    name_th VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
    name_en VARCHAR(255) NOT NULL,
    created_date DATETIME NOT NULL,
    updated_date DATETIME NOT NULL,
    INDEX idx_cid (cid)
); 