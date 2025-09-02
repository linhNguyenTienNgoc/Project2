-- =====================================================
-- UPDATE PRODUCT IMAGES
-- File: database/update_product_images.sql
-- Purpose: Cập nhật hình ảnh cho tất cả sản phẩm
-- Author: Team 2_C2406L
-- Version: 1.0.0
-- =====================================================

USE cafe_management;

-- =====================================================
-- UPDATE COFFEE PRODUCTS IMAGES
-- =====================================================

UPDATE products SET image_url = '/images/products/coffee/ca-phe-den.jpg' WHERE product_name = 'Cà phê đen';
UPDATE products SET image_url = '/images/products/coffee/ca-phe-sua.jpg' WHERE product_name = 'Cà phê sữa';
UPDATE products SET image_url = '/images/products/coffee/ca-phe-sua-da.jpg' WHERE product_name = 'Cà phê sữa đá';
UPDATE products SET image_url = '/images/products/coffee/espresso.jpg' WHERE product_name = 'Espresso';
UPDATE products SET image_url = '/images/products/coffee/americano.jpg' WHERE product_name = 'Americano';
UPDATE products SET image_url = '/images/products/coffee/cappuccino.jpg' WHERE product_name = 'Cappuccino';
UPDATE products SET image_url = '/images/products/coffee/latte.jpg' WHERE product_name = 'Latte';
UPDATE products SET image_url = '/images/products/coffee/macchiato.jpg' WHERE product_name = 'Macchiato';
UPDATE products SET image_url = '/images/products/coffee/mocha.jpg' WHERE product_name = 'Mocha';
UPDATE products SET image_url = '/images/products/coffee/cold-brew.jpg' WHERE product_name = 'Cold Brew';

-- =====================================================
-- UPDATE TEA & MILK TEA PRODUCTS IMAGES
-- =====================================================

UPDATE products SET image_url = '/images/products/tea/tra-sua-tran-chau.jpg' WHERE product_name = 'Trà sữa trân châu';
UPDATE products SET image_url = '/images/products/tea/tra-sua-pudding.jpg' WHERE product_name = 'Trà sữa pudding';
UPDATE products SET image_url = '/images/products/tea/tra-dao.jpg' WHERE product_name = 'Trà đào';
UPDATE products SET image_url = '/images/products/tea/tra-chanh.jpg' WHERE product_name = 'Trà chanh';
UPDATE products SET image_url = '/images/products/tea/tra-den-da.jpg' WHERE product_name = 'Trà đen đá';
UPDATE products SET image_url = '/images/products/tea/tra-xanh.jpg' WHERE product_name = 'Trà xanh';
UPDATE products SET image_url = '/images/products/tea/tra-o-long.jpg' WHERE product_name = 'Trà ô long';
UPDATE products SET image_url = '/images/products/tea/tra-sua-socola.jpg' WHERE product_name = 'Trà sữa socola';
UPDATE products SET image_url = '/images/products/tea/tra-sua-matcha.jpg' WHERE product_name = 'Trà sữa matcha';
UPDATE products SET image_url = '/images/products/tea/tra-atiso.jpg' WHERE product_name = 'Trà atiso';

-- =====================================================
-- UPDATE JUICE & SMOOTHIE PRODUCTS IMAGES
-- =====================================================

