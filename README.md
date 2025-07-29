# ☕ Coffee Shop Management System

## 🌟 Tổng quan

Hệ thống quản lý quán cà phê hiện đại với giao diện đẹp mắt, được thiết kế theo xu hướng **Glass Morphism** và **Material Design**. Ứng dụng hỗ trợ đầy đủ các chức năng quản lý từ đặt hàng, quản lý bàn, menu đến báo cáo doanh thu.

## ✨ Tính năng nổi bật

### 🎨 **Giao diện hiện đại**
- **Glass Morphism Design**: Hiệu ứng kính mờ với backdrop blur
- **Gradient Backgrounds**: Nền gradient đẹp mắt
- **Smooth Animations**: Hiệu ứng chuyển động mượt mà
- **Responsive Layout**: Giao diện thích ứng với mọi kích thước màn hình

### 👥 **Phân quyền người dùng**
- **Admin**: Quản lý toàn bộ hệ thống, tạo tài khoản nhân viên
- **Staff**: Đặt hàng, quản lý bàn, xử lý thanh toán

### 🍽️ **Quản lý đặt hàng**
- **Quick Order**: Đặt hàng nhanh với các món phổ biến
- **Real-time Menu**: Menu cập nhật theo thời gian thực
- **Smart Table Management**: Quản lý trạng thái bàn thông minh
- **Payment Processing**: Xử lý thanh toán đa phương thức

### 📊 **Báo cáo & Analytics**
- **Dashboard**: Tổng quan doanh thu, đơn hàng
- **Invoice Management**: Quản lý hóa đơn chi tiết
- **Export Features**: Xuất báo cáo ra file

## 🚀 Cài đặt & Chạy ứng dụng

### Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+
- SQL Server (tùy chọn)

### Cài đặt nhanh
```bash
# Clone repository
git clone <repository-url>
cd ShopCoffeeManagement

# Build project
mvn clean compile

# Chạy ứng dụng
mvn javafx:run
```

### Chạy từ IntelliJ IDEA
1. Mở project trong IntelliJ IDEA
2. Chạy class `CoffeeShopApplication`
3. Hoặc sử dụng Maven plugin: `javafx:run`

## 🎯 Tài khoản demo

### Admin Account
- **Username**: admin
- **Password**: admin123
- **Quyền**: Quản lý toàn bộ hệ thống

### Staff Account
- **Username**: staff
- **Password**: staff123
- **Quyền**: Đặt hàng, quản lý bàn

## 📱 Hướng dẫn sử dụng

### 1. **Đăng nhập**
- Chọn tài khoản phù hợp với vai trò
- Hệ thống sẽ tự động chuyển hướng đến giao diện tương ứng

### 2. **Quản lý bàn**
- Xem trạng thái tất cả bàn (Available/Occupied/Reserved)
- Click vào bàn để thực hiện thao tác
- Tìm kiếm và lọc bàn theo trạng thái

### 3. **Đặt hàng**
- Chọn bàn từ danh sách
- Sử dụng "Đặt hàng nhanh" cho các món phổ biến
- Thêm món từ menu chi tiết
- Xử lý thanh toán và xuất hóa đơn

### 4. **Quản lý menu**
- Thêm/sửa/xóa món ăn
- Phân loại theo danh mục
- Cập nhật giá và mô tả

### 5. **Báo cáo**
- Xem tổng quan doanh thu
- Quản lý hóa đơn
- Xuất báo cáo chi tiết

## 🛠️ Cấu trúc dự án

```
ShopCoffeeManagement/
├── src/main/java/com/coffeeshop/shopcoffeemanagement/
│   ├── CoffeeShopApplication.java          # Main application
│   ├── controller/                         # Controllers
│   │   ├── LoginController.java
│   │   ├── MainController.java
│   │   ├── TablesController.java
│   │   ├── OrderController.java
│   │   ├── MenuController.java
│   │   ├── AdminController.java
│   │   └── InvoiceController.java
│   ├── model/                             # Data models
│   │   ├── Employee.java
│   │   ├── CoffeeTable.java
│   │   ├── Menu.java
│   │   ├── Invoice.java
│   │   └── InvoiceDetail.java
│   ├── dao/                               # Data access objects
│   │   ├── EmployeeDAO.java
│   │   ├── CoffeeTableDAO.java
│   │   └── MenuDAO.java
│   ├── service/                           # Business logic
│   │   └── EmployeeService.java
│   └── util/                              # Utilities
│       └── DatabaseConnection.java
├── src/main/resources/
│   ├── fxml/                              # UI layouts
│   │   ├── login.fxml
│   │   ├── main.fxml
│   │   ├── tables.fxml
│   │   ├── order.fxml
│   │   ├── menu.fxml
│   │   ├── admin.fxml
│   │   └── invoices.fxml
│   ├── css/                               # Stylesheets
│   │   └── main.css
│   └── config/                            # Configuration
│       └── database.properties
└── pom.xml                                # Maven configuration
```

## 🎨 Thiết kế giao diện

### **Glass Morphism Elements**
- **Transparent backgrounds** với backdrop blur
- **Subtle borders** với độ trong suốt
- **Layered shadows** tạo chiều sâu
- **Smooth transitions** cho tương tác

### **Color Palette**
- **Primary**: `#667eea` → `#764ba2` (Gradient)
- **Success**: `#4ecdc4` → `#44a08d` (Gradient)
- **Danger**: `#ff6b6b` → `#ee5a52` (Gradient)
- **Neutral**: `rgba(255, 255, 255, 0.9)` (Glass)

### **Typography**
- **Font Family**: Segoe UI, Roboto, Helvetica Neue
- **Font Weights**: 400 (Regular), 600 (Semi-bold), 700 (Bold)
- **Font Sizes**: 12px - 32px (Responsive)

## 🔧 Cấu hình database

### SQL Server Setup
1. Cài đặt SQL Server
2. Tạo database `CoffeeShopManagement`
3. Chạy script `database_setup.sql`
4. Cập nhật `config/database.properties`

### Demo Mode
- Ứng dụng có thể chạy với dữ liệu demo
- Không cần cấu hình database
- Tất cả chức năng hoạt động bình thường

## 🚀 Tính năng nâng cao

### **Smart Features**
- **Auto-save**: Tự động lưu dữ liệu
- **Search & Filter**: Tìm kiếm thông minh
- **Keyboard Shortcuts**: Phím tắt tiện lợi
- **Drag & Drop**: Kéo thả cho đặt hàng

### **Performance**
- **Lazy Loading**: Tải dữ liệu theo nhu cầu
- **Caching**: Cache dữ liệu thường dùng
- **Optimized Queries**: Truy vấn tối ưu

## 📈 Roadmap

### **Version 2.0**
- [ ] Mobile app companion
- [ ] Real-time notifications
- [ ] Advanced analytics
- [ ] Multi-language support

### **Version 2.1**
- [ ] Inventory management
- [ ] Employee scheduling
- [ ] Customer loyalty program
- [ ] Online ordering integration

## 🤝 Đóng góp

1. Fork project
2. Tạo feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## 📄 License

Dự án này được phát hành dưới MIT License - xem file [LICENSE](LICENSE) để biết thêm chi tiết.

## 📞 Liên hệ

- **Developer**: Nguyễn Văn Admin
- **Email**: admin@coffeeshop.com
- **Project**: [Coffee Shop Management System](https://github.com/your-repo)

---

⭐ **Nếu dự án này hữu ích, hãy cho chúng tôi một star!** 