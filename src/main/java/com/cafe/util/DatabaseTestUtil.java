package com.cafe.util;

import com.cafe.config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Utility class để test database connection và operations
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class DatabaseTestUtil {
    
    /**
     * Test basic database connection
     */
    public static boolean testConnection() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            boolean isConnected = dbConfig.testConnection();
            
            if (isConnected) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Database connection error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Test database và kiểm tra tables có tồn tại không
     */
    public static boolean testDatabaseStructure() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Danh sách các table cần kiểm tra
            String[] requiredTables = {
                "roles", "users", "categories", "products", 
                "areas", "tables", "customers", "orders", 
                "order_details", "promotions", "attendance"
            };
            
            boolean allTablesExist = true;
            for (String tableName : requiredTables) {
                boolean exists = checkTableExists(metaData, tableName);
                if (!exists) {
                    allTablesExist = false;
                }
            }
            
            if (allTablesExist) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Database structure test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Kiểm tra table có tồn tại không
     */
    private static boolean checkTableExists(DatabaseMetaData metaData, String tableName) {
        try (ResultSet tables = metaData.getTables(null, null, tableName, new String[]{"TABLE"})) {
            return tables.next();
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Test sample data trong database
     */
    public static boolean testSampleData() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            // Test roles table
            int roleCount = getTableRowCount(connection, "roles");
            
            // Test users table  
            int userCount = getTableRowCount(connection, "users");
            
            // Test categories table
            int categoryCount = getTableRowCount(connection, "categories");
            
            // Test products table
            int productCount = getTableRowCount(connection, "products");
            
            // Test areas table
            int areaCount = getTableRowCount(connection, "areas");
            
            // Test tables table
            int tableCount = getTableRowCount(connection, "tables");
            
            boolean hasData = roleCount > 0 && userCount > 0 && categoryCount > 0 && productCount > 0;
            
            if (hasData) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Sample data test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Đếm số lượng record trong table
     */
    private static int getTableRowCount(Connection connection, String tableName) {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM " + tableName)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
            
        } catch (Exception e) {
            System.err.println("❌ Error counting rows in table " + tableName + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Test authentication với sample user
     */
    public static boolean testAuthentication() {
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT username, full_name, is_active FROM users WHERE is_active = 1 LIMIT 5"
            );
            
            boolean hasUsers = false;
            
            while (rs.next()) {
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                boolean isActive = rs.getBoolean("is_active");
                
                hasUsers = true;
            }
            
            if (hasUsers) {
                return true;
            } else {
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Authentication test error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Chạy tất cả các test
     */
    public static void runAllTests() {
        boolean connectionTest = testConnection();
        boolean structureTest = testDatabaseStructure();
        boolean dataTest = testSampleData();
        boolean authTest = testAuthentication();
        
        boolean allPassed = connectionTest && structureTest && authTest;
    }
    
    /**
     * Main method để chạy test standalone
     */
    public static void main(String[] args) {
        runAllTests();
    }
}