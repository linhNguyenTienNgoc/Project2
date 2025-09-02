-- =====================================================
-- CAFE MANAGEMENT DATABASE STRUCTURE
-- File: database/cafe_database_structure.sql
-- Purpose: Cấu trúc cơ sở dữ liệu quản lý quán café
-- Author: Team 2_C2406L
-- Version: 2.1.0 (Optimized Structure)
-- =====================================================

-- Create and use database
CREATE DATABASE IF NOT EXISTS cafe_management;
USE cafe_management;

-- =====================================================
-- DISABLE SAFETY CHECKS
-- =====================================================
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- DROP EXISTING TABLES AND VIEWS
-- =====================================================

-- Drop views first
DROP VIEW IF EXISTS promotion_stats;
DROP VIEW IF EXISTS view_categories_with_image;
DROP VIEW IF EXISTS view_menu_items;
DROP VIEW IF EXISTS view_order_summary;
DROP VIEW IF EXISTS view_sales_report;

-- Drop tables in reverse order
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

-- =====================================================
-- CREATE CORE TABLES
-- =====================================================

-- Users table (Only Admin and Staff roles)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role ENUM('Admin', 'Staff') NOT NULL DEFAULT 'Staff',
    is_active BOOLEAN DEFAULT TRUE,
    image_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_user_role (role),
    INDEX idx_user_active (is_active),
    INDEX idx_username (username)
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
    
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT,
    INDEX idx_product_category (category_id),
    INDEX idx_product_active (is_active),
    INDEX idx_product_available (is_available),
    INDEX idx_product_price (price),
    INDEX idx_product_name (product_name)
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
    INDEX idx_table_active (is_active),
    INDEX idx_table_name (table_name)
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
    INDEX idx_customer_points (loyalty_points),
    INDEX idx_customer_name (full_name)
);

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
    
    FOREIGN KEY (table_id) REFERENCES tables(table_id) ON DELETE SET NULL,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    
    -- ✅ THÊM CONSTRAINT ĐỂ ĐẢM BẢO TÍNH NHẤT QUÁN (ĐÃ SỬA)
    CONSTRAINT chk_order_status_consistency CHECK (
        (payment_status = 'paid' AND order_status IN ('served', 'completed', 'confirmed', 'preparing', 'ready')) OR
        (payment_status = 'pending' AND order_status IN ('pending', 'confirmed', 'preparing', 'ready')) OR
        (payment_status = 'cancelled' AND order_status = 'cancelled')
    ),
    
    INDEX idx_order_date (order_date),
    INDEX idx_order_status (order_status),
    INDEX idx_payment_status (payment_status),
    INDEX idx_order_table (table_id),
    INDEX idx_order_user (user_id),
    INDEX idx_order_number (order_number),
    INDEX idx_order_customer (customer_id)
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
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT,
    
    INDEX idx_orderdetail_order (order_id),
    INDEX idx_orderdetail_product (product_id)
);

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
    
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE,
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
-- CREATE VIEWS FOR ANALYTICS
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

-- Menu items view
CREATE OR REPLACE VIEW view_menu_items AS
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    p.price,
    p.cost_price,
    p.description,
    p.image_url,
    p.is_available,
    p.is_active,
    c.display_order AS category_order
FROM products p
JOIN categories c ON p.category_id = c.category_id
WHERE p.is_active = TRUE AND c.is_active = TRUE
ORDER BY c.display_order, p.product_name;

-- Order summary view
CREATE OR REPLACE VIEW view_order_summary AS
SELECT 
    o.order_id,
    o.order_number,
    t.table_name,
    a.area_name,
    c.full_name AS customer_name,
    u.full_name AS staff_name,
    o.order_date,
    o.total_amount,
    o.discount_amount,
    o.final_amount,
    o.payment_method,
    o.payment_status,
    o.order_status,
    COUNT(od.order_detail_id) AS total_items,
    SUM(od.quantity) AS total_quantity
