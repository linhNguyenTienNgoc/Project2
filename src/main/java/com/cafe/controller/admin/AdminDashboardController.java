package com.cafe.controller.admin;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.controller.menu.MenuController;
import com.cafe.controller.table.TableController;
import com.cafe.model.enums.TableStatus;
import com.cafe.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
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
public class AdminDashboardController extends DashboardController implements Initializable {

    @FXML private BorderPane dashboardContainer;
    @FXML private HBox headerBar;
    @FXML private HBox tabNavigation;
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
    
    // Dashboard components
    @FXML private Label currentDateLabel;
    @FXML private Label currentTimeLabel;
    @FXML private Label cafeStatusLabel;
    @FXML private Label todayRevenueLabel;
    @FXML private Label tablesInUseLabel;
    @FXML private Label todayOrdersLabel;
    @FXML private Label staffOnDutyLabel;
    @FXML private Label kitchenStatusLabel;
    @FXML private Label pendingOrdersLabel;
    @FXML private Label avgPrepTimeLabel;
    @FXML private Label serviceRatingLabel;
    @FXML private Label customerWaitLabel;
    @FXML private Label satisfactionLabel;
    @FXML private Label inventoryStatusLabel;
    @FXML private Label revenueGoalLabel;
    @FXML private Label orderGoalLabel;
    @FXML private Label customerGoalLabel;

    // Order panel
    @FXML private VBox orderPanel;
    @FXML private Label tableInfoLabel;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalAmountLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button paymentButton;
    @FXML private Button clearOrderButton;

    // Current state
    private String currentTab = "overview";
    private Node currentContent;
    private Map<String, Node> loadedContent = new HashMap<>();
    private Map<String, Object> loadedControllers = new HashMap<>();

