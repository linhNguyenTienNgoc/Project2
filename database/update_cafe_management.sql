-- =====================================================
-- CẬP NHẬT DATABASE: THÊM QUẢN LÝ HÌNH ẢNH
-- Mô tả: Thêm các bảng và cột để quản lý hình ảnh
-- Tác giả: Team 2_C2406L (Updated)
-- Ngày cập nhật: 16/08/2025
-- =====================================================

USE cafe_management;

-- =====================================================
-- 10. BẢNG QUẢN LÝ HÌNH ẢNH
-- =====================================================

-- Bảng quản lý hình ảnh chung (images)
CREATE TABLE images (
    image_id INT PRIMARY KEY AUTO_INCREMENT,
    image_name VARCHAR(255) NOT NULL,
    image_path VARCHAR(500) NOT NULL,
    image_url VARCHAR(500),
    file_size INT, -- Kích thước file tính bằng bytes
    image_type VARCHAR(20), -- jpg, png, gif, webp...
    alt_text VARCHAR(255), -- Mô tả hình ảnh cho SEO
    upload_by INT, -- User ID người upload
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (upload_by) REFERENCES users(user_id)
);

-- Bảng liên kết hình ảnh với sản phẩm (product_images)
CREATE TABLE product_images (
    product_image_id INT PRIMARY KEY AUTO_INCREMENT,
    product_id INT NOT NULL,
    image_id INT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE, -- Hình ảnh chính của sản phẩm
    display_order INT DEFAULT 1, -- Thứ tự hiển thị
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES images(image_id) ON DELETE CASCADE,
    UNIQUE KEY unique_primary_per_product (product_id, is_primary)
);

-- Bảng hình ảnh danh mục (category_images)
CREATE TABLE category_images (
    category_image_id INT PRIMARY KEY AUTO_INCREMENT,
    category_id INT NOT NULL,
    image_id INT NOT NULL,
    is_primary BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(category_id) ON DELETE CASCADE,
    FOREIGN KEY (image_id) REFERENCES images(image_id) ON DELETE CASCADE
);

-- Bảng hình ảnh quán cafe (cafe_gallery)
CREATE TABLE cafe_gallery (
    gallery_id INT PRIMARY KEY AUTO_INCREMENT,
    image_id INT NOT NULL,
    gallery_type ENUM('interior', 'exterior', 'food', 'event', 'staff', 'other') DEFAULT 'other',
    title VARCHAR(255),
    description TEXT,
    is_featured BOOLEAN DEFAULT FALSE, -- Hình ảnh nổi bật
    display_order INT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (image_id) REFERENCES images(image_id) ON DELETE CASCADE
);

-- =====================================================
-- CẬP NHẬT CÁC BẢNG HIỆN CÓ
-- =====================================================

-- Cập nhật bảng products - thêm cột image_id
ALTER TABLE products 
ADD COLUMN image_id INT AFTER image_url,
ADD FOREIGN KEY (image_id) REFERENCES images(image_id);

-- Cập nhật bảng categories - thêm cột image_id
ALTER TABLE categories 
ADD COLUMN image_id INT AFTER description,
ADD FOREIGN KEY (image_id) REFERENCES images(image_id);

-- Cập nhật bảng users - thêm avatar
ALTER TABLE users 
ADD COLUMN avatar_id INT AFTER phone,
ADD FOREIGN KEY (avatar_id) REFERENCES images(image_id);

-- Cập nhật bảng areas - thêm hình ảnh khu vực
ALTER TABLE areas 
ADD COLUMN image_id INT AFTER description,
ADD FOREIGN KEY (image_id) REFERENCES images(image_id);

-- =====================================================
-- THÊM DỮ LIỆU MẪU CHO HÌNH ẢNH
-- =====================================================

-- Thêm hình ảnh mẫu
INSERT INTO images (image_name, image_path, image_url, file_size, image_type, alt_text, upload_by) VALUES
-- Hình ảnh sản phẩm
('ca-phe-den.jpg', '/uploads/products/ca-phe-den.jpg', 'https://cafe.com/images/ca-phe-den.jpg', 245760, 'jpg', 'Cà phê đen truyền thống', 1),
('ca-phe-sua.jpg', '/uploads/products/ca-phe-sua.jpg', 'https://cafe.com/images/ca-phe-sua.jpg', 312540, 'jpg', 'Cà phê sữa đặc', 1),
('cappuccino.jpg', '/uploads/products/cappuccino.jpg', 'https://cafe.com/images/cappuccino.jpg', 298760, 'jpg', 'Cappuccino kiểu Ý', 1),
('latte.jpg', '/uploads/products/latte.jpg', 'https://cafe.com/images/latte.jpg', 287500, 'jpg', 'Latte với sữa tươi', 1),
('tra-sua-tran-chau.jpg', '/uploads/products/tra-sua-tran-chau.jpg', 'https://cafe.com/images/tra-sua-tran-chau.jpg', 334200, 'jpg', 'Trà sữa với trân châu', 1),

