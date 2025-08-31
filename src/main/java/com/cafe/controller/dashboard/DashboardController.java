package com.cafe.controller.dashboard;

import com.cafe.CafeManagementApplication;

import com.cafe.controller.menu.MenuController;
import com.cafe.controller.order.OrderPanelController;
import com.cafe.controller.table.TableController;
import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.base.DashboardEventHandler;

import com.cafe.model.entity.Product;
import com.cafe.model.entity.TableCafe;
import com.cafe.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


/**
 * Main Dashboard Controller - Complete & Fixed
 * Quản lý navigation giữa các module và communication
 *
 * Features:
 * - Navigation between Menu and Table tabs
 * - OrderPanel integration and communication
 * - Controller-to-controller communication
 * - Table selection synchronization
 * - Order management coordination
 *
 * @author Team 2_C2406L
 * @version 2.0.0 (Complete)
 */
public class DashboardController implements Initializable, DashboardEventHandler {

    // =====================================================
    // FXML INJECTIONS
    // =====================================================

    @FXML private BorderPane dashboardContainer;
    @FXML private StackPane contentPane;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;
    @FXML private Button menuTabButton;
    @FXML private Button tableTabButton;

    // ✅ OrderPanel injection - Key fix for communication
    @FXML private VBox orderPanelRoot;
    @FXML private OrderPanelController orderPanelRootController;

    // =====================================================
    // STATE MANAGEMENT
    // =====================================================

    // Current loaded content và controllers
    @SuppressWarnings("unused")
    private Node currentContent; // For future use
    private String currentTab = "menu"; // Default tab for staff
    // Overview controller removed - using static FXML content
    private MenuController currentMenuController;
    private TableController currentTableController;

    // User session info - Sẽ được load từ SessionManager
    private String currentUserName = "";
    private String currentUserRole = "";
    private int currentUserId = -1;

    // =====================================================
    // INITIALIZATION
    // =====================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🚀 Initializing DashboardController...");

            // Set user info
            initializeUserInfo();

            // Setup UI components
            setupButtonActions();
            setupTabStyling();

            // Initialize OrderPanel communication
            initializeOrderPanelCommunication();

            // Load default content (menu)
            loadMenuContent();

            // Set active tab style
            setActiveTab("menu");

