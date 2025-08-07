-- =====================================================
-- TEST_TRIGGER_FIX.SQL - KIỂM TRA SỬA LỖI TRIGGER
-- Mô tả: Test việc sửa lỗi Error Code: 1442 trong data.sql
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- =====================================================

-- Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management;
USE test_cafe_management;

-- Tạo bảng products (đơn giản)
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(100) NOT NULL,
    stock_quantity INT DEFAULT 0
);

-- Tạo bảng orders (đơn giản)
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(50) NOT NULL
);

-- Tạo bảng order_details với trigger
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT,
    product_id INT,
    quantity INT,
    unit_price DECIMAL(10,2),
    total_price DECIMAL(10,2),
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- Tạo trigger gây lỗi (tương tự như trong cafe_management.sql)
DELIMITER //
CREATE TRIGGER tr_order_details_insert
AFTER INSERT ON order_details
FOR EACH ROW
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
END//
DELIMITER ;

-- Thêm dữ liệu test
INSERT INTO products (product_name, stock_quantity) VALUES
('Cà phê sữa', 100),
('Bánh tiramisu', 50);

INSERT INTO orders (order_number) VALUES
('ORD001'),
('ORD002');

-- Test 1: Insert không vô hiệu hóa trigger (sẽ gây lỗi)
-- INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price) VALUES
-- (1, 1, 2, 30000, 60000);

-- Test 2: Insert với vô hiệu hóa trigger (sẽ thành công)
-- Drop trigger trước khi insert
DROP TRIGGER IF EXISTS tr_order_details_insert;

INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price) VALUES
(1, 1, 2, 30000, 60000),
(1, 2, 1, 45000, 45000),
(2, 1, 1, 30000, 30000);

-- Tạo lại trigger sau khi insert xong
DELIMITER //
CREATE TRIGGER tr_order_details_insert
AFTER INSERT ON order_details
FOR EACH ROW
BEGIN
    UPDATE products
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
END//
DELIMITER ;

-- Kiểm tra kết quả
SELECT 'Test thành công! Trigger đã được vô hiệu hóa và kích hoạt lại đúng cách.' AS message;
SELECT * FROM order_details;
SELECT * FROM products;

-- Dọn dẹp
DROP DATABASE test_cafe_management; 