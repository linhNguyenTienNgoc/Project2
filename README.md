# ☕ Cafe Management System

Hệ thống quản lý quán cà phê được phát triển bằng JavaFX với kiến trúc MVC chuyên nghiệp.

## 🚀 Tính năng chính

### 👥 Quản lý Người dùng & Phân quyền
- **Đăng nhập/Đăng xuất** với xác thực bảo mật
- **Phân quyền** theo vai trò (Admin, Manager, Cashier, Waiter, Barista)
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
- **Java 17** - Ngôn ngữ lập trình chính
- **JavaFX 17** - Framework giao diện
- **MySQL 8.0** - Hệ quản trị cơ sở dữ liệu
- **HikariCP** - Connection pooling
- **BCrypt** - Mã hóa mật khẩu

### Frontend
- **FXML** - Định nghĩa giao diện
- **CSS** - Styling và themes
- **Scene Builder** - Thiết kế giao diện

### Build & Deploy
- **Maven** - Quản lý dependencies và build
- **JUnit 5** - Unit testing
- **Mockito** - Mock testing

### Utilities
- **Logback** - Logging framework
- **Jackson** - JSON processing
- **Apache POI** - Excel export
- **iText 7** - PDF generation
- **JavaMail** - Email notifications

## 📋 Yêu cầu hệ thống

### Minimum Requirements
- **Java 17** hoặc cao hơn
- **MySQL 8.0** hoặc cao hơn
- **RAM:** 4GB
- **Storage:** 2GB free space
- **OS:** Windows 10+, macOS 10.14+, Linux

### Recommended Requirements
- **Java 17** hoặc cao hơn
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
# Import database schema
mysql -u root -p < database/cafe_management.sql

# Hoặc sử dụng phpMyAdmin để import file SQL
```

### 3. Cấu hình Database
```bash
# Chỉnh sửa file config/database.properties
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
# Sử dụng Maven
mvn javafx:run

# Hoặc chạy JAR file
java -jar target/cafe-management-1.0.0.jar
```

## 📁 Cấu trúc Project

```
Project2/
├── src/main/java/com/cafe/          # Source code chính
│   ├── config/                      # Cấu hình
│   ├── model/                       # Models & Entities
│   ├── dao/                         # Data Access Objects
│   ├── service/                     # Business Logic
│   ├── controller/                  # Controllers
│   ├── util/                        # Utilities
│   └── exception/                   # Exception handling
├── src/main/resources/              # Resources
│   ├── fxml/                        # FXML files
│   ├── css/                         # Stylesheets
│   ├── images/                      # Images & Icons
│   └── properties/                  # Properties files
├── src/test/                        # Test files
├── database/                        # Database files
├── docs/                           # Documentation
└── scripts/                        # Build scripts
```

## 🔧 Cấu hình

### Database Configuration
```properties
# config/database.properties
database.url=jdbc:mysql://localhost:3306/cafe_management
database.username=root
database.password=your_password
database.driver=com.mysql.cj.jdbc.Driver
```

### Application Configuration
```properties
# config/application.properties
app.name=Cafe Management System
app.version=1.0.0
app.language=vi
app.currency=VND
```

## 👥 Tài khoản mẫu

| Username | Password | Role | Tên |
|----------|----------|------|-----|
| admin | password | Admin | Nguyễn Tiến Ngọc Linh |
| manager | password | Manager | Trần Xuân Quang Minh |
| cashier1 | password | Cashier | Vũ Hoàng Nam |
| waiter1 | password | Waiter | Dương Tuấn Minh |
| barista1 | password | Barista | Nguyễn Thị Nguyệt Nhi |

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

- [User Guide](docs/user-guide/user-manual.md)
- [Developer Guide](docs/developer/architecture.md)
- [API Documentation](docs/api/controllers.md)
- [Database Design](docs/developer/database-design.md)

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

## 👨‍💻 Team

- **Nguyễn Tiến Ngọc Linh** - Project Manager
- **Trần Xuân Quang Minh** - Lead Developer
- **Vũ Hoàng Nam** - Backend Developer
- **Dương Tuấn Minh** - Frontend Developer
- **Nguyễn Thị Nguyệt Nhi** - UI/UX Designer

## 📞 Liên hệ

- **Email:** cafe.management@example.com
- **Website:** https://cafe-management.example.com
- **GitHub:** https://github.com/your-username/cafe-management

## 🙏 Acknowledgments

- JavaFX Community
- MySQL Documentation
- Maven Community
- Open Source Contributors

---

**Lưu ý:** Đây là project demo cho mục đích học tập. Vui lòng không sử dụng trong môi trường production mà không có kiểm tra bảo mật đầy đủ. 