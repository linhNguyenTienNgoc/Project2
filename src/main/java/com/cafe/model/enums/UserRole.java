package com.cafe.model.enums;

/**
 * Enum định nghĩa các vai trò người dùng trong hệ thống
 */
public enum UserRole {
    ADMIN("ADMIN", "Quản trị viên"),
    MANAGER("MANAGER", "Quản lý"),
    STAFF("STAFF", "Nhân viên"),
    CASHIER("CASHIER", "Thu ngân"),
    WAITER("WAITER", "Phục vụ"),
    BARISTA("BARISTA", "Pha chế");

    private final String code;
    private final String displayName;

    UserRole(String code, String displayName) {
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
     * Tìm UserRole theo code
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown UserRole code: " + code);
    }

    /**
     * Kiểm tra xem có phải là admin không
     */
    public boolean isAdmin() {
        return this == ADMIN;
    }

    /**
     * Kiểm tra xem có phải là quản lý trở lên không
     */
    public boolean isManagerOrHigher() {
        return this == ADMIN || this == MANAGER;
    }

    /**
     * Kiểm tra xem có phải là nhân viên trở lên không
     */
    public boolean isStaffOrHigher() {
        return this != null;
    }
}
