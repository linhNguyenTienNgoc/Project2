package com.cafe.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Utility class cho formatting prices và currency
 */
public class PriceFormatter {
    
    private static final DecimalFormat VND_FORMAT = new DecimalFormat("#,###");
    private static final NumberFormat CURRENCY_FORMAT = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    
    /**
     * Format price thành VND với comma separator
     */
    public static String formatVND(Double price) {
        if (price == null) {
            return "0 VNĐ";
        }
        
        try {
            return VND_FORMAT.format(price) + " VNĐ";
        } catch (Exception e) {
            return "0 VNĐ";
        }
    }
    
    /**
     * Format price thành currency format
     */
    public static String formatCurrency(Double price) {
        if (price == null) {
            return "0 ₫";
        }
        
        try {
            return CURRENCY_FORMAT.format(price);
        } catch (Exception e) {
            return "0 ₫";
        }
    }
    
    /**
     * Format price thành compact format (e.g., 1.5K, 2.3M)
     */
    public static String formatCompact(Double price) {
        if (price == null) {
            return "0";
        }
        
        try {
            if (price < 1000) {
                return String.format("%.0f", price);
            } else if (price < 1000000) {
                return String.format("%.1fK", price / 1000);
            } else {
                return String.format("%.1fM", price / 1000000);
            }
        } catch (Exception e) {
            return "0";
        }
    }
    
    /**
     * Format price thành compact format với VND
     */
    public static String formatCompactVND(Double price) {
        return formatCompact(price) + " VNĐ";
    }
    
    /**
     * Parse VND string thành Double
     */
    public static Double parseVND(String priceString) {
        if (priceString == null || priceString.trim().isEmpty()) {
            return 0.0;
        }
        
        try {
            // Remove VNĐ and spaces
            String cleanPrice = priceString.replaceAll("[^0-9,]", "");
            // Remove commas
            cleanPrice = cleanPrice.replace(",", "");
            
            return Double.parseDouble(cleanPrice);
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    /**
     * Format percentage
     */
    public static String formatPercentage(Double percentage) {
        if (percentage == null) {
            return "0%";
        }
        
        try {
            return String.format("%.1f%%", percentage);
        } catch (Exception e) {
            return "0%";
        }
    }
    
    /**
     * Format discount amount
     */
    public static String formatDiscount(Double originalPrice, Double discountPrice) {
        if (originalPrice == null || discountPrice == null) {
            return "0 VNĐ";
        }
        
        double discount = originalPrice - discountPrice;
        return formatVND(discount);
    }
    
    /**
     * Format discount percentage
     */
    public static String formatDiscountPercentage(Double originalPrice, Double discountPrice) {
        if (originalPrice == null || discountPrice == null || originalPrice == 0) {
            return "0%";
        }
        
        double discountPercent = ((originalPrice - discountPrice) / originalPrice) * 100;
        return formatPercentage(discountPercent);
    }
    
    /**
     * Format total amount với tax
     */
    public static String formatTotalWithTax(Double subtotal, Double taxRate) {
        if (subtotal == null) {
            return "0 VNĐ";
        }
        
        if (taxRate == null) {
            return formatVND(subtotal);
        }
        
        double total = subtotal + (subtotal * taxRate / 100);
        return formatVND(total);
    }
    
    /**
     * Format total amount với discount
     */
    public static String formatTotalWithDiscount(Double subtotal, Double discountAmount) {
        if (subtotal == null) {
            return "0 VNĐ";
        }
        
        if (discountAmount == null) {
            return formatVND(subtotal);
        }
        
        double total = subtotal - discountAmount;
        return formatVND(Math.max(0, total));
    }
    
    /**
     * Format total amount với discount và tax
     */
    public static String formatTotalWithDiscountAndTax(Double subtotal, Double discountAmount, Double taxRate) {
        if (subtotal == null) {
            return "0 VNĐ";
        }
        
        double afterDiscount = subtotal - (discountAmount != null ? discountAmount : 0);
        double total = afterDiscount + (afterDiscount * (taxRate != null ? taxRate : 0) / 100);
        
        return formatVND(Math.max(0, total));
    }
    
    /**
     * Check if price is valid
     */
    public static boolean isValidPrice(Double price) {
        return price != null && price >= 0;
    }
    
    /**
     * Check if price string is valid
     */
    public static boolean isValidPriceString(String priceString) {
        if (priceString == null || priceString.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double price = parseVND(priceString);
            return isValidPrice(price);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get formatted price range
     */
    public static String formatPriceRange(Double minPrice, Double maxPrice) {
        if (minPrice == null && maxPrice == null) {
            return "Liên hệ";
        }
        
        if (minPrice == null) {
            return "Dưới " + formatVND(maxPrice);
        }
        
        if (maxPrice == null) {
            return "Từ " + formatVND(minPrice);
        }
        
        if (minPrice.equals(maxPrice)) {
            return formatVND(minPrice);
        }
        
        return formatVND(minPrice) + " - " + formatVND(maxPrice);
    }
}
