package com.cafe.dao.base;

import com.cafe.model.entity.Customer;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Data Access Object interface for Customer operations
 */
public interface CustomerDAO {
    
    /**
     * Get all customers
     */
    List<Customer> getAllCustomers() throws SQLException;
    
    /**
     * Get customer by ID
     */
    Customer getCustomerById(int customerId) throws SQLException;
    
    /**
     * Find customer by phone
     */
    Customer findCustomerByPhone(String phone) throws SQLException;
    
    /**
     * Find customer by email
     */
    Customer findCustomerByEmail(String email) throws SQLException;
    
    /**
     * Add new customer
     */
    boolean addCustomer(Customer customer) throws SQLException;
    
    /**
     * Insert new customer (alias for addCustomer)
     */
    boolean insertCustomer(Customer customer) throws SQLException;
    
    /**
     * Update customer
     */
    boolean updateCustomer(Customer customer) throws SQLException;
    
    /**
     * Delete customer
     */
    boolean deleteCustomer(int customerId) throws SQLException;
    
    /**
     * Update customer loyalty points
     */
    boolean updateCustomerLoyaltyPoints(int customerId, int points) throws SQLException;
    
    /**
     * Get new customers this month
     */
    int getNewCustomersThisMonth() throws SQLException;
    
    /**
     * Get top customers by spending within date range
     */
    List<CustomerSpendingData> getTopCustomers(LocalDate startDate, LocalDate endDate, int limit) throws SQLException;
    
    /**
     * Get customer statistics
     */
    CustomerStatistics getCustomerStatistics() throws SQLException;
    
    /**
     * Inner class for customer spending data
     */
    class CustomerSpendingData {
        private int customerId;
        private String fullName;
        private String phone;
        private String email;
        private int orderCount;
        private double totalSpent;
        private double avgOrderValue;
        private LocalDate lastOrderDate;

        // Getters and setters
        public int getCustomerId() { return customerId; }
        public void setCustomerId(int customerId) { this.customerId = customerId; }

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }

        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public int getOrderCount() { return orderCount; }
        public void setOrderCount(int orderCount) { this.orderCount = orderCount; }

        public double getTotalSpent() { return totalSpent; }
        public void setTotalSpent(double totalSpent) { this.totalSpent = totalSpent; }

        public double getAvgOrderValue() { return avgOrderValue; }
        public void setAvgOrderValue(double avgOrderValue) { this.avgOrderValue = avgOrderValue; }

        public LocalDate getLastOrderDate() { return lastOrderDate; }
        public void setLastOrderDate(LocalDate lastOrderDate) { this.lastOrderDate = lastOrderDate; }
    }

    /**
     * Inner class for customer statistics
     */
    class CustomerStatistics {
        private int totalCustomers;
        private int newCustomersThisMonth;
        private int activeCustomers;

        // Getters and setters
        public int getTotalCustomers() { return totalCustomers; }
        public void setTotalCustomers(int totalCustomers) { this.totalCustomers = totalCustomers; }

        public int getNewCustomersThisMonth() { return newCustomersThisMonth; }
        public void setNewCustomersThisMonth(int newCustomersThisMonth) { this.newCustomersThisMonth = newCustomersThisMonth; }

        public int getActiveCustomers() { return activeCustomers; }
        public void setActiveCustomers(int activeCustomers) { this.activeCustomers = activeCustomers; }
    }
}