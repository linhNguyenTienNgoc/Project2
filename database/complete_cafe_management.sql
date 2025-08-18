-- =====================================================
-- COMPLETE CAFE MANAGEMENT DATABASE
-- File: database/complete_cafe_management.sql
-- Purpose: Hệ thống quản lý quán café hoàn chỉnh với sample data
-- Author: Team 2_C2406L
-- Version: 2.0.0 (Enhanced with Promotions & QR Payments)
-- =====================================================

-- Create and use database
CREATE DATABASE IF NOT EXISTS cafe_management;
USE cafe_management;

-- =====================================================
-- 1. DROP EXISTING TABLES (if any) - IN REVERSE ORDER
-- =====================================================

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS order_promotions;
DROP TABLE IF EXISTS promotions;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS system_settings;
DROP TABLE IF EXISTS order_details;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS customers;
DROP TABLE IF EXISTS tables;
DROP TABLE IF EXISTS areas;
DROP TABLE IF EXISTS products;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS users;

SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- 2. CREATE TABLES - CORE SYSTEM
-- =====================================================

-- Users table
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role VARCHAR(50) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active)
);

-- Categories table
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    display_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_category_active (is_active),
    INDEX idx_category_order (display_order)
);

-- Products table
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    cost_price DECIMAL(10,2),
    description TEXT,
    image_url VARCHAR(255),
    is_available BOOLEAN DEFAULT TRUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (category_id) REFERENCES categories(category_id),
    INDEX idx_product_category (category_id),
    INDEX idx_product_active (is_active),
    INDEX idx_product_available (is_available),
    INDEX idx_product_price (price)
);

-- Areas table
CREATE TABLE areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_area_active (is_active)
);

-- Tables table
CREATE TABLE tables (
    table_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    area_id INT NOT NULL,
    capacity INT DEFAULT 4,
    status ENUM('available', 'occupied', 'reserved', 'cleaning') DEFAULT 'available',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (area_id) REFERENCES areas(area_id) ON DELETE CASCADE,
    INDEX idx_table_area (area_id),
    INDEX idx_table_status (status),
    INDEX idx_table_active (is_active)
);

-- Customers table
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20) UNIQUE,
    email VARCHAR(100) UNIQUE,
    address TEXT,
    loyalty_points INT DEFAULT 0,
    total_spent DECIMAL(12,2) DEFAULT 0.00,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_customer_phone (phone),
    INDEX idx_customer_email (email),
    INDEX idx_customer_points (loyalty_points)
);

-- =====================================================
-- 3. CREATE TABLES - ORDERS SYSTEM
-- =====================================================

-- Orders table
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    table_id INT,
    customer_id INT,
    user_id INT NOT NULL, -- Staff who created the order
    order_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_amount DECIMAL(10,2) NOT NULL,
    discount_amount DECIMAL(10,2) DEFAULT 0.00,
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('CASH', 'CARD', 'MOMO', 'VNPAY', 'ZALOPAY', 'BANK_TRANSFER') DEFAULT 'CASH',
    payment_status ENUM('pending', 'paid', 'cancelled') DEFAULT 'pending',
    order_status ENUM('pending', 'confirmed', 'preparing', 'ready', 'served', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (table_id) REFERENCES tables(table_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    
    INDEX idx_order_date (order_date),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_order_table (table_id),
    INDEX idx_order_user (user_id),
    INDEX idx_order_number (order_number)
);

-- Order details table
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id),
    
    INDEX idx_orderdetail_order (order_id),
    INDEX idx_orderdetail_product (product_id)
);

-- =====================================================
-- 4. CREATE TABLES - PROMOTIONS SYSTEM
-- =====================================================

-- Promotions table
CREATE TABLE promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2) DEFAULT 0,
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    usage_limit INT DEFAULT 0, -- 0 means unlimited
    usage_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_promotion_active (is_active),
    INDEX idx_promotion_dates (start_date, end_date),
    INDEX idx_promotion_min_amount (min_order_amount)
);

-- Order promotions table (tracking usage)
CREATE TABLE order_promotions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    promotion_id INT NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE RESTRICT,
    
    INDEX idx_order_promotion (order_id),
    INDEX idx_promotion_usage (promotion_id),
    INDEX idx_applied_date (applied_at),
    
    UNIQUE KEY unique_order_promotion (order_id, promotion_id)
);

