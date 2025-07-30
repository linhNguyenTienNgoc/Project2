-- Script setup toàn bộ database CoffeeShopManagement
-- Chạy script này trong SQL Server Management Studio

USE CoffeeShopManagement;

-- 1. Thêm bàn còn thiếu (T06-T10)
INSERT INTO CoffeeTable (table_number, capacity, status, location, active, created_at, updated_at)
VALUES 
('T06', 4, 'AVAILABLE', 'Khu vực A - Gần cửa sổ', 1, GETDATE(), GETDATE()),
('T07', 4, 'RESERVED', 'Khu vực B - Giữa quán', 1, GETDATE(), GETDATE()),
('T08', 6, 'AVAILABLE', 'Khu vực C - Góc yên tĩnh', 1, GETDATE(), GETDATE()),
('T09', 4, 'AVAILABLE', 'Khu vực A - Gần cửa sổ', 1, GETDATE(), GETDATE()),
('T10', 8, 'AVAILABLE', 'Khu vực C - Góc yên tĩnh', 1, GETDATE(), GETDATE());

-- 2. Cập nhật trạng thái bàn T05 thành OCCUPIED
UPDATE CoffeeTable 
SET status = 'OCCUPIED', updated_at = GETDATE()
WHERE table_number = 'T05';

-- 3. Thêm nhân viên mẫu (nếu chưa có)
IF NOT EXISTS (SELECT 1 FROM Employee WHERE username = 'admin')
BEGIN
    INSERT INTO Employee (username, password, name, email, phone, role, active, created_at, updated_at)
    VALUES ('admin', 'admin123', 'Nguyễn Văn Admin', 'admin@coffeeshop.com', '0901234567', 'ADMIN', 1, GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM Employee WHERE username = 'staff1')
BEGIN
    INSERT INTO Employee (username, password, name, email, phone, role, active, created_at, updated_at)
    VALUES ('staff1', 'staff123', 'Trần Thị Nhân Viên', 'staff1@coffeeshop.com', '0901234568', 'STAFF', 1, GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM Employee WHERE username = 'manager')
BEGIN
    INSERT INTO Employee (username, password, name, email, phone, role, active, created_at, updated_at)
    VALUES ('manager', 'manager123', 'Phạm Thị Quản Lý', 'manager@coffeeshop.com', '0901234570', 'ADMIN', 1, GETDATE(), GETDATE());
END

-- 4. Thêm menu items mẫu (nếu chưa có)
IF NOT EXISTS (SELECT 1 FROM Menu WHERE name = 'Cà phê đen')
BEGIN
    INSERT INTO Menu (name, description, price, category, available, image_url, created_at, updated_at)
    VALUES 
    -- Coffee
    ('Cà phê đen', 'Cà phê đen truyền thống, đậm đà hương vị', 25000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    ('Cà phê sữa', 'Cà phê sữa đặc, ngọt ngào', 30000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    ('Cappuccino', 'Cappuccino Ý, bọt sữa mịn', 45000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    ('Latte', 'Latte mượt mà, hương vị nhẹ nhàng', 50000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    ('Espresso', 'Espresso đậm đà, tinh khiết', 35000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    ('Cà phê đá xay', 'Cà phê đá xay mát lạnh', 55000, 'COFFEE', 1, NULL, GETDATE(), GETDATE()),
    
    -- Tea
    ('Trà sữa trân châu', 'Trà sữa trân châu đường đen', 35000, 'TEA', 1, NULL, GETDATE(), GETDATE()),
    ('Trà đào', 'Trà đào mát lạnh, thơm ngon', 30000, 'TEA', 1, NULL, GETDATE(), GETDATE()),
    ('Trà chanh', 'Trà chanh tươi, giải nhiệt', 25000, 'TEA', 1, NULL, GETDATE(), GETDATE()),
    
    -- Juice
    ('Nước ép cam', 'Nước ép cam tươi, giàu vitamin C', 40000, 'JUICE', 1, NULL, GETDATE(), GETDATE()),
    ('Nước ép táo', 'Nước ép táo tươi, ngọt tự nhiên', 45000, 'JUICE', 1, NULL, GETDATE(), GETDATE()),
    
    -- Dessert
    ('Bánh tiramisu', 'Bánh tiramisu Ý, hương vị đặc biệt', 55000, 'DESSERT', 1, NULL, GETDATE(), GETDATE()),
    ('Bánh cheesecake', 'Cheesecake mịn màng, béo ngậy', 50000, 'DESSERT', 1, NULL, GETDATE(), GETDATE()),
    ('Bánh chocolate', 'Bánh chocolate đậm đà', 45000, 'DESSERT', 1, NULL, GETDATE(), GETDATE()),
    ('Kem vanilla', 'Kem vanilla mát lạnh', 30000, 'DESSERT', 1, NULL, GETDATE(), GETDATE()),
    ('Kem chocolate', 'Kem chocolate đậm đà', 35000, 'DESSERT', 1, NULL, GETDATE(), GETDATE()),
    
    -- Food
    ('Bánh mì sandwich', 'Bánh mì sandwich thịt nguội', 35000, 'FOOD', 1, NULL, GETDATE(), GETDATE()),
    ('Bánh mì bơ', 'Bánh mì bơ thơm béo', 25000, 'FOOD', 1, NULL, GETDATE(), GETDATE()),
    
    -- Smoothie
    ('Sinh tố dâu', 'Sinh tố dâu tươi, mát lạnh', 40000, 'SMOOTHIE', 1, NULL, GETDATE(), GETDATE()),
    ('Sinh tố xoài', 'Sinh tố xoài chín, ngọt tự nhiên', 45000, 'SMOOTHIE', 1, NULL, GETDATE(), GETDATE());
END

-- 5. Kiểm tra kết quả
PRINT '=== KẾT QUẢ SETUP DATABASE ===';
PRINT '';

SELECT 'Employee' as TableName, COUNT(*) as Count FROM Employee WHERE active = 1
UNION ALL
SELECT 'Menu' as TableName, COUNT(*) as Count FROM Menu WHERE available = 1
UNION ALL
SELECT 'CoffeeTable' as TableName, COUNT(*) as Count FROM CoffeeTable WHERE active = 1;

PRINT '';
PRINT '=== CHI TIẾT BÀN ===';
SELECT table_number, capacity, status, location FROM CoffeeTable WHERE active = 1 ORDER BY table_number;

PRINT '';
PRINT '=== CHI TIẾT MENU ===';
SELECT name, price, category FROM Menu WHERE available = 1 ORDER BY category, name;

PRINT '';
PRINT '=== CHI TIẾT NHÂN VIÊN ===';
SELECT username, name, role FROM Employee WHERE active = 1 ORDER BY role, name; 