# ☕ Cafe Management System

Hệ thống quản lý quán cà phê được phát triển bằng JavaFX với kiến trúc MVC chuyên nghiệp.

## 🚀 Tính năng chính

### 👥 Quản lý Người dùng & Phân quyền
- **Đăng nhập/Đăng xuất** với xác thực bảo mật
- **Phân quyền** theo vai trò (Admin, Staff)
- **Quản lý nhân viên** với chấm công tự động

### 🍽️ Quản lý Menu & Sản phẩm
- **Danh mục sản phẩm** (Cà phê, Trà, Bánh, Món ăn...)
- **Quản lý sản phẩm** với hình ảnh, giá cả, mô tả
- **Công thức nấu** liên kết sản phẩm với nguyên liệu

### 🪑 Quản lý Khu vực & Bàn
- **Khu vực** (Tầng 1, Tầng 2, VIP, Sân thượng)
- **Trạng thái bàn** (Trống, Có khách, Đặt trước, Dọn dẹp)
- **Đặt bàn** và quản lý lịch

### 📋 Quản lý Đơn hàng
- **Tạo đơn hàng** nhanh chóng
- **Thanh toán** đa phương thức (Tiền mặt, Thẻ, MoMo, VNPay...)
- **In hóa đơn** và xuất báo cáo
- **Khuyến mãi** và giảm giá

### 👤 Quản lý Khách hàng
- **Thông tin khách hàng** chi tiết
- **Điểm tích lũy** và chương trình loyalty
- **Lịch sử mua hàng**

### 📦 Quản lý Kho & Nguyên liệu
- **Theo dõi tồn kho** nguyên liệu
- **Nhập kho** và cảnh báo khi hết hàng
- **Công thức** và định mức nguyên liệu

### 📊 Báo cáo & Thống kê
- **Doanh thu** theo ngày/tháng/năm
- **Sản phẩm bán chạy**
- **Báo cáo tồn kho**
- **Thống kê nhân viên**

## 🛠️ Công nghệ sử dụng

### Backend
- **Java 21** - Ngôn ngữ lập trình chính
- **JavaFX 24** - Framework giao diện desktop
- **MySQL 8.0** - Hệ quản trị cơ sở dữ liệu
- **Maven** - Quản lý dependencies và build

### Frontend
- **FXML** - Định nghĩa giao diện JavaFX
- **CSS** - Styling và themes
- **Scene Builder** - Thiết kế giao diện

### Architecture
- **MVC Pattern** - Model-View-Controller
- **DAO Pattern** - Data Access Object
- **Service Layer** - Business logic layer
- **Layered Architecture** - Tách biệt các layer

### Utilities & Libraries
- **BCrypt** - Mã hóa mật khẩu
- **Apache POI** - Excel export
- **iText 7** - PDF generation
- **QR Code Generator** - Tạo mã QR thanh toán

## 📋 Yêu cầu hệ thống

### Minimum Requirements
- **Java 21** hoặc cao hơn
- **MySQL 8.0** hoặc cao hơn
- **RAM:** 4GB
- **Storage:** 2GB free space
- **OS:** Windows 10+, macOS 10.14+, Linux

### Recommended Requirements
- **Java 21** hoặc cao hơn
- **MySQL 8.0** hoặc cao hơn
- **RAM:** 8GB
- **Storage:** 5GB free space
- **OS:** Windows 11, macOS 12+, Ubuntu 20.04+

## 🚀 Cài đặt và Chạy

### 1. Clone Repository
```bash
git clone https://github.com/your-username/cafe-management.git
cd cafe-management
```

### 2. Cài đặt Database
```bash
# Import database schema và sample data
mysql -u root -p < database/cafe_database_structure.sql
mysql -u root -p < database/cafe_sample_data.sql
mysql -u root -p < database/update_product_images.sql
# Hoặc sử dụng script tự động
cd database
setup_database.bat  # Windows
./setup_database.sh # Linux/Mac
```

