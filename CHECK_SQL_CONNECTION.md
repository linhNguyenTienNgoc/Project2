# 🔍 Hướng Dẫn Kiểm Tra Kết Nối SQL Server

## 🎯 Mục tiêu:
Kiểm tra xem SQL Server đã kết nối với IntelliJ chưa và database có hoạt động không.

## 🔧 Cách 1: Kiểm tra trong IntelliJ IDEA

### **Bước 1: Mở Database Tool Window**
1. **Mở IntelliJ IDEA**
2. **Nhấn `Alt + 8`** hoặc **View → Tool Windows → Database**
3. **Hoặc click vào biểu tượng Database** ở bên phải màn hình

### **Bước 2: Thêm Data Source**
1. **Click dấu `+`** trong Database tool window
2. **Chọn `Data Source` → `Microsoft SQL Server`**

### **Bước 3: Cấu hình kết nối**
```
Host: M12345\M12345
Port: 1433 (mặc định)
Database: CoffeeShopManagement
Authentication: Windows Authentication
User: (để trống)
Password: (để trống)
```

### **Bước 4: Test Connection**
1. **Click `Test Connection`**
2. **Nếu thành công:** Hiển thị "Connection successful" ✅
3. **Nếu lỗi:** Hiển thị thông báo lỗi cụ thể ❌

## 🔧 Cách 2: Kiểm tra trong SQL Server Management Studio

### **Bước 1: Mở SQL Server Management Studio**
1. **Mở SQL Server Management Studio**
2. **Kết nối với server:** `M12345\M12345`
3. **Authentication:** Windows Authentication

### **Bước 2: Chạy script test**
1. **Mở file `test_sql_connection.sql`**
2. **Chạy toàn bộ script** (F5)
3. **Kiểm tra kết quả**

## 🔧 Cách 3: Kiểm tra từ ứng dụng Spring Boot

### **Bước 1: Chạy ứng dụng**
```bash
mvnw.cmd spring-boot:run
```

### **Bước 2: Test API endpoints**
```
http://localhost:8080/test/database
http://localhost:8080/debug/employees
```

## 📋 Checklist kiểm tra:

### **✅ Kết nối cơ bản:**
- [ ] SQL Server đang chạy
- [ ] Server name đúng: `M12345\M12345`
- [ ] Windows Authentication hoạt động
- [ ] Port 1433 mở

### **✅ Database:**
- [ ] Database `CoffeeShopManagement` tồn tại
- [ ] Có quyền truy cập database
- [ ] Bảng `Employee` tồn tại
- [ ] Có dữ liệu trong bảng `Employee`

### **✅ Tài khoản:**
- [ ] Tài khoản `admin` tồn tại
- [ ] Password: `admin123`
- [ ] Role: `ADMIN`
- [ ] Active: `1`

## 🚨 Các lỗi thường gặp:

### **1. Lỗi kết nối:**
```
Error: Login failed for user
```
**Giải pháp:** Kiểm tra Windows Authentication

### **2. Lỗi database không tồn tại:**
```
Database 'CoffeeShopManagement' does not exist
```
**Giải pháp:** Chạy `fix_login.sql`

### **3. Lỗi quyền truy cập:**
```
Access denied
```
**Giải pháp:** Chạy IntelliJ với quyền Administrator

### **4. Lỗi port:**
```
Port 1433 is not available
```
**Giải pháp:** Kiểm tra SQL Server Browser service

## 🔍 Script test chi tiết:

### **Chạy trong SQL Server Management Studio:**
```sql
-- Test kết nối cơ bản
SELECT @@VERSION as SQLServerVersion;

-- Test database
USE CoffeeShopManagement;
SELECT DB_NAME() as CurrentDatabase;

-- Test bảng Employee
SELECT COUNT(*) as EmployeeCount FROM Employee;

-- Test tài khoản admin
SELECT * FROM Employee WHERE username = 'admin';
```

## 🎯 Kết quả mong đợi:

### **Nếu kết nối thành công:**
- ✅ Hiển thị thông tin SQL Server version
- ✅ Database CoffeeShopManagement tồn tại
- ✅ Bảng Employee có dữ liệu
- ✅ Tài khoản admin có password = 'admin123'

### **Nếu có lỗi:**
- ❌ Hiển thị thông báo lỗi cụ thể
- ❌ Cần fix theo hướng dẫn

## 📞 Hỗ trợ:

Nếu gặp vấn đề, cung cấp:
1. Screenshot lỗi từ IntelliJ
2. Kết quả từ SQL Server Management Studio
3. Log lỗi từ ứng dụng Spring Boot
4. Thông tin server SQL Server 