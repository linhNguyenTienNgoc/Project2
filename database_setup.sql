-- Coffee Shop Management System - Database Setup
-- SQL Server Database Creation and Sample Data

-- Create Database
IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'CoffeeShopManagement')
BEGIN
    CREATE DATABASE CoffeeShopManagement;
END
GO

USE CoffeeShopManagement;
GO

-- Create Tables
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Employee')
BEGIN
    CREATE TABLE Employee (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        name NVARCHAR(100) NOT NULL,
        email VARCHAR(100),
        phone VARCHAR(20),
        role VARCHAR(20) NOT NULL DEFAULT 'STAFF',
        active BIT DEFAULT 1,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'CoffeeTable')
BEGIN
    CREATE TABLE CoffeeTable (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        table_number VARCHAR(20) UNIQUE NOT NULL,
        capacity INT NOT NULL DEFAULT 4,
        status VARCHAR(20) DEFAULT 'AVAILABLE',
        location NVARCHAR(100),
        active BIT DEFAULT 1,
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Menu')
BEGIN
    CREATE TABLE Menu (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        name NVARCHAR(100) NOT NULL,
        description NVARCHAR(500),
        price DECIMAL(10,2) NOT NULL,
        category VARCHAR(50) NOT NULL,
        available BIT DEFAULT 1,
        image_url VARCHAR(255),
        created_at DATETIME DEFAULT GETDATE(),
        updated_at DATETIME DEFAULT GETDATE()
    );
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Invoice')
BEGIN
    CREATE TABLE Invoice (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        invoice_number VARCHAR(50) UNIQUE NOT NULL,
        table_id BIGINT,
        employee_id BIGINT,
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
END
GO

IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'InvoiceDetail')
BEGIN
    CREATE TABLE InvoiceDetail (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        invoice_id BIGINT NOT NULL,
        menu_id BIGINT NOT NULL,
        quantity INT NOT NULL DEFAULT 1,
        unit_price DECIMAL(10,2) NOT NULL,
        total_price DECIMAL(10,2) NOT NULL,
        notes NVARCHAR(200),
        created_at DATETIME DEFAULT GETDATE(),
        FOREIGN KEY (invoice_id) REFERENCES Invoice(id) ON DELETE CASCADE,
        FOREIGN KEY (menu_id) REFERENCES Menu(id)
    );
END
GO

-- Insert Sample Data

-- Employees
IF NOT EXISTS (SELECT * FROM Employee WHERE username = 'admin')
BEGIN
    INSERT INTO Employee (username, password, name, email, phone, role, active) VALUES
    ('admin', 'admin123', N'Nguyễn Văn Admin', 'admin@coffeeshop.com', '0901234567', 'ADMIN', 1),
    ('staff1', 'staff123', N'Trần Thị Nhân Viên', 'staff1@coffeeshop.com', '0901234568', 'STAFF', 1),
    ('staff2', 'staff123', N'Lê Văn Phục Vụ', 'staff2@coffeeshop.com', '0901234569', 'STAFF', 1),
    ('manager', 'manager123', N'Phạm Thị Quản Lý', 'manager@coffeeshop.com', '0901234570', 'ADMIN', 1);
END
GO

-- Coffee Tables
IF NOT EXISTS (SELECT * FROM CoffeeTable WHERE table_number = 'T01')
BEGIN
    INSERT INTO CoffeeTable (table_number, capacity, status, location) VALUES
    ('T01', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
    ('T02', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
    ('T03', 6, 'AVAILABLE', N'Khu vực B - Giữa quán'),
    ('T04', 6, 'AVAILABLE', N'Khu vực B - Giữa quán'),
    ('T05', 8, 'AVAILABLE', N'Khu vực C - Góc yên tĩnh'),
    ('T06', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
    ('T07', 4, 'AVAILABLE', N'Khu vực B - Giữa quán'),
    ('T08', 6, 'AVAILABLE', N'Khu vực C - Góc yên tĩnh'),
    ('T09', 4, 'AVAILABLE', N'Khu vực A - Gần cửa sổ'),
    ('T10', 8, 'AVAILABLE', N'Khu vực C - Góc yên tĩnh');
END
GO

-- Menu Items
IF NOT EXISTS (SELECT * FROM Menu WHERE name = N'Cà phê đen')
BEGIN
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
    (N'Bánh mì bơ', N'Bánh mì bơ thơm béo', 25000, 'FOOD', 1, '/images/butter-bread.jpg'),
    (N'Kem vanilla', N'Kem vanilla mát lạnh', 30000, 'DESSERT', 1, '/images/vanilla-ice-cream.jpg'),
    (N'Kem chocolate', N'Kem chocolate đậm đà', 35000, 'DESSERT', 1, '/images/chocolate-ice-cream.jpg'),
    (N'Sinh tố dâu', N'Sinh tố dâu tươi, mát lạnh', 40000, 'SMOOTHIE', 1, '/images/strawberry-smoothie.jpg'),
    (N'Sinh tố xoài', N'Sinh tố xoài chín, ngọt tự nhiên', 45000, 'SMOOTHIE', 1, '/images/mango-smoothie.jpg'),
    (N'Cà phê đá xay', N'Cà phê đá xay mát lạnh', 55000, 'COFFEE', 1, '/images/iced-coffee.jpg');
END
GO

PRINT 'Database setup completed successfully!';
PRINT 'Sample data has been inserted.';
PRINT 'You can now run the Coffee Shop Management application.';
PRINT '';
PRINT 'Default login accounts:';
PRINT 'Admin: admin/admin123';
PRINT 'Staff: staff1/staff123';
PRINT 'Manager: manager/manager123'; 