-- =====================================================
-- PROMOTIONS DATABASE MIGRATION
-- File: database/promotions_migration.sql
-- Purpose: Add promotion system tables to cafe management database
-- =====================================================

USE cafe_management;

-- =====================================================
-- 1. CREATE PROMOTIONS TABLE
-- =====================================================

CREATE TABLE IF NOT EXISTS promotions (
    promotion_id INT AUTO_INCREMENT PRIMARY KEY,
    promotion_name VARCHAR(255) NOT NULL,
    description TEXT,
    discount_type ENUM('PERCENTAGE', 'FIXED_AMOUNT') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0,
    max_discount_amount DECIMAL(10,2) DEFAULT 0,
    start_date TIMESTAMP NULL,
    end_date TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    usage_limit INT DEFAULT 0, -- 0 means unlimited
    usage_count INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Indexes for performance
    INDEX idx_promotion_active (is_active),
    INDEX idx_promotion_dates (start_date, end_date),
    INDEX idx_promotion_min_amount (min_order_amount)
);

-- =====================================================
-- 2. CREATE ORDER_PROMOTIONS TABLE (for tracking usage)
-- =====================================================

CREATE TABLE IF NOT EXISTS order_promotions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    promotion_id INT NOT NULL,
    discount_amount DECIMAL(10,2) NOT NULL,
    applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Foreign keys
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (promotion_id) REFERENCES promotions(promotion_id) ON DELETE RESTRICT,
    
    -- Indexes
    INDEX idx_order_promotion (order_id),
    INDEX idx_promotion_usage (promotion_id),
    INDEX idx_applied_date (applied_at),
    
    -- Unique constraint to prevent multiple promotions per order
    UNIQUE KEY unique_order_promotion (order_id, promotion_id)
);

-- =====================================================
-- 3. INSERT SAMPLE PROMOTIONS DATA
-- =====================================================

-- Clear existing sample data if any
DELETE FROM order_promotions WHERE promotion_id IN (
    SELECT promotion_id FROM promotions WHERE promotion_name LIKE 'Sample%' OR promotion_name LIKE 'Giảm%'
);

DELETE FROM promotions WHERE promotion_name LIKE 'Sample%' OR promotion_name LIKE 'Giảm%';

-- Insert sample promotions
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
    50000.00, -- Max discount 50k
    DATE_SUB(NOW(), INTERVAL 1 DAY), -- Started yesterday
    DATE_ADD(NOW(), INTERVAL 30 DAY), -- Expires in 30 days
    TRUE,
    0 -- Unlimited usage
),

(
    'Giảm 15% - Khách VIP',
    'Giảm giá 15% cho khách hàng VIP (đơn hàng từ 50k)',
    'PERCENTAGE',
    15.00,
    50000.00,
    100000.00, -- Max discount 100k
    NOW(),
    DATE_ADD(NOW(), INTERVAL 60 DAY), -- Expires in 60 days
    TRUE,
    0 -- Unlimited usage
),

(
    'Giảm 5% - Mọi đơn hàng',
    'Giảm giá 5% cho mọi đơn hàng, không điều kiện',
    'PERCENTAGE',
    5.00,
    0.00, -- No minimum
    20000.00, -- Max discount 20k
    NOW(),
    DATE_ADD(NOW(), INTERVAL 90 DAY), -- Expires in 90 days
    TRUE,
    1000 -- Limited to 1000 uses
),

-- Fixed amount discounts
(
    'Giảm 20k - Đơn hàng từ 150k',
    'Giảm 20,000đ cho đơn hàng từ 150,000đ trở lên',
    'FIXED_AMOUNT',
    20000.00,
    150000.00,
    0.00, -- No max limit for fixed amount
    NOW(),
    DATE_ADD(NOW(), INTERVAL 45 DAY), -- Expires in 45 days
    TRUE,
    500 -- Limited to 500 uses
),

(
    'Giảm 50k - Đơn hàng từ 300k',
    'Giảm 50,000đ cho đơn hàng từ 300,000đ trở lên',
    'FIXED_AMOUNT',
    50000.00,
    300000.00,
    0.00, -- No max limit for fixed amount
    NOW(),
    DATE_ADD(NOW(), INTERVAL 30 DAY), -- Expires in 30 days
    TRUE,
    100 -- Limited to 100 uses
),