-- Hình ảnh danh mục
('category-coffee.jpg', '/uploads/categories/category-coffee.jpg', 'https://cafe.com/images/category-coffee.jpg', 456780, 'jpg', 'Danh mục cà phê', 1),
('category-tea.jpg', '/uploads/categories/category-tea.jpg', 'https://cafe.com/images/category-tea.jpg', 423150, 'jpg', 'Danh mục trà', 1),
('category-juice.jpg', '/uploads/categories/category-juice.jpg', 'https://cafe.com/images/category-juice.jpg', 389200, 'jpg', 'Danh mục nước ép', 1),
('category-cake.jpg', '/uploads/categories/category-cake.jpg', 'https://cafe.com/images/category-cake.jpg', 401330, 'jpg', 'Danh mục bánh', 1),

-- Hình ảnh không gian quán
('interior-1.jpg', '/uploads/gallery/interior-1.jpg', 'https://cafe.com/images/interior-1.jpg', 1024000, 'jpg', 'Không gian tầng 1', 1),
('interior-2.jpg', '/uploads/gallery/interior-2.jpg', 'https://cafe.com/images/interior-2.jpg', 987600, 'jpg', 'Không gian tầng 2', 1),
('exterior-1.jpg', '/uploads/gallery/exterior-1.jpg', 'https://cafe.com/images/exterior-1.jpg', 1156780, 'jpg', 'Mặt tiền quán cafe', 1),

-- Avatar nhân viên
('avatar-admin.jpg', '/uploads/avatars/avatar-admin.jpg', 'https://cafe.com/images/avatar-admin.jpg', 156780, 'jpg', 'Avatar Admin', 1),
('avatar-manager.jpg', '/uploads/avatars/avatar-manager.jpg', 'https://cafe.com/images/avatar-manager.jpg', 167890, 'jpg', 'Avatar Manager', 1);

-- Liên kết hình ảnh với sản phẩm
INSERT INTO product_images (product_id, image_id, is_primary, display_order) VALUES
(1, 1, TRUE, 1),  -- Cà phê đen
(2, 2, TRUE, 1),  -- Cà phê sữa
(3, 3, TRUE, 1),  -- Cappuccino
(4, 4, TRUE, 1),  -- Latte
(5, 5, TRUE, 1);  -- Trà sữa trân châu

-- Cập nhật image_id cho products (hình ảnh chính)
UPDATE products SET image_id = 1 WHERE product_id = 1;
UPDATE products SET image_id = 2 WHERE product_id = 2;
UPDATE products SET image_id = 3 WHERE product_id = 3;
UPDATE products SET image_id = 4 WHERE product_id = 4;
UPDATE products SET image_id = 5 WHERE product_id = 5;

-- Liên kết hình ảnh với danh mục
INSERT INTO category_images (category_id, image_id, is_primary) VALUES
(1, 6, TRUE),  -- Cà phê
(2, 7, TRUE),  -- Trà
(3, 8, TRUE),  -- Nước ép
(4, 9, TRUE);  -- Bánh

-- Cập nhật image_id cho categories
UPDATE categories SET image_id = 6 WHERE category_id = 1;
UPDATE categories SET image_id = 7 WHERE category_id = 2;
UPDATE categories SET image_id = 8 WHERE category_id = 3;
UPDATE categories SET image_id = 9 WHERE category_id = 4;

-- Thêm hình ảnh vào gallery
INSERT INTO cafe_gallery (image_id, gallery_type, title, description, is_featured, display_order) VALUES
(10, 'interior', 'Không gian tầng 1', 'Khu vực ngồi tầng trệt với thiết kế hiện đại', TRUE, 1),
(11, 'interior', 'Không gian tầng 2', 'Khu vực yên tĩnh cho làm việc và học tập', TRUE, 2),
(12, 'exterior', 'Mặt tiền quán', 'Thiết kế mặt tiền thu hút với logo nổi bật', TRUE, 3);

