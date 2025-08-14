# 🗂️ Entity Relationship Diagram (ERD)

## 📊 Sơ đồ mối quan hệ giữa các bảng

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│     ROLES       │     │      USERS      │     │   ATTENDANCE    │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ role_id (PK)    │◄────┤ role_id (FK)    │     │ attendance_id   │
│ role_name       │     │ user_id (PK)    │◄────┤ user_id (FK)    │
│ description     │     │ username        │     │ check_in        │
│ created_at      │     │ password        │     │ check_out       │
│ updated_at      │     │ full_name       │     │ work_date       │
└─────────────────┘     │ email           │     │ total_hours     │
                        │ phone           │     │ notes           │
                        │ is_active       │     │ created_at      │
                        │ created_at      │     └─────────────────┘
                        │ updated_at      │
                        └─────────────────┘
                                │
                                ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│    AREAS        │     │     TABLES      │     │     ORDERS      │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ area_id (PK)    │◄────┤ area_id (FK)    │     │ order_id (PK)   │
│ area_name       │     │ table_id (PK)   │◄────┤ table_id (FK)   │
│ description     │     │ table_name      │     │ order_number    │
│ is_active       │     │ capacity        │     │ customer_id (FK)│
│ created_at      │     │ status          │     │ user_id (FK)    │
│ updated_at      │     │ is_active       │     │ order_date      │
└─────────────────┘     │ created_at      │     │ total_amount    │
                        │ updated_at      │     │ discount_amount │
                        └─────────────────┘     │ final_amount    │
                                                 │ payment_method  │
                                                 │ payment_status  │
                                                 │ order_status    │
                                                 │ notes           │
                                                 │ created_at      │
                                                 │ updated_at      │
                                                 └─────────────────┘
                                                         │
                                                         ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   CUSTOMERS     │     │ ORDER_DETAILS   │     │   PRODUCTS      │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ customer_id (PK)│◄────┤ order_id (FK)   │     │ product_id (PK) │
│ full_name       │     │ order_detail_id │     │ product_name    │
│ phone           │     │ product_id (FK) │◄────┤ category_id (FK)│
│ email           │     │ quantity        │     │ price           │
│ address         │     │ unit_price      │     │ cost_price      │
│ loyalty_points  │     │ total_price     │     │ description     │
│ total_spent     │     │ notes           │     │ image_url       │
│ is_active       │     │ created_at      │     │ is_available    │
│ created_at      │     └─────────────────┘     │ is_active       │
│ updated_at      │                             │ created_at      │
└─────────────────┘                             │ updated_at      │
                                                └─────────────────┘
                                                        │
                                                        ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│   CATEGORIES    │     │   PROMOTIONS    │     │ORDER_PROMOTIONS │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ category_id (PK)│◄────┤ promotion_id    │     │ order_promotion │
