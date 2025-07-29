# 🔍 Hướng Dẫn Debug Login - Coffee Shop Management

## 🚨 Vấn đề hiện tại:
- Không thể đăng nhập với tài khoản admin/admin123
- Hiển thị lỗi "Tên đăng nhập hoặc mật khẩu không đúng!"

## 🔧 Các bước debug:

### 1. **Kiểm tra Database**
```sql
-- Chạy script này trong SQL Server Management Studio
USE CoffeeShopManagement;

-- Kiểm tra tài khoản admin
SELECT * FROM Employee WHERE username = 'admin';

-- Kiểm tra tất cả employees
SELECT id, username, password, role, active FROM Employee;
```

### 2. **Setup Database mới (nếu cần)**
```sql
-- Chạy file setup_database.sql để tạo database và dữ liệu mẫu
```

### 3. **Test API Debug**
Sau khi chạy ứng dụng, test các endpoint sau:

#### a) Kiểm tra tất cả employees:
```
http://localhost:8080/debug/employees
```

#### b) Kiểm tra user cụ thể:
```
http://localhost:8080/debug/user?username=admin
```

#### c) Test login:
```
http://localhost:8080/debug/test-login?username=admin&password=admin123
```

### 4. **Restart và Test tự động**
```bash
# Chạy script restart_and_test.bat
```

## 🎯 Tài khoản test:

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| staff1 | staff123 | STAFF |
| manager | manager123 | ADMIN |

## 🔍 Các nguyên nhân có thể:

### 1. **Database chưa được tạo**
- Chạy `setup_database.sql`
- Kiểm tra kết nối database trong `application.properties`

### 2. **Password encoding**
- Spring Security đã được cấu hình với `NoOpPasswordEncoder`
- CustomUserDetailsService đã thêm prefix `{noop}`

### 3. **Entity mapping**
- Employee entity đã có đầy đủ fields
- Repository có method `findByUsername`

### 4. **Spring Security config**
- SecurityConfig đã cấu hình đúng
- Form login được enable

## 🚀 Các bước thực hiện:

### Bước 1: Setup Database
1. Mở SQL Server Management Studio
2. Chạy file `setup_database.sql`
3. Kiểm tra dữ liệu đã được tạo

### Bước 2: Restart Application
1. Dừng ứng dụng hiện tại (Ctrl+C)
2. Chạy `mvnw.cmd spring-boot:run`
3. Đợi ứng dụng khởi động hoàn toàn

### Bước 3: Test Debug APIs
1. Mở browser, truy cập `http://localhost:8080/debug/employees`
2. Kiểm tra kết quả trả về
3. Test các endpoint debug khác

### Bước 4: Test Login
1. Truy cập `http://localhost:8080`
2. Đăng nhập với admin/admin123
3. Kiểm tra kết quả

## 📋 Checklist:

- [ ] Database CoffeeShopManagement tồn tại
- [ ] Bảng Employee có dữ liệu
- [ ] Tài khoản admin có password = 'admin123'
- [ ] Ứng dụng khởi động thành công
- [ ] Debug API trả về dữ liệu
- [ ] Login form hiển thị đúng
- [ ] Đăng nhập thành công

## 🆘 Nếu vẫn lỗi:

1. **Kiểm tra logs** trong console khi khởi động ứng dụng
2. **Test database connection** bằng script `test_db_connection.sql`
3. **Kiểm tra port 8080** có bị chiếm không
4. **Restart hoàn toàn** máy tính nếu cần

## 📞 Thông tin liên hệ:
Nếu vẫn gặp vấn đề, hãy cung cấp:
- Log lỗi từ console
- Kết quả từ debug APIs
- Screenshot lỗi 