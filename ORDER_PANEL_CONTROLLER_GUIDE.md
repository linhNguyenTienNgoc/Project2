# OrderPanelController - Hướng dẫn sử dụng

## 📋 Tổng quan

`OrderPanelController` là controller chính để quản lý đơn hàng trong hệ thống Cafe Management. Controller này xử lý việc tạo đơn hàng, thêm món, thanh toán và quản lý trạng thái bàn.

## 🔧 Tính năng chính

### 1. **Kết nối Database**
- Tự động kết nối với MySQL database thông qua `DatabaseConfig`
- Sử dụng `OrderService` và `MenuService` để tương tác với database
- Connection pooling với HikariCP

### 2. **Quản lý Menu**
- Lấy danh sách sản phẩm có sẵn từ database
- Tìm kiếm sản phẩm theo tên
- Lọc sản phẩm theo danh mục

### 3. **Quản lý Đơn hàng**
- Tạo đơn hàng mới khi khách vào bàn
- Thêm sản phẩm vào đơn hàng
- Cập nhật số lượng và tính tổng tiền
- Xóa sản phẩm khỏi đơn hàng

### 4. **Quản lý Bàn**
- Cập nhật thông tin bàn (tên, trạng thái)
- Tự động trích xuất ID bàn từ tên bàn
- Xóa đơn hàng khi chuyển bàn

### 5. **Thanh toán**
- Đặt món (chuyển trạng thái sang "preparing")
- Thanh toán (chuyển trạng thái sang "paid")
- Xóa đơn hàng sau khi thanh toán

## 🚀 Cách sử dụng

### 1. **Khởi tạo Controller**

```java
// Controller được khởi tạo tự động khi load FXML
// Hoặc có thể tạo thủ công:
OrderPanelController controller = new OrderPanelController();
```

### 2. **Cập nhật thông tin bàn**

```java
// Cập nhật bàn và trạng thái
controller.updateTableInfo("Bàn 1", TableStatus.AVAILABLE);
```

### 3. **Thêm món vào đơn hàng**

```java
// Thêm sản phẩm với tên, giá và số lượng
controller.addToOrder("Cà phê đen", 25000, 2);
```

### 4. **Lấy danh sách sản phẩm**

```java
// Lấy tất cả sản phẩm có sẵn
List<Product> products = controller.getAvailableProducts();

// Lấy sản phẩm theo danh mục
List<Product> coffeeProducts = controller.getProductsByCategory(1);
```

### 5. **Test kết nối database**

```java
// Test kết nối và dữ liệu menu
controller.testDatabaseConnection();
```

## 📊 Cấu trúc dữ liệu

### **OrderItem (Inner Class)**
```java
private static class OrderItem {
    String productName;    // Tên sản phẩm
    double price;          // Giá đơn vị
    int quantity;          // Số lượng
    double totalPrice;     // Tổng tiền = price * quantity
}
```

### **Trạng thái đơn hàng**
- `pending`: Đang chờ (có thể thêm/sửa món)
- `preparing`: Đang chuẩn bị (đã đặt món)
- `ready`: Sẵn sàng phục vụ
- `served`: Đã phục vụ
- `paid`: Đã thanh toán

## 🔗 Kết nối với các component khác

### **MenuController**
- Nhận dữ liệu sản phẩm từ MenuController
- Hiển thị danh sách sản phẩm theo danh mục

### **TableController**
- Nhận thông tin bàn từ TableController
- Cập nhật trạng thái bàn khi có đơn hàng

### **DashboardController**
- Được quản lý bởi DashboardController
- Hiển thị trong tab Order của dashboard

## 🛠️ Cấu hình Database

### **Database Config**
```properties
# config/database_config.properties
database.url=jdbc:mysql://localhost:3306/cafe_management
database.username=root
database.password=your_password
database.driver=com.mysql.cj.jdbc.Driver
```

### **Required Tables**
- `products`: Thông tin sản phẩm
- `categories`: Danh mục sản phẩm
- `orders`: Đơn hàng
- `order_details`: Chi tiết đơn hàng
- `tables`: Thông tin bàn

## 🧪 Testing

### **Chạy Demo**
```bash
# Compile project
mvn compile

# Chạy demo
java -cp target/classes com.cafe.demo.OrderPanelDemo
```

### **Test Database Connection**
```java
OrderPanelController controller = new OrderPanelController();
controller.testDatabaseConnection();
```

## ⚠️ Lưu ý quan trọng

1. **Database Connection**: Đảm bảo MySQL server đang chạy và database `cafe_management` đã được tạo
2. **Dependencies**: Cần có đầy đủ các dependency trong `pom.xml`
3. **Session Management**: `currentUserId` hiện tại hardcode = 1, cần cập nhật để lấy từ session
4. **Error Handling**: Tất cả các operation đều có try-catch để xử lý lỗi
5. **UI Updates**: Controller tự động cập nhật UI khi có thay đổi dữ liệu

## 🔧 Troubleshooting

### **Lỗi kết nối database**
- Kiểm tra MySQL server có đang chạy không
- Kiểm tra thông tin kết nối trong `database_config.properties`
- Kiểm tra database `cafe_management` đã được tạo chưa

### **Lỗi không tìm thấy sản phẩm**
- Kiểm tra bảng `products` có dữ liệu không
- Kiểm tra sản phẩm có `is_available = true` và `is_active = true` không

### **Lỗi tạo đơn hàng**
- Kiểm tra bảng `orders` và `order_details` có đúng schema không
- Kiểm tra quyền truy cập database

## 📝 Changelog

### **Version 1.0.0**
- ✅ Khắc phục lỗi linter
- ✅ Thêm kết nối database
- ✅ Thêm method để nhận dữ liệu từ menu
- ✅ Thêm method để quản lý table
- ✅ Thêm error handling
- ✅ Thêm demo class để test

---

**Tác giả**: Team 2_C2406L  
**Ngày cập nhật**: 31/07/2025