-- Weekend special
(
    'Cuối tuần giảm 12%',
    'Giảm giá 12% cho đơn hàng cuối tuần (từ 80k)',
    'PERCENTAGE',
    12.00,
    80000.00,
    60000.00, -- Max discount 60k
    NOW(),
    DATE_ADD(NOW(), INTERVAL 14 DAY), -- Expires in 2 weeks
    TRUE,
    0 -- Unlimited usage
),

-- Happy hour
(
    'Happy Hour - Giảm 25k',
    'Giảm 25,000đ cho đơn hàng từ 200k trong khung giờ vàng',
    'FIXED_AMOUNT',
    25000.00,
    200000.00,
    0.00,
    NOW(),
    DATE_ADD(NOW(), INTERVAL 7 DAY), -- Expires in 1 week
    TRUE,
    200 -- Limited to 200 uses
),

-- Inactive promotion for testing
(
    'Khuyến mãi hết hạn',
    'Promotion này đã hết hạn - dùng để test',
    'PERCENTAGE',
    20.00,
    100000.00,
    0.00,
    DATE_SUB(NOW(), INTERVAL 10 DAY), -- Started 10 days ago
    DATE_SUB(NOW(), INTERVAL 1 DAY), -- Expired yesterday
    FALSE, -- Inactive
    0
);

-- =====================================================
-- 4. UPDATE ORDERS TABLE (if needed)
-- =====================================================

-- Check if discount_amount column exists, if not add it
SET @column_exists = (
    SELECT COUNT(*) 
    FROM INFORMATION_SCHEMA.COLUMNS 
    WHERE TABLE_SCHEMA = 'cafe_management' 
    AND TABLE_NAME = 'orders' 
    AND COLUMN_NAME = 'discount_amount'
);

SET @sql = IF(@column_exists = 0,
    'ALTER TABLE orders ADD COLUMN discount_amount DECIMAL(10,2) DEFAULT 0 AFTER total_amount',
    'SELECT "discount_amount column already exists" AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- =====================================================
-- 5. CREATE PROMOTION STATISTICS VIEW
-- =====================================================

CREATE OR REPLACE VIEW promotion_stats AS
SELECT 
    p.promotion_id,
    p.promotion_name,
    p.discount_type,
    p.discount_value,
    p.min_order_amount,
    p.usage_limit,
    p.usage_count,
    COALESCE(COUNT(op.id), 0) AS actual_usage_count,
    COALESCE(SUM(op.discount_amount), 0) AS total_discount_given,
    COALESCE(AVG(op.discount_amount), 0) AS avg_discount_per_use,
    p.is_active,
    p.start_date,
    p.end_date,
    CASE 
        WHEN p.end_date IS NOT NULL AND p.end_date < NOW() THEN 'EXPIRED'
        WHEN p.start_date IS NOT NULL AND p.start_date > NOW() THEN 'UPCOMING'
        WHEN p.is_active = TRUE THEN 'ACTIVE'
        ELSE 'INACTIVE'
    END AS status
FROM promotions p
LEFT JOIN order_promotions op ON p.promotion_id = op.promotion_id
GROUP BY p.promotion_id
ORDER BY p.created_at DESC;

-- =====================================================
-- 6. VERIFICATION QUERIES
-- =====================================================

-- Show all created tables
SHOW TABLES LIKE '%promotion%';

-- Show promotions table structure
DESCRIBE promotions;

-- Show order_promotions table structure  
DESCRIBE order_promotions;

-- Show sample promotions data
SELECT 
    promotion_id,
    promotion_name,
    discount_type,
    discount_value,
    min_order_amount,
    is_active,
    DATE_FORMAT(start_date, '%d/%m/%Y') as start_date,
    DATE_FORMAT(end_date, '%d/%m/%Y') as end_date
FROM promotions 
ORDER BY promotion_id;

-- Show promotion statistics
SELECT * FROM promotion_stats;

-- =====================================================
-- MIGRATION COMPLETED SUCCESSFULLY
-- =====================================================

SELECT 
    'PROMOTIONS MIGRATION COMPLETED' AS status,
    COUNT(*) AS promotions_created,
    NOW() AS completed_at
FROM promotions;
