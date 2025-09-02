# HƯỚNG DẪN THÊM HÌNH ẢNH CHO SẢN PHẨM

## 📋 Tổng quan

Hệ thống quản lý quán cà phê đã được thiết kế để hỗ trợ hình ảnh sản phẩm với cấu trúc linh hoạt và dễ sử dụng.

## 🏗️ Cấu trúc hiện tại

### 1. Database Schema
- **Bảng `products`** có trường `image_url VARCHAR(255)` để lưu đường dẫn hình ảnh
- Hỗ trợ lưu trữ đường dẫn tương đối hoặc tuyệt đối

### 2. Thư mục hình ảnh
```
src/main/resources/images/
├── products/           # Hình ảnh sản phẩm
│   ├── coffee/        # Hình ảnh cà phê
│   ├── tea/           # Hình ảnh trà & trà sữa
│   ├── cake/          # Hình ảnh bánh ngọt
│   └── food/          # Hình ảnh đồ ăn nhẹ
├── placeholders/      # Hình ảnh mặc định
├── icons/            # Icon hệ thống
└── backgrounds/      # Hình nền
```

### 3. ImageLoader Utility
- Class `ImageLoader` xử lý việc load hình ảnh
- Tự động fallback về hình mặc định nếu không tìm thấy
- Hỗ trợ load từ resources hoặc file system

## 🚀 Cách thêm hình ảnh cho sản phẩm

### Phương pháp 1: Thêm hình ảnh vào thư mục resources

#### Bước 1: Chuẩn bị hình ảnh
- **Kích thước khuyến nghị**: 400x300px hoặc tỷ lệ 4:3
- **Định dạng**: JPG, PNG, GIF
- **Tên file**: Sử dụng tên tiếng Việt không dấu hoặc tiếng Anh
- **Ví dụ**: `ca-phe-den.jpg`, `tra-sua-tran-chau.png`

#### Bước 2: Đặt hình ảnh vào thư mục phù hợp
```
src/main/resources/images/products/
├── coffee/
│   ├── ca-phe-den.jpg
│   ├── ca-phe-sua.jpg
│   ├── espresso.jpg
│   └── latte.jpg
├── tea/
│   ├── tra-sua-tran-chau.jpg
│   ├── tra-dao.jpg
│   └── tra-chanh.jpg
├── cake/
│   ├── banh-tiramisu.jpg
│   ├── banh-cheesecake.jpg
│   └── banh-chocolate.jpg
└── food/
    ├── khoai-tay-chien.jpg
    ├── sandwich.jpg
    └── banh-mi-nuong.jpg
```

#### Bước 3: Cập nhật database
```sql
-- Cập nhật image_url cho sản phẩm
UPDATE products 
SET image_url = '/images/products/coffee/ca-phe-den.jpg' 
WHERE product_name = 'Cà phê đen';

UPDATE products 
SET image_url = '/images/products/tea/tra-sua-tran-chau.jpg' 
WHERE product_name = 'Trà sữa trân châu';
```

### Phương pháp 2: Sử dụng đường dẫn tuyệt đối

#### Bước 1: Tạo thư mục hình ảnh bên ngoài project
```
C:\CafeImages\
├── coffee\
├── tea\
├── cake\
└── food\
```

#### Bước 2: Cập nhật database với đường dẫn tuyệt đối
```sql
UPDATE products 
SET image_url = 'C:/CafeImages/coffee/ca-phe-den.jpg' 
WHERE product_name = 'Cà phê đen';
```

### Phương pháp 3: Thêm hình ảnh qua ứng dụng (nếu có tính năng upload)

#### Bước 1: Sử dụng tính năng upload trong admin panel
- Vào menu "Quản lý sản phẩm"
- Chọn sản phẩm cần thêm hình
- Click "Upload hình ảnh"
- Chọn file và upload

#### Bước 2: Hệ thống tự động lưu vào thư mục và cập nhật database

## 📝 Script SQL để cập nhật hàng loạt

### Script cập nhật hình ảnh cho tất cả sản phẩm cà phê
```sql
-- Cà phê
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
```

### Script cập nhật hình ảnh cho trà & trà sữa
```sql
-- Trà & Trà sữa
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
```

### Script cập nhật hình ảnh cho bánh ngọt
```sql
-- Bánh ngọt
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
```

## 🎨 Tạo hình ảnh placeholder

### Tạo hình ảnh mặc định
```java
// Tạo file: src/main/resources/images/placeholders/no-image.png
// Kích thước: 400x300px
// Màu nền: #f5f5f5
// Text: "Không có hình ảnh"
// Font: Arial, 16px, màu #999
```

## 🔧 Troubleshooting

### Vấn đề 1: Hình ảnh không hiển thị
**Nguyên nhân**: Đường dẫn không đúng hoặc file không tồn tại
**Giải pháp**:
1. Kiểm tra đường dẫn trong database
2. Kiểm tra file có tồn tại trong thư mục
3. Kiểm tra quyền truy cập file

### Vấn đề 2: Hình ảnh hiển thị chậm
**Nguyên nhân**: File hình ảnh quá lớn
**Giải pháp**:
1. Nén hình ảnh trước khi upload
2. Sử dụng định dạng JPG thay vì PNG
3. Giảm kích thước hình ảnh

### Vấn đề 3: Hình ảnh bị méo
**Nguyên nhân**: Tỷ lệ khung hình không phù hợp
**Giải pháp**:
1. Sử dụng tỷ lệ 4:3 hoặc 16:9
2. Cắt hình ảnh trước khi upload
3. Sử dụng CSS để giữ tỷ lệ

## 📱 Tối ưu hóa cho mobile

### Responsive Images
```css
.product-image {
    width: 100%;
    height: auto;
    max-width: 400px;
    max-height: 300px;
    object-fit: cover;
}
```

## 🚀 Best Practices

### 1. Đặt tên file
- Sử dụng tên tiếng Việt không dấu
- Tránh ký tự đặc biệt
- Sử dụng dấu gạch ngang thay vì khoảng trắng

### 2. Kích thước file
- **JPG**: 50-200KB
- **PNG**: 100-500KB
- **GIF**: Chỉ dùng cho animation

### 3. Tổ chức thư mục
- Phân loại theo category
- Sử dụng tên thư mục rõ ràng
- Tránh lồng quá nhiều cấp

### 4. Backup
- Backup hình ảnh định kỳ
- Lưu trữ ở nhiều nơi
- Sử dụng version control cho hình ảnh

## 📊 Monitoring

### Kiểm tra hình ảnh bị thiếu
```sql
-- Tìm sản phẩm không có hình ảnh
SELECT product_id, product_name, category_id 
FROM products 
WHERE image_url IS NULL OR image_url = '';

-- Tìm sản phẩm có đường dẫn hình ảnh không hợp lệ
SELECT product_id, product_name, image_url 
FROM products 
WHERE image_url IS NOT NULL 
AND image_url != '' 
AND image_url NOT LIKE '/images/%' 
AND image_url NOT LIKE 'C:/%';
```

## 🎯 Kết luận

Hệ thống hình ảnh sản phẩm đã được thiết kế linh hoạt và dễ sử dụng. Bạn có thể:

1. **Thêm hình ảnh thủ công** bằng cách copy file vào thư mục và cập nhật database
2. **Sử dụng script SQL** để cập nhật hàng loạt
3. **Tích hợp tính năng upload** trong ứng dụng (nếu cần)

Hệ thống sẽ tự động hiển thị hình mặc định nếu không tìm thấy hình ảnh, đảm bảo giao diện luôn đẹp và nhất quán.
