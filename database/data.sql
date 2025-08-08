-- =====================================================
-- DATA.SQL - DỮ LIỆU MẪU CHO HỆ THỐNG QUẢN LÝ CAFE
-- Mô tả: File chứa dữ liệu mẫu cho hệ thống
-- Tác giả: Team 2_C2406L
-- Ngày tạo: 31/01/2025
-- Phiên bản: 2.0.1
-- =====================================================

USE cafe_management1;

-- Tắt safe update mode để cho phép DELETE với JOIN
SET SQL_SAFE_UPDATES = 0;

-- =====================================================
-- 0. XÓA TẤT CẢ DỮ LIỆU CŨ (THEO THỨ TỰ FOREIGN KEY)
-- =====================================================

-- Xóa dữ liệu theo thứ tự để tránh foreign key constraint
-- 1. Xóa order_details trước (child của orders)
DELETE FROM order_details;

-- 2. Xóa orders (child của users, customers, tables)
DELETE FROM orders;

-- 3. Xóa attendance (child của users)
DELETE FROM attendance;

-- 4. Xóa users (child của roles)
DELETE FROM users;

-- 5. Xóa roles (parent table)
DELETE FROM roles;

-- 6. Xóa customers
DELETE FROM customers;

-- 7. Xóa tables (child của areas)
DELETE FROM tables;

-- 8. Xóa areas
DELETE FROM areas;

-- 9. Xóa products (child của categories)
DELETE FROM products;

-- 10. Xóa categories
DELETE FROM categories;

-- 11. Xóa promotions
DELETE FROM promotions;

-- 12. Xóa system_settings
DELETE FROM system_settings;

-- =====================================================
-- 1. DỮ LIỆU VAI TRÒ (ROLES) - THÊM TRƯỚC
-- =====================================================

-- Thêm 3 vai trò chính
INSERT INTO roles (role_name, description, permissions) VALUES
('admin', 'Quản trị viên hệ thống - Có toàn quyền quản lý', 
 '{"users": ["create", "read", "update", "delete"], "products": ["create", "read", "update", "delete"], "orders": ["create", "read", "update", "delete"], "reports": ["read"]}'),
('waiter', 'Nhân viên phục vụ - Phục vụ khách hàng và tạo đơn hàng', 
 '{"orders": ["create", "read", "update"], "customers": ["create", "read"], "tables": ["read", "update"]}'),
('barista', 'Nhân viên pha chế - Chuyên pha chế đồ uống', 
 '{"orders": ["read", "update"], "products": ["read"]}');

-- =====================================================
-- 2. DỮ LIỆU NGƯỜI DÙNG (USERS)
-- =====================================================

-- Thêm người dùng mẫu
INSERT INTO users (username, password, full_name, email, phone, role_id) VALUES
-- Admin
('admin', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 
 (SELECT role_id FROM roles WHERE role_name = 'admin')),

-- Waiters
('waiter1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Dương Tuấn Minh', 'waiter1@cafe.com', '0123456790', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),
('waiter2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Trần Thị Hương', 'waiter2@cafe.com', '0123456791', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),
('waiter3', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Lê Văn Nam', 'waiter3@cafe.com', '0123456792', 
 (SELECT role_id FROM roles WHERE role_name = 'waiter')),

-- Baristas
('barista1', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Dương Đức Thành', 'barista1@cafe.com', '0123456793', 
 (SELECT role_id FROM roles WHERE role_name = 'barista')),
('barista2', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', 'Phạm Thị Mai', 'barista2@cafe.com', '0123456794', 
 (SELECT role_id FROM roles WHERE role_name = 'barista'));

-- =====================================================
-- 3. DỮ LIỆU DANH MỤC (CATEGORIES)
-- =====================================================

-- Thêm danh mục sản phẩm
INSERT INTO categories (category_name, description, sort_order) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại', 1),
('Trà', 'Trà sữa, trà đá, trà nóng', 2),
('Nước ép', 'Nước ép trái cây tươi', 3),
('Bánh', 'Bánh ngọt, bánh mặn', 4),
('Snack', 'Đồ ăn nhẹ', 5);

-- =====================================================
-- 4. DỮ LIỆU KHU VỰC (AREAS)
-- =====================================================

