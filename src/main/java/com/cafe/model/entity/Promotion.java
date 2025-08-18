package com.cafe.model.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Promotion Entity - Represents promotional discounts and offers
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class Promotion {

    // =====================================================
    // FIELDS
    // =====================================================
    
    private int promotionId;
    private String promotionName;
    private String description;
    private DiscountType discountType;
    private double discountValue;
    private double minOrderAmount;
    private double maxDiscountAmount;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isActive;
    private int usageLimit;
    private int usageCount;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // =====================================================
    // ENUMS
    // =====================================================
    
    public enum DiscountType {
        PERCENTAGE,      // Giảm theo phần trăm
        FIXED_AMOUNT     // Giảm số tiền cố định
    }

    // =====================================================
    // CONSTRUCTORS
    // =====================================================
    
    public Promotion() {}

    public Promotion(String promotionName, String description, DiscountType discountType, 
                    double discountValue, double minOrderAmount) {
        this.promotionName = promotionName;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.isActive = true;
        this.usageCount = 0;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    // =====================================================
    // BUSINESS LOGIC METHODS
    // =====================================================

    /**
     * Check if this promotion can be applied to an order with given amount
     * @param orderAmount Order total amount
     * @return true if promotion can be applied
     */
    public boolean canApplyToOrder(double orderAmount) {
        if (!isValidPromotion()) {
            return false;
        }
        
        if (orderAmount < minOrderAmount) {
            return false;
        }
        
        if (usageLimit > 0 && usageCount >= usageLimit) {
            return false;
        }
        
        return isInValidPeriod();
    }

    /**
     * Calculate discount amount for given order total
     * @param orderAmount Order total amount
     * @return Discount amount
     */
    public double calculateDiscountAmount(double orderAmount) {
        if (!canApplyToOrder(orderAmount)) {
            return 0;
        }
        
        double discount = 0;
        
        switch (discountType) {
            case PERCENTAGE:
                discount = orderAmount * (discountValue / 100.0);
                break;
            case FIXED_AMOUNT:
                discount = discountValue;
                break;
        }
        
        // Apply maximum discount limit if set
        if (maxDiscountAmount > 0 && discount > maxDiscountAmount) {
            discount = maxDiscountAmount;
        }
        
        // Ensure discount doesn't exceed order amount
        if (discount > orderAmount) {
            discount = orderAmount;
        }
        
        return Math.round(discount * 100.0) / 100.0; // Round to 2 decimal places
    }

    /**
     * Check if promotion is valid (active and properly configured)
     * @return true if valid
     */
    public boolean isValidPromotion() {
        return isActive && 
               promotionName != null && !promotionName.trim().isEmpty() &&
               discountValue > 0 &&
               minOrderAmount >= 0;
    }

    /**
     * Check if current time is within promotion period
     * @return true if in valid period
     */
    public boolean isInValidPeriod() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        if (startDate != null && now.before(startDate)) {
            return false;
        }
        
        if (endDate != null && now.after(endDate)) {
            return false;
        }
        
        return true;
    }

    /**
     * Check if promotion is expiring soon (within 7 days)
     * @return true if expiring soon
     */
    public boolean isExpiringSoon() {
        if (endDate == null) {
            return false;
        }
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        Timestamp sevenDaysLater = new Timestamp(now.getTime() + (7 * 24 * 60 * 60 * 1000));
        
        return endDate.before(sevenDaysLater) && endDate.after(now);
    }

    /**
     * Apply promotion (increment usage count)
     */
    public void applyPromotion() {
        this.usageCount++;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    /**
     * Get formatted discount value for display
     * @return Formatted string
     */
    public String getFormattedDiscountValue() {
        switch (discountType) {
            case PERCENTAGE:
                return String.format("%.0f%%", discountValue);
            case FIXED_AMOUNT:
                return String.format("%,.0f ₫", discountValue);
            default:
                return discountValue + "";
        }
    }

    /**
     * Get display text for promotion
     * @return Display text
     */
    public String getDisplayText() {
        return promotionName + " - " + getFormattedDiscountValue();
    }

    // =====================================================
    // GETTERS AND SETTERS
    // =====================================================

    public int getPromotionId() {
        return promotionId;
    }

    public void setPromotionId(int promotionId) {
        this.promotionId = promotionId;
    }

    public String getPromotionName() {
        return promotionName;
    }

    public void setPromotionName(String promotionName) {
        this.promotionName = promotionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public DiscountType getDiscountType() {
        return discountType;
    }

    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }

    public double getMinOrderAmount() {
        return minOrderAmount;
    }

    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }

    public double getMaxDiscountAmount() {
        return maxDiscountAmount;
    }

    public void setMaxDiscountAmount(double maxDiscountAmount) {
        this.maxDiscountAmount = maxDiscountAmount;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public Timestamp getEndDate() {
        return endDate;
    }

    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsageCount() {
        return usageCount;
    }

    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
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

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    @Override
    public String toString() {
        return String.format("Promotion{id=%d, name='%s', type=%s, value=%.2f, minOrder=%.2f, active=%s}",
                promotionId, promotionName, discountType, discountValue, minOrderAmount, isActive);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Promotion promotion = (Promotion) obj;
        return promotionId == promotion.promotionId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(promotionId);
    }
}