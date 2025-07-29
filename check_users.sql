-- Kiểm tra tài khoản trong database
USE CoffeeShopManagement;

-- Xem tất cả employees
SELECT id, username, password, role, active, name, email, phone 
FROM Employee;

-- Kiểm tra tài khoản admin cụ thể
SELECT id, username, password, role, active, name, email, phone 
FROM Employee 
WHERE username = 'admin';

-- Kiểm tra tài khoản staff
SELECT id, username, password, role, active, name, email, phone 
FROM Employee 
WHERE username = 'staff1';

-- Đếm tổng số employees
SELECT COUNT(*) as total_employees FROM Employee;

-- Kiểm tra employees active
SELECT COUNT(*) as active_employees FROM Employee WHERE active = 1; 