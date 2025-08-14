# Phân tích Luồng Dữ Liệu - Phương thức addToOrder

## 📋 **Tổng quan phương thức**

```java
public void addToOrder(String productName, double price, int quantity)
```

**Mục đích**: Thêm sản phẩm vào đơn hàng tạm thời trong bộ nhớ và cập nhật giao diện người dùng.

## 🔄 **Luồng dữ liệu chi tiết**

### **1. Dữ liệu vào (Input)**

#### **Tham số đầu vào:**
- `productName` (String): Tên sản phẩm
- `price` (double): Giá đơn vị của sản phẩm
- `quantity` (int): Số lượng cần thêm

#### **Trạng thái hiện tại (State):**
- `orderItemsContainer` (VBox): Container UI cho danh sách món
- `orderItems` (Map<String, OrderItem>): Map lưu trữ món tạm thời
- `totalAmount` (double): Tổng tiền hiện tại
- `menuService` (MenuService): Service để truy vấn sản phẩm

### **2. Luồng xử lý chính**

#### **Bước 1: Gọi updateOrderItemUI()**
```java
updateOrderItemUI(productName, price, quantity);
```

#### **Bước 2: Kiểm tra UI availability**
```java
if (orderItemsContainer == null) {
    updateOrderItemInternal(productName, price, quantity);
    return;
}
```

#### **Bước 3: Xóa placeholder nếu cần**
```java
if (orderItemsContainer.getChildren().size() == 1 &&
    orderItemsContainer.getChildren().get(0) instanceof Label) {
    orderItemsContainer.getChildren().clear();
}
```

#### **Bước 4: Kiểm tra món đã tồn tại**
```java
OrderItem existingItem = orderItems.get(productName);
```

#### **Bước 5A: Cập nhật món đã tồn tại**
```java
if (existingItem != null) {
    existingItem.quantity += quantity;
    existingItem.totalPrice = existingItem.price * existingItem.quantity;
    updateOrderItemRow(existingItem);
}
```

#### **Bước 5B: Tạo món mới**
```java
else {
    OrderItem newItem = new OrderItem(productName, price, quantity);
    orderItems.put(productName, newItem);
    HBox orderItem = createOrderItemRow(productName, price, quantity);
    orderItemsContainer.getChildren().add(orderItem);
}
```

#### **Bước 6: Cập nhật tổng tiền**
```java
totalAmount += price * quantity;
updateTotalAmount();
```

### **3. Dữ liệu ra (Output)**

#### **Trạng thái được cập nhật:**
- `orderItems`: Map chứa OrderItem mới hoặc cập nhật
- `totalAmount`: Tổng tiền được cộng thêm
- `orderItemsContainer`: UI được cập nhật với món mới

#### **Log output:**
- Console log: `"✅ Added product to order: {productName} x{quantity}"`

#### **UI changes:**
- Thêm hoặc cập nhật row trong order panel
- Cập nhật total amount label
- Xóa placeholder nếu cần

### **4. Biến trạng thái bị thay đổi**

#### **Trực tiếp thay đổi:**
1. **`orderItems`** (Map<String, OrderItem>)
   - Thêm key-value mới hoặc cập nhật existing item
   - Key: `productName`
   - Value: `OrderItem` object

2. **`totalAmount`** (double)
   - Tăng thêm: `price * quantity`

3. **`orderItemsContainer`** (VBox)
   - Thêm hoặc cập nhật UI row
   - Xóa placeholder label

#### **Gián tiếp thay đổi:**
1. **`totalAmountLabel`** (Label)
   - Được cập nhật qua `updateTotalAmount()`

2. **Existing OrderItem objects**
   - `quantity`: Tăng thêm
   - `totalPrice`: Tính lại = `price * quantity`

### **5. Các điểm có khả năng gây lỗi**

#### **🔴 Lỗi nghiêm trọng:**

1. **NullPointerException**
   ```java
   // Điểm 1: orderItemsContainer có thể null
   if (orderItemsContainer == null) { ... }
   
   // Điểm 2: existingItem có thể null
   existingItem.quantity += quantity; // NPE nếu existingItem null
   
   // Điểm 3: orderItems có thể null
   orderItems.get(productName); // NPE nếu orderItems null
   ```

2. **ConcurrentModificationException**
   ```java
   // Điểm 4: Thay đổi orderItems trong khi đang iterate
   for (OrderItem item : orderItems.values()) { ... }
   ```

