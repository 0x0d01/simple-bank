-- Create an admin user for testing
-- Password: admin123 (BCrypt hashed)
INSERT INTO users (id, email, password, role, created_date, updated_date) 
VALUES (
    UUID(), 
    'admin@example.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
    'ADMIN', 
    NOW(), 
    NOW()
);

-- Verify the user was created
SELECT id, email, role, created_date FROM users WHERE email = 'admin@example.com'; 