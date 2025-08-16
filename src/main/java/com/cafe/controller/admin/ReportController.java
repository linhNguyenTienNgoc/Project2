package com.cafe.controller.admin;

import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.CustomerDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.dao.base.CustomerDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.Product;
import com.cafe.model.entity.Customer;
import com.cafe.config.DatabaseConfig;
import com.cafe.util.ExcelExporter;
import com.cafe.util.PDFExporter;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.sql.*;

/**
 * Controller cho màn hình báo cáo và thống kê với biểu đồ từ database
 *
 * @author Team 2_C2406L
 * @version 2.0.0
 */
public class ReportController implements Initializable {

    // Filter Controls
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;
    @FXML private Button generateReportButton;
    @FXML private Button exportExcelButton;
    @FXML private Button exportPDFButton;

    // Statistics Labels
    @FXML private Label todayRevenueLabel;
    @FXML private Label todayOrdersLabel;
    @FXML private Label newCustomersLabel;
    @FXML private Label monthlyProfitLabel;

    // Tables
    @FXML private TableView<SalesData> salesTable;
    @FXML private TableColumn<SalesData, String> dateColumn;
    @FXML private TableColumn<SalesData, String> revenueColumn;
    @FXML private TableColumn<SalesData, String> orderCountColumn;
    @FXML private TableColumn<SalesData, String> avgOrderColumn;
    @FXML private TableColumn<SalesData, String> growthColumn;

    @FXML private TableView<ProductData> productTable;
    @FXML private TableColumn<ProductData, String> productNameColumn;
    @FXML private TableColumn<ProductData, String> quantityColumn;
    @FXML private TableColumn<ProductData, String> productRevenueColumn;
    @FXML private TableColumn<ProductData, String> percentageColumn;

    @FXML private TableView<CustomerData> customerTable;
    @FXML private TableColumn<CustomerData, String> customerNameColumn;
    @FXML private TableColumn<CustomerData, String> customerOrderCountColumn;
    @FXML private TableColumn<CustomerData, String> customerTotalColumn;
    @FXML private TableColumn<CustomerData, String> lastOrderColumn;

    // Chart containers (these will be added programmatically)
    @FXML private VBox salesChartContainer;
    @FXML private VBox productChartContainer;
    @FXML private VBox customerChartContainer;

    // Data lists
    private ObservableList<SalesData> salesList = FXCollections.observableArrayList();
    private ObservableList<ProductData> productList = FXCollections.observableArrayList();
    private ObservableList<CustomerData> customerList = FXCollections.observableArrayList();

    // DAOs
    private OrderDAO orderDAO;
    private ProductDAO productDAO;
    private CustomerDAO customerDAO;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize DAOs
            initializeDAOs();

            // Setup UI components
            setupReportTypeCombo();
            setupTables();
            setupEventHandlers();
            setupDatePickers();

            // Load real-time data
            loadTodayStatistics();
            loadDefaultReport();