3. **ArithmeticException**
   ```java
   // Điểm 5: Overflow khi tính toán
   existingItem.quantity += quantity; // Overflow nếu quantity quá lớn
   totalAmount += price * quantity; // Overflow nếu giá trị quá lớn
   ```

#### **🟡 Lỗi logic:**

4. **Data inconsistency**
   ```java
   // Điểm 6: Inconsistency giữa UI và data
   existingItem.totalPrice = existingItem.price * existingItem.quantity;
   // Có thể khác với price * quantity từ parameter
   ```

5. **Memory leak**
   ```java
   // Điểm 7: UI elements không được cleanup
   orderItemsContainer.getChildren().add(orderItem);
   // Nếu remove không đúng cách
   ```

6. **Race condition**
   ```java
   // Điểm 8: Multiple threads access cùng lúc
   orderItems.put(productName, newItem);
   totalAmount += price * quantity;
   ```

#### **🟠 Lỗi validation:**

7. **Invalid input data**
   ```java
   // Điểm 9: Không validate input
   public void addToOrder(String productName, double price, int quantity)
   // - productName có thể null/empty
   // - price có thể âm
   // - quantity có thể âm hoặc 0
   ```

8. **Business rule violation**
   ```java
   // Điểm 10: Không kiểm tra business rules
   // - Sản phẩm có tồn tại không?
   // - Có đủ stock không?
   // - Giá có đúng không?
   ```

### **6. Các điểm sai lệch dữ liệu**

#### **🔄 Inconsistency issues:**

1. **Price inconsistency**
   ```java
   // Vấn đề: Giá từ parameter có thể khác giá trong database
   OrderItem newItem = new OrderItem(productName, price, quantity);
   // price từ parameter, không phải từ database
   ```

2. **Quantity calculation error**
   ```java
   // Vấn đề: Tính toán sai khi update existing item
   existingItem.quantity += quantity;
   existingItem.totalPrice = existingItem.price * existingItem.quantity;
   // Có thể khác với: (existingItem.quantity + quantity) * existingItem.price
   ```

3. **Total amount drift**
   ```java
   // Vấn đề: totalAmount có thể không khớp với sum của orderItems
   totalAmount += price * quantity;
   // Không đồng bộ với: orderItems.values().stream().mapToDouble(item -> item.totalPrice).sum()
   ```

#### **📊 Data integrity issues:**

4. **Duplicate handling**
   ```java
   // Vấn đề: Xử lý duplicate không nhất quán
   OrderItem existingItem = orderItems.get(productName);
   // Chỉ dựa vào productName, không xét đến price
   ```

5. **State synchronization**
   ```java
   // Vấn đề: UI và data state không đồng bộ
   orderItems.put(productName, newItem);
   // UI update có thể fail nhưng data đã thay đổi
   ```

### **7. Đề xuất cải thiện**

#### **🛡️ Validation & Error Handling:**
```java
public void addToOrder(String productName, double price, int quantity) {
    // Validate input
    if (productName == null || productName.trim().isEmpty()) {
        throw new IllegalArgumentException("Product name cannot be null or empty");
    }
    if (price < 0) {
        throw new IllegalArgumentException("Price cannot be negative");
    }
    if (quantity <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
    }
    
    // Validate business rules
    Product product = findProductByName(productName);
    if (product == null) {
        throw new IllegalStateException("Product not found: " + productName);
    }
    if (product.getPrice() != price) {
        throw new IllegalStateException("Price mismatch for product: " + productName);
    }
    
    // Rest of the method...
}
```

#### **🔒 Thread Safety:**
```java
private final Object orderLock = new Object();

public void addToOrder(String productName, double price, int quantity) {
    synchronized(orderLock) {
        // Method implementation
    }
}
```

#### **📊 Data Consistency:**
```java
private void recalculateTotalAmount() {
    totalAmount = orderItems.values().stream()
        .mapToDouble(item -> item.totalPrice)
        .sum();
    updateTotalAmount();
}
```

#### **🧪 Unit Testing:**
```java
@Test
public void testAddToOrder_ValidInput_ShouldUpdateOrder() {
    // Test cases for various scenarios
}
```

## 📝 **Kết luận**

Phương thức `addToOrder` có luồng dữ liệu phức tạp với nhiều điểm có thể gây lỗi. Cần cải thiện validation, error handling, và data consistency để đảm bảo tính ổn định và chính xác của hệ thống.
