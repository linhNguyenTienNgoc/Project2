package com.cafe.model.entity;

import java.sql.Timestamp;

/**
 * Entity class cho bảng order_promotions
 * Liên kết giữa đơn hàng và khuyến mãi được áp dụng
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class OrderPromotion {
    
    private int orderPromotionId;
    private int orderId;
    private int promotionId;
    private double discountAmount;
    private Timestamp createdAt;
    
    // Additional fields for display purposes
    private String promotionName;
    private String discountType;
    private double discountValue;
    private double orderTotalAmount;
    
    // Constructors
    public OrderPromotion() {
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    public OrderPromotion(int orderId, int promotionId, double discountAmount) {
        this();
        this.orderId = orderId;
        this.promotionId = promotionId;
        this.discountAmount = discountAmount;
    }
    
    public OrderPromotion(int orderPromotionId, int orderId, int promotionId, 
                         double discountAmount, Timestamp createdAt) {
        this.orderPromotionId = orderPromotionId;
        this.orderId = orderId;
        this.promotionId = promotionId;
        this.discountAmount = discountAmount;
        this.createdAt = createdAt;
    }
    
    // Business Methods
    
    /**
     * Tính phần trăm giảm giá so với tổng đơn hàng
     */
    public double getDiscountPercentage() {
        if (orderTotalAmount <= 0) return 0;
        return (discountAmount / orderTotalAmount) * 100;
    }
    
    /**
     * Validate thông tin order promotion
     */
    public boolean isValid() {
        return orderId > 0 && 
               promotionId > 0 && 
               discountAmount >= 0;
    }
    
    /**
     * Kiểm tra có phải discount lớn không (>= 20% hoặc >= 100,000 VND)
     */
    public boolean isSignificantDiscount() {
        return discountAmount >= 100000 || getDiscountPercentage() >= 20;
    }
    
    /**
     * Format hiển thị số tiền giảm giá
     */
    public String getFormattedDiscountAmount() {
        return String.format("%,.0f VND", discountAmount);
    }
    
    /**
     * Format hiển thị phần trăm giảm giá
     */
    public String getFormattedDiscountPercentage() {
        return String.format("%.1f%%", getDiscountPercentage());
    }
    
    /**
     * Lấy mô tả chi tiết khuyến mãi được áp dụng
     */
    public String getPromotionDescription() {
        StringBuilder desc = new StringBuilder();
        
        if (promotionName != null && !promotionName.trim().isEmpty()) {
            desc.append(promotionName);
        } else {
            desc.append("Promotion ").append(promotionId);
        }
        
        desc.append(" - Giảm ");
        desc.append(getFormattedDiscountAmount());
        
        if (orderTotalAmount > 0) {
            desc.append(" (").append(getFormattedDiscountPercentage()).append(")");
        }
        
        return desc.toString();
    }
    
    /**
     * Tính giá trị tiết kiệm cho khách hàng
     */
    public double getSavingsValue() {
        return discountAmount;
    }
    
    /**
     * Tính hiệu quả của promotion (discount amount vs minimum order)
     */
    public double getPromotionEffectiveness(double minOrderAmount) {
        if (minOrderAmount <= 0) return 0;
        return (discountAmount / minOrderAmount) * 100;
    }
    
    // Getters and Setters
    public int getOrderPromotionId() {
        return orderPromotionId;
    }
    
    public void setOrderPromotionId(int orderPromotionId) {
        this.orderPromotionId = orderPromotionId;
    }
    
    public int getOrderId() {
        return orderId;
    }
    
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public int getPromotionId() {
        return promotionId;
    }
    
    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }
    
    public double getDiscountAmount() {
        return discountAmount;
    }
    
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }
    
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    // Display fields
    public String getPromotionName() {
        return promotionName;
    }
    
    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }
    
    public double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }
    
    public double getOrderTotalAmount() {
        return orderTotalAmount;
    }
    
    public void setOrderTotalAmount(double orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }
    
    @Override
    public String toString() {
        return "OrderPromotion{" +
                "orderPromotionId=" + orderPromotionId +
                ", orderId=" + orderId +
                ", promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", discountAmount=" + getFormattedDiscountAmount() +
                ", percentage=" + getFormattedDiscountPercentage() +
                '}';
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        OrderPromotion that = (OrderPromotion) obj;
        return orderPromotionId == that.orderPromotionId;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(orderPromotionId);
    }
}