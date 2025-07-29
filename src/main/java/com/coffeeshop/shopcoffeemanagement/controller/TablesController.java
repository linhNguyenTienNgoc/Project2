package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import com.coffeeshop.shopcoffeemanagement.dao.CoffeeTableDAO;
import com.coffeeshop.shopcoffeemanagement.controller.OrderController;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TablesController {
    
    @FXML
    private GridPane tablesGrid;
    
    @FXML
    private Button addTableButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilter;
    
    private List<CoffeeTable> tables;
    private CoffeeTableDAO tableDAO;
    
    @FXML
    public void initialize() {
        tableDAO = new CoffeeTableDAO();
        setupFilters();
        loadTables();
        displayTables();
    }
    
    private void setupFilters() {
        // Setup status filter
        statusFilter.getItems().addAll("Tất cả", "Trống", "Có khách", "Đã đặt", "Đang dọn");
        statusFilter.setValue("Tất cả");
        
        // Setup search field
        searchField.setPromptText("Tìm kiếm bàn...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTables();
        });
        
        // Setup status filter
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterTables();
        });
    }
    
    private void filterTables() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        
        List<CoffeeTable> filteredTables = tables.stream()
            .filter(table -> {
                // Filter by search text
                boolean matchesSearch = table.getTableNumber().toLowerCase().contains(searchText) ||
                                      table.getLocation().toLowerCase().contains(searchText);
                
                // Filter by status
                boolean matchesStatus = "Tất cả".equals(selectedStatus) ||
                                      getStatusText(table.getStatus()).equals(selectedStatus);
                
                return matchesSearch && matchesStatus;
            })
            .collect(java.util.stream.Collectors.toList());
        
        displayFilteredTables(filteredTables);
    }
    
    private void displayFilteredTables(List<CoffeeTable> filteredTables) {
        tablesGrid.getChildren().clear();
        
        int columns = 5; // Số cột hiển thị
        int row = 0;
        int col = 0;
        
        for (CoffeeTable table : filteredTables) {
            VBox tableNode = createTableNode(table);
            tablesGrid.add(tableNode, col, row);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    @FXML
    private void refreshTables() {
        loadTables();
        displayTables();
    }
    
    @FXML
    private void showAddTableDialog() {
        // TODO: Implement add table dialog
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng thêm bàn sẽ được phát triển sau");
    }
    
    private void loadTables() {
        try {
            tables = tableDAO.findAll();
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách bàn: " + e.getMessage());
            // Fallback to demo data
            tables = new ArrayList<>();
            tables.add(new CoffeeTable("T01", 4, "Khu vực A - Gần cửa sổ"));
            tables.add(new CoffeeTable("T02", 4, "Khu vực A - Gần cửa sổ"));
            tables.add(new CoffeeTable("T03", 6, "Khu vực B - Giữa quán"));
            tables.add(new CoffeeTable("T04", 6, "Khu vực B - Giữa quán"));
            tables.add(new CoffeeTable("T05", 8, "Khu vực C - Góc yên tĩnh"));
            tables.add(new CoffeeTable("T06", 4, "Khu vực A - Gần cửa sổ"));
            tables.add(new CoffeeTable("T07", 4, "Khu vực B - Giữa quán"));
            tables.add(new CoffeeTable("T08", 6, "Khu vực C - Góc yên tĩnh"));
            tables.add(new CoffeeTable("T09", 4, "Khu vực A - Gần cửa sổ"));
            tables.add(new CoffeeTable("T10", 8, "Khu vực C - Góc yên tĩnh"));
            
            // Set status cho một số bàn
            tables.get(4).setStatus("OCCUPIED"); // T05
            tables.get(6).setStatus("RESERVED"); // T07
        }
    }
    
    private void displayTables() {
        tablesGrid.getChildren().clear();
        
        int columns = 5; // Số cột hiển thị
        int row = 0;
        int col = 0;
        
        for (CoffeeTable table : tables) {
            VBox tableNode = createTableNode(table);
            tablesGrid.add(tableNode, col, row);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createTableNode(CoffeeTable table) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.getStyleClass().add("table-item");
        
        // Thêm class CSS dựa trên trạng thái
        String status = table.getStatus();
        if ("OCCUPIED".equals(status)) {
            container.getStyleClass().add("occupied");
        } else if ("AVAILABLE".equals(status)) {
            container.getStyleClass().add("available");
        } else if ("RESERVED".equals(status)) {
            container.getStyleClass().add("reserved");
        }
        
        // Tạo hình bàn (Rectangle thay vì Circle)
        Rectangle tableShape = createTableShape(table);
        
        // Tạo text hiển thị số bàn
        Text tableNumber = new Text(table.getTableNumber());
        tableNumber.setFont(Font.font("System", FontWeight.BOLD, 16));
        tableNumber.setFill(Color.WHITE);
        tableNumber.setTextAlignment(TextAlignment.CENTER);
        
        // Tạo text hiển thị trạng thái
        Text statusText = new Text(getStatusText(status));
        statusText.setFont(Font.font("System", 12));
        statusText.setTextAlignment(TextAlignment.CENTER);
        
        // Tạo text hiển thị sức chứa
        Text capacityText = new Text("Sức chứa: " + table.getCapacity() + " người");
        capacityText.setFont(Font.font("System", 10));
        capacityText.setTextAlignment(TextAlignment.CENTER);
        
        // Tạo text hiển thị vị trí
        Text locationText = new Text(table.getLocation());
        locationText.setFont(Font.font("System", 10));
        locationText.setTextAlignment(TextAlignment.CENTER);
        locationText.setWrappingWidth(120);
        
        // Thêm các thành phần vào container
        StackPane tablePane = new StackPane();
        tablePane.getChildren().addAll(tableShape, tableNumber);
        
        container.getChildren().addAll(tablePane, statusText, capacityText, locationText);
        
        // Thêm event handler
        container.setOnMouseClicked(e -> handleTableClick(table));
        
        return container;
    }
    
    private Rectangle createTableShape(CoffeeTable table) {
        Rectangle rectangle = new Rectangle();
        
        // Kích thước dựa trên sức chứa
        int capacity = table.getCapacity();
        if (capacity <= 4) {
            rectangle.setWidth(80);
            rectangle.setHeight(60);
        } else if (capacity <= 6) {
            rectangle.setWidth(100);
            rectangle.setHeight(70);
        } else {
            rectangle.setWidth(120);
            rectangle.setHeight(80);
        }
        
        // Màu sắc dựa trên trạng thái
        String status = table.getStatus();
        if ("OCCUPIED".equals(status)) {
            rectangle.setFill(Color.rgb(231, 76, 60)); // Đỏ
        } else if ("AVAILABLE".equals(status)) {
            rectangle.setFill(Color.rgb(39, 174, 96)); // Xanh lá
        } else if ("RESERVED".equals(status)) {
            rectangle.setFill(Color.rgb(243, 156, 18)); // Cam
        } else {
            rectangle.setFill(Color.rgb(149, 165, 166)); // Xám
        }
        
        rectangle.setArcWidth(10);
        rectangle.setArcHeight(10);
        rectangle.setStroke(Color.WHITE);
        rectangle.setStrokeWidth(2);
        
        return rectangle;
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "AVAILABLE":
                return "Trống";
            case "OCCUPIED":
                return "Có khách";
            case "RESERVED":
                return "Đã đặt";
            case "CLEANING":
                return "Đang dọn";
            default:
                return "Không xác định";
        }
    }
    
    private void handleTableClick(CoffeeTable table) {
        if ("AVAILABLE".equals(table.getStatus())) {
            // Bàn trống - cho phép đặt hàng
            showOrderDialog(table);
        } else if ("OCCUPIED".equals(table.getStatus())) {
            // Bàn có khách - hiển thị thông tin đơn hàng
            showTableDetails(table);
        } else if ("RESERVED".equals(table.getStatus())) {
            // Bàn đã đặt - hiển thị thông tin đặt bàn
            showReservationDetails(table);
        }
    }
    
    private void showOrderDialog(CoffeeTable table) {
        String message = "Bàn " + table.getTableNumber() + "\n" +
                        "Sức chứa: " + table.getCapacity() + " người\n" +
                        "Vị trí: " + table.getLocation() + "\n\n" +
                        "Bạn có muốn tạo đơn hàng cho bàn này không?";
        
        CoffeeShopApplication.showConfirmation("Đặt hàng cho bàn " + table.getTableNumber(), 
            message, () -> openOrderScreen(table));
    }
    
    private void showTableDetails(CoffeeTable table) {
        String message = "Bàn " + table.getTableNumber() + " đang được sử dụng.\n" +
                        "Sức chứa: " + table.getCapacity() + " người\n" +
                        "Vị trí: " + table.getLocation() + "\n\n" +
                        "Bạn có muốn xem chi tiết đơn hàng không?";
        
        CoffeeShopApplication.showConfirmation("Thông tin bàn " + table.getTableNumber(), 
            message, () -> viewTableDetails(table));
    }
    
    private void showReservationDetails(CoffeeTable table) {
        String message = "Bàn " + table.getTableNumber() + " đã được đặt trước.\n" +
                        "Sức chứa: " + table.getCapacity() + " người\n" +
                        "Vị trí: " + table.getLocation();
        
        CoffeeShopApplication.showInfo("Thông tin đặt bàn " + table.getTableNumber(), message);
    }
    
    private void openOrderScreen(CoffeeTable selectedTable) {
        try {
            // Lưu bàn được chọn vào application context
            CoffeeShopApplication.setSelectedTable(selectedTable);
            
            // Chuyển sang màn hình đặt hàng
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/order.fxml"));
            javafx.scene.Parent orderRoot = loader.load();
            
            // Lấy controller của màn hình order
            OrderController orderController = loader.getController();
            
            // Tạo scene mới
            javafx.scene.Scene orderScene = new javafx.scene.Scene(orderRoot);
            orderScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            
            // Lấy stage hiện tại và thay đổi scene
            javafx.stage.Stage currentStage = (javafx.stage.Stage) tablesGrid.getScene().getWindow();
            currentStage.setScene(orderScene);
            currentStage.setTitle("Đặt hàng - Bàn " + selectedTable.getTableNumber());
            
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể mở màn hình đặt hàng: " + e.getMessage());
        }
    }
    
    private void occupyTable(CoffeeTable table) {
        try {
            if (tableDAO.updateStatus(table.getId(), "OCCUPIED")) {
                table.setStatus("OCCUPIED");
                displayTables(); // Refresh display
                CoffeeShopApplication.showInfo("Thành công", "Đã đặt bàn " + table.getTableNumber());
            } else {
                CoffeeShopApplication.showError("Lỗi", "Không thể cập nhật trạng thái bàn");
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể cập nhật trạng thái bàn: " + e.getMessage());
        }
    }
    
    private void viewTableDetails(CoffeeTable table) {
        // Hiển thị dialog với các tùy chọn cho bàn có khách
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Quản lý bàn " + table.getTableNumber());
        alert.setHeaderText("Bàn đang có khách");
        alert.setContentText("Bàn " + table.getTableNumber() + " - " + table.getCapacity() + " người\n" +
                           "Vị trí: " + table.getLocation() + "\n\n" +
                           "Chọn hành động:");
        
        ButtonType viewOrderButton = new ButtonType("📋 Xem đơn hàng");
        ButtonType addMoreButton = new ButtonType("➕ Thêm món");
        ButtonType checkoutButton = new ButtonType("💳 Thanh toán");
        ButtonType freeTableButton = new ButtonType("🔄 Giải phóng bàn");
        ButtonType cancelButton = new ButtonType("❌ Đóng");
        
        alert.getButtonTypes().setAll(viewOrderButton, addMoreButton, checkoutButton, freeTableButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            ButtonType selectedButton = result.get();
            if (selectedButton == viewOrderButton) {
                showOrderDetails(table);
            } else if (selectedButton == addMoreButton) {
                openOrderScreen(table);
            } else if (selectedButton == checkoutButton) {
                processCheckout(table);
            } else if (selectedButton == freeTableButton) {
                freeTable(table);
            }
        }
    }
    
    private void showOrderDetails(CoffeeTable table) {
        // TODO: Hiển thị chi tiết đơn hàng hiện tại
        CoffeeShopApplication.showInfo("Chi tiết đơn hàng", 
            "Đơn hàng cho bàn " + table.getTableNumber() + "\n\n" +
            "Tính năng xem chi tiết đơn hàng sẽ được phát triển sau.");
    }
    
    private void processCheckout(CoffeeTable table) {
        CoffeeShopApplication.showConfirmation("Thanh toán", 
            "Xác nhận thanh toán cho bàn " + table.getTableNumber() + "?\n\n" +
            "Sau khi thanh toán, bàn sẽ được giải phóng.", 
            () -> {
                try {
                    if (tableDAO.updateStatus(table.getId(), "AVAILABLE")) {
                        table.setStatus("AVAILABLE");
                        displayTables(); // Refresh display
                        CoffeeShopApplication.showInfo("Thành công", 
                            "Đã thanh toán và giải phóng bàn " + table.getTableNumber());
                    } else {
                        CoffeeShopApplication.showError("Lỗi", "Không thể cập nhật trạng thái bàn");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoffeeShopApplication.showError("Lỗi", "Không thể xử lý thanh toán: " + e.getMessage());
                }
            });
    }
    
    private void freeTable(CoffeeTable table) {
        CoffeeShopApplication.showConfirmation("Giải phóng bàn", 
            "Xác nhận giải phóng bàn " + table.getTableNumber() + "?\n\n" +
            "Bàn sẽ được đánh dấu là trống.", 
            () -> {
                try {
                    if (tableDAO.updateStatus(table.getId(), "AVAILABLE")) {
                        table.setStatus("AVAILABLE");
                        displayTables(); // Refresh display
                        CoffeeShopApplication.showInfo("Thành công", 
                            "Đã giải phóng bàn " + table.getTableNumber());
                    } else {
                        CoffeeShopApplication.showError("Lỗi", "Không thể cập nhật trạng thái bàn");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoffeeShopApplication.showError("Lỗi", "Không thể giải phóng bàn: " + e.getMessage());
                }
            });
    }
} 