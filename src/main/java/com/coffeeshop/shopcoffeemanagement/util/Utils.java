package com.coffeeshop.shopcoffeemanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Utils {
    
    public static String formatCurrency(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }
    
    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return dateTime.format(formatter);
    }
    
    public static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return dateTime.format(formatter);
    }
} 