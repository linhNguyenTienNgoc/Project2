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
│   │   │           │   ├── 📁 DatabaseConfig.java                # Cấu hình kết nối DB
│   │   │           │   ├── 📁 AppConfig.java                     # Cấu hình ứng dụng
│   │   │           │   └── 📁 SecurityConfig.java                # Cấu hình bảo mật
│   │   │           ├── 📁 model/
│   │   │           │   ├── 📁 entity/                            # Các entity/POJO
│   │   │           │   │   ├── 📁 User.java
│   │   │           │   │   ├── 📁 Role.java
│   │   │           │   │   ├── 📁 Product.java
│   │   │           │   │   ├── 📁 Category.java
│   │   │           │   │   ├── 📁 Order.java
│   │   │           │   │   ├── 📁 OrderDetail.java
│   │   │           │   │   ├── 📁 Customer.java
│   │   │           │   │   ├── 📁 Table.java
│   │   │           │   │   ├── 📁 Area.java
│   │   │           │   │   ├── 📁 Promotion.java
│   │   │           │   │   ├── 📁 Ingredient.java
│   │   │           │   │   ├── 📁 Recipe.java
│   │   │           │   │   ├── 📁 Attendance.java
│   │   │           │   │   └── 📁 SystemSetting.java
│   │   │           │   ├── 📁 dto/                               # Data Transfer Objects
│   │   │           │   │   ├── 📁 OrderDTO.java
│   │   │           │   │   ├── 📁 ProductDTO.java
│   │   │           │   │   ├── 📁 CustomerDTO.java
│   │   │           │   │   ├── 📁 RevenueDTO.java
│   │   │           │   │   └── 📁 DashboardDTO.java
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
│   │   │           │   ├── 📁 IngredientDAO.java
│   │   │           │   ├── 📁 IngredientDAOImpl.java
│   │   │           │   ├── 📁 AttendanceDAO.java
│   │   │           │   └── 📁 AttendanceDAOImpl.java
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
│   │   │           │   ├── 📁 IngredientService.java
│   │   │           │   ├── 📁 IngredientServiceImpl.java
│   │   │           │   ├── 📁 AttendanceService.java
│   │   │           │   ├── 📁 AttendanceServiceImpl.java
│   │   │           │   ├── 📁 AuthService.java                   # Xác thực & phân quyền
│   │   │           │   ├── 📁 AuthServiceImpl.java
│   │   │           │   ├── 📁 ReportService.java                 # Báo cáo
│   │   │           │   ├── 📁 ReportServiceImpl.java
│   │   │           │   ├── 📁 NotificationService.java           # Thông báo
│   │   │           │   └── 📁 NotificationServiceImpl.java
│   │   │           ├── 📁 controller/                            # Controllers (MVC)
│   │   │           │   ├── 📁 base/
│   │   │           │   │   ├── 📁 BaseController.java
│   │   │           │   │   └── 📁 BaseControllerImpl.java
│   │   │           │   ├── 📁 auth/
│   │   │           │   │   ├── 📁 LoginController.java
│   │   │           │   │   └── 📁 RegisterController.java
│   │   │           │   ├── 📁 dashboard/
│   │   │           │   │   └── 📁 DashboardController.java
│   │   │           │   ├── 📁 order/
│   │   │           │   │   ├── 📁 OrderController.java
│   │   │           │   │   ├── 📁 OrderDetailController.java
│   │   │           │   │   └── 📁 PaymentController.java
│   │   │           │   ├── 📁 product/
│   │   │           │   │   ├── 📁 ProductController.java
│   │   │           │   │   ├── 📁 CategoryController.java
│   │   │           │   │   └── 📁 ProductManagementController.java
│   │   │           │   ├── 📁 customer/
│   │   │           │   │   ├── 📁 CustomerController.java
│   │   │           │   │   └── 📁 CustomerManagementController.java
│   │   │           │   ├── 📁 table/
│   │   │           │   │   ├── 📁 TableController.java
│   │   │           │   │   └── 📁 TableManagementController.java
│   │   │           │   ├── 📁 user/
│   │   │           │   │   ├── 📁 UserController.java
│   │   │           │   │   └── 📁 UserManagementController.java
│   │   │           │   ├── 📁 inventory/
│   │   │           │   │   ├── 📁 IngredientController.java
│   │   │           │   │   ├── 📁 StockController.java
│   │   │           │   │   └── 📁 RecipeController.java
│   │   │           │   ├── 📁 promotion/
│   │   │           │   │   └── 📁 PromotionController.java
│   │   │           │   ├── 📁 attendance/
│   │   │           │   │   └── 📁 AttendanceController.java
│   │   │           │   ├── 📁 report/
│   │   │           │   │   ├── 📁 RevenueReportController.java
│   │   │           │   │   ├── 📁 SalesReportController.java
│   │   │           │   │   ├── 📁 InventoryReportController.java
│   │   │           │   │   └── 📁 AttendanceReportController.java
│   │   │           │   └── 📁 settings/
│   │   │           │       ├── 📁 SettingsController.java
│   │   │           │       └── 📁 SystemSettingsController.java
│   │   │           ├── 📁 util/                                  # Utilities
│   │   │           │   ├── 📁 DatabaseUtil.java                  # Tiện ích database
│   │   │           │   ├── 📁 AlertUtil.java                     # Hiển thị thông báo
│   │   │           │   ├── 📁 ValidationUtil.java                # Kiểm tra dữ liệu
│   │   │           │   ├── 📁 DateUtil.java                      # Xử lý ngày tháng
│   │   │           │   ├── 📁 CurrencyUtil.java                  # Xử lý tiền tệ
│   │   │           │   ├── 📁 PasswordUtil.java                  # Mã hóa mật khẩu
│   │   │           │   ├── 📁 FileUtil.java                      # Xử lý file
│   │   │           │   ├── 📁 ImageUtil.java                     # Xử lý hình ảnh
│   │   │           │   ├── 📁 PrintUtil.java                     # In ấn
│   │   │           │   ├── 📁 EmailUtil.java                     # Gửi email
│   │   │           │   ├── 📁 LogUtil.java                       # Ghi log
│   │   │           │   └── 📁 Constants.java                     # Hằng số
│   │   │           ├── 📁 exception/                             # Exception handling
│   │   │           │   ├── 📁 DatabaseException.java
│   │   │           │   ├── 📁 ValidationException.java
│   │   │           │   ├── 📁 AuthenticationException.java
│   │   │           │   └── 📁 BusinessException.java
│   │   │           └── 📁 event/                                 # Custom Events
│   │   │               ├── 📁 OrderEvent.java
│   │   │               ├── 📁 TableEvent.java
│   │   │               └── 📁 UserEvent.java
│   │   ├── 📁 resources/
│   │   │   ├── 📁 fxml/                                          # FXML Files (Views)
│   │   │   │   ├── 📁 auth/
│   │   │   │   │   ├── 📁 login.fxml
│   │   │   │   │   └── 📁 register.fxml
│   │   │   │   ├── 📁 dashboard/
│   │   │   │   │   └── 📁 dashboard.fxml
│   │   │   │   ├── 📁 order/
│   │   │   │   │   ├── 📁 order.fxml
│   │   │   │   │   ├── 📁 order-detail.fxml
│   │   │   │   │   ├── 📁 payment.fxml
│   │   │   │   │   └── 📁 receipt.fxml
│   │   │   │   ├── 📁 product/
│   │   │   │   │   ├── 📁 product.fxml
│   │   │   │   │   ├── 📁 category.fxml
│   │   │   │   │   └── 📁 product-management.fxml
│   │   │   │   ├── 📁 customer/
│   │   │   │   │   ├── 📁 customer.fxml
│   │   │   │   │   └── 📁 customer-management.fxml
│   │   │   │   ├── 📁 table/

