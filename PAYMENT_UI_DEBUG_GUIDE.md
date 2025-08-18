# 🔧 Hướng dẫn Debug Giao diện Thanh toán

## Vấn đề hiện tại
Không thể mở giao diện thanh toán trong ứng dụng Cafe Management System.

## Phân tích ban đầu
- ✅ Code đã compile thành công (không có lỗi cú pháp)
- ✅ File FXML và CSS đã tồn tại
- ❓ Vấn đề có thể xảy ra ở runtime

## Các vấn đề có thể gây lỗi

### 1. **Resource Loading Issues**
- File FXML không được tìm thấy khi runtime
- File CSS không load được
- Path không đúng trong JAR/classpath

### 2. **Data Validation Issues** 
- `currentOrder` có thể null
- `currentOrderDetails` rỗng
- Dữ liệu order không hợp lệ

### 3. **JavaFX Threading Issues**
- Load FXML không đúng thread
- Stage/Scene initialization problems

### 4. **Dependencies Issues**
- JavaFX modules thiếu
- FXML loader không hoạt động

## 🔍 Cách Debug

### Bước 1: Chạy ứng dụng và kiểm tra Console
1. Chạy ứng dụng: `mvn javafx:run` hoặc `java -jar target/cafe-management.jar`
2. Thực hiện các bước để đến giao diện thanh toán:
   - Đăng nhập
   - Chọn bàn
   - Thêm món
   - Đặt hàng
   - Click nút "Thanh toán"
3. Kiểm tra console output, tìm các message:
   ```
   🔧 showModernPaymentWindow() started
   🔍 =====DEBUGGING PAYMENT RESOURCES=====
   ```

### Bước 2: Phân tích Debug Output
Kiểm tra các thông báo debug:

#### ✅ **Thành công** - Output mong đợi:
```
🔧 showModernPaymentWindow() started
  - currentOrder: ORD-20241219-001
  - currentTableId: 5
  - currentOrderDetails.size(): 3
🔍 =====DEBUGGING PAYMENT RESOURCES=====
✅ FXML found: jar:file:/path/to/app.jar!/fxml/payment/payment.fxml
✅ CSS found: jar:file:/path/to/app.jar!/css/payment.css
✅ Order data:
  - Order ID: 1
  - Order Number: ORD-20241219-001
  - Table ID: 5
  - Status: CONFIRMED
  - Payment Status: PENDING
  - Total Amount: 150000.0
✅ Order Details:
  - Details count: 3
  - Item 1: Cà phê đen x2 = 50000.0
  - Item 2: Bánh croissant x1 = 35000.0
  - Item 3: Nước cam x2 = 65000.0
🔍 =================================
🔧 Loading payment FXML...
🔧 Loading FXML content...
✅ FXML loaded successfully
🔧 Getting PaymentController...
✅ PaymentController obtained
🔧 Initializing payment data...
✅ Payment data initialized
🔧 Creating payment stage...
🔧 Creating scene and loading CSS...
🔧 Payment window size: 950.0x750.0
🔧 Showing payment window...
```

#### ❌ **Lỗi thường gặp** - Các vấn đề có thể có:

**1. Resource không tìm thấy:**
```
❌ FXML không tìm thấy: /fxml/payment/payment.fxml
❌ CSS không tìm thấy: /css/payment.css
```
→ **Giải pháp**: Kiểm tra cấu trúc thư mục resources

**2. Order data null:**
```
❌ CRITICAL: currentOrder is NULL
```
→ **Giải pháp**: Đảm bảo đã đặt hàng trước khi thanh toán

**3. FXML loading failed:**
```
❌ Error showing payment window: ...
```
→ **Giải pháp**: Kiểm tra file FXML và controller binding

### Bước 3: Kiểm tra cấu trúc file
Đảm bảo các file tồn tại:
```
src/main/resources/
├── fxml/payment/payment.fxml  ✅
├── css/payment.css            ✅
└── ...
```

### Bước 4: Test với mock data
Nếu vấn đề là order data, có thể tạo mock order để test:
1. Tạo order test trong database
2. Hoặc sử dụng method `debugPaymentWindow()` đã được thêm

## 🛠️ Các giải pháp có thể

### Giải pháp 1: Resource Path Issues
```java
// Thử các path khác nếu resource không tìm thấy
FXMLLoader loader = new FXMLLoader();
loader.setLocation(getClass().getResource("/fxml/payment/payment.fxml"));
// hoặc
loader.setLocation(PaymentController.class.getResource("/fxml/payment/payment.fxml"));
```

### Giải pháp 2: Threading Issues  
```java
Platform.runLater(() -> {
    // Load payment window on JavaFX Application Thread
    showModernPaymentWindow();
});
```

### Giải pháp 3: Alternative FXML Loading
```java
try {
    FXMLLoader loader = new FXMLLoader();
    InputStream fxmlStream = getClass().getResourceAsStream("/fxml/payment/payment.fxml");
    if (fxmlStream == null) {
        throw new IOException("FXML file not found");
    }
    BorderPane root = loader.load(fxmlStream);
    // ...
} catch (Exception e) {
    // Handle error
}
```

## 📝 Kết quả Debug

### ✅ **CÁC VẤN ĐỀ ĐÃ XÁC ĐỊNH VÀ SỬA**

#### **Vấn đề 1: Missing resource key**
**Nguyên nhân**: File FXML sử dụng ký tự `%` trong text attributes
- JavaFX FXML loader hiểu `%` là resource bundle key reference
- Khi không có ResourceBundle, JavaFX throws "Missing resource key" exception

#### **Vấn đề 2: ToggleGroup layout error**  
**Nguyên nhân**: ToggleGroup được đặt như child element của layout container
- `ToggleGroup` không phải là `Node` và không thể add vào HBox/VBox
- Lỗi: `Unable to coerce ToggleGroup to class javafx.scene.Node`

#### **Vấn đề 3: Deprecated escape sequence**
**Nguyên nhân**: JavaFX 24 đã deprecated `%%` syntax
- Cảnh báo: `%% is a deprecated escape sequence. Please use \% instead.`

**Giải pháp đã áp dụng**:
- ✅ **ToggleGroup**: Di chuyển vào `<fx:define>` thay vì layout container
- ✅ **Escape sequence**: Thay `%%` bằng `\%` cho JavaFX 24
- ✅ **Fixed 2 ToggleGroups**: `discountTypeGroup` và `paymentMethodGroup`

**Files đã sửa**:
- `src/main/resources/fxml/payment/payment.fxml` - Multiple fixes

**Trước khi sửa**:
```xml
<HBox>
    <ToggleGroup fx:id="discountTypeGroup" />  <!-- ❌ Wrong placement -->
    <RadioButton text="%%" />                  <!-- ❌ Deprecated -->
</HBox>
```

**Sau khi sửa**:
```xml
<VBox>
    <fx:define>
        <ToggleGroup fx:id="discountTypeGroup" />  <!-- ✅ Correct placement -->
    </fx:define>
    <HBox>
        <RadioButton text="\%" />                  <!-- ✅ Modern syntax -->
    </HBox>
</VBox>
```

## 🔄 Test Fix
1. Compile dự án: `mvn compile -q` ✅
2. Chạy ứng dụng: `mvn javafx:run`
3. Test payment flow:
   - Đăng nhập → Chọn bàn → Thêm món → Đặt hàng → **Click "Thanh toán"**
4. **Expect**: Giao diện thanh toán mở thành công

---

**Note**: Debug methods đã được thêm vào `OrderPanelController.java`:
- `debugResourcePaths()` - Kiểm tra resource và data
- `debugPaymentWindow()` - Test payment window riêng