-- =====================================================
-- 5. CREATE TABLES - ADDITIONAL SYSTEMS
-- =====================================================

-- Attendance table
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    work_date DATE NOT NULL,
    total_hours DECIMAL(4,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (user_id) REFERENCES users(user_id),
    INDEX idx_attendance_user_date (user_id, work_date),
    INDEX idx_attendance_date (work_date)
);

-- System settings table
CREATE TABLE system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_setting_key (setting_key)
);

-- =====================================================
-- 6. CREATE VIEWS FOR ANALYTICS
-- =====================================================

-- Promotion statistics view
CREATE OR REPLACE VIEW promotion_stats AS
SELECT 
    p.promotion_id,
    p.promotion_name,
    p.discount_type,
    p.discount_value,
    p.min_order_amount,
    p.usage_limit,
    p.usage_count,
    COALESCE(COUNT(op.id), 0) AS actual_usage_count,
    COALESCE(SUM(op.discount_amount), 0) AS total_discount_given,
    COALESCE(AVG(op.discount_amount), 0) AS avg_discount_per_use,
    p.is_active,
    p.start_date,
    p.end_date,
    CASE 
        WHEN p.end_date IS NOT NULL AND p.end_date < NOW() THEN 'EXPIRED'
        WHEN p.start_date IS NOT NULL AND p.start_date > NOW() THEN 'UPCOMING'
        WHEN p.is_active = TRUE THEN 'ACTIVE'
        ELSE 'INACTIVE'
    END AS status
FROM promotions p
LEFT JOIN order_promotions op ON p.promotion_id = op.promotion_id
GROUP BY p.promotion_id
ORDER BY p.created_at DESC;

-- =====================================================
-- 7. INSERT SAMPLE DATA - USERS & CATEGORIES
-- =====================================================

