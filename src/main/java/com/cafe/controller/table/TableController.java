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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;

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
    private static final int TABLES_PER_ROW = 6;
    private static final double TABLE_CARD_WIDTH = 140;
    private static final double TABLE_CARD_HEIGHT = 100;

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
        tableGrid.setHgap(15);
        tableGrid.setVgap(15);
        tableGrid.setPadding(new Insets(10));

        // Add tables to grid
        int row = 0;
        int col = 0;

        for (TableCafe table : tables) {
            VBox tableCard = createTableCard(table);
            tableGrid.add(tableCard, col, row);

            col++;
            if (col >= TABLES_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * ✅ ENHANCED: Create table card UI component với improved selection handling
     */
    private VBox createTableCard(TableCafe table) {
        VBox card = new VBox(8);
        card.setPrefWidth(TABLE_CARD_WIDTH);
        card.setPrefHeight(TABLE_CARD_HEIGHT);
        card.setAlignment(Pos.CENTER);

        // Get status color and Vietnamese status text
        String statusColor = getStatusColor(table.getStatus());
        String statusText = getStatusText(table.getStatus());

        // ✅ Check if this table is selected
        boolean isSelected = selectedTable != null && selectedTable.getTableId() == table.getTableId();
        String borderStyle = isSelected ? "-fx-border-color: #007bff; -fx-border-width: 4;" : String.format("-fx-border-color: %s; -fx-border-width: 3;", statusColor);

        card.setStyle(String.format("""
            -fx-background-color: white;
            %s
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 10;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
            """, borderStyle));

        // Table name
        Label nameLabel = new Label(table.getTableName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");

        // Capacity
        Label capacityLabel = new Label(table.getCapacity() + " chỗ");
        capacityLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        // Status
        Label statusLabel = new Label(statusText);
        statusLabel.setStyle(String.format("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: %s;", statusColor));

        // ✅ ENHANCED: Click action với Dashboard communication
        card.setOnMouseClicked(e -> handleTableClick(table));

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + "; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 8, 0, 0, 3);");
        });

        card.setOnMouseExited(e -> {
            String borderStyleHover = isSelected ? "-fx-border-color: #007bff; -fx-border-width: 4;" : String.format("-fx-border-color: %s; -fx-border-width: 3;", statusColor);
            card.setStyle(String.format("""
                -fx-background-color: white;
                %s
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 10;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
                """, borderStyleHover));
        });

        card.getChildren().addAll(nameLabel, capacityLabel, statusLabel);

        return card;
    }

    /**
     * Get color for table status
     */
    private String getStatusColor(String status) {
        switch (status.toLowerCase()) {
            case "available": return "#28a745"; // Green
            case "occupied": return "#dc3545";  // Red
            case "reserved": return "#ffc107";  // Yellow
            case "cleaning": return "#6c757d";  // Gray
            default: return "#6c757d";
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
     * ✅ ENHANCED: Handle table click với Dashboard communication
     */
    private void handleTableClick(TableCafe table) {
        try {
            System.out.println("Table clicked: " + table.getTableName() + " - Status: " + table.getStatus());

            // ✅ Update selected table và refresh UI
            selectedTable = table;
            displayTables(currentTables); // Refresh to show selection

            // ✅ Notify Dashboard about table selection
            DashboardHelper.notifyTableSelected(dashboardController, table);
            DashboardHelper.updateTableInfo(dashboardController, table.getTableName(), table.getStatus());

            // Show table options dialog based on status
            showTableOptionsDialog(table);

        } catch (Exception e) {
            System.err.println("Error handling table click: " + e.getMessage());
            showError("Không thể xử lý thao tác với bàn");
        }
    }

    /**
     * Show table options dialog
     */
    private void showTableOptionsDialog(TableCafe table) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Quản lý bàn");
        dialog.setHeaderText("Bàn: " + table.getTableName());
        dialog.setContentText("Trạng thái hiện tại: " + getStatusText(table.getStatus()) +
                "\nSức chứa: " + table.getCapacity() + " người" +
                "\n\nBạn muốn làm gì?");

        // Custom buttons based on table status
        dialog.getButtonTypes().clear();

        switch (table.getStatus().toLowerCase()) {
            case "available":
                dialog.getButtonTypes().addAll(
                        new ButtonType("Tạo đơn mới", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Đặt trước", ButtonBar.ButtonData.OTHER),
                        new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE)
                );
                break;
            case "occupied":
                dialog.getButtonTypes().addAll(
                        new ButtonType("Xem đơn hàng", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Thanh toán", ButtonBar.ButtonData.OTHER),
                        new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE)
                );
                break;
            case "reserved":
                dialog.getButtonTypes().addAll(
                        new ButtonType("Check-in", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Hủy đặt bàn", ButtonBar.ButtonData.OTHER),
                        new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE)
                );
                break;
            case "cleaning":
                dialog.getButtonTypes().addAll(
                        new ButtonType("Hoàn thành dọn dẹp", ButtonBar.ButtonData.OK_DONE),
                        new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE)
                );
                break;
        }

        dialog.showAndWait().ifPresent(buttonType -> {
            handleTableAction(table, buttonType.getText());
        });
    }

    /**
     * ✅ ENHANCED: Handle table action với Dashboard notification
     */
    private void handleTableAction(TableCafe table, String action) {
        String oldStatus = table.getStatus();

        switch (action) {
            case "Tạo đơn mới":
                createNewOrder(table);
                break;
            case "Đặt trước":
                reserveTable(table);
                break;
            case "Xem đơn hàng":
                viewCurrentOrder(table);
                break;
            case "Thanh toán":
                processPayment(table);
                break;
            case "Check-in":
                checkInReservedTable(table);
                break;
            case "Hủy đặt bàn":
                cancelReservation(table);
                break;
            case "Hoàn thành dọn dẹp":
                finishCleaning(table);
                break;
        }

        // ✅ Notify Dashboard if status changed
        if (!oldStatus.equals(table.getStatus())) {
            DashboardHelper.notifyOrderStatusChanged(dashboardController, table.getStatus(), table.getTableId());
        }
    }

    /**
     * Create new order for table
     */
    private void createNewOrder(TableCafe table) {
        try {
            // Update table status to occupied
            boolean success = tableService.updateTableStatus(table.getTableId(), "occupied");
            if (success) {
                table.setStatus("occupied"); // Update local object
                showInfo("Đã tạo đơn mới cho " + table.getTableName());
                refreshTables(); // Refresh UI
            } else {
                showError("Không thể tạo đơn mới");
            }
        } catch (Exception e) {
            System.err.println("Error creating new order: " + e.getMessage());
            showError("Lỗi tạo đơn mới");
        }
    }

    /**
     * Reserve table
     */
    private void reserveTable(TableCafe table) {
        try {
            boolean success = tableService.updateTableStatus(table.getTableId(), "reserved");
            if (success) {
                table.setStatus("reserved"); // Update local object
                showInfo("Đã đặt trước " + table.getTableName());
                refreshTables();
            } else {
                showError("Không thể đặt trước bàn");
            }
        } catch (Exception e) {
            System.err.println("Error reserving table: " + e.getMessage());
            showError("Lỗi đặt trước bàn");
        }
    }

    /**
     * View current order for table
     */
    private void viewCurrentOrder(TableCafe table) {
        // TODO: Implement view order functionality
        showInfo("Xem đơn hàng cho " + table.getTableName());
    }

    /**
     * Process payment for table
     */
    private void processPayment(TableCafe table) {
        try {
            // Update table status to cleaning after payment
            boolean success = tableService.updateTableStatus(table.getTableId(), "cleaning");
            if (success) {
                table.setStatus("cleaning"); // Update local object
                showInfo("Đã thanh toán cho " + table.getTableName() + ". Bàn đang được dọn dẹp.");
                refreshTables();
            } else {
                showError("Không thể xử lý thanh toán");
            }
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            showError("Lỗi xử lý thanh toán");
        }
    }

    /**
     * Check-in reserved table
     */
    private void checkInReservedTable(TableCafe table) {
        try {
            boolean success = tableService.updateTableStatus(table.getTableId(), "occupied");
            if (success) {
                table.setStatus("occupied"); // Update local object
                showInfo("Đã check-in " + table.getTableName());
                refreshTables();
            } else {
                showError("Không thể check-in");
            }
        } catch (Exception e) {
            System.err.println("Error checking in table: " + e.getMessage());
            showError("Lỗi check-in");
        }
    }

    /**
     * Cancel table reservation
     */
    private void cancelReservation(TableCafe table) {
        try {
            boolean success = tableService.updateTableStatus(table.getTableId(), "available");
            if (success) {
                table.setStatus("available"); // Update local object
                showInfo("Đã hủy đặt bàn " + table.getTableName());
                refreshTables();
            } else {
                showError("Không thể hủy đặt bàn");
            }
        } catch (Exception e) {
            System.err.println("Error cancelling reservation: " + e.getMessage());
            showError("Lỗi hủy đặt bàn");
        }
    }

    /**
     * Finish cleaning table
     */
    private void finishCleaning(TableCafe table) {
        try {
            boolean success = tableService.updateTableStatus(table.getTableId(), "available");
            if (success) {
                table.setStatus("available"); // Update local object
                showInfo("Đã hoàn thành dọn dẹp " + table.getTableName());
                refreshTables();
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