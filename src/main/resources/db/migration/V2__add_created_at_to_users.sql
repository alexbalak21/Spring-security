-- Add created_at column to users table
ALTER TABLE users 
ADD COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
