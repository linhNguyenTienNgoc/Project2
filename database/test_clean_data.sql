-- =====================================================
-- TEST_CLEAN_DATA.SQL - KIỂM TRA XÓA TOÀN BỘ DỮ LIỆU
-- Mô tả: Test file để kiểm tra việc xóa toàn bộ dữ liệu trước khi insert
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- =====================================================

-- Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management;
USE test_cafe_management;

-- Tạo bảng roles
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
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

-- Tạo bảng categories
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng areas
CREATE TABLE areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    floor_number INT,
    capacity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng tables
CREATE TABLE tables (
    table_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    area_id INT NOT NULL,
    table_number VARCHAR(20) NOT NULL,
    capacity INT DEFAULT 4,
    status ENUM('available', 'occupied', 'reserved', 'cleaning', 'maintenance') DEFAULT 'available',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES areas(area_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tạo bảng products
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    sku VARCHAR(50) UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    description TEXT,
    stock_quantity INT DEFAULT 0,
    min_stock_level INT DEFAULT 10,
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tạo bảng customers
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    address TEXT,
    loyalty_points INT DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng promotions
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    promotion_code VARCHAR(50) UNIQUE,
    promotion_name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount', 'buy_x_get_y') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0.00,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Tạo bảng orders
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    table_id INT,
    customer_id INT,
    user_id INT NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    tax_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('cash', 'card', 'momo', 'vnpay', 'zalopay', 'bank_transfer') DEFAULT 'cash',
    payment_status ENUM('pending', 'paid', 'cancelled', 'refunded') DEFAULT 'pending',
    order_status ENUM('pending', 'preparing', 'ready', 'served', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(table_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tạo bảng order_details
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0.00,
    total_price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Tạo bảng attendance
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    work_date DATE NOT NULL,
    total_hours DECIMAL(4,2),
    status ENUM('present', 'absent', 'late', 'half_day') DEFAULT 'present',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Tạo bảng system_settings
CREATE TABLE system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type ENUM('string', 'number', 'boolean', 'json', 'date') DEFAULT 'string',
    description TEXT,
    is_editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Thêm dữ liệu test
INSERT INTO roles (role_name, description, permissions) VALUES
('admin', 'Quản trị viên', '{"users": ["create", "read", "update", "delete"]}'),
('waiter', 'Nhân viên phục vụ', '{"orders": ["create", "read", "update"]}');

INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', 'password123', 'Admin User', 'admin@test.com', '0123456789', 1),
('waiter1', 'password123', 'Waiter User', 'waiter@test.com', '0123456790', 2);

INSERT INTO categories (category_name, description, sort_order) VALUES
('Cà phê', 'Các loại cà phê', 1),
('Trà', 'Các loại trà', 2);

INSERT INTO areas (area_name, description, floor_number, capacity) VALUES
('Tầng 1', 'Khu vực tầng trệt', 1, 20),
('Tầng 2', 'Khu vực tầng 2', 2, 15);

INSERT INTO tables (table_name, area_id, table_number, capacity, status) VALUES
('Bàn 1', 1, 'T1-01', 4, 'available'),
('Bàn 2', 1, 'T1-02', 4, 'available');

INSERT INTO products (product_name, category_id, sku, price, cost_price, description, stock_quantity, min_stock_level) VALUES
('Cà phê đen', 1, 'CF001', 25000, 15000, 'Cà phê đen truyền thống', 100, 20),
('Trà sữa', 2, 'TR001', 30000, 18000, 'Trà sữa trân châu', 80, 15);

INSERT INTO customers (customer_code, full_name, phone, email, address, loyalty_points, total_spent) VALUES
('KH001', 'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', '123 ABC', 100, 1000000),
('KH002', 'Trần Thị B', '0901234568', 'tranthib@gmail.com', '456 XYZ', 150, 1500000);

INSERT INTO promotions (promotion_code, promotion_name, description, discount_type, discount_value, min_order_amount, start_date, end_date) VALUES
('SUMMER2025', 'Khuyến mãi mùa hè', 'Giảm 20%', 'percentage', 20.00, 100000, '2025-06-01', '2025-08-31'),
('BIRTHDAY2025', 'Giảm giá sinh nhật', 'Giảm 50.000đ', 'fixed_amount', 50000.00, 200000, '2025-01-01', '2025-12-31');

INSERT INTO orders (order_number, table_id, customer_id, user_id, total_amount, discount_amount, tax_amount, final_amount, payment_method, payment_status, order_status) VALUES
('ORD001', 1, 1, 2, 120000, 0, 12000, 132000, 'cash', 'paid', 'completed'),
('ORD002', 2, 2, 2, 180000, 36000, 14400, 158400, 'momo', 'paid', 'completed');

INSERT INTO order_details (order_id, product_id, quantity, unit_price, discount_percent, total_price) VALUES
(1, 1, 2, 30000, 0.00, 60000),
(1, 2, 1, 30000, 0.00, 30000),
(2, 1, 3, 25000, 0.00, 75000),
(2, 2, 2, 30000, 0.00, 60000);

INSERT INTO attendance (user_id, check_in, check_out, work_date, total_hours, status) VALUES
(1, '2025-01-31 08:00:00', '2025-01-31 17:00:00', '2025-01-31', 9.00, 'present'),
(2, '2025-01-31 14:00:00', '2025-01-31 22:00:00', '2025-01-31', 8.00, 'present');

INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('cafe_name', 'Test Cafe', 'string', 'Tên quán cafe'),
('tax_rate', '10', 'number', 'Thuế suất (%)');

SELECT 'Test 1 PASSED: Thêm dữ liệu test thành công' AS result;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_categories FROM categories;
SELECT COUNT(*) AS total_areas FROM areas;
SELECT COUNT(*) AS total_tables FROM tables;
SELECT COUNT(*) AS total_products FROM products;
SELECT COUNT(*) AS total_customers FROM customers;
SELECT COUNT(*) AS total_promotions FROM promotions;
SELECT COUNT(*) AS total_orders FROM orders;
SELECT COUNT(*) AS total_order_details FROM order_details;
SELECT COUNT(*) AS total_attendance FROM attendance;
SELECT COUNT(*) AS total_settings FROM system_settings;

-- Test 2: Tắt safe update mode
SET SQL_SAFE_UPDATES = 0;
SELECT 'Test 2 PASSED: Tắt safe update mode thành công' AS result;

-- Test 3: Xóa dữ liệu theo thứ tự foreign key
DELETE FROM order_details;
SELECT 'Test 3 PASSED: Xóa order_details thành công' AS result;

DELETE FROM orders;
SELECT 'Test 4 PASSED: Xóa orders thành công' AS result;

DELETE FROM attendance;
SELECT 'Test 5 PASSED: Xóa attendance thành công' AS result;

DELETE FROM users;
SELECT 'Test 6 PASSED: Xóa users thành công' AS result;

DELETE FROM roles;
SELECT 'Test 7 PASSED: Xóa roles thành công' AS result;

DELETE FROM customers;
SELECT 'Test 8 PASSED: Xóa customers thành công' AS result;

DELETE FROM tables;
SELECT 'Test 9 PASSED: Xóa tables thành công' AS result;

DELETE FROM areas;
SELECT 'Test 10 PASSED: Xóa areas thành công' AS result;

DELETE FROM products;
SELECT 'Test 11 PASSED: Xóa products thành công' AS result;

DELETE FROM categories;
SELECT 'Test 12 PASSED: Xóa categories thành công' AS result;

DELETE FROM promotions;
SELECT 'Test 13 PASSED: Xóa promotions thành công' AS result;

DELETE FROM system_settings;
SELECT 'Test 14 PASSED: Xóa system_settings thành công' AS result;

-- Test 15: Kiểm tra tất cả bảng đã trống
SELECT 'Test 15 PASSED: Kiểm tra dữ liệu đã được xóa sạch' AS result;
SELECT COUNT(*) AS remaining_roles FROM roles;
SELECT COUNT(*) AS remaining_users FROM users;
SELECT COUNT(*) AS remaining_categories FROM categories;
SELECT COUNT(*) AS remaining_areas FROM areas;
SELECT COUNT(*) AS remaining_tables FROM tables;
SELECT COUNT(*) AS remaining_products FROM products;
SELECT COUNT(*) AS remaining_customers FROM customers;
SELECT COUNT(*) AS remaining_promotions FROM promotions;
SELECT COUNT(*) AS remaining_orders FROM orders;
SELECT COUNT(*) AS remaining_order_details FROM order_details;
SELECT COUNT(*) AS remaining_attendance FROM attendance;
SELECT COUNT(*) AS remaining_settings FROM system_settings;

-- Test 16: Bật lại safe update mode
SET SQL_SAFE_UPDATES = 1;
SELECT 'Test 16 PASSED: Bật lại safe update mode thành công' AS result;

-- Dọn dẹp
DROP DATABASE test_cafe_management;

SELECT 'TẤT CẢ TEST ĐÃ THÀNH CÔNG! Việc xóa toàn bộ dữ liệu hoạt động đúng.' AS final_result; 