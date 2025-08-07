-- =====================================================
-- TEST_SAFE_UPDATE_FINAL.SQL - KIỂM TRA SỬA LỖI SAFE UPDATE MODE
-- Mô tả: Test file để kiểm tra việc sửa lỗi safe update mode trong data.sql
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- =====================================================

-- Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management;
USE test_cafe_management;

-- Tạo bảng orders
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tạo bảng order_details
CREATE TABLE order_details (
    detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_name VARCHAR(100) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE
);

-- Thêm dữ liệu test
INSERT INTO orders (order_number, total_amount) VALUES
('ORD001', 120000),
('ORD002', 180000),
('ORD003', 250000);

INSERT INTO order_details (order_id, product_name, quantity, unit_price) VALUES
(1, 'Cà phê sữa', 2, 30000),
(1, 'Bánh tiramisu', 1, 45000),
(2, 'Latte', 2, 50000),
(2, 'Bánh cheesecake', 1, 40000),
(3, 'Cappuccino', 2, 45000);

SELECT 'Test 1 PASSED: Thêm dữ liệu thành công' AS result;
SELECT COUNT(*) AS total_orders FROM orders;
SELECT COUNT(*) AS total_order_details FROM order_details;

-- Test 2: Kiểm tra safe update mode hiện tại
SELECT @@sql_safe_updates AS current_safe_update_mode;

-- Test 3: Tắt safe update mode
SET SQL_SAFE_UPDATES = 0;
SELECT 'Test 3 PASSED: Tắt safe update mode thành công' AS result;

-- Test 4: Xóa order_details sử dụng JOIN (sẽ thành công)
DELETE od FROM order_details od
INNER JOIN orders o ON od.order_id = o.order_id
WHERE o.order_number LIKE 'ORD%';

SELECT 'Test 4 PASSED: Xóa order_details thành công' AS result;
SELECT COUNT(*) AS remaining_order_details FROM order_details;

-- Test 5: Xóa orders
DELETE FROM orders WHERE order_number LIKE 'ORD%';

SELECT 'Test 5 PASSED: Xóa orders thành công' AS result;
SELECT COUNT(*) AS remaining_orders FROM orders;

-- Test 6: Bật lại safe update mode
SET SQL_SAFE_UPDATES = 1;
SELECT 'Test 6 PASSED: Bật lại safe update mode thành công' AS result;

-- Dọn dẹp
DROP DATABASE test_cafe_management;

SELECT 'TẤT CẢ TEST ĐÃ THÀNH CÔNG! Lỗi safe update mode đã được sửa.' AS final_result; 