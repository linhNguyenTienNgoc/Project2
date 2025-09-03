package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.util.DateUtils;
import com.cafe.util.PriceFormatter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho báo cáo trong Admin Dashboard
 */
public class AdminReportController implements Initializable, DashboardCommunicator {

    // Date Range Selection
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private Button generateReportButton;
    @FXML private Button exportReportButton;
    @FXML private Button refreshButton;

    // Revenue Statistics
    @FXML private Label totalRevenueLabel;
    @FXML private Label totalOrdersLabel;
    @FXML private Label avgOrderValueLabel;
    @FXML private Label dailyAvgRevenueLabel;

    // Charts
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis revenueXAxis;
    @FXML private NumberAxis revenueYAxis;
    
    @FXML private BarChart<String, Number> productSalesChart;
    @FXML private CategoryAxis productXAxis;
    @FXML private NumberAxis productYAxis;
    
    @FXML private PieChart categoryDistributionChart;

    // Tables
    @FXML private TableView<ReportData> reportTable;
    @FXML private TableColumn<ReportData, String> dateColumn;
    @FXML private TableColumn<ReportData, Integer> ordersColumn;
    @FXML private TableColumn<ReportData, Double> revenueColumn;
    @FXML private TableColumn<ReportData, Double> avgValueColumn;

    @FXML private TableView<ProductReportData> productReportTable;
    @FXML private TableColumn<ProductReportData, String> productNameColumn;
    @FXML private TableColumn<ProductReportData, Integer> quantitySoldColumn;
    @FXML private TableColumn<ProductReportData, Double> productRevenueColumn;
    @FXML private TableColumn<ProductReportData, Double> percentageColumn;

    // Export and Print
    @FXML private VBox reportContentPane;
    @FXML private Button printReportButton;

    // Data
    private ObservableList<ReportData> reportDataList = FXCollections.observableArrayList();
    private ObservableList<ProductReportData> productReportDataList = FXCollections.observableArrayList();
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupDatePickers();
            setupReportTypes();
            setupTables();
            setupCharts();
            setupEventHandlers();
            
            // Load default report (last 30 days)
            generateDefaultReport();

