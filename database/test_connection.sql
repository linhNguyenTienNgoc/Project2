-- =====================================================
-- TEST CONNECTION SCRIPT
-- Script để test kết nối database và kiểm tra cấu trúc
-- =====================================================

-- Test basic connection
SELECT 'Database connection OK' AS test_result;

-- Show database info
SELECT 
    VERSION() AS mysql_version,
    DATABASE() AS current_database,
    USER() AS current_user,
    NOW() AS current_time;

-- Check if all required tables exist
SELECT 
    TABLE_NAME,
    TABLE_ROWS,
    CREATE_TIME
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'cafe_management'
ORDER BY TABLE_NAME;

-- Test sample users for authentication
SELECT 
    username,
    full_name,
    CASE 
        WHEN is_active = 1 THEN 'Active'
        ELSE 'Inactive'
    END AS status
FROM users 
WHERE is_active = 1
LIMIT 5;

-- Test sample products
SELECT 
    p.product_name,
    c.category_name,
    p.price,
    CASE 
        WHEN p.is_available = 1 THEN 'Available'
        ELSE 'Not Available'
    END AS availability
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
WHERE p.is_active = 1
LIMIT 5;

-- Test sample areas and tables
SELECT 
    a.area_name,
    COUNT(t.table_id) AS table_count
FROM areas a
LEFT JOIN tables t ON a.area_id = t.area_id
WHERE a.is_active = 1
GROUP BY a.area_id, a.area_name;

-- Show database summary
SELECT 
    'Tables' AS item,
    COUNT(*) AS count
FROM information_schema.TABLES 
WHERE TABLE_SCHEMA = 'cafe_management'

UNION ALL

SELECT 
    'Users' AS item,
    COUNT(*) AS count
FROM users

UNION ALL

SELECT 
    'Products' AS item,
    COUNT(*) AS count
FROM products

UNION ALL

SELECT 
    'Categories' AS item,
    COUNT(*) AS count
FROM categories

UNION ALL

SELECT 
    'Areas' AS item,
    COUNT(*) AS count
FROM areas

UNION ALL

SELECT 
    'Tables' AS item,
    COUNT(*) AS count
FROM tables;