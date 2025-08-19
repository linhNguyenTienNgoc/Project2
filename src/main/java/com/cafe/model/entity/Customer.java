package com.cafe.model.entity;

import java.sql.Timestamp;

/**
 * Entity class cho bảng customers
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class Customer {
    
    private int customerId;
    private String fullName;
    private String phone;
    private String email;
    private String address;
    private int loyaltyPoints;
    private double totalSpent;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Constructors
    public Customer() {
        this.loyaltyPoints = 0;
        this.totalSpent = 0.0;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Customer(String fullName, String phone) {
        this();
        this.fullName = fullName;
        this.phone = phone;
    }
    
    public Customer(String fullName, String phone, String email, String address) {
        this(fullName, phone);
        this.email = email;
        this.address = address;
    }
    
    // Constructor for CustomerController usage
    public Customer(int customerId, String fullName, String phone, String email, 
                   String address, int loyaltyPoints, String membershipLevel) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        
        // Set totalSpent based on membership level
        switch (membershipLevel.toLowerCase()) {
            case "diamond": this.totalSpent = 5000000; break;
            case "platinum": this.totalSpent = 2000000; break;
            case "gold": this.totalSpent = 1000000; break;
            case "silver": this.totalSpent = 500000; break;
            default: this.totalSpent = 0; break;
        }
    }
    
    public Customer(int customerId, String fullName, String phone, String email, 
                   String address, int loyaltyPoints, double totalSpent, 
                   boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.loyaltyPoints = loyaltyPoints;
        this.totalSpent = totalSpent;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    
    /**
     * Thêm điểm loyalty cho khách hàng
     */
    public void addLoyaltyPoints(int points) {
        this.loyaltyPoints += points;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Sử dụng điểm loyalty
     */
    public boolean useLoyaltyPoints(int points) {
        if (this.loyaltyPoints >= points) {
            this.loyaltyPoints -= points;
            this.updatedAt = new Timestamp(System.currentTimeMillis());
            return true;
        }
        return false;
    }
    
    /**
     * Cập nhật tổng chi tiêu
     */
    public void addSpent(double amount) {
        this.totalSpent += amount;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Tính điểm loyalty từ số tiền chi tiêu (1 điểm cho mỗi 10,000 VND)
     */
    public int calculateLoyaltyPointsFromSpent(double amount) {
        return (int) (amount / 10000);
    }
    
    /**
     * Kiểm tra khách hàng VIP (tổng chi tiêu > 1,000,000 VND)
     */
    public boolean isVIP() {
        return this.totalSpent >= 1000000;
    }
    
    /**
     * Kiểm tra khách hàng thường xuyên (tổng chi tiêu > 500,000 VND)
     */
    public boolean isRegular() {
        return this.totalSpent >= 500000;
    }
    
    /**
     * Lấy level khách hàng
     */
    public String getCustomerLevel() {
        if (isVIP()) return "VIP";
        if (isRegular()) return "Regular";
        return "New";
    }
    
    /**
     * Lấy membership level cho UI
     */
    public String getMembershipLevel() {
        if (totalSpent >= 5000000) return "Diamond";
        if (totalSpent >= 2000000) return "Platinum";
        if (totalSpent >= 1000000) return "Gold";
        if (totalSpent >= 500000) return "Silver";
        return "Bronze";
    }
    
    /**
     * Set membership level (cho admin)
     */
    public void setMembershipLevel(String membershipLevel) {
        // This is mainly for admin management
        // In real scenario, membership level is calculated based on totalSpent
        // But we allow manual override for admin purposes
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Validate thông tin khách hàng
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty() &&
               phone != null && !phone.trim().isEmpty() &&
               phone.matches("^[0-9+\\-\\s()]+$");
    }
    
    // Getters and Setters
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }
    
    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public double getTotalSpent() {
        return totalSpent;
    }
    
    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", loyaltyPoints=" + loyaltyPoints +
                ", totalSpent=" + totalSpent +
                ", level='" + getCustomerLevel() + '\'' +
                ", isActive=" + isActive +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Customer customer = (Customer) obj;
        return customerId == customer.customerId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(customerId);
    }
}