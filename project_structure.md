# 🌳 Cấu trúc Thư mục Project - Cafe Management System (JavaFX) v2.0

## 📋 Tổng quan
Hệ thống quản lý quán cafe được phát triển với kiến trúc MVC tối ưu, database schema hiệu suất cao và các tính năng nâng cao.

## 🏗️ Cấu trúc Thư mục

```
Project2/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/
│   │   │   └── 📁 com/
│   │   │       └── 📁 cafe/
│   │   │           ├── 📁 CafeManagementApplication.java          # Main Application
│   │   │           ├── 📁 config/
│   │   │           │   ├── 📁 DatabaseConfig.java                # Cấu hình kết nối DB tối ưu
│   │   │           │   ├── 📁 AppConfig.java                     # Cấu hình ứng dụng
│   │   │           │   ├── 📁 SecurityConfig.java                # Cấu hình bảo mật
│   │   │           │   └── 📁 MySQLConnect.java                  # Connection pooling
│   │   │           ├── 📁 model/
│   │   │           │   ├── 📁 entity/                            # Các entity/POJO
│   │   │           │   │   ├── 📁 User.java                      # Người dùng với JSON permissions
│   │   │           │   │   ├── 📁 Role.java                      # Vai trò (Admin, Waiter, Barista)
│   │   │           │   │   ├── 📁 Product.java                   # Sản phẩm với SKU và inventory
│   │   │           │   │   ├── 📁 Category.java                  # Danh mục với sort_order
│   │   │           │   │   ├── 📁 Order.java                     # Đơn hàng với tax calculation
│   │   │           │   │   ├── 📁 OrderDetail.java               # Chi tiết đơn hàng
│   │   │           │   │   ├── 📁 Customer.java                  # Khách hàng với loyalty system
│   │   │           │   │   ├── 📁 TableCafe.java                 # Bàn với unique constraints
│   │   │           │   │   ├── 📁 Area.java                      # Khu vực với capacity
│   │   │           │   │   ├── 📁 Promotion.java                 # Khuyến mãi nâng cao
│   │   │           │   │   ├── 📁 OrderPromotion.java            # Áp dụng khuyến mãi
│   │   │           │   │   ├── 📁 Attendance.java                # Chấm công với overtime
│   │   │           │   │   ├── 📁 SystemSetting.java             # Cài đặt hệ thống
│   │   │           │   │   └── 📁 SystemLog.java                 # Audit logging
│   │   │           │   ├── 📁 dto/                               # Data Transfer Objects
│   │   │           │   │   ├── 📁 OrderDTO.java
│   │   │           │   │   ├── 📁 ProductDTO.java
│   │   │           │   │   ├── 📁 CustomerDTO.java
│   │   │           │   │   ├── 📁 RevenueDTO.java
│   │   │           │   │   ├── 📁 DashboardDTO.java
│   │   │           │   │   ├── 📁 InventoryDTO.java              # Tồn kho
│   │   │           │   │   └── 📁 AttendanceDTO.java             # Chấm công
│   │   │           │   └── 📁 enums/                             # Các enum
│   │   │           │       ├── 📁 OrderStatus.java
│   │   │           │       ├── 📁 PaymentStatus.java
│   │   │           │       ├── 📁 PaymentMethod.java
│   │   │           │       ├── 📁 TableStatus.java
│   │   │           │       └── 📁 UserRole.java
│   │   │           ├── 📁 dao/                                   # Data Access Objects
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 BaseDAO.java                   # Interface cơ bản
│   │   │           │   │   └── 📁 BaseDAOImpl.java               # Implementation cơ bản
│   │   │           │   ├── 📁 UserDAO.java
│   │   │           │   ├── 📁 UserDAOImpl.java
│   │   │           │   ├── 📁 ProductDAO.java
│   │   │           │   ├── 📁 ProductDAOImpl.java
│   │   │           │   ├── 📁 OrderDAO.java
│   │   │           │   ├── 📁 OrderDAOImpl.java
│   │   │           │   ├── 📁 CustomerDAO.java
│   │   │           │   ├── 📁 CustomerDAOImpl.java
│   │   │           │   ├── 📁 TableDAO.java
│   │   │           │   ├── 📁 TableDAOImpl.java
│   │   │           │   ├── 📁 CategoryDAO.java
│   │   │           │   ├── 📁 CategoryDAOImpl.java
│   │   │           │   ├── 📁 PromotionDAO.java
│   │   │           │   ├── 📁 PromotionDAOImpl.java
│   │   │           │   ├── 📁 AttendanceDAO.java
│   │   │           │   ├── 📁 AttendanceDAOImpl.java
│   │   │           │   ├── 📁 AreaDAO.java
│   │   │           │   └── 📁 AreaDAOImpl.java
│   │   │           ├── 📁 service/                               # Business Logic Layer
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 BaseService.java
│   │   │           │   │   └── 📁 BaseServiceImpl.java
│   │   │           │   ├── 📁 UserService.java
│   │   │           │   ├── 📁 UserServiceImpl.java
│   │   │           │   ├── 📁 ProductService.java
│   │   │           │   ├── 📁 ProductServiceImpl.java
│   │   │           │   ├── 📁 OrderService.java
│   │   │           │   ├── 📁 OrderServiceImpl.java
│   │   │           │   ├── 📁 CustomerService.java
│   │   │           │   ├── 📁 CustomerServiceImpl.java
│   │   │           │   ├── 📁 TableService.java
│   │   │           │   ├── 📁 TableServiceImpl.java
│   │   │           │   ├── 📁 CategoryService.java
│   │   │           │   ├── 📁 CategoryServiceImpl.java
│   │   │           │   ├── 📁 PromotionService.java
│   │   │           │   ├── 📁 PromotionServiceImpl.java
│   │   │           │   ├── 📁 AttendanceService.java
│   │   │           │   ├── 📁 AttendanceServiceImpl.java
│   │   │           │   ├── 📁 AuthService.java                   # Xác thực & phân quyền
│   │   │           │   ├── 📁 AuthServiceImpl.java
│   │   │           │   ├── 📁 ReportService.java                 # Báo cáo
│   │   │           │   ├── 📁 ReportServiceImpl.java
│   │   │           │   ├── 📁 NotificationService.java           # Thông báo
│   │   │           │   ├── 📁 NotificationServiceImpl.java
│   │   │           │   ├── 📁 InventoryService.java              # Quản lý tồn kho
│   │   │           │   ├── 📁 InventoryServiceImpl.java
│   │   │           │   ├── 📁 LoyaltyService.java                # Hệ thống tích điểm
│   │   │           │   └── 📁 LoyaltyServiceImpl.java
│   │   │           ├── 📁 controller/                            # Controllers (MVC)
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 BaseController.java
│   │   │           │   │   └── 📁 BaseControllerImpl.java
│   │   │           │   ├── 📁 auth/
│   │   │           │   │   ├── 📁 LoginController.java
│   │   │           │   │   └── 📁 RegisterController.java
│   │   │           │   ├── 📁 admin/
│   │   │           │   │   ├── 📁 AdminDashboardController.java
│   │   │           │   │   ├── 📁 UserManagementController.java
│   │   │           │   │   ├── 📁 ProductManagementController.java
│   │   │           │   │   ├── 📁 OrderManagementController.java
│   │   │           │   │   ├── 📁 CustomerManagementController.java
│   │   │           │   │   ├── 📁 TableManagementController.java
│   │   │           │   │   ├── 📁 PromotionManagementController.java
│   │   │           │   │   ├── 📁 InventoryController.java
│   │   │           │   │   ├── 📁 AttendanceController.java
│   │   │           │   │   ├── 📁 ReportController.java
│   │   │           │   │   └── 📁 SystemSettingsController.java
│   │   │           │   ├── 📁 waiter/
│   │   │           │   │   ├── 📁 WaiterDashboardController.java
│   │   │           │   │   ├── 📁 OrderController.java
│   │   │           │   │   ├── 📁 TableController.java
│   │   │           │   │   └── 📁 CustomerController.java
│   │   │           │   └── 📁 barista/
│   │   │           │       ├── 📁 BaristaDashboardController.java
│   │   │           │       ├── 📁 ProductController.java
│   │   │           │       └── 📁 InventoryController.java
│   │   │           ├── 📁 util/                                  # Utilities
│   │   │           │   ├── 📁 DatabaseTestUtil.java              # Test database connection
│   │   │           │   ├── 📁 SessionManager.java                # Quản lý session
│   │   │           │   ├── 📁 PasswordUtil.java                  # Mã hóa mật khẩu
│   │   │           │   ├── 📁 ValidationUtil.java                # Validation
│   │   │           │   ├── 📁 DateUtil.java                      # Xử lý ngày tháng
│   │   │           │   ├── 📁 CurrencyUtil.java                  # Format tiền tệ
│   │   │           │   ├── 📁 FileUtil.java                      # Xử lý file
│   │   │           │   ├── 📁 EmailUtil.java                     # Gửi email
│   │   │           │   ├── 📁 PDFUtil.java                       # Tạo PDF
│   │   │           │   ├── 📁 ExcelUtil.java                     # Export Excel
│   │   │           │   ├── 📁 QRCodeUtil.java                    # Tạo QR code
│   │   │           │   └── 📁 NotificationUtil.java              # Thông báo
│   │   │           └── 📁 exception/                             # Exception handling
│   │   │               ├── 📁 DatabaseConnectionException.java
│   │   │               ├── 📁 ValidationException.java
│   │   │               ├── 📁 AuthenticationException.java
│   │   │               ├── 📁 BusinessLogicException.java
│   │   │               └── 📁 SystemException.java
│   │   └── 📁 resources/
│   │       ├── 📁 fxml/                                          # FXML files
│   │       │   ├── 📁 auth/
│   │       │   │   ├── 📁 login.fxml
│   │       │   │   └── 📁 register.fxml
│   │       │   ├── 📁 admin/
│   │       │   │   ├── 📁 admin-dashboard.fxml
│   │       │   │   ├── 📁 user-management.fxml
│   │       │   │   ├── 📁 product-management.fxml
│   │       │   │   ├── 📁 order-management.fxml
│   │       │   │   ├── 📁 customer-management.fxml
│   │       │   │   ├── 📁 table-management.fxml
│   │       │   │   ├── 📁 promotion-management.fxml
│   │       │   │   ├── 📁 inventory.fxml
│   │       │   │   ├── 📁 attendance.fxml
│   │       │   │   ├── 📁 reports.fxml
│   │       │   │   └── 📁 system-settings.fxml
│   │       │   ├── 📁 waiter/
│   │       │   │   ├── 📁 waiter-dashboard.fxml
│   │       │   │   ├── 📁 order.fxml
│   │       │   │   ├── 📁 table.fxml
│   │       │   │   └── 📁 customer.fxml
│   │       │   └── 📁 barista/
│   │       │       ├── 📁 barista-dashboard.fxml
│   │       │       ├── 📁 product.fxml
│   │       │       └── 📁 inventory.fxml
│   │       ├── 📁 css/                                           # Stylesheets
│   │       │   ├── 📁 login.css
│   │       │   ├── 📁 dashboard.css
│   │       │   ├── 📁 components.css
│   │       │   ├── 📁 forms.css
│   │       │   ├── 📁 tables.css
│   │       │   ├── 📁 buttons.css
│   │       │   ├── 📁 modals.css
│   │       │   └── 📁 themes/
│   │       │       ├── 📁 light.css
│   │       │       ├── 📁 dark.css
│   │       │       └── 📁 custom.css
│   │       ├── 📁 images/                                        # Images & Icons
│   │       │   ├── 📁 logo/
│   │       │   │   ├── 📁 nopita-logo.png
│   │       │   │   └── 📁 favicon.ico
│   │       │   ├── 📁 icons/
│   │       │   │   ├── 📁 power-icon.png
│   │       │   │   ├── 📁 menu-icon.png
│   │       │   │   ├── 📁 user-icon.png
│   │       │   │   ├── 📁 order-icon.png
│   │       │   │   ├── 📁 product-icon.png
│   │       │   │   ├── 📁 customer-icon.png
│   │       │   │   ├── 📁 table-icon.png
│   │       │   │   ├── 📁 report-icon.png
│   │       │   │   └── 📁 settings-icon.png
│   │       │   ├── 📁 products/                                  # Product images
│   │       │   │   ├── 📁 coffee/
│   │       │   │   ├── 📁 tea/
│   │       │   │   ├── 📁 juice/
│   │       │   │   ├── 📁 cake/
│   │       │   │   └── 📁 snack/
│   │       │   └── 📁 backgrounds/                               # Background images
│   │       │       ├── 📁 login-bg.jpg
│   │       │       ├── 📁 dashboard-bg.jpg
│   │       │       └── 📁 pattern-bg.png
│   │       ├── 📁 properties/                                    # Properties files
│   │       │   ├── 📁 application.properties
│   │       │   ├── 📁 database.properties
│   │       │   ├── 📁 logback.xml
│   │       │   └── 📁 messages.properties
│   │       └── 📁 sql/                                           # SQL scripts
│   │           ├── 📁 schema/
│   │           │   ├── 📁 create_tables.sql
│   │           │   ├── 📁 create_indexes.sql
│   │           │   ├── 📁 create_views.sql
│   │           │   ├── 📁 create_triggers.sql
│   │           │   └── 📁 create_procedures.sql
│   │           ├── 📁 data/
│   │           │   ├── 📁 sample_data.sql
│   │           │   ├── 📁 test_data.sql
│   │           │   └── 📁 migration_data.sql
│   │           └── 📁 reports/
│   │               ├── 📁 revenue_report.sql
│   │               ├── 📁 inventory_report.sql
│   │               ├── 📁 customer_report.sql
│   │               └── 📁 attendance_report.sql
│   └── 📁 test/                                                  # Test files
│       ├── 📁 java/
│       │   └── 📁 com/
│       │       └── 📁 cafe/
│       │           ├── 📁 test/
│       │           │   ├── 📁 TestDatabaseConnection.java
│       │           │   ├── 📁 TestUserDAO.java
│       │           │   ├── 📁 TestProductDAO.java
│       │           │   ├── 📁 TestOrderDAO.java
│       │           │   ├── 📁 TestCustomerDAO.java
│       │           │   ├── 📁 TestTableDAO.java
│       │           │   ├── 📁 TestCategoryDAO.java
│       │           │   ├── 📁 TestPromotionDAO.java
│       │           │   ├── 📁 TestAttendanceDAO.java
│       │           │   ├── 📁 TestAreaDAO.java
│       │           │   ├── 📁 TestUserService.java
│       │           │   ├── 📁 TestProductService.java
│       │           │   ├── 📁 TestOrderService.java
│       │           │   ├── 📁 TestCustomerService.java
│       │           │   ├── 📁 TestTableService.java
│       │           │   ├── 📁 TestCategoryService.java
│       │           │   ├── 📁 TestPromotionService.java
│       │           │   ├── 📁 TestAttendanceService.java
│       │           │   ├── 📁 TestAreaService.java
│       │           │   ├── 📁 TestAuthService.java
│       │           │   ├── 📁 TestReportService.java
│       │           │   ├── 📁 TestNotificationService.java
│       │           │   ├── 📁 TestInventoryService.java
│       │           │   ├── 📁 TestLoyaltyService.java
│       │           │   ├── 📁 TestLoginController.java
│       │           │   ├── 📁 TestAdminDashboardController.java
│       │           │   ├── 📁 TestWaiterDashboardController.java
│       │           │   ├── 📁 TestBaristaDashboardController.java
│       │           │   ├── 📁 TestUserManagementController.java
│       │           │   ├── 📁 TestProductManagementController.java
│       │           │   ├── 📁 TestOrderManagementController.java
│       │           │   ├── 📁 TestCustomerManagementController.java
│       │           │   ├── 📁 TestTableManagementController.java
│       │           │   ├── 📁 TestPromotionManagementController.java
│       │           │   ├── 📁 TestInventoryController.java
│       │           │   ├── 📁 TestAttendanceController.java
│       │           │   ├── 📁 TestReportController.java
│       │           │   ├── 📁 TestSystemSettingsController.java
│       │           │   ├── 📁 TestOrderController.java
│       │           │   ├── 📁 TestTableController.java
│       │           │   ├── 📁 TestCustomerController.java
│       │           │   ├── 📁 TestProductController.java
│       │           │   ├── 📁 TestDatabaseTestUtil.java
│       │           │   ├── 📁 TestSessionManager.java
│       │           │   ├── 📁 TestPasswordUtil.java
│       │           │   ├── 📁 TestValidationUtil.java
│       │           │   ├── 📁 TestDateUtil.java
│       │           │   ├── 📁 TestCurrencyUtil.java
│       │           │   ├── 📁 TestFileUtil.java
│       │           │   ├── 📁 TestEmailUtil.java
│       │           │   ├── 📁 TestPDFUtil.java
│       │           │   ├── 📁 TestExcelUtil.java
│       │           │   ├── 📁 TestQRCodeUtil.java
│       │           │   ├── 📁 TestNotificationUtil.java
│       │           │   ├── 📁 TestDatabaseConnectionException.java
│       │           │   ├── 📁 TestValidationException.java
│       │           │   ├── 📁 TestAuthenticationException.java
│       │           │   ├── 📁 TestBusinessLogicException.java
│       │           │   └── 📁 TestSystemException.java
│       │           └── 📁 util/
│       │               └── 📁 TestConnection.java
│       └── 📁 resources/
│           ├── 📁 test-data/
│           │   ├── 📁 test_users.sql
│           │   ├── 📁 test_products.sql
│           │   ├── 📁 test_orders.sql
│           │   ├── 📁 test_customers.sql
│           │   ├── 📁 test_tables.sql
│           │   ├── 📁 test_categories.sql
│           │   ├── 📁 test_promotions.sql
│           │   ├── 📁 test_attendance.sql
│           │   └── 📁 test_areas.sql
│           └── 📁 test-config/
│               ├── 📁 test-database.properties
│               ├── 📁 test-application.properties
│               └── 📁 test-logback.xml
├── 📁 database/                                                  # Database files
│   ├── 📁 cafe_management.sql                                   # Optimized schema
│   ├── 📁 data.sql                                              # Sample data
│   ├── 📁 test_connection.sql                                   # Test connection
│   ├── 📁 update_admin_password.sql                             # Update admin password
│   ├── 📁 database_config.properties                            # Database configuration
│   ├── 📁 README.md                                             # Database documentation
│   ├── 📁 SETUP_GUIDE.md                                        # Setup guide
│   ├── 📁 ERD.md                                                # Entity Relationship Diagram
│   └── 📁 EER.mwb                                               # MySQL Workbench file
├── 📁 lib/                                                       # External libraries
│   └── 📁 javafx-sdk-24.0.2/                                    # JavaFX SDK
│       ├── 📁 lib/                                              # JavaFX libraries
│       ├── 📁 bin/                                              # JavaFX binaries
│       └── 📁 legal/                                            # Legal files
├── 📁 config/                                                    # Configuration files
│   ├── 📁 application.properties                                # Application configuration
│   ├── 📁 database.properties                                   # Database configuration
│   ├── 📁 logback.xml                                           # Logging configuration
│   └── 📁 messages.properties                                   # Internationalization
├── 📁 scripts/                                                   # Build & run scripts
│   ├── 📁 build.bat                                             # Windows build script
│   ├── 📁 build.sh                                              # Linux/Mac build script
│   ├── 📁 run.bat                                               # Windows run script
│   └── 📁 run.sh                                                # Linux/Mac run script
├── 📁 docs/                                                      # Documentation
│   ├── 📁 user-guide/                                           # User documentation
│   │   ├── 📁 user-manual.md
│   │   ├── 📁 quick-start.md
│   │   ├── 📁 troubleshooting.md
│   │   └── 📁 faq.md
│   ├── 📁 developer/                                            # Developer documentation
│   │   ├── 📁 architecture.md
│   │   ├── 📁 coding-standards.md
│   │   ├── 📁 database-design.md
│   │   ├── 📁 api-documentation.md
│   │   └── 📁 deployment.md
│   ├── 📁 api/                                                  # API documentation
│   │   ├── 📁 controllers.md
│   │   ├── 📁 services.md
│   │   ├── 📁 daos.md
│   │   └── 📁 models.md
│   └── 📁 assets/                                               # Documentation assets
│       ├── 📁 screenshots/                                      # Application screenshots
│       │   ├── 📁 dashboard.png
│       │   ├── 📁 order-management.png
│       │   ├── 📁 product-management.png
│       │   ├── 📁 customer-management.png
│       │   ├── 📁 table-management.png
│       │   ├── 📁 promotion-management.png
│       │   ├── 📁 inventory.png
│       │   ├── 📁 attendance.png
│       │   ├── 📁 reports.png
│       │   └── 📁 system-settings.png
│       ├── 📁 diagrams/                                         # System diagrams
│       │   ├── 📁 architecture-diagram.png
│       │   ├── 📁 database-diagram.png
│       │   ├── 📁 class-diagram.png
│       │   ├── 📁 sequence-diagram.png
│       │   └── 📁 use-case-diagram.png
│       └── 📁 videos/                                           # Demo videos
│           ├── 📁 installation.mp4
│           ├── 📁 user-guide.mp4
│           └── 📁 features-demo.mp4
├── 📁 target/                                                    # Build output
│   ├── 📁 classes/                                              # Compiled classes
│   ├── 📁 test-classes/                                         # Test classes
│   ├── 📁 generated-sources/                                    # Generated sources
│   ├── 📁 maven-archiver/                                       # Maven archive
│   ├── 📁 surefire-reports/                                     # Test reports
│   ├── 📁 jacoco/                                               # Coverage reports
│   └── 📁 cafe-management-2.0.0.jar                            # Final JAR file
├── 📁 .gitignore                                                # Git ignore file
├── 📁 pom.xml                                                   # Maven configuration
├── 📁 README.md                                                 # Project README
├── 📁 CHANGELOG.md                                              # Change log
├── 📁 RUNNING_GUIDE.md                                          # Running guide
├── 📁 project_structure.md                                      # This file
├── 📁 build-and-run.bat                                         # Windows build & run
├── 📁 run-app.bat                                               # Windows run app
└── 📁 run-simple.bat                                            # Windows simple run
```