-- Insert users
INSERT INTO users (username, password, full_name, email, phone, role) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 'Admin'),
('manager', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Trần Xuân Quang Minh', 'manager@cafe.com', '0123456790', 'Manager'),
('cashier1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Vũ Hoàng Nam', 'cashier@cafe.com', '0123456791', 'Cashier'),
('waiter1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Dương Tuấn Minh', 'waiter@cafe.com', '0123456792', 'Waiter'),
('barista1', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Dương Đức Thành', 'barista@cafe.com', '0123456793', 'Barista'),
('waiter2', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Nguyễn Thị Hoa', 'waiter2@cafe.com', '0123456794', 'Waiter'),
('cashier2', '$2a$10$N9qo8uLOickgx2ZMRZoMye1J4H.8gYy.6Y5cF5QO5zH4FH8HhqKny', 'Lê Văn Đức', 'cashier2@cafe.com', '0123456795', 'Cashier');

-- Insert categories with display order
INSERT INTO categories (category_name, description, display_order) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại', 1),
('Trà & Trà sữa', 'Trà sữa, trà đá, trà nóng các loại', 2),
('Nước ép & Sinh tố', 'Nước ép trái cây tươi và sinh tố', 3),
('Bánh ngọt', 'Bánh ngọt, bánh kem, dessert', 4),
('Đồ ăn nhẹ', 'Snack, đồ ăn vặt', 5),
('Món chính', 'Cơm, mì, phở các loại', 6),
('Đồ uống đá xay', 'Smoothies, frappuccino', 7);

-- =====================================================
-- 8. INSERT SAMPLE DATA - PRODUCTS (EXTENSIVE)
-- =====================================================

-- Cà phê (category_id = 1)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Cà phê đen', 1, 25000, 15000, 'Cà phê đen truyền thống Việt Nam', TRUE),
('Cà phê sữa', 1, 30000, 18000, 'Cà phê sữa đặc ngọt ngào', TRUE),
('Cà phê sữa đá', 1, 32000, 19000, 'Cà phê sữa đá mát lạnh', TRUE),
('Espresso', 1, 35000, 20000, 'Espresso đậm đà kiểu Ý', TRUE),
('Americano', 1, 38000, 22000, 'Americano nhẹ nhàng', TRUE),
('Cappuccino', 1, 45000, 25000, 'Cappuccino với foam sữa mịn', TRUE),
('Latte', 1, 50000, 28000, 'Latte với sữa tươi thơm béo', TRUE),
('Macchiato', 1, 48000, 26000, 'Macchiato với caramel', TRUE),
('Mocha', 1, 55000, 30000, 'Mocha socola đậm đà', TRUE),
('Cà phê cold brew', 1, 42000, 24000, 'Cold brew ủ lạnh 12 tiếng', TRUE);

-- Trà & Trà sữa (category_id = 2)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Trà sữa trân châu', 2, 35000, 20000, 'Trà sữa với trân châu đen', TRUE),
('Trà sữa pudding', 2, 38000, 22000, 'Trà sữa với pudding flan', TRUE),
('Trà đào', 2, 32000, 18000, 'Trà đào tươi mát', TRUE),
('Trà chanh', 2, 28000, 16000, 'Trà chanh tươi vitamin C', TRUE),
('Trà đen đá', 2, 20000, 12000, 'Trà đen đá truyền thống', TRUE),
('Trà xanh', 2, 22000, 13000, 'Trà xanh thanh mát', TRUE),
('Trà ô long', 2, 26000, 15000, 'Trà ô long thơm nồng', TRUE),
('Trà sữa socola', 2, 40000, 23000, 'Trà sữa vị socola', TRUE),
('Trà sữa matcha', 2, 42000, 24000, 'Trà sữa matcha Nhật Bản', TRUE),
('Trà atiso', 2, 25000, 14000, 'Trà atiso giải nhiệt', TRUE);

-- Nước ép & Sinh tố (category_id = 3)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Nước cam ép', 3, 40000, 25000, 'Nước cam tươi 100%', TRUE),
('Nước chanh dây', 3, 35000, 20000, 'Nước chanh dây tươi mát', TRUE),
('Sinh tố bơ', 3, 45000, 26000, 'Sinh tố bơ sáp béo ngậy', TRUE),
('Sinh tố xoài', 3, 42000, 24000, 'Sinh tố xoài cát Hòa Lộc', TRUE),
('Sinh tố dâu', 3, 48000, 27000, 'Sinh tố dâu tươi ngọt', TRUE),
('Nước dưa hấu', 3, 32000, 18000, 'Nước dưa hấu giải khát', TRUE),
('Nước đu đủ', 3, 35000, 20000, 'Nước đu đủ giàu vitamin', TRUE),
('Sinh tố chuối', 3, 38000, 22000, 'Sinh tố chuối bổ dưỡng', TRUE),
('Nước táo ép', 3, 38000, 22000, 'Nước táo ép ngọt thanh', TRUE),
('Sinh tố mix berry', 3, 52000, 29000, 'Sinh tố các loại berry', TRUE);

-- Bánh ngọt (category_id = 4)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Bánh tiramisu', 4, 45000, 25000, 'Bánh tiramisu Ý truyền thống', TRUE),
('Bánh cheesecake', 4, 40000, 22000, 'Bánh cheesecake New York', TRUE),
('Bánh chocolate', 4, 42000, 24000, 'Bánh chocolate đậm đà', TRUE),
('Bánh red velvet', 4, 48000, 27000, 'Bánh red velvet nhung đỏ', TRUE),
('Bánh croissant', 4, 28000, 16000, 'Bánh croissant giòn thơm', TRUE),
('Bánh muffin', 4, 25000, 14000, 'Bánh muffin nhiều vị', TRUE),
('Bánh flan', 4, 20000, 12000, 'Bánh flan truyền thống', TRUE),
('Bánh su kem', 4, 30000, 17000, 'Bánh su kem tươi', TRUE),
('Bánh macaron', 4, 35000, 20000, 'Bánh macaron Pháp', TRUE),
('Bánh opera', 4, 50000, 28000, 'Bánh opera cao cấp', TRUE);

-- Đồ ăn nhẹ (category_id = 5)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Khoai tây chiên', 5, 25000, 15000, 'Khoai tây chiên giòn rụm', TRUE),
('Gà rán', 5, 35000, 20000, 'Gà rán giòn tan', TRUE),
('Sandwich', 5, 32000, 18000, 'Sandwich thịt nguội', TRUE),
('Hotdog', 5, 28000, 16000, 'Hotdog xúc xích Đức', TRUE),
('Bánh mì nướng', 5, 22000, 13000, 'Bánh mì nướng bơ tỏi', TRUE),
('Khoai lang nướng', 5, 20000, 12000, 'Khoai lang nướng thơm', TRUE),
('Nem chua rán', 5, 30000, 17000, 'Nem chua rán đặc sản', TRUE),
('Chả cá viên', 5, 26000, 15000, 'Chả cá viên chiên', TRUE),
('Bánh tráng nướng', 5, 18000, 10000, 'Bánh tráng nướng Đà Lạt', TRUE),
('Mix nuts', 5, 35000, 20000, 'Hỗn hợp hạt dinh dưỡng', TRUE);

-- Món chính (category_id = 6)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Cơm gà Hải Nam', 6, 55000, 32000, 'Cơm gà Hải Nam truyền thống', TRUE),
('Phở bò', 6, 50000, 28000, 'Phở bò Hà Nội chính hiệu', TRUE),
('Bún bò Huế', 6, 48000, 27000, 'Bún bò Huế cay nồng', TRUE),
('Mì quảng', 6, 45000, 25000, 'Mì quảng đặc sản Quảng Nam', TRUE),
('Cơm sườn nướng', 6, 52000, 30000, 'Cơm sườn nướng thơm lừng', TRUE),
('Pasta carbonara', 6, 65000, 38000, 'Pasta carbonara Ý', TRUE),
('Fried rice', 6, 42000, 24000, 'Cơm chiên Dương Châu', TRUE),
('Bánh xèo', 6, 38000, 22000, 'Bánh xèo miền Tây', TRUE);

-- Đồ uống đá xay (category_id = 7)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Frappuccino', 7, 58000, 33000, 'Frappuccino cà phê đá xay', TRUE),
('Chocolate smoothie', 7, 52000, 29000, 'Chocolate smoothie đá xay', TRUE),
('Oreo smoothie', 7, 55000, 31000, 'Oreo smoothie bánh quy', TRUE),
('Matcha frappé', 7, 60000, 34000, 'Matcha frappé Nhật Bản', TRUE),
('Coconut smoothie', 7, 48000, 27000, 'Coconut smoothie dừa tươi', TRUE);

-- =====================================================
-- 9. INSERT SAMPLE DATA - AREAS & TABLES
-- =====================================================

-- Insert areas
INSERT INTO areas (area_name, description) VALUES
('Tầng trệt', 'Khu vực tầng trệt - không gian mở'),
('Tầng 2', 'Khu vực tầng 2 - yên tĩnh'),
('Sân thượng', 'Khu vực ngoài trời - view đẹp'),
('VIP', 'Phòng riêng cao cấp'),
('Quầy bar', 'Khu vực quầy bar');

-- Insert tables (extensive)
INSERT INTO tables (table_name, area_id, capacity, status) VALUES
-- Tầng trệt (area_id = 1)
('Bàn 1', 1, 2, 'available'),
('Bàn 2', 1, 2, 'available'),
('Bàn 3', 1, 4, 'available'),
('Bàn 4', 1, 4, 'available'),
('Bàn 5', 1, 6, 'available'),
('Bàn 6', 1, 4, 'available'),
('Bàn 7', 1, 2, 'available'),
('Bàn 8', 1, 4, 'available'),

-- Tầng 2 (area_id = 2)
('Bàn 9', 2, 4, 'available'),
('Bàn 10', 2, 4, 'available'),
('Bàn 11', 2, 6, 'available'),
('Bàn 12', 2, 8, 'available'),
('Bàn 13', 2, 4, 'available'),
('Bàn 14', 2, 2, 'available'),
('Bàn 15', 2, 4, 'available'),

-- Sân thượng (area_id = 3)
('Bàn ST1', 3, 4, 'available'),
('Bàn ST2', 3, 6, 'available'),
('Bàn ST3', 3, 4, 'available'),
('Bàn ST4', 3, 8, 'available'),
('Bàn ST5', 3, 2, 'available'),

-- VIP (area_id = 4)
('Phòng VIP 1', 4, 6, 'available'),
('Phòng VIP 2', 4, 8, 'available'),
('Phòng VIP 3', 4, 10, 'available'),

-- Quầy bar (area_id = 5)
('Bar 1', 5, 1, 'available'),
('Bar 2', 5, 1, 'available'),
('Bar 3', 5, 1, 'available'),
('Bar 4', 5, 1, 'available');

-- =====================================================
-- 10. INSERT SAMPLE DATA - CUSTOMERS
-- =====================================================

INSERT INTO customers (full_name, phone, email, address, loyalty_points, total_spent) VALUES
('Nguyễn Văn An', '0901234567', 'an.nguyen@email.com', '123 Đường ABC, Quận 1, TP.HCM', 150, 850000),
('Trần Thị Bình', '0912345678', 'binh.tran@email.com', '456 Đường DEF, Quận 2, TP.HCM', 200, 1200000),
('Lê Hoàng Cường', '0923456789', 'cuong.le@email.com', '789 Đường GHI, Quận 3, TP.HCM', 75, 425000),
('Phạm Thị Dung', '0934567890', 'dung.pham@email.com', '321 Đường JKL, Quận 4, TP.HCM', 300, 1850000),
('Vũ Minh Quân', '0945678901', 'quan.vu@email.com', '654 Đường MNO, Quận 5, TP.HCM', 120, 680000),
('Đỗ Thị Hương', '0956789012', 'huong.do@email.com', '987 Đường PQR, Quận 6, TP.HCM', 180, 950000),
('Bùi Văn Nam', '0967890123', 'nam.bui@email.com', '147 Đường STU, Quận 7, TP.HCM', 90, 520000),
('Ngô Thị Lan', '0978901234', 'lan.ngo@email.com', '258 Đường VWX, Quận 8, TP.HCM', 250, 1350000);

-- =====================================================
-- 11. INSERT SAMPLE DATA - PROMOTIONS
-- =====================================================

INSERT INTO promotions (
    promotion_name, description, discount_type, discount_value, 
    min_order_amount, max_discount_amount, start_date, end_date, 
    is_active, usage_limit
) VALUES 
-- Percentage discounts
(
    'Giảm 10% - Đơn hàng từ 100k',
    'Giảm giá 10% cho đơn hàng từ 100,000đ trở lên',
    'PERCENTAGE',
    10.00,
    100000.00,
    50000.00,
    DATE_SUB(NOW(), INTERVAL 1 DAY),
    DATE_ADD(NOW(), INTERVAL 30 DAY),
    TRUE,
    0
),
(
    'Giảm 15% - Khách VIP',
    'Giảm giá 15% cho khách hàng VIP (đơn hàng từ 50k)',
    'PERCENTAGE',
    15.00,
    50000.00,
    100000.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 60 DAY),
    TRUE,
    0
),
(
    'Giảm 5% - Mọi đơn hàng',
    'Giảm giá 5% cho mọi đơn hàng, không điều kiện',
    'PERCENTAGE',
    5.00,
    0.00,
    20000.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 90 DAY),
    TRUE,
    1000
),

-- Fixed amount discounts
(
    'Giảm 20k - Đơn hàng từ 150k',
    'Giảm 20,000đ cho đơn hàng từ 150,000đ trở lên',
    'FIXED_AMOUNT',
    20000.00,
    150000.00,
    0.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 45 DAY),
    TRUE,
    500
),
(
    'Giảm 50k - Đơn hàng từ 300k',
    'Giảm 50,000đ cho đơn hàng từ 300,000đ trở lên',
    'FIXED_AMOUNT',
    50000.00,
    300000.00,
    0.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 30 DAY),
    TRUE,
    100
),

