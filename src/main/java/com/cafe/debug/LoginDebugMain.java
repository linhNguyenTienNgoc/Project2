package com.cafe.debug;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Debug class để kiểm tra login issues
 */
public class LoginDebugMain {
    
    public static void main(String[] args) {
        System.out.println("🔍 DEBUGGING LOGIN PROCESS...\n");
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            System.out.println("✅ Database connected successfully\n");
            
            // 1. Kiểm tra all users
            checkAllUsers(connection);
            
            // 2. Test authentication với admin
            testAuthentication(connection, "admin", "123456");
            
            // 3. Test authentication với waiter1
            testAuthentication(connection, "waiter1", "123456");
            
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void checkAllUsers(Connection connection) throws Exception {
        System.out.println("📋 ALL USERS IN DATABASE:");
        System.out.println("=" .repeat(80));
        
        String sql = "SELECT user_id, username, password, full_name, role, is_active FROM users ORDER BY user_id";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String password = rs.getString("password");
                String passwordType = password.startsWith("$2") ? "BCrypt Hash" : "Plaintext";
                
                System.out.printf("ID: %d | Username: %s | Password: %s | Type: %s | Role: %s | Active: %s%n",
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    password.length() > 20 ? password.substring(0, 20) + "..." : password,
                    passwordType,
                    rs.getString("role"),
                    rs.getBoolean("is_active")
                );
            }
        }
        System.out.println();
    }
    
    private static void testAuthentication(Connection connection, String username, String password) {
        System.out.println("🔐 TESTING AUTHENTICATION: " + username + "/" + password);
        System.out.println("-".repeat(50));
        
        try {
            UserDAO userDAO = new UserDAOImpl(connection);
            User user = userDAO.getUserByUsername(username);
            
            if (user == null) {
                System.out.println("❌ User not found: " + username);
                return;
            }
            
            System.out.println("✅ User found:");
            System.out.println("   - ID: " + user.getUserId());
            System.out.println("   - Username: " + user.getUsername());
            System.out.println("   - Full Name: " + user.getFullName());
            System.out.println("   - Role: " + user.getRole());
            System.out.println("   - Active: " + user.isActive());
            System.out.println("   - Password: " + user.getPassword());
            
            if (!user.isActive()) {
                System.out.println("❌ User is not active");
                return;
            }
            
            String storedPassword = user.getPassword();
            boolean isAuthenticated = false;
            
            // Check password type and verify
            if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
                System.out.println("🔒 Password type: BCrypt Hash");
                isAuthenticated = BCrypt.verifyer().verify(password.toCharArray(), storedPassword).verified;
                System.out.println("   BCrypt verification: " + (isAuthenticated ? "SUCCESS" : "FAILED"));
            } else {
                System.out.println("🔓 Password type: Plaintext");
                isAuthenticated = password.equals(storedPassword);
                System.out.println("   Plain text comparison: " + (isAuthenticated ? "SUCCESS" : "FAILED"));
                System.out.println("   Expected: '" + password + "'");
                System.out.println("   Actual: '" + storedPassword + "'");
                System.out.println("   Equal: " + password.equals(storedPassword));
            }
            
            System.out.println("🎯 FINAL RESULT: " + (isAuthenticated ? "✅ AUTHENTICATION SUCCESS" : "❌ AUTHENTICATION FAILED"));
            
        } catch (Exception e) {
            System.err.println("❌ Error testing authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println();
    }
}
