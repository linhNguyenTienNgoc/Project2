package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.service.ReportService;

import com.cafe.util.AlertUtils;
import com.cafe.util.PriceFormatter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * ReportController - Báo cáo thống kê cho Admin
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class ReportController implements Initializable, DashboardCommunicator {

    // FXML Components
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private Button filterButton;
    
    @FXML private BarChart<String, Number> dailyRevenueChart;
    @FXML private CategoryAxis revenueXAxis;
    @FXML private NumberAxis revenueYAxis;
    
    @FXML private PieChart categoryPieChart;
    
    @FXML private LineChart<String, Number> monthlyComparisonChart;
    @FXML private CategoryAxis comparisonXAxis;
    @FXML private NumberAxis comparisonYAxis;
    
    @FXML private TableView<ReportDetailData> reportDetailTable;
    @FXML private TableColumn<ReportDetailData, String> dateColumn;
    @FXML private TableColumn<ReportDetailData, Integer> orderCountColumn;
    @FXML private TableColumn<ReportDetailData, String> revenueColumn;
    @FXML private TableColumn<ReportDetailData, Integer> customerCountColumn;

    // Services and Data
    private ReportService reportService;
    // private OrderService orderService; // TODO: Implement if needed for more complex queries
    private ObservableList<ReportDetailData> reportData = FXCollections.observableArrayList();
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🚀 Initializing ReportController...");
            
            reportService = new ReportService();
            // orderService = new OrderService(); // TODO: Initialize if needed
            
            setupDatePickers();
            setupTable();
            setupCharts();
            loadDefaultReportData();
            loadStylesheet();
            
            System.out.println("✅ ReportController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing ReportController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo giao diện báo cáo: " + e.getMessage());
        }
    }
    
    private void setupDatePickers() {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6);
        
        startDatePicker.setValue(startDate);
        endDatePicker.setValue(endDate);
        
        System.out.println("📅 Date range initialized: " + startDate + " to " + endDate);
    }
    
    private void setupTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        orderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        customerCountColumn.setCellValueFactory(new PropertyValueFactory<>("customerCount"));
        
        reportDetailTable.setItems(reportData);
        reportDetailTable.setPlaceholder(new Label("Không có dữ liệu để hiển thị"));
        
        System.out.println("📊 Table setup completed");
    }
    
    private void setupCharts() {
        dailyRevenueChart.setTitle("Doanh thu theo ngày");
        revenueXAxis.setLabel("Ngày");
        revenueYAxis.setLabel("Doanh thu (VNĐ)");
        
        categoryPieChart.setTitle("Doanh thu theo danh mục sản phẩm");
        
        monthlyComparisonChart.setTitle("So sánh doanh thu tháng");
        comparisonXAxis.setLabel("Tháng");
        comparisonYAxis.setLabel("Doanh thu (VNĐ)");
        
        System.out.println("📈 Charts setup completed");
    }
    
    private void loadStylesheet() {
        try {
            String cssFile = getClass().getResource("/css/report.css").toExternalForm();
            if (filterButton.getScene() != null) {
                filterButton.getScene().getStylesheets().add(cssFile);
                System.out.println("🎨 Stylesheet loaded successfully");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Could not load stylesheet: " + e.getMessage());
        }
    }

    private void loadDefaultReportData() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null) {
            loadReportData(startDate, endDate);
        }
    }
    
    public void loadReportData(LocalDate fromDate, LocalDate toDate) {
        System.out.println("📊 Loading report data from " + fromDate + " to " + toDate);
        
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                loadDailyRevenueChart(fromDate, toDate);
                loadCategoryPieChart(fromDate, toDate);
                loadMonthlyComparisonChart();
                loadReportDetailTable(fromDate, toDate);
                return null;
            }
            
            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    System.out.println("✅ Report data loaded successfully");
                });
            }
            
            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Failed to load report data: " + getException().getMessage());
                    getException().printStackTrace();
                    AlertUtils.showError("Lỗi", "Không thể tải dữ liệu báo cáo: " + getException().getMessage());
                });
            }
        };
        
        new Thread(loadTask).start();
    }
    
    private void loadDailyRevenueChart(LocalDate fromDate, LocalDate toDate) {
        Platform.runLater(() -> {
            try {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu");
                
                LocalDate currentDate = fromDate;
                while (!currentDate.isAfter(toDate)) {
                    double revenue = reportService.getRevenueByDateRange(currentDate, currentDate);
                    String dateLabel = currentDate.format(DateTimeFormatter.ofPattern("dd/MM"));
                    series.getData().add(new XYChart.Data<>(dateLabel, revenue));
                    currentDate = currentDate.plusDays(1);
                }
                
                dailyRevenueChart.getData().clear();
                dailyRevenueChart.getData().add(series);
                
                System.out.println("📊 Daily revenue chart updated");
                
            } catch (Exception e) {
                System.err.println("❌ Error updating daily revenue chart: " + e.getMessage());
            }
        });
    }
    
    private void loadCategoryPieChart(LocalDate fromDate, LocalDate toDate) {
        Platform.runLater(() -> {
            try {
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                
                // Mock data cho categories
                pieChartData.add(new PieChart.Data("Cà phê", 40));
                pieChartData.add(new PieChart.Data("Trà", 25));
                pieChartData.add(new PieChart.Data("Bánh ngọt", 20));
                pieChartData.add(new PieChart.Data("Đồ uống lạnh", 10));
                pieChartData.add(new PieChart.Data("Khác", 5));
                
                categoryPieChart.setData(pieChartData);
                
                System.out.println("🍰 Category pie chart updated");
                
            } catch (Exception e) {
                System.err.println("❌ Error updating category pie chart: " + e.getMessage());
            }
        });
    }
    
    private void loadMonthlyComparisonChart() {
        Platform.runLater(() -> {
            try {
                LocalDate now = LocalDate.now();
                LocalDate thisMonthStart = now.withDayOfMonth(1);
                LocalDate thisMonthEnd = now;
                
                LocalDate lastMonthStart = thisMonthStart.minusMonths(1);
                LocalDate lastMonthEnd = thisMonthStart.minusDays(1);
                
                double thisMonthRevenue = reportService.getRevenueByDateRange(thisMonthStart, thisMonthEnd);
                double lastMonthRevenue = reportService.getRevenueByDateRange(lastMonthStart, lastMonthEnd);
                
                XYChart.Series<String, Number> thisMonthSeries = new XYChart.Series<>();
                thisMonthSeries.setName("Tháng này");
                thisMonthSeries.getData().add(new XYChart.Data<>("Tháng " + now.getMonthValue(), thisMonthRevenue));
                
                XYChart.Series<String, Number> lastMonthSeries = new XYChart.Series<>();
                lastMonthSeries.setName("Tháng trước");
                lastMonthSeries.getData().add(new XYChart.Data<>("Tháng " + now.minusMonths(1).getMonthValue(), lastMonthRevenue));
                
                monthlyComparisonChart.getData().clear();
                monthlyComparisonChart.getData().add(lastMonthSeries);
                monthlyComparisonChart.getData().add(thisMonthSeries);
                
                System.out.println("📈 Monthly comparison chart updated");
                
            } catch (Exception e) {
                System.err.println("❌ Error updating monthly comparison chart: " + e.getMessage());
            }
        });
    }
    
    private void loadReportDetailTable(LocalDate fromDate, LocalDate toDate) {
        Platform.runLater(() -> {
            try {
                reportData.clear();
                
                LocalDate currentDate = fromDate;
                while (!currentDate.isAfter(toDate)) {
                    double revenue = reportService.getRevenueByDateRange(currentDate, currentDate);
                    long orderCount = reportService.getOrderCountByDateRange(currentDate, currentDate);
                    
                    int customerCount = (int) (orderCount * 0.8);
                    
                    String dateStr = currentDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    String revenueStr = PriceFormatter.format(revenue);
                    
                    ReportDetailData detailData = new ReportDetailData(
                        dateStr,
                        (int) orderCount,
                        revenueStr,
                        customerCount
                    );
                    
                    reportData.add(detailData);
                    currentDate = currentDate.plusDays(1);
                }
                
                System.out.println("📋 Report detail table updated with " + reportData.size() + " rows");
                
            } catch (Exception e) {
                System.err.println("❌ Error updating report detail table: " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleFilterAction() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate == null || endDate == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn khoảng thời gian");
            return;
        }
        
        if (startDate.isAfter(endDate)) {
            AlertUtils.showWarning("Cảnh báo", "Ngày bắt đầu không thể sau ngày kết thúc");
            return;
        }
        
        loadReportData(startDate, endDate);
        
        System.out.println("🔍 Filter applied: " + startDate + " to " + endDate);
    }

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ ReportController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }

    // =====================================================
    // DATA CLASSES FOR REPORTS
    // =====================================================

    public static class ReportDetailData {
        private String date;
        private int orderCount;
        private String revenue;
        private int customerCount;
        
        public ReportDetailData(String date, int orderCount, String revenue, int customerCount) {
            this.date = date;
            this.orderCount = orderCount;
            this.revenue = revenue;
            this.customerCount = customerCount;
        }
        
        public String getDate() { return date; }
        public int getOrderCount() { return orderCount; }
        public String getRevenue() { return revenue; }
        public int getCustomerCount() { return customerCount; }
        
        public void setDate(String date) { this.date = date; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }
        public void setRevenue(String revenue) { this.revenue = revenue; }
        public void setCustomerCount(int customerCount) { this.customerCount = customerCount; }
    }

    /**
     * Data class for sales reports
     */
    public static class SalesData {
        private String date;
        private int orderId;
        private String customerName;
        private double totalAmount;
        private String status;
        private String items;
        
        public SalesData() {}
        
        public SalesData(String date, int orderId, String customerName, double totalAmount, String status, String items) {
            this.date = date;
            this.orderId = orderId;
            this.customerName = customerName;
            this.totalAmount = totalAmount;
            this.status = status;
            this.items = items;
        }
        
        // Getters and Setters
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public int getOrderId() { return orderId; }
        public void setOrderId(int orderId) { this.orderId = orderId; }
        
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public double getTotalAmount() { return totalAmount; }
        public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public String getItems() { return items; }
        public void setItems(String items) { this.items = items; }
    }

    /**
     * Data class for product reports
     */
    public static class ProductData {
        private String productName;
        private String category;
        private double price;
        private int quantitySold;
        private double revenue;
        private int stockLevel;
        
        public ProductData() {}
        
        public ProductData(String productName, String category, double price, int quantitySold, double revenue, int stockLevel) {
            this.productName = productName;
            this.category = category;
            this.price = price;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
            this.stockLevel = stockLevel;
        }
        
        // Getters and Setters
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
        
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
        
        public int getQuantitySold() { return quantitySold; }
        public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
        
        public double getRevenue() { return revenue; }
        public void setRevenue(double revenue) { this.revenue = revenue; }
        
        public int getStockLevel() { return stockLevel; }
        public void setStockLevel(int stockLevel) { this.stockLevel = stockLevel; }
    }

    /**
     * Data class for customer reports
     */
    public static class CustomerData {
        private int customerId;
        private String fullName;
        private String phone;
        private String email;
        private int totalOrders;
        private double totalSpent;
        private double avgOrderValue;
        private String lastOrderDate;
        private int loyaltyPoints;
        
        public CustomerData() {}
        
        public CustomerData(int customerId, String fullName, String phone, String email, 
                           int totalOrders, double totalSpent, double avgOrderValue, 
                           String lastOrderDate, int loyaltyPoints) {
            this.customerId = customerId;
            this.fullName = fullName;
            this.phone = phone;
            this.email = email;
            this.totalOrders = totalOrders;
            this.totalSpent = totalSpent;
            this.avgOrderValue = avgOrderValue;
            this.lastOrderDate = lastOrderDate;
            this.loyaltyPoints = loyaltyPoints;
        }
        
        // Getters and Setters
        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public int getTotalOrders() { return totalOrders; }
        public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }
        
        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }
        
        public double getAvgOrderValue() { return avgOrderValue; }
        public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }
        
        public String getLastOrderDate() { return lastOrderDate; }
        public void setLastOrderDate(String lastOrderDate) { this.lastOrderDate = lastOrderDate; }
        
        public int getLoyaltyPoints() { return loyaltyPoints; }
        public void setLoyaltyPoints(int loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }
    }
}
