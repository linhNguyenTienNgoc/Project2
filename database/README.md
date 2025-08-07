# 🗄️ Database - Hệ thống Quản lý Quán Cafe (v2.0)

## 📋 Mô tả
Database được thiết kế tối ưu cho phần mềm quản lý quán cafe với đầy đủ các chức năng cần thiết, hiệu suất cao và bảo mật tốt.

## 🏗️ Cấu trúc Database (Tối ưu)

### 1. **Quản lý Người dùng & Phân quyền**
- `roles` - Vai trò trong hệ thống (Admin, Waiter, Barista)
- `users` - Thông tin người dùng với JSON permissions
- `system_logs` - Audit trail cho mọi thay đổi

### 2. **Quản lý Menu & Sản phẩm**
- `categories` - Danh mục sản phẩm với sort_order
- `products` - Thông tin sản phẩm với SKU và inventory management

### 3. **Quản lý Khu vực & Bàn**
- `areas` - Khu vực trong quán với capacity
- `tables` - Thông tin bàn với unique constraints

### 4. **Quản lý Khách hàng**
- `customers` - Thông tin khách hàng với loyalty system

### 5. **Quản lý Đơn hàng**
- `orders` - Đơn hàng với tax calculation
- `order_details` - Chi tiết đơn hàng với discount_percent

### 6. **Quản lý Khuyến mãi**
- `promotions` - Chương trình khuyến mãi nâng cao
- `order_promotions` - Áp dụng khuyến mãi cho đơn hàng

### 7. **Quản lý Nhân viên**
- `attendance` - Chấm công với overtime tracking

### 8. **Cài đặt Hệ thống**
- `system_settings` - Cài đặt chung với type validation

## 🚀 Cách sử dụng

### 1. **Cài đặt MySQL**
```bash
# Tải và cài đặt MySQL Server 8.0+
# Hoặc sử dụng XAMPP/WAMP
```

### 2. **Tạo Database (Tối ưu)**
```bash
# Import optimized schema
mysql -u root -p < cafe_management.sql

# Import sample data
mysql -u root -p cafe_management < data.sql
```

### 3. **Kết nối từ ứng dụng**
```java
// Thông tin kết nối với UTF8MB4
String url = "jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
String username = "root";
String password = "your_password";
```

## 📊 Database Features

### **Optimized Schema**
- **10 Tables** với quan hệ rõ ràng
- **45+ Indexes** cho hiệu suất tối đa
- **3 Views** cho báo cáo nhanh
- **3 Triggers** tự động hóa
- **2 Stored Procedures** xử lý logic phức tạp
- **2 Functions** tiện ích

### **Advanced Features**
- **UTF8MB4** - Hỗ trợ đầy đủ tiếng Việt và emoji
- **JSON Fields** - Lưu trữ dữ liệu linh hoạt
- **Fulltext Search** - Tìm kiếm nhanh chóng
- **Audit Logging** - Theo dõi mọi thay đổi
- **Auto-increment Optimization** - Hiệu suất cao

## 📊 Dữ liệu mẫu

Database đã bao gồm dữ liệu mẫu đầy đủ:

### **Người dùng mẫu:**
- **Admin:** `admin` / `123456` - Nguyễn Tiến Ngọc Linh
- **Waiter:** `waiter1` / `123456` - Dương Tuấn Minh
- **Waiter:** `waiter2` / `123456` - Trần Thị Hương
- **Barista:** `barista1` / `123456` - Dương Đức Thành
- **Barista:** `barista2` / `123456` - Phạm Thị Mai

### **Sản phẩm mẫu (24 sản phẩm):**
- **Cà phê:** Cà phê đen, cà phê sữa, Cappuccino, Latte, Americano, Espresso
- **Trà:** Trà sữa trân châu, trà đá, trà sữa matcha, trà sữa thái, trà hoa cúc
- **Nước ép:** Nước cam ép, nước chanh dây, nước dừa tươi, nước ép táo
- **Bánh:** Bánh tiramisu, bánh cheesecake, bánh brownie, bánh croissant
- **Snack:** Khoai tây chiên, gà rán, bánh mì sandwich, salad trộn

### **Khu vực & Bàn (10 bàn):**
- **Tầng 1:** Bàn 1, 2, 3, 4 (4-6 người)
- **Tầng 2:** Bàn 5, 6, 7 (4-8 người)
- **Sân thượng:** Bàn 8, 9 (4-6 người)
- **VIP:** Bàn VIP 1, 2 (4-6 người)

