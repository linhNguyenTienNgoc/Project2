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
mysql -u root -p cafe_management < cafe_management.sql

# Option 2: Using phpMyAdmin
# - Chọn database cafe_management
# - Import file cafe_management.sql
```

### 3. Cấu hình Connection
Cập nhật file `src/main/resources/database_config.properties`:

```properties
# Thay đổi thông tin phù hợp với setup của bạn
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
database.username=root
database.password=YOUR_MYSQL_PASSWORD
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
| admin | password | Admin | Nguyễn Tiến Ngọc Linh |
| manager | password | Manager | Trần Xuân Quang Minh |
| cashier1 | password | Cashier | Vũ Hoàng Nam |
| waiter1 | password | Waiter | Dương Tuấn Minh |
| barista1 | password | Barista | Nguyễn Thị Nguyệt Nhi |

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
- Import lại file `cafe_management.sql`
- Kiểm tra database name đúng chưa

## 📊 Database Schema

Database gồm 12 bảng chính:

1. **roles** - Vai trò người dùng
2. **users** - Thông tin người dùng  
3. **categories** - Danh mục sản phẩm
4. **products** - Sản phẩm
5. **areas** - Khu vực
6. **tables** - Bàn
7. **customers** - Khách hàng
8. **orders** - Đơn hàng
9. **order_details** - Chi tiết đơn hàng
10. **promotions** - Khuyến mãi
11. **ingredients** - Nguyên liệu
12. **attendance** - Chấm công

Chi tiết schema xem file `ERD.md`

## 🔧 Advanced Configuration

### Connection Pooling
HikariCP được sử dụng cho connection pooling với cấu hình:
- Min connections: 5
- Max connections: 20
- Connection timeout: 30s
- Idle timeout: 10 minutes

### Performance Tuning
```properties
# Trong database_config.properties
database.maxActive=20
database.connectionTimeout=30000
database.idleTimeout=600000
```

### Security
```properties
# Enable SSL (production)
database.url=jdbc:mysql://localhost:3306/cafe_management?useSSL=true&serverTimezone=Asia/Ho_Chi_Minh

# Stronger password requirements
security.password.strength=12
```