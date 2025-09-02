@echo off
echo =====================================================
echo CREATE PRODUCT IMAGE FOLDERS
echo =====================================================

REM Tạo thư mục gốc cho hình ảnh sản phẩm
if not exist "src\main\resources\images\products" (
    mkdir "src\main\resources\images\products"
    echo ✅ Created: src\main\resources\images\products
)

REM Tạo thư mục cho từng category
if not exist "src\main\resources\images\products\coffee" (
    mkdir "src\main\resources\images\products\coffee"
    echo ✅ Created: src\main\resources\images\products\coffee
)

if not exist "src\main\resources\images\products\tea" (
    mkdir "src\main\resources\images\products\tea"
    echo ✅ Created: src\main\resources\images\products\tea
)

if not exist "src\main\resources\images\products\juice" (
    mkdir "src\main\resources\images\products\juice"
    echo ✅ Created: src\main\resources\images\products\juice
)

if not exist "src\main\resources\images\products\cake" (
    mkdir "src\main\resources\images\products\cake"
    echo ✅ Created: src\main\resources\images\products\cake
)

if not exist "src\main\resources\images\products\food" (
    mkdir "src\main\resources\images\products\food"
    echo ✅ Created: src\main\resources\images\products\food
)

if not exist "src\main\resources\images\products\smoothie" (
    mkdir "src\main\resources\images\products\smoothie"
    echo ✅ Created: src\main\resources\images\products\smoothie
)

REM Tạo thư mục placeholders nếu chưa có
if not exist "src\main\resources\images\placeholders" (
    mkdir "src\main\resources\images\placeholders"
    echo ✅ Created: src\main\resources\images\placeholders
)

echo.
echo =====================================================
echo FOLDER STRUCTURE CREATED SUCCESSFULLY!
echo =====================================================
echo.
echo 📁 Image folders created:
echo    ├── coffee/     (Cà phê)
echo    ├── tea/        (Trà & Trà sữa)
echo    ├── juice/      (Nước ép & Sinh tố)
echo    ├── cake/       (Bánh ngọt)
echo    ├── food/       (Đồ ăn nhẹ)
echo    ├── smoothie/   (Đồ uống đá xay)
echo    └── placeholders/ (Hình ảnh mặc định)
echo.
echo 📝 Next steps:
echo    1. Add product images to appropriate folders
echo    2. Run database/update_product_images.sql
echo    3. Test image display in application
echo.
pause
