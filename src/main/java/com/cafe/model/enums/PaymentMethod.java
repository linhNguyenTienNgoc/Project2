package com.cafe.model.enums;

/**
 * Enum cho phương thức thanh toán
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public enum PaymentMethod {
    CASH("cash", "Tiền mặt"),
    CARD("card", "Thẻ tín dụng/ghi nợ"),
    MOMO("momo", "Ví MoMo"),
    VNPAY("vnpay", "VNPay"),
    ZALOPAY("zalopay", "ZaloPay"),
    BANK_TRANSFER("bank_transfer", "Chuyển khoản");
    
    private final String value;
    private final String displayName;
    
    PaymentMethod(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }
    
    public String getValue() {
        return value;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public static PaymentMethod fromString(String text) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equalsIgnoreCase(text)) {
                return method;
            }
        }
        return CASH; // default
    }
    
    public boolean isElectronic() {
        return this != CASH;
    }
    
    public boolean requiresOnlineVerification() {
        return this == MOMO || this == VNPAY || this == ZALOPAY;
    }
}