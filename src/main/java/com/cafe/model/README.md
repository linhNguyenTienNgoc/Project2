# 📊 Model Layer Documentation

## 📋 Tổng quan

Model layer chứa tất cả entities và enums đại diện cho cấu trúc dữ liệu của hệ thống quản lý quán cà phê.

## 🏗️ Cấu trúc

```
src/main/java/com/cafe/model/
├── entity/          # Các entity classes
├── enums/           # Các enum cho trạng thái
└── dto/             # Data Transfer Objects (sẽ tạo sau)
```

## 📊 Entities

### Core Entities (7 entities chính)

| Entity | Mô tả | Chức năng chính |
|--------|-------|-----------------|
| **User** | Người dùng hệ thống | Quản lý thông tin nhân viên, authentication |
| **Role** | Vai trò người dùng | Phân quyền (Admin, Manager, Cashier, Waiter, Barista) |
| **Customer** | Khách hàng | Thông tin KH, loyalty points, lịch sử mua hàng |
| **Product** | Sản phẩm | Menu items với giá, mô tả, hình ảnh |
| **Category** | Danh mục sản phẩm | Phân loại menu (Cà phê, Trà, Bánh, Món ăn) |
| **Area** | Khu vực | Vùng trong quán (Tầng 1, Tầng 2, VIP, Sân thượng) |
| **TableCafe** | Bàn | Bàn trong quán với capacity và trạng thái |

### Transaction Entities (3 entities giao dịch)

| Entity | Mô tả | Quan hệ |
|--------|-------|---------|
| **Order** | Đơn hàng | 1 Order → N OrderDetails |
| **OrderDetail** | Chi tiết đơn hàng | N OrderDetails → 1 Product |
| **OrderPromotion** | Khuyến mãi áp dụng | N Orders ↔ N Promotions |

### ~~Inventory Entities~~ (Removed for simplicity)
*Note: Inventory management features (Ingredient, Recipe, StockIn) have been removed to simplify the initial system. Can be added in future versions if needed.*

### Support Entities (2 entities hỗ trợ)

| Entity | Mô tả | Chức năng |
|--------|-------|-----------|
| **Promotion** | Khuyến mãi | Giảm giá theo %, số tiền cố định |
| **Attendance** | Chấm công | Check-in/out, tính giờ làm, overtime |

## 📋 Enums

| Enum | Values | Mô tả |
|------|--------|-------|
| **OrderStatus** | pending, preparing, ready, served, completed, cancelled | Trạng thái đơn hàng |
| **PaymentStatus** | pending, paid, cancelled | Trạng thái thanh toán |
| **PaymentMethod** | cash, card, momo, vnpay, zalopay, bank_transfer | Phương thức thanh toán |
| **TableStatus** | available, occupied, reserved, cleaning | Trạng thái bàn |

## 🔗 Quan hệ chính

### User Management
```
roles (1) ←→ (N) users
users (1) ←→ (N) attendance
```

### Menu & Products
```
categories (1) ←→ (N) products
```

### Orders & Sales
```
customers (1) ←→ (N) orders
tables (1) ←→ (N) orders
users (1) ←→ (N) orders (staff)
orders (1) ←→ (N) order_details ←→ (N) products
orders (N) ←→ (N) promotions (order_promotions)
```

### ~~Inventory~~ (Removed)
*Inventory management relationships have been simplified.*

## 🎯 Business Logic Methods

### Customer Entity
- `addLoyaltyPoints(int points)` - Thêm điểm tích lũy
- `useLoyaltyPoints(int points)` - Sử dụng điểm
- `isVIP()` - Kiểm tra khách VIP
- `getCustomerLevel()` - Lấy level khách hàng

### Order & OrderDetail
- `calculateTotalPrice()` - Tính tổng giá
- `addDiscount(double amount)` - Áp dụng giảm giá
- `canCancel()` - Kiểm tra có thể hủy không
- `updateQuantity(int qty)` - Cập nhật số lượng

