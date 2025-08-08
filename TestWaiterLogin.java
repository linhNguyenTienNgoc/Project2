import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.sql.Connection;

public class TestWaiterLogin {
    public static void main(String[] args) {
        try {
            // Kết nối database
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            Connection connection = DatabaseConfig.getConnection();
            UserDAO userDAO = new UserDAOImpl(connection);
            
            System.out.println("=== Testing Waiter Login ===");
            
            // Test 1: Tìm user waiter1
            System.out.println("\n1. Tìm user waiter1:");
            var waiter1Optional = userDAO.findByUsername("waiter1");
            if (waiter1Optional.isPresent()) {
                User waiter1 = waiter1Optional.get();
                System.out.println("✅ Found waiter1:");
                System.out.println("   - User ID: " + waiter1.getUserId());
                System.out.println("   - Username: " + waiter1.getUsername());
                System.out.println("   - Full Name: " + waiter1.getFullName());
                System.out.println("   - Role ID: " + waiter1.getRoleId());
                System.out.println("   - Role Name: " + waiter1.getRole().getRoleName());
                System.out.println("   - Is Active: " + waiter1.getIsActive());
                System.out.println("   - Password Hash: " + waiter1.getPassword());
            } else {
                System.out.println("❌ waiter1 not found!");
            }
            
            // Test 2: Kiểm tra mật khẩu
            System.out.println("\n2. Kiểm tra mật khẩu:");
            String testPassword = "password"; // Mật khẩu thực tế trong database
            String storedHash = "$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi";
            
            boolean passwordValid = BCrypt.verifyer().verify(testPassword.toCharArray(), storedHash).verified;
            System.out.println("   - Test password: " + testPassword);
            System.out.println("   - Stored hash: " + storedHash);
            System.out.println("   - Password valid: " + passwordValid);
            
            // Test 3: Thử đăng nhập với password
            System.out.println("\n3. Thử đăng nhập với password:");
            if (waiter1Optional.isPresent()) {
                User waiter1 = waiter1Optional.get();
                String inputPassword = "password";
                String storedPassword = waiter1.getPassword();
                
                boolean loginSuccess = false;
                if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
                    loginSuccess = BCrypt.verifyer().verify(inputPassword.toCharArray(), storedPassword).verified;
                } else {
                    loginSuccess = inputPassword.equals(storedPassword);
                }
                
                System.out.println("   - Input password: " + inputPassword);
                System.out.println("   - Login success: " + loginSuccess);
                
                if (loginSuccess) {
                    System.out.println("   - Role: " + waiter1.getRole().getRoleName());
                    System.out.println("   - Should go to: " + ("admin".equalsIgnoreCase(waiter1.getRole().getRoleName()) ? "Admin Dashboard" : "User Dashboard"));
                }
            }
            
            // Test 4: Thử đăng nhập với 123456
            System.out.println("\n4. Thử đăng nhập với 123456:");
            if (waiter1Optional.isPresent()) {
                User waiter1 = waiter1Optional.get();
                String inputPassword = "123456";
                String storedPassword = waiter1.getPassword();
                
                boolean loginSuccess = false;
                if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
                    loginSuccess = BCrypt.verifyer().verify(inputPassword.toCharArray(), storedPassword).verified;
                } else {
                    loginSuccess = inputPassword.equals(storedPassword);
                }
                
                System.out.println("   - Input password: " + inputPassword);
                System.out.println("   - Login success: " + loginSuccess);
            }
            
            connection.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
