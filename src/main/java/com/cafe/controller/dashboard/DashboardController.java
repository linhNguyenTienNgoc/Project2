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
import javafx.scene.Parent;

import java.io.IOException;
import java.lang.reflect.Field;
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
    
    // Order panel root node
    @FXML private javafx.scene.layout.VBox orderPanelRoot;
    
    // Order panel controller (will be set manually)
    private OrderPanelController orderPanelController;
    
    // Current state
    private String currentTab = "menu";
    private Node currentContent;
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
            
            // Setup order panel controller
            setupOrderPanelController();
            
            // Load default content (Menu)
            loadTabContent("menu");
            
            System.out.println("✅ DashboardController initialized successfully");
            System.out.println("🔗 OrderPanelController reference: " + (orderPanelController != null ? "✅ Connected" : "❌ NULL"));
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
        if (orderPanelRoot == null) missingElements.append("orderPanelRoot, ");
        
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
     * Setup order panel controller
     */
    private void setupOrderPanelController() {
        try {
            System.out.println("🔍 DashboardController: Setting up OrderPanelController...");
            
            if (orderPanelRoot != null) {
                // Try to get the controller from the FXML loader
                FXMLLoader loader = (FXMLLoader) orderPanelRoot.getUserData();
                if (loader != null) {
                    orderPanelController = loader.getController();
                    System.out.println("✅ DashboardController: Got OrderPanelController from FXML loader");
                } else {
                    // Fallback: create new instance and manually inject UI elements
                    orderPanelController = new OrderPanelController();
                    orderPanelController.initializeServices();
                    
                    // Manually inject UI elements using reflection
                    injectUIElements();
                    System.out.println("✅ DashboardController: Created new OrderPanelController and injected UI elements");
                }
            } else {
                // Create new instance without UI
                orderPanelController = new OrderPanelController();
                orderPanelController.initializeServices();
                System.out.println("⚠️ DashboardController: orderPanelRoot is null, created controller without UI");
            }
        } catch (Exception e) {
            System.err.println("❌ DashboardController: Error setting up OrderPanelController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Manually inject UI elements into OrderPanelController
     */
    private void injectUIElements() {
        try {
            if (orderPanelController == null || orderPanelRoot == null) {
                System.err.println("❌ Cannot inject UI elements - controller or root is null");
                return;
            }
            
            System.out.println("🔧 DashboardController: Manually injecting UI elements...");
            
            // Debug UI structure
            System.out.println("🔍 Debugging UI structure:");
            debugUIStructure(orderPanelRoot, 0);
            
            // Try to find orderItemsContainer in ScrollPane content
            if (orderPanelRoot.getChildren().size() > 1) {
                Node scrollPaneNode = orderPanelRoot.getChildren().get(1); // ScrollPane is at index 1
                if (scrollPaneNode instanceof javafx.scene.control.ScrollPane) {
                    javafx.scene.control.ScrollPane scrollPane = (javafx.scene.control.ScrollPane) scrollPaneNode;
                    Node content = scrollPane.getContent();
                    System.out.println("🔍 ScrollPane content: " + (content != null ? content.getClass().getSimpleName() : "null"));
                    if (content instanceof VBox) {
                        VBox orderItemsContainer = (VBox) content;
                        setFieldValue(orderPanelController, "orderItemsContainer", orderItemsContainer);
                        System.out.println("✅ Injected orderItemsContainer from ScrollPane content");
                    } else {
                        System.err.println("❌ ScrollPane content is not VBox: " + (content != null ? content.getClass().getSimpleName() : "null"));
                    }
                } else {
                    System.err.println("❌ Node at index 1 is not ScrollPane: " + scrollPaneNode.getClass().getSimpleName());
                }
            } else {
                System.err.println("❌ orderPanelRoot has less than 2 children: " + orderPanelRoot.getChildren().size());
            }
            
            // Find and inject totalAmountLabel
            Label totalAmountLabel = findNodeById(orderPanelRoot, "totalAmountLabel");
            if (totalAmountLabel != null) {
                setFieldValue(orderPanelController, "totalAmountLabel", totalAmountLabel);
                System.out.println("✅ Injected totalAmountLabel");
            }
            
            // Find and inject tableInfoLabel
            Label tableInfoLabel = findNodeById(orderPanelRoot, "tableInfoLabel");
            if (tableInfoLabel != null) {
                setFieldValue(orderPanelController, "tableInfoLabel", tableInfoLabel);
                System.out.println("✅ Injected tableInfoLabel");
            }
            
            // Find and inject buttons
            Button placeOrderButton = findNodeById(orderPanelRoot, "placeOrderButton");
            if (placeOrderButton != null) {
                setFieldValue(orderPanelController, "placeOrderButton", placeOrderButton);
                System.out.println("✅ Injected placeOrderButton");
            }
            
            Button paymentButton = findNodeById(orderPanelRoot, "paymentButton");
            if (paymentButton != null) {
                setFieldValue(orderPanelController, "paymentButton", paymentButton);
                System.out.println("✅ Injected paymentButton");
            }
            
            Button clearOrderButton = findNodeById(orderPanelRoot, "clearOrderButton");
            if (clearOrderButton != null) {
                setFieldValue(orderPanelController, "clearOrderButton", clearOrderButton);
                System.out.println("✅ Injected clearOrderButton");
            }
            
            // Call initialize on the controller
            orderPanelController.verifyFXMLInjection();
            
            // Force refresh UI if we have items
            if (orderPanelController.getOrderItemsCount() > 0) {
                System.out.println("🔄 Force refreshing UI after injection...");
                orderPanelController.refreshUIDisplay();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error injecting UI elements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Find a node by its fx:id in the scene graph
     */
    private <T extends Node> T findNodeById(Node root, String id) {
        System.out.println("🔍 Searching for node with id: " + id);
        System.out.println("🔍 Current node: " + root.getClass().getSimpleName() + " (id: " + root.getId() + ")");
        
        if (root.getId() != null && root.getId().equals(id)) {
            System.out.println("✅ Found node: " + id);
            return (T) root;
        }
        
        if (root instanceof Parent) {
            System.out.println("🔍 Checking children of: " + root.getClass().getSimpleName());
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                T result = findNodeById(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        
        System.out.println("❌ Node not found: " + id);
        return null;
    }
    
    /**
     * Set field value using reflection
     */
    private void setFieldValue(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            System.err.println("❌ Error setting field " + fieldName + ": " + e.getMessage());
        }
    }
    
    /**
     * Debug UI structure
     */
    private void debugUIStructure(Node root, int depth) {
        String indent = "  ".repeat(depth);
        System.out.println(indent + "📁 " + root.getClass().getSimpleName() + " (id: " + root.getId() + ")");
        
        if (root instanceof Parent) {
            for (Node child : ((Parent) root).getChildrenUnmodifiable()) {
                debugUIStructure(child, depth + 1);
            }
        }
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
     * Update table information in order panel
     */
    public void updateTableInfo(String tableName, TableStatus status) {
        if (orderPanelController != null) {
            orderPanelController.updateTableInfo(tableName, status);
        }
    }
    
    /**
     * Add item to order
     */
    public void addToOrder(String productName, double price, int quantity) {
        System.out.println("🛒 DashboardController: Adding to order: " + productName + " x" + quantity);
        
        if (orderPanelController != null) {
            System.out.println("✅ DashboardController: OrderPanelController found, calling addToOrder");
            orderPanelController.addToOrder(productName, price, quantity);
        } else {
            System.err.println("❌ DashboardController: OrderPanelController is null!");
        }
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
    
    /**
     * Get current table name
     */
    public String getCurrentTableName() {
        return orderPanelController != null ? orderPanelController.getCurrentTableName() : "--";
    }
    
    /**
     * Get current table status
     */
    public TableStatus getCurrentTableStatus() {
        return orderPanelController != null ? orderPanelController.getCurrentTableStatus() : TableStatus.AVAILABLE;
    }
}
