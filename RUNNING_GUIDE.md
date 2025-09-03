# Hướng dẫn chạy ứng dụng Cafe Management System

## Yêu cầu hệ thống

- **Java**: JDK 21 hoặc cao hơn
- **Maven**: 3.6.0 hoặc cao hơn
- **MySQL**: 8.0 hoặc cao hơn
- **JavaFX**: 24.0.2 (đã bao gồm trong project)

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
- Kiểm tra file `database/database_config.properties`
- Chạy script setup database: `cd database && setup_database.bat`
- Import database schema: `mysql -u root -p < database/cafe_database_structure.sql`

### Lỗi 5: "JavaFX runtime components are missing"
**Giải pháp:**
- JavaFX 24.0.2 đã được bao gồm trong project
- Đảm bảo đang sử dụng Java 21+
- Chạy: `mvn clean compile javafx:run`

## Cấu hình database

1. Tạo database MySQL:
   ```sql
   CREATE DATABASE cafe_management;
   ```

2. Import database schema và sample data:
   ```bash
   # Import schema
   mysql -u root -p < database/cafe_database_structure.sql
   
   # Import sample data
   mysql -u root -p < database/cafe_sample_data.sql
   
   # Hoặc sử dụng script tự động
   cd database
   setup_database.bat  # Windows
   ./setup_database.sh # Linux/Mac
   ```

3. Cập nhật thông tin kết nối trong file `database/database_config.properties`

## Thông tin đăng nhập mặc định

### Tài khoản Admin:
- **Username**: admin
- **Password**: 123456
- **Role**: admin

### Các tài khoản khác:
- **Manager**: manager / 123456 (Staff role)
- **Cashier**: cashier1 / 123456 (Staff role)
- **Waiter**: waiter1 / 123456 (Staff role)
- **Barista**: barista1 / 123456 (Staff role)

### Lưu ý:
- Tất cả password đều là: `123456`
- Admin sẽ được chuyển đến màn hình admin dashboard
- Các role khác sẽ được chuyển đến màn hình dashboard thường

## Cấu trúc thư mục chính

```
Project2/
├── src/main/java/com/cafe/          # Source code (77 Java files)
├── src/main/resources/              # Resources (FXML, CSS, Images)
├── database/                        # Database files & scripts
├── docs/                           # Documentation
├── lib/javafx-sdk-24.0.2/         # JavaFX SDK
├── run-app.bat                     # Main run script
├── run-simple.bat                  # Simple run script
├── build-and-run.bat               # Build and run script
├── test-dashboard.bat              # Test dashboard script
└── pom.xml                         # Maven configuration
```

Chi tiết cấu trúc xem file [project_structure.md](project_structure.md)

## Liên hệ hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra log lỗi trong console
2. Đảm bảo đã cài đặt đúng các yêu cầu hệ thống
3. Thử các phương pháp chạy khác nhau
4. Liên hệ team phát triển 