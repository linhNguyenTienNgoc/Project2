# Changelog

Tất cả các thay đổi quan trọng trong project này sẽ được ghi lại trong file này.

Format dựa trên [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
và project này tuân thủ [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Cập nhật documentation cho phù hợp với cấu trúc hiện tại
- Cải thiện hướng dẫn setup và chạy ứng dụng
- Cập nhật project structure documentation

### Changed
- Cập nhật README.md với thông tin chính xác về Java 21 và JavaFX 24
- Cập nhật RUNNING_GUIDE.md với hướng dẫn setup database mới
- Cập nhật project_structure.md phản ánh đúng cấu trúc thực tế

### Fixed
- Sửa thông tin password mặc định từ "password" thành "123456"
- Cập nhật đường dẫn database config files

## [2.0.0] - 2025-01-31

### Added
- **JavaFX Application hoàn chỉnh**
  - 77 Java files với kiến trúc MVC
  - 12 CSS themes cho giao diện
  - 45 product images và UI assets
  - Admin dashboard với 8 views

- **Database Schema hoàn chỉnh**
  - 12 bảng chính cho quản lý quán cà phê
  - Sample data phong phú (7 users, 50+ products, 10 orders)
  - ERD và documentation chi tiết

- **Cấu hình Database**
  - File cấu hình kết nối
  - Setup scripts cho Windows và Linux/Mac
  - Database structure và sample data riêng biệt

- **Tài liệu Project**
  - README.md chi tiết với cấu trúc thực tế
  - RUNNING_GUIDE.md với hướng dẫn chi tiết
  - project_structure.md phản ánh đúng cấu trúc

### Features
- Quản lý người dùng và phân quyền
- Quản lý menu và sản phẩm
- Quản lý khu vực và bàn
- Quản lý đơn hàng và thanh toán
- Quản lý khách hàng
- Quản lý kho và nguyên liệu
- Báo cáo và thống kê
- Chấm công nhân viên

### Technical Details
- **Backend:** Java 21, JavaFX 24.0.2
- **Database:** MySQL 8.0
- **Architecture:** Layered Architecture với MVC Pattern
- **Design Pattern:** MVC, DAO, Service Layer
- **Build Tool:** Maven với JavaFX dependencies
- **Documentation:** Markdown, ERD, Developer guides

### Database Tables
1. `users` - Thông tin người dùng (Admin, Staff roles)
2. `categories` - Danh mục sản phẩm
3. `products` - Sản phẩm
4. `areas` - Khu vực
5. `tables` - Bàn
6. `customers` - Khách hàng
7. `orders` - Đơn hàng
8. `order_details` - Chi tiết đơn hàng
9. `promotions` - Khuyến mãi
10. `order_promotions` - Áp dụng khuyến mãi
11. `attendance` - Chấm công
12. `system_settings` - Cài đặt hệ thống

### Sample Data
- **Users:** 7 tài khoản mẫu với các vai trò khác nhau
- **Products:** 50+ sản phẩm mẫu (cà phê, trà, bánh, món ăn)
- **Areas & Tables:** 5 khu vực với 25+ bàn
- **Categories:** 7 danh mục sản phẩm
- **Orders:** 10 đơn hàng mẫu với các trạng thái khác nhau
- **Promotions:** 7 chương trình khuyến mãi

### Security
- Mật khẩu được mã hóa bằng BCrypt
- Phân quyền theo vai trò
- Session management
- Login attempt limiting

### Performance
- Indexes trên các trường thường query
- Connection pooling
- Optimized queries
- Caching support

## [1.0.0] - 2025-01-30

### Added
- Database schema cơ bản
- ERD diagram
- Initial documentation

### Changed
- Cải thiện database design
- Thêm foreign key constraints

### Fixed
- Sửa lỗi trong database schema

## [0.9.0] - 2025-01-29

### Added
- Project initialization
- Basic folder structure
- Database planning

### Changed
- Cập nhật project requirements
- Refine business logic

### Fixed
- Không có

## [0.8.0] - 2025-01-28

### Added
- Project concept
- Requirements gathering
- Team formation

### Changed
- Không có

### Fixed
- Không có

---

## Types of Changes

- **Added** - Tính năng mới
- **Changed** - Thay đổi trong tính năng hiện có
- **Deprecated** - Tính năng sẽ bị loại bỏ
- **Removed** - Tính năng đã bị loại bỏ
- **Fixed** - Sửa lỗi
- **Security** - Cải thiện bảo mật

## Version Format

- **Major.Minor.Patch**
- **Major:** Thay đổi lớn, có thể không tương thích ngược
- **Minor:** Thêm tính năng mới, tương thích ngược
- **Patch:** Sửa lỗi, tương thích ngược

## Release Schedule

- **Alpha:** Tính năng cơ bản, chưa hoàn thiện
- **Beta:** Tính năng đầy đủ, cần test
- **RC (Release Candidate):** Sẵn sàng release
- **Stable:** Phiên bản ổn định

## Support

- **Current Version:** 2.0.0
- **Supported Versions:** 2.0.x
- **End of Life:** Chưa có

## Migration Guide

### From 1.0.0 to 2.0.0
- Backup database hiện tại
- Import schema mới từ `cafe_database_structure.sql`
- Import sample data từ `cafe_sample_data.sql`
- Cập nhật application configuration
- Test tất cả tính năng

### From 0.9.0 to 1.0.0
- Không có thay đổi breaking
- Chỉ cần import schema mới

---

**Lưu ý:** Changelog này được cập nhật theo từng phiên bản release. 
Vui lòng tham khảo commit history để biết chi tiết các thay đổi nhỏ. 