-- Test database connection
USE master;
GO

-- Kiểm tra xem database có tồn tại không
IF DB_ID('CoffeeShopManagement') IS NOT NULL
    PRINT 'Database CoffeeShopManagement exists!'
ELSE
    PRINT 'Database CoffeeShopManagement does not exist!'

-- Kiểm tra xem có thể connect đến server không
SELECT @@VERSION as SQL_Server_Version;
SELECT DB_NAME() as Current_Database; 