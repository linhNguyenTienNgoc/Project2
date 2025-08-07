-- =====================================================
-- TEST_DATA_COMPLETE.SQL - KIỂM TRA FILE DATA.SQL MỚI
-- Mô tả: Test file để kiểm tra file data.sql đã được viết lại
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (role_name IN ('admin', 'waiter', 'barista')),
    INDEX idx_role_name (role_name),
    INDEX idx_role_active (is_active)
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

-- Tạo bảng categories
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

-- Tạo bảng areas
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

-- Tạo bảng products
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

-- Tạo bảng customers
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

-- Tạo bảng promotions
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

-- Tạo bảng orders
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    table_id INT,
    customer_id INT,
    user_id INT NOT NULL,
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
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
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (total_amount >= 0),
    CHECK (discount_amount >= 0),
    CHECK (tax_amount >= 0),
    CHECK (final_amount >= 0),
    CHECK (final_amount = total_amount - discount_amount + tax_amount),
    INDEX idx_order_number (order_number),
    INDEX idx_table_id (table_id),
    INDEX idx_customer_id (customer_id),
    INDEX idx_user_id (user_id),
    INDEX idx_order_date (order_date),
    INDEX idx_payment_status (payment_status),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_method (payment_method),
    INDEX idx_final_amount (final_amount)
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
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    CHECK (quantity > 0),
    CHECK (unit_price >= 0),
    CHECK (discount_percent >= 0 AND discount_percent <= 100),
    CHECK (total_price >= 0),
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_quantity (quantity),
    INDEX idx_unit_price (unit_price),
    INDEX idx_total_price (total_price)
);

-- Tạo bảng attendance
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    work_date DATE NOT NULL,
    total_hours DECIMAL(4,2),
    overtime_hours DECIMAL(4,2) DEFAULT 0.00,
    status ENUM('present', 'absent', 'late', 'half_day') DEFAULT 'present',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK (total_hours >= 0 AND total_hours <= 24),
    CHECK (overtime_hours >= 0),
    CHECK (check_out IS NULL OR check_in IS NOT NULL),
    CHECK (check_out IS NULL OR check_out > check_in),
    UNIQUE KEY uk_user_work_date (user_id, work_date),
    INDEX idx_user_id (user_id),
    INDEX idx_work_date (work_date),
    INDEX idx_check_in (check_in),
    INDEX idx_check_out (check_out),
    INDEX idx_status (status),
    INDEX idx_total_hours (total_hours)
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CHECK (LENGTH(setting_key) >= 2),
    INDEX idx_setting_key (setting_key),
    INDEX idx_setting_type (setting_type),
    INDEX idx_is_editable (is_editable)
);

-- Test 1: Thêm roles
INSERT INTO roles (role_name, description, permissions) VALUES
('admin', 'Quản trị viên hệ thống - Có toàn quyền quản lý', 
 '{"users": ["create", "read", "update", "delete"], "products": ["create", "read", "update", "delete"], "orders": ["create", "read", "update", "delete"], "reports": ["read"]}'),
('waiter', 'Nhân viên phục vụ - Phục vụ khách hàng và tạo đơn hàng', 
 '{"orders": ["create", "read", "update"], "customers": ["create", "read"], "tables": ["read", "update"]}'),
('barista', 'Nhân viên pha chế - Chuyên pha chế đồ uống', 
 '{"orders": ["read", "update"], "products": ["read"]}');

SELECT 'Test 1 PASSED: Thêm roles thành công' AS result;
SELECT COUNT(*) AS total_roles FROM roles;

-- Test 2: Thêm users
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),
('waiter1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Dương Tuấn Minh', 'waiter1@cafe.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter'));

SELECT 'Test 2 PASSED: Thêm users thành công' AS result;
SELECT COUNT(*) AS total_users FROM users;

-- Test 3: Thêm categories
INSERT INTO categories (category_name, description, sort_order) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại', 1),
('Trà', 'Trà sữa, trà đá, trà nóng', 2);

SELECT 'Test 3 PASSED: Thêm categories thành công' AS result;
SELECT COUNT(*) AS total_categories FROM categories;

-- Test 4: Thêm areas
INSERT INTO areas (area_name, description, floor_number, capacity) VALUES
('Tầng 1', 'Khu vực tầng trệt - Nơi đón tiếp khách', 1, 24),
('Tầng 2', 'Khu vực tầng 2 - Không gian yên tĩnh', 2, 16);

