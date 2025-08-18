package com.cafe.util;

import com.cafe.config.DatabaseConfig;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Utility để cập nhật passwords trong database
 */
public class PasswordUpdater {
    
    public static void main(String[] args) {
        System.out.println("🔧 UPDATING USER PASSWORDS...\n");
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("✅ Database connected successfully\n");
            
            // 1. Update passwords to plaintext for testing
            updatePasswords(connection);
            
            // 2. Verify the updates
            verifyPasswords(connection);
            
            System.out.println("\n🎉 PASSWORD UPDATE COMPLETED!");
            System.out.println("📝 You can now login with:");
            System.out.println("   admin / 123456");
            System.out.println("   waiter1 / 123456");
            System.out.println("   manager1 / 123456");
            System.out.println("   cashier1 / 123456");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void updatePasswords(Connection connection) throws Exception {
        System.out.println("🔄 Updating user passwords...");
        
        String sql = "UPDATE users SET password = '123456' WHERE username IN ('admin', 'waiter1', 'manager1', 'cashier1')";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int updated = stmt.executeUpdate();
            System.out.println("✅ Updated " + updated + " user passwords");
        }
    }
    
    private static void verifyPasswords(Connection connection) throws Exception {
        System.out.println("\n📋 VERIFYING PASSWORD UPDATES:");
        System.out.println("=" .repeat(80));
        
        String sql = "SELECT user_id, username, password, full_name, role, is_active, " +
                    "CASE WHEN password LIKE '$2%' THEN 'BCrypt Hash' ELSE 'Plaintext' END as password_type " +
                    "FROM users WHERE username IN ('admin', 'waiter1', 'manager1', 'cashier1') " +
                    "ORDER BY user_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                System.out.printf("ID: %d | Username: %-10s | Password: %-10s | Type: %-10s | Role: %-8s | Active: %s%n",
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("password_type"),
                    rs.getString("role"),
                    rs.getBoolean("is_active") ? "Yes" : "No"
                );
            }
        }
    }
}
