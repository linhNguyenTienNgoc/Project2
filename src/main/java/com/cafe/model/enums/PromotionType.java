package com.cafe.model.enums;

/**
 * Enum định nghĩa các loại khuyến mãi trong hệ thống
 */
public enum PromotionType {
    PERCENTAGE("PERCENTAGE", "Giảm giá phần trăm"),
    FIXED_AMOUNT("FIXED_AMOUNT", "Giảm giá cố định"),
    BUY_ONE_GET_ONE("BUY_ONE_GET_ONE", "Mua 1 tặng 1"),
    FREE_SHIPPING("FREE_SHIPPING", "Miễn phí ship"),
    DISCOUNT_PRODUCT("DISCOUNT_PRODUCT", "Giảm giá sản phẩm"),
    BUNDLE_OFFER("BUNDLE_OFFER", "Ưu đãi combo");

    private final String code;
    private final String displayName;

    PromotionType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Tìm PromotionType theo code
     */
    public static PromotionType fromCode(String code) {
        for (PromotionType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown PromotionType code: " + code);
    }

    /**
     * Kiểm tra xem có phải là giảm giá phần trăm không
     */
    public boolean isPercentage() {
        return this == PERCENTAGE;
    }

    /**
     * Kiểm tra xem có phải là giảm giá cố định không
     */
    public boolean isFixedAmount() {
        return this == FIXED_AMOUNT;
    }

    /**
     * Kiểm tra xem có phải là khuyến mãi đặc biệt không
     */
    public boolean isSpecialOffer() {
        return this == BUY_ONE_GET_ONE || this == FREE_SHIPPING || this == BUNDLE_OFFER;
    }
}
