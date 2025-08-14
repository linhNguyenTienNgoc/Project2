package com.cafe.model.enums;

/**
 * Enum cho trạng thái bàn
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public enum TableStatus {
    AVAILABLE("available", "Trống"),
    OCCUPIED("occupied", "Có khách"),
    RESERVED("reserved", "Đã đặt trước"),
    CLEANING("cleaning", "Đang dọn dẹp");
    
    private final String value;
    private final String displayName;
    
    TableStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static TableStatus fromString(String text) {
        for (TableStatus status : TableStatus.values()) {
            if (status.value.equalsIgnoreCase(text)) {
                return status;
            }
        }
        return AVAILABLE; // default
    }
    
    public boolean isAvailableForBooking() {
        return this == AVAILABLE;
    }
    
    public boolean canTakeOrder() {
        return this == OCCUPIED || this == RESERVED;
    }
    
    public boolean needsCleaning() {
        return this == CLEANING;
    }
}