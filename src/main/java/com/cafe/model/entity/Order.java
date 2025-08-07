package com.cafe.model.entity;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * Entity class cho bảng orders
 * Cải thiện với đầy đủ fields, validation và business methods
 */
public class Order {
    private Integer orderId;
    private String orderNumber;
    private Integer tableId;
    private Integer customerId;
    private Integer userId;
    private Timestamp orderDate;
    private Double totalAmount;
    private Double discountAmount;
    private Double taxAmount;
    private Double finalAmount;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relationship objects
    private TableCafe table;
    private Customer customer;
    private User user;
    private List<OrderDetail> orderDetails;
    private List<OrderPromotion> orderPromotions;

    // Constructors
    public Order() {
        this.orderDate = new Timestamp(System.currentTimeMillis());
        this.totalAmount = 0.0;
        this.discountAmount = 0.0;
        this.taxAmount = 0.0;
        this.finalAmount = 0.0;
        this.paymentStatus = "pending";
        this.orderStatus = "pending";
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Order(String orderNumber, Integer tableId, Integer userId) {
        this();
        this.orderNumber = orderNumber;
        this.tableId = tableId;
        this.userId = userId;
    }

    public Order(Integer orderId, String orderNumber, Integer tableId, Integer customerId, Integer userId, 
                 Timestamp orderDate, Double totalAmount, Double discountAmount, Double taxAmount, 
                 Double finalAmount, String paymentMethod, String paymentStatus, String orderStatus, String notes) {
        this();
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.tableId = tableId;
        this.customerId = customerId;
        this.userId = userId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.discountAmount = discountAmount;
        this.taxAmount = taxAmount;
        this.finalAmount = finalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.notes = notes;
    }

    // Getters and Setters
    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public Integer getTableId() {
        return tableId;
    }

    public void setTableId(Integer tableId) {
        this.tableId = tableId;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(Double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public Double getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(Double taxAmount) {
        this.taxAmount = taxAmount;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
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

    // Relationship getters and setters
    public TableCafe getTable() {
        return table;
    }

    public void setTable(TableCafe table) {
        this.table = table;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public List<OrderPromotion> getOrderPromotions() {
        return orderPromotions;
    }

    public void setOrderPromotions(List<OrderPromotion> orderPromotions) {
        this.orderPromotions = orderPromotions;
    }

    // Business methods
    public boolean isPending() {
        return "pending".equals(orderStatus);
    }
    
    public boolean isConfirmed() {
        return "confirmed".equals(orderStatus);
    }
    
    public boolean isPreparing() {
        return "preparing".equals(orderStatus);
    }
    
    public boolean isReady() {
        return "ready".equals(orderStatus);
    }
    
    public boolean isCompleted() {
        return "completed".equals(orderStatus);
    }
    
    public boolean isCancelled() {
        return "cancelled".equals(orderStatus);
    }
    
    public boolean isPaid() {
        return "paid".equals(paymentStatus);
    }
    
    public boolean isPendingPayment() {
        return "pending".equals(paymentStatus);
    }
    
    public boolean isFailedPayment() {
        return "failed".equals(paymentStatus);
    }
    
    public boolean canBeCancelled() {
        return isPending() || isConfirmed() || isPreparing();
    }
    
    public boolean canBeCompleted() {
        return isReady();
    }
    
    public boolean canBePaid() {
        return isCompleted() && isPendingPayment();
    }
    
    public void calculateFinalAmount() {
        if (totalAmount != null) {
            double subtotal = totalAmount;
            double discount = discountAmount != null ? discountAmount : 0.0;
            double tax = taxAmount != null ? taxAmount : 0.0;
            this.finalAmount = subtotal - discount + tax;
        }
    }
    
    public Double getSubtotal() {
        if (orderDetails != null) {
            return orderDetails.stream()
                    .mapToDouble(detail -> {
                        Double price = detail.getTotalPrice();
                        return price != null ? price : 0.0;
                    })
                    .sum();
        }
        return totalAmount;
    }
    
    public int getTotalItems() {
        if (orderDetails != null) {
            return orderDetails.stream()
                    .mapToInt(detail -> {
                        Integer quantity = detail.getQuantity();
                        return quantity != null ? quantity : 0;
                    })
                    .sum();
        }
        return 0;
    }
    
    public String getOrderStatusDisplay() {
        switch (orderStatus) {
            case "pending": return "Chờ xác nhận";
            case "confirmed": return "Đã xác nhận";
            case "preparing": return "Đang chuẩn bị";
            case "ready": return "Sẵn sàng";
            case "completed": return "Hoàn thành";
            case "cancelled": return "Đã hủy";
            default: return orderStatus;
        }
    }
    
    public String getPaymentStatusDisplay() {
        switch (paymentStatus) {
            case "pending": return "Chờ thanh toán";
            case "paid": return "Đã thanh toán";
            case "failed": return "Thanh toán thất bại";
            case "refunded": return "Đã hoàn tiền";
            default: return paymentStatus;
        }
    }
    
    public String getPaymentMethodDisplay() {
        switch (paymentMethod) {
            case "cash": return "Tiền mặt";
            case "card": return "Thẻ";
            case "momo": return "MoMo";
            case "zalo": return "ZaloPay";
            case "bank_transfer": return "Chuyển khoản";
            default: return paymentMethod;
        }
    }

    // Validation methods
    public boolean isValidOrderNumber() {
        return orderNumber != null && !orderNumber.trim().isEmpty();
    }
    
    public boolean isValidTableId() {
        return tableId != null && tableId > 0;
    }
    
    public boolean isValidUserId() {
        return userId != null && userId > 0;
    }
    
    public boolean isValidTotalAmount() {
        return totalAmount != null && totalAmount >= 0;
    }
    
    public boolean isValidDiscountAmount() {
        return discountAmount == null || (discountAmount >= 0 && 
               (totalAmount == null || discountAmount <= totalAmount));
    }
    
    public boolean isValidTaxAmount() {
        return taxAmount == null || taxAmount >= 0;
    }
    
    public boolean isValid() {
        return isValidOrderNumber() && isValidTableId() && isValidUserId() && 
               isValidTotalAmount() && isValidDiscountAmount() && isValidTaxAmount();
    }

    // Utility methods
    public void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getFormattedTotalAmount() {
        if (totalAmount != null) {
            return String.format("%,.0f VNĐ", totalAmount);
        }
        return "0 VNĐ";
    }
    
    public String getFormattedFinalAmount() {
        if (finalAmount != null) {
            return String.format("%,.0f VNĐ", finalAmount);
        }
        return "0 VNĐ";
    }
    
    public String getFormattedDiscountAmount() {
        if (discountAmount != null) {
            return String.format("%,.0f VNĐ", discountAmount);
        }
        return "0 VNĐ";
    }
    
    public String getCustomerName() {
        return customer != null ? customer.getFullName() : "Khách lẻ";
    }
    
    public String getTableName() {
        return table != null ? table.getTableName() : "Bàn " + tableId;
    }
    
    public String getUserName() {
        return user != null ? user.getFullName() : "Nhân viên " + userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId) && 
               Objects.equals(orderNumber, order.orderNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, orderNumber);
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
                ", finalAmount=" + finalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", customerName='" + getCustomerName() + '\'' +
                ", tableName='" + getTableName() + '\'' +
                '}';
    }
}
