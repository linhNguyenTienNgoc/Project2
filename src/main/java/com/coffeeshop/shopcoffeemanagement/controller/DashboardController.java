package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.dao.CoffeeTableDAO;
import com.coffeeshop.shopcoffeemanagement.dao.MenuDAO;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import com.coffeeshop.shopcoffeemanagement.model.Menu;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class DashboardController {
    
    @FXML
    private VBox dashboardContainer;
    
    @FXML
    private Label totalTablesLabel;
    
    @FXML
    private Label availableTablesLabel;
    
    @FXML
    private Label occupiedTablesLabel;
    
    @FXML
    private Label totalMenuItemsLabel;
    
    @FXML
    private Label currentTimeLabel;
    
    @FXML
    private VBox tableStatusChart;
    
    @FXML
    private VBox menuCategoryChart;
    
    @FXML
    private VBox recentActivityBox;
    
    private CoffeeTableDAO tableDAO;
    private MenuDAO menuDAO;
    
    @FXML
    public void initialize() {
        tableDAO = new CoffeeTableDAO();
        menuDAO = new MenuDAO();
        
        loadDashboardData();
        setupCharts();
        setupRecentActivity();
        startTimeUpdate();
    }
    
    private void loadDashboardData() {
        try {
            // Load table statistics
            List<CoffeeTable> tables = tableDAO.findAll();
            long totalTables = tables.size();
            long availableTables = tables.stream().filter(t -> "AVAILABLE".equals(t.getStatus())).count();
            long occupiedTables = tables.stream().filter(t -> "OCCUPIED".equals(t.getStatus())).count();
            
            totalTablesLabel.setText(String.valueOf(totalTables));
            availableTablesLabel.setText(String.valueOf(availableTables));
            occupiedTablesLabel.setText(String.valueOf(occupiedTables));
            
            // Load menu statistics
            List<Menu> menuItems = menuDAO.findAll();
            totalMenuItemsLabel.setText(String.valueOf(menuItems.size()));
            
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải dữ liệu dashboard: " + e.getMessage());
        }
    }
    
    private void setupCharts() {
        try {
            // Table Status Chart
            List<CoffeeTable> tables = tableDAO.findAll();
            Map<String, Long> tableStatusCount = tables.stream()
                .collect(Collectors.groupingBy(CoffeeTable::getStatus, Collectors.counting()));
            
            PieChart tableChart = new PieChart();
            tableChart.setTitle("Trạng thái bàn");
            tableChart.setLabelsVisible(true);
            
            if (tableStatusCount.containsKey("AVAILABLE")) {
                PieChart.Data availableData = new PieChart.Data(getStatusDisplayName("AVAILABLE"), tableStatusCount.get("AVAILABLE"));
                availableData.getNode().setStyle("-fx-pie-color: #4ecdc4;");
                tableChart.getData().add(availableData);
            }
            
            if (tableStatusCount.containsKey("OCCUPIED")) {
                PieChart.Data occupiedData = new PieChart.Data(getStatusDisplayName("OCCUPIED"), tableStatusCount.get("OCCUPIED"));
                occupiedData.getNode().setStyle("-fx-pie-color: #ff6b6b;");
                tableChart.getData().add(occupiedData);
            }
            
            if (tableStatusCount.containsKey("RESERVED")) {
                PieChart.Data reservedData = new PieChart.Data(getStatusDisplayName("RESERVED"), tableStatusCount.get("RESERVED"));
                reservedData.getNode().setStyle("-fx-pie-color: #ffa726;");
                tableChart.getData().add(reservedData);
            }
            
            tableStatusChart.getChildren().add(tableChart);
            
            // Menu Category Chart
            List<Menu> menuItems = menuDAO.findAll();
            Map<String, Long> categoryCount = menuItems.stream()
                .collect(Collectors.groupingBy(Menu::getCategory, Collectors.counting()));
            
            PieChart menuChart = new PieChart();
            menuChart.setTitle("Danh mục menu");
            menuChart.setLabelsVisible(true);
            
            Color[] colors = {Color.web("#667eea"), Color.web("#764ba2"), Color.web("#f093fb"), 
                            Color.web("#f5576c"), Color.web("#4facfe"), Color.web("#00f2fe")};
            int colorIndex = 0;
            
            for (Map.Entry<String, Long> entry : categoryCount.entrySet()) {
                PieChart.Data data = new PieChart.Data(getCategoryDisplayName(entry.getKey()), entry.getValue());
                data.getNode().setStyle("-fx-pie-color: " + colors[colorIndex % colors.length].toString().replace("0x", "#") + ";");
                menuChart.getData().add(data);
                colorIndex++;
            }
            
            menuCategoryChart.getChildren().add(menuChart);
            
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tạo biểu đồ: " + e.getMessage());
        }
    }
    
    private String getCategoryDisplayName(String category) {
        switch (category) {
            case "COFFEE": return "Cà phê";
            case "TEA": return "Trà";
            case "JUICE": return "Nước ép";
            case "DESSERT": return "Tráng miệng";
            case "FOOD": return "Đồ ăn";
            case "SMOOTHIE": return "Sinh tố";
            default: return category;
        }
    }
    
    private String getStatusDisplayName(String status) {
        switch (status) {
            case "AVAILABLE": return "Có sẵn";
            case "OCCUPIED": return "Đang sử dụng";
            case "RESERVED": return "Đã đặt";
            default: return status;
        }
    }
    
    private void setupRecentActivity() {
        VBox activityContainer = new VBox(10);
        activityContainer.setPadding(new Insets(15));
        activityContainer.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        Label titleLabel = new Label("Hoạt động gần đây");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        
        activityContainer.getChildren().add(titleLabel);
        
        // TODO: Load recent activities from database
        // For now, show empty state
        Label emptyLabel = new Label("Chưa có hoạt động gần đây");
        emptyLabel.setFont(Font.font("System", 12));
        emptyLabel.setTextFill(Color.web("#95a5a6"));
        activityContainer.getChildren().add(emptyLabel);
        
        recentActivityBox.getChildren().add(activityContainer);
    }
    
    private void addActivityItem(VBox container, String activity, String time, String color) {
        HBox itemBox = new HBox(10);
        itemBox.setAlignment(Pos.CENTER_LEFT);
        
        // Activity indicator
        VBox indicator = new VBox();
        indicator.setPrefWidth(4);
        indicator.setPrefHeight(20);
        indicator.setStyle("-fx-background-color: " + color + "; -fx-background-radius: 2;");
        
        // Activity content
        VBox content = new VBox(2);
        Label activityLabel = new Label(activity);
        activityLabel.setFont(Font.font("System", 12));
        activityLabel.setTextFill(Color.web("#2c3e50"));
        
        Label timeLabel = new Label(time);
        timeLabel.setFont(Font.font("System", 10));
        timeLabel.setTextFill(Color.web("#7f8c8d"));
        
        content.getChildren().addAll(activityLabel, timeLabel);
        
        itemBox.getChildren().addAll(indicator, content);
        container.getChildren().add(itemBox);
    }
    
    private void startTimeUpdate() {
        updateTime();
        // Update time every second
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
            new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1), e -> updateTime())
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }
    
    private void updateTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");
        currentTimeLabel.setText(now.format(formatter));
    }
    
    @FXML
    private void refreshDashboard() {
        loadDashboardData();
        setupCharts();
        CoffeeShopApplication.showInfo("Thành công", "Đã làm mới dashboard");
    }
    
    @FXML
    private void exportReport() {
        // TODO: Implement export functionality
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng xuất báo cáo sẽ được phát triển sau");
    }
    
    @FXML
    private void showAnalytics() {
        // TODO: Implement analytics view
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng phân tích sẽ được phát triển sau");
    }
} 