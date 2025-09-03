# 🌳 Cấu trúc Thư mục Project - Cafe Management System (JavaFX)

```
Project2/
├── 📁 src/
│   ├── 📁 main/
│   │   ├── 📁 java/
│   │   │   └── 📁 com/
│   │   │       └── 📁 cafe/
│   │   │           ├── 📁 CafeManagementApplication.java          # Main Application
│   │   │           ├── 📁 config/
│   │   │           │   └── 📁 DatabaseConfig.java                # Cấu hình kết nối DB
│   │   │           ├── 📁 model/
│   │   │           │   ├── 📁 entity/                            # Các entity/POJO
│   │   │           │   │   ├── 📁 User.java
│   │   │           │   │   ├── 📁 Product.java
│   │   │           │   │   ├── 📁 Category.java
│   │   │           │   │   ├── 📁 Order.java
│   │   │           │   │   ├── 📁 OrderDetail.java
│   │   │           │   │   ├── 📁 Customer.java
│   │   │           │   │   ├── 📁 TableCafe.java
│   │   │           │   │   ├── 📁 Area.java
│   │   │           │   │   ├── 📁 Promotion.java
│   │   │           │   │   └── 📁 OrderPromotion.java
│   │   │           │   ├── 📁 dto/                               # Data Transfer Objects
│   │   │           │   │   ├── 📁 PaymentRequest.java
│   │   │           │   │   ├── 📁 PaymentResponse.java
│   │   │           │   │   └── 📁 SalesData.java
│   │   │           │   └── 📁 enums/                             # Các enum
│   │   │           │       ├── 📁 OrderStatus.java
│   │   │           │       ├── 📁 PaymentStatus.java
│   │   │           │       ├── 📁 PaymentMethod.java
│   │   │           │       ├── 📁 TableStatus.java
│   │   │           │       └── 📁 UserRole.java
│   │   │           ├── 📁 dao/                                   # Data Access Objects
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 AreaDAO.java
│   │   │           │   │   ├── 📁 AreaDAOImpl.java
│   │   │           │   │   ├── 📁 CategoryDAO.java
│   │   │           │   │   ├── 📁 CategoryDAOImpl.java
│   │   │           │   │   ├── 📁 CustomerDAO.java
│   │   │           │   │   ├── 📁 CustomerDAOImpl.java
│   │   │           │   │   ├── 📁 OrderDAO.java
│   │   │           │   │   ├── 📁 OrderDAOImpl.java
│   │   │           │   │   ├── 📁 OrderDetailDAO.java
│   │   │           │   │   ├── 📁 OrderDetailDAOImpl.java
│   │   │           │   │   ├── 📁 ProductDAO.java
│   │   │           │   │   ├── 📁 ProductDAOImpl.java
│   │   │           │   │   ├── 📁 PromotionDAO.java
│   │   │           │   │   ├── 📁 PromotionDAOImpl.java
│   │   │           │   │   ├── 📁 TableDAO.java
│   │   │           │   │   ├── 📁 TableDAOImpl.java
│   │   │           │   │   ├── 📁 UserDAO.java
│   │   │           │   │   └── 📁 UserDAOImpl.java
│   │   │           ├── 📁 service/                               # Business Logic Layer
│   │   │           │   ├── 📁 CustomerService.java
│   │   │           │   ├── 📁 MenuService.java
│   │   │           │   ├── 📁 OrderService.java
│   │   │           │   ├── 📁 PaymentService.java
│   │   │           │   ├── 📁 PromotionService.java
│   │   │           │   ├── 📁 QRCodeService.java
│   │   │           │   ├── 📁 ReceiptService.java
│   │   │           │   ├── 📁 ReportService.java
│   │   │           │   └── 📁 TableService.java
│   │   │           ├── 📁 controller/                            # Controllers (MVC)
│   │   │           │   ├── 📁 admin/
│   │   │           │   │   ├── 📁 AdminDashboardController.java
│   │   │           │   │   ├── 📁 AdminMenuController.java
│   │   │           │   │   ├── 📁 AdminReportController.java
│   │   │           │   │   ├── 📁 AdminTableController.java
│   │   │           │   │   ├── 📁 AdminUserController.java
│   │   │           │   │   └── 📁 UserFormDialogController.java
│   │   │           │   ├── 📁 auth/
│   │   │           │   │   └── 📁 LoginController.java
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 DashboardCommunicator.java
│   │   │           │   │   ├── 📁 DashboardEventHandler.java
│   │   │           │   │   └── 📁 DashboardHelper.java
│   │   │           │   ├── 📁 customer/
│   │   │           │   │   └── 📁 CustomerController.java
│   │   │           │   ├── 📁 dashboard/
│   │   │           │   │   └── 📁 DashboardController.java
│   │   │           │   ├── 📁 menu/
│   │   │           │   │   └── 📁 MenuController.java
│   │   │           │   ├── 📁 order/
│   │   │           │   │   └── 📁 OrderPanelController.java
│   │   │           │   ├── 📁 payment/
│   │   │           │   │   └── 📁 PaymentController.java
│   │   │           │   ├── 📁 promotion/
│   │   │           │   │   └── 📁 PromotionController.java
│   │   │           │   └── 📁 table/
│   │   │           │       └── 📁 TableController.java
│   │   │           ├── 📁 util/                                  # Utilities
│   │   │           │   ├── 📁 AlertUtils.java                    # Hiển thị thông báo
│   │   │           │   ├── 📁 CredentialManager.java             # Quản lý thông tin đăng nhập
│   │   │           │   ├── 📁 DateUtils.java                     # Xử lý ngày tháng
│   │   │           │   ├── 📁 ExcelExporter.java                 # Xuất Excel
│   │   │           │   ├── 📁 FXMLUtils.java                     # Tiện ích FXML
│   │   │           │   ├── 📁 ImageLoader.java                   # Tải hình ảnh
│   │   │           │   ├── 📁 PasswordUtil.java                  # Mã hóa mật khẩu
│   │   │           │   ├── 📁 PaymentValidator.java              # Kiểm tra thanh toán
│   │   │           │   ├── 📁 PDFExporter.java                   # Xuất PDF
│   │   │           │   ├── 📁 PriceFormatter.java                # Định dạng giá
│   │   │           │   ├── 📁 SessionManager.java                # Quản lý session
│   │   │           │   └── 📁 ValidationUtils.java               # Kiểm tra dữ liệu
│   │   │           └── 📁 exception/                             # Exception handling
│   │   │               └── 📁 DatabaseConnectionException.java
│   │   ├── 📁 resources/
│   │   │   ├── 📁 fxml/                                          # FXML Files (Views)
│   │   │   │   ├── 📁 admin/                                     # Admin views
│   │   │   │   ├── 📁 auth/                                      # Authentication views
│   │   │   │   ├── 📁 components/                                # Reusable components
│   │   │   │   ├── 📁 customer/                                  # Customer views
│   │   │   │   ├── 📁 dashboard/                                 # Dashboard views
│   │   │   │   ├── 📁 dialogs/                                   # Dialog windows
│   │   │   │   ├── 📁 inventory/                                 # Inventory views
│   │   │   │   ├── 📁 order/                                     # Order views
│   │   │   │   ├── 📁 payment/                                   # Payment views
│   │   │   │   ├── 📁 product/                                   # Product views
│   │   │   │   ├── 📁 promotion/                                 # Promotion views
│   │   │   │   ├── 📁 report/                                    # Report views
│   │   │   │   ├── 📁 screen/                                    # Screen layouts
│   │   │   │   ├── 📁 settings/                                  # Settings views
│   │   │   │   ├── 📁 table/                                     # Table views
│   │   │   │   └── 📁 user/                                      # User views
│   │   │   ├── 📁 css/                                           # Stylesheets
│   │   │   │   ├── 📁 admin-dashboard.css
│   │   │   │   ├── 📁 admin-styles.css
│   │   │   │   ├── 📁 admin-theme.css
│   │   │   │   ├── 📁 customer-layout.css
│   │   │   │   ├── 📁 dashboard.css
│   │   │   │   ├── 📁 login.css
│   │   │   │   ├── 📁 menu-style.css
│   │   │   │   ├── 📁 payment.css
│   │   │   │   ├── 📁 promotion.css
│   │   │   │   ├── 📁 report.css
│   │   │   │   ├── 📁 table-layout.css
│   │   │   │   ├── 📁 user-form-dialog.css
│   │   │   │   └── 📁 custom/                                     # Custom styles
│   │   │   ├── 📁 images/                                        # Hình ảnh
│   │   │   │   ├── 📁 backgrounds/                               # Hình nền
│   │   │   │   ├── 📁 icons/                                     # Icons
│   │   │   │   ├── 📁 logo/                                      # Logo
│   │   │   │   ├── 📁 placeholders/                              # Hình placeholder
│   │   │   │   └── 📁 products/                                  # Hình ảnh sản phẩm (45 files)
│   │   │   ├── 📁 fonts/                                         # Fonts
│   │   │   ├── 📁 templates/                                     # Templates
│   │   │   ├── 📁 properties/                                    # Properties files
│   │   │   ├── 📁 sql/                                           # SQL Scripts
│   │   │   │   └── 📁 queries/                                   # Các query phức tạp
│   │   │   └── 📁 database_config.properties                     # Database config
│   │   └── 📁 test/                                              # Test files
│   │       ├── 📁 java/
│   │       │   └── 📁 com/
│   │       │       └── 📁 cafe/
│   │       │           └── 📁 [2 test files]
│   │       └── 📁 resources/
│   │           ├── 📁 test-config/
│   │           └── 📁 test-data/
├── 📁 database/                                                   # Database files
│   ├── 📁 cafe_database_structure.sql                            # Database schema
│   ├── 📁 cafe_sample_data.sql                                   # Sample data
│   ├── 📁 database_config.properties                             # Database config
│   ├── 📁 EER.mwb                                                # MySQL Workbench model
│   ├── 📁 ERD.md                                                 # Entity Relationship Diagram
│   ├── 📁 README.md                                              # Database documentation
│   ├── 📁 SETUP_GUIDE.md                                         # Setup guide
│   ├── 📁 setup_database.bat                                     # Windows setup script
│   ├── 📁 setup_database.sh                                      # Linux/Mac setup script
│   └── 📁 update_product_images.sql                              # Product images update
├── 📁 docs/                                                       # Documentation
│   ├── 📁 api/                                                    # API Documentation
│   ├── 📁 assets/                                                 # Hình ảnh cho docs
│   │   ├── 📁 diagrams/
│   │   ├── 📁 mockups/
│   │   └── 📁 screenshots/
│   ├── 📁 developer/                                              # Tài liệu developer
│   │   ├── 📁 DYNAMIC_USER_DISPLAY.md
│   │   ├── 📁 ORDER_SERVICE_GUIDE.md
│   │   ├── 📁 PRODUCT_IMAGE_GUIDE.md
│   │   └── 📁 REMEMBER_ME_FEATURE.md
│   └── 📁 user-guide/                                             # Hướng dẫn sử dụng
├── 📁 backup/                                                     # Database backups
│   └── 📁 auto-backup/
├── 📁 config/                                                     # Configuration files
│   └── 📁 application.properties
├── 📁 lib/                                                        # External libraries
│   └── 📁 javafx-sdk-24.0.2/                                     # JavaFX SDK
├── 📁 logs/                                                       # Log files
├── 📁 scripts/                                                    # Build & run scripts
│   ├── 📁 build.bat
│   ├── 📁 build.sh
│   ├── 📁 run.bat
│   ├── 📁 run.sh
│   └── 📁 src/
├── 📁 temp/                                                       # Temporary files
│   ├── 📁 cache/
│   ├── 📁 exports/
│   └── 📁 uploads/
├── 📁 target/                                                     # Maven build output
├── 📁 .gitignore                                                  # Git ignore file
├── 📁 build-and-run.bat                                          # Build and run script
├── 📁 CHANGELOG.md                                               # Version history
├── 📁 dependency-reduced-pom.xml                                 # Maven dependency
├── 📁 pom.xml                                                     # Maven configuration
├── 📁 project_structure.md                                       # Project structure documentation
├── 📁 README.md                                                   # Project README
├── 📁 RUNNING_GUIDE.md                                           # Running guide
├── 📁 run-app.bat                                                # Main run script
├── 📁 run-simple.bat                                             # Simple run script
└── 📁 test-dashboard.bat                                         # Test dashboard script
```