FROM orders o
LEFT JOIN tables t ON o.table_id = t.table_id
LEFT JOIN areas a ON t.area_id = a.area_id
LEFT JOIN customers c ON o.customer_id = c.customer_id
LEFT JOIN users u ON o.user_id = u.user_id
LEFT JOIN order_details od ON o.order_id = od.order_id
GROUP BY o.order_id
ORDER BY o.order_date DESC;

-- Sales report view
CREATE OR REPLACE VIEW view_sales_report AS
SELECT 
    DATE(o.order_date) AS sale_date,
    COUNT(o.order_id) AS total_orders,
    SUM(o.total_amount) AS gross_revenue,
    SUM(o.discount_amount) AS total_discount,
    SUM(o.final_amount) AS net_revenue,
    AVG(o.final_amount) AS avg_order_value,
    COUNT(CASE WHEN o.payment_status = 'paid' THEN 1 END) AS paid_orders,
    COUNT(CASE WHEN o.payment_status = 'pending' THEN 1 END) AS pending_orders
FROM orders o
WHERE o.order_status != 'cancelled'
GROUP BY DATE(o.order_date)
ORDER BY sale_date DESC;

-- =====================================================
-- CREATE TRIGGERS FOR TABLE STATUS MANAGEMENT
-- =====================================================

DELIMITER //

-- Drop existing trigger first
DROP TRIGGER IF EXISTS update_table_status_on_insert//

-- Trigger to update table status when inserting new order
CREATE TRIGGER update_table_status_on_insert 
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
    IF NEW.table_id IS NOT NULL AND NEW.order_status IN ('pending', 'confirmed', 'preparing', 'ready', 'served') THEN
        UPDATE tables SET status = 'occupied' WHERE table_id = NEW.table_id;
    END IF;
END//

-- Drop existing trigger first
DROP TRIGGER IF EXISTS update_table_status_on_update//

-- Trigger to update table status when updating order - SIMPLIFIED LOGIC
CREATE TRIGGER update_table_status_on_update 
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    -- Chỉ xử lý khi có thay đổi thực sự
    IF NEW.order_status != OLD.order_status OR NEW.payment_status != OLD.payment_status THEN
        
        -- Nếu đơn hàng hoàn thành hoặc hủy -> bàn available
        IF NEW.order_status IN ('completed', 'cancelled') THEN
            UPDATE tables SET status = 'available' WHERE table_id = NEW.table_id;
        END IF;
        
        -- Nếu đơn hàng đang active -> bàn occupied
        IF NEW.order_status IN ('pending', 'confirmed', 'preparing', 'ready', 'served') THEN
            UPDATE tables SET status = 'occupied' WHERE table_id = NEW.table_id;
        END IF;
    END IF;
END//

-- Drop existing trigger first
DROP TRIGGER IF EXISTS update_customer_stats//

-- Trigger to update customer loyalty points and total spent
CREATE TRIGGER update_customer_stats 
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.payment_status = 'paid' AND OLD.payment_status != 'paid' AND NEW.customer_id IS NOT NULL THEN
        UPDATE customers SET 
            total_spent = total_spent + NEW.final_amount,
            loyalty_points = loyalty_points + FLOOR(NEW.final_amount / 1000)
        WHERE customer_id = NEW.customer_id;
    END IF;
END//

-- Drop existing trigger first
DROP TRIGGER IF EXISTS update_promotion_usage//

-- Trigger to update promotion usage count
CREATE TRIGGER update_promotion_usage 
AFTER INSERT ON order_promotions
FOR EACH ROW
BEGIN
    UPDATE promotions 
    SET usage_count = usage_count + 1 
    WHERE promotion_id = NEW.promotion_id;
END//

DELIMITER ;

-- =====================================================
-- CREATE STORED PROCEDURES
-- =====================================================

DELIMITER //

-- Drop existing procedure first
DROP PROCEDURE IF EXISTS sync_table_status//

