-- =====================================================
-- DATABASE: CAFE MANAGEMENT SYSTEM
-- Mô tả: Hệ thống quản lý quán cafe
-- Tác giả: Team 2_C2406L
-- Phiên bản: 2.0.1 (Tối ưu - Fixed CHECK constraints)
-- Ngày tạo: 31/07/2025
-- Ngày cập nhật: 31/01/2025
-- =====================================================

-- Tạo database với charset và collation tối ưu cho tiếng Việt
CREATE DATABASE IF NOT EXISTS cafe_management1 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE cafe_management1;

-- =====================================================
-- 1. BẢNG NGƯỜI DÙNG VÀ PHÂN QUYỀN
-- =====================================================

-- Bảng vai trò (roles)
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    permissions JSON, -- Lưu trữ quyền dưới dạng JSON
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (role_name IN ('admin', 'waiter', 'barista')),
    INDEX idx_role_name (role_name),
    INDEX idx_role_active (is_active)
);

-- Bảng người dùng (users)
CREATE TABLE users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL, -- Hash password
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE,
    phone VARCHAR(20),
    role_id INT NOT NULL,
    avatar_url VARCHAR(255),
    last_login TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (LENGTH(username) >= 3),
    CHECK (LENGTH(password) >= 6),
    CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CHECK (phone REGEXP '^[0-9]{10,11}$'),
    
    -- Indexes
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_phone (phone),
    INDEX idx_role_id (role_id),
    INDEX idx_user_active (is_active),
    INDEX idx_last_login (last_login)
);

-- =====================================================
-- 2. BẢNG DANH MỤC VÀ SẢN PHẨM
-- =====================================================

-- Bảng danh mục sản phẩm (categories)
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    image_url VARCHAR(255),
    sort_order INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (LENGTH(category_name) >= 2),
    
    -- Indexes
    INDEX idx_category_name (category_name),
    INDEX idx_category_active (is_active),
    INDEX idx_sort_order (sort_order)
);

-- Bảng sản phẩm (products)
CREATE TABLE products (
    product_id INT PRIMARY KEY AUTO_INCREMENT,
    product_name VARCHAR(150) NOT NULL,
    category_id INT NOT NULL,
    sku VARCHAR(50) UNIQUE, -- Stock Keeping Unit
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
    
    -- Foreign Keys
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (price >= 0),
    CHECK (cost_price >= 0),
    CHECK (stock_quantity >= 0),
    CHECK (min_stock_level >= 0),
    
    -- Indexes
    INDEX idx_product_name (product_name),
    INDEX idx_category_id (category_id),
    INDEX idx_sku (sku),
    INDEX idx_price (price),
    INDEX idx_product_active (is_active),
    INDEX idx_product_available (is_available),
    INDEX idx_stock_quantity (stock_quantity),
    FULLTEXT idx_product_search (product_name, description)
);

-- =====================================================
-- 3. BẢNG KHU VỰC VÀ BÀN
-- =====================================================

-- Bảng khu vực (areas)
CREATE TABLE areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    floor_number INT,
    capacity INT DEFAULT 0, -- Tổng sức chứa của khu vực
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (LENGTH(area_name) >= 2),
    CHECK (floor_number >= 0),
    CHECK (capacity >= 0),
    
    -- Indexes
    INDEX idx_area_name (area_name),
    INDEX idx_area_active (is_active),
    INDEX idx_floor_number (floor_number)
);

