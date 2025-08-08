-- =====================================================
-- UPDATE WAITER PASSWORD TO 123456
-- Cập nhật mật khẩu waiter thành 123456
-- =====================================================

USE cafe_management1;

-- Cập nhật mật khẩu waiter1 thành 123456 (BCrypt hash)
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE username = 'waiter1';

-- Cập nhật mật khẩu waiter2 thành 123456
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE username = 'waiter2';

-- Cập nhật mật khẩu waiter3 thành 123456
UPDATE users 
SET password = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa'
WHERE username = 'waiter3';

-- Kiểm tra kết quả
SELECT username, full_name, role_id FROM users WHERE username LIKE 'waiter%';

-- Thông báo thành công
SELECT 'Waiter passwords updated to 123456 successfully!' as message;
