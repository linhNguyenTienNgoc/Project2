package com.cafe.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class for formatting prices and currency values
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PriceFormatter {
    
    private static final NumberFormat VND_FORMAT = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
    
    /**
     * Format number to VND currency format
     */
    public static String formatVND(double amount) {
        return VND_FORMAT.format(amount) + " ₫";
    }
    
    /**
     * Format number to VND currency format with unit
     */
    public static String formatVNDWithUnit(double amount) {
        return VND_FORMAT.format(amount) + " VNĐ";
    }
    
    /**
     * Format number without currency symbol
     */
    public static String formatNumber(double amount) {
        return VND_FORMAT.format(amount);
    }
    
    /**
     * Parse VND formatted string to double
     */
    public static double parseVND(String formattedAmount) {
        try {
            // Remove currency symbols and parse
            String cleanAmount = formattedAmount
                .replace("₫", "")
                .replace("VNĐ", "")
                .replace(",", "")
                .trim();
            return Double.parseDouble(cleanAmount);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
