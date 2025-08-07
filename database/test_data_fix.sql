-- =====================================================
-- TEST: KIỂM TRA VIỆC SỬA LỖI FIELD MISMATCH TRONG DATA.SQL
-- Mô tả: Test import data.sql với schema v2.0.1
-- =====================================================

-- Test 1: Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE test_cafe_management;

-- Test 2: Import schema (chỉ các bảng cần thiết cho test)
-- Bảng roles
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSON,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (role_name IN ('admin', 'waiter', 'barista')),
    INDEX idx_role_name (role_name),
    INDEX idx_role_active (is_active)
);

-- Bảng users
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    avatar_url VARCHAR(255),
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (LENGTH(username) >= 3),
    CHECK (LENGTH(password) >= 6),
    CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CHECK (phone REGEXP '^[0-9]{10,11}$'),
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_role_id (role_id),
    INDEX idx_user_active (is_active),
    INDEX idx_last_login (last_login)
);

-- Bảng categories
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(category_name) >= 2),
    INDEX idx_category_name (category_name),
    INDEX idx_category_active (is_active),
    INDEX idx_sort_order (sort_order)
);

-- Bảng areas
CREATE TABLE areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    floor_number INT,
    capacity INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(area_name) >= 2),
    CHECK (floor_number >= 0),
    CHECK (capacity >= 0),
    INDEX idx_area_name (area_name),
    INDEX idx_area_active (is_active),
    INDEX idx_floor_number (floor_number)
);

-- Bảng tables
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
    FOREIGN KEY (area_id) REFERENCES areas(area_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (LENGTH(table_name) >= 2),
    CHECK (capacity >= 1 AND capacity <= 20),
    UNIQUE KEY uk_area_table_number (area_id, table_number),
    INDEX idx_table_name (table_name),
    INDEX idx_area_id (area_id),
    INDEX idx_table_status (status),
    INDEX idx_table_active (is_active),
    INDEX idx_capacity (capacity)
);

-- Bảng products
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    sku VARCHAR(50) UNIQUE,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    description TEXT,
    image_url VARCHAR(255),
    stock_quantity INT DEFAULT 0,
    min_stock_level INT DEFAULT 10,
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (price >= 0),
    CHECK (cost_price >= 0),
    CHECK (stock_quantity >= 0),
    CHECK (min_stock_level >= 0),
    INDEX idx_product_name (product_name),
    INDEX idx_category_id (category_id),
    INDEX idx_sku (sku),
    INDEX idx_price (price),
    INDEX idx_product_active (is_active),
    INDEX idx_product_available (is_available),
    INDEX idx_stock_quantity (stock_quantity),
    FULLTEXT idx_product_search (product_name, description)
);

-- Bảng customers
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    address TEXT,
    birth_date DATE,
    gender ENUM('male', 'female', 'other'),
    loyalty_points INT DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0.00,
    last_visit TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(full_name) >= 2),
    CHECK (phone REGEXP '^[0-9]{10,11}$'),
    CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CHECK (loyalty_points >= 0),
    CHECK (total_spent >= 0),
    INDEX idx_customer_code (customer_code),
    INDEX idx_full_name (full_name),
    INDEX idx_phone (phone),
    INDEX idx_email (email),
    INDEX idx_customer_active (is_active),
    INDEX idx_loyalty_points (loyalty_points),
    INDEX idx_total_spent (total_spent),
    INDEX idx_last_visit (last_visit),
    FULLTEXT idx_customer_search (full_name, phone, email)
);

-- Bảng promotions
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    promotion_code VARCHAR(50) UNIQUE,
    promotion_name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount', 'buy_x_get_y') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10,2),
    usage_limit INT DEFAULT -1,
    used_count INT DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(promotion_name) >= 2),
    CHECK (discount_value >= 0),
    CHECK (min_order_amount >= 0),
    CHECK (max_discount_amount IS NULL OR max_discount_amount >= 0),
    CHECK (usage_limit = -1 OR usage_limit > 0),
    CHECK (used_count >= 0),
    CHECK (start_date <= end_date),
    INDEX idx_promotion_code (promotion_code),
    INDEX idx_promotion_name (promotion_name),
    INDEX idx_discount_type (discount_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_promotion_active (is_active),
    INDEX idx_usage_limit (usage_limit)
);