│   │   │   │   │   └── 📁 table-management.fxml
│   │   │   │   ├── 📁 user/
│   │   │   │   │   ├── 📁 user.fxml
│   │   │   │   │   └── 📁 user-management.fxml
│   │   │   │   ├── 📁 inventory/
│   │   │   │   │   ├── 📁 ingredient.fxml
│   │   │   │   │   ├── 📁 stock.fxml
│   │   │   │   │   └── 📁 recipe.fxml
│   │   │   │   ├── 📁 promotion/
│   │   │   │   │   └── 📁 promotion.fxml
│   │   │   │   ├── 📁 attendance/
│   │   │   │   │   └── 📁 attendance.fxml
│   │   │   │   ├── 📁 report/
│   │   │   │   │   ├── 📁 revenue-report.fxml
│   │   │   │   │   ├── 📁 sales-report.fxml
│   │   │   │   │   ├── 📁 inventory-report.fxml
│   │   │   │   │   └── 📁 attendance-report.fxml
│   │   │   │   ├── 📁 settings/
│   │   │   │   │   ├── 📁 settings.fxml
│   │   │   │   │   └── 📁 system-settings.fxml
│   │   │   │   ├── 📁 components/                                # Reusable Components
│   │   │   │   │   ├── 📁 product-card.fxml
│   │   │   │   │   ├── 📁 table-card.fxml
│   │   │   │   │   ├── 📁 order-item.fxml
│   │   │   │   │   ├── 📁 customer-card.fxml
│   │   │   │   │   ├── 📁 user-card.fxml
│   │   │   │   │   └── 📁 navigation.fxml
│   │   │   │   └── 📁 dialogs/                                   # Dialog Windows
│   │   │   │       ├── 📁 confirm-dialog.fxml
│   │   │   │       ├── 📁 input-dialog.fxml
│   │   │   │       ├── 📁 error-dialog.fxml
│   │   │   │       └── 📁 success-dialog.fxml
│   │   │   ├── 📁 css/                                           # Stylesheets
│   │   │   │   ├── 📁 main.css                                   # CSS chính
│   │   │   │   ├── 📁 components.css                             # CSS cho components
│   │   │   │   ├── 📁 themes/
│   │   │   │   │   ├── 📁 light.css
│   │   │   │   │   ├── 📁 dark.css
│   │   │   │   │   └── 📁 modern.css
│   │   │   │   └── 📁 custom/
│   │   │   │       ├── 📁 buttons.css
│   │   │   │       ├── 📁 tables.css
│   │   │   │       ├── 📁 forms.css
│   │   │   │       └── 📁 cards.css
│   │   │   ├── 📁 images/                                        # Hình ảnh
│   │   │   │   ├── 📁 logo/
│   │   │   │   │   ├── 📁 logo.png
│   │   │   │   │   └── 📁 logo-dark.png
│   │   │   │   ├── 📁 icons/
│   │   │   │   │   ├── 📁 dashboard.png
│   │   │   │   │   ├── 📁 order.png
│   │   │   │   │   ├── 📁 product.png
│   │   │   │   │   ├── 📁 customer.png
│   │   │   │   │   ├── 📁 table.png
│   │   │   │   │   ├── 📁 user.png
│   │   │   │   │   ├── 📁 inventory.png
│   │   │   │   │   ├── 📁 report.png
│   │   │   │   │   ├── 📁 settings.png
│   │   │   │   │   └── 📁 logout.png
│   │   │   │   ├── 📁 products/                                  # Hình ảnh sản phẩm
│   │   │   │   │   ├── 📁 coffee/
│   │   │   │   │   ├── 📁 tea/
│   │   │   │   │   ├── 📁 cake/
│   │   │   │   │   └── 📁 food/
│   │   │   │   ├── 📁 backgrounds/
│   │   │   │   │   ├── 📁 login-bg.jpg
│   │   │   │   │   └── 📁 dashboard-bg.jpg
│   │   │   │   └── 📁 placeholders/
│   │   │   │       ├── 📁 no-image.png
│   │   │   │       └── 📁 avatar-default.png
│   │   │   ├── 📁 fonts/                                         # Fonts
│   │   │   │   ├── 📁 Roboto/
│   │   │   │   ├── 📁 OpenSans/
│   │   │   │   └── 📁 Poppins/
│   │   │   ├── 📁 templates/                                     # Templates
│   │   │   │   ├── 📁 receipt-template.html
│   │   │   │   ├── 📁 invoice-template.html
│   │   │   │   └── 📁 report-template.html
│   │   │   ├── 📁 properties/                                    # Properties files
│   │   │   │   ├── 📁 messages.properties                        # Đa ngôn ngữ
│   │   │   │   ├── 📁 messages_vi.properties
│   │   │   │   ├── 📁 messages_en.properties
│   │   │   │   ├── 📁 validation.properties
│   │   │   │   └── 📁 config.properties
│   │   │   └── 📁 sql/                                           # SQL Scripts
│   │   │       ├── 📁 init.sql                                   # Khởi tạo database
│   │   │       ├── 📁 sample-data.sql                            # Dữ liệu mẫu
│   │   │       └── 📁 queries/                                   # Các query phức tạp
│   │   │           ├── 📁 reports.sql
│   │   │           ├── 📁 analytics.sql
│   │   │           └── 📁 maintenance.sql
│   │   └── 📁 test/                                              # Test files
│   │       ├── 📁 java/
│   │       │   └── 📁 com/
│   │       │       └── 📁 cafe/
│   │       │           ├── 📁 test/
│   │       │           │   ├── 📁 dao/
│   │       │           │   │   ├── 📁 UserDAOTest.java
│   │       │           │   │   ├── 📁 ProductDAOTest.java
│   │       │           │   │   └── 📁 OrderDAOTest.java
│   │       │           │   ├── 📁 service/
│   │       │           │   │   ├── 📁 UserServiceTest.java
│   │       │           │   │   ├── 📁 ProductServiceTest.java
│   │       │           │   │   └── 📁 OrderServiceTest.java
│   │       │           │   ├── 📁 controller/
│   │       │           │   │   ├── 📁 LoginControllerTest.java
│   │       │           │   │   ├── 📁 OrderControllerTest.java
│   │       │           │   │   └── 📁 ProductControllerTest.java
│   │       │           │   └── 📁 util/
│   │       │           │       ├── 📁 ValidationUtilTest.java
│   │       │           │       ├── 📁 DateUtilTest.java
│   │       │           │       └── 📁 PasswordUtilTest.java
│   │       │           └── 📁 integration/
│   │       │               ├── 📁 DatabaseIntegrationTest.java
│   │       │               └── 📁 ApplicationIntegrationTest.java
│   │       └── 📁 resources/
│   │           ├── 📁 test-data/
│   │           │   ├── 📁 test-users.json
│   │           │   ├── 📁 test-products.json
│   │           │   └── 📁 test-orders.json
│   │           └── 📁 test-config/
│   │               └── 📁 test-database.properties
├── 📁 database/                                                   # Database files (existing)
│   ├── 📁 cafe_management.sql
│   ├── 📁 cafe_management.sql~
│   ├── 📁 database_config.properties
│   ├── 📁 EER.mwb
│   ├── 📁 EER.mwb.bak
│   ├── 📁 ERD.md
│   └── 📁 README.md
├── 📁 docs/                                                       # Documentation
│   ├── 📁 api/                                                    # API Documentation
│   │   ├── 📁 controllers.md
│   │   ├── 📁 services.md
│   │   └── 📁 daos.md
│   ├── 📁 user-guide/                                             # Hướng dẫn sử dụng
│   │   ├── 📁 installation.md
│   │   ├── 📁 user-manual.md
│   │   └── 📁 troubleshooting.md
│   ├── 📁 developer/                                              # Tài liệu developer
│   │   ├── 📁 architecture.md
│   │   ├── 📁 coding-standards.md
│   │   ├── 📁 database-design.md
│   │   └── 📁 deployment.md
│   └── 📁 assets/                                                 # Hình ảnh cho docs
│       ├── 📁 screenshots/
│       ├── 📁 diagrams/
│       └── 📁 mockups/
├── 📁 build/                                                      # Build output
│   ├── 📁 classes/
│   ├── 📁 libs/
│   └── 📁 reports/
├── 📁 dist/                                                       # Distribution
│   ├── 📁 lib/
│   ├── 📁 bin/
│   └── 📁 CafeManagement.jar
├── 📁 logs/                                                       # Log files
│   ├── 📁 application.log
│   ├── 📁 error.log
│   └── 📁 access.log
├── 📁 backup/                                                     # Database backups
│   └── 📁 auto-backup/
├── 📁 temp/                                                       # Temporary files
│   ├── 📁 uploads/
│   ├── 📁 exports/
│   └── 📁 cache/
├── 📁 scripts/                                                    # Build & deployment scripts
│   ├── 📁 build.bat                                              # Windows build script
│   ├── 📁 build.sh                                               # Linux/Mac build script
│   ├── 📁 run.bat                                                # Windows run script
│   ├── 📁 run.sh                                                 # Linux/Mac run script
│   ├── 📁 deploy.bat                                             # Windows deployment
│   ├── 📁 deploy.sh                                              # Linux/Mac deployment
│   └── 📁 database/
│       ├── 📁 backup.bat
│       ├── 📁 backup.sh
│       ├── 📁 restore.bat
│       └── 📁 restore.sh
├── 📁 lib/                                                        # External libraries
│   ├── 📁 javafx/
│   ├── 📁 mysql/
│   ├── 📁 apache/
│   └── 📁 other/
├── 📁 config/                                                     # Configuration files
│   ├── 📁 application.properties
│   ├── 📁 database.properties
│   ├── 📁 logging.properties
│   └── 📁 security.properties
├── 📁 .gitignore                                                  # Git ignore file
├── 📁 README.md                                                   # Project README
├── 📁 CHANGELOG.md                                               # Version history
├── 📁 LICENSE                                                     # License file
├── 📁 pom.xml                                                     # Maven configuration
├── 📁 build.gradle                                               # Gradle configuration
├── 📁 Project.iml                                                # IntelliJ IDEA project
├── 📁 .idea/                                                      # IntelliJ IDEA settings
│   ├── 📁 modules.xml
│   ├── 📁 misc.xml
│   ├── 📁 workspace.xml
│   └── 📁 runConfigurations/
└── 📁 .vscode/                                                    # VS Code settings
    ├── 📁 settings.json
    ├── 📁 launch.json
    └── 📁 tasks.json
