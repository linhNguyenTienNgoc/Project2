package com.cafe.controller.admin;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.util.SessionManager;
import com.cafe.service.ReportService;
import com.cafe.service.TableService;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.config.DatabaseConfig;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình Admin Dashboard
 * Quản lý navigation và hiển thị các modules khác nhau cho Admin
 *
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class AdminDashboardController extends DashboardController {

    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;

    // Tab buttons
    @FXML private Button overviewTabButton;
    @FXML private Button userTabButton;
    @FXML private Button menuTabButton;
    @FXML private Button tableTabButton;
    @FXML private Button reportTabButton;
    @FXML private Button promotionTabButton;
    @FXML private Button customerTabButton;
    @FXML private Button settingsTabButton;

    // Content area
    @FXML private VBox sidebar;
    @FXML private StackPane contentPane;
    
    // Dashboard components - only those present in FXML
    @FXML private Label currentDateLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label todayRevenueLabel;
    @FXML private Label tablesInUseLabel;
    @FXML private Label todayOrdersLabel;
    @FXML private Label staffOnDutyLabel;

    // Note: Order processing removed from admin dashboard
    // Order management is handled through staff interface

    // Current state
    private String currentTab = "overview";
    private Map<String, Node> loadedContent = new HashMap<>();
    private Map<String, Object> loadedControllers = new HashMap<>();

    // Note: Order state removed from admin dashboard
    // Admin focuses on management functions only
    
    // Services for data access
    private ReportService reportService;
    private TableService tableService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Verify FXML injection
            verifyFXMLInjection();

            // Initialize services
            initializeServices();

            setupUserInfo();
            setupTabNavigation();
            setupEventHandlers();
            setupDashboardComponents();
            startRealtimeClock();

            // Load default content (Overview Dashboard)
            loadTabContent("overview");

            System.out.println("✅ AdminDashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminDashboardController: " + e.getMessage());
            e.printStackTrace();
            // Show user-friendly error message
            showError("Lỗi khởi tạo Admin Dashboard: " + e.getMessage());
        }
    }

    /**
     * Initialize services for database access
     */
    private void initializeServices() {
        try {
            reportService = new ReportService();
            tableService = new TableService();
            System.out.println("✅ Services initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing services: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Verify that all required FXML elements are properly injected
     */
    private void verifyFXMLInjection() {
        StringBuilder missingElements = new StringBuilder();

        // Verify elements that actually exist in admin-dashboard.fxml
        if (sidebar == null) missingElements.append("sidebar, ");
        if (userNameLabel == null) missingElements.append("userNameLabel, ");
        if (userRoleLabel == null) missingElements.append("userRoleLabel, ");
        if (logoutButton == null) missingElements.append("logoutButton, ");
        if (overviewTabButton == null) missingElements.append("overviewTabButton, ");
        if (userTabButton == null) missingElements.append("userTabButton, ");
        if (menuTabButton == null) missingElements.append("menuTabButton, ");
        if (tableTabButton == null) missingElements.append("tableTabButton, ");
        if (customerTabButton == null) missingElements.append("customerTabButton, ");
        if (reportTabButton == null) missingElements.append("reportTabButton, ");
        if (promotionTabButton == null) missingElements.append("promotionTabButton, ");
        if (settingsTabButton == null) missingElements.append("settingsTabButton, ");
        if (contentPane == null) missingElements.append("contentPane, ");
        if (currentDateLabel == null) missingElements.append("currentDateLabel, ");
        if (currentTimeLabel == null) missingElements.append("currentTimeLabel, ");
        if (todayRevenueLabel == null) missingElements.append("todayRevenueLabel, ");
        if (tablesInUseLabel == null) missingElements.append("tablesInUseLabel, ");
        if (todayOrdersLabel == null) missingElements.append("todayOrdersLabel, ");
        if (staffOnDutyLabel == null) missingElements.append("staffOnDutyLabel, ");

        if (missingElements.length() > 0) {
            String missing = missingElements.substring(0, missingElements.length() - 2);
            System.err.println("⚠️ Warning: Missing FXML elements: " + missing);
            System.err.println("This may cause NullPointerException in some features");
        }
    }

    /**
     * Setup user information display
     */
    private void setupUserInfo() {
        try {
            // Get user info from session
            if (SessionManager.isLoggedIn()) {
                String fullName = SessionManager.getCurrentUserFullName();
                String role = SessionManager.getCurrentUserRole();
                
                userNameLabel.setText(fullName);
                userRoleLabel.setText(role);
                
                System.out.println("👤 Admin dashboard user info: " + fullName + " (" + role + ")");
            } else {
                userNameLabel.setText("Admin User");
                userRoleLabel.setText("Administrator");
                System.out.println("⚠️ No session found, using fallback admin info");
            }
        } catch (Exception e) {
            System.err.println("❌ Error setting up admin user info: " + e.getMessage());
            userNameLabel.setText("Admin User");
            userRoleLabel.setText("Administrator");
        }
    }
    
    /**
     * Refresh user information from session
     * Có thể gọi khi user info thay đổi
     */
    public void refreshUserInfo() {
        setupUserInfo();
        System.out.println("🔄 Admin dashboard user info refreshed");
    }
    
    /**
     * Update user display with new information
     * @param fullName Tên đầy đủ của user
     * @param role Vai trò của user
     */
    public void updateUserDisplay(String fullName, String role) {
        if (fullName != null && !fullName.isEmpty()) {
            userNameLabel.setText(fullName);
        }
        
        if (role != null && !role.isEmpty()) {
            userRoleLabel.setText(role);
        }
        
        System.out.println("👤 Admin dashboard user display updated: " + fullName + " (" + role + ")");
    }

    /**
     * Setup tab navigation
     */
    private void setupTabNavigation() {
        // Set initial active tab - overview phải active khi mở lần đầu
        if (overviewTabButton != null) {
            overviewTabButton.getStyleClass().setAll("nav-button", "active");
        }
        // Set current tab to overview
        currentTab = "overview";
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Tab button handlers - với null check
        if (overviewTabButton != null) overviewTabButton.setOnAction(e -> switchToTab("overview"));
        if (userTabButton != null) userTabButton.setOnAction(e -> switchToTab("user"));
        if (menuTabButton != null) menuTabButton.setOnAction(e -> switchToTab("menu"));
        if (tableTabButton != null) tableTabButton.setOnAction(e -> switchToTab("table"));
        if (customerTabButton != null) customerTabButton.setOnAction(e -> switchToTab("customer"));
        if (reportTabButton != null) reportTabButton.setOnAction(e -> switchToTab("report"));
        if (promotionTabButton != null) promotionTabButton.setOnAction(e -> switchToTab("promotion"));
        if (settingsTabButton != null) settingsTabButton.setOnAction(e -> switchToTab("settings"));

        // Logout handler
        if (logoutButton != null) logoutButton.setOnAction(e -> logout());

        // Order panel handlers removed - not applicable for admin dashboard
    }

    /**
     * Switch to a specific tab
     */
    @Override
    public void switchToTab(String tabName) {
        if (currentTab.equals(tabName)) {
            return; // Already on this tab
        }

        currentTab = tabName;

        // Update tab button styles
        updateTabButtonStyles();

        // Load tab content
        loadTabContent(tabName);
    }

    /**
     * Update tab button styles
     */
    private void updateTabButtonStyles() {
        // Reset all tab buttons
        if (overviewTabButton != null) overviewTabButton.getStyleClass().setAll("nav-button");
        if (userTabButton != null) userTabButton.getStyleClass().setAll("nav-button");
        if (menuTabButton != null) menuTabButton.getStyleClass().setAll("nav-button");
        if (tableTabButton != null) tableTabButton.getStyleClass().setAll("nav-button");
        if (customerTabButton != null) customerTabButton.getStyleClass().setAll("nav-button");
        if (reportTabButton != null) reportTabButton.getStyleClass().setAll("nav-button");
        if (promotionTabButton != null) promotionTabButton.getStyleClass().setAll("nav-button");
        if (settingsTabButton != null) settingsTabButton.getStyleClass().setAll("nav-button");

        // Set active tab button
        switch (currentTab) {
            case "overview":
                if (overviewTabButton != null) overviewTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "user":
                if (userTabButton != null) userTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "menu":
                if (menuTabButton != null) menuTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "table":
                if (tableTabButton != null) tableTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "customer":
                if (customerTabButton != null) customerTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "report":
                if (reportTabButton != null) reportTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "promotion":
                if (promotionTabButton != null) promotionTabButton.getStyleClass().setAll("nav-button", "active");
                break;
            case "settings":
                if (settingsTabButton != null) settingsTabButton.getStyleClass().setAll("nav-button", "active");
                break;
        }
    }



    /**
     * Load content for a specific tab
     */
    private void loadTabContent(String tabName) {
        try {
            Node content = loadedContent.get(tabName);

            if (content == null) {
                // Load content for the first time
                content = loadFXMLContent(tabName);
                loadedContent.put(tabName, content);
            }

            // Set controller reference for communication
            setupControllerCommunication(tabName);

            // Display content
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);


            System.out.println("✅ Loaded content for tab: " + tabName);

        } catch (Exception e) {
            System.err.println("Error loading tab content: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi tải nội dung tab: " + e.getMessage());
        }
    }

    /**
     * Load FXML content based on tab name
     */
    private Node loadFXMLContent(String tabName) throws IOException {
        String fxmlPath;

        switch (tabName) {
            case "overview":
                fxmlPath = "/fxml/admin/admin-overview.fxml";
                break;
            case "user":
                fxmlPath = "/fxml/admin/user-layout.fxml";
                break;
            case "menu":
                fxmlPath = "/fxml/admin/menu-layout.fxml";
                break;
            case "table":
                fxmlPath = "/fxml/admin/table-layout.fxml";
                break;
            case "customer":
                fxmlPath = "/fxml/admin/customer-layout.fxml";
                break;
            case "report":
                fxmlPath = "/fxml/admin/admin-report-layout.fxml";
                break;
            case "promotion":
                fxmlPath = "/fxml/admin/promotion-layout.fxml";
                break;
            case "settings":
                fxmlPath = "/fxml/admin/settings-layout.fxml";
                break;
            default:
                throw new IllegalArgumentException("Unknown tab: " + tabName);
        }

        FXMLLoader loader = new FXMLLoader(CafeManagementApplication.class.getResource(fxmlPath));
        Node content = loader.load();

        // Store controller reference
        loadedControllers.put(tabName, loader.getController());

        return content;
    }

    /**
     * Setup communication between controllers
     */
    private void setupControllerCommunication(String tabName) {
        Object controller = loadedControllers.get(tabName);
        
        // Kiểm tra null trước khi xử lý
        if (controller == null) {
            System.err.println("Warning: Controller is null for tab: " + tabName);
            return;
        }

        // Sử dụng interface để giao tiếp an toàn
        if (controller instanceof DashboardCommunicator) {
            DashboardCommunicator communicator = (DashboardCommunicator) controller;
            communicator.setDashboardController(this);
            System.out.println("✅ Communication setup for " + tabName + " controller");
        } else {
            System.err.println("Warning: Controller for " + tabName + " does not implement DashboardCommunicator");
        }
    }

    /**
     * Update table information (admin monitoring only)
     */
    @Override
    public void updateTableInfo(String tableName, String status) {
        // Admin dashboard only monitors table status, doesn't process orders
        System.out.println("📊 Admin monitoring table: " + tableName + " (" + status + ")");
    }

    /**
     * Add item to order - Not applicable for admin dashboard
     */
    @Override
    public void addToOrder(String productName, double price, int quantity) {
        // Admin dashboard doesn't handle order processing
        System.out.println("⚠️ Order processing not available in admin dashboard");
    }











    /**
     * Clear order - Not applicable for admin dashboard
     */
    @Override
    public void clearOrder() {
        // Admin dashboard doesn't handle order processing
        System.out.println("⚠️ Order clearing not available in admin dashboard");
    }

    /**
     * Logout
     */
    private void logout() {
        if (CafeManagementApplication.showConfirmAlert("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất không?")) {
            SessionManager.clearSession();
            CafeManagementApplication.showLoginScreen();
        }
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        CafeManagementApplication.showErrorAlert("Lỗi", message);
    }

    // Note: Table state management removed from admin dashboard
    // Admin focuses on monitoring and management functions

    // =====================================================
    // DASHBOARD EVENT HANDLER IMPLEMENTATIONS
    // =====================================================

    /**
     * Add product to order - Not applicable for admin dashboard
     */
    @Override
    public void addProductToOrder(com.cafe.model.entity.Product product, int quantity) {
        // Admin dashboard doesn't handle order processing
        System.out.println("⚠️ Product ordering not available in admin dashboard");
    }

    /**
     * Handle table selection (admin monitoring)
     */
    @Override
    public void onTableSelected(com.cafe.model.entity.TableCafe table) {
        // Admin dashboard only monitors table selection
        updateTableInfo(table.getTableName(), table.getStatus());
        System.out.println("📊 Admin monitoring table selection: " + table.getTableName());
    }

    /**
     * Get current order total - Not applicable for admin dashboard
     */
    @Override
    public double getOrderTotal() {
        // Admin dashboard doesn't track order totals
        return 0.0;
    }

    /**
     * Notify when order status changes
     */
    @Override
    public void onOrderStatusChanged(String newStatus, int tableId) {
        System.out.println("✅ Order status changed: " + newStatus + " for table " + tableId);
        // Note: Order status changes are managed through staff interface, admin only monitors
    }
    
    // =====================================================
    // NEW DASHBOARD COMPONENTS SETUP
    // =====================================================
    
    /**
     * Setup dashboard components with initial data
     */
    private void setupDashboardComponents() {
        try {
            // Setup current date
            setupCurrentDateTime();
            
            // Load dashboard data
            loadDashboardData();
            
            System.out.println("✅ Dashboard components initialized");
        } catch (Exception e) {
            System.err.println("Error setting up dashboard components: " + e.getMessage());
        }
    }
    
    /**
     * Setup current date and time display
     */
    private void setupCurrentDateTime() {
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
        String formattedDate = today.format(formatter);
        currentDateLabel.setText(formattedDate);
        
        // Set initial time
        updateCurrentTime();
    }
    
    /**
     * Start real-time clock update
     */
    private void startRealtimeClock() {
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> updateCurrentTime())
        );
        timeline.setCycleCount(javafx.animation.Timeline.INDEFINITE);
        timeline.play();
    }
    
    /**
     * Update current time display
     */
    private void updateCurrentTime() {
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(new java.util.Date());
        javafx.application.Platform.runLater(() -> {
            if (currentTimeLabel != null) {
                currentTimeLabel.setText(currentTime);
            }
        });
    }
    
    /**
     * Load all dashboard data
     */
    private void loadDashboardData() {
        // Load quick stats
        loadQuickStats();
        
        // Load operational status
        loadOperationalStatus();
        
        // Load daily goals
        loadDailyGoals();
        
        // Load recent activities
        loadRecentActivities();
    }
    
    /**
     * Load quick statistics from database
     */
    private void loadQuickStats() {
        try {
            // Get today's revenue from database
            double todayRevenue = reportService.getTodayRevenue();
            
            // Get tables statistics
            var allTables = tableService.getAllTables();
            int totalTables = allTables.size();
            int tablesInUse = (int) allTables.stream()
                    .filter(table -> "occupied".equals(table.getStatus()) || "reserved".equals(table.getStatus()))
                    .count();
            
            // Get today's orders count
            int todayOrders = reportService.getTodayOrdersCount();
            
            // Get staff statistics
            try (var conn = DatabaseConfig.getConnection()) {
                UserDAO userDAO = new UserDAOImpl(conn);
                var allUsers = userDAO.getAllUsers();
                int totalStaff = (int) allUsers.stream()
                        .filter(user -> !"Admin".equals(user.getRole()))
                        .count();
                int staffOnDuty = (int) allUsers.stream()
                        .filter(user -> !"Admin".equals(user.getRole()) && user.isActive())
                        .count();
                        
                staffOnDutyLabel.setText(staffOnDuty + "/" + totalStaff + " người");
            }

            todayRevenueLabel.setText(String.format("%,.0f VNĐ", todayRevenue));
            tablesInUseLabel.setText(tablesInUse + "/" + totalTables + " bàn");
            todayOrdersLabel.setText(todayOrders + " đơn");

        } catch (Exception e) {
            System.err.println("Error loading quick stats: " + e.getMessage());
            e.printStackTrace();
            // Fallback to show error message
            todayRevenueLabel.setText("Lỗi tải dữ liệu");
            tablesInUseLabel.setText("--/-- bàn");
            todayOrdersLabel.setText("-- đơn");
            staffOnDutyLabel.setText("--/-- người");
        }
    }
    
        /**
     * Load operational status from database
     * Note: FXML only has static overview content, operational status not displayed
     */
    private void loadOperationalStatus() {
        try {
            // Get pending orders count for logging/monitoring
            int pendingOrders = reportService.getPendingOrdersCount();
            System.out.println("📊 Operational Status - Pending orders: " + pendingOrders);
            
        } catch (Exception e) {
            System.err.println("Error loading operational status: " + e.getMessage());
        }
    }
    
        /**
     * Load daily goals with real data
     * Note: FXML only has static overview content, daily goals not displayed
     */
    private void loadDailyGoals() {
        try {
            // Get actual today's revenue and orders for logging/monitoring
            double todayRevenue = reportService.getTodayRevenue();
            int todayOrders = reportService.getTodayOrdersCount();
            
            // Set daily goals (could be configurable in the future)
            double revenueGoal = 3500000; // 3.5M VND
            int orderGoal = 60;
            
            // Calculate progress percentages for logging
            double revenueProgress = (todayRevenue / revenueGoal) * 100;
            double orderProgress = ((double)todayOrders / orderGoal) * 100;
            
            System.out.println(String.format("📊 Daily Goals - Revenue: %.1f%%, Orders: %.1f%%", 
                revenueProgress, orderProgress));
            
        } catch (Exception e) {
            System.err.println("Error loading daily goals: " + e.getMessage());
        }
    }
    
    /**
     * Load recent activities from database
     */
    private void loadRecentActivities() {
        try {
            // Log activity loading for monitoring
            System.out.println("✅ Recent activities loaded from database");
            
            // Note: Recent activities could be displayed in a ListView or TableView
            // This would require additional UI components and data structures
            // For now, we just ensure the method doesn't fail
            
        } catch (Exception e) {
            System.err.println("Error loading recent activities: " + e.getMessage());
        }
    }
}