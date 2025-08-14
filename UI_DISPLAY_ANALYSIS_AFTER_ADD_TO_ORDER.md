# Phân tích UI Hiển thị sau khi addToOrder()

## 📋 **Tổng quan**

Khi gọi `addToOrder(productName, price, quantity)`, UI sẽ được cập nhật để hiển thị món mới trong order panel. Dưới đây là phân tích chi tiết về luồng hiển thị UI.

## 🔄 **Luồng UI Update**

### **1. Entry Point: addToOrder()**
```java
public void addToOrder(String productName, double price, int quantity) {
    try {
        updateOrderItemUI(productName, price, quantity);
        System.out.println("✅ Added product to order: " + productName + " x" + quantity);
    } catch (Exception e) {
        // Error handling
    }
}
```

### **2. UI Update Logic: updateOrderItemUI()**

#### **Bước 1: Kiểm tra UI availability**
```java
if (orderItemsContainer == null) {
    updateOrderItemInternal(productName, price, quantity);
    return; // Không update UI nếu container null
}
```

#### **Bước 2: Xóa placeholder nếu cần**
```java
if (orderItemsContainer.getChildren().size() == 1 &&
    orderItemsContainer.getChildren().get(0) instanceof Label) {
    orderItemsContainer.getChildren().clear();
}
```

#### **Bước 3: Kiểm tra món đã tồn tại**
```java
OrderItem existingItem = orderItems.get(productName);
```

#### **Bước 4A: Cập nhật món đã tồn tại**
```java
if (existingItem != null) {
    existingItem.quantity += quantity;
    existingItem.totalPrice = existingItem.price * existingItem.quantity;
    updateOrderItemRow(existingItem); // Cập nhật UI row
}
```

#### **Bước 4B: Tạo món mới**
```java
else {
    OrderItem newItem = new OrderItem(productName, price, quantity);
    orderItems.put(productName, newItem);
    HBox orderItem = createOrderItemRow(productName, price, quantity);
    orderItemsContainer.getChildren().add(orderItem); // Thêm UI row mới
}
```

#### **Bước 5: Cập nhật tổng tiền**
```java
totalAmount += price * quantity;
updateTotalAmount(); // Cập nhật totalAmountLabel
```

## 🎨 **UI Components được cập nhật**

### **1. orderItemsContainer (VBox)**
- **Vị trí**: Trong ScrollPane của order panel
- **Thay đổi**: 
  - Xóa placeholder label "Chưa có món nào được chọn"
  - Thêm hoặc cập nhật HBox row cho món

### **2. Order Item Row (HBox)**
Mỗi row chứa 4 elements:
```java
HBox row = new HBox(8);
row.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 4;");

// 1. Tên sản phẩm
Label nameLabel = new Label(productName);
nameLabel.setStyle("-fx-font-size: 11px; -fx-pref-width: 100;");

// 2. Số lượng
Label quantityLabel = new Label(String.valueOf(quantity));
quantityLabel.setStyle("-fx-font-size: 11px; -fx-alignment: center; -fx-pref-width: 30;");

// 3. Giá tiền
Label priceLabel = new Label(String.format("%,.0f VNĐ", price * quantity));
priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #E67E22; -fx-font-weight: bold; -fx-pref-width: 80;");

// 4. Nút xóa
Button removeButton = new Button("×");
removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
```

### **3. totalAmountLabel (Label)**
- **Vị trí**: Trong Order Summary section
- **Thay đổi**: Cập nhật text với format "X,XXX VNĐ"
- **Style**: `-fx-font-weight: bold; -fx-text-fill: #E67E22; -fx-font-size: 16px;`

## 📊 **Các trường hợp hiển thị**

### **Trường hợp 1: Thêm món đầu tiên**
```
Trước: [Chưa có món nào được chọn]
Sau:  [Cà phê đen] [2] [50,000 VNĐ] [×]
      Tổng tiền: 50,000 VNĐ
```