-- Special promotions
(
    'Cuối tuần giảm 12%',
    'Giảm giá 12% cho đơn hàng cuối tuần (từ 80k)',
    'PERCENTAGE',
    12.00,
    80000.00,
    60000.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 14 DAY),
    TRUE,
    0
),
(
    'Happy Hour - Giảm 25k',
    'Giảm 25,000đ cho đơn hàng từ 200k trong khung giờ vàng',
    'FIXED_AMOUNT',
    25000.00,
    200000.00,
    0.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    TRUE,
    200
);

-- =====================================================
-- 12. INSERT SAMPLE DATA - ORDERS & ORDER DETAILS
-- =====================================================

-- Sample orders with details
INSERT INTO orders (order_number, table_id, customer_id, user_id, total_amount, discount_amount, final_amount, payment_method, payment_status, order_status, notes) VALUES
('ORD001', 1, 1, 1, 105000, 10500, 94500, 'CASH', 'paid', 'completed', 'Khách yêu cầu ít đường'),
('ORD002', 3, 2, 2, 85000, 0, 85000, 'MOMO', 'paid', 'completed', ''),
('ORD003', 5, NULL, 3, 150000, 15000, 135000, 'CARD', 'paid', 'served', 'Bàn lớn'),
('ORD004', 2, 3, 1, 65000, 0, 65000, 'CASH', 'paid', 'ready', ''),
('ORD005', 7, 4, 4, 95000, 4750, 90250, 'VNPAY', 'paid', 'preparing', 'Khách chờ lâu'),
('ORD006', 9, NULL, 2, 200000, 20000, 180000, 'ZALOPAY', 'paid', 'confirmed', 'Đơn lớn'),
('ORD007', 11, 5, 3, 45000, 0, 45000, 'CASH', 'pending', 'pending', 'Chưa thanh toán'),
('ORD008', 15, 6, 4, 125000, 12500, 112500, 'BANK_TRANSFER', 'paid', 'completed', 'Chuyển khoản'),
('ORD009', 4, 7, 1, 75000, 0, 75000, 'CASH', 'paid', 'served', ''),
('ORD010', 6, 8, 2, 320000, 50000, 270000, 'CARD', 'paid', 'completed', 'Nhóm bạn');