### 3. Cấu hình Database
```bash
# Chỉnh sửa file database/database_config.properties
# Cập nhật thông tin kết nối database
```

### 4. Build Project
```bash
# Sử dụng Maven
mvn clean install

# Hoặc sử dụng IDE (IntelliJ IDEA, Eclipse, VS Code)
```

### 5. Chạy ứng dụng
```bash
# Phương pháp 1: Sử dụng script (Khuyến nghị)
run-app.bat

# Phương pháp 2: Sử dụng Maven
mvn javafx:run

# Phương pháp 3: Build và chạy JAR
build-and-run.bat
```

## 📁 Cấu trúc Project

```
Project2/
├── src/main/java/com/cafe/          # Source code chính
│   ├── config/                      # Cấu hình database
│   ├── model/                       # Models & Entities
│   │   ├── entity/                  # Database entities
│   │   ├── dto/                     # Data Transfer Objects
│   │   └── enums/                   # Enumerations
│   ├── dao/                         # Data Access Objects
│   │   └── base/                    # Base DAO implementations
│   ├── service/                     # Business Logic Layer
│   ├── controller/                  # Controllers (MVC)
│   │   ├── admin/                   # Admin controllers
│   │   ├── auth/                    # Authentication
│   │   ├── dashboard/               # Dashboard
│   │   ├── order/                   # Order management
│   │   ├── payment/                 # Payment processing
│   │   └── table/                   # Table management
│   ├── util/                        # Utilities
│   └── exception/                   # Exception handling
├── src/main/resources/              # Resources
│   ├── fxml/                        # FXML files
│   ├── css/                         # Stylesheets
│   ├── images/                      # Images & Icons
│   └── database_config.properties   # Database config
├── database/                        # Database files
│   ├── cafe_database_structure.sql  # Database schema
│   ├── cafe_sample_data.sql         # Sample data
│   └── setup_database.bat          # Setup script
├── docs/                           # Documentation
└── scripts/                        # Build & run scripts
```

## 🔧 Cấu hình

### Database Configuration
```properties
# database/database_config.properties
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
database.username=root
database.password=your_password
database.driver=com.mysql.cj.jdbc.Driver
```

### Application Configuration
```properties
# src/main/resources/database_config.properties
app.name=Cafe Management System
app.version=2.0.0
app.language=vi
app.currency=VND
```

## 👥 Tài khoản mẫu

| Username | Password | Role | Tên |
|----------|----------|------|-----|
| admin | 123456 | Admin | Nguyễn Tiến Ngọc Linh |
| manager | 123456 | Staff | Trần Xuân Quang Minh |
| staff01 | 123456 | Staff | Vũ Hoàng Nam |
| staff02 | 123456 | Staff | Dương Tuấn Minh |

## 📊 Screenshots

### Dashboard
![Dashboard](docs/assets/screenshots/dashboard.png)

### Order Management
![Order Management](docs/assets/screenshots/order-management.png)

### Product Management
![Product Management](docs/assets/screenshots/product-management.png)

### Reports
![Reports](docs/assets/screenshots/reports.png)

## 🧪 Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests
```bash
mvn verify
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📚 Documentation

- [Running Guide](RUNNING_GUIDE.md) - Hướng dẫn chạy ứng dụng
- [Project Structure](project_structure.md) - Cấu trúc chi tiết project
- [Database Setup](database/SETUP_GUIDE.md) - Hướng dẫn setup database
- [Database ERD](database/ERD.md) - Sơ đồ mối quan hệ database
- [Changelog](CHANGELOG.md) - Lịch sử thay đổi

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Changelog

Xem [CHANGELOG.md](CHANGELOG.md) để biết chi tiết về các thay đổi.

## 📄 License

Project này được phân phối dưới giấy phép MIT. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.



## 🙏 Acknowledgments

- JavaFX Community
- MySQL Documentation
- Maven Community
- Open Source Contributors

---

**Lưu ý:** Đây là project demo cho mục đích học tập. Vui lòng không sử dụng trong môi trường production mà không có kiểm tra bảo mật đầy đủ. 