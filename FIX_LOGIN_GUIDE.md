# 🔧 Hướng Dẫn Fix Login - Coffee Shop Management

## 🚨 Vấn đề hiện tại:
- Không thể đăng nhập với tài khoản admin/admin123
- Hiển thị lỗi "Tên đăng nhập hoặc mật khẩu không đúng!"

## 🔧 Giải pháp hoàn chỉnh:

### **Bước 1: Fix Database**
1. **Mở SQL Server Management Studio**
2. **Kết nối** với server `M12345\M12345`
3. **Mở file `fix_login.sql`** trong project
4. **Chạy toàn bộ script** (F5 hoặc Execute)
5. **Đợi hoàn thành** và kiểm tra kết quả

### **Bước 2: Restart Application**
1. **Dừng ứng dụng** hiện tại (Ctrl+C)
2. **Chạy script** `fix_login_complete.bat`
3. **Hoặc chạy thủ công:**
   ```bash
   mvnw.cmd spring-boot:run
   ```

### **Bước 3: Test Login**
1. **Mở browser:** `http://localhost:8080`
2. **Đăng nhập với:**
   - Username: `admin`
   - Password: `admin123`

## 🎯 Tài khoản test:

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| staff1 | staff123 | STAFF |
| manager | manager123 | ADMIN |

## 🔍 Test Debug APIs:

### **Kiểm tra database:**
```
http://localhost:8080/test/database
```

### **Kiểm tra employees:**
```
http://localhost:8080/debug/employees
```

### **Kiểm tra admin user:**
```
http://localhost:8080/debug/user?username=admin
```

### **Test login:**
```
http://localhost:8080/debug/test-login?username=admin&password=admin123
```

## 🚀 Script tự động:

### **Chạy script fix hoàn chỉnh:**
```bash
fix_login_complete.bat
```

### **Chạy script test database:**
```bash
test_database.bat
```

## 🔧 Nếu vẫn lỗi:

### **1. Kiểm tra database:**
```sql
USE CoffeeShopManagement;
SELECT * FROM Employee WHERE username = 'admin';
```

### **2. Kiểm tra kết nối:**
- SQL Server đang chạy
- Server name: `M12345\M12345`
- Database: `CoffeeShopManagement`

### **3. Kiểm tra application.properties:**
```properties
spring.datasource.url=jdbc:sqlserver://M12345\M12345;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true
```

### **4. Restart hoàn toàn:**
1. Tắt ứng dụng
2. Tắt SQL Server Management Studio
3. Restart máy tính
4. Chạy lại từ đầu

## 📋 Checklist:

- [ ] Database CoffeeShopManagement tồn tại
- [ ] Bảng Employee có dữ liệu
- [ ] Tài khoản admin có password = 'admin123'
- [ ] Ứng dụng khởi động thành công
- [ ] Debug API trả về dữ liệu
- [ ] Login form hiển thị đúng
- [ ] Đăng nhập thành công

## 🆘 Troubleshooting:

### **Lỗi kết nối database:**
- Kiểm tra SQL Server đang chạy
- Kiểm tra server name đúng
- Kiểm tra Windows Authentication

### **Lỗi port 8080:**
- Tắt ứng dụng khác đang dùng port 8080
- Hoặc thay đổi port trong application.properties

### **Lỗi Maven:**
- Chạy `mvn clean install`
- Hoặc dùng IntelliJ IDEA

## 📞 Hỗ trợ:

Nếu vẫn gặp vấn đề, cung cấp:
1. Log lỗi từ console
2. Kết quả từ debug APIs
3. Screenshot lỗi
4. Kết quả từ SQL Server Management Studio

## 🎉 Kết quả mong đợi:

Sau khi fix thành công:
- ✅ Đăng nhập được với admin/admin123
- ✅ Truy cập được dashboard
- ✅ Tất cả tính năng hoạt động bình thường
- ✅ Có thể tạo hóa đơn, quản lý menu, bàn, nhân viên 