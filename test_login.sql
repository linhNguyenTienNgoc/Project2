-- Test login và kiểm tra tài khoản
USE CoffeeShopManagement;

-- Kiểm tra tất cả employees
SELECT 
    id,
    username,
    password,
    role,
    active,
    name,
    email,
    phone
FROM Employee;

-- Kiểm tra tài khoản admin cụ thể
SELECT 
    id,
    username,
    password,
    role,
    active,
    name
FROM Employee 
WHERE username = 'admin';

-- Kiểm tra tài khoản staff
SELECT 
    id,
    username,
    password,
    role,
    active,
    name
FROM Employee 
WHERE username = 'staff1';

-- Kiểm tra tài khoản manager
SELECT 
    id,
    username,
    password,
    role,
    active,
    name
FROM Employee 
WHERE username = 'manager';

-- Đếm tổng số employees
SELECT COUNT(*) as total_employees FROM Employee;

-- Kiểm tra employees active
SELECT COUNT(*) as active_employees FROM Employee WHERE active = 1;

-- Kiểm tra employees theo role
SELECT role, COUNT(*) as count FROM Employee GROUP BY role; 