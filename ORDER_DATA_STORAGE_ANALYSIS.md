# PHÂN TÍCH LƯU TRỮ DỮ LIỆU ORDER TẠM

## Tổng quan

Dữ liệu order tạm trong hệ thống Cafe Management được lưu trữ ở **2 cấp độ**:

1. **Local Memory (RAM)** - Dữ liệu tạm thời trong session
2. **Database** - Dữ liệu vĩnh viễn khi đã xác nhận

---

## 1. LOCAL MEMORY STORAGE (RAM)

### Vị trí lưu trữ:
```java
// Trong OrderPanelController.java
private Map<String, OrderItem> orderItems = new HashMap<>();
private Order currentOrder = null;
private double totalAmount = 0.0;
```

### Cấu trúc dữ liệu:

#### OrderItem (Inner Class):
```java
private static class OrderItem {
    String productName;    // Tên sản phẩm
    double price;          // Giá đơn vị
    int quantity;          // Số lượng
    double totalPrice;     // Tổng tiền = price * quantity
}
```

#### Các biến state:
```java
// Order management
private Order currentOrder = null;                    // Order hiện tại
private Map<String, OrderItem> orderItems = new HashMap<>();  // Danh sách món đã chọn
private double totalAmount = 0.0;                     // Tổng tiền

// Table information
private String currentTableName = "--";               // Tên bàn
private Integer currentTableId = null;                // ID bàn
private TableStatus currentTableStatus = TableStatus.AVAILABLE;  // Trạng thái bàn
private Integer currentUserId = 1;                    // ID nhân viên
```

### Quy trình lưu trữ:

#### 1. Khi thêm món (addToOrder):
```java
public void addToOrder(String productName, double price, int quantity) {
    // 1. Cập nhật orderItems Map
    updateOrderItemUI(productName, price, quantity);
    
    // 2. Cập nhật totalAmount
    totalAmount += price * quantity;
    
    // 3. Cập nhật UI
    updateTotalAmount();
}
```

#### 2. Khi xóa món:
```java
private void removeOrderItem(HBox itemRow, String productName, double itemTotal) {
    // 1. Xóa khỏi UI
    orderItemsContainer.getChildren().remove(itemRow);
    
    // 2. Xóa khỏi Map
    orderItems.remove(productName);
    
    // 3. Cập nhật tổng tiền
    totalAmount -= itemTotal;
}
```

#### 3. Khi clear order:
```java
public void clearOrder() {
    // 1. Xóa tất cả dữ liệu local
    orderItems.clear();
    totalAmount = 0.0;
    currentOrder = null;
    
    // 2. Reset table info
    currentTableName = "--";
    currentTableId = null;
    currentTableStatus = TableStatus.AVAILABLE;
    currentUserId = 1;
}
```

---

## 2. DATABASE STORAGE

### Khi nào dữ liệu được lưu vào database:

#### 1. Khi thanh toán (processPayment):
```java
public void processPayment() {
    // 1. Tạo order mới trong database (nếu chưa có)
    if (currentOrder == null) {
        currentOrder = orderService.createOrder(currentTableId, currentUserId, null);
    }
    
    // 2. Thêm tất cả items từ orderItems vào database
    for (OrderItem item : orderItems.values()) {
        Product product = findProductByName(item.productName);
        if (product != null) {
            orderService.addProductToOrder(currentOrder, product, item.quantity, null);
        }
    }
    
    // 3. Cập nhật trạng thái order thành "paid"
    currentOrder.setOrderStatus("paid");
    currentOrder.setPaymentStatus("paid");
    orderService.updateOrderStatus(currentOrder);
    
    // 4. Clear dữ liệu local
    clearOrder();
}
```

#### 2. Khi đặt món (placeOrder):
```java
public void placeOrder() {
    // Cập nhật trạng thái order thành "preparing"
    currentOrder.setOrderStatus("preparing");
    currentOrder.setTotalAmount(totalAmount);
    currentOrder.setFinalAmount(totalAmount);
    
    orderService.updateOrderStatus(currentOrder);
}
```

### Cấu trúc database:

#### Bảng `orders`:
- `id` - Primary key
- `order_number` - Mã đơn hàng
- `table_id` - ID bàn
- `user_id` - ID nhân viên
- `customer_id` - ID khách hàng (có thể null)
- `order_date` - Ngày đặt
- `order_status` - Trạng thái (pending/preparing/completed/paid)
- `payment_status` - Trạng thái thanh toán
- `total_amount` - Tổng tiền
- `discount_amount` - Giảm giá
- `final_amount` - Tiền cuối cùng

#### Bảng `order_details`:
- `id` - Primary key
- `order_id` - Foreign key đến orders
- `product_id` - Foreign key đến products
- `quantity` - Số lượng
- `unit_price` - Giá đơn vị
- `total_price` - Tổng tiền
- `notes` - Ghi chú

---

## 3. FLOW DỮ LIỆU

### Quy trình hoàn chỉnh:

```
1. User chọn bàn
   ↓
2. User thêm món vào order
   ↓
3. Dữ liệu lưu vào RAM (orderItems Map)
   ↓
4. User có thể:
   - Thêm/xóa món (chỉ trong RAM)
   - Clear order (xóa RAM)
   - Place order (lưu vào DB với status "preparing")
   - Process payment (lưu vào DB với status "paid")
```

