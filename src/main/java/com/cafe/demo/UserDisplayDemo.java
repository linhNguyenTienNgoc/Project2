package com.cafe.demo;

import com.cafe.model.entity.User;
import com.cafe.util.SessionManager;

/**
 * Demo class để test hiển thị tên người dùng trong dashboard
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class UserDisplayDemo {
    
    public static void main(String[] args) {
        System.out.println("🧪 TESTING USER DISPLAY IN DASHBOARD");
        System.out.println("====================================");
        
        // Test 1: Không có session
        System.out.println("\n📝 Test 1: No user session");
        System.out.println("Is logged in: " + SessionManager.isLoggedIn());
        System.out.println("Current user: " + SessionManager.getCurrentUserFullName());
        System.out.println("Current role: " + SessionManager.getCurrentUserRole());
        
        // Test 2: Tạo user và set session
        System.out.println("\n📝 Test 2: Set user session");
        User testUser = new User();
        testUser.setUserId(1);
        testUser.setUsername("admin");
        testUser.setFullName("Nguyễn Tiến Ngọc Linh");
        testUser.setRole("Admin");
        testUser.setEmail("admin@cafe.com");
        testUser.setPhone("0123456789");
        testUser.setActive(true);
        
        SessionManager.setCurrentUser(testUser);
        
        System.out.println("Is logged in: " + SessionManager.isLoggedIn());
        System.out.println("Current user: " + SessionManager.getCurrentUserFullName());
        System.out.println("Current role: " + SessionManager.getCurrentUserRole());
        System.out.println("Current username: " + SessionManager.getCurrentUsername());
        System.out.println("Current user ID: " + SessionManager.getCurrentUserId());
        
        // Test 3: Thay đổi user
        System.out.println("\n📝 Test 3: Change user session");
        User managerUser = new User();
        managerUser.setUserId(2);
        managerUser.setUsername("manager");
        managerUser.setFullName("Trần Xuân Quang Minh");
        managerUser.setRole("Manager");
        managerUser.setEmail("manager@cafe.com");
        managerUser.setPhone("0987654321");
        managerUser.setActive(true);
        
        SessionManager.setCurrentUser(managerUser);
        
        System.out.println("Current user: " + SessionManager.getCurrentUserFullName());
        System.out.println("Current role: " + SessionManager.getCurrentUserRole());
        
        // Test 4: Clear session
        System.out.println("\n📝 Test 4: Clear session");
        SessionManager.clearSession();
        
        System.out.println("Is logged in: " + SessionManager.isLoggedIn());
        System.out.println("Current user: " + SessionManager.getCurrentUserFullName());
        System.out.println("Current role: " + SessionManager.getCurrentUserRole());
        
        System.out.println("\n✅ All tests completed!");
        System.out.println("💡 Dashboard sẽ hiển thị tên thật của người dùng từ session!");
        
        System.out.println("\n🎯 EXPECTED BEHAVIOR:");
        System.out.println("- Dashboard sẽ hiển thị: 'Nguyễn Tiến Ngọc Linh' thay vì 'Admin User'");
        System.out.println("- AdminDashboard sẽ hiển thị: 'Trần Xuân Quang Minh' thay vì 'Admin User'");
        System.out.println("- Role sẽ hiển thị: 'Admin', 'Manager', etc. thay vì text cố định");
    }
}
