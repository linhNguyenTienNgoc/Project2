package com.cafe.model.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Entity class cho bảng promotions
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class Promotion {
    
    private int promotionId;
    private String promotionName;
    private String description;
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private double discountValue;
    private double minOrderAmount;
    private Timestamp startDate;
    private Timestamp endDate;
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Additional fields for business logic
    private int usageCount; // Số lần đã sử dụng
    private int maxUsage;   // Số lần sử dụng tối đa
    
    // Enum for discount types
    public enum DiscountType {
        PERCENTAGE("PERCENTAGE"),
        FIXED_AMOUNT("FIXED_AMOUNT");
        
        private final String value;
        
        DiscountType(String value) {
            this.value = value;
        }
        
        public String getValue() {
            return value;
        }
        
        public static DiscountType fromString(String text) {
            for (DiscountType type : DiscountType.values()) {
                if (type.value.equalsIgnoreCase(text)) {
                    return type;
                }
            }
            return PERCENTAGE; // default
        }
    }
    
    // Constructors
    public Promotion() {
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
        this.discountType = DiscountType.PERCENTAGE.getValue();
        this.usageCount = 0;
    }
    
    public Promotion(String promotionName, String description, DiscountType discountType, 
                    double discountValue, double minOrderAmount) {
        this();
        this.promotionName = promotionName;
        this.description = description;
        this.discountType = discountType.getValue();
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
    }
    
    public Promotion(String promotionName, String description, DiscountType discountType, 
                    double discountValue, double minOrderAmount, 
                    Timestamp startDate, Timestamp endDate) {
        this(promotionName, description, discountType, discountValue, minOrderAmount);
        this.startDate = startDate;
        this.endDate = endDate;
    }
    
    public Promotion(int promotionId, String promotionName, String description, 
                    String discountType, double discountValue, double minOrderAmount, 
                    Timestamp startDate, Timestamp endDate, boolean isActive, 
                    Timestamp createdAt, Timestamp updatedAt) {
        this.promotionId = promotionId;
        this.promotionName = promotionName;
        this.description = description;
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.minOrderAmount = minOrderAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Business Methods
    
    /**
     * Kiểm tra promotion có hợp lệ không
     */
    public boolean isValidPromotion() {
        return isActive && 
               promotionName != null && !promotionName.trim().isEmpty() &&
               discountValue > 0;
    }
    
    /**
     * Kiểm tra promotion có trong thời gian áp dụng không
     */
    public boolean isInValidPeriod() {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        if (startDate != null && now.before(startDate)) {
            return false; // Chưa bắt đầu
        }
        
        if (endDate != null && now.after(endDate)) {
            return false; // Đã hết hạn
        }
        
        return true;
    }
    
    /**
     * Kiểm tra có thể áp dụng cho đơn hàng không
     */
    public boolean canApplyToOrder(double orderAmount) {
        return isValidPromotion() && 
               isInValidPeriod() && 
               orderAmount >= minOrderAmount &&
               (maxUsage <= 0 || usageCount < maxUsage);
    }
    
    /**
     * Tính số tiền giảm giá
     */
    public double calculateDiscountAmount(double orderAmount) {
        if (!canApplyToOrder(orderAmount)) {
            return 0;
        }
        
        DiscountType type = DiscountType.fromString(discountType);
        
        switch (type) {
            case PERCENTAGE:
                double percentDiscount = (orderAmount * discountValue) / 100;
                // Giới hạn tối đa 50% cho phần trăm
                return Math.min(percentDiscount, orderAmount * 0.5);
                
            case FIXED_AMOUNT:
                return Math.min(discountValue, orderAmount);
                
            default:
                return 0;
        }
    }
    
    /**
     * Áp dụng promotion (tăng usage count)
     */
    public boolean applyPromotion() {
        if (maxUsage > 0 && usageCount >= maxUsage) {
            return false;
        }
        
        usageCount++;
        updatedAt = new Timestamp(System.currentTimeMillis());
        return true;
    }
    
    /**
     * Kiểm tra promotion sắp hết hạn (trong vòng 7 ngày)
     */
    public boolean isExpiringSoon() {
        if (endDate == null) return false;
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDateTime = endDate.toLocalDateTime();
        LocalDateTime weekFromNow = now.plusDays(7);
        
        return endDateTime.isBefore(weekFromNow) && endDateTime.isAfter(now);
    }
    
    /**
     * Lấy trạng thái promotion
     */
    public String getPromotionStatus() {
        if (!isActive) return "Inactive";
        if (!isInValidPeriod()) {
            if (startDate != null && new Timestamp(System.currentTimeMillis()).before(startDate)) {
                return "Upcoming";
            } else {
                return "Expired";
            }
        }
        if (maxUsage > 0 && usageCount >= maxUsage) return "Used Up";
        if (isExpiringSoon()) return "Expiring Soon";
        return "Active";
    }
    
    /**
     * Format discount value cho hiển thị
     */
    public String getFormattedDiscountValue() {
        DiscountType type = DiscountType.fromString(discountType);
        
        switch (type) {
            case PERCENTAGE:
                return String.format("%.0f%%", discountValue);
            case FIXED_AMOUNT:
                return String.format("%,.0f VND", discountValue);
            default:
                return discountValue + "";
        }
    }
    
    // Getters and Setters
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
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(String discountType) {
        this.discountType = discountType;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType.getValue();
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public double getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public void setMinOrderAmount(double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Timestamp getStartDate() {
        return startDate;
    }
    
    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public Timestamp getEndDate() {
        return endDate;
    }
    
    public void setEndDate(Timestamp endDate) {
        this.endDate = endDate;
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
    
    public int getUsageCount() {
        return usageCount;
    }
    
    public void setUsageCount(int usageCount) {
        this.usageCount = usageCount;
    }
    
    public int getMaxUsage() {
        return maxUsage;
    }
    
    public void setMaxUsage(int maxUsage) {
        this.maxUsage = maxUsage;
    }
    
    @Override
    public String toString() {
        return "Promotion{" +
                "promotionId=" + promotionId +
                ", promotionName='" + promotionName + '\'' +
                ", discountType='" + discountType + '\'' +
                ", discountValue=" + getFormattedDiscountValue() +
                ", minOrderAmount=" + minOrderAmount +
                ", status='" + getPromotionStatus() + '\'' +
                ", isActive=" + isActive +
                '}';
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