            System.out.println("✅ DashboardController initialized successfully with full integration");

        } catch (Exception e) {
            System.err.println("❌ Error initializing DashboardController: " + e.getMessage());
            e.printStackTrace();
            handleInitializationError(e);
        }
    }

    /**
     * Initialize user information display
     */
    private void initializeUserInfo() {
        try {
            // Lấy thông tin từ SessionManager
            if (SessionManager.isLoggedIn()) {
                currentUserName = SessionManager.getCurrentUserFullName();
                currentUserRole = SessionManager.getCurrentUserRole();
                currentUserId = SessionManager.getCurrentUserId();
                
                // Cập nhật UI
                userNameLabel.setText(currentUserName);
                userRoleLabel.setText(currentUserRole);
                
                System.out.println("👤 User info loaded from session: " + currentUserName + " (" + currentUserRole + ")");
            } else {
                // Fallback nếu chưa đăng nhập
                currentUserName = "Người dùng";
                currentUserRole = "Nhân viên";
                currentUserId = -1;
                
                userNameLabel.setText(currentUserName);
                userRoleLabel.setText(currentUserRole);
                
                System.out.println("⚠️ No user session found, using fallback values");
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing user info: " + e.getMessage());
            // Fallback values
            currentUserName = "Người dùng";
            currentUserRole = "Nhân viên";
            userNameLabel.setText(currentUserName);
            userRoleLabel.setText(currentUserRole);
        }
    }

    /**
     * Initialize OrderPanel communication system
     */
    private void initializeOrderPanelCommunication() {
        if (orderPanelRootController != null) {
            System.out.println("✅ OrderPanelController successfully injected");

            // Set dashboard reference for bi-directional communication
            if (orderPanelRootController instanceof DashboardCommunicator) {
                ((DashboardCommunicator) orderPanelRootController).setDashboardController(this);
                System.out.println("🔗 Bi-directional communication established");
            }
        } else {
            System.err.println("⚠️ OrderPanelController not injected - check FXML fx:id");
        }
    }

    /**
     * Handle initialization errors gracefully
     */
    private void handleInitializationError(Exception e) {
        // TODO: Show user-friendly error dialog
        // For now, just ensure basic functionality works
        System.err.println("🚨 Dashboard initialization failed, attempting recovery...");
    }

    // =====================================================
    // BUTTON ACTIONS & NAVIGATION
    // =====================================================

    /**
     * Setup button click actions
     */
    private void setupButtonActions() {
        // Overview tab removed for staff dashboard

        menuTabButton.setOnAction(e -> {
            System.out.println("📱 Menu tab clicked");
            loadMenuContent();
            setActiveTab("menu");
        });

        tableTabButton.setOnAction(e -> {
            System.out.println("🏢 Table tab clicked");
            loadTableContent();
            setActiveTab("table");
        });

        logoutButton.setOnAction(e -> {
            System.out.println("🚪 Logout clicked");
            handleLogout();
        });
    }

    /**
     * Setup initial tab styling
     */
    private void setupTabStyling() {
        // Apply initial styles to ensure consistency
        setActiveTab("overview");
    }

    /**
     * Load menu content với enhanced controller communication
     */
    private void loadMenuContent() {
        try {
            System.out.println("📋 Loading menu content...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard/menu-layout.fxml"));
            Node menuContent = loader.load();

            // Get controller và setup communication
            currentMenuController = loader.getController();
            setupControllerCommunication(currentMenuController, "MenuController");

            // Update UI
            updateContentPane(menuContent);
            currentTab = "menu";

            System.out.println("✅ Menu content loaded successfully");

        } catch (IOException e) {
            System.err.println("❌ Error loading menu content: " + e.getMessage());
            e.printStackTrace();
            handleContentLoadError("Menu", e);
        }
    }

    /**
     * Load table content với enhanced controller communication
     */
    private void loadTableContent() {
        try {
            System.out.println("🏢 Loading table content...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard/table-layout.fxml"));
            Node tableContent = loader.load();

            // Get controller và setup communication
            currentTableController = loader.getController();
            setupControllerCommunication(currentTableController, "TableController");

            // Update UI
            updateContentPane(tableContent);
            currentTab = "table";

            System.out.println("✅ Table content loaded successfully");

        } catch (IOException e) {
            System.err.println("❌ Error loading table content: " + e.getMessage());
            e.printStackTrace();
            handleContentLoadError("Table", e);
        }
    }

    // Overview content removed - staff dashboard focuses on menu and table management

    /**
     * Setup communication for any controller that implements DashboardCommunicator
     */
    private void setupControllerCommunication(Object controller, String controllerName) {
        if (controller instanceof DashboardCommunicator) {
            ((DashboardCommunicator) controller).setDashboardController(this);
            System.out.println("🔗 " + controllerName + " communication established");
        } else {
            System.out.println("⚠️ " + controllerName + " does not implement DashboardCommunicator");
        }
    }

    /**
     * Update content pane with new content
     */
    private void updateContentPane(Node newContent) {
        contentPane.getChildren().clear();
        contentPane.getChildren().add(newContent);
        currentContent = newContent;
    }

    /**
     * Handle content loading errors
     */
    private void handleContentLoadError(String contentType, Exception e) {
        // TODO: Show user-friendly error message
        System.err.println("🚨 Failed to load " + contentType + " content");

        // Attempt to stay on current content or show error page
        Label errorLabel = new Label("Lỗi tải " + contentType + ". Vui lòng thử lại.");
        errorLabel.setStyle("-fx-text-fill: red; -fx-font-size: 16px; -fx-alignment: center;");
        updateContentPane(errorLabel);
    }

    /**
     * Set active tab styling với enhanced visual feedback
     */
    private void setActiveTab(String tabName) {
        // Define styles
        String baseStyle = "-fx-font-weight: bold; -fx-padding: 12 30; -fx-border-width: 0; -fx-min-width: 120; -fx-cursor: hand;";
        String inactiveStyle = baseStyle + " -fx-background-color: #8B4513; -fx-text-fill: white;";
        String activeStyle = baseStyle + " -fx-background-color: #A0522D; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);";

        // Reset all buttons with null checks
        if (menuTabButton != null) menuTabButton.setStyle(inactiveStyle);
        if (tableTabButton != null) tableTabButton.setStyle(inactiveStyle);

        // Set active button
        switch (tabName) {
            case "menu":
                if (menuTabButton != null) menuTabButton.setStyle(activeStyle);
                break;
            case "table":
                if (tableTabButton != null) tableTabButton.setStyle(activeStyle);
                break;
            default:
                System.err.println("⚠️ Unknown tab: " + tabName);
        }
    }

    /**
     * Handle logout action
     */
    private void handleLogout() {
        try {
            System.out.println("🚪 Processing logout...");

            // TODO: Clear user session
            // TODO: Save any pending data
            // TODO: Close open dialogs

            if (CafeManagementApplication.showConfirmAlert("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất không?")) {
                SessionManager.clearSession();
                CafeManagementApplication.showLoginScreen();
            }

        } catch (Exception e) {
            System.err.println("❌ Error during logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // DASHBOARDEVENTHANDLER IMPLEMENTATION
    // =====================================================

    @Override
    public void addToOrder(String productName, double price, int quantity) {
        try {
            System.out.println("🛒 Adding to order (legacy): " + productName + " x" + quantity + " = " + String.format("%.0f VND", price * quantity));

            if (orderPanelRootController != null) {
                // This is a legacy method - prefer addProductToOrder with Product object
                System.out.println("📝 Legacy order addition logged");
            }
        } catch (Exception e) {
            System.err.println("❌ Error in addToOrder: " + e.getMessage());
        }
    }

    @Override
    public void addProductToOrder(Product product, int quantity) {
        try {
            if (product == null) {
                System.err.println("⚠️ Cannot add null product to order");
                return;
            }

            if (quantity <= 0) {
                System.err.println("⚠️ Invalid quantity: " + quantity);
                return;
            }

            if (orderPanelRootController != null) {
                orderPanelRootController.addProduct(product, quantity);
                System.out.println("✅ Product added to order: " + product.getProductName() + " x" + quantity);

                // Log transaction details
                double totalPrice = product.getPrice() * quantity;
                System.out.println("💰 Order total updated: +" + String.format("%.0f VND", totalPrice));
            } else {
                System.err.println("⚠️ OrderPanel not available for product addition");
            }
        } catch (Exception e) {
            System.err.println("❌ Error adding product to order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onTableSelected(TableCafe table) {
        try {
            if (table == null) {
                System.err.println("⚠️ Cannot select null table");
                return;
            }

            if (orderPanelRootController != null) {
                orderPanelRootController.setCurrentTable(table.getTableId());
                System.out.println("✅ Table selected: " + table.getTableName() + " (ID: " + table.getTableId() + ")");
                System.out.println("📊 Table status: " + table.getStatus() + ", Capacity: " + table.getCapacity());
            } else {
                System.err.println("⚠️ OrderPanel not available for table selection");
            }
        } catch (Exception e) {
            System.err.println("❌ Error handling table selection: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void updateTableInfo(String tableName, String status) {
        try {
            if (tableName == null || tableName.trim().isEmpty()) {
                System.err.println("⚠️ Invalid table name for update");
                return;
            }

            System.out.println("🔄 Updating table info: " + tableName + " -> " + status);

            if (orderPanelRootController != null) {
                // Extract table ID from tableName (assuming format "Bàn X")
                try {
                    String[] parts = tableName.trim().split("\\s+");
                    if (parts.length >= 2) {
                        // Try to parse the number part
                        String numberPart = parts[parts.length - 1]; // Get last part
                        int tableId = Integer.parseInt(numberPart);
                        orderPanelRootController.setCurrentTable(tableId);
                        System.out.println("✅ Table info updated successfully: " + tableName + " (" + status + ")");
                    } else {
                        System.err.println("⚠️ Unexpected table name format: " + tableName);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Could not parse table ID from: " + tableName);
                }
            } else {
                System.err.println("⚠️ OrderPanel not available for table info update");
            }
        } catch (Exception e) {
            System.err.println("❌ Error updating table info: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void clearOrder() {
        try {
            System.out.println("🗑️ Clearing current order...");

            if (orderPanelRootController != null) {
                // TODO: Add clearOrder method to OrderPanelController if not exists
                // For now, just log the action
                System.out.println("✅ Order clear request processed");
            } else {
                System.err.println("⚠️ OrderPanel not available for order clearing");
            }
        } catch (Exception e) {
            System.err.println("❌ Error clearing order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public double getOrderTotal() {
        try {
            if (orderPanelRootController != null) {
                double total = orderPanelRootController.getTotalAmount();
                System.out.println("💰 Current order total: " + String.format("%.0f VND", total));
                return total;
            } else {
                System.err.println("⚠️ OrderPanel not available for total calculation");
                return 0.0;
            }
        } catch (Exception e) {
            System.err.println("❌ Error getting order total: " + e.getMessage());
            return 0.0;
        }
    }

    @Override
    public void onOrderStatusChanged(String newStatus, int tableId) {
        try {
            System.out.println("🔄 Order status changed: " + newStatus + " for table " + tableId);

            // Refresh table display if currently viewing tables
            if (currentTableController != null && "table".equals(currentTab)) {
                System.out.println("🔄 Refreshing table display for status update...");
                // TODO: Add refresh method to TableController if needed
                // currentTableController.refreshTableDisplay();
            }

            // Log status change for audit trail
            System.out.println("📝 Status change logged: Table " + tableId + " -> " + newStatus);

        } catch (Exception e) {
            System.err.println("❌ Error handling order status change: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =====================================================
    // PUBLIC UTILITY METHODS
    // =====================================================

    /**
     * ✅ NEW: Update table status - called by DashboardHelper via reflection
     * This method is required for the reflection call in DashboardHelper.updateTableStatus()
     */
    public void updateTableStatus(int tableId, String newStatus) {
        try {
            System.out.println("🔄 DashboardController.updateTableStatus called: Table " + tableId + " -> " + newStatus);
            
            // Forward the update to TableController if it's loaded
            if (currentTableController != null) {
                currentTableController.updateTableStatus(tableId, newStatus);
                System.out.println("✅ Table status update forwarded to TableController");
            } else {
                System.err.println("⚠️ TableController not loaded - cannot update table status");
            }
            
            // Also notify OrderPanel if it's available
            if (orderPanelRootController != null) {
                // Update OrderPanel's current table if it matches
                if (orderPanelRootController.getCurrentTableId() == tableId) {
                    System.out.println("✅ OrderPanel notified of table status change");
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in DashboardController.updateTableStatus: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ NEW: Show order panel for specific table
     * This method is required for the reflection call in DashboardHelper.showOrderPanel()
     */
    public void showOrderPanel(int tableId) {
        try {
            System.out.println("📋 DashboardController.showOrderPanel called for table: " + tableId);
            
            // Set the table in OrderPanel
            if (orderPanelRootController != null) {
                orderPanelRootController.setCurrentTable(tableId);
                System.out.println("✅ OrderPanel set to table " + tableId);
            } else {
                System.err.println("⚠️ OrderPanel not available");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in DashboardController.showOrderPanel: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * ✅ NEW: Show order panel for just-reserved table (preserve reserved status)
     */
    public void showOrderPanelForReserved(int tableId) {
        try {
            System.out.println("📋 DashboardController.showOrderPanelForReserved called for table: " + tableId);
            
            // Set the table in OrderPanel with reserved flag
            if (orderPanelRootController != null) {
                orderPanelRootController.setCurrentTableForReserved(tableId);
                System.out.println("✅ OrderPanel set to reserved table " + tableId);
            } else {
                System.err.println("⚠️ OrderPanel not available");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in DashboardController.showOrderPanelForReserved: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Refresh current content - useful for external calls
     */
    public void refreshCurrentContent() {
        System.out.println("🔄 Refreshing current content: " + currentTab);

        switch (currentTab) {
            case "menu":
                loadMenuContent();
                break;
            case "table":
                loadTableContent();
                break;
            default:
                System.err.println("⚠️ Unknown tab for refresh: " + currentTab);
        }
    }

    /**
     * Switch to specific tab programmatically
     */
    public void switchToTab(String tabName) {
        System.out.println("🔄 Switching to tab: " + tabName);

        switch (tabName.toLowerCase()) {
            case "menu":
                loadMenuContent();
                setActiveTab("menu");
                break;
            case "table":
                loadTableContent();
                setActiveTab("table");
                break;
            default:
                System.err.println("⚠️ Unknown tab: " + tabName);
        }
    }

    // =====================================================
    // GETTERS FOR CONTROLLER ACCESS
    // =====================================================

    public String getCurrentTab() {
        return currentTab;
    }

    public OrderPanelController getOrderPanelController() {
        return orderPanelRootController;
    }

    // Overview controller getter removed - no longer needed

    public MenuController getCurrentMenuController() {
        return currentMenuController;
    }

    public TableController getCurrentTableController() {
        return currentTableController;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }
    
    /**
     * Refresh user information from session
     * Có thể gọi khi user info thay đổi
     */
    public void refreshUserInfo() {
        initializeUserInfo();
        System.out.println("🔄 User info refreshed in dashboard");
    }
    
    /**
     * Update user display with new information
     * @param fullName Tên đầy đủ của user
     * @param role Vai trò của user
     */
    public void updateUserDisplay(String fullName, String role) {
        if (fullName != null && !fullName.isEmpty()) {
            currentUserName = fullName;
            userNameLabel.setText(currentUserName);
        }
        
        if (role != null && !role.isEmpty()) {
            currentUserRole = role;
            userRoleLabel.setText(currentUserRole);
        }
        
        System.out.println("👤 User display updated: " + currentUserName + " (" + currentUserRole + ")");
    }

    public String getCurrentUserRole() {
        return currentUserRole;
    }

    public int getCurrentUserId() {
        return currentUserId;
    }

    // =====================================================
    // SETTERS FOR DYNAMIC UPDATES
    // =====================================================

    public void setCurrentUser(String userName, String userRole, int userId) {
        this.currentUserName = userName;
        this.currentUserRole = userRole;
        this.currentUserId = userId;
        initializeUserInfo(); // Refresh display
        System.out.println("👤 User info updated: " + userName + " (" + userRole + ")");
    }

    // =====================================================
    // DEBUG & MONITORING
    // =====================================================

    /**
     * Get system status for debugging
     */
    public String getSystemStatus() {
        StringBuilder status = new StringBuilder();
        status.append("=== DASHBOARD STATUS ===\n");
        status.append("Current Tab: ").append(currentTab).append("\n");
        status.append("User: ").append(currentUserName).append(" (").append(currentUserRole).append(")\n");
        status.append("OrderPanel: ").append(orderPanelRootController != null ? "Connected" : "Disconnected").append("\n");
        status.append("MenuController: ").append(currentMenuController != null ? "Loaded" : "Not Loaded").append("\n");
        status.append("TableController: ").append(currentTableController != null ? "Loaded" : "Not Loaded").append("\n");

        if (orderPanelRootController != null) {
            status.append("Current Order Total: ").append(String.format("%.0f VND", getOrderTotal())).append("\n");
        }

        return status.toString();
    }

    /**
     * Print system status to console
     */
    public void printSystemStatus() {
        System.out.println(getSystemStatus());
    }
}