package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.util.PriceFormatter;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.*;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho Admin Dashboard Overview với real-time statistics
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class AdminOverviewController implements Initializable, DashboardCommunicator {

    // Welcome Message
    @FXML private Label welcomeMessage;
    
    // Statistics Labels
    @FXML private Label todayRevenueLabel;
    @FXML private Label todayOrdersLabel;
    @FXML private Label activeTablesLabel;
    @FXML private Label totalProductsLabel;
    
    // Quick Action Buttons
    @FXML private Button addUserButton;
    @FXML private Button addProductButton;
    @FXML private Button viewReportsButton;
    @FXML private Button managePromotionsButton;
    
    // Charts
    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis revenueXAxis;
    @FXML private NumberAxis revenueYAxis;
    @FXML private PieChart popularProductsChart;
    
    // Activities
    @FXML private ListView<String> activitiesListView;
    @FXML private Button refreshActivitiesButton;
    
    // Note: Using direct database connections per operation to prevent connection leaks
    // No persistent DAO instances to avoid holding connections
    
    // Dashboard communication
    private Object dashboardController;
    
    // Data lists
    private ObservableList<String> activitiesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize DAOs
            initializeDAOs();
            
            // Setup UI components
            setupWelcomeMessage();
            setupQuickActions();
            setupActivitiesList();
            setupCharts();
            
            // Load initial data
            loadDashboardData();
            
            // Setup auto-refresh
            setupAutoRefresh();
            
            System.out.println("✅ AdminOverviewController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing AdminOverviewController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize database access objects
     * Note: Using connection per operation to avoid connection leaks
     */
    private void initializeDAOs() throws Exception {
        // We'll create connections per operation instead of keeping them open
        // This prevents connection leaks and ensures proper resource management
        System.out.println("✅ DAO initialization configured for per-operation connections");
    }
    
    /**
     * Setup welcome message with current date
     */
    private void setupWelcomeMessage() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
        welcomeMessage.setText("Hôm nay là " + today.format(formatter) + " - Chúc bạn có một ngày làm việc hiệu quả!");
    }
    
    /**
     * Setup quick action buttons
     */
    private void setupQuickActions() {
        addUserButton.setOnAction(e -> handleQuickAction("user"));
        addProductButton.setOnAction(e -> handleQuickAction("menu"));
        viewReportsButton.setOnAction(e -> handleQuickAction("report"));
        managePromotionsButton.setOnAction(e -> handleQuickAction("promotion"));
    }
    
    /**
     * Handle quick action button clicks
     */
    private void handleQuickAction(String action) {
        // Notify dashboard to switch to the appropriate tab
        if (dashboardController != null) {
            try {
                // Use reflection to call switchToTab method
                dashboardController.getClass().getMethod("switchToTab", String.class)
                    .invoke(dashboardController, action);
                System.out.println("🚀 Quick action: " + action);
            } catch (Exception e) {
                System.err.println("Error switching to tab: " + e.getMessage());
            }
        }
    }
    
    /**
     * Setup activities list
     */
    private void setupActivitiesList() {
        activitiesListView.setItems(activitiesList);
        refreshActivitiesButton.setOnAction(e -> loadRecentActivities());
    }
    
    /**
     * Setup charts
     */
    private void setupCharts() {
        // Revenue chart setup
        revenueChart.setTitle("Doanh thu 7 ngày qua");
        revenueChart.setAnimated(true);
        revenueChart.setLegendVisible(false);
        
        // Popular products chart setup
        popularProductsChart.setTitle("Top 5 món ăn phổ biến");
        popularProductsChart.setLegendVisible(true);
        popularProductsChart.setAnimated(true);
    }
    
    /**
     * Load all dashboard data
     */
    private void loadDashboardData() {
        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    loadTodayStatistics();
                    loadRevenueChart();
                    loadPopularProductsChart();
                    loadRecentActivities();
                });
                return null;
            }
        };
        
        new Thread(loadDataTask).start();
    }
    
    /**
     * Load today's statistics
     */
    private void loadTodayStatistics() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Today's revenue
            String revenueSQL = "SELECT COALESCE(SUM(final_amount), 0) as revenue FROM orders WHERE DATE(order_date) = CURDATE() AND payment_status = 'paid'";
            try (PreparedStatement stmt = conn.prepareStatement(revenueSQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    double revenue = rs.getDouble("revenue");
                    todayRevenueLabel.setText(PriceFormatter.format(revenue));
                }
            }
            
            // Today's orders count
            String ordersSQL = "SELECT COUNT(*) as count FROM orders WHERE DATE(order_date) = CURDATE()";
            try (PreparedStatement stmt = conn.prepareStatement(ordersSQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    todayOrdersLabel.setText(String.valueOf(count));
                }
            }
            
            // Active tables
            String tablesSQL = "SELECT COUNT(*) as total, SUM(CASE WHEN status = 'occupied' THEN 1 ELSE 0 END) as active FROM tables";
            try (PreparedStatement stmt = conn.prepareStatement(tablesSQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int total = rs.getInt("total");
                    int active = rs.getInt("active");
                    activeTablesLabel.setText(active + "/" + total);
                }
            }
            
            // Total products
            String productsSQL = "SELECT COUNT(*) as count FROM products WHERE is_available = TRUE AND is_active = TRUE";
            try (PreparedStatement stmt = conn.prepareStatement(productsSQL);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt("count");
                    totalProductsLabel.setText(String.valueOf(count));
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error loading today statistics: " + e.getMessage());
        }
    }
    
    /**
     * Load revenue chart data for last 7 days
     */
    private void loadRevenueChart() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT DATE(order_date) as date, COALESCE(SUM(final_amount), 0) as revenue " +
                        "FROM orders WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND payment_status = 'paid' " +
                        "GROUP BY DATE(order_date) ORDER BY date";
            
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName("Doanh thu");
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String date = rs.getString("date");
                    double revenue = rs.getDouble("revenue");
                    
                    // Format date for display
                    LocalDate localDate = LocalDate.parse(date);
                    String displayDate = localDate.format(DateTimeFormatter.ofPattern("dd/MM"));
                    
                    series.getData().add(new XYChart.Data<>(displayDate, revenue));
                }
            }
            
            revenueChart.getData().clear();
            revenueChart.getData().add(series);
            
        } catch (Exception e) {
            System.err.println("Error loading revenue chart: " + e.getMessage());
        }
    }
    
    /**
     * Load popular products pie chart
     */
    private void loadPopularProductsChart() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT p.product_name, SUM(od.quantity) as total_quantity " +
                        "FROM order_details od " +
                        "JOIN products p ON od.product_id = p.product_id " +
                        "JOIN orders o ON od.order_id = o.order_id " +
                        "WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                        "GROUP BY p.product_id, p.product_name " +
                        "ORDER BY total_quantity DESC LIMIT 5";
            
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String productName = rs.getString("product_name");
                    int quantity = rs.getInt("total_quantity");
                    
                    pieChartData.add(new PieChart.Data(productName, quantity));
                }
            }
            
            popularProductsChart.setData(pieChartData);
            
        } catch (Exception e) {
            System.err.println("Error loading popular products chart: " + e.getMessage());
        }
    }
    
    /**
     * Load recent activities
     */
    private void loadRecentActivities() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT 'Đơn hàng' as type, CONCAT('Đơn hàng #', order_id, ' - ', final_amount, ' VND') as description, order_date as activity_time " +
                        "FROM orders WHERE order_date >= DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                        "UNION ALL " +
                        "SELECT 'Thanh toán' as type, CONCAT('Thanh toán đơn #', order_id, ' - ', payment_method) as description, updated_at as activity_time " +
                        "FROM orders WHERE payment_status = 'paid' AND updated_at >= DATE_SUB(NOW(), INTERVAL 1 DAY) " +
                        "ORDER BY activity_time DESC LIMIT 10";
            
            List<String> activities = new ArrayList<>();
            
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    String type = rs.getString("type");
                    String description = rs.getString("description");
                    String time = rs.getTimestamp("activity_time").toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("HH:mm"));
                    
                    activities.add(String.format("[%s] %s - %s", time, type, description));
                }
            }
            
            Platform.runLater(() -> {
                activitiesList.clear();
                activitiesList.addAll(activities);
            });
            
        } catch (Exception e) {
            System.err.println("Error loading recent activities: " + e.getMessage());
        }
    }
    
    /**
     * Setup auto-refresh every 30 seconds
     */
    private void setupAutoRefresh() {
        Task<Void> autoRefreshTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    Thread.sleep(30000); // 30 seconds
                    Platform.runLater(() -> {
                        loadTodayStatistics();
                        loadRecentActivities();
                    });
                }
                return null;
            }
        };
        
        Thread autoRefreshThread = new Thread(autoRefreshTask);
        autoRefreshThread.setDaemon(true);
        autoRefreshThread.start();
    }
    
    // =====================================================
    // DASHBOARD COMMUNICATION IMPLEMENTATION
    // =====================================================
    
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminOverviewController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}
