package com.cafe.controller.table;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.base.DashboardHelper;
import com.cafe.model.entity.Area;
import com.cafe.model.entity.TableCafe;
import com.cafe.service.TableService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.concurrent.Task;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho table layout - UPDATED với Dashboard Communication
 * Quản lý hiển thị danh sách bàn và areas
 *
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced Communication)
 */
public class TableController implements Initializable, DashboardCommunicator {

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

    // Services
    private TableService tableService;

    // Current state
    private List<Area> areas;
    private List<TableCafe> currentTables;
    private Integer selectedAreaId = null;
    private TableCafe selectedTable = null; // ✅ Track selected table

    // Grid configuration
    private static final int TABLES_PER_ROW = 4; // Reduced from 6 to accommodate larger cards
    private static final double TABLE_CARD_WIDTH = 320;
    private static final double TABLE_CARD_HEIGHT = 110;

    // ✅ Dashboard communication
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize service
            tableService = new TableService();

            // Setup UI components
            setupAreaButtons();
            setupLoadingIndicator();

            // Load initial data
            loadInitialData();

            System.out.println("✅ TableController initialized successfully");

        } catch (Exception e) {
            System.err.println("❌ Error initializing TableController: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể khởi tạo quản lý bàn. Vui lòng kiểm tra kết nối database.");
        }
    }

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ TableController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }

    /**
     * Setup area button actions
     */
    private void setupAreaButtons() {
        allAreaBtn.setOnAction(e -> selectArea(null, allAreaBtn));
        floor1Btn.setOnAction(e -> selectAreaByName("Tầng 1", floor1Btn));
        floor2Btn.setOnAction(e -> selectAreaByName("Tầng 2", floor2Btn));
        vipBtn.setOnAction(e -> selectAreaByName("VIP", vipBtn));
        rooftopBtn.setOnAction(e -> selectAreaByName("Sân thượng", rooftopBtn));
    }

    /**
     * Setup loading indicator
     */
    private void setupLoadingIndicator() {
        loadingIndicator.setVisible(false);
        loadingIndicator.setManaged(false);
    }

    /**
     * Load initial data (areas and tables)
     */
    private void loadInitialData() {
        showLoading(true);

        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load areas
                    updateMessage("Đang tải khu vực...");
                    areas = tableService.getAvailableAreas();

                    // Load all tables initially
                    updateMessage("Đang tải danh sách bàn...");
                    currentTables = tableService.getAllAvailableTables();

                    return null;
                } catch (Exception e) {
                    System.err.println("Error loading initial data: " + e.getMessage());
                    throw e;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        displayTables(currentTables);
                        showLoading(false);
                        updateStatus("Đã tải " + currentTables.size() + " bàn");
                        setActiveAreaButton(allAreaBtn);
                    } catch (Exception e) {
                        System.err.println("Error updating UI: " + e.getMessage());
                        showError("Lỗi hiển thị dữ liệu");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Không thể tải dữ liệu bàn");
                });
            }
        };

        statusLabel.textProperty().bind(loadDataTask.messageProperty());
        new Thread(loadDataTask).start();
    }

    /**
     * Select area by ID
     */
    private void selectArea(Integer areaId, Button selectedButton) {
        selectedAreaId = areaId;
        setActiveAreaButton(selectedButton);
        loadTablesByArea(areaId);
    }

    /**
     * Select area by name (for hardcoded buttons)
     */
    private void selectAreaByName(String areaName, Button selectedButton) {
        // Find area ID by name
        Integer areaId = null;
        if (areas != null) {
            for (Area area : areas) {
                if (area.getAreaName().equals(areaName)) {
                    areaId = area.getAreaId();
                    break;
                }
            }
        }

        selectArea(areaId, selectedButton);
    }

    /**
     * Set active area button styling
     */
    private void setActiveAreaButton(Button activeButton) {
        // Reset all button styles
        String inactiveStyle = "-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;";
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;";

        allAreaBtn.setStyle(inactiveStyle);
        floor1Btn.setStyle(inactiveStyle);
        floor2Btn.setStyle(inactiveStyle);
        vipBtn.setStyle(inactiveStyle);
        rooftopBtn.setStyle(inactiveStyle);

        // Set active button style
        activeButton.setStyle(activeStyle);
    }

    /**
     * Load tables by area
     */
    private void loadTablesByArea(Integer areaId) {
        showLoading(true);

        Task<List<TableCafe>> loadTablesTask = new Task<List<TableCafe>>() {
            @Override
            protected List<TableCafe> call() throws Exception {
                if (areaId == null) {
                    updateMessage("Đang tải tất cả bàn...");
                    return tableService.getAllAvailableTables();
                } else {
                    updateMessage("Đang tải bàn theo khu vực...");
                    return tableService.getTablesByArea(areaId);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<TableCafe> tables = getValue();
                    currentTables = tables;
                    displayTables(tables);
                    showLoading(false);
                    updateStatus("Đã tải " + tables.size() + " bàn");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Không thể tải danh sách bàn");
                });
            }
        };

        statusLabel.textProperty().bind(loadTablesTask.messageProperty());
        new Thread(loadTablesTask).start();
    }

    /**
     * Display tables in grid
     */
    private void displayTables(List<TableCafe> tables) {
        tableGrid.getChildren().clear();

        if (tables == null || tables.isEmpty()) {
            Label emptyLabel = new Label("Không có bàn nào trong khu vực này");
            emptyLabel.setStyle("-fx-text-fill: #666; -fx-font-size: 16px;");
            tableGrid.add(emptyLabel, 0, 0);
            return;
        }

        // Configure grid
        tableGrid.setHgap(20);
        tableGrid.setVgap(20);
        tableGrid.setPadding(new Insets(15));

        // Add tables to grid
        int row = 0;
        int col = 0;

        for (TableCafe table : tables) {
            VBox tableCard = createTableCard(table.getTableName(), table.getStatus(), table.getTableId(), table.getCapacity());
            tableGrid.add(tableCard, col, row);

            col++;
            if (col >= TABLES_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * ✅ NEW DESIGN: Create table card UI component với improved modern design
     */
    private VBox createTableCard(String tableName, String status, int tableId, int capacity) {
        // Card container
        VBox card = new VBox();
        card.setPadding(new Insets(16));
        card.setPrefWidth(TABLE_CARD_WIDTH);
        card.setPrefHeight(TABLE_CARD_HEIGHT);

        // Check if this table is selected
        boolean isSelected = selectedTable != null && selectedTable.getTableId() == tableId;

        // Beige background with soft corners, subtle shadow
        String baseStyle = isSelected
                ? "-fx-background-color: #c9a876; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,123,255,0.3), 12, 0, 0, 3); -fx-cursor: hand; -fx-border-color: #007bff; -fx-border-width: 3; -fx-border-radius: 14;"
                : "-fx-background-color: #d9b59b; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.08), 10, 0, 0, 2); -fx-cursor: hand;";
        card.setStyle(baseStyle);

        // Internal content layout: left info, right status pill
        HBox row = new HBox();
        row.setSpacing(12);
        row.setFillHeight(true);

        VBox left = new VBox();
        left.setSpacing(8);

        Label title = new Label("# " + tableName);
        title.setFont(Font.font("System", FontWeight.BOLD, 20));
        title.setStyle("-fx-text-fill: rgba(255,255,255,0.95);");

        HBox people = new HBox();
        people.setSpacing(6);
        Label peopleIcon = new Label("\uD83D\uDC65"); // 👥
        peopleIcon.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
        Label peopleCount = new Label(String.valueOf(capacity));
        peopleCount.setStyle("-fx-text-fill: rgba(255,255,255,0.9); -fx-font-size: 14px;");
        people.getChildren().addAll(peopleIcon, peopleCount);

        left.getChildren().addAll(title, people);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Status pill (dark background + colored dot + label)
        HBox pill = new HBox();
        pill.setSpacing(8);
        pill.setPadding(new Insets(6, 12, 6, 10));
        pill.setStyle("-fx-background-color: #1f1f1f; -fx-background-radius: 999; -fx-alignment: CENTER;");

        Region dot = new Region();
        dot.setMinSize(10, 10);
        dot.setPrefSize(10, 10);
        dot.setMaxSize(10, 10);
        dot.setStyle("-fx-background-radius: 50%;");

        Label statusText = new Label(getStatusText(status));
        statusText.setFont(Font.font("System", FontWeight.BOLD, 12));

        // Colors by status
        String color;
        switch (status.toLowerCase()) {
            case "available":
                color = "#5ad15a"; // green
                break;
            case "occupied":
                color = "#ff3b30"; // red
                break;
            case "cleaning":
                color = "#f4c20d"; // yellow
                break;
            case "reserved":
            default:
                color = "#ffa000"; // orange
        }
        dot.setStyle(dot.getStyle() + " -fx-background-color: " + color + ";");
        statusText.setStyle("-fx-text-fill: " + color + ";");

        pill.getChildren().addAll(dot, statusText);

        row.getChildren().addAll(left, spacer, pill);
        card.getChildren().add(row);

        // Click + hover effects
        card.setOnMouseClicked(e -> selectTable(tableId, tableName, status));

        card.setOnMouseEntered(e -> {
            String hoverStyle = isSelected
                    ? "-fx-background-color: #c19d6b; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,123,255,0.4), 16, 0, 0, 4); -fx-cursor: hand; -fx-border-color: #007bff; -fx-border-width: 3; -fx-border-radius: 14;"
                    : "-fx-background-color: #d6ad90; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 14, 0, 0, 3); -fx-cursor: hand;";
            card.setStyle(hoverStyle);
        });

        card.setOnMouseExited(e -> card.setStyle(baseStyle));

        card.setOnMousePressed(e -> {
            String pressedStyle = isSelected
                    ? "-fx-background-color: #b8935f; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,123,255,0.5), 8, 0, 0, 1); -fx-cursor: hand; -fx-border-color: #007bff; -fx-border-width: 3; -fx-border-radius: 14;"
                    : "-fx-background-color: #cfa582; -fx-background-radius: 14; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.18), 8, 0, 0, 1); -fx-cursor: hand;";
            card.setStyle(pressedStyle);
        });

        card.setOnMouseReleased(e -> card.setStyle(baseStyle));

        return card;
    }

    /**
     * ✅ Handle table selection
     */
    private void selectTable(int tableId, String tableName, String status) {
        try {
            // Find the table object
            TableCafe table = getTableById(tableId);
            if (table != null) {
                handleTableClick(table);
            }
        } catch (Exception e) {
            System.err.println("Error selecting table: " + e.getMessage());
            showError("Không thể chọn bàn");
        }
    }

    /**
     * Get Vietnamese text for table status
     */
    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "available": return "Trống";
            case "occupied": return "Có khách";
            case "reserved": return "Đặt trước";
            case "cleaning": return "Dọn dẹp";
            default: return "Không xác định";
        }
    }

    /**
     * ✅ FIXED: Handle table click - Show appropriate action based on table status
     */
    private void handleTableClick(TableCafe table) {
        try {
            System.out.println("Table clicked: " + table.getTableName() + " - Status: " + table.getStatus());

            // For all tables, show order panel (which handles different statuses appropriately)
            selectedTable = table;
            displayTables(currentTables); // Refresh to show selection
            
            DashboardHelper.notifyTableSelected(dashboardController, table);
            DashboardHelper.updateTableInfo(dashboardController, table.getTableName(), table.getStatus());
            
            // Show order panel for table (handles cleaning status with alert)
            showOrderPanelForTable(table);

        } catch (Exception e) {
            System.err.println("Error handling table click: " + e.getMessage());
            showError("Không thể xử lý thao tác với bàn: " + e.getMessage());
        }
    }

    /**
     * ✅ OPTIMIZED: Switch to menu tab after selecting table - WITHOUT auto status update
     */
    private void switchToMenuTabWithoutStatusUpdate(TableCafe table) {
        try {
            System.out.println("🔄 Switching to menu tab for table: " + table.getTableName() + " (no auto-update)");
            
            // Use reflection to call switchToTab method on DashboardController
            if (dashboardController != null) {
                Method switchToTabMethod = dashboardController.getClass().getMethod("switchToTab", String.class);
                switchToTabMethod.invoke(dashboardController, "menu");
                
                // Set up the order panel for the selected table - use reserved method
                try {
                    // Use the new method for reserved tables to preserve status
                    Method showOrderPanelForReservedMethod = dashboardController.getClass().getMethod("showOrderPanelForReserved", int.class);
                    showOrderPanelForReservedMethod.invoke(dashboardController, table.getTableId());
                    System.out.println("✅ Using showOrderPanelForReserved method");
                } catch (NoSuchMethodException e) {
                    // Fallback to old method if new method doesn't exist yet
                    Method oldShowOrderPanelMethod = dashboardController.getClass().getMethod("showOrderPanel", int.class);
                    oldShowOrderPanelMethod.invoke(dashboardController, table.getTableId());
                    System.out.println("⚠️ Using fallback showOrderPanel method");
                }
                
                System.out.println("✅ Successfully switched to menu tab and set up order panel (status preserved)");
            } else {
                System.err.println("⚠️ Dashboard controller not available");
                showError("Không thể chuyển sang menu. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error switching to menu tab: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể chuyển sang menu: " + e.getMessage());
        }
    }

    /**
     * ✅ LEGACY: Switch to menu tab after selecting table (with potential auto status update)
     */
    private void switchToMenuTab(TableCafe table) {
        try {
            System.out.println("🔄 Switching to menu tab for table: " + table.getTableName());
            
            // Use reflection to call switchToTab method on DashboardController
            if (dashboardController != null) {
                Method switchToTabMethod = dashboardController.getClass().getMethod("switchToTab", String.class);
                switchToTabMethod.invoke(dashboardController, "menu");
                
                // Also set up the order panel for the selected table
                Method showOrderPanelMethod = dashboardController.getClass().getMethod("showOrderPanel", int.class);
                showOrderPanelMethod.invoke(dashboardController, table.getTableId());
                
                System.out.println("✅ Successfully switched to menu tab and set up order panel");
            } else {
                System.err.println("⚠️ Dashboard controller not available");
                showError("Không thể chuyển sang menu. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error switching to menu tab: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể chuyển sang menu: " + e.getMessage());
        }
    }

    /**
     * ✅ NEW: Show order panel for selected table
     */
    private void showOrderPanelForTable(TableCafe table) {
        try {
            // Special handling for cleaning status - show option to finish cleaning
            if ("cleaning".equalsIgnoreCase(table.getStatus())) {
                Alert cleaningAlert = new Alert(Alert.AlertType.CONFIRMATION);
                cleaningAlert.setTitle("Bàn đang dọn dẹp");
                cleaningAlert.setHeaderText("Bàn: " + table.getTableName());
                cleaningAlert.setContentText("Bàn này đang được dọn dẹp. Bạn có muốn đánh dấu hoàn thành dọn dẹp không?");

                cleaningAlert.getButtonTypes().setAll(
                        new ButtonType("Hoàn thành dọn dẹp", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Xem order", ButtonBar.ButtonData.OTHER),
                        new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE)
                );

                cleaningAlert.showAndWait().ifPresent(buttonType -> {
                    if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                        finishCleaning(table);
                    } else if (buttonType.getButtonData() == ButtonBar.ButtonData.OTHER) {
                        // Show order panel anyway
                        DashboardHelper.showOrderPanel(dashboardController, table.getTableId());
                    }
                });
                return;
            }

            // For all other statuses, switch to menu tab to start ordering
            switchToMenuTabWithoutStatusUpdate(table);

        } catch (Exception e) {
            System.err.println("Error showing order panel: " + e.getMessage());
            showError("Không thể hiển thị panel order");
        }
    }

    /**
     * ✅ IMPROVED: Update table status from external sources (like OrderPanel)
     */
    public void updateTableStatus(int tableId, String newStatus) {
        try {
            // Validate new status
            if (newStatus == null || newStatus.trim().isEmpty()) {
                System.err.println("❌ Invalid status: status cannot be null or empty");
                return;
            }

            // Validate status values and normalize
            String[] validStatuses = {"available", "occupied", "cleaning", "reserved"};
            boolean isValidStatus = false;
            String normalizedStatus = newStatus;
            for (String status : validStatuses) {
                if (status.equalsIgnoreCase(newStatus)) {
                    isValidStatus = true;
                    normalizedStatus = status; // Normalize to lowercase
                    break;
                }
            }
            
            if (!isValidStatus) {
                System.err.println("❌ Invalid status: " + newStatus + ". Valid statuses: " + String.join(", ", validStatuses));
                return;
            }

            // Find table in current list
            TableCafe table = getTableById(tableId);
            if (table == null) {
                System.err.println("❌ Table not found with ID: " + tableId);
                return;
            }

            String oldStatus = table.getStatus();

            // Update in database
            boolean success = tableService.updateTableStatus(tableId, normalizedStatus);
            if (success) {
                table.setStatus(normalizedStatus); // Update local object

                // Refresh display to show new status
                displayTables(currentTables);

                System.out.println("✅ Table " + tableId + " status updated: " + oldStatus + " → " + normalizedStatus);

                // ✅ Notify Dashboard about status change
                DashboardHelper.notifyOrderStatusChanged(dashboardController, normalizedStatus, tableId);
                
                // Show success message to user
                final String finalOldStatus = oldStatus;
                final String finalNewStatus = normalizedStatus;
                Platform.runLater(() -> {
                    showInfo("Đã cập nhật trạng thái bàn " + table.getTableName() + 
                           " từ " + getStatusText(finalOldStatus) + " thành " + getStatusText(finalNewStatus));
                });
            } else {
                System.err.println("❌ Failed to update table status in database");
                Platform.runLater(() -> {
                    showError("Không thể cập nhật trạng thái bàn trong cơ sở dữ liệu");
                });
            }
        } catch (Exception e) {
            System.err.println("Error updating table status: " + e.getMessage());
            e.printStackTrace();
            Platform.runLater(() -> {
                showError("Lỗi cập nhật trạng thái bàn: " + e.getMessage());
            });
        }
    }

    /**
     * Finish cleaning table (only remaining manual action)
     */
    private void finishCleaning(TableCafe table) {
        try {
            boolean success = tableService.updateTableStatus(table.getTableId(), "available");
            if (success) {
                table.setStatus("available"); // Update local object
                showInfo("Đã hoàn thành dọn dẹp " + table.getTableName());
                refreshTables();

                // ✅ Notify Dashboard about status change
                DashboardHelper.notifyOrderStatusChanged(dashboardController, "available", table.getTableId());
            } else {
                showError("Không thể cập nhật trạng thái bàn");
            }
        } catch (Exception e) {
            System.err.println("Error finishing cleaning: " + e.getMessage());
            showError("Lỗi cập nhật trạng thái");
        }
    }

    /**
     * Refresh tables display
     */
    private void refreshTables() {
        loadTablesByArea(selectedAreaId);
    }

    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
    }

    /**
     * Update status message
     */
    private void updateStatus(String message) {
        statusLabel.textProperty().unbind();
        statusLabel.setText(message);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Get current tables (public method for external calls)
     */
    public List<TableCafe> getCurrentTables() {
        return currentTables;
    }

    /**
     * Get table by ID
     */
    public TableCafe getTableById(int tableId) {
        if (currentTables != null) {
            return currentTables.stream()
                    .filter(table -> table.getTableId() == tableId)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * ✅ NEW: Get currently selected table
     */
    public TableCafe getSelectedTable() {
        return selectedTable;
    }
}