-- =====================================================
-- CAFE MANAGEMENT SAMPLE DATA
-- File: database/cafe_sample_data.sql
-- Purpose: Dữ liệu mẫu cho hệ thống quản lý quán café
-- Author: Team 2_C2406L
-- Version: 2.1.0 (Optimized Data)
-- =====================================================

USE cafe_management;

-- =====================================================
-- DISABLE SAFETY CHECKS
-- =====================================================
SET SQL_SAFE_UPDATES = 0;
SET FOREIGN_KEY_CHECKS = 0;

-- =====================================================
-- INSERT USERS (ONLY ADMIN AND STAFF)
-- =====================================================

INSERT INTO users (username, password, full_name, email, phone, role) VALUES
-- Admin users
('admin', '123456', 'Nguyễn Tiến Ngọc Linh', 'admin@cafe.com', '0123456789', 'Admin'),
('manager', '123456', 'Trần Xuân Quang Minh', 'manager@cafe.com', '0123456790', 'Admin'),

-- Staff users
('staff01', '123456', 'Vũ Hoàng Nam', 'nam.vu@cafe.com', '0123456791', 'Staff'),
('staff02', '123456', 'Dương Tuấn Minh', 'minh.duong@cafe.com', '0123456792', 'Staff'),
('staff03', '123456', 'Dương Đức Thành', 'thanh.duong@cafe.com', '0123456793', 'Staff'),
('staff04', '123456', 'Nguyễn Thị Hoa', 'hoa.nguyen@cafe.com', '0123456794', 'Staff'),
('staff05', '123456', 'Lê Văn Đức', 'duc.le@cafe.com', '0123456795', 'Staff'),
('staff06', '123456', 'Phạm Thị Mai', 'mai.pham@cafe.com', '0123456796', 'Staff');

-- =====================================================
-- INSERT CATEGORIES
-- =====================================================

INSERT INTO categories (category_name, description, display_order) VALUES
('Cà phê', 'Các loại cà phê truyền thống và hiện đại', 1),
('Trà & Trà sữa', 'Trà sữa, trà đá, trà nóng các loại', 2),
('Nước ép & Sinh tố', 'Nước ép trái cây tươi và sinh tố', 3),
('Bánh ngọt', 'Bánh ngọt, bánh kem, dessert', 4),
('Đồ uống đá xay', 'Smoothies, frappuccino', 5);

-- =====================================================
-- INSERT PRODUCTS
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
('Cold Brew', 1, 42000, 24000, 'Cold brew ủ lạnh 12 tiếng', TRUE);

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



-- Đồ uống đá xay (category_id = 5)
INSERT INTO products (product_name, category_id, price, cost_price, description, is_available) VALUES
('Frappuccino', 5, 58000, 33000, 'Frappuccino cà phê đá xay', TRUE),
('Chocolate smoothie', 5, 52000, 29000, 'Chocolate smoothie đá xay', TRUE),
('Oreo smoothie', 5, 55000, 31000, 'Oreo smoothie bánh quy', TRUE),
('Matcha frappé', 5, 60000, 34000, 'Matcha frappé Nhật Bản', TRUE),
('Coconut smoothie', 5, 48000, 27000, 'Coconut smoothie dừa tươi', TRUE);

-- =====================================================
-- INSERT AREAS
-- =====================================================

INSERT INTO areas (area_name, description) VALUES
('Tầng trệt', 'Khu vực tầng trệt - không gian mở'),
('Tầng 2', 'Khu vực tầng 2 - yên tĩnh'),
('Sân thượng', 'Khu vực ngoài trời - view đẹp'),
('VIP', 'Phòng riêng cao cấp'),
('Quầy bar', 'Khu vực quầy bar');

-- =====================================================
-- INSERT TABLES
-- =====================================================

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
-- INSERT CUSTOMERS
-- =====================================================

