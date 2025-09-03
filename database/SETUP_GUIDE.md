# 🗄️ Database Setup Guide

## 📋 Yêu cầu

- **MySQL 8.0+** hoặc **MariaDB 10.5+**
- **Database name:** `cafe_management`
- **Port:** 3306 (default)

## 🚀 Cách setup

### 1. Tạo Database
```sql
CREATE DATABASE cafe_management 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;
```

### 2. Import Schema và Sample Data
```bash
# Option 1: Using mysql command line
mysql -u root -p cafe_management < cafe_database_structure.sql
mysql -u root -p cafe_management < cafe_sample_data.sql

# Option 2: Using setup scripts
cd database
setup_database.bat  # Windows
./setup_database.sh # Linux/Mac

# Option 3: Using phpMyAdmin
# - Chọn database cafe_management
# - Import file cafe_database_structure.sql
# - Import file cafe_sample_data.sql
```

### 3. Cấu hình Connection
Cập nhật file `database/database_config.properties`:

```properties
# Thay đổi thông tin phù hợp với setup của bạn
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
database.username=root
database.password=12345678
```

### 4. Test Connection
Chạy test để kiểm tra kết nối:

```bash
# Option 1: Chạy test class (khi có Java/Maven)
mvn test-compile exec:java -Dexec.mainClass="com.cafe.test.TestDatabaseConnection"

# Option 2: Chạy SQL test script
mysql -u root -p cafe_management < test_connection.sql
```

## 🔑 Sample Accounts

Sau khi import database, bạn có thể sử dụng các tài khoản mẫu:

| Username | Password | Role | Tên |
|----------|----------|------|-----|
| admin | 123456 | Admin | Nguyễn Tiến Ngọc Linh |
| manager | 123456 | Staff | Trần Xuân Quang Minh |
| cashier1 | 123456 | Staff | Vũ Hoàng Nam |
| waiter1 | 123456 | Staff | Dương Tuấn Minh |
| barista1 | 123456 | Staff | Dương Dức Thành |

## 🛠️ Troubleshooting

### Lỗi: "Access denied for user"
- Kiểm tra username/password trong `database_config.properties`
- Đảm bảo user có quyền truy cập database

### Lỗi: "Unknown database 'cafe_management'"
- Tạo database bằng lệnh `CREATE DATABASE cafe_management`
- Import file `cafe_management.sql`

### Lỗi: "Communications link failure"
- Kiểm tra MySQL server đã chạy chưa
- Kiểm tra port 3306 có available không
- Kiểm tra firewall settings

### Lỗi: "Table doesn't exist"
- Import lại file `cafe_database_structure.sql`
- Import file `cafe_sample_data.sql`
- Kiểm tra database name đúng chưa

## 📊 Database Schema

Database gồm 12 bảng chính:

1. **users** - Thông tin người dùng (Admin, Staff roles)
2. **categories** - Danh mục sản phẩm
3. **products** - Sản phẩm
4. **areas** - Khu vực
5. **tables** - Bàn
6. **customers** - Khách hàng
7. **orders** - Đơn hàng
8. **order_details** - Chi tiết đơn hàng
9. **promotions** - Khuyến mãi
10. **order_promotions** - Áp dụng khuyến mãi
11. **attendance** - Chấm công
12. **system_settings** - Cài đặt hệ thống

Chi tiết schema xem file `ERD.md`

## 🔧 Advanced Configuration

### Connection Pooling
Database connection được quản lý thông qua DatabaseConfig.java với cấu hình:
- Connection timeout: 30s
- Auto-reconnect: true
- Character encoding: UTF-8

### Performance Tuning
```properties
# Trong database/database_config.properties
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&autoReconnect=true
```

### Security
```properties
# Enable SSL (production)
database.url=jdbc:mysql://localhost:3306/cafe_management?useSSL=true&serverTimezone=Asia/Ho_Chi_Minh

# Stronger password requirements
# Mật khẩu được mã hóa bằng BCrypt trong ứng dụng
```