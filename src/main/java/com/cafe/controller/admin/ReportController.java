package com.cafe.controller.admin;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình báo cáo và thống kê
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class ReportController implements Initializable {
    
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ComboBox<String> reportTypeCombo;
    
    @FXML private TableView<Object> salesTable;
    @FXML private TableColumn<Object, String> dateColumn;
    @FXML private TableColumn<Object, String> revenueColumn;
    @FXML private TableColumn<Object, String> orderCountColumn;
    @FXML private TableColumn<Object, String> avgOrderColumn;
    @FXML private TableColumn<Object, String> growthColumn;
    
    @FXML private TableView<Object> productTable;
    @FXML private TableColumn<Object, String> productNameColumn;
    @FXML private TableColumn<Object, String> quantityColumn;
    @FXML private TableColumn<Object, String> productRevenueColumn;
    @FXML private TableColumn<Object, String> percentageColumn;
    
    @FXML private TableView<Object> customerTable;
    @FXML private TableColumn<Object, String> customerNameColumn;
    @FXML private TableColumn<Object, String> customerOrderCountColumn;
    @FXML private TableColumn<Object, String> customerTotalColumn;
    @FXML private TableColumn<Object, String> lastOrderColumn;
    
    private ObservableList<Object> salesList = FXCollections.observableArrayList();
    private ObservableList<Object> productList = FXCollections.observableArrayList();
    private ObservableList<Object> customerList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupReportTypeCombo();
        setupTables();
        loadSampleData();
        setupEventHandlers();
        
        System.out.println("✅ ReportController initialized successfully");
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
     * Load sample data
     */
    private void loadSampleData() {
        // Load sample sales data
        salesList.add(new SalesData("01/12/2024", "2,500,000", "45", "55,556", "+12.5%"));
        salesList.add(new SalesData("02/12/2024", "2,800,000", "52", "53,846", "+8.3%"));
        salesList.add(new SalesData("03/12/2024", "3,200,000", "58", "55,172", "+14.3%"));
        salesList.add(new SalesData("04/12/2024", "2,900,000", "51", "56,863", "-9.4%"));
        salesList.add(new SalesData("05/12/2024", "3,500,000", "62", "56,452", "+20.7%"));
        
        // Load sample product data
        productList.add(new ProductData("Cà phê đen", "156", "1,560,000", "15.2%"));
        productList.add(new ProductData("Cà phê sữa", "142", "1,420,000", "13.8%"));
        productList.add(new ProductData("Trà sữa", "98", "1,960,000", "19.1%"));
        productList.add(new ProductData("Bánh tiramisu", "45", "675,000", "6.6%"));
        productList.add(new ProductData("Bánh cheesecake", "38", "570,000", "5.5%"));
        
        // Load sample customer data
        customerList.add(new CustomerData("Nguyễn Văn A", "25", "2,500,000", "05/12/2024"));
        customerList.add(new CustomerData("Trần Thị B", "18", "1,800,000", "04/12/2024"));
        customerList.add(new CustomerData("Lê Văn C", "32", "3,200,000", "03/12/2024"));
        customerList.add(new CustomerData("Phạm Thị D", "15", "1,500,000", "02/12/2024"));
        customerList.add(new CustomerData("Hoàng Văn E", "28", "2,800,000", "01/12/2024"));
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        reportTypeCombo.setOnAction(e -> {
            generateReport();
        });
    }
    
    /**
     * Generate report based on selected type and date range
     */
    private void generateReport() {
        String reportType = reportTypeCombo.getValue();
        System.out.println("Generating report: " + reportType);
        
        // TODO: Implement actual report generation logic
        // This would typically involve:
        // 1. Getting data from database based on date range
        // 2. Processing and aggregating data
        // 3. Updating table views
        // 4. Generating charts/graphs
    }
    
    /**
     * Sample data classes
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
