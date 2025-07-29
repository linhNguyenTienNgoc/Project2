# Coffee Shop Management System

Hệ thống quản lý quán cà phê được xây dựng bằng Java Spring Boot với giao diện web hiện đại.

## 🚀 Tính năng chính

### 👥 Quản lý người dùng
- **Admin**: Quản lý toàn bộ hệ thống (nhân viên, menu, bàn, hóa đơn, báo cáo)
- **Staff**: Chỉ xem và tạo hóa đơn, không thể sửa đổi menu/bàn

### 🍽️ Quản lý Menu
- Thêm, sửa, xóa món ăn/đồ uống
- Phân loại theo danh mục
- Tìm kiếm và lọc món ăn

### 🪑 Quản lý Bàn
- Hiển thị trạng thái bàn (Trống/Đang phục vụ/Đã đặt)
- Thêm, sửa, xóa bàn (chỉ Admin)
- Thống kê số lượng bàn theo trạng thái

### 🧾 Quản lý Hóa đơn
- Tạo hóa đơn mới với giỏ hàng trực quan
- Chọn món ăn và số lượng
- Thanh toán, hủy hóa đơn
- Xem chi tiết hóa đơn với khả năng in

### 📊 Báo cáo & Thống kê
- Dashboard với biểu đồ doanh thu
- Thống kê hóa đơn theo thời gian
- Top món ăn bán chạy
- Xuất báo cáo

## 🛠️ Công nghệ sử dụng

- **Backend**: Java Spring Boot 2.7.x
- **Database**: SQL Server
- **Frontend**: Thymeleaf, Bootstrap 5, Font Awesome
- **Charts**: Chart.js
- **Build Tool**: Maven

## 📋 Yêu cầu hệ thống

- Java 8 trở lên
- SQL Server 2012 trở lên
- Maven 3.6+

## 🚀 Hướng dẫn cài đặt

### 1. Chuẩn bị Database

Chạy script SQL để tạo database và bảng:

```sql
-- Tạo database
CREATE DATABASE CoffeeShopManagement;
GO

USE CoffeeShopManagement;
GO

-- Tạo bảng Employee
CREATE TABLE Employee (
    employeeID INT IDENTITY(1,1) PRIMARY KEY,
    employeeName NVARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('admin', 'staff'))
);

-- Tạo bảng CoffeeTable
CREATE TABLE CoffeeTable (
    tableID INT IDENTITY(1,1) PRIMARY KEY,
    tableName NVARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Available' CHECK (status IN ('Available', 'Occupied', 'Reserved'))
);

-- Tạo bảng Menu
CREATE TABLE Menu (
    menuID INT IDENTITY(1,1) PRIMARY KEY,
    menuName NVARCHAR(100) NOT NULL,
    description NVARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    category NVARCHAR(50) NOT NULL
);

-- Tạo bảng Invoice
CREATE TABLE Invoice (
    invoiceID INT IDENTITY(1,1) PRIMARY KEY,
    tableID INT NOT NULL,
    employeeID INT NOT NULL,
    totalAmount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Pending' CHECK (status IN ('Pending', 'Paid', 'Cancelled')),
    createdAt DATETIME2 DEFAULT GETDATE(),
    updatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (tableID) REFERENCES CoffeeTable(tableID),
    FOREIGN KEY (employeeID) REFERENCES Employee(employeeID)
);

-- Tạo bảng InvoiceDetail
CREATE TABLE InvoiceDetail (
    detailID INT IDENTITY(1,1) PRIMARY KEY,
    invoiceID INT NOT NULL,
    menuID INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (invoiceID) REFERENCES Invoice(invoiceID),
    FOREIGN KEY (menuID) REFERENCES Menu(menuID)
);

-- Thêm dữ liệu mẫu
INSERT INTO Employee (employeeName, username, password, role) VALUES
('Admin', 'admin', 'admin123', 'admin'),
('Nhân viên 1', 'staff1', 'staff123', 'staff'),
('Nhân viên 2', 'staff2', 'staff123', 'staff');

INSERT INTO CoffeeTable (tableName, status) VALUES
('Bàn 1', 'Available'),
('Bàn 2', 'Available'),
('Bàn 3', 'Available'),
('Bàn VIP 1', 'Available'),
('Bàn VIP 2', 'Available');

INSERT INTO Menu (menuName, description, price, category) VALUES
('Cà phê đen', 'Cà phê đen truyền thống', 25000, 'Cà phê'),
('Cà phê sữa', 'Cà phê sữa đặc', 30000, 'Cà phê'),
('Bạc xỉu', 'Cà phê sữa đặc với ít cà phê', 35000, 'Cà phê'),
('Trà sữa trân châu', 'Trà sữa với trân châu', 45000, 'Trà sữa'),
('Bánh tiramisu', 'Bánh tiramisu truyền thống', 55000, 'Bánh'),
('Bánh cheesecake', 'Bánh cheesecake New York', 65000, 'Bánh');
```

