package com.cafe.config;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Legacy MySQL Connection class
 * 
 * @deprecated Use {@link DatabaseConfig} instead for better connection pooling and configuration management
 * @author Team 2_C2406L
 * @version 1.0.0
 */
@Deprecated
public class MySQLConnect {
    
    /**
     * @deprecated Use {@link DatabaseConfig#getConnection()} instead
     */
    @Deprecated
    public static Connection getConnection() {
        try {
            // Forward to new DatabaseConfig for better connection pooling
            return DatabaseConfig.getConnection();
        } catch (SQLException e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * @deprecated Use {@link DatabaseConfig#getInstance().testConnection()} instead
     */
    @Deprecated
    public static boolean testConnection() {
        return DatabaseConfig.getInstance().testConnection();
    }
}
