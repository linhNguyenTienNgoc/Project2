-- Test SQL Server Connection
-- Chạy script này trong SQL Server Management Studio hoặc IntelliJ

-- 1. Kiểm tra kết nối cơ bản
SELECT 
    @@VERSION as SQLServerVersion,
    DB_NAME() as CurrentDatabase,
    SYSTEM_USER as CurrentUser,
    GETDATE() as CurrentTime;

-- 2. Kiểm tra database CoffeeShopManagement
IF EXISTS (SELECT * FROM sys.databases WHERE name = 'CoffeeShopManagement')
BEGIN
    PRINT 'Database CoffeeShopManagement EXISTS!';
    
    USE CoffeeShopManagement;
    
    -- 3. Kiểm tra các bảng
    SELECT 
        TABLE_NAME,
        TABLE_TYPE
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_TYPE = 'BASE TABLE'
    ORDER BY TABLE_NAME;
    
    -- 4. Kiểm tra bảng Employee
    IF EXISTS (SELECT * FROM sys.tables WHERE name = 'Employee')
    BEGIN
        PRINT 'Table Employee EXISTS!';
        
        -- 5. Kiểm tra dữ liệu Employee
        SELECT 
            COUNT(*) as TotalEmployees,
            COUNT(CASE WHEN active = 1 THEN 1 END) as ActiveEmployees,
            COUNT(CASE WHEN role = 'ADMIN' THEN 1 END) as AdminCount,
            COUNT(CASE WHEN role = 'STAFF' THEN 1 END) as StaffCount
        FROM Employee;
        
        -- 6. Hiển thị tài khoản admin
        SELECT 
            id,
            username,
            password,
            role,
            active,
            name
        FROM Employee 
        WHERE username = 'admin';
        
    END
    ELSE
    BEGIN
        PRINT 'Table Employee NOT EXISTS!';
    END
    
END
ELSE
BEGIN
    PRINT 'Database CoffeeShopManagement NOT EXISTS!';
    PRINT 'Please run fix_login.sql to create the database.';
END

-- 7. Kiểm tra quyền truy cập
SELECT 
    name as DatabaseName,
    state_desc as State,
    recovery_model_desc as RecoveryModel
FROM sys.databases 
WHERE name = 'CoffeeShopManagement';

-- 8. Kiểm tra kết nối hiện tại
SELECT 
    @@SPID as SessionID,
    DB_NAME() as CurrentDatabase,
    SYSTEM_USER as CurrentUser,
    HOST_NAME() as HostName; 