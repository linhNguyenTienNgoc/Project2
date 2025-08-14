package com.cafe.model.enums;

/**
 * Enum cho trạng thái thanh toán
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public enum PaymentStatus {
    PENDING("pending", "Chờ thanh toán"),
    PAID("paid", "Đã thanh toán"),
    CANCELLED("cancelled", "Đã hủy");
    
    private final String value;
    private final String displayName;
    
    PaymentStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static PaymentStatus fromString(String text) {
        for (PaymentStatus status : PaymentStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING; // default
    }
    
    public boolean isPaid() {
        return this == PAID;
    }
    
    public boolean canRefund() {
        return this == PAID;
    }
}