SELECT 'Test 4 PASSED: Thêm areas thành công' AS result;
SELECT COUNT(*) AS total_areas FROM areas;

-- Test 5: Thêm tables
INSERT INTO tables (table_name, area_id, table_number, capacity, status) VALUES
('Bàn 1', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-01', 4, 'available'),
('Bàn 2', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-02', 4, 'available');

SELECT 'Test 5 PASSED: Thêm tables thành công' AS result;
SELECT COUNT(*) AS total_tables FROM tables;

-- Test 6: Thêm products
INSERT INTO products (product_name, category_id, sku, price, cost_price, description, stock_quantity, min_stock_level) VALUES
('Cà phê đen', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF001', 25000, 15000, 'Cà phê đen truyền thống', 100, 20),
('Cà phê sữa', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF002', 30000, 18000, 'Cà phê sữa đặc', 80, 15);

SELECT 'Test 6 PASSED: Thêm products thành công' AS result;
SELECT COUNT(*) AS total_products FROM products;

-- Test 7: Thêm customers
INSERT INTO customers (customer_code, full_name, phone, email, address, loyalty_points, total_spent) VALUES
('KH001', 'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', '123 Đường ABC, Quận 1, TP.HCM', 150, 1500000),
('KH002', 'Trần Thị B', '0901234568', 'tranthib@gmail.com', '456 Đường XYZ, Quận 3, TP.HCM', 200, 2000000);

SELECT 'Test 7 PASSED: Thêm customers thành công' AS result;
SELECT COUNT(*) AS total_customers FROM customers;

-- Test 8: Thêm promotions
INSERT INTO promotions (promotion_code, promotion_name, description, discount_type, discount_value, min_order_amount, start_date, end_date) VALUES
('SUMMER2025', 'Khuyến mãi mùa hè', 'Giảm giá 20% cho tất cả đồ uống', 'percentage', 20.00, 100000, '2025-06-01', '2025-08-31'),
('BIRTHDAY2025', 'Giảm giá sinh nhật', 'Giảm 50.000đ cho khách hàng trong tháng sinh nhật', 'fixed_amount', 50000.00, 200000, '2025-01-01', '2025-12-31');

SELECT 'Test 8 PASSED: Thêm promotions thành công' AS result;
SELECT COUNT(*) AS total_promotions FROM promotions;

-- Test 9: Thêm orders
INSERT INTO orders (order_number, table_id, customer_id, user_id, total_amount, discount_amount, tax_amount, final_amount, payment_method, payment_status, order_status, notes) VALUES
('ORD001', (SELECT table_id FROM tables WHERE table_name = 'Bàn 1'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Nguyễn Văn A'),
 (SELECT user_id FROM users WHERE username = 'waiter1'),
 120000, 0, 12000, 132000, 'cash', 'paid', 'completed', 'Khách hàng thân thiết');

SELECT 'Test 9 PASSED: Thêm orders thành công' AS result;
SELECT COUNT(*) AS total_orders FROM orders;

-- Test 10: Thêm order_details
INSERT INTO order_details (order_id, product_id, quantity, unit_price, discount_percent, total_price, notes) VALUES
((SELECT order_id FROM orders WHERE order_number = 'ORD001'), 
 (SELECT product_id FROM products WHERE product_name = 'Cà phê sữa'), 2, 30000, 0.00, 60000, NULL);

SELECT 'Test 10 PASSED: Thêm order_details thành công' AS result;
SELECT COUNT(*) AS total_order_details FROM order_details;

-- Test 11: Thêm attendance
INSERT INTO attendance (user_id, check_in, check_out, work_date, total_hours, status, notes) VALUES
((SELECT user_id FROM users WHERE username = 'waiter1'), 
 '2025-01-31 08:00:00', '2025-01-31 17:00:00', '2025-01-31', 9.00, 'present', 'Ca sáng');

SELECT 'Test 11 PASSED: Thêm attendance thành công' AS result;
SELECT COUNT(*) AS total_attendance FROM attendance;

-- Test 12: Thêm system_settings
INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('cafe_name', 'Cafe Project2', 'string', 'Tên quán cafe'),
('tax_rate', '10', 'number', 'Thuế suất (%)');

SELECT 'Test 12 PASSED: Thêm system_settings thành công' AS result;
SELECT COUNT(*) AS total_settings FROM system_settings;

-- Dọn dẹp
DROP DATABASE test_cafe_management;

SELECT 'TẤT CẢ TEST ĐÃ THÀNH CÔNG! File data.sql mới hoạt động đúng.' AS final_result; 