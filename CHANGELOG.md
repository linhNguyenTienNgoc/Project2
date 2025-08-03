# Changelog

Tất cả các thay đổi quan trọng trong project này sẽ được ghi lại trong file này.

Format dựa trên [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
và project này tuân thủ [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Cấu trúc project JavaFX hoàn chỉnh
- Maven configuration với dependencies
- Build scripts cho Windows và Linux/Mac
- Application configuration file
- Project documentation

### Changed
- Cập nhật cấu trúc thư mục theo chuẩn Maven
- Tối ưu hóa database schema

### Fixed
- Không có

## [1.0.0] - 2025-01-31

### Added
- **Database Schema hoàn chỉnh**
  - 12 bảng chính cho quản lý quán cà phê
  - Dữ liệu mẫu đầy đủ
  - ERD và documentation chi tiết

- **Cấu hình Database**
  - File cấu hình kết nối
  - Hướng dẫn setup và sử dụng
  - Backup và restore scripts

- **Tài liệu Project**
  - README.md chi tiết
  - Hướng dẫn cài đặt
  - Mô tả tính năng

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
- **Database:** MySQL 8.0
- **Architecture:** Layered Architecture
- **Design Pattern:** MVC, DAO, Service Layer
- **Documentation:** Markdown, ERD

### Database Tables
1. `roles` - Vai trò người dùng
2. `users` - Thông tin người dùng
3. `categories` - Danh mục sản phẩm
4. `products` - Sản phẩm
5. `areas` - Khu vực
6. `tables` - Bàn
7. `customers` - Khách hàng
8. `orders` - Đơn hàng
9. `order_details` - Chi tiết đơn hàng
10. `promotions` - Khuyến mãi
11. `ingredients` - Nguyên liệu
12. `attendance` - Chấm công

### Sample Data
- **Users:** 5 tài khoản mẫu với các vai trò khác nhau
- **Products:** 20+ sản phẩm mẫu (cà phê, trà, bánh, món ăn)
- **Areas & Tables:** 4 khu vực với 6 bàn
- **Categories:** 5 danh mục sản phẩm

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

## [0.9.0] - 2025-01-30

### Added
- Database schema cơ bản
- ERD diagram
- Initial documentation

### Changed
- Cải thiện database design
- Thêm foreign key constraints

### Fixed
- Sửa lỗi trong database schema

## [0.8.0] - 2025-01-29

### Added
- Project initialization
- Basic folder structure
- Database planning

### Changed
- Cập nhật project requirements
- Refine business logic

### Fixed
- Không có

## [0.7.0] - 2025-01-28

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

- **Current Version:** 1.0.0
- **Supported Versions:** 1.0.x
- **End of Life:** Chưa có

## Migration Guide

### From 0.9.0 to 1.0.0
- Backup database hiện tại
- Import schema mới
- Cập nhật application configuration
- Test tất cả tính năng

### From 0.8.0 to 0.9.0
- Không có thay đổi breaking
- Chỉ cần import schema mới

---

**Lưu ý:** Changelog này được cập nhật theo từng phiên bản release. 
Vui lòng tham khảo commit history để biết chi tiết các thay đổi nhỏ. 