### **Khách hàng mẫu (8 khách hàng):**
- Thông tin đầy đủ với loyalty points và purchase history
- Địa chỉ và thông tin liên hệ thực tế

## 🔧 Cấu hình

### **Thay đổi mật khẩu:**
```sql
-- Sử dụng BCrypt hash
UPDATE users SET password = 'new_hashed_password' WHERE username = 'admin';
```

### **Cập nhật thông tin quán:**
```sql
UPDATE system_settings SET setting_value = 'Tên quán mới' WHERE setting_key = 'cafe_name';
UPDATE system_settings SET setting_value = 'Địa chỉ mới' WHERE setting_key = 'cafe_address';
```

### **Cấu hình thuế:**
```sql
UPDATE system_settings SET setting_value = '8' WHERE setting_key = 'tax_rate';
```

## 📈 Báo cáo SQL (Tối ưu)

### **Doanh thu theo ngày (sử dụng View):**
```sql
SELECT * FROM v_daily_revenue 
WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
ORDER BY sale_date DESC;
```

### **Sản phẩm tồn kho (sử dụng View):**
```sql
SELECT * FROM v_product_inventory 
WHERE stock_status = 'Low Stock' OR stock_status = 'Out of Stock'
ORDER BY stock_quantity ASC;
```

### **Tổng quan đơn hàng (sử dụng View):**
```sql
SELECT * FROM v_order_summary 
WHERE order_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)
ORDER BY order_date DESC;
```

### **Sản phẩm bán chạy:**
```sql
SELECT 
    p.product_name,
    p.sku,
    SUM(od.quantity) as total_sold,
    SUM(od.total_price) as total_revenue,
    c.category_name
FROM order_details od
JOIN products p ON od.product_id = p.product_id
JOIN categories c ON p.category_id = c.category_id
JOIN orders o ON od.order_id = o.order_id
WHERE o.payment_status = 'paid'
  AND o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY p.product_id
ORDER BY total_sold DESC
LIMIT 10;
```

### **Khách hàng VIP:**
```sql
SELECT 
    customer_code,
    full_name,
    loyalty_points,
    total_spent,
    last_visit
FROM customers 
WHERE loyalty_points >= 100
ORDER BY total_spent DESC;
```

## 🔒 Bảo mật & Backup

### **Backup Database:**
```bash
# Backup với charset UTF8MB4
mysqldump -u root -p --default-character-set=utf8mb4 cafe_management > backup_$(date +%Y%m%d).sql
```

### **Restore Database:**
```bash
mysql -u root -p cafe_management < backup_file.sql
```

### **Security Best Practices:**
- Sử dụng strong passwords
- Enable SSL connections
- Regular backups
- Monitor system_logs
- Update MySQL regularly

## 🚀 Performance Tips

### **Index Optimization:**
```sql
-- Kiểm tra index usage
SHOW INDEX FROM table_name;

-- Analyze table performance
ANALYZE TABLE table_name;
```

### **Query Optimization:**
```sql
-- Sử dụng EXPLAIN để analyze queries
EXPLAIN SELECT * FROM orders WHERE order_date >= '2025-01-01';

-- Sử dụng Views cho complex queries
SELECT * FROM v_order_summary WHERE payment_status = 'paid';
```

### **Connection Pooling:**
```properties
# config/database.properties
database.pool.size=10
database.pool.maxLifetime=1800000
database.pool.connectionTimeout=30000
```

## 📝 Ghi chú

- **Mật khẩu mặc định:** `123456` (cần thay đổi trong production)
- **Charset:** UTF8MB4 cho hỗ trợ đầy đủ tiếng Việt và emoji
- **Timestamps:** Tất cả bảng đều có created_at và updated_at
- **Foreign Keys:** Được thiết lập với CASCADE/RESTRICT actions
- **Constraints:** Check constraints đảm bảo data integrity
- **Triggers:** Tự động cập nhật stock, loyalty points, và audit logs

## 🔄 Migration từ v1.0

### **Breaking Changes:**
- Role system: Giảm từ 5 roles xuống 3 roles
- Database charset: Chuyển sang UTF8MB4
- New constraints: Có thể gây lỗi với dữ liệu cũ
- Table structure: Thêm columns mới

### **Migration Steps:**
1. Backup database hiện tại
2. Drop và recreate database
3. Import optimized schema
4. Import sample data
5. Update application configuration
6. Test tất cả tính năng

## 📞 Support

- **Version:** 2.0.0
- **MySQL Version:** 8.0+
- **Charset:** UTF8MB4
- **Collation:** utf8mb4_unicode_ci

