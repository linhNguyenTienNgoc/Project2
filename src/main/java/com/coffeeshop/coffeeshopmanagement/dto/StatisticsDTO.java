package com.coffeeshop.coffeeshopmanagement.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.time.LocalDateTime;

public class StatisticsDTO {
    private long totalInvoices;
    private BigDecimal totalRevenue;
    private long totalCustomers;
    private long activeTables;
    private long totalEmployees;
    private long totalMenuItems;
    
    // Daily statistics
    private BigDecimal todayRevenue;
    private long todayInvoices;
    
    // Monthly statistics
    private BigDecimal monthlyRevenue;
    private long monthlyInvoices;
    
    // Top selling items
    private List<Map<String, Object>> topSellingItems;
    
    // Revenue by category
    private List<Map<String, Object>> revenueByCategory;
    
    // Revenue trend (last 7 days)
    private List<Map<String, Object>> revenueTrend;
    
    // Constructor with default values
    public StatisticsDTO() {
        this.totalRevenue = BigDecimal.ZERO;
        this.todayRevenue = BigDecimal.ZERO;
        this.monthlyRevenue = BigDecimal.ZERO;
    }
    
    // Enhanced method with pattern matching (Java 17 compatible)
    public String getRevenueStatus() {
        int comparison = this.totalRevenue.compareTo(BigDecimal.valueOf(1000000));
        if (comparison == 1) {
            return "High Revenue";
        } else if (comparison == 0) {
            return "Medium Revenue";
        } else if (comparison == -1) {
            return "Low Revenue";
        } else {
            return "No Revenue";
        }
    }
    
    // Method with enhanced switch for table status (Java 17 compatible)
    public String getTableUtilizationStatus() {
        if (totalTables() == 0) return "No Tables";
        
        double utilizationRate = (double) activeTables / totalTables();
        int utilizationPercent = (int) (utilizationRate * 100);
        
        if (utilizationPercent >= 80 && utilizationPercent <= 100) {
            return "Full Capacity";
        } else if (utilizationPercent >= 60 && utilizationPercent <= 79) {
            return "High Utilization";
        } else if (utilizationPercent >= 40 && utilizationPercent <= 59) {
            return "Medium Utilization";
        } else if (utilizationPercent >= 20 && utilizationPercent <= 39) {
            return "Low Utilization";
        } else {
            return "Very Low Utilization";
        }
    }
    
    private long totalTables() {
        return activeTables + (totalInvoices > 0 ? 1 : 0); // Simple calculation
    }
    
    // Getters and Setters
    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }
    
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    
    public long getTotalCustomers() { return totalCustomers; }
    public void setTotalCustomers(long totalCustomers) { this.totalCustomers = totalCustomers; }
    
    public long getActiveTables() { return activeTables; }
    public void setActiveTables(long activeTables) { this.activeTables = activeTables; }
    
    public long getTotalEmployees() { return totalEmployees; }
    public void setTotalEmployees(long totalEmployees) { this.totalEmployees = totalEmployees; }
    
    public long getTotalMenuItems() { return totalMenuItems; }
    public void setTotalMenuItems(long totalMenuItems) { this.totalMenuItems = totalMenuItems; }
    
    public BigDecimal getTodayRevenue() { return todayRevenue; }
    public void setTodayRevenue(BigDecimal todayRevenue) { this.todayRevenue = todayRevenue; }
    
    public long getTodayInvoices() { return todayInvoices; }
    public void setTodayInvoices(long todayInvoices) { this.todayInvoices = todayInvoices; }
    
    public BigDecimal getMonthlyRevenue() { return monthlyRevenue; }
    public void setMonthlyRevenue(BigDecimal monthlyRevenue) { this.monthlyRevenue = monthlyRevenue; }
    
    public long getMonthlyInvoices() { return monthlyInvoices; }
    public void setMonthlyInvoices(long monthlyInvoices) { this.monthlyInvoices = monthlyInvoices; }
    
    public List<Map<String, Object>> getTopSellingItems() { return topSellingItems; }
    public void setTopSellingItems(List<Map<String, Object>> topSellingItems) { this.topSellingItems = topSellingItems; }
    
    public List<Map<String, Object>> getRevenueByCategory() { return revenueByCategory; }
    public void setRevenueByCategory(List<Map<String, Object>> revenueByCategory) { this.revenueByCategory = revenueByCategory; }
    
    public List<Map<String, Object>> getRevenueTrend() { return revenueTrend; }
    public void setRevenueTrend(List<Map<String, Object>> revenueTrend) { this.revenueTrend = revenueTrend; }
} 