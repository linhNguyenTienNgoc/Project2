-- =====================================================
-- DATABASE: CAFE MANAGEMENT SYSTEM
-- Mô tả: Hệ thống quản lý quán cafe
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/07/2025
-- =====================================================

-- Tạo database
CREATE DATABASE IF NOT EXISTS cafe_management;
USE cafe_management;

-- =====================================================
-- 1. BẢNG NGƯỜI DÙNG VÀ PHÂN QUYỀN
-- =====================================================

-- Bảng vai trò (roles)
CREATE TABLE roles (
    role_id INT PRIMARY KEY AUTO_INCREMENT,
    role_name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng người dùng (users)
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
    FOREIGN KEY (role_id) REFERENCES roles(role_id)
);

-- =====================================================
-- 2. BẢNG DANH MỤC VÀ SẢN PHẨM
-- =====================================================

-- Bảng danh mục sản phẩm (categories)
CREATE TABLE categories (
    category_id INT PRIMARY KEY AUTO_INCREMENT,
    category_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng sản phẩm (products)
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
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- =====================================================
-- 3. BẢNG KHU VỰC VÀ BÀN
-- =====================================================

-- Bảng khu vực (areas)
CREATE TABLE areas (
    area_id INT PRIMARY KEY AUTO_INCREMENT,
    area_name VARCHAR(100) NOT NULL,
    description TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng bàn (tables)
CREATE TABLE tables (
    table_id INT PRIMARY KEY AUTO_INCREMENT,
    table_name VARCHAR(50) NOT NULL,
    area_id INT NOT NULL,
    capacity INT DEFAULT 4,
    status ENUM('available', 'occupied', 'reserved', 'cleaning') DEFAULT 'available',
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (area_id) REFERENCES areas(area_id)
);

-- =====================================================
-- 4. BẢNG KHÁCH HÀNG
-- =====================================================

-- Bảng khách hàng (customers)
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
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
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
    final_amount DECIMAL(10,2) NOT NULL,
    payment_method ENUM('cash', 'card', 'momo', 'vnpay', 'zalopay') DEFAULT 'cash',
    payment_status ENUM('pending', 'paid', 'cancelled') DEFAULT 'pending',
    order_status ENUM('pending', 'preparing', 'ready', 'served', 'completed', 'cancelled') DEFAULT 'pending',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(table_id),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- Bảng chi tiết đơn hàng (order_details)
CREATE TABLE order_details (
    order_detail_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

-- =====================================================
-- 6. BẢNG KHUYẾN MÃI
-- =====================================================

-- Bảng khuyến mãi (promotions)
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    promotion_name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0.00,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Bảng áp dụng khuyến mãi cho đơn hàng
CREATE TABLE order_promotions (
    order_promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    order_id INT NOT NULL,
    promotion_id INT NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id),
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id)
);


-- =====================================================
-- 8. BẢNG CHẤM CÔNG
-- =====================================================

-- Bảng chấm công (attendance)
CREATE TABLE attendance (
    attendance_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    check_in TIMESTAMP,
    check_out TIMESTAMP,
    work_date DATE NOT NULL,
    total_hours DECIMAL(4,2),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- =====================================================
-- 9. BẢNG CÀI ĐẶT HỆ THỐNG
-- =====================================================

-- Bảng cài đặt hệ thống (system_settings)
CREATE TABLE system_settings (
    setting_id INT PRIMARY KEY AUTO_INCREMENT,
    setting_key VARCHAR(100) NOT NULL UNIQUE,
    setting_value TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =====================================================
-- INSERT DỮ LIỆU MẪU
-- =====================================================

-- Thêm vai trò
INSERT INTO roles (role_name, description) VALUES
('admin', 'Quản trị viên hệ thống'),
('manager', 'Quản lý quán'),
('cashier', 'Thu ngân'),
('waiter', 'Phục vụ'),
('barista', 'Pha chế');

-- Thêm người dùng mẫu
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
('admin', '123', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 1),
('manager', '123', 'Trần Xuân Quang Minh', 'manager@cafe.com', '0123456790', 2),
('cashier1', '123', 'Vũ Hoàng Nam', 'cashier@cafe.com', '0123456791', 3),
('waiter1', '123', 'Dương Tuấn Minh', 'waiter@cafe.com', '0123456792', 4),
('barista1', '123', 'Nguyễn Thị Nguyệt Nhi', 'barista@cafe.com', '0123456793', 5);

-- Thêm danh mục
INSERT INTO categories (category_name, description) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại'),
('Trà', 'Trà sữa, trà đá, trà nóng'),
('Nước ép', 'Nước ép trái cây tươi'),
('Bánh', 'Bánh ngọt, bánh mặn'),
('Snack', 'Đồ ăn nhẹ');

-- Thêm khu vực
INSERT INTO areas (area_name, description) VALUES
('Tầng 1', 'Khu vực tầng trệt'),
('Tầng 2', 'Khu vực tầng 2'),
('Sân thượng', 'Khu vực ngoài trời'),
('VIP', 'Khu vực riêng tư');

-- Thêm bàn
INSERT INTO tables (table_name, area_id, capacity, status) VALUES
('Bàn 1', 1, 4, 'available'),
('Bàn 2', 1, 4, 'available'),
('Bàn 3', 1, 6, 'available'),
('Bàn 4', 2, 4, 'available'),
('Bàn 5', 2, 8, 'available'),
('Bàn VIP 1', 4, 4, 'available');

-- Thêm sản phẩm
INSERT INTO products (product_name, category_id, price, cost_price, description) VALUES
('Cà phê đen', 1, 25000, 15000, 'Cà phê đen truyền thống'),
('Cà phê sữa', 1, 30000, 18000, 'Cà phê sữa đặc'),
('Cappuccino', 1, 45000, 25000, 'Cappuccino kiểu Ý'),
('Latte', 1, 50000, 28000, 'Latte với sữa tươi'),
('Trà sữa trân châu', 2, 35000, 20000, 'Trà sữa với trân châu'),
('Trà đá', 2, 20000, 12000, 'Trà đá mát lạnh'),
('Nước cam ép', 3, 40000, 25000, 'Nước cam tươi'),
('Nước chanh dây', 3, 35000, 20000, 'Nước chanh dây tươi'),
('Bánh tiramisu', 4, 45000, 25000, 'Bánh tiramisu Ý'),
('Bánh cheesecake', 4, 40000, 22000, 'Bánh cheesecake New York'),
('Khoai tây chiên', 5, 25000, 15000, 'Khoai tây chiên giòn'),
('Gà rán', 5, 35000, 20000, 'Gà rán giòn');


-- Thêm cài đặt hệ thống
INSERT INTO system_settings (setting_key, setting_value, description) VALUES
('cafe_name', 'Cafe Project2', 'Tên quán cafe'),
('cafe_address', '123 Đường ABC, Quận XYZ, TP.HCM', 'Địa chỉ quán'),
('cafe_phone', '0123456789', 'Số điện thoại quán'),
('tax_rate', '10', 'Thuế suất (%)'),
('currency', 'VND', 'Đơn vị tiền tệ'),
('receipt_footer', 'Cảm ơn quý khách đã sử dụng dịch vụ!', 'Chữ ký cuối hóa đơn');

-- Tạo index để tối ưu hiệu suất
CREATE INDEX idx_orders_date ON orders(order_date);
CREATE INDEX idx_orders_status ON orders(order_status);
CREATE INDEX idx_products_category ON products(category_id);
CREATE INDEX idx_tables_area ON tables(area_id);
CREATE INDEX idx_attendance_user_date ON attendance(user_id, work_date); 