-- Procedure to sync table status - IMPROVED LOGIC
CREATE PROCEDURE sync_table_status()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE table_id_var INT;
    DECLARE table_status_var VARCHAR(20);
    
    -- Cursor để xử lý từng bàn một cách an toàn
    DECLARE table_cursor CURSOR FOR 
        SELECT t.table_id, t.status 
        FROM tables t;
    
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;
    
    -- Mở cursor
    OPEN table_cursor;
    
    table_loop: LOOP
        FETCH table_cursor INTO table_id_var, table_status_var;
        IF done THEN
            LEAVE table_loop;
        END IF;
        
        -- Kiểm tra trạng thái đơn hàng của bàn
        IF EXISTS (
            SELECT 1 FROM orders o 
            WHERE o.table_id = table_id_var 
            AND o.order_status IN ('pending', 'confirmed', 'preparing', 'ready', 'served')
        ) THEN
            -- Có đơn hàng active -> occupied
            IF table_status_var != 'occupied' THEN
                UPDATE tables SET status = 'occupied' WHERE table_id = table_id_var;
            END IF;
        ELSE
            -- Không có đơn hàng active -> available (nếu không phải cleaning/reserved)
            IF table_status_var NOT IN ('cleaning', 'reserved') AND table_status_var != 'available' THEN
                UPDATE tables SET status = 'available' WHERE table_id = table_id_var;
            END IF;
        END IF;
    END LOOP;
    
    -- Đóng cursor
    CLOSE table_cursor;
    
    SELECT 'Table status synchronized successfully with improved logic' AS message;
END//

-- Drop existing procedure first
DROP PROCEDURE IF EXISTS get_daily_sales_report//

-- Procedure to get daily sales report
CREATE PROCEDURE get_daily_sales_report(IN report_date DATE)
BEGIN
    SELECT 
        COUNT(o.order_id) AS total_orders,
        COALESCE(SUM(o.total_amount), 0) AS gross_revenue,
        COALESCE(SUM(o.discount_amount), 0) AS total_discount,
        COALESCE(SUM(o.final_amount), 0) AS net_revenue,
        COALESCE(AVG(o.final_amount), 0) AS avg_order_value,
        COUNT(CASE WHEN o.payment_method = 'CASH' THEN 1 END) AS cash_orders,
        COUNT(CASE WHEN o.payment_method IN ('MOMO', 'VNPAY', 'ZALOPAY') THEN 1 END) AS digital_orders
    FROM orders o
    WHERE DATE(o.order_date) = report_date 
    AND o.payment_status = 'paid'
    AND o.order_status != 'cancelled';
END//

DELIMITER ;

-- =====================================================
-- ENABLE SAFETY CHECKS
-- =====================================================

SET SQL_SAFE_UPDATES = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- FIXES APPLIED FOR TABLE STATUS ISSUES
-- =====================================================

-- ✅ ĐÃ SỬA CÁC VẤN ĐỀ:
-- 1. Thêm CONSTRAINT chk_order_status_consistency để đảm bảo tính nhất quán
-- 2. Đơn giản hóa trigger update_table_status_on_update để tránh conflict
-- 3. Cải thiện stored procedure sync_table_status() với cursor logic
-- 4. Sửa sample data trong cafe_sample_data.sql để nhất quán

-- ✅ QUY TẮC TRẠNG THÁI MỚI:
-- - paid + completed/served: Đã thanh toán và hoàn thành
-- - pending + pending/confirmed/preparing/ready: Chưa thanh toán, đang xử lý
-- - cancelled + cancelled: Đã hủy

-- =====================================================
-- SHOW COMPLETION MESSAGE
-- =====================================================

SELECT 
    '🎉 CAFE DATABASE STRUCTURE CREATED SUCCESSFULLY!' AS message,
    NOW() AS created_at,
    'Ready for data insertion with fixes applied' AS status;