-- Order details for ORD001 (table 1)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(1, 1, 2, 25000, 50000, 'Đen không đường'),
(1, 6, 1, 45000, 45000, 'Foam nhiều'),
(1, 21, 1, 10000, 10000, 'Thêm đá');

-- Order details for ORD002 (table 3)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(2, 11, 2, 35000, 70000, 'Ít đường'),
(2, 26, 1, 15000, 15000, '');

-- Order details for ORD003 (table 5)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(3, 7, 3, 50000, 150000, 'Hot latte');

-- Order details for ORD004 (table 2)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(4, 2, 1, 30000, 30000, ''),
(4, 21, 1, 35000, 35000, '');

-- Order details for ORD005 (table 7)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(5, 31, 1, 45000, 45000, ''),
(6, 25, 2, 25000, 50000, 'Ít muối');

-- Order details for ORD006 (table 9) - Large order
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(6, 9, 2, 55000, 110000, ''),
(6, 15, 2, 45000, 90000, '');

-- Order details for ORD007 (table 11) - Pending
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(7, 13, 1, 32000, 32000, ''),
(7, 22, 1, 13000, 13000, '');

-- Order details for ORD008 (table 15)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(8, 8, 1, 48000, 48000, ''),
(8, 19, 1, 42000, 42000, ''),
(8, 29, 1, 35000, 35000, '');