-- Cập nhật avatar cho users
UPDATE users SET avatar_id = 13 WHERE user_id = 1; -- Admin
UPDATE users SET avatar_id = 14 WHERE user_id = 2; -- Manager

-- =====================================================
-- TẠO CÁC INDEX ĐỂ TỐI ƯU HIỆU SUẤT
-- =====================================================

CREATE INDEX idx_images_type ON images(image_type);
CREATE INDEX idx_images_upload_date ON images(upload_date);
CREATE INDEX idx_product_images_product ON product_images(product_id);
CREATE INDEX idx_product_images_primary ON product_images(is_primary);
CREATE INDEX idx_category_images_category ON category_images(category_id);
CREATE INDEX idx_gallery_type ON cafe_gallery(gallery_type);
CREATE INDEX idx_gallery_featured ON cafe_gallery(is_featured);

-- =====================================================
-- STORED PROCEDURES ĐỂ QUẢN LỶ HÌNH ẢNH
-- =====================================================

DELIMITER //

-- Procedure lấy tất cả hình ảnh của một sản phẩm
CREATE PROCEDURE GetProductImages(IN p_product_id INT)
BEGIN
    SELECT 
        pi.product_image_id,
        pi.product_id,
        i.image_id,
        i.image_name,
        i.image_path,
        i.image_url,
        i.alt_text,
        pi.is_primary,
        pi.display_order
    FROM product_images pi
    JOIN images i ON pi.image_id = i.image_id
    WHERE pi.product_id = p_product_id 
    AND i.is_active = TRUE
    ORDER BY pi.is_primary DESC, pi.display_order ASC;
END //

-- Procedure đặt hình ảnh chính cho sản phẩm
CREATE PROCEDURE SetPrimaryProductImage(IN p_product_id INT, IN p_image_id INT)
BEGIN
    -- Bỏ primary của tất cả hình ảnh khác
    UPDATE product_images 
    SET is_primary = FALSE 
    WHERE product_id = p_product_id;
    
    -- Đặt hình ảnh được chọn làm primary
    UPDATE product_images 
    SET is_primary = TRUE 
    WHERE product_id = p_product_id AND image_id = p_image_id;
    
    -- Cập nhật image_id trong bảng products
    UPDATE products 
    SET image_id = p_image_id 
    WHERE product_id = p_product_id;
END //

-- Procedure lấy gallery theo loại
CREATE PROCEDURE GetGalleryByType(IN p_gallery_type VARCHAR(20))
BEGIN
    SELECT 
        cg.gallery_id,
        cg.gallery_type,
        cg.title,
        cg.description,
        cg.is_featured,
        cg.display_order,
        i.image_id,
        i.image_name,
        i.image_path,
        i.image_url,
        i.alt_text
    FROM cafe_gallery cg
    JOIN images i ON cg.image_id = i.image_id
    WHERE cg.gallery_type = p_gallery_type
    AND i.is_active = TRUE
    ORDER BY cg.is_featured DESC, cg.display_order ASC;
END //

DELIMITER ;

-- =====================================================
-- VIEW ĐỂ TRUY VẤN DỮ LIỆU DỄ DÀNG HỞN
-- =====================================================

-- View sản phẩm với hình ảnh chính
CREATE VIEW view_products_with_image AS
SELECT 
    p.product_id,
    p.product_name,
    p.price,
    p.cost_price,
    p.description,
    p.is_available,
    c.category_name,
    i.image_name,
    i.image_path,
    i.image_url,
    i.alt_text
FROM products p
LEFT JOIN categories c ON p.category_id = c.category_id
LEFT JOIN images i ON p.image_id = i.image_id
WHERE p.is_active = TRUE;

-- View danh mục với hình ảnh
CREATE VIEW view_categories_with_image AS
SELECT 
    c.category_id,
    c.category_name,
    c.description,
    c.is_active,
    i.image_name,
    i.image_path,
    i.image_url,
    i.alt_text
FROM categories c
LEFT JOIN images i ON c.image_id = i.image_id
WHERE c.is_active = TRUE;

-- View gallery nổi bật
CREATE VIEW view_featured_gallery AS
SELECT 
    cg.gallery_id,
    cg.gallery_type,
    cg.title,
    cg.description,
    cg.display_order,
    i.image_name,
    i.image_path,
    i.image_url,
    i.alt_text
FROM cafe_gallery cg
JOIN images i ON cg.image_id = i.image_id
WHERE cg.is_featured = TRUE
AND i.is_active = TRUE
ORDER BY cg.display_order ASC;