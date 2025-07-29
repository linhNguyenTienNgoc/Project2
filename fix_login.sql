-- Fix Login cho Coffee Shop Management
USE master;
GO

-- Kiểm tra và tạo database nếu chưa có
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'CoffeeShopManagement')
BEGIN
    CREATE DATABASE CoffeeShopManagement;
    PRINT 'Database CoffeeShopManagement created successfully!';
END
ELSE
BEGIN
    PRINT 'Database CoffeeShopManagement already exists!';
END
GO

USE CoffeeShopManagement;
GO

-- Xóa bảng cũ nếu có để tạo lại sạch
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'InvoiceDetail')
    DROP TABLE InvoiceDetail;
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Invoice')
    DROP TABLE Invoice;
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Menu')
    DROP TABLE Menu;
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'CoffeeTable')
    DROP TABLE CoffeeTable;
IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Employee')
    DROP TABLE Employee;
GO

-- Tạo bảng Employee với cấu trúc đúng
CREATE TABLE Employee (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'STAFF',
    active BIT DEFAULT 1,
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
PRINT 'Table Employee created successfully!';

-- Tạo bảng CoffeeTable
CREATE TABLE CoffeeTable (
    id INT IDENTITY(1,1) PRIMARY KEY,
    table_number VARCHAR(20) UNIQUE NOT NULL,
    capacity INT NOT NULL DEFAULT 4,
    status VARCHAR(20) DEFAULT 'AVAILABLE',
    location NVARCHAR(200),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
PRINT 'Table CoffeeTable created successfully!';

-- Tạo bảng Menu
CREATE TABLE Menu (
    id INT IDENTITY(1,1) PRIMARY KEY,
    name NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    category VARCHAR(50) NOT NULL,
    available BIT DEFAULT 1,
    image_url VARCHAR(255),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE()
);
PRINT 'Table Menu created successfully!';

-- Tạo bảng Invoice
CREATE TABLE Invoice (
    id INT IDENTITY(1,1) PRIMARY KEY,
    invoice_number VARCHAR(50) UNIQUE NOT NULL,
    table_id INT,
    employee_id INT,
    order_time DATETIME DEFAULT GETDATE(),
    payment_time DATETIME,
    status VARCHAR(20) DEFAULT 'PENDING',
    total_amount DECIMAL(10,2) DEFAULT 0,
    payment_method VARCHAR(50),
    notes NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE(),
    updated_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (table_id) REFERENCES CoffeeTable(id),
    FOREIGN KEY (employee_id) REFERENCES Employee(id)
);
PRINT 'Table Invoice created successfully!';

-- Tạo bảng InvoiceDetail
CREATE TABLE InvoiceDetail (
    id INT IDENTITY(1,1) PRIMARY KEY,
    invoice_id INT NOT NULL,
    menu_id INT NOT NULL,
    quantity INT NOT NULL DEFAULT 1,
    unit_price DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    notes NVARCHAR(200),
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (invoice_id) REFERENCES Invoice(id) ON DELETE CASCADE,
    FOREIGN KEY (menu_id) REFERENCES Menu(id)
);
PRINT 'Table InvoiceDetail created successfully!';

-- Insert dữ liệu mẫu - ĐẢM BẢO TÀI KHOẢN ADMIN ĐÚNG
INSERT INTO Employee (username, password, name, email, phone, role, active) VALUES
('admin', 'admin123', N'Nguyễn Văn Admin', 'admin@coffeeshop.com', '0901234567', 'ADMIN', 1),
('staff1', 'staff123', N'Trần Thị Nhân Viên', 'staff1@coffeeshop.com', '0901234568', 'STAFF', 1),
('staff2', 'staff123', N'Lê Văn Phục Vụ', 'staff2@coffeeshop.com', '0901234569', 'STAFF', 1),
('manager', 'manager123', N'Phạm Thị Quản Lý', 'manager@coffeeshop.com', '0901234570', 'ADMIN', 1);
PRINT 'Sample employees inserted successfully!';

-- Coffee Tables
INSERT INTO CoffeeTable (table_number, capacity, status, location) VALUES
('T01', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
('T02', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
('T03', 6, 'AVAILABLE', N'Khu vực B - Giữa quán'),
('T04', 6, 'AVAILABLE', N'Khu vực B - Giữa quán'),
('T05', 8, 'AVAILABLE', N'Khu vực C - Góc yên tĩnh');
PRINT 'Sample coffee tables inserted successfully!';

-- Menu Items
INSERT INTO Menu (name, description, price, category, available, image_url) VALUES
(N'Cà phê đen', N'Cà phê đen truyền thống, đậm đà hương vị', 25000, 'COFFEE', 1, '/images/coffee-black.jpg'),
(N'Cà phê sữa', N'Cà phê sữa đặc, ngọt ngào', 30000, 'COFFEE', 1, '/images/coffee-milk.jpg'),
(N'Cappuccino', N'Cappuccino Ý, bọt sữa mịn', 45000, 'COFFEE', 1, '/images/cappuccino.jpg'),
(N'Latte', N'Latte mượt mà, hương vị nhẹ nhàng', 50000, 'COFFEE', 1, '/images/latte.jpg'),
(N'Espresso', N'Espresso đậm đà, tinh khiết', 35000, 'COFFEE', 1, '/images/espresso.jpg'),
(N'Trà sữa trân châu', N'Trà sữa trân châu đường đen', 35000, 'TEA', 1, '/images/bubble-tea.jpg'),
(N'Trà đào', N'Trà đào mát lạnh, thơm ngon', 30000, 'TEA', 1, '/images/peach-tea.jpg'),
(N'Trà chanh', N'Trà chanh tươi, giải nhiệt', 25000, 'TEA', 1, '/images/lemon-tea.jpg'),
(N'Nước ép cam', N'Nước ép cam tươi, giàu vitamin C', 40000, 'JUICE', 1, '/images/orange-juice.jpg'),
(N'Nước ép táo', N'Nước ép táo tươi, ngọt tự nhiên', 45000, 'JUICE', 1, '/images/apple-juice.jpg'),
(N'Bánh tiramisu', N'Bánh tiramisu Ý, hương vị đặc biệt', 55000, 'DESSERT', 1, '/images/tiramisu.jpg'),
(N'Bánh cheesecake', N'Cheesecake mịn màng, béo ngậy', 50000, 'DESSERT', 1, '/images/cheesecake.jpg'),
(N'Bánh chocolate', N'Bánh chocolate đậm đà', 45000, 'DESSERT', 1, '/images/chocolate-cake.jpg'),
(N'Bánh mì sandwich', N'Bánh mì sandwich thịt nguội', 35000, 'FOOD', 1, '/images/sandwich.jpg'),
(N'Bánh mì bơ', N'Bánh mì bơ thơm béo', 25000, 'FOOD', 1, '/images/butter-bread.jpg');
PRINT 'Sample menu items inserted successfully!';

-- Kiểm tra dữ liệu
SELECT 'Employee' as TableName, COUNT(*) as Count FROM Employee
UNION ALL
SELECT 'CoffeeTable', COUNT(*) FROM CoffeeTable
UNION ALL
SELECT 'Menu', COUNT(*) FROM Menu;

-- Kiểm tra tài khoản admin cụ thể
SELECT 
    'ADMIN ACCOUNT CHECK' as Info,
    id,
    username,
    password,
    role,
    active,
    name
FROM Employee 
WHERE username = 'admin';

PRINT '========================================';
PRINT 'LOGIN FIX COMPLETED!';
PRINT '========================================';
PRINT 'Database: CoffeeShopManagement';
PRINT 'Admin account: admin/admin123';
PRINT 'Staff account: staff1/staff123';
PRINT 'Manager account: manager/manager123';
PRINT '========================================'; 