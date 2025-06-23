-- Main database initialization script
-- This script runs all migrations in the correct order

-- Set character set and collation
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;
SET collation_connection = 'utf8mb4_unicode_ci';

-- Create database if not exists (this is already done by MYSQL_DATABASE env var, but just in case)
CREATE DATABASE IF NOT EXISTS simplebank CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- Use the simplebank database
USE simplebank;

-- The following scripts will be executed automatically by MySQL in alphabetical order:
-- 01-create-users-table.sql
-- 02-create-accounts-table.sql  
-- 03-create-transactions-table.sql
-- 04-create-balances-table.sql
-- 05-create-admin-user.sql

-- Also run the test database setup
SOURCE /docker-entrypoint-initdb.d/06-setup-test-database.sql;

SELECT 'Database initialization completed successfully' as status; 