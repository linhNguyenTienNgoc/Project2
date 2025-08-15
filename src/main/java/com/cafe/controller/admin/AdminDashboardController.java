package com.cafe.controller.admin;

import com.cafe.CafeManagementApplication;
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
    @FXML private Button userTabButton;
    @FXML private Button menuTabButton;
    @FXML private Button tableTabButton;
    @FXML private Button reportTabButton;
    @FXML private Button promotionTabButton;

    // Content area
    @FXML private StackPane contentPane;

    // Order panel
    @FXML private VBox orderPanel;
    @FXML private Label tableInfoLabel;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalAmountLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button paymentButton;
    @FXML private Button clearOrderButton;

    // Current state
    private String currentTab = "user";
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

            // Load default content (User Management)
            loadTabContent("user");

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
        // Set initial active tab
        setActiveTabButton(userTabButton);
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Tab button handlers
        userTabButton.setOnAction(e -> switchToTab("user"));
        menuTabButton.setOnAction(e -> switchToTab("menu"));
        tableTabButton.setOnAction(e -> switchToTab("table"));
        reportTabButton.setOnAction(e -> switchToTab("report"));
        promotionTabButton.setOnAction(e -> switchToTab("promotion"));

        // Logout handler
        logoutButton.setOnAction(e -> logout());

        // Order panel handlers
        placeOrderButton.setOnAction(e -> placeOrder());
        paymentButton.setOnAction(e -> processPayment());
        clearOrderButton.setOnAction(e -> clearOrder());
    }

    /**
     * Switch to a specific tab
     */
    private void switchToTab(String tabName) {
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
        String defaultStyle = "-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;";
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;";

        userTabButton.setStyle(defaultStyle);
        menuTabButton.setStyle(defaultStyle);
        tableTabButton.setStyle(defaultStyle);
        reportTabButton.setStyle(defaultStyle);
        promotionTabButton.setStyle(defaultStyle);

        // Set active tab button
        switch (currentTab) {
            case "user":
                userTabButton.setStyle(activeStyle);
                break;
            case "menu":
                menuTabButton.setStyle(activeStyle);
                break;
            case "table":
                tableTabButton.setStyle(activeStyle);
                break;
            case "report":
                reportTabButton.setStyle(activeStyle);
                break;
            case "promotion":
                promotionTabButton.setStyle(activeStyle);
                break;
        }
    }

    /**
     * Set active tab button
     */
    private void setActiveTabButton(Button activeButton) {
        // Reset all buttons
        String defaultStyle = "-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;";
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;";

        userTabButton.setStyle(defaultStyle);
        menuTabButton.setStyle(defaultStyle);
        tableTabButton.setStyle(defaultStyle);
        reportTabButton.setStyle(defaultStyle);
        promotionTabButton.setStyle(defaultStyle);

        // Set active button
        activeButton.setStyle(activeStyle);
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
            case "user":
                fxmlPath = "/fxml/admin/user-layout.fxml";
                break;
            case "menu":
                fxmlPath = "/fxml/admin/menu-layout.fxml";
                break;
            case "table":
                fxmlPath = "/fxml/admin/table-layout.fxml";
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

        if (controller instanceof TableController) {
            TableController tableController = (TableController) controller;
            tableController.setDashboardController(this);
        }

        if (controller instanceof MenuController) {
            MenuController menuController = (MenuController) controller;
            menuController.setDashboardController(this);
        }
    }

    /**
     * Update table information in order panel
     */
    public void updateTableInfo(String tableName, TableStatus status) {
        currentTableName = tableName;
        currentTableStatus = status;

        if (tableInfoLabel != null) {
            tableInfoLabel.setText("Bàn: " + tableName);
        }

        // Clear order when switching tables
        clearOrder();
    }

    /**
     * Add item to order
     */
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
        row.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 4;");

        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-pref-width: 100;");

        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.setStyle("-fx-font-size: 11px; -fx-alignment: center; -fx-pref-width: 30;");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", price * quantity));
        priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #E67E22; -fx-font-weight: bold; -fx-pref-width: 80;");

        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 2 6; -fx-background-radius: 3;");
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
            placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-alignment: center;");
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
    private void clearOrder() {
        orderItemsContainer.getChildren().clear();
        totalAmount = 0.0;
        updateTotalAmount();

        // Add placeholder
        Label placeholder = new Label("Chưa có món nào được chọn");
        placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-alignment: center;");
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
}