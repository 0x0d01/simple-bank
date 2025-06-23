-- Admin user initialization script
-- This script creates an admin user if it doesn't already exist

-- Create admin user if not exists
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

-- Verify the admin user was created
SELECT 'Admin user initialization completed' as status;
SELECT id, email, role, created_date FROM users WHERE email = 'admin@example.com'; 