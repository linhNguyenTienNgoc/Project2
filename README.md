# ☕ Cafe Management System

Hệ thống quản lý quán cà phê được phát triển bằng JavaFX với kiến trúc MVC chuyên nghiệp và database tối ưu.

## 🚀 Tính năng chính

### 👥 Quản lý Người dùng & Phân quyền
- **Đăng nhập/Đăng xuất** với xác thực bảo mật và mã hóa password
- **Phân quyền** theo vai trò (Admin, Waiter, Barista) với JSON permissions
- **Quản lý nhân viên** với chấm công tự động và theo dõi giờ làm
- **Audit trail** - Log mọi thay đổi trong hệ thống

### 🍽️ Quản lý Menu & Sản phẩm
- **Danh mục sản phẩm** (Cà phê, Trà, Bánh, Món ăn...) với sắp xếp thứ tự
- **Quản lý sản phẩm** với SKU, hình ảnh, giá cả, mô tả
- **Quản lý tồn kho** với cảnh báo khi hết hàng
- **Fulltext search** tìm kiếm sản phẩm nhanh chóng

### 🪑 Quản lý Khu vực & Bàn
- **Khu vực** (Tầng 1, Tầng 2, VIP, Sân thượng) với sức chứa
- **Trạng thái bàn** (Trống, Có khách, Đặt trước, Dọn dẹp, Bảo trì)
- **Đặt bàn** và quản lý lịch với unique constraints
- **Auto-update** trạng thái bàn khi tạo đơn hàng

### 📋 Quản lý Đơn hàng
- **Tạo đơn hàng** nhanh chóng với stored procedures
- **Thanh toán** đa phương thức (Tiền mặt, Thẻ, MoMo, VNPay, Chuyển khoản)
- **Tính thuế tự động** với configurable tax rate
- **Khuyến mãi nâng cao** với usage limits và max discount
- **In hóa đơn** và xuất báo cáo

### 👤 Quản lý Khách hàng
- **Thông tin khách hàng** chi tiết với mã khách hàng tự động
- **Điểm tích lũy** và chương trình loyalty tự động
- **Lịch sử mua hàng** và thống kê chi tiêu
- **Fulltext search** tìm kiếm khách hàng

### 📦 Quản lý Kho & Nguyên liệu
- **Theo dõi tồn kho** với min stock level
- **Auto-update** stock khi tạo đơn hàng
- **Cảnh báo** khi hết hàng hoặc sắp hết
- **Báo cáo tồn kho** real-time

### 📊 Báo cáo & Thống kê
- **Doanh thu** theo ngày/tháng/năm với views tối ưu
- **Sản phẩm bán chạy** và phân tích xu hướng
- **Báo cáo tồn kho** với stock status
- **Thống kê nhân viên** và chấm công
- **Dashboard** real-time với performance metrics

## 🛠️ Công nghệ sử dụng

### Backend
- **Java 17** - Ngôn ngữ lập trình chính
- **JavaFX 17** - Framework giao diện
- **MySQL 8.0** - Hệ quản trị cơ sở dữ liệu tối ưu
- **HikariCP** - Connection pooling hiệu suất cao
- **BCrypt** - Mã hóa mật khẩu bảo mật

### Database Optimization
- **UTF8MB4** - Hỗ trợ đầy đủ tiếng Việt và emoji
- **45+ Indexes** - Tối ưu hiệu suất truy vấn
- **JSON Fields** - Lưu trữ dữ liệu linh hoạt
- **Triggers & Procedures** - Tự động hóa business logic
- **Views** - Báo cáo nhanh và tối ưu

### Frontend
- **FXML** - Định nghĩa giao diện
- **CSS** - Styling và themes responsive
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
# Import database schema (tối ưu)
mysql -u root -p < database/cafe_management.sql

# Import sample data
mysql -u root -p cafe_management < database/data.sql

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