            System.out.println("✅ Enhanced ReportController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing ReportController: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khởi tạo báo cáo: " + e.getMessage());
        }
    }

    /**
     * Initialize Data Access Objects
     */
    private void initializeDAOs() {
        // DAOs will be created with fresh connections when needed
        // This follows the pattern used in other parts of the application
    }

    /**
     * Setup date pickers with default values
     */
    private void setupDatePickers() {
        // Set default date range (last 7 days)
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(LocalDate.now().minusDays(7));
    }

    /**
     * Setup report type combo box
     */
    private void setupReportTypeCombo() {
        reportTypeCombo.getItems().addAll(
                "Báo cáo doanh thu",
                "Báo cáo sản phẩm",
                "Báo cáo khách hàng",
                "Báo cáo tổng hợp"
        );
        reportTypeCombo.setValue("Báo cáo doanh thu");
    }

    /**
     * Setup all tables
     */
    private void setupTables() {
        setupSalesTable();
        setupProductTable();
        setupCustomerTable();
    }

    /**
     * Setup sales table
     */
    private void setupSalesTable() {
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        revenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        orderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        avgOrderColumn.setCellValueFactory(new PropertyValueFactory<>("avgOrder"));
        growthColumn.setCellValueFactory(new PropertyValueFactory<>("growth"));

        salesTable.setItems(salesList);
    }

    /**
     * Setup product table
     */
    private void setupProductTable() {
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productRevenueColumn.setCellValueFactory(new PropertyValueFactory<>("revenue"));
        percentageColumn.setCellValueFactory(new PropertyValueFactory<>("percentage"));

        productTable.setItems(productList);
    }

    /**
     * Setup customer table
     */
    private void setupCustomerTable() {
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerOrderCountColumn.setCellValueFactory(new PropertyValueFactory<>("orderCount"));
        customerTotalColumn.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        lastOrderColumn.setCellValueFactory(new PropertyValueFactory<>("lastOrder"));

        customerTable.setItems(customerList);
    }

    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        generateReportButton.setOnAction(e -> generateReport());
        exportExcelButton.setOnAction(e -> exportToExcel());
        exportPDFButton.setOnAction(e -> exportToPDF());

        reportTypeCombo.setOnAction(e -> generateReport());
        startDatePicker.setOnAction(e -> generateReport());
        endDatePicker.setOnAction(e -> generateReport());
    }

    /**
     * Load today's statistics from database
     */
    private void loadTodayStatistics() {
        try {
            LocalDate today = LocalDate.now();

            // Today's revenue
            double todayRevenue = getTodayRevenue();
            todayRevenueLabel.setText(formatCurrency(todayRevenue));

            // Today's orders count
            int todayOrders = getTodayOrdersCount();
            todayOrdersLabel.setText(String.valueOf(todayOrders));

            // New customers this month
            int newCustomers = getNewCustomersThisMonth();
            newCustomersLabel.setText(String.valueOf(newCustomers));

            // Monthly profit
            double monthlyProfit = getMonthlyProfit();
            monthlyProfitLabel.setText(formatCurrency(monthlyProfit));

        } catch (Exception e) {
            System.err.println("Error loading today's statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get today's revenue from database
     */
    private double getTodayRevenue() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(final_amount), 0) as total_revenue
            FROM orders 
            WHERE DATE(order_date) = CURDATE() 
            AND payment_status = 'paid'
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("total_revenue");
            }
            return 0.0;
        }
    }

    /**
     * Get today's orders count
     */
    private int getTodayOrdersCount() throws SQLException {
        String sql = """
            SELECT COUNT(*) as order_count
            FROM orders 
            WHERE DATE(order_date) = CURDATE()
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("order_count");
            }
            return 0;
        }
    }

    /**
     * Get new customers this month
     */
    private int getNewCustomersThisMonth() throws SQLException {
        String sql = """
            SELECT COUNT(*) as new_customers
            FROM customers 
            WHERE YEAR(created_at) = YEAR(CURDATE()) 
            AND MONTH(created_at) = MONTH(CURDATE())
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("new_customers");
            }
            return 0;
        }
    }

    /**
     * Get monthly profit
     */
    private double getMonthlyProfit() throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(od.total_price - (p.cost_price * od.quantity)), 0) as profit
            FROM orders o
            JOIN order_details od ON o.order_id = od.order_id
            JOIN products p ON od.product_id = p.product_id
            WHERE YEAR(o.order_date) = YEAR(CURDATE()) 
            AND MONTH(o.order_date) = MONTH(CURDATE())
            AND o.payment_status = 'paid'
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getDouble("profit");
            }
            return 0.0;
        }
    }

    /**
     * Load default report on startup
     */
    private void loadDefaultReport() {
        generateReport();
    }

    /**
     * Generate report based on selected type and date range
     */
    @FXML
    private void generateReport() {
        try {
            String reportType = reportTypeCombo.getValue();
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

            System.out.println("Generating report: " + reportType + " from " + startDate + " to " + endDate);

            switch (reportType) {
                case "Báo cáo doanh thu":
                    loadSalesReport(startDate, endDate);
                    break;
                case "Báo cáo sản phẩm":
                    loadProductReport(startDate, endDate);
                    break;
                case "Báo cáo khách hàng":
                    loadCustomerReport(startDate, endDate);
                    break;
                case "Báo cáo tổng hợp":
                    loadAllReports(startDate, endDate);
                    break;
            }

        } catch (Exception e) {
            System.err.println("Error generating report: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi tạo báo cáo: " + e.getMessage());
        }
    }

    /**
     * Load sales report from database
     */
    private void loadSalesReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        salesList.clear();

        String sql = """
            SELECT 
                DATE(order_date) as order_date,
                COUNT(*) as order_count,
                COALESCE(SUM(final_amount), 0) as revenue,
                COALESCE(AVG(final_amount), 0) as avg_order
            FROM orders 
            WHERE DATE(order_date) BETWEEN ? AND ?
            AND payment_status = 'paid'
            GROUP BY DATE(order_date)
            ORDER BY order_date
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                double previousRevenue = 0;

                while (rs.next()) {
                    String date = rs.getDate("order_date").toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    double revenue = rs.getDouble("revenue");
                    int orderCount = rs.getInt("order_count");
                    double avgOrder = rs.getDouble("avg_order");

                    // Calculate growth rate
                    String growth = "0%";
                    if (previousRevenue > 0) {
                        double growthRate = ((revenue - previousRevenue) / previousRevenue) * 100;
                        growth = String.format("%+.1f%%", growthRate);
                    }

                    salesList.add(new SalesData(
                            date,
                            formatCurrency(revenue),
                            String.valueOf(orderCount),
                            formatCurrency(avgOrder),
                            growth
                    ));

                    previousRevenue = revenue;
                }
            }
        }

        // Create and display chart
        createSalesChart();
    }

    /**
     * Load product report from database
     */
    private void loadProductReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        productList.clear();

        String sql = """
            SELECT 
                p.product_name,
                SUM(od.quantity) as total_quantity,
                SUM(od.total_price) as total_revenue
            FROM order_details od
            JOIN products p ON od.product_id = p.product_id
            JOIN orders o ON od.order_id = o.order_id
            WHERE DATE(o.order_date) BETWEEN ? AND ?
            AND o.payment_status = 'paid'
            GROUP BY p.product_id, p.product_name
            ORDER BY total_revenue DESC
            LIMIT 10
        """;

        // First, get total revenue for percentage calculation
        double totalRevenue = getTotalRevenueForPeriod(startDate, endDate);

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("total_quantity");
                    double revenue = rs.getDouble("total_revenue");
                    double percentage = totalRevenue > 0 ? (revenue / totalRevenue) * 100 : 0;

                    productList.add(new ProductData(
                            productName,
                            String.valueOf(quantity),
                            formatCurrency(revenue),
                            String.format("%.1f%%", percentage)
                    ));
                }
            }
        }

        // Create and display chart
        createProductChart();
    }

    /**
     * Load customer report from database
     */
    private void loadCustomerReport(LocalDate startDate, LocalDate endDate) throws SQLException {
        customerList.clear();

        String sql = """
            SELECT 
                c.full_name,
                COUNT(o.order_id) as order_count,
                COALESCE(SUM(o.final_amount), 0) as total_spent,
                MAX(o.order_date) as last_order
            FROM customers c
            LEFT JOIN orders o ON c.customer_id = o.customer_id
            WHERE o.order_date IS NULL OR DATE(o.order_date) BETWEEN ? AND ?
            GROUP BY c.customer_id, c.full_name
            HAVING order_count > 0
            ORDER BY total_spent DESC
            LIMIT 20
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String customerName = rs.getString("full_name");
                    int orderCount = rs.getInt("order_count");
                    double totalSpent = rs.getDouble("total_spent");
                    Date lastOrderDate = rs.getDate("last_order");
                    String lastOrder = lastOrderDate != null ?
                            lastOrderDate.toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A";

                    customerList.add(new CustomerData(
                            customerName,
                            String.valueOf(orderCount),
                            formatCurrency(totalSpent),
                            lastOrder
                    ));
                }
            }
        }

        // Create and display chart
        createCustomerChart();
    }

    /**
     * Load all reports
     */
    private void loadAllReports(LocalDate startDate, LocalDate endDate) throws SQLException {
        loadSalesReport(startDate, endDate);
        loadProductReport(startDate, endDate);
        loadCustomerReport(startDate, endDate);
    }

    /**
     * Get total revenue for a period
     */
    private double getTotalRevenueForPeriod(LocalDate startDate, LocalDate endDate) throws SQLException {
        String sql = """
            SELECT COALESCE(SUM(final_amount), 0) as total_revenue
            FROM orders 
            WHERE DATE(order_date) BETWEEN ? AND ?
            AND payment_status = 'paid'
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(startDate));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_revenue");
                }
                return 0.0;
            }
        }
    }

    /**
     * Create sales chart
     */
    private void createSalesChart() {
        // Clear existing chart
        salesChartContainer.getChildren().clear();

        // Create line chart for sales trend
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Ngày");
        yAxis.setLabel("Doanh thu (VNĐ)");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Biểu đồ doanh thu theo ngày");
        lineChart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (SalesData data : salesList) {
            String revenueStr = data.getRevenue().replace(",", "").replace(" VNĐ", "");
            try {
                double revenue = Double.parseDouble(revenueStr);
                series.getData().add(new XYChart.Data<>(data.getDate(), revenue));
            } catch (NumberFormatException e) {
                System.err.println("Error parsing revenue: " + data.getRevenue());
            }
        }

        lineChart.getData().add(series);
        salesChartContainer.getChildren().add(lineChart);
    }

    /**
     * Create product chart
     */
    private void createProductChart() {
        // Clear existing chart
        productChartContainer.getChildren().clear();

        // Create pie chart for product distribution
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Phân bố doanh thu theo sản phẩm");
        pieChart.setPrefHeight(300);

        for (ProductData data : productList) {
            String revenueStr = data.getRevenue().replace(",", "").replace(" VNĐ", "");
            try {
                double revenue = Double.parseDouble(revenueStr);
                PieChart.Data slice = new PieChart.Data(data.getProductName(), revenue);
                pieChart.getData().add(slice);
            } catch (NumberFormatException e) {
                System.err.println("Error parsing revenue: " + data.getRevenue());
            }
        }

        productChartContainer.getChildren().add(pieChart);
    }

    /**
     * Create customer chart
     */
    private void createCustomerChart() {
        // Clear existing chart
        customerChartContainer.getChildren().clear();

        // Create bar chart for top customers
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Khách hàng");
        yAxis.setLabel("Tổng chi tiêu (VNĐ)");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Top khách hàng chi tiêu nhiều nhất");
        barChart.setPrefHeight(300);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Chi tiêu");

        // Take only top 10 customers for better visualization
        int count = 0;
        for (CustomerData data : customerList) {
            if (count >= 10) break;

            String totalStr = data.getTotalSpent().replace(",", "").replace(" VNĐ", "");
            try {
                double total = Double.parseDouble(totalStr);
                series.getData().add(new XYChart.Data<>(data.getCustomerName(), total));
                count++;
            } catch (NumberFormatException e) {
                System.err.println("Error parsing total: " + data.getTotalSpent());
            }
        }

        barChart.getData().add(series);
        customerChartContainer.getChildren().add(barChart);
    }

    /**
     * Export to Excel
     */
    @FXML
    private void exportToExcel() {
        try {
            String reportType = reportTypeCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            ExcelExporter exporter = new ExcelExporter();

            switch (reportType) {
                case "Báo cáo doanh thu":
                    exporter.exportSalesReport(salesList, startDate, endDate);
                    break;
                case "Báo cáo sản phẩm":
                    exporter.exportProductReport(productList, startDate, endDate);
                    break;
                case "Báo cáo khách hàng":
                    exporter.exportCustomerReport(customerList, startDate, endDate);
                    break;
                case "Báo cáo tổng hợp":
                    exporter.exportAllReports(salesList, productList, customerList, startDate, endDate);
                    break;
            }

            showSuccess("Xuất Excel thành công!");

        } catch (Exception e) {
            System.err.println("Error exporting to Excel: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi xuất Excel: " + e.getMessage());
        }
    }

    /**
     * Export to PDF
     */
    @FXML
    private void exportToPDF() {
        try {
            String reportType = reportTypeCombo.getValue();
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();

            PDFExporter exporter = new PDFExporter();

            switch (reportType) {
                case "Báo cáo doanh thu":
                    exporter.exportSalesReport(salesList, startDate, endDate);
                    break;
                case "Báo cáo sản phẩm":
                    exporter.exportProductReport(productList, startDate, endDate);
                    break;
                case "Báo cáo khách hàng":
                    exporter.exportCustomerReport(customerList, startDate, endDate);
                    break;
                case "Báo cáo tổng hợp":
                    exporter.exportAllReports(salesList, productList, customerList, startDate, endDate);
                    break;
            }

            showSuccess("Xuất PDF thành công!");

        } catch (Exception e) {
            System.err.println("Error exporting to PDF: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi xuất PDF: " + e.getMessage());
        }
    }

    /**
     * Format currency
     */
    private String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show success message
     */
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // =====================================================
    // DATA CLASSES
    // =====================================================

    /**
     * Sales data class
     */
    public static class SalesData {
        private String date, revenue, orderCount, avgOrder, growth;

        public SalesData(String date, String revenue, String orderCount, String avgOrder, String growth) {
            this.date = date;
            this.revenue = revenue;
            this.orderCount = orderCount;
            this.avgOrder = avgOrder;
            this.growth = growth;
        }

        // Getters
        public String getDate() { return date; }
        public String getRevenue() { return revenue; }
        public String getOrderCount() { return orderCount; }
        public String getAvgOrder() { return avgOrder; }
        public String getGrowth() { return growth; }
    }

    /**
     * Product data class
     */
    public static class ProductData {
        private String productName, quantity, revenue, percentage;

        public ProductData(String productName, String quantity, String revenue, String percentage) {
            this.productName = productName;
            this.quantity = quantity;
            this.revenue = revenue;
            this.percentage = percentage;
        }

        // Getters
        public String getProductName() { return productName; }
        public String getQuantity() { return quantity; }
        public String getRevenue() { return revenue; }
        public String getPercentage() { return percentage; }
    }

    /**
     * Customer data class
     */
    public static class CustomerData {
        private String customerName, orderCount, totalSpent, lastOrder;

        public CustomerData(String customerName, String orderCount, String totalSpent, String lastOrder) {
            this.customerName = customerName;
            this.orderCount = orderCount;
            this.totalSpent = totalSpent;
            this.lastOrder = lastOrder;
        }

        // Getters
        public String getCustomerName() { return customerName; }
        public String getOrderCount() { return orderCount; }
        public String getTotalSpent() { return totalSpent; }
        public String getLastOrder() { return lastOrder; }
    }
}
