package com.cafe.controller.dashboard;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.menu.MenuController;
import com.cafe.controller.order.OrderPanelController;
import com.cafe.controller.table.TableController;
import com.cafe.model.enums.TableStatus;
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
 * Controller cho màn hình dashboard chính
 * Quản lý navigation và hiển thị các modules khác nhau
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class DashboardController implements Initializable {
    
    @FXML private BorderPane dashboardContainer;
    @FXML private HBox headerBar;
    @FXML private HBox tabNavigation;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;
    
    // Tab buttons
    @FXML private Button menuTabButton;
    @FXML private Button tableTabButton;
    
    // Content area
    @FXML private StackPane contentPane;
    
    // Order panel (loaded from FXML)
    @FXML private VBox orderPanelRoot;
    
    // Order panel controller (will be set from FXML)
    private OrderPanelController orderPanelController;
    
    // Current state
    private String currentTab = "menu";
    private Map<String, Node> loadedContent = new HashMap<>();
    private Map<String, Object> loadedControllers = new HashMap<>();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Verify FXML injection
            verifyFXMLInjection();
            
            setupUserInfo();
            setupTabNavigation();
            setupEventHandlers();
            
            // AUTO-SETUP OrderPanelController to enable order functionality
            setupOrderPanelController();
            
            // Load default content (Menu)
            loadTabContent("menu");
            
            System.out.println("✅ DashboardController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing DashboardController: " + e.getMessage());
            e.printStackTrace();
            // Show user-friendly error message
            showError("Lỗi khởi tạo Dashboard: " + e.getMessage());
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
        if (menuTabButton == null) missingElements.append("menuTabButton, ");
        if (tableTabButton == null) missingElements.append("tableTabButton, ");
        if (contentPane == null) missingElements.append("contentPane, ");
        
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
            // TODO: Get user info from session
            userNameLabel.setText("Test");
            userRoleLabel.setText("Waiter");
        } catch (Exception e) {
            System.err.println("Error setting up user info: " + e.getMessage());
        }
    }
    
    /**
     * Setup tab navigation
     */
    private void setupTabNavigation() {
        // Set initial active tab
        setActiveTabButton(menuTabButton);
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Tab button handlers
        menuTabButton.setOnAction(e -> switchToTab("menu"));
        tableTabButton.setOnAction(e -> switchToTab("table"));
        
        // Logout handler
        logoutButton.setOnAction(e -> logout());
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
        menuTabButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;");
        tableTabButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;");
        
        // Set active tab button
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;";
        
        switch (currentTab) {
            case "menu":
                menuTabButton.setStyle(activeStyle);
                break;
            case "table":
                tableTabButton.setStyle(activeStyle);
                break;
        }
    }
    
    /**
     * Set active tab button
     */
    private void setActiveTabButton(Button activeButton) {
        // Reset all buttons
        menuTabButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;");
        tableTabButton.setStyle("-fx-background-color: #A0522D; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;");
        
        // Set active button
        activeButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120;");
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
            case "menu":
                fxmlPath = "/fxml/dashboard/menu-layout.fxml";
                break;
            case "table":
                fxmlPath = "/fxml/dashboard/table-layout.fxml";
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
     * AUTO-SETUP OrderPanelController to enable order functionality
     * This method is called automatically during DashboardController initialization
     */
    private void setupOrderPanelController() {
        try {
            System.out.println("🔧 DashboardController: Auto-setting up OrderPanelController...");
            
            // Get OrderPanelController from FXML (with full UI elements)
            if (orderPanelRoot != null) {
                // The controller is already created by FXML loader
                // We need to get it from the FXMLLoader that loaded the included FXML
                System.out.println("🔍 DashboardController: orderPanelRoot found, getting controller from FXML...");
                
                // Get the controller from the included FXML
                // This is a bit tricky with fx:include, but we can access it
                this.orderPanelController = getOrderPanelControllerFromFXML();
                
                if (this.orderPanelController != null) {
                    System.out.println("✅ DashboardController: OrderPanelController loaded from FXML successfully");
                    System.out.println("🔗 Order functionality is now ENABLED (with full UI)");
                    System.out.println("🎯 UI mode: Full UI Mode (orderItemsContainer available)");
                } else {
                    System.out.println("⚠️ DashboardController: Could not get controller from FXML, creating new instance");
                    // Fallback: create new instance
                    this.orderPanelController = new OrderPanelController();
                    this.orderPanelController.initializeServices();
                    System.out.println("🔗 Order functionality is now ENABLED (logic-only mode)");
                }
            } else {
                System.out.println("⚠️ DashboardController: orderPanelRoot is null, creating new instance");
                // Fallback: create new instance
                this.orderPanelController = new OrderPanelController();
                this.orderPanelController.initializeServices();
                System.out.println("🔗 Order functionality is now ENABLED (logic-only mode)");
            }
            
        } catch (Exception e) {
            System.err.println("❌ DashboardController: Error auto-setting up OrderPanelController: " + e.getMessage());
            e.printStackTrace();
            // Don't fail initialization, just disable order functionality
            this.orderPanelController = null;
        }
    }
    
    /**
     * Get OrderPanelController from the included FXML
     * This is a workaround to access the controller from fx:include
     */
    private OrderPanelController getOrderPanelControllerFromFXML() {
        try {
            System.out.println("🔍 DashboardController: Attempting to get controller from FXML...");
            
            // Load the order panel FXML manually to get the controller
            FXMLLoader loader = new FXMLLoader(CafeManagementApplication.class.getResource("/fxml/order/order_panel.fxml"));
            Node orderPanelNode = loader.load();
            OrderPanelController controller = loader.getController();
            
            if (controller != null) {
                System.out.println("✅ DashboardController: Successfully loaded OrderPanelController from FXML");
                System.out.println("🔍 DashboardController: Controller instance: " + controller.getClass().getSimpleName() + "@" + Integer.toHexString(controller.hashCode()));
                
                // Replace the existing orderPanelRoot with our loaded node
                if (orderPanelRoot != null && orderPanelRoot.getParent() != null) {
                    VBox parent = (VBox) orderPanelRoot.getParent();
                    int index = parent.getChildren().indexOf(orderPanelRoot);
                    if (index >= 0) {
                        parent.getChildren().set(index, orderPanelNode);
                        System.out.println("✅ DashboardController: Replaced orderPanelRoot with loaded FXML node");
                    }
                }
                
                return controller;
            } else {
                System.out.println("❌ DashboardController: Controller is null from FXML loader");
                return null;
            }
            
        } catch (Exception e) {
            System.err.println("❌ DashboardController: Error getting controller from FXML: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Set OrderPanelController reference (to be called externally if needed)
     * This avoids UI injection issues while enabling order functionality
     * 
     * IMPORTANT: This method is now optional since auto-setup is enabled
     * 
     * Usage:
     * OrderPanelController orderController = new OrderPanelController();
     * dashboardController.setOrderPanelController(orderController);
     */
    public void setOrderPanelController(OrderPanelController orderPanelController) {
        this.orderPanelController = orderPanelController;
        System.out.println("✅ DashboardController: OrderPanelController reference set manually (no UI injection)");
        System.out.println("🔗 Order functionality is now ENABLED");
    }
    
    /**
     * Setup communication between controllers
     */
    private void setupControllerCommunication(String tabName) {
        Object controller = loadedControllers.get(tabName);
        
        System.out.println("🔗 DashboardController: Setting up communication for tab: " + tabName);
        System.out.println("🔗 DashboardController: Controller type: " + (controller != null ? controller.getClass().getSimpleName() : "null"));
        
        if (controller instanceof TableController) {
            TableController tableController = (TableController) controller;
            tableController.setDashboardController(this);
            System.out.println("✅ DashboardController: TableController communication setup");
        }
        
        if (controller instanceof MenuController) {
            MenuController menuController = (MenuController) controller;
            menuController.setDashboardController(this);
            System.out.println("✅ DashboardController: MenuController communication setup");
        }
    }
    
    /**
     * Add item to order (restored functionality)
     */
    public void addToOrder(String productName, double price, int quantity) {
        System.out.println("🛒 DashboardController: Adding to order: " + productName + " x" + quantity);
        
        if (orderPanelController != null) {
            System.out.println("✅ DashboardController: OrderPanelController found, calling addToOrder");
            orderPanelController.addToOrder(productName, price, quantity);
        } else {
            System.err.println("❌ DashboardController: OrderPanelController is null!");
            System.err.println("💡 Hint: Call setOrderPanelController() to enable order functionality");
        }
    }
    
    /**
     * Update table information in order panel (restored functionality)
     */
    public void updateTableInfo(String tableName, TableStatus status) {
        System.out.println("🪑 DashboardController: Updating table info: " + tableName + " (" + status + ")");
        
        if (orderPanelController != null) {
            orderPanelController.updateTableInfo(tableName, status);
            System.out.println("✅ DashboardController: Table info updated in OrderPanel");
        } else {
            System.err.println("❌ DashboardController: OrderPanelController is null!");
            System.err.println("💡 Hint: Call setOrderPanelController() to enable table functionality");
        }
    }
    
    /**
     * Get current table name (restored functionality)
     */
    public String getCurrentTableName() {
        if (orderPanelController != null) {
            return orderPanelController.getCurrentTableName();
        } else {
            System.err.println("❌ DashboardController: OrderPanelController is null!");
            return "--";
        }
    }
    
    /**
     * Get current table status (restored functionality)
     */
    public TableStatus getCurrentTableStatus() {
        if (orderPanelController != null) {
            return orderPanelController.getCurrentTableStatus();
        } else {
            System.err.println("❌ DashboardController: OrderPanelController is null!");
            return TableStatus.AVAILABLE;
        }
    }
    
    /**
     * Debug method to check order functionality connection
     */
    public void debugOrderConnection() {
        System.out.println("\n🔍 ===== DASHBOARD ORDER CONNECTION DEBUG =====");
        
        // Basic status
        System.out.println("📊 DashboardController Status:");
        System.out.println("   - Class: " + this.getClass().getSimpleName());
        System.out.println("   - Instance: " + this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()));
        
        // Order panel connection
        System.out.println("\n🔗 Order Connection Status:");
        System.out.println("   - orderPanelController: " + (orderPanelController != null ? "✅ Connected" : "❌ NULL"));
        
        if (orderPanelController != null) {
            System.out.println("   - OrderPanel instance: " + orderPanelController.getClass().getSimpleName() + "@" + Integer.toHexString(orderPanelController.hashCode()));
            System.out.println("   - Order functionality: ✅ ENABLED");
            
            // Test order panel connection
            System.out.println("\n🧪 Testing Order Panel Connection:");
            try {
                orderPanelController.debugConnectionStatus();
            } catch (Exception e) {
                System.err.println("❌ Error testing order panel: " + e.getMessage());
            }
        } else {
            System.out.println("   - Order functionality: ❌ DISABLED");
            System.out.println("💡 To enable: Call setupOrderPanelController() or setOrderPanelController()");
        }
        
        // Controller connections
        System.out.println("\n🔗 Controller Connections:");
        System.out.println("   - Loaded controllers: " + loadedControllers.size());
        for (String key : loadedControllers.keySet()) {
            Object controller = loadedControllers.get(key);
            System.out.println("   - " + key + ": " + (controller != null ? controller.getClass().getSimpleName() : "null"));
        }
        
        System.out.println("\n🎯 Overall Order Status: " + (orderPanelController != null ? "✅ FUNCTIONAL" : "❌ NEEDS SETUP"));
        System.out.println("===== DEBUG COMPLETE =====\n");
    }
    
    /**
     * Logout
     */
    private void logout() {
        if (CafeManagementApplication.showConfirmAlert("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất không?")) {
            CafeManagementApplication.showLoginScreen();
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        CafeManagementApplication.showErrorAlert("Lỗi", message);
    }
    

}