            System.out.println("✅ AdminReportController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminReportController: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khởi tạo báo cáo: " + e.getMessage());
        }
    }

    private void setupDatePickers() {
        // Set default date range (last 30 days)
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(30));

        // Set date format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDatePicker.setConverter(DateUtils.createDateConverter(formatter));
        endDatePicker.setConverter(DateUtils.createDateConverter(formatter));
    }

    private void setupReportTypes() {
        reportTypeCombo.setItems(FXCollections.observableArrayList(
            "Báo cáo doanh thu",
            "Báo cáo sản phẩm",
            "Báo cáo khách hàng", 
            "Báo cáo nhân viên",
            "Báo cáo tổng hợp"
        ));
        reportTypeCombo.setValue("Báo cáo doanh thu");
    }

    private void setupTables() {
        // Revenue report table
        dateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getDate()));
        ordersColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getOrders()).asObject());
        revenueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getRevenue()).asObject());
        avgValueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getAvgValue()).asObject());

        // Format currency columns
        revenueColumn.setCellFactory(col -> new TableCell<ReportData, Double>() {
            @Override
            protected void updateItem(Double revenue, boolean empty) {
                super.updateItem(revenue, empty);
                if (empty || revenue == null) {
                    setText(null);
                } else {
                    setText(PriceFormatter.format(revenue));
                }
            }
        });

        avgValueColumn.setCellFactory(col -> new TableCell<ReportData, Double>() {
            @Override
            protected void updateItem(Double avgValue, boolean empty) {
                super.updateItem(avgValue, empty);
                if (empty || avgValue == null) {
                    setText(null);
                } else {
                    setText(PriceFormatter.format(avgValue));
                }
            }
        });

        // Product report table
        productNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProductName()));
        quantitySoldColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getQuantitySold()).asObject());
        productRevenueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getRevenue()).asObject());
        percentageColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getPercentage()).asObject());

        // Format product table cells
        productRevenueColumn.setCellFactory(col -> new TableCell<ProductReportData, Double>() {
            @Override
            protected void updateItem(Double revenue, boolean empty) {
                super.updateItem(revenue, empty);
                if (empty || revenue == null) {
                    setText(null);
                } else {
                    setText(PriceFormatter.format(revenue));
                }
            }
        });

        percentageColumn.setCellFactory(col -> new TableCell<ProductReportData, Double>() {
            @Override
            protected void updateItem(Double percentage, boolean empty) {
                super.updateItem(percentage, empty);
                if (empty || percentage == null) {
                    setText(null);
                } else {
                    setText(String.format("%.1f%%", percentage));
                }
            }
        });

        // Set data
        reportTable.setItems(reportDataList);
        productReportTable.setItems(productReportDataList);
    }

    private void setupCharts() {
        // Revenue chart
        revenueChart.setTitle("Biểu đồ doanh thu theo ngày");
        revenueXAxis.setLabel("Ngày");
        revenueYAxis.setLabel("Doanh thu (VNĐ)");

        // Product sales chart
        productSalesChart.setTitle("Top sản phẩm bán chạy");
        productXAxis.setLabel("Sản phẩm");
        productYAxis.setLabel("Số lượng bán");

        // Category distribution chart
        categoryDistributionChart.setTitle("Phân bố doanh thu theo danh mục");
    }

    private void setupEventHandlers() {
        // Event handlers are now handled by @FXML annotations in the FXML file
        // Auto-generate when date changes
        startDatePicker.setOnAction(e -> generateReport());
        endDatePicker.setOnAction(e -> generateReport());
        reportTypeCombo.setOnAction(e -> generateReport());
    }

    private void generateDefaultReport() {
        generateReport();
    }

    @FXML
    private void handleGenerateReport() {
        generateReport();
    }

    @FXML
    private void handleRefresh() {
        generateReport();
    }

    @FXML
    private void handleExportReport() {
        exportReport();
    }

    @FXML
    private void handlePrintReport() {
        printReport();
    }

    private void generateReport() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            showError("Vui lòng chọn khoảng thời gian");
            return;
        }

        if (startDate.isAfter(endDate)) {
            showError("Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }

        Task<Void> reportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loadRevenueData(startDate, endDate);
                loadProductData(startDate, endDate);
                loadCategoryData(startDate, endDate);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    updateCharts();
                    updateStatistics();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi khi tạo báo cáo: " + getException().getMessage());
                });
            }
        };

        new Thread(reportTask).start();
    }

    private void loadRevenueData(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT DATE(o.order_date) as order_date,
                   COUNT(*) as total_orders,
                   SUM(o.total_amount) as total_revenue,
                   AVG(o.total_amount) as avg_order_value
            FROM orders o
            WHERE DATE(o.order_date) BETWEEN ? AND ?
            GROUP BY DATE(o.order_date)
            ORDER BY order_date
            """;

        List<ReportData> data = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ReportData reportData = new ReportData(
                        rs.getDate("order_date").toString(),
                        rs.getInt("total_orders"),
                        rs.getDouble("total_revenue"),
                        rs.getDouble("avg_order_value")
                    );
                    data.add(reportData);
                }
            }
            
            Platform.runLater(() -> {
                reportDataList.clear();
                reportDataList.addAll(data);
            });
            
        } catch (Exception e) {
            System.err.println("Error loading revenue data: " + e.getMessage());
        }
    }

    private void loadProductData(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT p.product_name as product_name,
                   SUM(od.quantity) as quantity_sold,
                   SUM(od.quantity * od.unit_price) as revenue
            FROM order_details od
            JOIN products p ON od.product_id = p.product_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE DATE(o.order_date) BETWEEN ? AND ?
            GROUP BY p.product_id, p.product_name
            ORDER BY revenue DESC
            LIMIT 10
            """;

        List<ProductReportData> data = new ArrayList<>();
        double totalRevenue = 0;
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    double revenue = rs.getDouble("revenue");
                    totalRevenue += revenue;
                    
                    ProductReportData productData = new ProductReportData(
                        rs.getString("product_name"),
                        rs.getInt("quantity_sold"),
                        revenue,
                        0 // percentage will be calculated later
                    );
                    data.add(productData);
                }
            }
            
            // Calculate percentages
            final double finalTotalRevenue = totalRevenue;
            data.forEach(item -> item.setPercentage((item.getRevenue() / finalTotalRevenue) * 100));
            
            Platform.runLater(() -> {
                productReportDataList.clear();
                productReportDataList.addAll(data);
            });
            
        } catch (Exception e) {
            System.err.println("Error loading product data: " + e.getMessage());
        }
    }

    private void loadCategoryData(LocalDate startDate, LocalDate endDate) {
        String sql = """
            SELECT c.category_name as category_name,
                   SUM(od.quantity * od.unit_price) as revenue
            FROM order_details od
            JOIN products p ON od.product_id = p.product_id
            JOIN categories c ON p.category_id = c.category_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE DATE(o.order_date) BETWEEN ? AND ?
            GROUP BY c.category_id, c.category_name
            ORDER BY revenue DESC
            """;

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pieChartData.add(new PieChart.Data(
                        rs.getString("category_name"),
                        rs.getDouble("revenue")
                    ));
                }
            }
            
            Platform.runLater(() -> {
                categoryDistributionChart.setData(pieChartData);
            });
            
        } catch (Exception e) {
            System.err.println("Error loading category data: " + e.getMessage());
        }
    }

    private void updateCharts() {
        // Update revenue chart
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Doanh thu");
        
        for (ReportData data : reportDataList) {
            revenueSeries.getData().add(new XYChart.Data<>(data.getDate(), data.getRevenue()));
        }
        
        revenueChart.getData().clear();
        revenueChart.getData().add(revenueSeries);

        // Update product sales chart
        XYChart.Series<String, Number> productSeries = new XYChart.Series<>();
        productSeries.setName("Số lượng bán");
        
        productReportDataList.stream()
            .limit(10) // Top 10 products
            .forEach(data -> {
                productSeries.getData().add(new XYChart.Data<>(
                    data.getProductName().length() > 15 ? 
                        data.getProductName().substring(0, 15) + "..." : data.getProductName(),
                    data.getQuantitySold()
                ));
            });
        
        productSalesChart.getData().clear();
        productSalesChart.getData().add(productSeries);
    }

    private void updateStatistics() {
        double totalRevenue = reportDataList.stream()
            .mapToDouble(ReportData::getRevenue)
            .sum();
        
        int totalOrders = reportDataList.stream()
            .mapToInt(ReportData::getOrders)
            .sum();
        
        double avgOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0;
        
        int dayCount = reportDataList.size();
        double dailyAvgRevenue = dayCount > 0 ? totalRevenue / dayCount : 0;

        totalRevenueLabel.setText(PriceFormatter.format(totalRevenue));
        totalOrdersLabel.setText(String.valueOf(totalOrders));
        avgOrderValueLabel.setText(PriceFormatter.format(avgOrderValue));
        dailyAvgRevenueLabel.setText(PriceFormatter.format(dailyAvgRevenue));
    }

    private void exportReport() {
        try {
            String fileName = "BaoCao_" + java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";
            
            // Create Excel file using Apache POI
            org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Báo cáo doanh thu");
            
            // Create header row
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Ngày");
            headerRow.createCell(1).setCellValue("Số đơn hàng");
            headerRow.createCell(2).setCellValue("Doanh thu");
            headerRow.createCell(3).setCellValue("Giá trị TB");
            
            // Add data rows
            int rowNum = 1;
            for (ReportData data : reportTable.getItems()) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(data.getDate());
                row.createCell(1).setCellValue(data.getOrders());
                row.createCell(2).setCellValue(data.getRevenue());
                row.createCell(3).setCellValue(data.getAvgValue());
            }
            
            // Auto-size columns
            for (int i = 0; i < 4; i++) {
                sheet.autoSizeColumn(i);
            }
            
            // Write to file
            try (java.io.FileOutputStream fileOut = new java.io.FileOutputStream(fileName)) {
                workbook.write(fileOut);
                workbook.close();
                showInfo("Thành công", "Đã xuất báo cáo ra file: " + fileName);
            }
            
        } catch (Exception e) {
            showError("Lỗi xuất báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void printReport() {
        try {
            // Create a simple print dialog
            javafx.print.PrinterJob job = javafx.print.PrinterJob.createPrinterJob();
            if (job != null && job.showPrintDialog(reportTable.getScene().getWindow())) {
                
                // Create printable content
                javafx.scene.layout.VBox printContent = new javafx.scene.layout.VBox(10);
                printContent.setPadding(new javafx.geometry.Insets(20));
                
                // Title
                javafx.scene.control.Label title = new javafx.scene.control.Label("BÁO CÁO DOANH THU");
                title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                
                // Date range
                javafx.scene.control.Label dateRange = new javafx.scene.control.Label(
                    "Từ ngày: " + (startDatePicker != null ? startDatePicker.getValue() : "N/A") + 
                    " - Đến ngày: " + (endDatePicker != null ? endDatePicker.getValue() : "N/A"));
                
                // Summary statistics
                javafx.scene.layout.HBox summaryBox = new javafx.scene.layout.HBox(30);
                summaryBox.getChildren().addAll(
                    new javafx.scene.control.Label("Tổng doanh thu: " + totalRevenueLabel.getText()),
                    new javafx.scene.control.Label("Tổng đơn hàng: " + totalOrdersLabel.getText()),
                    new javafx.scene.control.Label("TB/đơn: " + avgOrderValueLabel.getText())
                );
                
                printContent.getChildren().addAll(title, dateRange, summaryBox);
                
                // Print
                boolean success = job.printPage(printContent);
                if (success) {
                    job.endJob();
                    showInfo("Thành công", "Đã gửi báo cáo đến máy in");
                } else {
                    showError("Lỗi in báo cáo");
                }
            }
        } catch (Exception e) {
            showError("Lỗi in báo cáo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Data classes
    public static class ReportData {
        private String date;
        private int orders;
        private double revenue;
        private double avgValue;

        public ReportData(String date, int orders, double revenue, double avgValue) {
            this.date = date;
            this.orders = orders;
            this.revenue = revenue;
            this.avgValue = avgValue;
        }

        public String getDate() { return date; }
        public int getOrders() { return orders; }
        public double getRevenue() { return revenue; }
        public double getAvgValue() { return avgValue; }
    }

    public static class ProductReportData {
        private String productName;
        private int quantitySold;
        private double revenue;
        private double percentage;

        public ProductReportData(String productName, int quantitySold, double revenue, double percentage) {
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
            this.percentage = percentage;
        }

        public String getProductName() { return productName; }
        public int getQuantitySold() { return quantitySold; }
        public double getRevenue() { return revenue; }
        public double getPercentage() { return percentage; }
        public void setPercentage(double percentage) { this.percentage = percentage; }
    }

    public static class CustomerData {
        private String customerName;
        private int orderCount;
        private double totalSpent;
        private String lastVisit;

        public CustomerData(String customerName, int orderCount, double totalSpent, String lastVisit) {
            this.customerName = customerName;
            this.orderCount = orderCount;
            this.totalSpent = totalSpent;
            this.lastVisit = lastVisit;
        }

        public String getCustomerName() { return customerName; }
        public int getOrderCount() { return orderCount; }
        public double getTotalSpent() { return totalSpent; }
        public String getLastVisit() { return lastVisit; }
    }

    // Dashboard Communication
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminReportController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}