-- Order details for ORD009 (table 4)
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(9, 3, 1, 32000, 32000, ''),
(9, 33, 1, 43000, 43000, '');

-- Order details for ORD010 (table 6) - Large group
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) VALUES
(10, 41, 4, 55000, 220000, 'Nhóm 8 người'),
(10, 14, 2, 48000, 96000, ''),
(10, 25, 1, 25000, 25000, '');

-- =====================================================
-- 13. INSERT SAMPLE DATA - SYSTEM SETTINGS
-- =====================================================

INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('cafe_name', 'Nopita Café', 'Tên quán café'),
('cafe_address', '123 Đường Nguyễn Văn Cừ, Quận 1, TP.HCM', 'Địa chỉ quán'),
('cafe_phone', '0283.8965.789', 'Số điện thoại quán'),
('cafe_email', 'contact@nopitacafe.com', 'Email liên hệ'),
('tax_rate', '8', 'Thuế suất VAT (%)'),
('currency', 'VNĐ', 'Đơn vị tiền tệ'),
('receipt_footer', 'Cảm ơn quý khách! Hẹn gặp lại!', 'Lời cảm ơn cuối hóa đơn'),
('business_hours', '06:00-22:00', 'Giờ hoạt động'),
('wifi_password', 'nopita123', 'Mật khẩu WiFi'),
('social_facebook', 'facebook.com/nopitacafe', 'Facebook page'),
('social_instagram', '@nopitacafe', 'Instagram handle'),
('loyalty_rate', '1', 'Tỷ lệ tích điểm (1 điểm/1000đ)'),
('auto_print_receipt', 'true', 'Tự động in hóa đơn'),
('default_table_capacity', '4', 'Sức chứa mặc định của bàn');

-- =====================================================
-- 14. UPDATE STATISTICS AND RELATIONSHIPS
-- =====================================================