## 🏗️ Kiến trúc Hệ thống

### **1. Layered Architecture**
```
┌─────────────────────────────────────────────────────────────┐
│                    Presentation Layer                       │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   Admin     │ │   Waiter    │ │   Barista   │           │
│  │ Controllers │ │ Controllers │ │ Controllers │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Business Logic Layer                     │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   User      │ │   Order     │ │   Product   │           │
│  │  Service    │ │  Service    │ │  Service    │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                        │
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │   User      │ │   Order     │ │   Product   │           │
│  │    DAO      │ │    DAO      │ │    DAO      │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                    Database Layer                           │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │              MySQL Database (v2.0)                      │ │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐       │ │
│  │  │  Users  │ │ Orders  │ │Products │ │Customers│       │ │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘       │ │
│  └─────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

### **2. Design Patterns**
- **MVC (Model-View-Controller)** - Separation of concerns
- **DAO (Data Access Object)** - Database abstraction
- **Service Layer** - Business logic encapsulation
- **Singleton** - Configuration management
- **Factory** - Object creation
- **Observer** - Event handling
- **Strategy** - Payment methods
- **Template Method** - Report generation

### **3. Database Optimization**
- **45+ Indexes** - Query performance
- **3 Views** - Fast reporting
- **3 Triggers** - Data consistency
- **2 Stored Procedures** - Complex operations
- **2 Functions** - Reusable logic
- **UTF8MB4** - Full Unicode support
- **JSON Fields** - Flexible data storage

## 🔧 Cấu hình & Deployment

### **Development Environment**
- **Java:** 17+
- **MySQL:** 8.0+
- **JavaFX:** 17+
- **Maven:** 3.6+
- **IDE:** IntelliJ IDEA / Eclipse / VS Code

### **Production Environment**
- **Java:** 17 LTS
- **MySQL:** 8.0+
- **OS:** Windows Server / Linux / macOS
- **Memory:** 8GB+ RAM
- **Storage:** 50GB+ SSD

### **Build & Deploy**
```bash
# Build project
mvn clean install