```

## 📋 Mô tả các thư mục chính:

### 🏗️ **src/main/java/com/cafe/**
- **Cấu trúc MVC hoàn chỉnh** với Model, View, Controller
- **Layered Architecture** với DAO, Service, Controller
- **Package organization** theo chức năng nghiệp vụ

### 🎨 **src/main/resources/**
- **FXML files** cho giao diện JavaFX
- **CSS stylesheets** cho styling
- **Images & Icons** cho UI
- **Properties files** cho đa ngôn ngữ

### 🧪 **src/test/**
- **Unit tests** cho từng layer
- **Integration tests** cho toàn bộ hệ thống
- **Test data** và configuration

### 📚 **docs/**
- **API documentation** cho developers
- **User guides** cho end users
- **Architecture docs** cho maintainers

### 🔧 **Scripts & Config**
- **Build scripts** cho nhiều platform
- **Database scripts** cho backup/restore
- **Configuration files** cho deployment

## 🚀 **Lợi ích của cấu trúc này:**

1. **Scalability:** Dễ dàng mở rộng và thêm tính năng mới
2. **Maintainability:** Code được tổ chức rõ ràng, dễ bảo trì
3. **Testability:** Hỗ trợ testing ở mọi level
4. **Deployment:** Scripts tự động hóa việc build và deploy
5. **Documentation:** Tài liệu đầy đủ cho mọi đối tượng
6. **Internationalization:** Hỗ trợ đa ngôn ngữ
7. **Security:** Tách biệt logic bảo mật
8. **Performance:** Tối ưu hóa database và caching

Cấu trúc này sẽ giúp phát triển một hệ thống quản lý quán cà phê chuyên nghiệp, dễ bảo trì và mở rộng! 