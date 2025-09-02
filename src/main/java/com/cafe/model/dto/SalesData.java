package com.cafe.model.dto;

import java.time.LocalDate;

/**
 * Sales Data DTO for reporting
 * Contains sales information for a specific date
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class SalesData {
    
    private LocalDate date;
    private int totalOrders;
    private double totalRevenue;
    private int totalItemsSold;
    private double averageOrderValue;
    
    public SalesData() {
    }
    
    public SalesData(LocalDate date, int totalOrders, double totalRevenue, int totalItemsSold) {
        this.date = date;
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.totalItemsSold = totalItemsSold;
        this.averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
    }
    
    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getTotalOrders() {
        return totalOrders;
    }
    
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
        this.averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
    }
    
    public double getTotalRevenue() {
        return totalRevenue;
    }
    
    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
        this.averageOrderValue = totalOrders > 0 ? totalRevenue / totalOrders : 0.0;
    }
    
    public int getTotalItemsSold() {
        return totalItemsSold;
    }
    
    public void setTotalItemsSold(int totalItemsSold) {
        this.totalItemsSold = totalItemsSold;
    }
    
    public double getAverageOrderValue() {
        return averageOrderValue;
    }
    
    public void setAverageOrderValue(double averageOrderValue) {
        this.averageOrderValue = averageOrderValue;
    }
    
    @Override
    public String toString() {
        return "SalesData{" +
                "date=" + date +
                ", totalOrders=" + totalOrders +
                ", totalRevenue=" + totalRevenue +
                ", totalItemsSold=" + totalItemsSold +
                ", averageOrderValue=" + averageOrderValue +
                '}';
    }
}
