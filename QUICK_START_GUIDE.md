# 🚀 Quick Start - Coffee Shop Management (No Login)

## 🎯 Đã bỏ trang login - Truy cập trực tiếp!

### **✅ Các thay đổi đã thực hiện:**
1. **Bỏ Spring Security authentication**
2. **Redirect thẳng vào dashboard**
3. **Tạo employee mặc định (admin)**
4. **Tất cả tính năng có thể truy cập trực tiếp**

## 🚀 Cách chạy:

### **Bước 1: Start ứng dụng**
```bash
# Chạy script tự động
start_without_login.bat

# Hoặc chạy thủ công
mvnw.cmd spring-boot:run
```

### **Bước 2: Truy cập ứng dụng**
Mở browser và truy cập các URL sau:

| Trang | URL | Mô tả |
|-------|-----|-------|
| **Dashboard** | `http://localhost:8080/dashboard` | Trang chính với thống kê |
| **Menu** | `http://localhost:8080/menu` | Quản lý menu |
| **Tables** | `http://localhost:8080/tables` | Quản lý bàn |
| **Invoices** | `http://localhost:8080/invoices` | Quản lý hóa đơn |
| **Employees** | `http://localhost:8080/employees` | Quản lý nhân viên |
| **Reports** | `http://localhost:8080/reports` | Báo cáo |

## 🎯 Tính năng có sẵn:

### **✅ Dashboard:**
- Thống kê tổng quan
- Biểu đồ doanh thu
- Số liệu bàn, menu, hóa đơn

### **✅ Menu Management:**
- Xem danh sách menu
- Thêm/sửa/xóa món ăn
- Tìm kiếm menu

### **✅ Table Management:**
- Xem trạng thái bàn
- Quản lý bàn
- Thống kê bàn

### **✅ Invoice Management:**
- Tạo hóa đơn mới
- Xem danh sách hóa đơn
- Quản lý thanh toán

### **✅ Employee Management:**
- Quản lý nhân viên
- Thêm/sửa/xóa nhân viên
- Phân quyền

### **✅ Reports:**
- Báo cáo doanh thu
- Thống kê bán hàng
- Biểu đồ Chart.js

## 🔧 API Endpoints:

### **REST API (cho mobile app):**
```
GET /api/employees
GET /api/menu
GET /api/tables
GET /api/invoices
POST /api/invoices
```

### **Debug APIs:**
```
GET /debug/employees
GET /debug/user?username=admin
GET /test/database
```

## 🎉 Kết quả:

- ✅ **Không cần đăng nhập**
- ✅ **Truy cập trực tiếp tất cả tính năng**
- ✅ **Employee mặc định: admin/ADMIN**
- ✅ **Tất cả quyền admin**

## 📱 Demo cho giảng viên:

1. **Mở Dashboard:** Hiển thị thống kê tổng quan
2. **Quản lý Menu:** Thêm/sửa món ăn
3. **Quản lý Bàn:** Xem trạng thái bàn
4. **Tạo Hóa đơn:** Demo tạo hóa đơn mới
5. **Báo cáo:** Hiển thị biểu đồ doanh thu

## 🚨 Lưu ý:

- **Không cần database** để demo cơ bản
- **Tất cả tính năng hoạt động**
- **UI đẹp và responsive**
- **Sẵn sàng cho demo**

## 🎯 Cho dự án tốt nghiệp:

**Đã đủ để nộp!** Chỉ cần:
1. Chạy ứng dụng
2. Demo các tính năng
3. Giải thích code structure
4. Trả lời câu hỏi giảng viên 