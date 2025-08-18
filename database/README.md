# 🎯 Complete Café Management Database

Hệ thống database hoàn chỉnh cho ứng dụng quản lý quán café với sample data đầy đủ.

## 📋 **Tổng quan**

File `complete_cafe_management.sql` là phiên bản gộp và nâng cấp từ tất cả các file SQL trước đó, bao gồm:
- ✅ Schema cơ bản từ `cafe_management.sql`
- ✅ Promotion system từ `promotions_migration.sql`
- ✅ Sample data phong phú cho tất cả bảng
- ✅ Indexes và optimizations

## 🗄️ **Cấu trúc Database**

### **Core Tables**
- `users` - Người dùng và phân quyền (7 accounts)
- `categories` - Danh mục sản phẩm (7 categories)
- `products` - Sản phẩm (50+ items)
- `areas` - Khu vực (5 areas)
- `tables` - Bàn (25+ tables)
- `customers` - Khách hàng (8 customers)

### **Orders System**
- `orders` - Đơn hàng (10 sample orders)
- `order_details` - Chi tiết đơn hàng
- `promotions` - Khuyến mãi (7 promotions)
- `order_promotions` - Áp dụng khuyến mãi

### **Additional Systems**
- `attendance` - Chấm công
- `system_settings` - Cài đặt hệ thống

## 🚀 **Cách Setup**

### **Windows:**
```bash
cd database
setup_database.bat
```

### **Linux/Mac:**
```bash
cd database
chmod +x setup_database.sh
./setup_database.sh
```

### **Manual Setup:**
```bash
mysql -u root -p < complete_cafe_management.sql
```

## 📊 **Sample Data Chi tiết**

### **👥 User Accounts**
| Username | Password | Role | Mô tả |
|----------|----------|------|-------|
| admin | 123456 | Admin | Quản trị viên |
| manager | 123456 | Manager | Quản lý |
| cashier1 | 123456 | Cashier | Thu ngân 1 |
| waiter1 | 123456 | Waiter | Phục vụ 1 |
| barista1 | 123456 | Barista | Pha chế |
| waiter2 | 123456 | Waiter | Phục vụ 2 |
| cashier2 | 123456 | Cashier | Thu ngân 2 |

### **🍰 Product Categories**
1. **Cà phê** - 10 sản phẩm (25k-55k)
2. **Trà & Trà sữa** - 10 sản phẩm (20k-42k)
3. **Nước ép & Sinh tố** - 10 sản phẩm (32k-52k)
4. **Bánh ngọt** - 10 sản phẩm (20k-50k)
5. **Đồ ăn nhẹ** - 10 sản phẩm (18k-35k)
6. **Món chính** - 8 sản phẩm (38k-65k)
7. **Đồ uống đá xay** - 5 sản phẩm (48k-60k)

### **🏠 Areas & Tables**
- **Tầng trệt**: 8 bàn (2-6 chỗ)
- **Tầng 2**: 7 bàn (2-8 chỗ)
- **Sân thượng**: 5 bàn (2-8 chỗ)
- **VIP**: 3 phòng riêng (6-10 chỗ)
- **Quầy bar**: 4 ghế bar

### **👤 Sample Customers**
8 khách hàng với loyalty points và history:
- Nguyễn Văn An: 150 điểm, đã chi 850k
- Trần Thị Bình: 200 điểm, đã chi 1.2M
- Phạm Thị Dung: 300 điểm, đã chi 1.85M (VIP)

### **📝 Sample Orders**
10 đơn hàng với các trạng thái khác nhau:
- **Completed**: 4 đơn đã hoàn thành
- **Served**: 2 đơn đã phục vụ
- **Ready**: 1 đơn sẵn sàng
- **Preparing**: 1 đơn đang pha chế
- **Pending**: 2 đơn đang chờ

### **🎟️ Active Promotions**
1. **Giảm 10%** - Đơn từ 100k (max 50k)
2. **Giảm 15% VIP** - Đơn từ 50k (max 100k)
3. **Giảm 5%** - Mọi đơn hàng (max 20k, limit 1000)
4. **Giảm 20k** - Đơn từ 150k (limit 500)
5. **Giảm 50k** - Đơn từ 300k (limit 100)
6. **Cuối tuần 12%** - Đơn từ 80k (max 60k)
7. **Happy Hour 25k** - Đơn từ 200k (limit 200)

## 🎨 **Features Nổi bật**

### **Enhanced Payment System**
- ✅ 6 payment methods: Cash, Card, MoMo, VNPay, ZaloPay, Bank Transfer
- ✅ QR code generation support
- ✅ Auto-fill cash amounts
- ✅ Promotion system integration

### **Professional Order Management**
- ✅ Order status tracking (7 states)
- ✅ Payment status tracking
- ✅ Table status management
- ✅ Order details with notes

### **Smart Promotion System**
- ✅ Percentage và fixed amount discounts
- ✅ Min order amount validation
- ✅ Usage limits và tracking
- ✅ Time-based promotions
- ✅ Analytics view

### **Comprehensive Analytics**
- ✅ Customer loyalty tracking
- ✅ Sales statistics
- ✅ Promotion performance
- ✅ Table utilization
- ✅ Staff activity tracking

## 🔍 **Verification Queries**

Sau khi setup, bạn có thể chạy các query sau để kiểm tra:

```sql
-- Xem tổng quan tables
SELECT table_name, table_rows 
FROM information_schema.tables 
WHERE table_schema = 'cafe_management';

-- Xem sản phẩm theo category
SELECT c.category_name, COUNT(p.product_id) as product_count
FROM categories c 
LEFT JOIN products p ON c.category_id = p.category_id 
GROUP BY c.category_id;

-- Xem orders summary
SELECT 
    order_status,
    payment_status,
    COUNT(*) as count,
    SUM(final_amount) as total_amount
FROM orders 
GROUP BY order_status, payment_status;

-- Xem promotion performance
SELECT * FROM promotion_stats;
```

## 🛠️ **Technical Notes**

### **Indexes Created**
- Performance indexes cho orders, products, customers
- Composite indexes cho common queries
- Foreign key indexes

### **Views Created**
- `promotion_stats` - Thống kê khuyến mãi realtime

### **Data Integrity**
- Foreign key constraints
- ENUM validations
- Proper timestamps
- Logical relationships

## 📱 **Integration với JavaFX App**

Database này được thiết kế để work seamlessly với:
- ✅ Enhanced PaymentController
- ✅ PromotionService  
- ✅ QRCodeService
- ✅ All existing DAOs
- ✅ Order management system

## 🎉 **Ready to Use!**

Database setup xong là có thể chạy JavaFX application ngay:

```bash
mvn clean install
mvn javafx:run
```

Hoặc:
```bash
./run-app.bat
```

**Happy coding! ☕**