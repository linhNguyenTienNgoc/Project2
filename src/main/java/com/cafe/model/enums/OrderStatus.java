package com.cafe.model.enums;

/**
 * Enum cho trạng thái đơn hàng
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public enum OrderStatus {
    PENDING("pending", "Chờ xử lý"),
    PREPARING("preparing", "Đang chuẩn bị"),
    READY("ready", "Sẵn sàng phục vụ"),
    SERVED("served", "Đã phục vụ"),
    COMPLETED("completed", "Hoàn thành"),
    CANCELLED("cancelled", "Đã hủy");
    
    private final String value;
    private final String displayName;
    
    OrderStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static OrderStatus fromString(String text) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return PENDING; // default
    }
    
    public boolean isActive() {
        return this != CANCELLED && this != COMPLETED;
    }
    
    public boolean isEditable() {
        return this == PENDING || this == PREPARING;
    }
    
    public boolean canCancel() {
        return this == PENDING || this == PREPARING;
    }
    
    public boolean canServe() {
        return this == READY;
    }
    
    public boolean canComplete() {
        return this == SERVED;
    }
}