# Run application
mvn javafx:run

# Create executable JAR
mvn package

# Run JAR file
java -jar target/cafe-management-2.0.0.jar
```

## 📊 Tính năng Chính

### **1. Quản lý Người dùng**
- Đăng nhập/Đăng xuất với bảo mật
- Phân quyền theo vai trò (Admin, Waiter, Barista)
- Audit logging mọi thay đổi
- Session management

### **2. Quản lý Sản phẩm**
- CRUD operations với SKU
- Inventory management với stock tracking
- Category management với sort order
- Fulltext search capabilities

### **3. Quản lý Đơn hàng**
- Tạo đơn hàng với stored procedures
- Multiple payment methods
- Tax calculation tự động
- Promotion system nâng cao

### **4. Quản lý Khách hàng**
- Customer loyalty system
- Purchase history tracking
- VIP customer identification
- Fulltext search

### **5. Quản lý Bàn**
- Real-time table status
- Area management với capacity
- Unique constraints
- Auto-update status

### **6. Báo cáo & Analytics**
- Revenue reports với views
- Inventory reports
- Customer analytics
- Attendance tracking

## 🔒 Bảo mật

### **Authentication & Authorization**
- BCrypt password hashing
- Role-based access control
- Session management
- IP address logging

### **Data Protection**
- Input validation
- SQL injection prevention
- XSS protection
- CSRF protection

### **Audit Trail**
- System logs cho mọi thay đổi
- User action tracking
- Data modification history
- Security event logging

## 🚀 Performance

### **Database Optimization**
- Composite indexes
- Query optimization
- Connection pooling
- Caching strategies

### **Application Performance**
- Lazy loading
- Pagination
- Background processing
- Memory management

### **UI/UX Optimization**
- Responsive design
- Fast loading
- Smooth animations
- Accessibility features

## 📝 Ghi chú

- **Version:** 2.0.0 (Optimized)
- **Last Updated:** 2025-01-31
- **Database:** MySQL 8.0+ với UTF8MB4
- **JavaFX:** 17+ với modern UI
- **Architecture:** Layered MVC với DAO pattern
- **Security:** BCrypt + Role-based access
- **Performance:** 45+ indexes + Views + Triggers 