# Hoặc sử dụng script có sẵn
./run-app.bat  # Windows
./run-app.sh   # Linux/Mac
```

## 📁 Cấu trúc Project

```
Project2/
├── src/main/java/com/cafe/          # Source code chính
│   ├── config/                      # Cấu hình database và app
│   ├── model/                       # Models & Entities
│   │   ├── entity/                  # Database entities
│   │   └── enums/                   # Enums và constants
│   ├── dao/                         # Data Access Objects
│   │   └── base/                    # Base DAO implementations
│   ├── service/                     # Business Logic
│   ├── controller/                  # Controllers
│   │   ├── admin/                   # Admin controllers
│   │   └── auth/                    # Authentication controllers
│   ├── util/                        # Utilities và helpers
│   └── exception/                   # Exception handling
├── src/main/resources/              # Resources
│   ├── fxml/                        # FXML files
│   ├── css/                         # Stylesheets
│   ├── images/                      # Images & Icons
│   └── properties/                  # Properties files
├── src/test/                        # Test files
├── database/                        # Database files
│   ├── cafe_management.sql          # Schema tối ưu
│   ├── data.sql                     # Sample data
│   └── *.md                         # Database documentation
├── docs/                           # Documentation
├── scripts/                        # Build scripts
└── lib/                           # JavaFX SDK
```

## 🔧 Cấu hình

### Database Configuration
```properties
# config/database.properties
database.url=jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC
database.username=root
database.password=your_password
database.driver=com.mysql.cj.jdbc.Driver
database.pool.size=10
database.pool.maxLifetime=1800000
```

### Application Configuration
```properties
# config/application.properties
app.name=Cafe Management System
app.version=2.0.0
app.language=vi
app.currency=VND
app.tax.rate=10
app.loyalty.points.rate=1
```

## 👥 Tài khoản mẫu

| Username | Password | Role | Tên | Mô tả |
|----------|----------|------|-----|-------|
| admin | 123456 | Admin | Nguyễn Tiến Ngọc Linh | Quản trị viên hệ thống |
| waiter1 | 123456 | Waiter | Dương Tuấn Minh | Nhân viên phục vụ |
| waiter2 | 123456 | Waiter | Trần Thị Hương | Nhân viên phục vụ |
| barista1 | 123456 | Barista | Dương Đức Thành | Nhân viên pha chế |
| barista2 | 123456 | Barista | Phạm Thị Mai | Nhân viên pha chế |

## 📊 Database Features

### Optimized Schema
- **10 Tables** với quan hệ rõ ràng
- **45+ Indexes** cho hiệu suất tối đa
- **3 Views** cho báo cáo nhanh
- **3 Triggers** tự động hóa
- **2 Stored Procedures** xử lý logic phức tạp
- **2 Functions** tiện ích

### Advanced Features
- **JSON Permissions** - Quản lý quyền linh hoạt
- **Audit Logging** - Theo dõi mọi thay đổi
- **Auto-increment Optimization** - Hiệu suất cao
- **Constraint Validation** - Đảm bảo tính toàn vẹn
- **Fulltext Search** - Tìm kiếm nhanh chóng

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

### Database Tests
```bash
# Test database connection
mysql -u root -p < database/test_connection.sql
```

### Test Coverage
```bash
mvn jacoco:report
```

## 📚 Documentation

- [User Guide](docs/user-guide/user-manual.md)
- [Developer Guide](docs/developer/architecture.md)
- [API Documentation](docs/api/controllers.md)
- [Database Design](database/ERD.md)
- [Database Setup](database/SETUP_GUIDE.md)
- [Running Guide](RUNNING_GUIDE.md)

## 🤝 Contributing

1. Fork the project
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📝 Changelog

### Version 2.0.1 (Latest)
- ✅ **Database Optimization**: Tối ưu schema với 45+ indexes
- ✅ **Performance Improvements**: Triggers, procedures, views
- ✅ **Security Enhancements**: JSON permissions, audit logging
- ✅ **New Features**: Inventory management, loyalty system
- ✅ **Code Quality**: Better constraints, validation
- ✅ **Bug Fixes**: Fixed MySQL CHECK constraint error with CURDATE() function

Xem [CHANGELOG.md](CHANGELOG.md) để biết chi tiết về các thay đổi.

## 📄 License

Project này được phân phối dưới giấy phép MIT. Xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## 👨‍💻 Team

- **Nguyễn Tiến Ngọc Linh** - Project Manager & Lead Developer
- **Trần Xuân Quang Minh** - Backend Developer
- **Vũ Hoàng Nam** - Database Architect
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