### 2. Cấu hình ứng dụng

Chỉnh sửa file `src/main/resources/application.properties`:

```properties
# Thay đổi thông tin kết nối database theo máy của bạn
spring.datasource.url=jdbc:sqlserver://localhost\\SQLEXPRESS;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 3. Chạy ứng dụng

```bash
# Clone project
git clone <repository-url>
cd CoffeeShopManagement

# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Hoặc chạy file `CoffeeShopManagementApplication.java` trong IDE.

### 4. Truy cập ứng dụng

Mở trình duyệt và truy cập: `http://localhost:8080`

## 👤 Tài khoản mặc định

- **Admin**: 
  - Username: `admin`
  - Password: `admin123`
- **Staff**: 
  - Username: `staff1` hoặc `staff2`
  - Password: `staff123`

## 📱 Giao diện

### Đăng nhập
- Giao diện đăng nhập đẹp mắt với gradient
- Validation form
- Thông báo lỗi rõ ràng

### Dashboard
- Thống kê tổng quan
- Biểu đồ doanh thu
- Quick actions

### Quản lý Menu
- Grid view với hình ảnh
- Tìm kiếm và lọc
- Form thêm/sửa món ăn

### Quản lý Bàn
- Hiển thị trạng thái màu sắc
- Thống kê số lượng bàn
- Quản lý trạng thái

### Tạo Hóa đơn
- Giao diện giỏ hàng trực quan
- Chọn món ăn dễ dàng
- Tính toán tự động

### Báo cáo
- Biểu đồ Chart.js
- Thống kê chi tiết
- Xuất báo cáo

## 🔧 Cấu trúc project

```
src/
├── main/
│   ├── java/
│   │   └── com/coffeeshop/coffeeshopmanagement/
│   │       ├── controller/     # Controllers
│   │       ├── entity/         # JPA Entities
│   │       ├── repository/     # Data Repositories
│   │       ├── service/        # Business Logic
│   │       └── CoffeeShopManagementApplication.java
│   └── resources/
│       ├── templates/          # Thymeleaf templates
│       ├── static/             # CSS, JS, Images
│       └── application.properties
└── test/                       # Unit tests
```

## 🚀 API Endpoints

### REST API (cho mobile app)
- `GET /api/employees` - Lấy danh sách nhân viên
- `GET /api/menu` - Lấy danh sách menu
- `GET /api/tables` - Lấy danh sách bàn
- `GET /api/invoices` - Lấy danh sách hóa đơn
- `POST /api/invoices` - Tạo hóa đơn mới

### Web Controllers
- `GET /login` - Trang đăng nhập
- `GET /dashboard` - Dashboard chính
- `GET /menu` - Quản lý menu
- `GET /tables` - Quản lý bàn
- `GET /invoices` - Quản lý hóa đơn
- `GET /employees` - Quản lý nhân viên (Admin)
- `GET /reports` - Báo cáo (Admin)

## 🔒 Bảo mật

- Session-based authentication
- Role-based access control
- Password validation
- SQL injection prevention
- XSS protection

## 📊 Tính năng nâng cao

- [ ] Spring Security integration
- [ ] JWT authentication
- [ ] Email notifications
- [ ] Mobile app API
- [ ] Real-time notifications
- [ ] Inventory management
- [ ] Customer management
- [ ] Loyalty program

## 🤝 Đóng góp

1. Fork project
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.

## 📞 Liên hệ

- Email: your.email@example.com
- Project Link: [https://github.com/yourusername/coffee-shop-management](https://github.com/yourusername/coffee-shop-management)

## 🙏 Cảm ơn

Cảm ơn bạn đã sử dụng Coffee Shop Management System! 