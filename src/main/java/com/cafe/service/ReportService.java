package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.Order;

import com.cafe.model.entity.User;

import java.sql.Connection;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Report Service - Complete Reporting System
 * Xử lý tất cả các nghiệp vụ liên quan đến báo cáo và thống kê
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class ReportService {
    
    public ReportService() {
        // Connections will be managed per operation using try-with-resources
    }

    /**
     * Báo cáo doanh thu theo khoảng thời gian
     */
    public double getRevenueByDateRange(LocalDate fromDate, LocalDate toDate) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getOrderDate() != null)
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();
                        return !orderDate.isBefore(fromDate) && !orderDate.isAfter(toDate);
                    })
                    .filter(order -> "paid".equals(order.getOrderStatus()))
                    .mapToDouble(Order::getTotalAmount)
                    .sum();
        } catch (Exception e) {
            System.err.println("Error calculating revenue: " + e.getMessage());
            return 0.0;
        }
    }

    /**
     * Số lượng đơn hàng theo khoảng thời gian
     */
    public long getOrderCountByDateRange(LocalDate fromDate, LocalDate toDate) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getOrderDate() != null)
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();
                        return !orderDate.isBefore(fromDate) && !orderDate.isAfter(toDate);
                    })
                    .count();
        } catch (Exception e) {
            System.err.println("Error counting orders: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Báo cáo doanh thu hôm nay
     */
    public double getTodayRevenue() {
        LocalDate today = LocalDate.now();
        return getRevenueByDateRange(today, today);
    }

    /**
     * Báo cáo doanh thu tháng này
     */
    public double getThisMonthRevenue() {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        LocalDate lastDay = LocalDate.now();
        return getRevenueByDateRange(firstDay, lastDay);
    }

    /**
     * Top sản phẩm bán chạy
     */
    public List<ProductSalesReport> getTopSellingProducts(int limit) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            // Mock data for now - would need order_details table for real implementation
            return productDAO.getAllProducts().stream()
                    .limit(limit)
                    .map(product -> new ProductSalesReport(
                        product.getProductName(),
                        (int)(Math.random() * 100) + 10, // Mock quantity sold
                        product.getPrice() * ((int)(Math.random() * 100) + 10), // Mock revenue
                        Math.random() * 100 // Mock percentage
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting top selling products: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Báo cáo hiệu suất nhân viên
     */
    public List<StaffPerformanceReport> getStaffPerformance() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(conn);
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            
            List<User> staff = userDAO.getAllUsers();
            List<Order> orders = orderDAO.getAllOrders();
            
            return staff.stream()
                    .filter(user -> !"Admin".equals(user.getRole()))
                    .map(user -> {
                        long userOrders = orders.stream()
                                .filter(order -> order.getUserId() == user.getUserId())
                                .count();
                        double userRevenue = orders.stream()
                                .filter(order -> order.getUserId() == user.getUserId())
                                .filter(order -> "paid".equals(order.getOrderStatus()))
                                .mapToDouble(Order::getTotalAmount)
                                .sum();
                        
                        return new StaffPerformanceReport(
                            user.getFullName(),
                            user.getRole(),
                            (int)userOrders,
                            userRevenue,
                            userOrders > 0 ? userRevenue / userOrders : 0
                        );
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error getting staff performance: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Thống kê theo giờ trong ngày
     */
    public Map<Integer, Double> getHourlyRevenue(LocalDate date) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getOrderDate() != null)
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();
                        return orderDate.equals(date);
                    })
                    .filter(order -> "paid".equals(order.getOrderStatus()))
                    .collect(Collectors.groupingBy(
                        order -> order.getOrderDate().toLocalDateTime().getHour(),
                        Collectors.summingDouble(Order::getTotalAmount)
                    ));
        } catch (Exception e) {
            System.err.println("Error getting hourly revenue: " + e.getMessage());
            return Map.of();
        }
    }

    /**
     * Get today's orders count
     */
    public int getTodayOrdersCount() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            LocalDate today = LocalDate.now();
            return (int) orderDAO.getAllOrders().stream()
                    .filter(order -> order.getOrderDate() != null)
                    .filter(order -> {
                        LocalDate orderDate = order.getOrderDate().toLocalDateTime().toLocalDate();
                        return orderDate.equals(today);
                    })
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting today's orders count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get pending orders count
     */
    public int getPendingOrdersCount() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return (int) orderDAO.getAllOrders().stream()
                    .filter(order -> "pending".equals(order.getOrderStatus()) || 
                                   "preparing".equals(order.getOrderStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting pending orders count: " + e.getMessage());
            return 0;
        }
    }

    // Data Transfer Objects for reports
    public static class ProductSalesReport {
        private String productName;
        private int quantitySold;
        private double revenue;
        private double percentage;

        public ProductSalesReport(String productName, int quantitySold, double revenue, double percentage) {
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.revenue = revenue;
            this.percentage = percentage;
        }

        // Getters
        public String getProductName() { return productName; }
        public int getQuantitySold() { return quantitySold; }
        public double getRevenue() { return revenue; }
        public double getPercentage() { return percentage; }
    }

    public static class StaffPerformanceReport {
        private String staffName;
        private String role;
        private int totalOrders;
        private double totalRevenue;
        private double avgOrderValue;

        public StaffPerformanceReport(String staffName, String role, int totalOrders, double totalRevenue, double avgOrderValue) {
            this.staffName = staffName;
            this.role = role;
            this.totalOrders = totalOrders;
            this.totalRevenue = totalRevenue;
            this.avgOrderValue = avgOrderValue;
        }

        // Getters
        public String getStaffName() { return staffName; }
        public String getRole() { return role; }
        public int getTotalOrders() { return totalOrders; }
        public double getTotalRevenue() { return totalRevenue; }
        public double getAvgOrderValue() { return avgOrderValue; }
    }
    
    /**
     * Get sales data by date range
     * @param startDate Start date
     * @param endDate End date
     * @return List of sales data
     */
    public java.util.List<com.cafe.model.dto.SalesData> getSalesDataByDateRange(java.time.LocalDate startDate, java.time.LocalDate endDate) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = """
                SELECT 
                    DATE(o.order_date) as order_date,
                    COUNT(o.order_id) as total_orders,
                    COALESCE(SUM(o.total_amount), 0) as total_revenue,
                    COALESCE(SUM(od.quantity), 0) as total_items_sold
                FROM orders o
                LEFT JOIN order_details od ON o.order_id = od.order_id
                WHERE o.payment_status = 'paid'
                AND DATE(o.order_date) BETWEEN ? AND ?
                GROUP BY DATE(o.order_date)
                ORDER BY order_date DESC
                """;
            
            java.util.List<com.cafe.model.dto.SalesData> salesDataList = new java.util.ArrayList<>();
            
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setDate(1, java.sql.Date.valueOf(startDate));
                stmt.setDate(2, java.sql.Date.valueOf(endDate));
                
                try (var rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        java.time.LocalDate date = rs.getDate("order_date").toLocalDate();
                        int totalOrders = rs.getInt("total_orders");
                        double totalRevenue = rs.getDouble("total_revenue");
                        int totalItemsSold = rs.getInt("total_items_sold");
                        
                        salesDataList.add(new com.cafe.model.dto.SalesData(date, totalOrders, totalRevenue, totalItemsSold));
                    }
                }
            }
            
            System.out.println("📊 Retrieved " + salesDataList.size() + " sales data records for date range: " + startDate + " to " + endDate);
            return salesDataList;
            
        } catch (Exception e) {
            System.err.println("❌ Error getting sales data by date range: " + e.getMessage());
            e.printStackTrace();
            return java.util.List.of();
        }
    }
}


