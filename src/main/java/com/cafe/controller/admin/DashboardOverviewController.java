package com.cafe.controller.admin;


import com.cafe.service.TableService;
import com.cafe.service.CustomerService;
import com.cafe.service.ReportService;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.entity.Customer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import java.net.URL;

import java.text.DecimalFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * Dashboard Overview Controller - Complete Statistics Dashboard
 * Hiển thị thống kê tổng quan của quán café
 * 
 * Features:
 * - Thống kê doanh thu, đơn hàng, bàn, khách hàng
 * - Biểu đồ doanh thu 7 ngày gần nhất
 * - Biểu đồ phân phối doanh thu theo danh mục
 * - Cập nhật tự động và làm mới dữ liệu
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class DashboardOverviewController implements Initializable {
    
    // =====================================================
    // FXML INJECTIONS
    // =====================================================
    
    @FXML private Label dateTimeLabel;
    @FXML private Label revenueLabel;
    @FXML private Label tablesLabel;
    @FXML private Label ordersLabel;
    @FXML private Label customersLabel;
    @FXML private Button refreshButton;
    
    @FXML private BarChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    
    @FXML private PieChart categoryChart;
    
    // =====================================================
    // SERVICES
    // =====================================================
    
    private final ReportService reportService;
    private final TableService tableService;
    private final CustomerService customerService;
    private final DecimalFormat numberFormatter;
    
    // =====================================================
    // CONSTRUCTOR & INITIALIZATION
    // =====================================================
    
    public DashboardOverviewController() {
        this.reportService = new ReportService();
        this.tableService = new TableService();
        this.customerService = new CustomerService();
        this.numberFormatter = new DecimalFormat("#,##0");
        
        System.out.println("✅ DashboardOverviewController initialized");
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🚀 Initializing Dashboard Overview...");
            
            // Setup UI components
            setupDateTimeDisplay();
            setupCharts();
            
            // Load initial data
            loadDashboardData();
            
            System.out.println("✅ Dashboard Overview initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing Dashboard Overview: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // =====================================================
    // UI SETUP METHODS
    // =====================================================
    
    /**
     * Thiết lập hiển thị ngày giờ
     */
    private void setupDateTimeDisplay() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy", Locale.of("vi", "VN"));
        String formattedDateTime = now.format(formatter);
        dateTimeLabel.setText(formattedDateTime);
    }
    
    /**
     * Thiết lập cấu hình cho các biểu đồ
     */
    private void setupCharts() {
        // Setup Bar Chart
        revenueChart.setTitle("Doanh thu 7 ngày gần nhất");
        revenueChart.setAnimated(true);
        revenueChart.setLegendVisible(false);
        
        xAxis.setLabel("Ngày");
        yAxis.setLabel("Doanh thu (VNĐ)");
        yAxis.setAutoRanging(true);
        
        // Setup Pie Chart
        categoryChart.setTitle("Phân phối doanh thu theo danh mục");
        categoryChart.setAnimated(true);
        categoryChart.setLabelsVisible(true);
        categoryChart.setStartAngle(90);
    }
    
    // =====================================================
    // DATA LOADING METHODS
    // =====================================================
    
    /**
     * Tải tất cả dữ liệu dashboard
     */
    private void loadDashboardData() {
        System.out.println("📊 Loading dashboard data...");
        
        // Create background task to load data
        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load statistics
                    loadStatistics();
                    
                    // Load charts data
                    loadRevenueChart();
                    loadCategoryChart();
                    
                    Platform.runLater(() -> {
                        System.out.println("✅ Dashboard data loaded successfully");
                    });
                    
                } catch (Exception e) {
                    System.err.println("❌ Error loading dashboard data: " + e.getMessage());
                    e.printStackTrace();
                    
                    Platform.runLater(() -> {
                        showErrorState();
                    });
                }
                
                return null;
            }
        };
        
        // Run task in background thread
        Thread loadThread = new Thread(loadDataTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }
    
    /**
     * Tải thống kê cơ bản
     */
    private void loadStatistics() {
        try {
            System.out.println("📈 Loading basic statistics...");
            
            // Doanh thu hôm nay
            double todayRevenue = reportService.getTodayRevenue();
            Platform.runLater(() -> {
                revenueLabel.setText(formatCurrency(todayRevenue));
            });
            
            // Tổng số bàn
            List<TableCafe> tables = tableService.getAllTables();
            int totalTables = (int) tables.stream()
                    .filter(TableCafe::isActive)
                    .count();
            Platform.runLater(() -> {
                tablesLabel.setText(String.valueOf(totalTables));
            });
            
            // Đơn hàng hôm nay
            LocalDate today = LocalDate.now();
            long todayOrders = reportService.getOrderCountByDateRange(today, today);
            Platform.runLater(() -> {
                ordersLabel.setText(String.valueOf(todayOrders));
            });
            
            // Tổng khách hàng
            List<Customer> customers = customerService.getAllCustomers();
            int totalCustomers = customers.size();
            Platform.runLater(() -> {
                customersLabel.setText(String.valueOf(totalCustomers));
            });
            
            System.out.println("✅ Statistics loaded - Revenue: " + todayRevenue + ", Tables: " + totalTables + 
                             ", Orders: " + todayOrders + ", Customers: " + totalCustomers);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tải dữ liệu biểu đồ doanh thu 7 ngày
     */
    private void loadRevenueChart() {
        try {
            System.out.println("📊 Loading revenue chart data...");
            
            ObservableList<XYChart.Data<String, Number>> revenueData = FXCollections.observableArrayList();
            
            // Tạo dữ liệu cho 7 ngày gần nhất
            for (int i = 6; i >= 0; i--) {
                LocalDate date = LocalDate.now().minusDays(i);
                double revenue = reportService.getRevenueByDateRange(date, date);
                
                // Format date for display
                String dayLabel = date.format(DateTimeFormatter.ofPattern("dd/MM"));
                revenueData.add(new XYChart.Data<>(dayLabel, revenue));
            }
            
            Platform.runLater(() -> {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Doanh thu");
                series.setData(revenueData);
                
                revenueChart.getData().clear();
                revenueChart.getData().add(series);
            });
            
            System.out.println("✅ Revenue chart loaded with " + revenueData.size() + " data points");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading revenue chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tải dữ liệu biểu đồ phân phối theo danh mục
     */
    private void loadCategoryChart() {
        try {
            System.out.println("🥧 Loading category chart data...");
            
            // Tạo dữ liệu mẫu cho danh mục (vì chưa có bảng categories trong OrderService)
            ObservableList<PieChart.Data> categoryData = FXCollections.observableArrayList();
            
            // Mock data - trong thực tế sẽ query từ database
            Map<String, Double> mockCategoryRevenue = createMockCategoryData();
            
            for (Map.Entry<String, Double> entry : mockCategoryRevenue.entrySet()) {
                if (entry.getValue() > 0) {
                    categoryData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
                }
            }
            
            Platform.runLater(() -> {
                categoryChart.setData(categoryData);
                
                // Add percentage labels
                categoryData.forEach(data -> {
                    double percentage = (data.getPieValue() / mockCategoryRevenue.values().stream()
                            .mapToDouble(Double::doubleValue).sum()) * 100;
                    data.setName(data.getName() + " (" + String.format("%.1f%%", percentage) + ")");
                });
            });
            
            System.out.println("✅ Category chart loaded with " + categoryData.size() + " categories");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading category chart: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo dữ liệu mẫu cho danh mục sản phẩm
     * TODO: Thay thế bằng query thực từ database khi có bảng categories
     */
    private Map<String, Double> createMockCategoryData() {
        Map<String, Double> categoryRevenue = new HashMap<>();
        
        try {
            double todayRevenue = reportService.getTodayRevenue();
            
            if (todayRevenue > 0) {
                // Phân phối doanh thu theo các danh mục chính
                categoryRevenue.put("Cà phê", todayRevenue * 0.45);
                categoryRevenue.put("Trà", todayRevenue * 0.25);
                categoryRevenue.put("Bánh ngọt", todayRevenue * 0.15);
                categoryRevenue.put("Đồ ăn nhẹ", todayRevenue * 0.10);
                categoryRevenue.put("Khác", todayRevenue * 0.05);
            } else {
                // Default mock data khi không có doanh thu
                categoryRevenue.put("Cà phê", 4500000.0);
                categoryRevenue.put("Trà", 2500000.0);
                categoryRevenue.put("Bánh ngọt", 1500000.0);
                categoryRevenue.put("Đồ ăn nhẹ", 1000000.0);
                categoryRevenue.put("Khác", 500000.0);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error creating mock category data: " + e.getMessage());
            // Fallback data
            categoryRevenue.put("Cà phê", 3000000.0);
            categoryRevenue.put("Trà", 2000000.0);
            categoryRevenue.put("Bánh ngọt", 1000000.0);
        }
        
        return categoryRevenue;
    }
    
    // =====================================================
    // EVENT HANDLERS
    // =====================================================
    
    /**
     * Xử lý sự kiện click nút làm mới
     */
    @FXML
    private void handleRefresh() {
        System.out.println("🔄 Refreshing dashboard data...");
        
        // Update datetime
        setupDateTimeDisplay();
        
        // Show loading state
        showLoadingState();
        
        // Reload all data
        loadDashboardData();
    }
    
    // =====================================================
    // UI STATE METHODS
    // =====================================================
    
    /**
     * Hiển thị trạng thái loading
     */
    private void showLoadingState() {
        revenueLabel.setText("Đang tải...");
        tablesLabel.setText("--");
        ordersLabel.setText("--");
        customersLabel.setText("--");
    }
    
    /**
     * Hiển thị trạng thái lỗi
     */
    private void showErrorState() {
        revenueLabel.setText("Lỗi tải");
        tablesLabel.setText("--");
        ordersLabel.setText("--");
        customersLabel.setText("--");
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    /**
     * Format currency với đơn vị VNĐ
     */
    private String formatCurrency(double amount) {
        if (amount == 0) {
            return "0 VNĐ";
        }
        return numberFormatter.format(amount) + " VNĐ";
    }
    

    
    /**
     * Refresh dashboard sau một khoảng thời gian
     */
    public void scheduleRefresh(int delaySeconds) {
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> handleRefresh());
            }
        }, delaySeconds * 1000L);
    }
    
    // =====================================================
    // PUBLIC API METHODS
    // =====================================================
    
    /**
     * Làm mới dữ liệu từ bên ngoài
     */
    public void refreshData() {
        handleRefresh();
    }
    
    /**
     * Lấy doanh thu hiện tại
     */
    public double getCurrentRevenue() {
        try {
            return reportService.getTodayRevenue();
        } catch (Exception e) {
            System.err.println("❌ Error getting current revenue: " + e.getMessage());
            return 0.0;
        }
    }
    
    /**
     * Lấy số đơn hàng hiện tại
     */
    public long getCurrentOrders() {
        try {
            LocalDate today = LocalDate.now();
            return reportService.getOrderCountByDateRange(today, today);
        } catch (Exception e) {
            System.err.println("❌ Error getting current orders: " + e.getMessage());
            return 0;
        }
    }
}
