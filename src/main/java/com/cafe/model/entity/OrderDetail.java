package com.cafe.model.entity;

import java.sql.Timestamp;

/**
 * Entity class cho bảng order_details
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class OrderDetail {
    
    private int orderDetailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;
    private String notes;
    private Timestamp createdAt;
    
    // Additional fields for display purposes (not in database)
    private String productName;
    private String categoryName;
    
    // Constructors
    public OrderDetail() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.quantity = 1;
    }
    
    public OrderDetail(int orderId, int productId, int quantity, double unitPrice) {
        this();
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public OrderDetail(int orderId, int productId, int quantity, double unitPrice, String notes) {
        this(orderId, productId, quantity, unitPrice);
        this.notes = notes;
    }
    
    public OrderDetail(int orderDetailId, int orderId, int productId, int quantity, 
                      double unitPrice, double totalPrice, String notes, Timestamp createdAt) {
        this.orderDetailId = orderDetailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.notes = notes;
        this.createdAt = createdAt;
    }
    
    // Business Methods
    
    /**
     * Tính tổng giá dựa trên số lượng và giá đơn vị
     */
    public void calculateTotalPrice() {
        this.totalPrice = this.quantity * this.unitPrice;
    }
    
    /**
     * Cập nhật số lượng và tính lại tổng giá
     */
    public void updateQuantity(int newQuantity) {
        if (newQuantity > 0) {
            this.quantity = newQuantity;
            calculateTotalPrice();
        }
    }
    
    /**
     * Cập nhật giá đơn vị và tính lại tổng giá
     */
    public void updateUnitPrice(double newUnitPrice) {
        if (newUnitPrice >= 0) {
            this.unitPrice = newUnitPrice;
            calculateTotalPrice();
        }
    }
    
    /**
     * Thêm số lượng
     */
    public void addQuantity(int additionalQuantity) {
        this.quantity += additionalQuantity;
        calculateTotalPrice();
    }
    
    /**
     * Giảm số lượng
     */
    public boolean reduceQuantity(int reductionQuantity) {
        if (this.quantity >= reductionQuantity) {
            this.quantity -= reductionQuantity;
            calculateTotalPrice();
            return true;
        }
        return false;
    }
    
    /**
     * Kiểm tra item có hợp lệ không
     */
    public boolean isValid() {
        return orderId > 0 && productId > 0 && quantity > 0 && unitPrice >= 0;
    }
    
    /**
     * Lấy giá trị chiết khấu nếu có
     */
    public double getDiscountAmount(double discountPercent) {
        return (totalPrice * discountPercent) / 100;
    }
    
    /**
     * Lấy giá sau chiết khấu
     */
    public double getDiscountedPrice(double discountPercent) {
        return totalPrice - getDiscountAmount(discountPercent);
    }
    
    // Getters and Setters
    public int getOrderDetailId() {
        return orderDetailId;
    }
    
    public void setOrderDetailId(int orderDetailId) {
        this.orderDetailId = orderDetailId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getProductId() {
        return productId;
    }
    
    public void setProductId(int productId) {
        this.productId = productId;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateTotalPrice();
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateTotalPrice();
    }
    
    public double getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
    
    // Display fields (not in database)
    public String getProductName() {
        return productName;
    }
    
    public void setProductName(String productName) {
        this.productName = productName;
    }
    
    public String getCategoryName() {
        return categoryName;
    }
    
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    @Override
    public String toString() {
        return "OrderDetail{" +
                "orderDetailId=" + orderDetailId +
                ", orderId=" + orderId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", totalPrice=" + totalPrice +
                ", notes='" + notes + '\'' +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderDetail that = (OrderDetail) obj;
        return orderDetailId == that.orderDetailId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(orderDetailId);
    }
}