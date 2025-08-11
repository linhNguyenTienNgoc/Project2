package com.cafe.controller.dashboard;

import com.cafe.CafeManagementApplication;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
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
    @FXML private HBox navigationBar;
    @FXML private VBox sidebar;
    @FXML private Label userNameLabel;
    @FXML private Label userRoleLabel;
    @FXML private Button logoutButton;
    
    // Navigation buttons
    @FXML private Button menuButton;
    @FXML private Button tableButton;
    @FXML private Button orderButton;
    @FXML private Button customerButton;
    @FXML private Button reportButton;
    @FXML private Button settingsButton;
    
    // Current active module
    private String currentModule = "menu";
    private Node currentContent;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Verify FXML injection
            verifyFXMLInjection();
            
            setupUserInfo();
            setupNavigation();
            setupEventHandlers();
            
            // Default content is already loaded in FXML
            if (menuButton != null) {
                setActiveNavButton(menuButton);
            }
            
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
        if (navigationBar == null) missingElements.append("navigationBar, ");
        if (sidebar == null) missingElements.append("sidebar, ");
        if (userNameLabel == null) missingElements.append("userNameLabel, ");
        if (userRoleLabel == null) missingElements.append("userRoleLabel, ");
        if (logoutButton == null) missingElements.append("logoutButton, ");
        if (menuButton == null) missingElements.append("menuButton, ");
        if (tableButton == null) missingElements.append("tableButton, ");
        if (orderButton == null) missingElements.append("orderButton, ");
        if (customerButton == null) missingElements.append("customerButton, ");
        if (reportButton == null) missingElements.append("reportButton, ");
        if (settingsButton == null) missingElements.append("settingsButton, ");
        
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
            // Use SessionManager static methods with safe fallback
            if (com.cafe.util.SessionManager.isLoggedIn()) {
                userNameLabel.setText(com.cafe.util.SessionManager.getCurrentUserFullName());
                String roleName = getRoleDisplayName(com.cafe.util.SessionManager.getCurrentUserRole());
                userRoleLabel.setText(roleName);
            } else {
                // Default fallback values
                userNameLabel.setText("Người dùng");
                userRoleLabel.setText("Nhân viên");
            }
        } catch (Exception e) {
            // Fallback if SessionManager has issues
            System.err.println("SessionManager error, using fallback: " + e.getMessage());
            if (userNameLabel != null) userNameLabel.setText("Người dùng");
            if (userRoleLabel != null) userRoleLabel.setText("Nhân viên");
        }
    }
    
    /**
     * Get role display name from role string
     */
    private String getRoleDisplayName(String role) {
        if (role == null) return "Nhân viên";
        
        switch (role.toLowerCase()) {
            case "admin": return "Quản trị viên";
            case "manager": return "Quản lý";
            case "cashier": return "Thu ngân";
            case "waiter": return "Phục vụ";
            case "barista": return "Pha chế";
            default: return "Nhân viên";
        }
    }
    
    /**
     * Setup navigation buttons
     */
    private void setupNavigation() {
        // Set initial active state - with null safety
        if (menuButton != null) {
            setActiveNavButton(menuButton);
        }
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Navigation buttons - with null safety
        if (menuButton != null) {
            menuButton.setOnAction(event -> loadMenuModule());
        }
        if (tableButton != null) {
            tableButton.setOnAction(event -> loadTableModule());
        }
        if (orderButton != null) {
            orderButton.setOnAction(event -> loadOrderModule());
        }
        if (customerButton != null) {
            customerButton.setOnAction(event -> loadCustomerModule());
        }
        if (reportButton != null) {
            reportButton.setOnAction(event -> loadReportModule());
        }
        if (settingsButton != null) {
            settingsButton.setOnAction(event -> loadSettingsModule());
        }
        
        // Logout button - with null safety
        if (logoutButton != null) {
            logoutButton.setOnAction(event -> handleLogout());
        }
    }
    
    /**
     * Load Menu module
     */
    @FXML
    private void loadMenuModule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard/menu-dashboard.fxml"));
            Node menuContent = loader.load();
            
            // Set the content
            dashboardContainer.setCenter(menuContent);
            currentContent = menuContent;
            currentModule = "menu";
            
            // Update navigation
            setActiveNavButton(menuButton);
            
        } catch (IOException e) {
            showError("Không thể tải module Menu: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Load Table module
     */
    @FXML
    private void loadTableModule() {
        showModuleNotImplemented("Quản lý Bàn");
        setActiveNavButton(tableButton);
        currentModule = "table";
    }
    
    /**
     * Load Order module  
     */
    @FXML
    private void loadOrderModule() {
        showModuleNotImplemented("Quản lý Đơn hàng");
        setActiveNavButton(orderButton);
        currentModule = "order";
    }
    
    /**
     * Load Customer module
     */
    @FXML
    private void loadCustomerModule() {
        showModuleNotImplemented("Quản lý Khách hàng");
        setActiveNavButton(customerButton);
        currentModule = "customer";
    }
    
    /**
     * Load Report module
     */
    @FXML
    private void loadReportModule() {
        showModuleNotImplemented("Báo cáo");
        setActiveNavButton(reportButton);
        currentModule = "report";
    }
    
    /**
     * Load Settings module
     */
    @FXML
    private void loadSettingsModule() {
        showModuleNotImplemented("Cài đặt");
        setActiveNavButton(settingsButton);
        currentModule = "settings";
    }
    
    /**
     * Set active navigation button
     */
    private void setActiveNavButton(Button activeButton) {
        // Reset all buttons - with null safety
        Button[] navButtons = {menuButton, tableButton, orderButton, customerButton, reportButton, settingsButton};
        
        for (Button button : navButtons) {
            if (button != null) {
                button.getStyleClass().remove("active-nav-button");
                button.setStyle("-fx-background-color: transparent; -fx-text-fill: #333;");
            }
        }
        
        // Set active button - with null safety
        if (activeButton != null) {
            activeButton.getStyleClass().add("active-nav-button");
            activeButton.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-background-radius: 5;");
        }
    }
    
    /**
     * Show module not implemented message
     */
    private void showModuleNotImplemented(String moduleName) {
        try {
            // Create a simple placeholder content
            VBox placeholder = new VBox();
            placeholder.setStyle("-fx-alignment: center; -fx-spacing: 20; -fx-padding: 50;");
            
            Label titleLabel = new Label("🚧 " + moduleName);
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #666;");
            
            Label messageLabel = new Label("Module này đang được phát triển");
            messageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #999;");
            
            Label infoLabel = new Label("Vui lòng sử dụng module Menu để trải nghiệm hệ thống");
            infoLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #999;");
            
            Button backToMenuButton = new Button("Về Menu");
            backToMenuButton.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-padding: 10 20; -fx-background-radius: 5;");
            backToMenuButton.setOnAction(event -> loadMenuModule());
            
            placeholder.getChildren().addAll(titleLabel, messageLabel, infoLabel, backToMenuButton);
            
            dashboardContainer.setCenter(placeholder);
            currentContent = placeholder;
            
        } catch (Exception e) {
            showError("Lỗi hiển thị placeholder: " + e.getMessage());
        }
    }
    
    /**
     * Handle logout
     */
    @FXML
    private void handleLogout() {
        try {
            // Clear session using full class name
            com.cafe.util.SessionManager.clearSession();
            
            // Navigate back to login
            CafeManagementApplication.showLoginScreen();
            
        } catch (Exception e) {
            System.err.println("Error during logout: " + e.getMessage());
            // Force logout anyway
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
     * Get current module
     */
    public String getCurrentModule() {
        return currentModule;
    }
    
    /**
     * Get current content
     */
    public Node getCurrentContent() {
        return currentContent;
    }
    
    /**
     * Refresh current module
     */
    public void refreshCurrentModule() {
        switch (currentModule) {
            case "menu":
                loadMenuModule();
                break;
            case "table":
                loadTableModule();
                break;
            case "order":
                loadOrderModule();
                break;
            case "customer":
                loadCustomerModule();
                break;
            case "report":
                loadReportModule();
                break;
            case "settings":
                loadSettingsModule();
                break;
        }
    }
}
