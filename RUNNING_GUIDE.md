# 🚀 Hướng dẫn chạy ứng dụng Cafe Management System v2.0

## 📋 Yêu cầu hệ thống

### **Minimum Requirements**
- **Java**: JDK 17 hoặc cao hơn
- **Maven**: 3.6.0 hoặc cao hơn
- **MySQL**: 8.0 hoặc cao hơn
- **RAM**: 4GB
- **Storage**: 2GB free space

### **Recommended Requirements**
- **Java**: JDK 17 LTS
- **Maven**: 3.8.0+
- **MySQL**: 8.0+
- **RAM**: 8GB
- **Storage**: 5GB free space
- **OS**: Windows 11, macOS 12+, Ubuntu 20.04+

## 🗄️ Cài đặt Database (Bắt buộc)

### **1. Tạo Database với Schema tối ưu**
```bash
# Import optimized schema (v2.0)
mysql -u root -p < database/cafe_management.sql
```

### **2. Import dữ liệu mẫu**
```bash
# Import sample data
mysql -u root -p cafe_management < database/data.sql
```

### **3. Kiểm tra kết nối**
```bash
# Test database connection
mysql -u root -p < database/test_connection.sql
```

## 🚀 Cách chạy ứng dụng

### **Phương pháp 1: Sử dụng run-app.bat (Khuyến nghị)**
```bash
# Windows
run-app.bat

# Linux/Mac
./run-app.sh
```

### **Phương pháp 2: Sử dụng run-simple.bat**
```bash
# Windows
run-simple.bat

# Linux/Mac
./run-simple.sh
```

### **Phương pháp 3: Build và chạy JAR file**
```bash
# Windows
build-and-run.bat

# Linux/Mac
./build-and-run.sh
```

### **Phương pháp 4: Chạy thủ công bằng Maven**
```bash
# Clean và compile
mvn clean compile

# Run với JavaFX
mvn javafx:run

# Hoặc build JAR và chạy
mvn clean package
java -jar target/cafe-management-2.0.0.jar
```

## 🔧 Cấu hình Database

### **1. Cập nhật thông tin kết nối**
```properties
# config/database.properties
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
database.username=root
database.password=your_password
database.driver=com.mysql.cj.jdbc.Driver
database.pool.size=10
database.pool.maxLifetime=1800000
```

### **2. Cấu hình ứng dụng**
```properties
# config/application.properties
app.name=Cafe Management System
app.version=2.0.0
app.language=vi
app.currency=VND
app.tax.rate=10
app.loyalty.points.rate=1
```

## 👥 Thông tin đăng nhập mặc định

### **Tài khoản Admin:**
- **Username**: `admin`
- **Password**: `123456`
- **Role**: Admin (Quản trị viên hệ thống)

### **Tài khoản Waiter:**
- **Username**: `waiter1`
- **Password**: `123456`
- **Role**: Waiter (Nhân viên phục vụ)

- **Username**: `waiter2`
- **Password**: `123456`
- **Role**: Waiter (Nhân viên phục vụ)

### **Tài khoản Barista:**
- **Username**: `barista1`
- **Password**: `123456`
- **Role**: Barista (Nhân viên pha chế)

- **Username**: `barista2`
- **Password**: `123456`
- **Role**: Barista (Nhân viên pha chế)

## ⚠️ Xử lý lỗi thường gặp

### **Lỗi 1: "Java is not installed or not in PATH"**
**Giải pháp:**
```bash
# Cài đặt Java JDK 17+
# Thêm Java vào PATH environment variable
# Kiểm tra version
java -version
```

### **Lỗi 2: "Maven is not installed or not in PATH"**
**Giải pháp:**
```bash
# Cài đặt Maven 3.6.0+
# Thêm Maven vào PATH environment variable
# Kiểm tra version
mvn -version
```

### **Lỗi 3: "Compilation failed"**
**Giải pháp:**
```bash
# Kiểm tra Java version (phải là JDK 17+)
java -version

# Xóa thư mục target và thử lại
rm -rf target/
mvn clean compile
```

### **Lỗi 4: "Database connection failed"**
**Giải pháp:**
```bash
# Đảm bảo MySQL Server đang chạy
sudo systemctl start mysql  # Linux
net start mysql             # Windows

# Kiểm tra file config
cat config/database.properties

# Test connection
mysql -u root -p -e "USE cafe_management; SHOW TABLES;"
```

