package com.cafe.model.entity;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private String orderNumber;
    private int tableId;
    private Integer customerId;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;
    private double discountAmount;
    private double finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Order() {
    }

    public Order(int orderId, String orderNumber, int tableId, Integer customerId, int userId, Timestamp orderDate, double totalAmount, double discountAmount, double finalAmount, String paymentMethod, String paymentStatus, String orderStatus, String notes) {
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.tableId = tableId;
        this.customerId = customerId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.notes = notes;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    // Business logic methods
    public boolean canBeCancelled() {
        return "pending".equals(orderStatus) || "preparing".equals(orderStatus);
    }

    /**
     * ✅ ENHANCED: Allow completion for orders that have been paid
     * Support early payment workflow where customers pay before food is served
     */
    public boolean canBeCompleted() {
        // Traditional workflow: served → completed
        if ("served".equals(orderStatus)) {
            return true;
        }
        
        // ✅ NEW: Early payment workflow: preparing/ready + paid → completed
        if (("preparing".equals(orderStatus) || "ready".equals(orderStatus)) && "paid".equals(paymentStatus)) {
            return true;
        }
        
        // ✅ Handle edge case: already completed orders (idempotent)
        if ("completed".equals(orderStatus)) {
            return false; // Prevent double completion
        }
        
        return false;
    }

    public boolean canBePaid() {
        // ✅ FIXED: Allow payment for confirmed orders (not just completed)
        String[] payableStatuses = {"confirmed", "preparing", "ready", "served", "completed"};
        boolean hasPayableStatus = false;
        
        for (String status : payableStatuses) {
            if (status.equals(orderStatus)) {
                hasPayableStatus = true;
                break;
            }
        }
        
        return hasPayableStatus && 
               ("pending".equals(paymentStatus) || paymentStatus == null) &&
               finalAmount > 0;
    }

    public void calculateFinalAmount() {
        this.finalAmount = this.totalAmount - this.discountAmount;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", tableId=" + tableId +
                ", customerId=" + customerId +
                ", userId=" + userId +
                ", orderDate=" + orderDate +
                ", totalAmount=" + totalAmount +
                ", discountAmount=" + discountAmount +
                ", finalAmount=" + finalAmount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