│ category_name   │     │ promotion_name  │     │ order_id (FK)   │
│ description     │     │ description     │     │ promotion_id(FK)│
│ is_active       │     │ discount_type   │     │ discount_amount │
│ created_at      │     │ discount_value  │     │ created_at      │
│ updated_at      │     │ min_order_amount│     └─────────────────┘
└─────────────────┘     │ start_date      │
                        │ end_date        │
                        │ is_active       │
                        │ created_at      │
                        │ updated_at      │
                        └─────────────────┘
                                │
                                ▼
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  INGREDIENTS    │     │    RECIPES      │     │   STOCK_IN      │
├─────────────────┤     ├─────────────────┤     ├─────────────────┤
│ ingredient_id   │     │ recipe_id (PK)  │     │ stock_in_id     │
│ ingredient_name │     │ product_id (FK) │     │ ingredient_id   │
│ unit            │     │ ingredient_id   │     │ quantity        │
│ current_stock   │◄────┤ quantity_required│     │ unit_price      │
│ min_stock       │     │ created_at      │     │ total_amount    │
│ cost_per_unit   │     └─────────────────┘     │ supplier        │
│ supplier        │                             │ notes           │
│ is_active       │                             │ user_id (FK)    │
│ created_at      │                             │ stock_in_date   │
│ updated_at      │                             └─────────────────┘
└─────────────────┘
```

## 🔗 Mối quan hệ chính

### **1. Quản lý Người dùng**
- `roles` (1) ←→ (N) `users` - Một vai trò có nhiều người dùng
- `users` (1) ←→ (N) `attendance` - Một người dùng có nhiều bản ghi chấm công

### **2. Quản lý Khu vực & Bàn**
- `areas` (1) ←→ (N) `tables` - Một khu vực có nhiều bàn
- `tables` (1) ←→ (N) `orders` - Một bàn có thể có nhiều đơn hàng

### **3. Quản lý Sản phẩm**
- `categories` (1) ←→ (N) `products` - Một danh mục có nhiều sản phẩm
- `products` (1) ←→ (N) `order_details` - Một sản phẩm có thể có trong nhiều đơn hàng
- `products` (1) ←→ (N) `recipes` - Một sản phẩm có thể cần nhiều nguyên liệu

### **4. Quản lý Đơn hàng**
- `orders` (1) ←→ (N) `order_details` - Một đơn hàng có nhiều chi tiết
- `orders` (1) ←→ (N) `order_promotions` - Một đơn hàng có thể áp dụng nhiều khuyến mãi
- `customers` (1) ←→ (N) `orders` - Một khách hàng có thể có nhiều đơn hàng

### **5. Quản lý Nguyên liệu**
- `ingredients` (1) ←→ (N) `recipes` - Một nguyên liệu có thể dùng cho nhiều sản phẩm
- `ingredients` (1) ←→ (N) `stock_in` - Một nguyên liệu có thể nhập nhiều lần

## 📋 Ý nghĩa các trạng thái

### **Trạng thái Bàn (tables.status):**
- `available` - Trống, có thể sử dụng
- `occupied` - Có khách
- `reserved` - Đã đặt trước
- `cleaning` - Đang dọn dẹp

### **Trạng thái Đơn hàng (orders.order_status):**
- `pending` - Chờ xử lý
- `preparing` - Đang chuẩn bị
- `ready` - Sẵn sàng phục vụ
- `served` - Đã phục vụ
- `completed` - Hoàn thành
- `cancelled` - Đã hủy

### **Trạng thái Thanh toán (orders.payment_status):**
- `pending` - Chờ thanh toán
- `paid` - Đã thanh toán
- `cancelled` - Đã hủy

### **Phương thức Thanh toán (orders.payment_method):**
- `cash` - Tiền mặt
- `card` - Thẻ tín dụng/ghi nợ
- `momo` - Ví MoMo
- `vnpay` - VNPay
- `zalopay` - ZaloPay

## 🔍 Các truy vấn quan trọng

### **1. Lấy thông tin đơn hàng đầy đủ:**
```sql
SELECT 
    o.order_number,
    o.order_date,
    c.full_name as customer_name,
    u.full_name as staff_name,
    t.table_name,
    o.total_amount,
    o.final_amount,
    o.order_status,
    o.payment_status
FROM orders o
LEFT JOIN customers c ON o.customer_id = c.customer_id
LEFT JOIN users u ON o.user_id = u.user_id
LEFT JOIN tables t ON o.table_id = t.table_id
ORDER BY o.order_date DESC;
```

### **2. Lấy chi tiết đơn hàng:**
```sql
SELECT 
    od.order_detail_id,
    p.product_name,
    od.quantity,
    od.unit_price,
    od.total_price,
    od.notes
FROM order_details od
JOIN products p ON od.product_id = p.product_id
WHERE od.order_id = ?;
```

### **3. Kiểm tra tồn kho nguyên liệu:**
```sql
SELECT 
    i.ingredient_name,
    i.current_stock,
    i.min_stock,
    i.unit,
    CASE 
        WHEN i.current_stock <= i.min_stock THEN 'Cần nhập'
        ELSE 'Đủ'
    END as status
FROM ingredients i
WHERE i.is_active = 1
ORDER BY i.current_stock ASC;
```

## 🎯 Lưu ý thiết kế

1. **Tính toàn vẹn dữ liệu:** Tất cả foreign keys được thiết lập để đảm bảo tính nhất quán
2. **Timestamps:** Mọi bảng đều có created_at và updated_at để theo dõi thời gian
3. **Soft Delete:** Sử dụng is_active thay vì xóa dữ liệu thực
4. **Indexing:** Các trường thường query được index để tối ưu hiệu suất
5. **Enum:** Sử dụng ENUM cho các trạng thái cố định
6. **Decimal:** Sử dụng DECIMAL cho các trường tiền tệ để tránh lỗi làm tròn 