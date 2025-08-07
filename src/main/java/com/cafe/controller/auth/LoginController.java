package com.cafe.controller.auth;

import com.cafe.CafeManagementApplication;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import com.cafe.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import at.favre.lib.crypto.bcrypt.BCrypt;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình đăng nhập
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class LoginController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Label errorLabel;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private Button exitButton;
    
    private UserDAO userDAO;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatabase();
        setupEventHandlers();
        loadSavedCredentials();
    }
    
    /**
     * Khởi tạo kết nối database
     */
    private void setupDatabase() {
        try {
            // Test database connection first
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            
            if (dbConfig.testConnection()) {
                Connection connection = DatabaseConfig.getConnection();
                userDAO = new UserDAOImpl(connection);
                
                System.out.println("✅ Database connection established successfully");
                System.out.println("🔗 " + dbConfig.getDatabaseInfo());
                
                // Optionally log pool info in debug mode
                if (DatabaseConfig.getPropertyAsBoolean("debug.enabled", false)) {
                    System.out.println("🏊 " + dbConfig.getPoolInfo());
                }
                
            } else {
                showError("Không thể kết nối đến cơ sở dữ liệu!\nVui lòng kiểm tra:\n" +
                         "1. MySQL Server đã chạy chưa?\n" +
                         "2. Database 'cafe_management' đã tồn tại chưa?\n" +
                         "3. Username/Password có đúng không?");
            }
            
        } catch (Exception e) {
            showError("Lỗi kết nối database: " + e.getMessage() + 
                     "\n\nVui lòng kiểm tra cấu hình trong file database_config.properties");
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập các event handlers
     */
    private void setupEventHandlers() {
        // Enter key để đăng nhập
        usernameField.setOnKeyPressed(this::handleKeyPressed);
        passwordField.setOnKeyPressed(this::handleKeyPressed);
        
        // Clear error khi user typing
        usernameField.textProperty().addListener((obs, oldText, newText) -> clearError());
        passwordField.textProperty().addListener((obs, oldText, newText) -> clearError());
    }
    
    /**
     * Xử lý sự kiện nhấn phím
     */
    private void handleKeyPressed(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin(null);
        }
    }
    
    /**
     * Tải thông tin đăng nhập đã lưu
     */
    private void loadSavedCredentials() {
        // TODO: Implement remember me functionality
        // For now, set some default values for testing
        usernameField.setText("");
        passwordField.setText("");
    }
    
    /**
     * Xử lý đăng nhập
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        
        // Validation
        if (!validateInput(username, password)) {
            return;
        }
        
        // Disable login button to prevent double clicks
                    loginButton.setDisable(true);
        
        try {
            // Authenticate user
            User user = authenticateUser(username, password);
            
            if (user != null) {
                // Save session
                SessionManager.setCurrentUser(user);
                
                // Save credentials if remember me is checked
                if (rememberMeCheckBox.isSelected()) {
                    saveCredentials(username);
                }
                
                showSuccess("Đăng nhập thành công! Chào mừng " + user.getFullName());
                
                // Navigate based on user role
                if ("admin".equalsIgnoreCase(user.getRole().getRoleName())) {
                    CafeManagementApplication.showAdminDashboard();
                } else {
                    CafeManagementApplication.showDashboard();
                }
                
            } else {
                showError("Tên đăng nhập hoặc mật khẩu không đúng!");
            }
            
        } catch (Exception e) {
            showError("Lỗi trong quá trình đăng nhập: " + e.getMessage());
            e.printStackTrace();
        } finally {
            loginButton.setDisable(false);
        }
    }
    
    /**
     * Xác thực người dùng
     */
    private User authenticateUser(String username, String password) {
        try {
            // Sử dụng findByUsername thay vì getUserByUsername
            java.util.Optional<User> userOptional = userDAO.findByUsername(username);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                if (user.getIsActive()) {
                    String storedPassword = user.getPassword();
                    
                    // Check if password is BCrypt hashed (starts with $2a$ or $2b$)
                    if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
                        // Verify BCrypt password
                        if (BCrypt.verifyer().verify(password.toCharArray(), storedPassword).verified) {
                            return user;
                        }
                    } else {
                        // Plain text password (for development/testing)
                        if (password.equals(storedPassword)) {
                            return user;
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Validate input fields
     */
    private boolean validateInput(String username, String password) {
        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            usernameField.requestFocus();
            return false;
        }
        
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            passwordField.requestFocus();
            return false;
        }
        
        if (username.length() < 3) {
            showError("Tên đăng nhập phải có ít nhất 3 ký tự!");
            usernameField.requestFocus();
            return false;
        }
        
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự!");
            passwordField.requestFocus();
            return false;
        }
        
        return true;
    }
    
    /**
     * Lưu thông tin đăng nhập
     */
    private void saveCredentials(String username) {
        // TODO: Implement save credentials functionality
        System.out.println("💾 Saving credentials for: " + username);
    }
    
    /**
     * Hiển thị lỗi
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #dc3545;");
        errorLabel.setVisible(true);
    }
    
    /**
     * Hiển thị thành công
     */
    private void showSuccess(String message) {
        errorLabel.setText(message);
        errorLabel.setStyle("-fx-text-fill: #28a745;");
        errorLabel.setVisible(true);
    }
    
    /**
     * Xóa thông báo lỗi
     */
    private void clearError() {
        errorLabel.setVisible(false);
    }
    
    /**
     * Xử lý thoát ứng dụng
     */
    @FXML
    private void handleExit(ActionEvent event) {
        CafeManagementApplication.exitApplication();
    }
    
    /**
     * Xử lý quên mật khẩu
     */
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        CafeManagementApplication.showSuccessAlert(
            "Quên mật khẩu", 
            "Vui lòng liên hệ quản trị viên để đặt lại mật khẩu!"
        );
    }
}