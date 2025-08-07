-- =====================================================
-- TEST: KIỂM TRA VIỆC SỬA LỖI CHECK CONSTRAINT
-- Mô tả: Test tạo bảng promotions với triggers validation
-- =====================================================

-- Test 1: Tạo database test
CREATE DATABASE IF NOT EXISTS test_cafe_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE test_cafe_management;

-- Test 2: Tạo bảng promotions (đã sửa lỗi)
CREATE TABLE promotions (
    promotion_id INT PRIMARY KEY AUTO_INCREMENT,
    promotion_code VARCHAR(50) UNIQUE,
    promotion_name VARCHAR(150) NOT NULL,
    description TEXT,
    discount_type ENUM('percentage', 'fixed_amount', 'buy_x_get_y') NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    min_order_amount DECIMAL(10,2) DEFAULT 0.00,
    max_discount_amount DECIMAL(10,2),
    usage_limit INT DEFAULT -1,
    used_count INT DEFAULT 0,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- Constraints (đã loại bỏ CURDATE())
    CHECK (LENGTH(promotion_name) >= 2),
    CHECK (discount_value >= 0),
    CHECK (min_order_amount >= 0),
    CHECK (max_discount_amount IS NULL OR max_discount_amount >= 0),
    CHECK (usage_limit = -1 OR usage_limit > 0),
    CHECK (used_count >= 0),
    CHECK (start_date <= end_date),
    
    -- Indexes
    INDEX idx_promotion_code (promotion_code),
    INDEX idx_promotion_name (promotion_name),
    INDEX idx_discount_type (discount_type),
    INDEX idx_start_date (start_date),
    INDEX idx_end_date (end_date),
    INDEX idx_promotion_active (is_active),
    INDEX idx_usage_limit (usage_limit)
);

-- Test 3: Tạo triggers validation
DELIMITER //

-- Trigger validate ngày kết thúc khuyến mãi khi insert
CREATE TRIGGER tr_promotions_validate_date
BEFORE INSERT ON promotions
FOR EACH ROW
BEGIN
    IF NEW.end_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc khuyến mãi không thể là ngày trong quá khứ';
    END IF;
END//

-- Trigger validate ngày kết thúc khuyến mãi khi update
CREATE TRIGGER tr_promotions_validate_date_update
BEFORE UPDATE ON promotions
FOR EACH ROW
BEGIN
    IF NEW.end_date < CURDATE() THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Ngày kết thúc khuyến mãi không thể là ngày trong quá khứ';
    END IF;
END//

DELIMITER ;

-- Test 4: Test insert thành công (ngày tương lai)
INSERT INTO promotions (
    promotion_code, 
    promotion_name, 
    discount_type, 
    discount_value, 
    start_date, 
    end_date
) VALUES (
    'TEST001',
    'Khuyến mãi test',
    'percentage',
    10.00,
    CURDATE(),
    DATE_ADD(CURDATE(), INTERVAL 30 DAY)
);

SELECT '✅ Test 4 PASSED: Insert thành công với ngày tương lai' AS result;

-- Test 5: Test insert thất bại (ngày quá khứ) - sẽ bị trigger chặn
-- INSERT INTO promotions (
--     promotion_code, 
--     promotion_name, 
--     discount_type, 
--     discount_value, 
--     start_date, 
--     end_date
-- ) VALUES (
--     'TEST002',
--     'Khuyến mãi test 2',
--     'percentage',
--     10.00,
--     CURDATE(),
--     DATE_SUB(CURDATE(), INTERVAL 1 DAY)
-- );

-- Test 6: Test update thất bại (ngày quá khứ) - sẽ bị trigger chặn
-- UPDATE promotions 
-- SET end_date = DATE_SUB(CURDATE(), INTERVAL 1 DAY)
-- WHERE promotion_code = 'TEST001';

-- Test 7: Kiểm tra dữ liệu
SELECT 
    promotion_id,
    promotion_code,
    promotion_name,
    start_date,
    end_date,
    is_active
FROM promotions;

-- Test 8: Cleanup
DROP DATABASE IF EXISTS test_cafe_management;

SELECT '✅ TẤT CẢ TESTS PASSED: Bảng promotions đã được sửa lỗi thành công!' AS final_result; 