-- Bảng bàn (tables)
CREATE TABLE tables (
    table_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    area_id INT NOT NULL,
    table_number VARCHAR(20) NOT NULL, -- Số bàn
    capacity INT DEFAULT 4,
    status ENUM('available', 'occupied', 'reserved', 'cleaning', 'maintenance') DEFAULT 'available',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (area_id) REFERENCES areas(area_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (LENGTH(table_name) >= 2),
    CHECK (capacity >= 1 AND capacity <= 20),
    
    -- Unique constraint cho table_number trong cùng area
    UNIQUE KEY uk_area_table_number (area_id, table_number),
    
    -- Indexes
    INDEX idx_table_name (table_name),
    INDEX idx_area_id (area_id),
    INDEX idx_table_status (status),
    INDEX idx_table_active (is_active),
    INDEX idx_capacity (capacity)
);

-- =====================================================
-- 4. BẢNG KHÁCH HÀNG
-- =====================================================

-- Bảng khách hàng (customers)
CREATE TABLE customers (
    customer_id INT PRIMARY KEY AUTO_INCREMENT,
    customer_code VARCHAR(20) UNIQUE, -- Mã khách hàng
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
    
    -- Constraints
    CHECK (LENGTH(full_name) >= 2),
    CHECK (phone REGEXP '^[0-9]{10,11}$'),
    CHECK (email REGEXP '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CHECK (loyalty_points >= 0),
    CHECK (total_spent >= 0),
    
    -- Indexes
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

-- =====================================================
-- 5. BẢNG ĐƠN HÀNG
-- =====================================================

-- Bảng đơn hàng (orders)
CREATE TABLE orders (
    order_id INT PRIMARY KEY AUTO_INCREMENT,
    order_number VARCHAR(20) UNIQUE NOT NULL,
    table_id INT,
    customer_id INT,
    user_id INT NOT NULL, -- Nhân viên tạo đơn
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
    
    -- Foreign Keys
    FOREIGN KEY (table_id) REFERENCES tables(table_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (total_amount >= 0),
    CHECK (discount_amount >= 0),
    CHECK (tax_amount >= 0),
    CHECK (final_amount >= 0),
    CHECK (final_amount = total_amount - discount_amount + tax_amount),
    
    -- Indexes
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

-- Bảng chi tiết đơn hàng (order_details)
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    discount_percent DECIMAL(5,2) DEFAULT 0.00, -- Giảm giá theo %
    total_price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (quantity > 0),
    CHECK (unit_price >= 0),
    CHECK (discount_percent >= 0 AND discount_percent <= 100),
    CHECK (total_price >= 0),
    
    -- Indexes
    INDEX idx_order_id (order_id),
    INDEX idx_product_id (product_id),
    INDEX idx_quantity (quantity),
    INDEX idx_unit_price (unit_price),
    INDEX idx_total_price (total_price)
);

-- =====================================================
-- 6. BẢNG KHUYẾN MÃI
-- =====================================================

-- Bảng khuyến mãi (promotions)
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    promotion_code VARCHAR(50) UNIQUE, -- Mã khuyến mãi
    promotion_name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount', 'buy_x_get_y') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10,2), -- Giới hạn giảm giá tối đa
    usage_limit INT DEFAULT -1, -- -1 = không giới hạn
    used_count INT DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (LENGTH(promotion_name) >= 2),
    CHECK (discount_value >= 0),
    CHECK (min_order_amount >= 0),
    CHECK (max_discount_amount IS NULL OR max_discount_amount >= 0),
    CHECK (usage_limit = -1 OR usage_limit > 0),
    CHECK (used_count >= 0),
    CHECK (start_date <= end_date),
    
    -- Indexes
    INDEX idx_promotion_code (promotion_code),
    INDEX idx_promotion_name (promotion_name),
    INDEX idx_discount_type (discount_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_promotion_active (is_active),
    INDEX idx_usage_limit (usage_limit)
);

-- Bảng áp dụng khuyến mãi cho đơn hàng
CREATE TABLE order_promotions (
    order_promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    promotion_id INT NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE RESTRICT ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (discount_amount >= 0),
    
    -- Indexes
    INDEX idx_order_id (order_id),
    INDEX idx_promotion_id (promotion_id),
    INDEX idx_applied_at (applied_at)
);

-- =====================================================
-- 7. BẢNG CHẤM CÔNG
-- =====================================================

-- Bảng chấm công (attendance)
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
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
    
    -- Constraints
    CHECK (total_hours >= 0 AND total_hours <= 24),
    CHECK (overtime_hours >= 0),
    CHECK (check_out IS NULL OR check_in IS NOT NULL),
    CHECK (check_out IS NULL OR check_out > check_in),
    
    -- Unique constraint: một user chỉ có một record chấm công mỗi ngày
    UNIQUE KEY uk_user_work_date (user_id, work_date),
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_work_date (work_date),
    INDEX idx_check_in (check_in),
    INDEX idx_check_out (check_out),
    INDEX idx_status (status),
    INDEX idx_total_hours (total_hours)
);

-- =====================================================
-- 8. BẢNG CÀI ĐẶT HỆ THỐNG
-- =====================================================

-- Bảng cài đặt hệ thống (system_settings)
CREATE TABLE system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    setting_type ENUM('string', 'number', 'boolean', 'json', 'date') DEFAULT 'string',
    description TEXT,
    is_editable BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints
    CHECK (LENGTH(setting_key) >= 2),
    
    -- Indexes
    INDEX idx_setting_key (setting_key),
    INDEX idx_setting_type (setting_type),
    INDEX idx_is_editable (is_editable)
);

-- =====================================================
-- 9. BẢNG LOG HỆ THỐNG
-- =====================================================

-- Bảng log hệ thống (system_logs)
CREATE TABLE system_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    action VARCHAR(100) NOT NULL,
    table_name VARCHAR(50),
    record_id INT,
    old_values JSON,
    new_values JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign Keys
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL ON UPDATE CASCADE,
    
    -- Indexes
    INDEX idx_user_id (user_id),
    INDEX idx_action (action),
    INDEX idx_table_name (table_name),
    INDEX idx_record_id (record_id),
    INDEX idx_created_at (created_at),
    INDEX idx_ip_address (ip_address)
);

-- =====================================================
-- 10. TẠO INDEXES TỐI ƯU HIỆU SUẤT
-- =====================================================

-- Indexes cho bảng orders
CREATE INDEX idx_orders_date_status ON orders(order_date, order_status);
CREATE INDEX idx_orders_payment_status ON orders(payment_status, payment_method);
CREATE INDEX idx_orders_user_date ON orders(user_id, order_date);

-- Indexes cho bảng products
CREATE INDEX idx_products_category_price ON products(category_id, price);
CREATE INDEX idx_products_stock ON products(stock_quantity, is_available);

-- Indexes cho bảng tables
CREATE INDEX idx_tables_area_status ON tables(area_id, status);
CREATE INDEX idx_tables_capacity_status ON tables(capacity, status);

-- Indexes cho bảng customers
CREATE INDEX idx_customers_loyalty ON customers(loyalty_points DESC, total_spent DESC);
CREATE INDEX idx_customers_last_visit ON customers(last_visit DESC);

-- Indexes cho bảng attendance
CREATE INDEX idx_attendance_user_date ON attendance(user_id, work_date);
CREATE INDEX idx_attendance_date_status ON attendance(work_date, status);

-- Indexes cho bảng promotions
CREATE INDEX idx_promotions_date_active ON promotions(start_date, end_date, is_active);
CREATE INDEX idx_promotions_usage ON promotions(usage_limit, used_count);

-- =====================================================
-- 11. TẠO VIEWS HỮU ÍCH
-- =====================================================

-- View tổng quan đơn hàng
CREATE VIEW v_order_summary AS
SELECT 
    o.order_id,
    o.order_number,
    o.order_date,
    o.total_amount,
    o.discount_amount,
    o.final_amount,
    o.payment_status,
    o.order_status,
    c.full_name AS customer_name,
    u.full_name AS staff_name,
    t.table_name,
    a.area_name
FROM orders o
LEFT JOIN customers c ON o.customer_id = c.customer_id
LEFT JOIN users u ON o.user_id = u.user_id
LEFT JOIN tables t ON o.table_id = t.table_id
LEFT JOIN areas a ON t.area_id = a.area_id;

-- View sản phẩm tồn kho
CREATE VIEW v_product_inventory AS
SELECT 
    p.product_id,
    p.product_name,
    p.sku,
    c.category_name,
    p.price,
    p.stock_quantity,
    p.min_stock_level,
    CASE 
        WHEN p.stock_quantity <= p.min_stock_level THEN 'Low Stock'
        WHEN p.stock_quantity = 0 THEN 'Out of Stock'
        ELSE 'In Stock'
    END AS stock_status,
    p.is_available,
    p.is_active
FROM products p
JOIN categories c ON p.category_id = c.category_id;

-- View báo cáo doanh thu theo ngày
CREATE VIEW v_daily_revenue AS
SELECT 
    DATE(order_date) AS sale_date,
    COUNT(*) AS total_orders,
    SUM(final_amount) AS total_revenue,
    SUM(discount_amount) AS total_discount,
    AVG(final_amount) AS avg_order_value
FROM orders 
WHERE payment_status = 'paid'
GROUP BY DATE(order_date);

-- =====================================================
-- 12. TẠO TRIGGERS (5 triggers)
-- =====================================================

-- Trigger cập nhật used_count khi áp dụng khuyến mãi
DELIMITER //
CREATE TRIGGER tr_order_promotions_insert
AFTER INSERT ON order_promotions
FOR EACH ROW
BEGIN
    UPDATE promotions 
    SET used_count = used_count + 1 
    WHERE promotion_id = NEW.promotion_id;
END//

-- Trigger cập nhật total_spent và loyalty_points của customer
CREATE TRIGGER tr_orders_update_customer
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF NEW.payment_status = 'paid' AND OLD.payment_status != 'paid' THEN
        UPDATE customers 
        SET total_spent = total_spent + NEW.final_amount,
            loyalty_points = loyalty_points + FLOOR(NEW.final_amount / 10000),
            last_visit = NOW()
        WHERE customer_id = NEW.customer_id;
    END IF;
END//

-- Trigger cập nhật stock_quantity khi tạo order_detail
CREATE TRIGGER tr_order_details_insert
AFTER INSERT ON order_details
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity 
    WHERE product_id = NEW.product_id;
END//

-- Trigger validate ngày kết thúc khuyến mãi
CREATE TRIGGER tr_promotions_validate_date
BEFORE INSERT ON promotions
FOR EACH ROW
BEGIN
    IF NEW.end_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc khuyến mãi không thể là ngày trong quá khứ';
    END IF;
END//

-- Trigger validate ngày kết thúc khuyến mãi khi update
CREATE TRIGGER tr_promotions_validate_date_update
BEFORE UPDATE ON promotions
FOR EACH ROW
BEGIN
    IF NEW.end_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc khuyến mãi không thể là ngày trong quá khứ';
    END IF;
END//

DELIMITER ;

-- =====================================================
-- 13. TẠO STORED PROCEDURES
-- =====================================================

-- Procedure tạo đơn hàng mới
DELIMITER //
CREATE PROCEDURE sp_create_order(
    IN p_table_id INT,
    IN p_customer_id INT,
    IN p_user_id INT,
    IN p_notes TEXT
)
BEGIN
    DECLARE v_order_number VARCHAR(20);
    DECLARE v_order_id INT;
    
    -- Tạo order number
    SET v_order_number = CONCAT('ORD', DATE_FORMAT(NOW(), '%Y%m%d'), LPAD((SELECT COUNT(*) + 1 FROM orders WHERE DATE(order_date) = CURDATE()), 4, '0'));
    
    -- Tạo đơn hàng
    INSERT INTO orders (order_number, table_id, customer_id, user_id, notes)
    VALUES (v_order_number, p_table_id, p_customer_id, p_user_id, p_notes);
    
    SET v_order_id = LAST_INSERT_ID();
    
    -- Cập nhật trạng thái bàn
    IF p_table_id IS NOT NULL THEN
        UPDATE tables SET status = 'occupied' WHERE table_id = p_table_id;
    END IF;
    
    SELECT v_order_id AS order_id, v_order_number AS order_number;
END//

-- Procedure tính toán tổng đơn hàng
CREATE PROCEDURE sp_calculate_order_total(IN p_order_id INT)
BEGIN
    DECLARE v_total DECIMAL(10,2);
    DECLARE v_discount DECIMAL(10,2);
    DECLARE v_tax_rate DECIMAL(5,2);
    DECLARE v_tax_amount DECIMAL(10,2);
    DECLARE v_final_amount DECIMAL(10,2);
    
    -- Tính tổng tiền
    SELECT COALESCE(SUM(total_price), 0) INTO v_total
    FROM order_details 
    WHERE order_id = p_order_id;
    
    -- Tính tổng giảm giá
    SELECT COALESCE(SUM(discount_amount), 0) INTO v_discount
    FROM order_promotions 
    WHERE order_id = p_order_id;
    
    -- Lấy thuế suất
    SELECT CAST(setting_value AS DECIMAL(5,2)) INTO v_tax_rate
    FROM system_settings 
    WHERE setting_key = 'tax_rate';
    
    SET v_tax_rate = COALESCE(v_tax_rate, 10.00);
    SET v_tax_amount = (v_total - v_discount) * v_tax_rate / 100;
    SET v_final_amount = v_total - v_discount + v_tax_amount;
    
    -- Cập nhật đơn hàng
    UPDATE orders 
    SET total_amount = v_total,
        discount_amount = v_discount,
        tax_amount = v_tax_amount,
        final_amount = v_final_amount
    WHERE order_id = p_order_id;
    
    SELECT v_total AS total_amount, v_discount AS discount_amount, v_tax_amount AS tax_amount, v_final_amount AS final_amount;
END//

DELIMITER ;

-- =====================================================
-- 14. TẠO FUNCTIONS
-- =====================================================

-- Function tạo mã khách hàng tự động
DELIMITER //
CREATE FUNCTION fn_generate_customer_code() 
RETURNS VARCHAR(20)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_code VARCHAR(20);
    DECLARE v_count INT;
    
    SELECT COUNT(*) + 1 INTO v_count FROM customers;
    SET v_code = CONCAT('CUS', LPAD(v_count, 6, '0'));
    
    RETURN v_code;
END//

-- Function kiểm tra khuyến mãi có hợp lệ không
CREATE FUNCTION fn_check_promotion_valid(
    p_promotion_id INT,
    p_order_amount DECIMAL(10,2)
) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE v_is_valid BOOLEAN DEFAULT FALSE;
    DECLARE v_start_date DATE;
    DECLARE v_end_date DATE;
    DECLARE v_is_active BOOLEAN;
    DECLARE v_min_amount DECIMAL(10,2);
    DECLARE v_usage_limit INT;
    DECLARE v_used_count INT;
    
    SELECT start_date, end_date, is_active, min_order_amount, usage_limit, used_count
    INTO v_start_date, v_end_date, v_is_active, v_min_amount, v_usage_limit, v_used_count
    FROM promotions 
    WHERE promotion_id = p_promotion_id;
    
    IF v_is_active = TRUE 
       AND CURDATE() BETWEEN v_start_date AND v_end_date
       AND p_order_amount >= v_min_amount
       AND (v_usage_limit = -1 OR v_used_count < v_usage_limit) THEN
        SET v_is_valid = TRUE;
    END IF;
    
    RETURN v_is_valid;
END//

DELIMITER ;

-- =====================================================
-- HOÀN THÀNH TẠO DATABASE
-- =====================================================

SELECT 'Database cafe_management đã được tạo thành công!' AS message;
SELECT 'Phiên bản: 2.0 (Tối ưu)' AS version;
SELECT 'Tổng số bảng: 10' AS total_tables;
SELECT 'Tổng số indexes: 45+' AS total_indexes;
SELECT 'Tổng số views: 3' AS total_views;
SELECT 'Tổng số triggers: 3' AS total_triggers;
SELECT 'Tổng số procedures: 2' AS total_procedures;
SELECT 'Tổng số functions: 2' AS total_functions; 