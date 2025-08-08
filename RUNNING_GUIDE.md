# Hướng Dẫn Chạy Ứng Dụng Cafe Management System

## Các File Chạy Ứng Dụng

### 1. `start-cafe.bat` (Khuyến nghị)
- **Mô tả**: Menu chính để chạy ứng dụng với các tùy chọn
- **Cách sử dụng**: Bấm đúp vào file này
- **Tùy chọn**:
  - `[1]` Run Application (Full Build) - Chạy với build đầy đủ
  - `[2]` Quick Start (Skip Build) - Chạy nhanh (bỏ qua build)
  - `[3]` Build Only (No Run) - Chỉ build, không chạy
  - `[4]` Exit - Thoát

### 2. `run-app.bat` (Build và Chạy)
- **Mô tả**: Chạy ứng dụng với build đầy đủ
- **Cách sử dụng**: Bấm đúp vào file này
- **Quy trình**:
  1. Kiểm tra Java và Maven
  2. Clean và compile project
  3. Chạy ứng dụng JavaFX

### 3. `run-quick.bat` (Chạy Nhanh)
- **Mô tả**: Chạy ứng dụng nhanh (bỏ qua build nếu đã compile)
- **Cách sử dụng**: Bấm đúp vào file này
- **Lưu ý**: Nếu chưa build, sẽ tự động chuyển sang full build

### 4. `build-only.bat` (Chỉ Build)
- **Mô tả**: Chỉ compile project, không chạy ứng dụng
- **Cách sử dụng**: Bấm đúp vào file này
- **Mục đích**: Build trước để sau này chạy nhanh hơn

## Yêu Cầu Hệ Thống

### Java
- **Phiên bản**: Java 21 hoặc mới hơn
- **Kiểm tra**: `java -version`
- **Tải về**: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) hoặc [OpenJDK](https://adoptium.net/)

### Maven
- **Phiên bản**: Apache Maven 3.6+ 
- **Kiểm tra**: `mvn -version`
- **Tải về**: [Apache Maven](https://maven.apache.org/download.cgi)

### MySQL Database
- **Phiên bản**: MySQL 8.0+
- **Database**: `cafe_management`
- **Cấu hình**: Xem file `config/application.properties`

## Cách Sử Dụng

### Lần Đầu Chạy
1. Đảm bảo đã cài đặt Java và Maven
2. Đảm bảo MySQL đang chạy và có database `cafe_management`
3. Bấm đúp vào `start-cafe.bat`
4. Chọn tùy chọn `[1]` để build và chạy lần đầu

### Các Lần Chạy Sau
1. Bấm đúp vào `start-cafe.bat`
2. Chọn tùy chọn `[2]` để chạy nhanh
3. Hoặc bấm đúp trực tiếp vào `run-quick.bat`

## Xử Lý Lỗi

### Lỗi "Java is not installed"
- Cài đặt Java 21+ và thêm vào PATH
- Restart Command Prompt sau khi cài đặt

### Lỗi "Maven is not installed"
- Cài đặt Apache Maven và thêm vào PATH
- Restart Command Prompt sau khi cài đặt

### Lỗi "pom.xml not found"
- Đảm bảo đang chạy script từ thư mục gốc của project
- Kiểm tra file `pom.xml` có tồn tại không

### Lỗi Database Connection
- Kiểm tra MySQL có đang chạy không
- Kiểm tra thông tin kết nối trong `config/application.properties`
- Đảm bảo database `cafe_management` đã được tạo

### Lỗi Compilation
- Kiểm tra Java version (cần Java 21+)
- Xóa thư mục `target` và thử build lại
- Kiểm tra lỗi syntax trong code

## Cấu Trúc Thư Mục

```
Project2/
├── start-cafe.bat          # Menu chính (khuyến nghị)
├── run-app.bat            # Build và chạy đầy đủ
├── run-quick.bat          # Chạy nhanh
├── build-only.bat         # Chỉ build
├── pom.xml                # Cấu hình Maven
├── config/                # Cấu hình ứng dụng
├── src/                   # Mã nguồn
└── target/                # Thư mục build (tự động tạo)
```

## Lưu Ý

- **Lần đầu**: Sử dụng `run-app.bat` hoặc `start-cafe.bat` → `[1]`
- **Các lần sau**: Sử dụng `run-quick.bat` hoặc `start-cafe.bat` → `[2]`
- **Cửa sổ**: Ứng dụng sẽ mở trong cửa sổ JavaFX riêng biệt
- **Đóng ứng dụng**: Đóng cửa sổ JavaFX, không đóng Command Prompt
- **Logs**: Xem thông tin chi tiết trong Command Prompt để debug

## Hỗ Trợ

Nếu gặp vấn đề, hãy:
1. Kiểm tra các yêu cầu hệ thống
2. Xem thông báo lỗi trong Command Prompt
3. Thử chạy lại với tùy chọn full build
4. Kiểm tra cấu hình database 