### Promotion
- `canApplyToOrder(double amount)` - Kiểm tra áp dụng được không
- `calculateDiscountAmount(double amount)` - Tính tiền giảm
- `isInValidPeriod()` - Kiểm tra thời gian hiệu lực

### Ingredient & Inventory
- `needsRestocking()` - Cần nhập thêm hàng
- `hasEnoughStock(double amount)` - Đủ tồn kho
- `useIngredient(double amount)` - Sử dụng nguyên liệu
- `addStock(double amount)` - Nhập thêm hàng

### Attendance
- `performCheckIn()` - Thực hiện check-in
- `performCheckOut()` - Thực hiện check-out
- `calculateTotalHours()` - Tính tổng giờ làm
- `isOvertime()` - Kiểm tra làm thêm giờ
- `getAttendanceScore()` - Tính điểm chấm công

## 💡 Cách sử dụng

### Tạo đơn hàng mới
```java
// Tạo order
Order order = new Order();
order.setTableId(5);
order.setUserId(SessionManager.getCurrentUserId());
order.setOrderStatus(OrderStatus.PENDING.getValue());

// Thêm sản phẩm
OrderDetail detail = new OrderDetail(order.getOrderId(), 1, 2, 50000);
detail.calculateTotalPrice(); // 100,000 VND

// Áp dụng khuyến mãi
Promotion promo = new Promotion("Giảm 10%", "Giảm giá 10%", 
    Promotion.DiscountType.PERCENTAGE, 10, 0);
double discount = promo.calculateDiscountAmount(100000); // 10,000 VND

order.setTotalAmount(100000);
order.setDiscountAmount(discount);
order.setFinalAmount(90000);
```

### Quản lý tồn kho
```java
// Kiểm tra nguyên liệu
Ingredient coffee = new Ingredient("Cà phê arabica", "kg", 10, 200000);
coffee.setCurrentStock(5);

if (coffee.needsRestocking()) {
    System.out.println("Cần nhập thêm " + coffee.getRestockAmount() + " kg");
}

// Sử dụng nguyên liệu
Recipe recipe = new Recipe(1, 1, 0.02); // 20g cà phê cho 1 ly
if (coffee.hasEnoughStock(recipe.getQuantityRequired())) {
    coffee.useIngredient(recipe.getQuantityRequired());
}
```

### Chấm công
```java
// Check-in
Attendance attendance = new Attendance(userId);
attendance.performCheckIn();

// Check-out (sau khi làm việc)
attendance.performCheckOut();

if (attendance.isOvertime()) {
    System.out.println("Làm thêm: " + attendance.getOvertimeHours() + " giờ");
}
```

## 🔒 Validation Rules

### Bắt buộc phải có
- **User:** username, password, fullName, roleId
- **Customer:** fullName, phone
- **Product:** productName, categoryId, price
- **Order:** tableId, userId, orderDate
- **OrderDetail:** orderId, productId, quantity, unitPrice

### Business Rules
- **Price:** >= 0
- **Quantity:** > 0 
- **Discount:** 0 <= discount <= totalAmount
- **Stock:** currentStock >= 0
- **Hours:** totalHours >= 0

## 🧪 Testing

Các entities đều có:
- `toString()` method cho debugging
- `equals()` và `hashCode()` cho collections
- `isValid()` method cho validation
- Business methods với error handling

## 📈 Performance Tips

1. **Lazy Loading:** Chỉ load data cần thiết
2. **Caching:** Cache frequently used data
3. **Indexing:** Index các foreign keys trong database
4. **Pagination:** Limit records cho large datasets
5. **Connection Pooling:** Sử dụng HikariCP

## 🔄 Future Enhancements

- **Audit Trail:** Track changes cho các entities quan trọng
- **Soft Delete:** Mark as deleted thay vì xóa thực sự
- **Versioning:** Version control cho entities
- **Event Sourcing:** Store domain events
- **Multi-tenancy:** Support multiple cafe locations