-- =====================================================
-- UPDATE ADMIN PASSWORD TO BCRYPT HASH
-- =====================================================

USE cafe_management;

-- Update admin password to BCrypt hash of '123456'
-- BCrypt hash for '123456' with cost 12
UPDATE users 
SET password = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iQmO' 
WHERE username = 'admin';

-- Update other users passwords to BCrypt hash
UPDATE users 
SET password = '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewdBPj4J/HS.iQmO' 
WHERE username IN ('manager', 'cashier1', 'waiter1', 'barista1');

-- Verify the update
SELECT username, role_id, 
       CASE 
           WHEN password LIKE '$2a$%' THEN 'BCrypt Hash'
           ELSE 'Plain Text'
       END as password_type
FROM users; 