## 📋 Mô tả các thư mục chính:

### 🏗️ **src/main/java/com/cafe/**
- **Cấu trúc MVC hoàn chỉnh** với Model, View, Controller
- **Layered Architecture** với DAO, Service, Controller
- **Package organization** theo chức năng nghiệp vụ
- **77 Java files** bao gồm controllers, services, DAOs, models

### 🎨 **src/main/resources/**
- **FXML files** cho giao diện JavaFX (8 admin views, auth, dashboard, etc.)
- **CSS stylesheets** cho styling (12 CSS files)
- **Images & Icons** cho UI (45 product images + backgrounds, icons, logos)
- **Database config** cho kết nối MySQL

### 🗄️ **database/**
- **Database schema** hoàn chỉnh với 12 bảng
- **Sample data** phong phú (7 users, 50+ products, 10 orders)
- **Setup scripts** cho Windows và Linux/Mac
- **Documentation** chi tiết (ERD, README, SETUP_GUIDE)

### 📚 **docs/**
- **Developer guides** cho các tính năng cụ thể
- **API documentation** cho developers
- **Assets** cho screenshots và diagrams

### 🔧 **Scripts & Config**
- **Run scripts** đa dạng (run-app.bat, run-simple.bat, build-and-run.bat)
- **Database scripts** cho setup và backup
- **Maven configuration** với JavaFX dependencies

## 🚀 **Lợi ích của cấu trúc này:**

1. **Scalability:** Dễ dàng mở rộng và thêm tính năng mới
2. **Maintainability:** Code được tổ chức rõ ràng, dễ bảo trì
3. **Testability:** Hỗ trợ testing với 2 test files
4. **Deployment:** Scripts tự động hóa việc build và deploy
5. **Documentation:** Tài liệu đầy đủ cho mọi đối tượng
6. **Database Integration:** Schema hoàn chỉnh với sample data
7. **JavaFX UI:** Giao diện desktop hiện đại với 12 CSS themes
8. **Performance:** Tối ưu hóa database và caching

Cấu trúc này đã được triển khai và hoạt động ổn định cho hệ thống quản lý quán cà phê! 