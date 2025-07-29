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
import java.util.ArrayList;
import java.util.List;

public class TablesController {
    
    @FXML
    private GridPane tablesGrid;
    
    @FXML
    private Button addTableButton;
    
    @FXML
    private Button refreshButton;
    
    private List<CoffeeTable> tables;
    private CoffeeTableDAO tableDAO;
    
    @FXML
    public void initialize() {
        tableDAO = new CoffeeTableDAO();
        loadTables();
        displayTables();
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
        String status = table.getStatus();
        String message = "Bàn " + table.getTableNumber() + "\n" +
                        "Trạng thái: " + getStatusText(status) + "\n" +
                        "Sức chứa: " + table.getCapacity() + " người\n" +
                        "Vị trí: " + table.getLocation();
        
        if ("AVAILABLE".equals(status)) {
            CoffeeShopApplication.showConfirmation("Chọn bàn", 
                message + "\n\nBạn có muốn đặt bàn này không?", 
                () -> occupyTable(table));
        } else if ("OCCUPIED".equals(status)) {
            CoffeeShopApplication.showConfirmation("Quản lý bàn", 
                message + "\n\nBạn có muốn xem chi tiết đơn hàng không?", 
                () -> viewTableDetails(table));
        } else {
            CoffeeShopApplication.showInfo("Thông tin bàn", message);
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
        // TODO: Implement view table details
        CoffeeShopApplication.showInfo("Chi tiết bàn", "Tính năng xem chi tiết sẽ được phát triển sau");
    }
} 