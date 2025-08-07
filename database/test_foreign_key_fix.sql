-- =====================================================
-- TEST_FOREIGN_KEY_FIX.SQL - KIỂM TRA SỬA LỖI FOREIGN KEY
-- Mô tả: Test file để kiểm tra việc sửa lỗi foreign key constraint
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

-- Thêm dữ liệu test
INSERT INTO roles (role_name, description) VALUES
('admin', 'Quản trị viên hệ thống'),
('waiter', 'Nhân viên phục vụ'),
('barista', 'Nhân viên pha chế');

INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', '123456', 'Admin Test', 'admin@test.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),
('waiter1', '123456', 'Waiter Test', 'waiter@test.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter'));

-- Test 1: Xóa users trước (sẽ thành công)
DELETE FROM users WHERE username IN ('admin', 'waiter1');
SELECT 'Test 1 PASSED: Xóa users thành công' AS result;

-- Test 2: Xóa roles sau khi đã xóa users (sẽ thành công)
DELETE FROM roles WHERE role_name IN ('admin', 'waiter', 'barista');
SELECT 'Test 2 PASSED: Xóa roles thành công' AS result;

-- Test 3: Thêm lại roles
INSERT INTO roles (role_name, description) VALUES
('admin', 'Quản trị viên hệ thống'),
('waiter', 'Nhân viên phục vụ'),
('barista', 'Nhân viên pha chế');

-- Test 4: Thêm lại users
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', '123456', 'Admin Test', 'admin@test.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),
('waiter1', '123456', 'Waiter Test', 'waiter@test.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter'));

SELECT 'Test 4 PASSED: Thêm lại dữ liệu thành công' AS result;

-- Kiểm tra dữ liệu
SELECT 'Dữ liệu cuối cùng:' AS info;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_users FROM users;

-- Dọn dẹp
DROP DATABASE test_cafe_management;
SELECT 'Test hoàn thành thành công!' AS final_result; 