UPDATE products SET image_url = '/images/products/juice/nuoc-cam-ep.jpg' WHERE product_name = 'Nước cam ép';
UPDATE products SET image_url = '/images/products/juice/nuoc-chanh-day.jpg' WHERE product_name = 'Nước chanh dây';
UPDATE products SET image_url = '/images/products/juice/sinh-to-bo.jpg' WHERE product_name = 'Sinh tố bơ';
UPDATE products SET image_url = '/images/products/juice/sinh-to-xoai.jpg' WHERE product_name = 'Sinh tố xoài';
UPDATE products SET image_url = '/images/products/juice/sinh-to-dau.jpg' WHERE product_name = 'Sinh tố dâu';
UPDATE products SET image_url = '/images/products/juice/nuoc-dua-hau.jpg' WHERE product_name = 'Nước dưa hấu';
UPDATE products SET image_url = '/images/products/juice/nuoc-du-du.jpg' WHERE product_name = 'Nước đu đủ';
UPDATE products SET image_url = '/images/products/juice/sinh-to-chuoi.jpg' WHERE product_name = 'Sinh tố chuối';
UPDATE products SET image_url = '/images/products/juice/nuoc-tao-ep.jpg' WHERE product_name = 'Nước táo ép';
UPDATE products SET image_url = '/images/products/juice/sinh-to-mix-berry.jpg' WHERE product_name = 'Sinh tố mix berry';

-- =====================================================
-- UPDATE CAKE PRODUCTS IMAGES
-- =====================================================

UPDATE products SET image_url = '/images/products/cake/banh-tiramisu.jpg' WHERE product_name = 'Bánh tiramisu';
UPDATE products SET image_url = '/images/products/cake/banh-cheesecake.jpg' WHERE product_name = 'Bánh cheesecake';
UPDATE products SET image_url = '/images/products/cake/banh-chocolate.jpg' WHERE product_name = 'Bánh chocolate';
UPDATE products SET image_url = '/images/products/cake/banh-red-velvet.jpg' WHERE product_name = 'Bánh red velvet';
UPDATE products SET image_url = '/images/products/cake/banh-croissant.jpg' WHERE product_name = 'Bánh croissant';
UPDATE products SET image_url = '/images/products/cake/banh-muffin.jpg' WHERE product_name = 'Bánh muffin';
UPDATE products SET image_url = '/images/products/cake/banh-flan.jpg' WHERE product_name = 'Bánh flan';
UPDATE products SET image_url = '/images/products/cake/banh-su-kem.jpg' WHERE product_name = 'Bánh su kem';
UPDATE products SET image_url = '/images/products/cake/banh-macaron.jpg' WHERE product_name = 'Bánh macaron';
UPDATE products SET image_url = '/images/products/cake/banh-opera.jpg' WHERE product_name = 'Bánh opera';



-- =====================================================
-- UPDATE SMOOTHIE & FROZEN DRINKS IMAGES
-- =====================================================

UPDATE products SET image_url = '/images/products/smoothie/frappuccino.jpg' WHERE product_name = 'Frappuccino';
UPDATE products SET image_url = '/images/products/smoothie/chocolate-smoothie.jpg' WHERE product_name = 'Chocolate smoothie';
UPDATE products SET image_url = '/images/products/smoothie/oreo-smoothie.jpg' WHERE product_name = 'Oreo smoothie';
UPDATE products SET image_url = '/images/products/smoothie/matcha-frappe.jpg' WHERE product_name = 'Matcha frappé';
UPDATE products SET image_url = '/images/products/smoothie/coconut-smoothie.jpg' WHERE product_name = 'Coconut smoothie';

-- =====================================================
-- VERIFICATION QUERIES
-- =====================================================

-- Check products with images
SELECT 
    'Products with images' AS status,
    COUNT(*) AS count
FROM products 
WHERE image_url IS NOT NULL AND image_url != '';

-- Check products without images
SELECT 
    'Products without images' AS status,
    COUNT(*) AS count
FROM products 
WHERE image_url IS NULL OR image_url = '';

-- Show all products with their image URLs
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    p.image_url,
    CASE 
        WHEN p.image_url IS NULL OR p.image_url = '' THEN '❌ No Image'
        ELSE '✅ Has Image'
    END AS image_status
FROM products p
JOIN categories c ON p.category_id = c.category_id
ORDER BY c.display_order, p.product_name;

-- =====================================================
-- COMPLETION MESSAGE
-- =====================================================

SELECT 
    '🎉 PRODUCT IMAGES UPDATED SUCCESSFULLY!' AS message,
    NOW() AS updated_at,
    'All products now have image URLs configured' AS status;
