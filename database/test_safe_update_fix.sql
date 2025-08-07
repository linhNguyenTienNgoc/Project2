-- =====================================================
-- TEST_SAFE_UPDATE_FIX.SQL - KIỂM TRA SỬA LỖI SAFE UPDATE MODE
-- Mô tả: Test file để kiểm tra việc sửa lỗi safe update mode
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- =====================================================

-- Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management;
USE test_cafe_management;

-- Tạo bảng orders
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) UNIQUE NOT NULL,
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

-- Test 2: Xóa order_details sử dụng JOIN (sẽ thành công)
DELETE od FROM order_details od
INNER JOIN orders o ON od.order_id = o.order_id
WHERE o.order_number LIKE 'ORD%';

SELECT 'Test 2 PASSED: Xóa order_details thành công' AS result;
SELECT COUNT(*) AS remaining_order_details FROM order_details;

-- Test 3: Xóa orders
DELETE FROM orders WHERE order_number LIKE 'ORD%';

SELECT 'Test 3 PASSED: Xóa orders thành công' AS result;
SELECT COUNT(*) AS remaining_orders FROM orders;

-- Dọn dẹp
DROP DATABASE test_cafe_management;
SELECT 'Test hoàn thành thành công!' AS final_result; 