-- Update customer total spent based on orders
UPDATE customers c SET 
    total_spent = (
        SELECT COALESCE(SUM(o.final_amount), 0) 
        FROM orders o 
        WHERE o.customer_id = c.customer_id 
        AND o.payment_status = 'paid'
    ),
    loyalty_points = (
        SELECT COALESCE(SUM(o.final_amount), 0) / 1000
        FROM orders o 
        WHERE o.customer_id = c.customer_id 
        AND o.payment_status = 'paid'
    );

-- Add some order promotions usage
INSERT INTO order_promotions (order_id, promotion_id, discount_amount) VALUES
(1, 1, 10500),  -- ORD001 used 10% promotion
(3, 1, 15000),  -- ORD003 used 10% promotion  
(5, 3, 4750),   -- ORD005 used 5% promotion
(6, 1, 20000),  -- ORD006 used 10% promotion
(8, 1, 12500),  -- ORD008 used 10% promotion
(10, 4, 50000); -- ORD010 used 50k fixed promotion

-- Update promotion usage counts
UPDATE promotions SET usage_count = (
    SELECT COUNT(*) FROM order_promotions op WHERE op.promotion_id = promotions.promotion_id
);

-- =====================================================
-- 15. CREATE ADDITIONAL INDEXES FOR PERFORMANCE
-- =====================================================

-- Orders related indexes
CREATE INDEX idx_orders_date_status ON orders(order_date, order_status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status, order_date);
CREATE INDEX idx_orders_table_status ON orders(table_id, order_status);

-- Products related indexes  
CREATE INDEX idx_products_name ON products(product_name);
CREATE INDEX idx_products_price_range ON products(price, is_available);

-- Customer related indexes
CREATE INDEX idx_customers_loyalty ON customers(loyalty_points DESC);
CREATE INDEX idx_customers_spent ON customers(total_spent DESC);

-- Performance indexes for common queries
CREATE INDEX idx_order_details_summary ON order_details(order_id, quantity, total_price);

-- =====================================================
-- 16. VERIFICATION QUERIES
-- =====================================================

-- Show database summary
SELECT 
    'Database Created Successfully' AS status,
    NOW() AS created_at;

-- Show table counts
SELECT 
    'users' AS table_name, COUNT(*) AS record_count FROM users
UNION ALL
SELECT 'categories', COUNT(*) FROM categories  
UNION ALL
SELECT 'products', COUNT(*) FROM products
UNION ALL  
SELECT 'areas', COUNT(*) FROM areas
UNION ALL
SELECT 'tables', COUNT(*) FROM tables
UNION ALL
SELECT 'customers', COUNT(*) FROM customers
UNION ALL
SELECT 'orders', COUNT(*) FROM orders
UNION ALL
SELECT 'order_details', COUNT(*) FROM order_details
UNION ALL
SELECT 'promotions', COUNT(*) FROM promotions
UNION ALL
SELECT 'order_promotions', COUNT(*) FROM order_promotions
UNION ALL
SELECT 'system_settings', COUNT(*) FROM system_settings
ORDER BY table_name;

-- Show sample data preview
SELECT 'Sample Categories:' AS info;
SELECT category_id, category_name, display_order FROM categories ORDER BY display_order;

SELECT 'Sample Products (first 10):' AS info;
SELECT p.product_name, c.category_name, p.price 
FROM products p 
JOIN categories c ON p.category_id = c.category_id 
LIMIT 10;

SELECT 'Sample Tables by Area:' AS info;
SELECT a.area_name, COUNT(t.table_id) AS table_count
FROM areas a 
LEFT JOIN tables t ON a.area_id = t.area_id 
GROUP BY a.area_id, a.area_name;

SELECT 'Sample Orders Summary:' AS info;
SELECT 
    COUNT(*) AS total_orders,
    COUNT(CASE WHEN payment_status = 'paid' THEN 1 END) AS paid_orders,
    SUM(final_amount) AS total_revenue
FROM orders;

-- =====================================================
-- DATABASE SETUP COMPLETED SUCCESSFULLY!
-- =====================================================

SELECT 
    '🎉 CAFÉ MANAGEMENT DATABASE SETUP COMPLETED!' AS message,
    'Ready for JavaFX Application' AS status,
    NOW() AS timestamp;
