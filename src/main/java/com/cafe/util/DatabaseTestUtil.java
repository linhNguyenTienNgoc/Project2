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
        System.out.println("🔄 Testing database connection...");
        
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            boolean isConnected = dbConfig.testConnection();
            
            if (isConnected) {
                System.out.println("✅ Database connection successful!");
                System.out.println(dbConfig.getDatabaseInfo());
                System.out.println(dbConfig.getPoolInfo());
                return true;
            } else {
                System.out.println("❌ Database connection failed!");
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
        System.out.println("🔄 Testing database structure...");
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            DatabaseMetaData metaData = connection.getMetaData();
            
            // Danh sách các table cần kiểm tra
            String[] requiredTables = {
                "roles", "users", "categories", "products", 
                "areas", "tables", "customers", "orders", 
                "order_details", "promotions", "attendance"
            };
            
            System.out.println("📋 Checking required tables:");
            
            boolean allTablesExist = true;
            for (String tableName : requiredTables) {
                boolean exists = checkTableExists(metaData, tableName);
                System.out.printf("  %s %s\n", 
                    exists ? "✅" : "❌", 
                    tableName
                );
                if (!exists) {
                    allTablesExist = false;
                }
            }
            
            if (allTablesExist) {
                System.out.println("✅ All required tables exist!");
                return true;
            } else {
                System.out.println("❌ Some required tables are missing!");
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
        System.out.println("🔄 Testing sample data...");
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            // Test roles table
            int roleCount = getTableRowCount(connection, "roles");
            System.out.println("📊 Roles: " + roleCount + " records");
            
            // Test users table  
            int userCount = getTableRowCount(connection, "users");
            System.out.println("📊 Users: " + userCount + " records");
            
            // Test categories table
            int categoryCount = getTableRowCount(connection, "categories");
            System.out.println("📊 Categories: " + categoryCount + " records");
            
            // Test products table
            int productCount = getTableRowCount(connection, "products");
            System.out.println("📊 Products: " + productCount + " records");
            
            // Test areas table
            int areaCount = getTableRowCount(connection, "areas");
            System.out.println("📊 Areas: " + areaCount + " records");
            
            // Test tables table
            int tableCount = getTableRowCount(connection, "tables");
            System.out.println("📊 Tables: " + tableCount + " records");
            
            boolean hasData = roleCount > 0 && userCount > 0 && categoryCount > 0 && productCount > 0;
            
            if (hasData) {
                System.out.println("✅ Sample data found in database!");
                return true;
            } else {
                System.out.println("⚠️ No sample data found. You may need to import sample data.");
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
        System.out.println("🔄 Testing authentication...");
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT username, full_name, is_active FROM users WHERE is_active = 1 LIMIT 5"
            );
            
            System.out.println("👥 Available test users:");
            boolean hasUsers = false;
            
            while (rs.next()) {
                String username = rs.getString("username");
                String fullName = rs.getString("full_name");
                boolean isActive = rs.getBoolean("is_active");
                
                System.out.printf("  🔑 %s (%s) - %s\n", 
                    username, 
                    fullName, 
                    isActive ? "Active" : "Inactive"
                );
                hasUsers = true;
            }
            
            if (hasUsers) {
                System.out.println("✅ Authentication test ready!");
                System.out.println("💡 You can try logging in with: admin/password");
                return true;
            } else {
                System.out.println("❌ No active users found for authentication test!");
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
        System.out.println("🧪 Running comprehensive database tests...");
        System.out.println("================================================");
        
        boolean connectionTest = testConnection();
        System.out.println();
        
        boolean structureTest = testDatabaseStructure();
        System.out.println();
        
        boolean dataTest = testSampleData();
        System.out.println();
        
        boolean authTest = testAuthentication();
        System.out.println();
        
        System.out.println("================================================");
        System.out.println("📋 TEST RESULTS SUMMARY:");
        System.out.printf("  %s Database Connection\n", connectionTest ? "✅" : "❌");
        System.out.printf("  %s Database Structure\n", structureTest ? "✅" : "❌");
        System.out.printf("  %s Sample Data\n", dataTest ? "✅" : "⚠️");
        System.out.printf("  %s Authentication\n", authTest ? "✅" : "❌");
        
        boolean allPassed = connectionTest && structureTest && authTest;
        
        if (allPassed) {
            System.out.println("🎉 All critical tests passed! Database is ready to use.");
        } else {
            System.out.println("⚠️ Some tests failed. Please check database configuration and setup.");
        }
    }
    
    /**
     * Main method để chạy test standalone
     */
    public static void main(String[] args) {
        runAllTests();
    }
}