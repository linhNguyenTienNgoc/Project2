# Hướng dẫn chạy ứng dụng Cafe Management System

## Yêu cầu hệ thống

- **Java**: JDK 21 hoặc cao hơn
- **Maven**: 3.6.0 hoặc cao hơn
- **MySQL**: 8.0 hoặc cao hơn (để chạy database)

## Cách chạy ứng dụng

### Phương pháp 1: Sử dụng run-app.bat (Khuyến nghị)

1. Mở Command Prompt hoặc PowerShell
2. Di chuyển đến thư mục gốc của project
3. Chạy lệnh:
   ```bash
   run-app.bat
   ```

### Phương pháp 2: Sử dụng run-simple.bat

1. Mở Command Prompt hoặc PowerShell
2. Di chuyển đến thư mục gốc của project
3. Chạy lệnh:
   ```bash
   run-simple.bat
   ```

### Phương pháp 3: Build và chạy JAR file

1. Mở Command Prompt hoặc PowerShell
2. Di chuyển đến thư mục gốc của project
3. Chạy lệnh:
   ```bash
   build-and-run.bat
   ```

### Phương pháp 4: Chạy thủ công bằng Maven

1. Mở Command Prompt hoặc PowerShell
2. Di chuyển đến thư mục gốc của project
3. Chạy các lệnh:
   ```bash
   mvn clean compile
   mvn javafx:run
   ```

## Xử lý lỗi thường gặp

### Lỗi 1: "Java is not installed or not in PATH"
**Giải pháp:**
- Cài đặt Java JDK 21 hoặc cao hơn
- Thêm Java vào PATH environment variable
- Kiểm tra bằng lệnh: `java -version`

### Lỗi 2: "Maven is not installed or not in PATH"
**Giải pháp:**
- Cài đặt Maven 3.6.0 hoặc cao hơn
- Thêm Maven vào PATH environment variable
- Kiểm tra bằng lệnh: `mvn -version`

### Lỗi 3: "Compilation failed"
**Giải pháp:**
- Kiểm tra Java version (phải là JDK 21+)
- Xóa thư mục `target` và thử lại
- Chạy: `mvn clean compile`

### Lỗi 4: "Database connection failed"
**Giải pháp:**
- Đảm bảo MySQL Server đang chạy
- Kiểm tra file `database_config.properties`
- Tạo database `cafe_management` nếu chưa có

### Lỗi 5: "JavaFX runtime components are missing"
**Giải pháp:**
- Đảm bảo đang sử dụng Java 21+
- Chạy: `mvn clean compile javafx:run`

## Cấu hình database

1. Tạo database MySQL:
   ```sql
   CREATE DATABASE cafe_management;
   ```

2. Chạy script SQL trong file `database/cafe_management.sql`

3. Cập nhật thông tin kết nối trong file `config/database_config.properties`

## Thông tin đăng nhập mặc định

### Tài khoản Admin:
- **Username**: admin
- **Password**: 123456
- **Role**: admin

### Các tài khoản khác:
- **Manager**: manager / 123456
- **Cashier**: cashier1 / 123456  
- **Waiter**: waiter1 / 123456
- **Barista**: barista1 / 123456

### Lưu ý:
- Tất cả password đều là: `123456`
- Admin sẽ được chuyển đến màn hình admin dashboard
- Các role khác sẽ được chuyển đến màn hình dashboard thường

## Cấu trúc thư mục

```
Project2/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/cafe/
│   │   └── resources/
│   │       ├── fxml/
│   │       ├── css/
│   │       └── images/
├── config/
├── database/
├── run-app.bat
├── run-simple.bat
├── build-and-run.bat
└── pom.xml
```

## Liên hệ hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra log lỗi trong console
2. Đảm bảo đã cài đặt đúng các yêu cầu hệ thống
3. Thử các phương pháp chạy khác nhau
4. Liên hệ team phát triển 