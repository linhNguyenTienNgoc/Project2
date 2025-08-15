package com.cafe.controller.table;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.enums.TableStatus;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho table layout
 * Quản lý hiển thị và tương tác với bàn
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class TableController implements Initializable {
    
    // FXML Elements
    @FXML private VBox tableLayoutContainer;
    @FXML private HBox areaBar;
    @FXML private Button allAreaBtn;
    @FXML private Button floor1Btn;
    @FXML private Button floor2Btn;
    @FXML private Button vipBtn;
    @FXML private Button rooftopBtn;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;
    @FXML private ScrollPane tableScrollPane;
    @FXML private VBox tableContainer;
    @FXML private GridPane tableGrid;
    
    // Current state
    private String selectedArea = "all";
    private TableCafe selectedTable;
    
    // Reference to parent dashboard controller for order panel communication
    private DashboardController dashboardController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            verifyFXMLInjection();
            setupEventHandlers();
            loadTables();
            
            System.out.println("✅ TableController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing TableController: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khởi tạo Table: " + e.getMessage());
        }
    }
    
    /**
     * Verify that all required FXML elements are properly injected
     */
    private void verifyFXMLInjection() {
        StringBuilder missingElements = new StringBuilder();
        
        if (tableLayoutContainer == null) missingElements.append("tableLayoutContainer, ");
        if (areaBar == null) missingElements.append("areaBar, ");
        if (allAreaBtn == null) missingElements.append("allAreaBtn, ");
        if (floor1Btn == null) missingElements.append("floor1Btn, ");
        if (floor2Btn == null) missingElements.append("floor2Btn, ");
        if (vipBtn == null) missingElements.append("vipBtn, ");
        if (rooftopBtn == null) missingElements.append("rooftopBtn, ");
        if (loadingIndicator == null) missingElements.append("loadingIndicator, ");
        if (statusLabel == null) missingElements.append("statusLabel, ");
        if (tableScrollPane == null) missingElements.append("tableScrollPane, ");
        if (tableContainer == null) missingElements.append("tableContainer, ");
        if (tableGrid == null) missingElements.append("tableGrid, ");
        
        if (missingElements.length() > 0) {
            String missing = missingElements.substring(0, missingElements.length() - 2);
            System.err.println("⚠️ Warning: Missing FXML elements: " + missing);
        }
    }
    
    /**
     * Setup event handlers for buttons
     */
    private void setupEventHandlers() {
        // Area selection buttons
        allAreaBtn.setOnAction(e -> selectArea("all"));
        floor1Btn.setOnAction(e -> selectArea("floor1"));
        floor2Btn.setOnAction(e -> selectArea("floor2"));
        vipBtn.setOnAction(e -> selectArea("vip"));
        rooftopBtn.setOnAction(e -> selectArea("rooftop"));
    }
    
    /**
     * Select area and update table display
     */
    private void selectArea(String area) {
        selectedArea = area;
        updateAreaButtonStyles();
        loadTables();
    }
    
    /**
     * Update button styles based on selected area
     */
    private void updateAreaButtonStyles() {
        // Reset all buttons
        allAreaBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        floor1Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        floor2Btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        vipBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        rooftopBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        
        // Set active button
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;";
        
        switch (selectedArea) {
            case "all":
                allAreaBtn.setStyle(activeStyle);
                break;
            case "floor1":
                floor1Btn.setStyle(activeStyle);
                break;
            case "floor2":
                floor2Btn.setStyle(activeStyle);
                break;
            case "vip":
                vipBtn.setStyle(activeStyle);
                break;
            case "rooftop":
                rooftopBtn.setStyle(activeStyle);
                break;
        }
    }
    
    /**
     * Load tables based on selected area
     */
    private void loadTables() {
        showLoading(true);
        
        // Clear existing tables
        tableGrid.getChildren().clear();
        
        try {
            // TODO: Load tables from database based on selected area
            // For now, create sample tables
            createSampleTables();
            
            showLoading(false);
            updateStatus("Đã tải " + tableGrid.getChildren().size() + " bàn");
            
        } catch (Exception e) {
            showLoading(false);
            showError("Lỗi tải danh sách bàn: " + e.getMessage());
        }
    }
    
    /**
     * Create sample tables for demonstration
     */
    private void createSampleTables() {
        // Sample table data
        String[] tableNames = {"Bàn 1", "Bàn 2", "Bàn 3", "Bàn 4", "Bàn 5", "Bàn 6", "Bàn 7", "Bàn 8"};
        TableStatus[] statuses = {TableStatus.AVAILABLE, TableStatus.OCCUPIED, TableStatus.RESERVED, TableStatus.CLEANING};
        
        int col = 0;
        int row = 0;
        int maxCols = 4;
        
        for (int i = 0; i < tableNames.length; i++) {
            String tableName = tableNames[i];
            TableStatus status = statuses[i % statuses.length];
            
            VBox tableCard = createTableCard(tableName, status, i + 1);
            
            tableGrid.add(tableCard, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Create a table card component
     */
    private VBox createTableCard(String tableName, TableStatus status, int tableId) {
        VBox card = new VBox(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(120);
        card.setPrefHeight(100);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Status indicator
        Rectangle statusIndicator = new Rectangle(20, 20);
        statusIndicator.setArcWidth(5);
        statusIndicator.setArcHeight(5);
        
        // Set color based on status
        switch (status) {
            case AVAILABLE:
                statusIndicator.setFill(Color.web("#28a745"));
                break;
            case OCCUPIED:
                statusIndicator.setFill(Color.web("#dc3545"));
                break;
            case RESERVED:
                statusIndicator.setFill(Color.web("#ffc107"));
                break;
            case CLEANING:
                statusIndicator.setFill(Color.web("#6c757d"));
                break;
        }
        
        // Table name label
        Label nameLabel = new Label(tableName);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #333;");
        
        // Status label
        Label statusLabel = new Label(getStatusText(status));
        statusLabel.setFont(Font.font("System", 10));
        statusLabel.setStyle("-fx-text-fill: #666;");
        
        card.getChildren().addAll(statusIndicator, nameLabel, statusLabel);
        
        // Add click handler
        card.setOnMouseClicked(e -> selectTable(tableId, tableName, status));
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #8B4513; -fx-border-width: 2; -fx-border-radius: 8; -fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8;");
        });
        
        return card;
    }
    
    /**
     * Get status text in Vietnamese
     */
    private String getStatusText(TableStatus status) {
        switch (status) {
            case AVAILABLE:
                return "Trống";
            case OCCUPIED:
                return "Có khách";
            case RESERVED:
                return "Đặt trước";
            case CLEANING:
                return "Dọn dẹp";
            default:
                return "Không xác định";
        }
    }
    
    /**
     * Select a table
     */
    private void selectTable(int tableId, String tableName, TableStatus status) {
        selectedTable = new TableCafe();
        selectedTable.setTableId(tableId);
        selectedTable.setTableName(tableName);
        selectedTable.setStatus(status.name());
        
        // Update order panel in dashboard (RESTORED FUNCTIONALITY)
        if (dashboardController != null) {
            dashboardController.updateTableInfo(tableName, status);
            System.out.println("✅ TableController: Updated order panel with table info");
        } else {
            System.err.println("❌ TableController: DashboardController is null!");
        }
        
        updateStatus("Đã chọn " + tableName);
    }
    
    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        if (show) {
            updateStatus("Đang tải...");
        }
    }
    
    /**
     * Update status label
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        CafeManagementApplication.showErrorAlert("Lỗi", message);
    }
    
    /**
     * Set reference to dashboard controller
     */
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    
    /**
     * Get selected table
     */
    public TableCafe getSelectedTable() {
        return selectedTable;
    }
    
    /**
     * Clear table selection
     */
    public void clearSelection() {
        selectedTable = null;
        updateStatus("Sẵn sàng");
    }
}