### Trạng thái dữ liệu:

| Giai đoạn | RAM | Database | Mô tả |
|-----------|-----|----------|-------|
| Chọn món | ✅ | ❌ | Chỉ lưu trong memory |
| Place Order | ✅ | ✅ | Lưu vào DB, status="preparing" |
| Payment | ✅ | ✅ | Cập nhật DB, status="paid", clear RAM |
| Clear Order | ❌ | ❌ | Xóa hoàn toàn |

---

## 4. PHÂN TÍCH LỖI VÀ SỬA CHỮA

### Lỗi đã phát hiện và sửa:

#### ❌ Lỗi 1: Thiếu reset currentUserId trong error recovery
**Vấn đề:**
```java
// Error recovery thiếu currentUserId
try {
    if (orderItems != null) orderItems.clear();
    totalAmount = 0.0;
    currentOrder = null;
    currentTableName = "--";
    currentTableId = null;
    currentTableStatus = TableStatus.AVAILABLE;
    // ❌ Thiếu: currentUserId = 1;
} catch (Exception recoveryError) {
    // ...
}
```

**Giải pháp:**
```java
// Đã sửa - thêm reset currentUserId
try {
    if (orderItems != null) orderItems.clear();
    totalAmount = 0.0;
    currentOrder = null;
    currentTableName = "--";
    currentTableId = null;
    currentTableStatus = TableStatus.AVAILABLE;
    currentUserId = 1; // ✅ Đã thêm
} catch (Exception recoveryError) {
    // ...
}
```

#### ❌ Lỗi 2: Phương thức clearOrder() private không thể test
**Vấn đề:**
```java
private void clearOrder() { // ❌ Không thể test từ bên ngoài
    // ...
}
```

**Giải pháp:**
```java
public void clearOrder() { // ✅ Đã sửa thành public để có thể test
    // ...
}
```

#### ❌ Lỗi 3: Thiếu comprehensive testing
**Vấn đề:** Không có test để verify việc xóa dữ liệu

**Giải pháp:** Đã tạo `OrderPanelControllerTest.java` với các test cases:
- Test clearOrder() functionality
- Test clearOrder() với table info
- Verify tất cả biến state được reset

### Cải thiện đã thực hiện:

1. **✅ Error Handling:** Thêm try-catch với recovery mechanism
2. **✅ Logging:** Detailed logging để debug
3. **✅ Null Safety:** Kiểm tra null trước khi thao tác
4. **✅ UI Synchronization:** Cập nhật tất cả UI elements
5. **✅ Testing:** Tạo comprehensive test suite
6. **✅ Documentation:** Cập nhật documentation chi tiết

---

## 5. ƯU ĐIỂM VÀ NHƯỢC ĐIỂM

### Ưu điểm:
- ✅ **Performance cao**: Không cần truy cập DB mỗi lần thêm món
- ✅ **User experience tốt**: Phản hồi nhanh
- ✅ **Flexibility**: Có thể sửa/xóa món dễ dàng
- ✅ **Offline capability**: Có thể hoạt động tạm thời không cần DB

### Nhược điểm:
- ❌ **Mất dữ liệu**: Nếu app crash, dữ liệu tạm sẽ mất
- ❌ **Memory usage**: Tốn RAM khi có nhiều order
- ❌ **No persistence**: Không lưu trữ lâu dài cho order chưa hoàn thành

---

## 6. CẢI THIỆN ĐỀ XUẤT

### 1. Auto-save to Database:
```java
// Tự động lưu order tạm vào DB với status "draft"
public void autoSaveOrder() {
    if (currentOrder == null && !orderItems.isEmpty()) {
        currentOrder = orderService.createOrder(currentTableId, currentUserId, null);
        currentOrder.setOrderStatus("draft");
    }
    
    // Lưu items vào DB
    for (OrderItem item : orderItems.values()) {
        // Save to order_details
    }
}
```

### 2. Session Storage:
```java
// Lưu vào session storage để recover khi restart
public void saveToSession() {
    // Serialize orderItems to JSON
    // Save to application session
}
```

### 3. Database Transaction:
```java
// Sử dụng transaction để đảm bảo consistency
public void processPaymentWithTransaction() {
    Connection conn = DatabaseConfig.getConnection();
    conn.setAutoCommit(false);
    try {
        // Create order
        // Add items
        // Update status
        conn.commit();
    } catch (Exception e) {
        conn.rollback();
    }
}
```

---

## KẾT LUẬN

Dữ liệu order tạm được lưu trữ chủ yếu trong **RAM (HashMap)** cho đến khi user thực hiện thanh toán hoặc đặt món. Điều này đảm bảo performance tốt nhưng có rủi ro mất dữ liệu. 

**Các lỗi đã được sửa:**
- ✅ Thiếu reset currentUserId trong error recovery
- ✅ Phương thức clearOrder() không thể test
- ✅ Thiếu comprehensive testing

**Cần cân nhắc implement auto-save hoặc session storage để cải thiện reliability.**