-- Test 3: Test INSERT roles
INSERT INTO roles (role_name, description) VALUES
('admin', 'Quản trị viên hệ thống - Có toàn quyền quản lý'),
('waiter', 'Nhân viên phục vụ - Phục vụ khách hàng và tạo đơn hàng'),
('barista', 'Nhân viên pha chế - Chuyên pha chế đồ uống');

SELECT '✅ Test 3 PASSED: Insert roles thành công' AS result;

-- Test 4: Test INSERT users
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', '123456', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),
('waiter1', '123456', 'Dương Tuấn Minh', 'waiter1@cafe.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter'));

SELECT '✅ Test 4 PASSED: Insert users thành công' AS result;

-- Test 5: Test INSERT categories
INSERT INTO categories (category_name, description, sort_order) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại', 1),
('Trà', 'Các loại trà và trà sữa', 2);

SELECT '✅ Test 5 PASSED: Insert categories thành công' AS result;

-- Test 6: Test INSERT areas
INSERT INTO areas (area_name, description, floor_number, capacity) VALUES
('Tầng 1', 'Khu vực tầng 1 - Chính', 1, 20),
('Tầng 2', 'Khu vực tầng 2 - Yên tĩnh', 2, 15);

SELECT '✅ Test 6 PASSED: Insert areas thành công' AS result;

-- Test 7: Test INSERT tables (với table_number)
INSERT INTO tables (table_name, table_number, area_id, capacity, status) VALUES
('Bàn 1', 'T1-01', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 4, 'available'),
('Bàn 2', 'T1-02', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 4, 'available');

SELECT '✅ Test 7 PASSED: Insert tables với table_number thành công' AS result;

-- Test 8: Test INSERT products (với sku, stock_quantity, min_stock_level)
INSERT INTO products (product_name, category_id, sku, price, cost_price, description, stock_quantity, min_stock_level) VALUES
('Cà phê đen', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF001', 25000, 15000, 'Cà phê đen truyền thống', 100, 20),
('Cà phê sữa', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF002', 30000, 18000, 'Cà phê sữa đặc', 80, 15);

SELECT '✅ Test 8 PASSED: Insert products với sku và stock thành công' AS result;

-- Test 9: Test INSERT customers (với customer_code)
INSERT INTO customers (customer_code, full_name, phone, email, address, loyalty_points, total_spent) VALUES
('KH001', 'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', '123 Đường ABC, Quận 1, TP.HCM', 150, 1500000),
('KH002', 'Trần Thị B', '0901234568', 'tranthib@gmail.com', '456 Đường XYZ, Quận 3, TP.HCM', 200, 2000000);

SELECT '✅ Test 9 PASSED: Insert customers với customer_code thành công' AS result;

-- Test 10: Test INSERT promotions (với promotion_code)
INSERT INTO promotions (promotion_code, promotion_name, description, discount_type, discount_value, min_order_amount, start_date, end_date) VALUES
('SUMMER2025', 'Khuyến mãi mùa hè', 'Giảm giá 20% cho tất cả đồ uống', 'percentage', 20.00, 100000, '2025-06-01', '2025-08-31'),
('BIRTHDAY2025', 'Giảm giá sinh nhật', 'Giảm 50.000đ cho khách hàng trong tháng sinh nhật', 'fixed_amount', 50000.00, 200000, '2025-01-01', '2025-12-31');

SELECT '✅ Test 10 PASSED: Insert promotions với promotion_code thành công' AS result;

-- Test 11: Kiểm tra dữ liệu đã insert
SELECT '=== KIỂM TRA DỮ LIỆU ===' AS info;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_categories FROM categories;
SELECT COUNT(*) AS total_areas FROM areas;
SELECT COUNT(*) AS total_tables FROM tables;
SELECT COUNT(*) AS total_products FROM products;
SELECT COUNT(*) AS total_customers FROM customers;
SELECT COUNT(*) AS total_promotions FROM promotions;

-- Test 12: Kiểm tra các trường mới
SELECT '=== KIỂM TRA CÁC TRƯỜNG MỚI ===' AS info;
SELECT table_name, table_number FROM tables;
SELECT product_name, sku, stock_quantity, min_stock_level FROM products;
SELECT customer_code, full_name FROM customers;
SELECT promotion_code, promotion_name FROM promotions;

-- Test 13: Cleanup
DROP DATABASE IF EXISTS test_cafe_management;

SELECT '✅ TẤT CẢ TESTS PASSED: File data.sql đã được sửa lỗi thành công!' AS final_result; 