### **Lỗi 5: "JavaFX runtime components are missing"**
**Giải pháp:**
```bash
# Đảm bảo đang sử dụng Java 17+
java -version

# Clean và run lại
mvn clean compile javafx:run
```

### **Lỗi 6: "Charset encoding issues"**
**Giải pháp:**
```bash
# Đảm bảo database sử dụng UTF8MB4
mysql -u root -p -e "ALTER DATABASE cafe_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"

# Reimport schema
mysql -u root -p < database/cafe_management.sql
```

### **Lỗi 7: "Permission denied"**
**Giải pháp:**
```bash
# Cấp quyền thực thi cho scripts
chmod +x *.sh

# Windows: Chạy Command Prompt với quyền Administrator
```

## 🔍 Kiểm tra hệ thống

### **1. Kiểm tra Database**
```sql
-- Kiểm tra tables
SHOW TABLES;

-- Kiểm tra users
SELECT username, role_name FROM users u JOIN roles r ON u.role_id = r.role_id;

-- Kiểm tra products
SELECT COUNT(*) as total_products FROM products;

-- Kiểm tra views
SHOW FULL TABLES WHERE Table_type = 'VIEW';
```

### **2. Kiểm tra Triggers**
```sql
-- Kiểm tra triggers
SHOW TRIGGERS;

-- Test trigger functionality
INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price) 
VALUES (1, 1, 1, 25000, 25000);
```

### **3. Kiểm tra Procedures**
```sql
-- Kiểm tra stored procedures
SHOW PROCEDURE STATUS WHERE Db = 'cafe_management';

-- Test procedure
CALL sp_create_order(1, 1, 1, 'Test order');
```

## 📊 Tính năng mới trong v2.0

### **Database Optimization**
- ✅ UTF8MB4 charset cho tiếng Việt và emoji
- ✅ 45+ indexes cho hiệu suất tối đa
- ✅ 3 Views cho báo cáo nhanh
- ✅ 3 Triggers tự động hóa
- ✅ 2 Stored Procedures xử lý logic phức tạp
- ✅ 2 Functions tiện ích

### **Advanced Features**
- ✅ Quản lý tồn kho với min stock level
- ✅ Auto-update stock khi tạo đơn hàng
- ✅ Hệ thống khuyến mãi nâng cao
- ✅ Audit logging mọi thay đổi
- ✅ JSON permissions cho quản lý quyền
- ✅ Fulltext search cho sản phẩm và khách hàng

### **Security Enhancements**
- ✅ Password hashing với BCrypt
- ✅ Email/Phone validation với regex
- ✅ Role-based access control
- ✅ Session management cải tiến
- ✅ IP address logging

## 🔄 Migration từ v1.0

### **Breaking Changes:**
- Role system: Giảm từ 5 roles xuống 3 roles
- Database charset: Chuyển sang UTF8MB4
- New constraints: Có thể gây lỗi với dữ liệu cũ
- Table structure: Thêm columns mới

### **Migration Steps:**
1. **Backup database hiện tại**
   ```bash
   mysqldump -u root -p cafe_management > backup_v1.sql
   ```

2. **Drop và recreate database**
   ```sql
   DROP DATABASE cafe_management;
   CREATE DATABASE cafe_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```

3. **Import optimized schema**
   ```bash
   mysql -u root -p < database/cafe_management.sql
   ```

4. **Import sample data**
   ```bash
   mysql -u root -p cafe_management < database/data.sql
   ```

5. **Update application configuration**
   - Cập nhật database.properties
   - Cập nhật application.properties

6. **Test tất cả tính năng**
   - Đăng nhập với tài khoản mới
   - Test order creation
   - Test inventory management
   - Verify triggers và procedures

## 📞 Support

### **Tài liệu tham khảo:**
- [README.md](README.md) - Tổng quan project
- [CHANGELOG.md](CHANGELOG.md) - Lịch sử thay đổi
- [Database README](database/README.md) - Tài liệu database
- [Setup Guide](database/SETUP_GUIDE.md) - Hướng dẫn cài đặt

### **Liên hệ:**
- **Email:** cafe.management@example.com
- **GitHub:** https://github.com/your-username/cafe-management
- **Documentation:** https://cafe-management.example.com/docs

---

**Lưu ý:** Đây là phiên bản 2.0.0 với nhiều cải tiến quan trọng. Vui lòng đọc kỹ hướng dẫn migration nếu đang sử dụng phiên bản cũ. 