    // Order state
    private double totalAmount = 0.0;
    private String currentTableName = "--";
    private TableStatus currentTableStatus = TableStatus.AVAILABLE;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Verify FXML injection
            verifyFXMLInjection();

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
     * Verify that all required FXML elements are properly injected
     */
    private void verifyFXMLInjection() {
        StringBuilder missingElements = new StringBuilder();

        if (dashboardContainer == null) missingElements.append("dashboardContainer, ");
        if (headerBar == null) missingElements.append("headerBar, ");
        if (tabNavigation == null) missingElements.append("tabNavigation, ");
        if (userNameLabel == null) missingElements.append("userNameLabel, ");
        if (userRoleLabel == null) missingElements.append("userRoleLabel, ");
        if (logoutButton == null) missingElements.append("logoutButton, ");
        if (userTabButton == null) missingElements.append("userTabButton, ");
        if (menuTabButton == null) missingElements.append("menuTabButton, ");
        if (tableTabButton == null) missingElements.append("tableTabButton, ");
        if (reportTabButton == null) missingElements.append("reportTabButton, ");
        if (promotionTabButton == null) missingElements.append("promotionTabButton, ");
        if (contentPane == null) missingElements.append("contentPane, ");
        if (orderPanel == null) missingElements.append("orderPanel, ");
        if (tableInfoLabel == null) missingElements.append("tableInfoLabel, ");
        if (orderItemsContainer == null) missingElements.append("orderItemsContainer, ");
        if (totalAmountLabel == null) missingElements.append("totalAmountLabel, ");
        if (placeOrderButton == null) missingElements.append("placeOrderButton, ");
        if (paymentButton == null) missingElements.append("paymentButton, ");
        if (clearOrderButton == null) missingElements.append("clearOrderButton, ");

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
                userNameLabel.setText(SessionManager.getCurrentUserFullName());
                userRoleLabel.setText(SessionManager.getCurrentUserRole());
            } else {
                userNameLabel.setText("Admin User");
                userRoleLabel.setText("Administrator");
            }
        } catch (Exception e) {
            System.err.println("Error setting up user info: " + e.getMessage());
            userNameLabel.setText("Admin User");
            userRoleLabel.setText("Administrator");
        }
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

        // Logout handler
        if (logoutButton != null) logoutButton.setOnAction(e -> logout());

        // Order panel handlers - với null check
        if (placeOrderButton != null) placeOrderButton.setOnAction(e -> placeOrder());
        if (paymentButton != null) paymentButton.setOnAction(e -> processPayment());
        if (clearOrderButton != null) clearOrderButton.setOnAction(e -> clearOrder());
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
        }
    }

    /**
     * Set active tab button
     */
    private void setActiveTabButton(Button activeButton) {
        // Reset all buttons
        if (overviewTabButton != null) overviewTabButton.getStyleClass().setAll("nav-button");
        if (userTabButton != null) userTabButton.getStyleClass().setAll("nav-button");
        if (menuTabButton != null) menuTabButton.getStyleClass().setAll("nav-button");
        if (tableTabButton != null) tableTabButton.getStyleClass().setAll("nav-button");
        if (customerTabButton != null) customerTabButton.getStyleClass().setAll("nav-button");
        if (reportTabButton != null) reportTabButton.getStyleClass().setAll("nav-button");
        if (promotionTabButton != null) promotionTabButton.getStyleClass().setAll("nav-button");

        // Set active button
        activeButton.getStyleClass().setAll("nav-button", "active");
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
            currentContent = content;

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
                fxmlPath = "/fxml/admin/report-layout.fxml";
                break;
            case "promotion":
                fxmlPath = "/fxml/admin/promotion-layout.fxml";
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
     * Update table information in order panel
     */
    @Override
    public void updateTableInfo(String tableName, String status) {
        currentTableName = tableName;
        // Convert string status to TableStatus enum
        try {
            currentTableStatus = TableStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            currentTableStatus = TableStatus.AVAILABLE; // Default fallback
        }

        if (tableInfoLabel != null) {
            tableInfoLabel.setText("Bàn: " + tableName);
        }

        // Clear order when switching tables
        clearOrder();
    }

    /**
     * Add item to order
     */
    @Override
    public void addToOrder(String productName, double price, int quantity) {
        // Remove placeholder if exists
        if (orderItemsContainer.getChildren().size() == 1 &&
                orderItemsContainer.getChildren().get(0) instanceof Label) {
            orderItemsContainer.getChildren().clear();
        }

        // Create order item row
        HBox orderItem = createOrderItemRow(productName, price, quantity);
        orderItemsContainer.getChildren().add(orderItem);

        // Update total
        totalAmount += price * quantity;
        updateTotalAmount();
    }

    /**
     * Create order item row
     */
    private HBox createOrderItemRow(String productName, double price, int quantity) {
        HBox row = new HBox(8);
        row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        row.getStyleClass().add("order-item-row");

        Label nameLabel = new Label(productName);
        nameLabel.getStyleClass().add("order-item-name");

        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.getStyleClass().add("order-item-quantity");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", price * quantity));
        priceLabel.getStyleClass().add("order-item-price");

        Button removeButton = new Button("×");
        removeButton.getStyleClass().add("order-item-remove-btn");
        removeButton.setOnAction(e -> removeOrderItem(row, price * quantity));

        row.getChildren().addAll(nameLabel, quantityLabel, priceLabel, removeButton);

        return row;
    }

    /**
     * Remove order item
     */
    private void removeOrderItem(HBox itemRow, double itemTotal) {
        orderItemsContainer.getChildren().remove(itemRow);
        totalAmount -= itemTotal;
        updateTotalAmount();

        // Show placeholder if no items
        if (orderItemsContainer.getChildren().isEmpty()) {
            Label placeholder = new Label("Chưa có món nào được chọn");
            placeholder.getStyleClass().add("placeholder-text");
            orderItemsContainer.getChildren().add(placeholder);
        }
    }

    /**
     * Update total amount display
     */
    private void updateTotalAmount() {
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
        }
    }

    /**
     * Place order
     */
    private void placeOrder() {
        if (currentTableName.equals("--")) {
            showError("Vui lòng chọn bàn trước khi đặt món");
            return;
        }

        if (orderItemsContainer.getChildren().isEmpty() ||
                (orderItemsContainer.getChildren().size() == 1 &&
                        orderItemsContainer.getChildren().get(0) instanceof Label)) {
            showError("Vui lòng chọn món trước khi đặt");
            return;
        }

        // TODO: Implement order placement logic
        CafeManagementApplication.showSuccessAlert("Thành công", "Đã đặt món cho " + currentTableName);
    }

    /**
     * Process payment
     */
    private void processPayment() {
        if (totalAmount <= 0) {
            showError("Không có món nào để thanh toán");
            return;
        }

        // TODO: Implement payment logic
        CafeManagementApplication.showSuccessAlert("Thành công", "Đã thanh toán thành công");
        clearOrder();
    }

    /**
     * Clear order
     */
    @Override
    public void clearOrder() {
        orderItemsContainer.getChildren().clear();
        totalAmount = 0.0;
        updateTotalAmount();

        // Add placeholder
        Label placeholder = new Label("Chưa có món nào được chọn");
        placeholder.getStyleClass().add("placeholder-text");
        orderItemsContainer.getChildren().add(placeholder);
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

    /**
     * Get current table name
     */
    public String getCurrentTableName() {
        return currentTableName;
    }

    /**
     * Get current table status
     */
    public TableStatus getCurrentTableStatus() {
        return currentTableStatus;
    }

    // =====================================================
    // DASHBOARD EVENT HANDLER IMPLEMENTATIONS
    // =====================================================

    /**
     * Add product to order (enhanced method)
     */
    @Override
    public void addProductToOrder(com.cafe.model.entity.Product product, int quantity) {
        addToOrder(product.getProductName(), product.getPrice(), quantity);
    }

    /**
     * Handle table selection
     */
    @Override
    public void onTableSelected(com.cafe.model.entity.TableCafe table) {
        currentTableName = table.getTableName();
        // Convert string status to TableStatus enum
        try {
            currentTableStatus = TableStatus.valueOf(table.getStatus().toUpperCase());
        } catch (IllegalArgumentException e) {
            currentTableStatus = TableStatus.AVAILABLE; // Default fallback
        }
        updateTableInfo(table.getTableName(), table.getStatus());
        System.out.println("✅ Table selected: " + table.getTableName());
    }

    /**
     * Get current order total
     */
    @Override
    public double getOrderTotal() {
        return totalAmount;
    }

    /**
     * Notify when order status changes
     */
    @Override
    public void onOrderStatusChanged(String newStatus, int tableId) {
        System.out.println("✅ Order status changed: " + newStatus + " for table " + tableId);
        // TODO: Implement order status change handling
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
     * Load quick statistics
     */
    private void loadQuickStats() {
        try {
            // TODO: Replace with actual database queries
            double todayRevenue = 2450000;
            int tablesInUse = 8;
            int totalTables = 12;
            int todayOrders = 47;
            int staffOnDuty = 6;
            int totalStaff = 8;

            todayRevenueLabel.setText(String.format("%,.0f VNĐ", todayRevenue));
            tablesInUseLabel.setText(tablesInUse + "/" + totalTables + " bàn");
            todayOrdersLabel.setText(todayOrders + " đơn");
            staffOnDutyLabel.setText(staffOnDuty + "/" + totalStaff + " người");

        } catch (Exception e) {
            System.err.println("Error loading quick stats: " + e.getMessage());
        }
    }
    
    /**
     * Load operational status
     */
    private void loadOperationalStatus() {
        // Kitchen status
        kitchenStatusLabel.setText("✅ Hoạt động bình thường");
        pendingOrdersLabel.setText("📝 3 đơn hàng đang chờ");
        avgPrepTimeLabel.setText("⏱️ Thời gian chuẩn bị TB: 12 phút");

        // Service quality
        serviceRatingLabel.setText("⭐ Đánh giá TB: 4.8/5.0");
        customerWaitLabel.setText("⏰ Thời gian chờ TB: 8 phút");
        satisfactionLabel.setText("😊 Mức độ hài lòng: 94%");

        // Inventory
        inventoryStatusLabel.setText("⚠️ 3 mặt hàng sắp hết");
    }
    
    /**
     * Load daily goals
     */
    private void loadDailyGoals() {
        // Calculate progress percentages
        double revenueProgress = (2400000.0 / 3500000.0) * 100;
        double orderProgress = (47.0 / 60.0) * 100;
        double customerProgress = (125.0 / 150.0) * 100;

        revenueGoalLabel.setText(String.format("💰 Doanh thu: 2.4M/3.5M (%.0f%%)", revenueProgress));
        orderGoalLabel.setText(String.format("📋 Đơn hàng: 47/60 (%.0f%%)", orderProgress));
        customerGoalLabel.setText(String.format("👥 Khách hàng: 125/150 (%.0f%%)", customerProgress));

        // Apply color coding based on progress using CSS classes
        revenueGoalLabel.getStyleClass().clear();
        if (revenueProgress >= 80) {
            revenueGoalLabel.getStyleClass().addAll("progress-indicator", "progress-high");
        } else if (revenueProgress >= 60) {
            revenueGoalLabel.getStyleClass().addAll("progress-indicator", "progress-medium");
        } else {
            revenueGoalLabel.getStyleClass().addAll("progress-indicator", "progress-low");
        }
    }
    
    /**
     * Load recent activities
     */
    private void loadRecentActivities() {
        try {
            // TODO: Replace with actual database query
            // For now, we'll use placeholder data
            System.out.println("✅ Recent activities loaded");
        } catch (Exception e) {
            System.err.println("Error loading recent activities: " + e.getMessage());
        }
    }
}