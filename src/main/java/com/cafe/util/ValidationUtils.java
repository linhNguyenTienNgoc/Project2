package com.cafe.util;

import java.util.regex.Pattern;

public class ValidationUtils {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9]{10,11}$"
    );

    /**
     * Check if string is null or empty
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return !isEmpty(email) && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return !isEmpty(phone) && PHONE_PATTERN.matcher(phone).matches();
    }

    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (isEmpty(password)) {
            return false;
        }
        
        // Mật khẩu phải có ít nhất 6 ký tự
        if (password.length() < 6) {
            return false;
        }
        
        return true;
    }

    /**
     * Get password strength description
     */
    public static String getPasswordStrengthDescription(String password) {
        if (isEmpty(password)) {
            return "Mật khẩu trống";
        }
        
        int score = 0;
        String description = "";
        
        // Length check
        if (password.length() >= 8) {
            score += 1;
        }
        if (password.length() >= 12) {
            score += 1;
        }
        
        // Character type checks
        if (password.matches(".*[a-z].*")) { // lowercase
            score += 1;
        }
        if (password.matches(".*[A-Z].*")) { // uppercase
            score += 1;
        }
        if (password.matches(".*[0-9].*")) { // numbers
            score += 1;
        }
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*")) { // special chars
            score += 1;
        }
        
        // Return description based on score
        switch (score) {
            case 0:
            case 1:
                description = "🔴 Rất yếu";
                break;
            case 2:
            case 3:
                description = "🟡 Yếu";
                break;
            case 4:
                description = "🟠 Trung bình";
                break;
            case 5:
                description = "🟢 Mạnh";
                break;
            case 6:
                description = "💚 Rất mạnh";
                break;
            default:
                description = "❓ Không xác định";
        }
        
        return description;
    }

    /**
     * Validate positive number
     */
    public static boolean isPositiveNumber(String str) {
        try {
            double value = Double.parseDouble(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate non-negative number
     */
    public static boolean isNonNegativeNumber(String str) {
        try {
            double value = Double.parseDouble(str);
            return value >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate integer
     */
    public static boolean isValidInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Validate positive integer
     */
    public static boolean isPositiveInteger(String str) {
        try {
            int value = Integer.parseInt(str);
            return value > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