### **Trường hợp 2: Thêm món thứ hai**
```
Trước: [Cà phê đen] [2] [50,000 VNĐ] [×]
Sau:  [Cà phê đen] [2] [50,000 VNĐ] [×]
      [Bánh tiramisu] [1] [35,000 VNĐ] [×]
      Tổng tiền: 85,000 VNĐ
```

### **Trường hợp 3: Thêm món đã tồn tại**
```
Trước: [Cà phê đen] [2] [50,000 VNĐ] [×]
Sau:  [Cà phê đen] [3] [75,000 VNĐ] [×]
      Tổng tiền: 75,000 VNĐ
```

## ⚠️ **Các vấn đề UI có thể xảy ra**

### **1. UI không hiển thị (orderItemsContainer = null)**
```java
// Nguyên nhân: FXML injection failed
// Kết quả: Chỉ update internal state, không update UI
updateOrderItemInternal(productName, price, quantity);
```

### **2. Placeholder không được xóa**
```java
// Nguyên nhân: Logic kiểm tra placeholder sai
if (orderItemsContainer.getChildren().size() == 1 &&
    orderItemsContainer.getChildren().get(0) instanceof Label) {
    // Có thể fail nếu có nhiều elements
}
```

### **3. Row không được cập nhật đúng**
```java
// Nguyên nhân: Tìm row sai trong updateOrderItemRow()
for (int i = 0; i < orderItemsContainer.getChildren().size(); i++) {
    Node node = orderItemsContainer.getChildren().get(i);
    if (node instanceof HBox) {
        HBox row = (HBox) node;
        Label nameLabel = (Label) row.getChildren().get(0);
        if (nameLabel.getText().equals(item.productName)) {
            // Có thể fail nếu text không match chính xác
        }
    }
}
```

### **4. Total amount không cập nhật**
```java
// Nguyên nhân: totalAmountLabel = null
private void updateTotalAmount() {
    if (totalAmountLabel != null) {
        totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
    }
}
```

### **5. Style không được áp dụng**
```java
// Nguyên nhân: CSS style không load hoặc bị override
row.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 4;");
```

## 🔍 **Kiểm tra UI hiển thị**

### **1. Kiểm tra FXML injection**
```java
// Trong initialize()
System.out.println("orderItemsContainer: " + (orderItemsContainer != null ? "✅" : "❌"));
System.out.println("totalAmountLabel: " + (totalAmountLabel != null ? "✅" : "❌"));
```

### **2. Kiểm tra UI update**
```java
// Trong updateOrderItemUI()
System.out.println("UI Container children count: " + orderItemsContainer.getChildren().size());
System.out.println("Total amount: " + totalAmount);
```

### **3. Kiểm tra row creation**
```java
// Trong createOrderItemRow()
System.out.println("Created row for: " + productName);
System.out.println("Row children count: " + row.getChildren().size());
```

## 🎯 **Các điểm cần lưu ý**

### **1. Thread Safety**
- UI updates phải chạy trên JavaFX Application Thread
- Nếu gọi từ background thread, cần dùng `Platform.runLater()`

### **2. Memory Management**
- UI elements được tạo động cần được cleanup khi remove
- Tránh memory leak khi tạo nhiều rows

### **3. Performance**
- Không nên update UI quá frequently
- Có thể batch multiple updates

### **4. Accessibility**
- Labels cần có meaningful text
- Buttons cần có proper tooltips

## 📝 **Kết luận**

UI hiển thị sau khi `addToOrder()` phụ thuộc vào:
1. **FXML injection** thành công
2. **Logic update** chính xác
3. **Thread safety** đảm bảo
4. **Error handling** phù hợp

Nếu UI không hiển thị, cần kiểm tra:
- FXML elements có được inject không
- Logic update có chạy đúng không
- Có exception nào xảy ra không
- Thread có đúng JavaFX Application Thread không

