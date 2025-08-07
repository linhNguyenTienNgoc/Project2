# Changelog

Tất cả các thay đổi quan trọng trong project này sẽ được ghi lại trong file này.

Format dựa trên [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
và project này tuân thủ [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.1] - 2025-01-31

### Fixed
- Fix MySQL CHECK constraint error: Loại bỏ `CURDATE()` function khỏi CHECK constraints và thay thế bằng triggers validation.
- Add promotion date validation triggers: `tr_promotions_validate_date` và `tr_promotions_validate_date_update` để validate ngày kết thúc khuyến mãi.
- Fix data.sql field mismatch: Cập nhật INSERT statements để bao gồm các trường mới trong schema v2.0 (table_number, sku, customer_code, promotion_code, discount_percent, stock_quantity, min_stock_level).
- Fix foreign key constraint error in data.sql: Thay đổi thứ tự DELETE statements để xóa users trước roles, tránh lỗi foreign key constraint khi xóa roles.
- Fix SQL syntax error in data.sql: Xóa dòng INSERT INTO users bị lặp lại gây lỗi syntax.
- Fix safe update mode error in data.sql: Thay đổi DELETE statement sử dụng JOIN thay vì subquery để tránh lỗi Error Code: 1175.
- Complete rewrite of data.sql: Viết lại hoàn toàn file data.sql dựa trên cấu trúc thực tế của cafe_management.sql v2.0.1, bao gồm tất cả các trường mới và constraints.
- Fix safe update mode in data.sql: Thêm SET SQL_SAFE_UPDATES = 0/1 để tắt/bật safe update mode, cho phép DELETE với JOIN hoạt động đúng.
- Add complete data cleanup in data.sql: Thêm phần xóa toàn bộ dữ liệu theo thứ tự foreign key trước khi insert, tránh lỗi constraint và đảm bảo database sạch.
- Fix MySQL trigger limitation error in data.sql: Sử dụng DROP TRIGGER và CREATE TRIGGER thay vì DISABLE/ENABLE TRIGGERS (cú pháp PostgreSQL) để tránh lỗi Error Code: 1442 khi insert order_details (trigger cố gắng cập nhật bảng products trong khi đang insert). Sửa tên trigger từ tr_order_details_after_insert thành tr_order_details_insert để khớp với cafe_management.sql.

## [2.0.0] - 2025-01-31

### Added
- **Database Optimization**
  - UTF8MB4 charset cho hỗ trợ tiếng Việt và emoji
  - 45+ indexes tối ưu hiệu suất truy vấn
  - JSON fields cho lưu trữ dữ liệu linh hoạt
  - Triggers tự động hóa business logic
  - Stored procedures xử lý logic phức tạp
  - Views cho báo cáo nhanh và tối ưu

- **Advanced Features**
  - Quản lý tồn kho với min stock level
  - Auto-update stock khi tạo đơn hàng
  - Hệ thống khuyến mãi nâng cao với usage limits
  - Audit logging - theo dõi mọi thay đổi
  - JSON permissions cho quản lý quyền linh hoạt
  - Fulltext search cho sản phẩm và khách hàng

- **Security Enhancements**
  - Password hashing với BCrypt
  - Email/Phone validation với regex patterns
  - Role-based access control
  - Session management cải tiến
  - IP address logging

- **Performance Improvements**
  - Composite indexes cho query phức tạp
  - Connection pooling optimization
  - Auto-increment optimization
  - Constraint validation tại database level
  - Partitioning ready cho dữ liệu lớn

### Changed
- **Database Schema**
  - Tối ưu cấu trúc bảng với constraints mạnh mẽ
  - Cập nhật foreign keys với CASCADE/RESTRICT
  - Thêm unique constraints cho data integrity
  - Cải thiện enum values cho trạng thái
  - Thêm check constraints cho validation

- **Role System**
  - Giảm từ 5 roles xuống 3 roles chính: Admin, Waiter, Barista
  - Thêm JSON permissions field
  - Cải thiện role descriptions

- **Product Management**
  - Thêm SKU (Stock Keeping Unit)
  - Thêm stock quantity và min stock level
  - Cải thiện product search với fulltext index

- **Order System**
  - Thêm tax calculation tự động
  - Cải thiện payment methods (thêm bank_transfer)
  - Thêm discount_percent trong order_details
  - Auto-update table status khi tạo đơn

- **Customer Management**
  - Thêm customer_code tự động
  - Cải thiện loyalty points system
  - Thêm last_visit tracking
  - Fulltext search cho customer

### Technical Improvements
- **Database Objects**
  - 3 Views: v_order_summary, v_product_inventory, v_daily_revenue
  - 5 Triggers: tr_order_promotions_insert, tr_orders_update_customer, tr_order_details_insert, tr_promotions_validate_date, tr_promotions_validate_date_update
  - 2 Stored Procedures: sp_create_order, sp_calculate_order_total
  - 2 Functions: fn_generate_customer_code, fn_check_promotion_valid

- **Index Strategy**
  - Composite indexes cho performance
  - Fulltext indexes cho search
  - Descending indexes cho sorting
  - Covering indexes cho common queries

- **Data Integrity**
  - Check constraints cho validation
  - Unique constraints cho business rules
  - Foreign key constraints với proper actions
  - Default values cho consistency

### Removed
- **Roles**: manager, cashier (consolidated into 3 main roles)
- **Sample data** from main schema file (moved to data.sql)
- **Redundant indexes** replaced with optimized ones

### Fixed
- **Database Issues**
  - Sửa lỗi charset encoding
  - Cải thiện foreign key relationships
  - Fix constraint violations
  - Optimize query performance
  - **Fix MySQL CHECK constraint error**: Loại bỏ `CURDATE()` function khỏi CHECK constraints và thay thế bằng triggers validation
  - **Add promotion date validation triggers**: `tr_promotions_validate_date` và `tr_promotions_validate_date_update` để validate ngày kết thúc khuyến mãi
  - **Fix data.sql field mismatch**: Cập nhật INSERT statements để bao gồm các trường mới trong schema v2.0 (table_number, sku, customer_code, promotion_code, discount_percent, stock_quantity, min_stock_level)

- **Data Consistency**
  - Ensure referential integrity
  - Fix duplicate data issues
  - Improve data validation

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

- **Current Version:** 2.0.0
- **Supported Versions:** 2.0.x, 1.0.x
- **End of Life:** Chưa có

## Migration Guide

### From 1.0.0 to 2.0.0
- **Backup database hiện tại**
- **Drop và recreate database** với schema mới
- **Import optimized schema**: `mysql -u root -p < database/cafe_management.sql`
- **Import sample data**: `mysql -u root -p cafe_management < database/data.sql`
- **Update application configuration** với new connection parameters
- **Test tất cả tính năng** đặc biệt là order creation và inventory
- **Verify triggers và procedures** hoạt động đúng

### Breaking Changes
- **Role system**: Giảm từ 5 roles xuống 3 roles
- **Database charset**: Chuyển sang UTF8MB4
- **New constraints**: Có thể gây lỗi với dữ liệu cũ
- **Table structure**: Một số bảng có thêm columns mới

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