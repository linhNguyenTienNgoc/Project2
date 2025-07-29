# 🚀 Hướng dẫn chạy Coffee Shop Management System

## 📋 Bước 1: Tạo Database

1. **Mở SQL Server Management Studio**
2. **Kết nối** với server `M12345\M12345` (Windows Authentication)
3. **Mở file `database_setup.sql`** trong project
4. **Chạy toàn bộ script** (F5 hoặc Execute)
5. **Kiểm tra** database `CoffeeShopManagement` đã được tạo

## ⚙️ Bước 2: Cấu hình ứng dụng

File `application.properties` đã được cấu hình sẵn:
```properties
spring.datasource.url=jdbc:sqlserver://M12345\\M12345;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true
```

## 🏃‍♂️ Bước 3: Chạy ứng dụng

### Phương pháp 1: IntelliJ IDEA (Khuyến nghị)
1. **Mở project** trong IntelliJ IDEA
2. **Đợi** IntelliJ download dependencies
3. **Tìm file** `CoffeeShopManagementApplication.java`
4. **Click chuột phải** → Run `CoffeeShopManagementApplication.main()`

### Phương pháp 2: Command Line
```bash
# Nếu có Maven
mvn spring-boot:run

# Hoặc dùng Maven Wrapper
./mvnw.cmd spring-boot:run
```

## 🌐 Bước 4: Truy cập ứng dụng

Mở trình duyệt: **http://localhost:8080**

## 👤 Tài khoản đăng nhập

| Vai trò | Username | Password |
|---------|----------|----------|
| **Admin** | `admin` | `admin123` |
| **Staff** | `staff1` | `staff123` |
| **Staff** | `staff2` | `staff123` |

## 🔧 Troubleshooting

### Lỗi kết nối database:
- Kiểm tra SQL Server đang chạy
- Kiểm tra server name `M12345\M12345`
- Kiểm tra database `CoffeeShopManagement` đã tạo

### Lỗi Maven:
- Cài đặt Maven hoặc dùng IntelliJ
- Chạy `mvn clean install` trước

### Lỗi port 8080:
- Thay đổi port trong `application.properties`
- Hoặc tắt ứng dụng khác đang dùng port 8080

## 📱 Tính năng chính

- ✅ **Đăng nhập/Đăng xuất**
- ✅ **Dashboard** với thống kê
- ✅ **Quản lý Menu** (Admin)
- ✅ **Quản lý Bàn** (Admin)
- ✅ **Tạo Hóa đơn** (Staff)
- ✅ **Quản lý Nhân viên** (Admin)
- ✅ **Báo cáo** với biểu đồ (Admin)

## 🎯 Lưu ý

- **Admin**: Có quyền truy cập tất cả tính năng
- **Staff**: Chỉ xem và tạo hóa đơn
- Dữ liệu mẫu đã được tạo sẵn
- Giao diện responsive trên mọi thiết bị

## 📞 Hỗ trợ

Nếu gặp lỗi, kiểm tra:
1. Database đã tạo chưa?
2. SQL Server đang chạy không?
3. Port 8080 có bị chiếm không?
4. Java 8+ đã cài chưa? 