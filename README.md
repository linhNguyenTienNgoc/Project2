# ☕ Hệ thống Quản lý Quán Cà phê

Ứng dụng JavaFX hiện đại để quản lý quán cà phê với giao diện đẹp mắt và tính năng đầy đủ.

## 🚀 Tính năng chính

### 👥 Quản lý người dùng
- **Đăng nhập/Đăng xuất** với phân quyền Admin và Staff
- **Quản lý nhân viên** với thông tin chi tiết
- **Phân quyền** theo vai trò

### 🏠 Quản lý bàn
- **Hiển thị bàn** dạng hình chữ nhật (không phải hình tròn)
- **Trạng thái bàn**: Có sẵn, Đang sử dụng, Đã đặt
- **Thêm/Sửa/Xóa bàn** với thông tin chi tiết
- **Tìm kiếm và lọc** bàn theo trạng thái

### 🍽️ Quản lý Menu
- **Danh mục đa dạng**: Cà phê, Trà, Nước ép, Tráng miệng, Đồ ăn, Sinh tố
- **Thông tin chi tiết**: Tên, mô tả, giá, hình ảnh
- **Tìm kiếm và lọc** theo danh mục
- **Thêm/Sửa/Xóa** món ăn

### 📋 Đặt hàng
- **Chọn bàn** từ danh sách có sẵn
- **Thêm món** vào đơn hàng với số lượng
- **Tính tổng tiền** tự động
- **Xác nhận đơn hàng** trước khi tạo

### 📊 Dashboard
- **Thống kê tổng quan**: Số bàn, trạng thái, menu
- **Biểu đồ tròn** hiển thị phân bố
- **Hoạt động gần đây** với timeline
- **Thời gian thực** cập nhật

### 💾 Kết nối Database
- **SQL Server** với JDBC
- **Dữ liệu thực** lưu trữ và truy xuất
- **Fallback data** khi không có database
- **Script SQL** tự động tạo database

## 🎨 Giao diện

### ✨ Thiết kế hiện đại
- **Gradient colors** với màu sắc đẹp mắt
- **Animation effects** mượt mà
- **Responsive layout** thích ứng
- **Icons và emoji** trực quan

### 🎯 UX/UI tốt
- **Navigation** rõ ràng với sidebar
- **Search và filter** nhanh chóng
- **Confirmation dialogs** an toàn
- **Error handling** thân thiện

## 🛠️ Công nghệ sử dụng

- **JavaFX 17** - Giao diện người dùng
- **Maven** - Quản lý dependencies
- **SQL Server** - Database
- **JDBC** - Kết nối database
- **CSS** - Styling hiện đại

## 📦 Cài đặt và chạy

### Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+
- SQL Server (tùy chọn)

### Cách 1: Chạy Demo (Không cần database)
```bash
# Clone repository
git clone <repository-url>
cd ShopCoffeeManagement

# Chạy ứng dụng demo
mvn clean javafx:run -DmainClass=com.coffeeshop.shopcoffeemanagement.DemoApplication
```

### Cách 2: Chạy với Database
1. **Cài đặt SQL Server** (xem hướng dẫn trong `DATABASE_SETUP_GUIDE.md`)
2. **Chạy script database**:
   ```bash
   # Mở SQL Server Management Studio
   # Chạy file database_setup.sql
   ```
3. **Cấu hình kết nối**:
   - Chỉnh sửa `src/main/resources/config/database.properties`
   - Thay đổi password SA
4. **Chạy ứng dụng**:
   ```bash
   mvn clean javafx:run -DmainClass=com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication
   ```

### Chạy từ IntelliJ IDEA
1. Mở project trong IntelliJ IDEA
2. Chạy `CoffeeShopApplication.java` hoặc `DemoApplication.java`
3. Hoặc sử dụng Maven plugin

## 🔐 Tài khoản đăng nhập

### Demo Mode
- **Admin**: admin/admin123
- **Staff**: staff1/staff123
- **Manager**: manager/manager123

### Database Mode
- **Admin**: admin/admin123
- **Staff**: staff1/staff123
- **Manager**: manager/manager123

## 📁 Cấu trúc project

```
ShopCoffeeManagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/coffeeshop/shopcoffeemanagement/
│   │   │       ├── controller/     # Controllers
│   │   │       ├── model/          # Data models
│   │   │       ├── dao/            # Data access objects
│   │   │       ├── service/        # Business logic
│   │   │       └── util/           # Utilities
│   │   └── resources/
│   │       ├── fxml/               # FXML layouts
│   │       ├── css/                # Stylesheets
│   │       └── config/             # Configuration
├── database_setup.sql              # Database script
├── DATABASE_SETUP_GUIDE.md         # Database setup guide
└── README.md                       # This file
```

## 🎯 Tính năng nâng cao

### 📈 Analytics
- **Biểu đồ thống kê** trực quan
- **Báo cáo doanh thu** theo thời gian
- **Phân tích xu hướng** bán hàng

### 🔄 Real-time Updates
- **Cập nhật trạng thái** bàn real-time
- **Thông báo** khi có đơn hàng mới
- **Đồng bộ dữ liệu** tự động

### 📱 Responsive Design
- **Giao diện thích ứng** với mọi kích thước màn hình
- **Touch-friendly** cho tablet
- **Keyboard shortcuts** cho power users

## 🐛 Troubleshooting

### Lỗi thường gặp
1. **"Không thể tìm thấy file config/database.properties"**
   - Đây là thông báo bình thường, ứng dụng sẽ sử dụng demo data
   - Để kết nối database, xem hướng dẫn trong `DATABASE_SETUP_GUIDE.md`

2. **"Driver not found"**
   - Refresh Maven project
   - Clean và rebuild project

3. **"Connection refused"**
   - Kiểm tra SQL Server đang chạy
   - Kiểm tra port 1433
   - Kiểm tra firewall

### Hỗ trợ
- Xem file `DATABASE_SETUP_GUIDE.md` để cấu hình database
- Kiểm tra logs trong console để debug
- Đảm bảo Java 17+ và Maven được cài đặt đúng

## 🎉 Kết quả

Sau khi hoàn thành, bạn sẽ có:
- ✅ **Ứng dụng hoàn chỉnh** có thể demo ngay
- ✅ **Giao diện đẹp** với animation mượt mà
- ✅ **Tính năng đầy đủ** cho quản lý quán cà phê
- ✅ **Database thực** lưu trữ dữ liệu
- ✅ **Code sạch** dễ bảo trì và mở rộng

## 📄 License

Project này được tạo cho mục đích học tập và demo.

---

**Chúc bạn thành công với dự án quản lý quán cà phê! ☕🚀** 