-- Thêm khu vực
INSERT INTO areas (area_name, description, floor_number, capacity) VALUES
('Tầng 1', 'Khu vực tầng trệt - Nơi đón tiếp khách', 1, 24),
('Tầng 2', 'Khu vực tầng 2 - Không gian yên tĩnh', 2, 16),
('Sân thượng', 'Khu vực ngoài trời - View đẹp', 3, 10),
('VIP', 'Khu vực riêng tư - Dành cho khách VIP', 1, 10);

-- =====================================================
-- 5. DỮ LIỆU BÀN (TABLES)
-- =====================================================

-- Thêm bàn cho từng khu vực
INSERT INTO tables (table_name, area_id, table_number, capacity, status) VALUES
-- Tầng 1
('Bàn 1', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-01', 4, 'available'),
('Bàn 2', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-02', 4, 'available'),
('Bàn 3', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-03', 6, 'available'),
('Bàn 4', (SELECT area_id FROM areas WHERE area_name = 'Tầng 1'), 'T1-04', 6, 'available'),

-- Tầng 2
('Bàn 5', (SELECT area_id FROM areas WHERE area_name = 'Tầng 2'), 'T2-01', 4, 'available'),
('Bàn 6', (SELECT area_id FROM areas WHERE area_name = 'Tầng 2'), 'T2-02', 4, 'available'),
('Bàn 7', (SELECT area_id FROM areas WHERE area_name = 'Tầng 2'), 'T2-03', 8, 'available'),

-- Sân thượng
('Bàn 8', (SELECT area_id FROM areas WHERE area_name = 'Sân thượng'), 'ST-01', 4, 'available'),
('Bàn 9', (SELECT area_id FROM areas WHERE area_name = 'Sân thượng'), 'ST-02', 6, 'available'),

-- VIP
('Bàn VIP 1', (SELECT area_id FROM areas WHERE area_name = 'VIP'), 'VIP-01', 4, 'available'),
('Bàn VIP 2', (SELECT area_id FROM areas WHERE area_name = 'VIP'), 'VIP-02', 6, 'available');

-- =====================================================
-- 6. DỮ LIỆU SẢN PHẨM (PRODUCTS)
-- =====================================================

-- Thêm sản phẩm
INSERT INTO products (product_name, category_id, sku, price, cost_price, description, stock_quantity, min_stock_level) VALUES
-- Cà phê
('Cà phê đen', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF001', 25000, 15000, 'Cà phê đen truyền thống', 100, 20),
('Cà phê sữa', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF002', 30000, 18000, 'Cà phê sữa đặc', 80, 15),
('Cappuccino', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF003', 45000, 25000, 'Cappuccino kiểu Ý', 60, 10),
('Latte', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF004', 50000, 28000, 'Latte với sữa tươi', 70, 12),
('Americano', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF005', 35000, 20000, 'Americano đậm đà', 90, 18),
('Espresso', (SELECT category_id FROM categories WHERE category_name = 'Cà phê'), 'CF006', 20000, 12000, 'Espresso nguyên bản', 120, 25),

-- Trà
('Trà sữa trân châu', (SELECT category_id FROM categories WHERE category_name = 'Trà'), 'TR001', 35000, 20000, 'Trà sữa với trân châu', 85, 15),
('Trà đá', (SELECT category_id FROM categories WHERE category_name = 'Trà'), 'TR002', 20000, 12000, 'Trà đá mát lạnh', 110, 20),
('Trà sữa matcha', (SELECT category_id FROM categories WHERE category_name = 'Trà'), 'TR003', 40000, 22000, 'Trà sữa matcha Nhật', 65, 12),
('Trà sữa thái', (SELECT category_id FROM categories WHERE category_name = 'Trà'), 'TR004', 38000, 21000, 'Trà sữa thái đặc biệt', 75, 15),
('Trà hoa cúc', (SELECT category_id FROM categories WHERE category_name = 'Trà'), 'TR005', 25000, 15000, 'Trà hoa cúc thảo mộc', 95, 18),

-- Nước ép
('Nước cam ép', (SELECT category_id FROM categories WHERE category_name = 'Nước ép'), 'NE001', 40000, 25000, 'Nước cam tươi', 50, 10),
('Nước chanh dây', (SELECT category_id FROM categories WHERE category_name = 'Nước ép'), 'NE002', 35000, 20000, 'Nước chanh dây tươi', 45, 8),
('Nước dừa tươi', (SELECT category_id FROM categories WHERE category_name = 'Nước ép'), 'NE003', 30000, 18000, 'Nước dừa tươi mát', 60, 12),
('Nước ép táo', (SELECT category_id FROM categories WHERE category_name = 'Nước ép'), 'NE004', 45000, 28000, 'Nước ép táo tươi', 40, 8),

-- Bánh
('Bánh tiramisu', (SELECT category_id FROM categories WHERE category_name = 'Bánh'), 'BH001', 45000, 25000, 'Bánh tiramisu Ý', 30, 5),
('Bánh cheesecake', (SELECT category_id FROM categories WHERE category_name = 'Bánh'), 'BH002', 40000, 22000, 'Bánh cheesecake New York', 25, 5),
('Bánh brownie', (SELECT category_id FROM categories WHERE category_name = 'Bánh'), 'BH003', 35000, 20000, 'Bánh brownie chocolate', 35, 8),
('Bánh croissant', (SELECT category_id FROM categories WHERE category_name = 'Bánh'), 'BH004', 25000, 15000, 'Bánh croissant Pháp', 40, 10),

-- Snack
('Khoai tây chiên', (SELECT category_id FROM categories WHERE category_name = 'Snack'), 'SN001', 25000, 15000, 'Khoai tây chiên giòn', 80, 15),
('Gà rán', (SELECT category_id FROM categories WHERE category_name = 'Snack'), 'SN002', 35000, 20000, 'Gà rán giòn', 60, 12),
('Bánh mì sandwich', (SELECT category_id FROM categories WHERE category_name = 'Snack'), 'SN003', 30000, 18000, 'Bánh mì sandwich thịt nguội', 70, 15),
('Salad trộn', (SELECT category_id FROM categories WHERE category_name = 'Snack'), 'SN004', 40000, 25000, 'Salad rau củ tươi', 45, 10);

-- =====================================================
-- 7. DỮ LIỆU KHÁCH HÀNG (CUSTOMERS)
-- =====================================================

-- Thêm khách hàng mẫu
INSERT INTO customers (customer_code, full_name, phone, email, address, loyalty_points, total_spent) VALUES
('KH001', 'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', '123 Đường ABC, Quận 1, TP.HCM', 150, 1500000),
('KH002', 'Trần Thị B', '0901234568', 'tranthib@gmail.com', '456 Đường XYZ, Quận 3, TP.HCM', 200, 2000000),
('KH003', 'Lê Văn C', '0901234569', 'levanc@gmail.com', '789 Đường DEF, Quận 7, TP.HCM', 100, 1000000),
('KH004', 'Phạm Thị D', '0901234570', 'phamthid@gmail.com', '321 Đường GHI, Quận 2, TP.HCM', 300, 3000000),
('KH005', 'Hoàng Văn E', '0901234571', 'hoangvane@gmail.com', '654 Đường JKL, Quận 5, TP.HCM', 80, 800000),
('KH006', 'Vũ Thị F', '0901234572', 'vuthif@gmail.com', '987 Đường MNO, Quận 10, TP.HCM', 120, 1200000),
('KH007', 'Đặng Văn G', '0901234573', 'dangvang@gmail.com', '147 Đường PQR, Quận 11, TP.HCM', 90, 900000),
('KH008', 'Bùi Thị H', '0901234574', 'buithih@gmail.com', '258 Đường STU, Quận 4, TP.HCM', 180, 1800000);

-- =====================================================
-- 8. DỮ LIỆU KHUYẾN MÃI (PROMOTIONS)
-- =====================================================

-- Thêm khuyến mãi
INSERT INTO promotions (promotion_code, promotion_name, description, discount_type, discount_value, min_order_amount, start_date, end_date) VALUES
('SUMMER2025', 'Khuyến mãi mùa hè', 'Giảm giá 20% cho tất cả đồ uống', 'percentage', 20.00, 100000, '2025-06-01', '2025-08-31'),
('BIRTHDAY2025', 'Giảm giá sinh nhật', 'Giảm 50.000đ cho khách hàng trong tháng sinh nhật', 'fixed_amount', 50000.00, 200000, '2025-01-01', '2025-12-31'),
('WEEKEND2025', 'Khuyến mãi cuối tuần', 'Giảm 15% cho đơn hàng từ 300.000đ', 'percentage', 15.00, 300000, '2025-01-01', '2025-12-31'),
('COMBO2025', 'Khuyến mãi combo', 'Giảm 30.000đ khi mua combo cà phê + bánh', 'fixed_amount', 30000.00, 150000, '2025-01-01', '2025-12-31');

-- =====================================================
-- 9. DỮ LIỆU ĐƠN HÀNG MẪU (ORDERS)
-- =====================================================

-- Thêm đơn hàng mẫu
INSERT INTO orders (order_number, table_id, customer_id, user_id, total_amount, discount_amount, tax_amount, final_amount, payment_method, payment_status, order_status, notes) VALUES
('ORD001', (SELECT table_id FROM tables WHERE table_name = 'Bàn 1'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Nguyễn Văn A'),
 (SELECT user_id FROM users WHERE username = 'waiter1'),
 120000, 0, 12000, 132000, 'cash', 'paid', 'completed', 'Khách hàng thân thiết'),

('ORD002', (SELECT table_id FROM tables WHERE table_name = 'Bàn 2'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Trần Thị B'),
 (SELECT user_id FROM users WHERE username = 'waiter2'),
 180000, 36000, 14400, 158400, 'momo', 'paid', 'completed', 'Áp dụng khuyến mãi mùa hè'),

('ORD003', (SELECT table_id FROM tables WHERE table_name = 'Bàn VIP 1'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Lê Văn C'),
 (SELECT user_id FROM users WHERE username = 'waiter1'),
 250000, 0, 25000, 275000, 'card', 'paid', 'completed', 'Khách VIP'),

('ORD004', (SELECT table_id FROM tables WHERE table_name = 'Bàn 5'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Phạm Thị D'),
 (SELECT user_id FROM users WHERE username = 'waiter3'),
 95000, 0, 9500, 104500, 'cash', 'paid', 'completed', NULL),

('ORD005', (SELECT table_id FROM tables WHERE table_name = 'Bàn 8'), 
 (SELECT customer_id FROM customers WHERE full_name = 'Hoàng Văn E'),
 (SELECT user_id FROM users WHERE username = 'waiter2'),
 160000, 24000, 13600, 149600, 'vnpay', 'paid', 'completed', 'Áp dụng khuyến mãi cuối tuần');

-- =====================================================
-- 10. DỮ LIỆU CHI TIẾT ĐƠN HÀNG (ORDER_DETAILS)
-- =====================================================

-- Tạm thời vô hiệu hóa trigger để tránh lỗi Error Code: 1442
-- (Trigger tr_order_details_insert cố gắng cập nhật bảng products trong khi đang insert order_details)
-- Trong MySQL, chúng ta cần drop và recreate trigger
DROP TRIGGER IF EXISTS tr_order_details_insert;

-- Thêm chi tiết đơn hàng
INSERT INTO order_details (order_id, product_id, quantity, unit_price, discount_percent, total_price, notes) VALUES
-- ORD001
((SELECT order_id FROM orders WHERE order_number = 'ORD001'), 
 (SELECT product_id FROM products WHERE product_name = 'Cà phê sữa'), 2, 30000, 0.00, 60000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD001'), 
 (SELECT product_id FROM products WHERE product_name = 'Bánh tiramisu'), 1, 45000, 0.00, 45000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD001'), 
 (SELECT product_id FROM products WHERE product_name = 'Khoai tây chiên'), 1, 25000, 0.00, 25000, NULL),

-- ORD002
((SELECT order_id FROM orders WHERE order_number = 'ORD002'), 
 (SELECT product_id FROM products WHERE product_name = 'Latte'), 2, 50000, 0.00, 100000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD002'), 
 (SELECT product_id FROM products WHERE product_name = 'Bánh cheesecake'), 1, 40000, 0.00, 40000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD002'), 
 (SELECT product_id FROM products WHERE product_name = 'Nước cam ép'), 1, 40000, 0.00, 40000, NULL),

-- ORD003
((SELECT order_id FROM orders WHERE order_number = 'ORD003'), 
 (SELECT product_id FROM products WHERE product_name = 'Cappuccino'), 2, 45000, 0.00, 90000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD003'), 
 (SELECT product_id FROM products WHERE product_name = 'Trà sữa matcha'), 1, 40000, 0.00, 40000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD003'), 
 (SELECT product_id FROM products WHERE product_name = 'Bánh brownie'), 2, 35000, 0.00, 70000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD003'), 
 (SELECT product_id FROM products WHERE product_name = 'Gà rán'), 1, 35000, 0.00, 35000, NULL),

-- ORD004
((SELECT order_id FROM orders WHERE order_number = 'ORD004'), 
 (SELECT product_id FROM products WHERE product_name = 'Cà phê đen'), 1, 25000, 0.00, 25000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD004'), 
 (SELECT product_id FROM products WHERE product_name = 'Bánh croissant'), 2, 25000, 0.00, 50000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD004'), 
 (SELECT product_id FROM products WHERE product_name = 'Trà đá'), 1, 20000, 0.00, 20000, NULL),

-- ORD005
((SELECT order_id FROM orders WHERE order_number = 'ORD005'), 
 (SELECT product_id FROM products WHERE product_name = 'Americano'), 2, 35000, 0.00, 70000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD005'), 
 (SELECT product_id FROM products WHERE product_name = 'Bánh tiramisu'), 1, 45000, 0.00, 45000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD005'), 
 (SELECT product_id FROM products WHERE product_name = 'Nước chanh dây'), 1, 35000, 0.00, 35000, NULL),
((SELECT order_id FROM orders WHERE order_number = 'ORD005'), 
 (SELECT product_id FROM products WHERE product_name = 'Salad trộn'), 1, 40000, 0.00, 40000, NULL);

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

-- =====================================================
-- 11. DỮ LIỆU CHẤM CÔNG (ATTENDANCE)
-- =====================================================

-- Thêm dữ liệu chấm công mẫu (cho ngày hôm nay)
INSERT INTO attendance (user_id, check_in, check_out, work_date, total_hours, status, notes) VALUES
((SELECT user_id FROM users WHERE username = 'waiter1'), 
 '2025-01-31 08:00:00', '2025-01-31 17:00:00', '2025-01-31', 9.00, 'present', 'Ca sáng'),
((SELECT user_id FROM users WHERE username = 'waiter2'), 
 '2025-01-31 14:00:00', '2025-01-31 22:00:00', '2025-01-31', 8.00, 'present', 'Ca chiều'),
((SELECT user_id FROM users WHERE username = 'barista1'), 
 '2025-01-31 07:00:00', '2025-01-31 16:00:00', '2025-01-31', 9.00, 'present', 'Ca sáng'),
((SELECT user_id FROM users WHERE username = 'barista2'), 
 '2025-01-31 13:00:00', '2025-01-31 21:00:00', '2025-01-31', 8.00, 'present', 'Ca chiều');

-- =====================================================
-- 12. DỮ LIỆU CÀI ĐẶT HỆ THỐNG (SYSTEM_SETTINGS)
-- =====================================================

-- Thêm cài đặt hệ thống
INSERT INTO system_settings (setting_key, setting_value, setting_type, description) VALUES
('cafe_name', 'Cafe Project2', 'string', 'Tên quán cafe'),
('cafe_address', '123 Đường ABC, Quận XYZ, TP.HCM', 'string', 'Địa chỉ quán'),
('cafe_phone', '0123456789', 'string', 'Số điện thoại quán'),
('tax_rate', '10', 'number', 'Thuế suất (%)'),
('currency', 'VND', 'string', 'Đơn vị tiền tệ'),
('receipt_footer', 'Cảm ơn quý khách đã sử dụng dịch vụ!', 'string', 'Chữ ký cuối hóa đơn'),
('opening_hours', '07:00-22:00', 'string', 'Giờ mở cửa'),
('max_table_capacity', '8', 'number', 'Sức chứa tối đa của bàn'),
('loyalty_points_rate', '1', 'number', 'Tỷ lệ tích điểm (1 điểm = 10.000đ)');

-- =====================================================
-- THÔNG BÁO HOÀN THÀNH
-- =====================================================

SELECT 'Dữ liệu mẫu đã được thêm thành công!' AS message;
SELECT COUNT(*) AS total_roles FROM roles;
SELECT COUNT(*) AS total_users FROM users;
SELECT COUNT(*) AS total_categories FROM categories;
SELECT COUNT(*) AS total_products FROM products;
SELECT COUNT(*) AS total_tables FROM tables;
SELECT COUNT(*) AS total_customers FROM customers;
SELECT COUNT(*) AS total_orders FROM orders;
SELECT COUNT(*) AS total_promotions FROM promotions;
SELECT COUNT(*) AS total_attendance FROM attendance;
SELECT COUNT(*) AS total_settings FROM system_settings;

-- Bật lại safe update mode để đảm bảo an toàn
SET SQL_SAFE_UPDATES = 1; 