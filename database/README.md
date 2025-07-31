# 🗄️ Database - Hệ thống Quản lý Quán Cafe

## 📋 Mô tả
Database được thiết kế cho phần mềm quản lý quán cafe với đầy đủ các chức năng cần thiết.

## 🏗️ Cấu trúc Database

### 1. **Quản lý Người dùng & Phân quyền**
- `roles` - Vai trò trong hệ thống
- `users` - Thông tin người dùng

### 2. **Quản lý Menu & Sản phẩm**
- `categories` - Danh mục sản phẩm
- `products` - Thông tin sản phẩm

### 3. **Quản lý Khu vực & Bàn**
- `areas` - Khu vực trong quán
- `tables` - Thông tin bàn

### 4. **Quản lý Khách hàng**
- `customers` - Thông tin khách hàng

### 5. **Quản lý Đơn hàng**
- `orders` - Đơn hàng
- `order_details` - Chi tiết đơn hàng

### 6. **Quản lý Khuyến mãi**
- `promotions` - Chương trình khuyến mãi
- `order_promotions` - Áp dụng khuyến mãi cho đơn hàng

### 7. **Quản lý Nguyên liệu & Tồn kho**
- `ingredients` - Nguyên liệu
- `recipes` - Công thức (liên kết sản phẩm - nguyên liệu)
- `stock_in` - Nhập kho

### 8. **Quản lý Nhân viên**
- `attendance` - Chấm công

### 9. **Cài đặt Hệ thống**
- `system_settings` - Cài đặt chung

## 🚀 Cách sử dụng

### 1. **Cài đặt MySQL**
```bash
# Tải và cài đặt MySQL Server
# Hoặc sử dụng XAMPP/WAMP
```

### 2. **Tạo Database**
```bash
# Mở MySQL Command Line hoặc phpMyAdmin
# Chạy file cafe_management.sql
mysql -u root -p < cafe_management.sql
```

### 3. **Kết nối từ ứng dụng**
```java
// Thông tin kết nối
String url = "jdbc:mysql://localhost:3306/cafe_management";
String username = "root";
String password = "your_password";
```

## 📊 Dữ liệu mẫu

Database đã bao gồm dữ liệu mẫu:

### **Người dùng mẫu:**
- **Admin:** `admin` / `password` - Nguyễn Tiến Ngọc Linh
- **Manager:** `manager` / `password` - Trần Xuân Quang Minh
- **Cashier:** `cashier1` / `password` - Vũ Hoàng Nam
- **Waiter:** `waiter1` / `password` - Dương Tuấn Minh
- **Barista:** `barista1` / `password` - Nguyễn Thị Nguyệt Nhi

### **Sản phẩm mẫu:**
- Cà phê đen, cà phê sữa, Cappuccino, Latte
- Trà sữa trân châu, trà đá
- Nước cam ép, nước chanh dây
- Bánh tiramisu, bánh cheesecake
- Khoai tây chiên, gà rán

### **Khu vực & Bàn:**
- Tầng 1: Bàn 1, 2, 3
- Tầng 2: Bàn 4, 5
- VIP: Bàn VIP 1
- Sân thượng

## 🔧 Cấu hình

### **Thay đổi mật khẩu:**
```sql
UPDATE users SET password = 'new_hashed_password' WHERE username = 'admin';
```

### **Cập nhật thông tin quán:**
```sql
UPDATE system_settings SET setting_value = 'Tên quán mới' WHERE setting_key = 'cafe_name';
UPDATE system_settings SET setting_value = 'Địa chỉ mới' WHERE setting_key = 'cafe_address';
```

## 📈 Báo cáo SQL

### **Doanh thu theo ngày:**
```sql
SELECT 
    DATE(order_date) as date,
    COUNT(*) as total_orders,
    SUM(final_amount) as total_revenue
FROM orders 
WHERE payment_status = 'paid'
GROUP BY DATE(order_date)
ORDER BY date DESC;
```

### **Sản phẩm bán chạy:**
```sql
SELECT 
    p.product_name,
    SUM(od.quantity) as total_sold,
    SUM(od.total_price) as total_revenue
FROM order_details od
JOIN products p ON od.product_id = p.product_id
JOIN orders o ON od.order_id = o.order_id
WHERE o.payment_status = 'paid'
GROUP BY p.product_id
ORDER BY total_sold DESC;
```

### **Tồn kho nguyên liệu:**
```sql
SELECT 
    ingredient_name,
    current_stock,
    min_stock,
    unit,
    CASE 
        WHEN current_stock <= min_stock THEN 'Cần nhập'
        ELSE 'Đủ'
    END as status
FROM ingredients
WHERE is_active = 1
ORDER BY current_stock ASC;
```

## 🔒 Bảo mật

### **Backup Database:**
```bash
mysqldump -u root -p cafe_management > backup_$(date +%Y%m%d).sql
```

### **Restore Database:**
```bash
mysql -u root -p cafe_management < backup_file.sql
```

## 📝 Ghi chú

- Tất cả mật khẩu trong dữ liệu mẫu là: `password`
- Database sử dụng UTF-8 để hỗ trợ tiếng Việt
- Các bảng đều có timestamps để theo dõi thời gian tạo/cập nhật
- Foreign keys được thiết lập để đảm bảo tính toàn vẹn dữ liệu

## 👥 Phân chia công việc

### **Nguyễn Tiến Ngọc Linh (Leader)**
- Quản lý users, roles, attendance
- System settings và báo cáo tổng hợp

### **Trần Xuân Quang Minh**
- Quản lý categories, products
- Quản lý areas, tables

### **Vũ Hoàng Nam**
- Quản lý customers
- Quản lý promotions, order_promotions

### **Dương Tuấn Minh**
- Quản lý orders, order_details
- Thanh toán và trạng thái đơn hàng

### **Nguyễn Thị Nguyệt Nhi**
- Quản lý ingredients, recipes, stock_in
- Báo cáo tồn kho và chi phí 