INSERT INTO customers (full_name, phone, email, address, loyalty_points, total_spent) VALUES
('Nguyễn Văn An', '0901234567', 'an.nguyen@email.com', '123 Đường ABC, Quận 1, TP.HCM', 0, 0),
('Trần Thị Bình', '0912345678', 'binh.tran@email.com', '456 Đường DEF, Quận 2, TP.HCM', 0, 0),
('Lê Hoàng Cường', '0923456789', 'cuong.le@email.com', '789 Đường GHI, Quận 3, TP.HCM', 0, 0),
('Phạm Thị Dung', '0934567890', 'dung.pham@email.com', '321 Đường JKL, Quận 4, TP.HCM', 0, 0),
('Vũ Minh Quân', '0945678901', 'quan.vu@email.com', '654 Đường MNO, Quận 5, TP.HCM', 0, 0),
('Đỗ Thị Hương', '0956789012', 'huong.do@email.com', '987 Đường PQR, Quận 6, TP.HCM', 0, 0),
('Bùi Văn Nam', '0967890123', 'nam.bui@email.com', '147 Đường STU, Quận 7, TP.HCM', 0, 0),
('Ngô Thị Lan', '0978901234', 'lan.ngo@email.com', '258 Đường VWX, Quận 8, TP.HCM', 0, 0),
('Hoàng Văn Tùng', '0989012345', 'tung.hoang@email.com', '369 Đường YZ, Quận 9, TP.HCM', 0, 0),
('Cao Thị Linh', '0990123456', 'linh.cao@email.com', '741 Đường ABC, Quận 10, TP.HCM', 0, 0);

-- =====================================================
-- INSERT PROMOTIONS
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
-- INSERT SAMPLE ORDERS (REMOVED TO AVOID CONSTRAINT CONFLICTS)
-- =====================================================

-- Orders will be created dynamically by the application
-- This prevents constraint violations during database setup

-- =====================================================
-- INSERT ORDER DETAILS (REMOVED - DEPENDS ON ORDERS)
-- =====================================================

-- Order details will be created dynamically when orders are created
-- This prevents foreign key constraint violations

-- =====================================================
-- INSERT ORDER PROMOTIONS (REMOVED - DEPENDS ON ORDERS)
-- =====================================================

-- Order promotions will be created dynamically when orders are created
-- This prevents foreign key constraint violations

-- =====================================================
-- INSERT SYSTEM SETTINGS
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
-- UPDATE DATA CONSISTENCY
-- =====================================================

-- Update promotion usage counts
UPDATE promotions SET usage_count = (
    SELECT COUNT(*) FROM order_promotions op WHERE op.promotion_id = promotions.promotion_id
);

-- Customer statistics will be updated when orders are created
-- Table status will be managed by triggers and stored procedures

-- =====================================================
-- ENABLE SAFETY CHECKS
-- =====================================================

SET SQL_SAFE_UPDATES = 1;
SET FOREIGN_KEY_CHECKS = 1;

-- =====================================================
-- VERIFICATION AND SUMMARY
-- =====================================================

-- Show database summary
SELECT 
    'SAMPLE DATA INSERTED SUCCESSFULLY!' AS status,
    NOW() AS created_at;

-- Show record counts (excluding orders that will be created dynamically)
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
SELECT 'promotions', COUNT(*) FROM promotions
UNION ALL
SELECT 'system_settings', COUNT(*) FROM system_settings
ORDER BY table_name;

-- Show table status distribution
SELECT 
    'Table Status Distribution:' AS info;
SELECT 
    status,
    COUNT(*) AS count,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM tables), 2) AS percentage
FROM tables 
GROUP BY status
ORDER BY status;

-- Show user roles
SELECT 
    'User Roles:' AS info;
SELECT 
    role,
    COUNT(*) AS count
FROM users 
GROUP BY role;

-- Sales summary will be available when orders are created
SELECT 
    'Sales Summary:' AS info;
SELECT 
    'No orders yet - will be created by application' AS status;

SELECT 
    '🎉 CAFE SAMPLE DATA SETUP COMPLETED!' AS message,
    'Database ready for JavaFX Application' AS status,
    NOW() AS timestamp;