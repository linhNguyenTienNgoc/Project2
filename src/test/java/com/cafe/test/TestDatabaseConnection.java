package com.cafe.test;

import com.cafe.util.DatabaseTestUtil;

/**
 * Test class để kiểm tra database connection trước khi chạy ứng dụng
 * 
 * Chạy class này để đảm bảo database đã được setup đúng cách
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class TestDatabaseConnection {
    
    public static void main(String[] args) {
        System.out.println("☕ CAFE MANAGEMENT SYSTEM - DATABASE TEST");
        System.out.println("==========================================");
        
        try {
            // Chạy tất cả các test database
            DatabaseTestUtil.runAllTests();
            
        } catch (Exception e) {
            System.err.println("💥 Fatal error during database testing:");
            e.printStackTrace();
        }
        
        System.out.println();
        System.out.println("💡 NEXT STEPS:");
        System.out.println("- If all tests passed: You can run the main application");
        System.out.println("- If tests failed: Please check database setup and configuration");
        System.out.println("- Need sample data: Import database/cafe_management.sql");
    }
}