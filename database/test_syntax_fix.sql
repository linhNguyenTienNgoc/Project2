-- =====================================================
-- TEST_SYNTAX_FIX.SQL - KIỂM TRA SỬA LỖI SYNTAX
-- Mô tả: Test file để kiểm tra việc sửa lỗi syntax trong data.sql
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- =====================================================

-- Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management;
USE test_cafe_management;

-- Tạo bảng roles
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng users với foreign key
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Test 1: Thêm roles
INSERT INTO roles (role_name, description) VALUES
('admin', 'Quản trị viên hệ thống'),
('waiter', 'Nhân viên phục vụ'),
('barista', 'Nhân viên pha chế');

SELECT 'Test 1 PASSED: Thêm roles thành công' AS result;

-- Test 2: Thêm users (kiểm tra syntax)
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
-- Admin
('admin', '123456', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),

-- Waiters
('waiter1', '123456', 'Dương Tuấn Minh', 'waiter1@cafe.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),
('waiter2', '123456', 'Trần Thị Hương', 'waiter2@cafe.com', '0123456791', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),
('waiter3', '123456', 'Lê Văn Nam', 'waiter3@cafe.com', '0123456792', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),

-- Baristas
('barista1', '123456', 'Dương Đức Thành', 'barista1@cafe.com', '0123456793', 
 (SELECT role_id FROM roles WHERE role_name = 'barista')),
('barista2', '123456', 'Phạm Thị Mai', 'barista2@cafe.com', '0123456794', 
 (SELECT role_id FROM roles WHERE role_name = 'barista'));

SELECT 'Test 2 PASSED: Thêm users thành công' AS result;

-- Kiểm tra dữ liệu
SELECT 'Dữ liệu cuối cùng:' AS info;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_users FROM users;

-- Dọn dẹp
DROP DATABASE test_cafe_management;
SELECT 'Test hoàn thành thành công!' AS final_result; 