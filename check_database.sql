-- Kiểm tra Database CoffeeShopManagement
USE master;
GO

-- Kiểm tra database có tồn tại không
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'CoffeeShopManagement')
BEGIN
    PRINT 'Database CoffeeShopManagement EXISTS!';
    
    -- Chuyển sang database CoffeeShopManagement
    USE CoffeeShopManagement;
    GO
    
    -- Kiểm tra bảng Employee
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Employee')
    BEGIN
        PRINT 'Table Employee EXISTS!';
        
        -- Kiểm tra dữ liệu
        SELECT COUNT(*) as TotalEmployees FROM Employee;
        
        -- Kiểm tra tài khoản admin
        SELECT 
            id,
            username,
            password,
            role,
            active,
            name
        FROM Employee 
        WHERE username = 'admin';
        
        -- Hiển thị tất cả employees
        SELECT 
            id,
            username,
            password,
            role,
            active,
            name
        FROM Employee;
        
    END
    ELSE
    BEGIN
        PRINT 'Table Employee NOT EXISTS!';
    END
    
    -- Kiểm tra các bảng khác
    SELECT name as TableName FROM sys.tables ORDER BY name;
    
END
ELSE
BEGIN
    PRINT 'Database CoffeeShopManagement NOT EXISTS!';
    PRINT 'Please run setup_database.sql to create the database.';
END
GO 