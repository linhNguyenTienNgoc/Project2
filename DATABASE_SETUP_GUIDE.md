# Hướng dẫn Cấu hình Database SQL Server

## 🗄️ Bước 1: Cài đặt SQL Server

### Tải SQL Server:
1. Truy cập: https://www.microsoft.com/en-us/sql-server/sql-server-downloads
2. Tải SQL Server Express (miễn phí)
3. Chạy installer và làm theo hướng dẫn

### Cài đặt:
1. Chọn "Basic" installation
2. Ghi nhớ password cho SA account
3. Hoàn tất cài đặt

## 🛠️ Bước 2: Cài đặt SQL Server Management Studio (SSMS)

### Tải SSMS:
1. Truy cập: https://docs.microsoft.com/en-us/sql/ssms/download-sql-server-management-studio-ssms
2. Tải và cài đặt SSMS

## 📊 Bước 3: Tạo Database

### Mở SSMS:
1. Chạy SQL Server Management Studio
2. Kết nối đến SQL Server (localhost)
3. Authentication: SQL Server Authentication
4. Login: sa
5. Password: [password bạn đã đặt]

### Chạy Script:
1. Mở file `database_setup.sql`
2. Copy toàn bộ nội dung
3. Paste vào SSMS Query Editor
4. Click "Execute" hoặc F5

### Kết quả mong đợi:
```
Database setup completed successfully!
Sample data has been inserted.
You can now run the Coffee Shop Management application.

Default login accounts:
Admin: admin/admin123
Staff: staff1/staff123
Manager: manager/manager123
```

## ⚙️ Bước 4: Cấu hình Kết nối

### Chỉnh sửa file `src/main/resources/config/database.properties`:

```properties
# Database Configuration
db.url=jdbc:sqlserver://localhost:1433;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true
db.username=sa
db.password=your_password_here
db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver

# Connection Pool Settings
db.initialSize=5
db.maxActive=20
db.maxIdle=10
db.minIdle=5
db.maxWait=60000
```

### Thay đổi cần thiết:
1. **db.password**: Thay `your_password_here` bằng password SA của bạn
2. **db.url**: Nếu SQL Server không chạy trên localhost, thay đổi địa chỉ

## 🚀 Bước 5: Chạy Ứng dụng

### Chạy CoffeeShopApplication:
1. Trong IntelliJ IDEA, tìm file `CoffeeShopApplication.java`
2. Click chuột phải → "Run 'CoffeeShopApplication.main()'"

### Đăng nhập:
- **Admin**: admin/admin123
- **Staff**: staff1/staff123
- **Manager**: manager/manager123

## 🔧 Troubleshooting

### Lỗi 1: "Login failed for user 'sa'"
**Giải pháp:**
1. Mở SQL Server Configuration Manager
2. Enable SQL Server Authentication Mode
3. Restart SQL Server

### Lỗi 2: "Connection refused"
**Giải pháp:**
1. Kiểm tra SQL Server đang chạy
2. Kiểm tra port 1433
3. Kiểm tra firewall

### Lỗi 3: "SSL/TLS connection"
**Giải pháp:**
- Đảm bảo `encrypt=true;trustServerCertificate=true` trong connection string

### Lỗi 4: "Driver not found"
**Giải pháp:**
1. Kiểm tra SQL Server JDBC driver trong pom.xml
2. Refresh Maven project
3. Clean và rebuild project

## 📋 Kiểm tra Database

### Trong SSMS, chạy các query sau:

```sql
-- Kiểm tra bảng Employee
SELECT * FROM CoffeeShopManagement.dbo.Employee;

-- Kiểm tra bảng CoffeeTable
SELECT * FROM CoffeeShopManagement.dbo.CoffeeTable;

-- Kiểm tra bảng Menu
SELECT * FROM CoffeeShopManagement.dbo.Menu;
```

## ✅ Kết quả mong đợi

Sau khi hoàn thành:
- ✅ Database CoffeeShopManagement được tạo
- ✅ 5 bảng được tạo với dữ liệu mẫu
- ✅ Ứng dụng kết nối thành công
- ✅ Có thể đăng nhập và sử dụng tất cả tính năng

## 🎉 Hoàn thành!

Bây giờ ứng dụng đã kết nối với database thực tế và có thể:
- Lưu trữ dữ liệu thực
- Cập nhật trạng thái bàn
- Quản lý menu
- Phân quyền người dùng
- Tạo báo cáo

**Chúc bạn thành công! 🚀** 