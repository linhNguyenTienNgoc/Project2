package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.DemoApplication;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class DemoLoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void initialize() {
        // Thêm event handler cho phím Enter
        passwordField.setOnKeyPressed(this::handleKeyPress);
        usernameField.setOnKeyPressed(this::handleKeyPress);
        
        // Focus vào username field khi khởi động
        usernameField.requestFocus();
    }
    
    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đầy đủ thông tin đăng nhập");
            return;
        }
        
        try {
            // Disable login button để tránh double click
            loginButton.setDisable(true);
            
            // Demo authentication
            Employee employee = authenticateDemo(username, password);
            
            if (employee != null) {
                // Đăng nhập thành công
                DemoApplication.setCurrentUser(employee);
                DemoApplication.showMainWindow();
            } else {
                // Đăng nhập thất bại
                showError("Tên đăng nhập hoặc mật khẩu không đúng");
                passwordField.clear();
                passwordField.requestFocus();
            }
            
        } catch (Exception e) {
            showError("Lỗi: " + e.getMessage());
        } finally {
            loginButton.setDisable(false);
        }
    }
    
    private Employee authenticateDemo(String username, String password) {
        // Demo authentication - không cần database
        if ("admin".equals(username) && "admin123".equals(password)) {
            Employee admin = new Employee("admin", "admin123", "Nguyễn Văn Admin", "admin@coffeeshop.com", "0901234567", "ADMIN");
            admin.setId(1L);
            return admin;
        } else if ("staff1".equals(username) && "staff123".equals(password)) {
            Employee staff = new Employee("staff1", "staff123", "Trần Thị Nhân Viên", "staff1@coffeeshop.com", "0901234568", "STAFF");
            staff.setId(2L);
            return staff;
        } else if ("manager".equals(username) && "manager123".equals(password)) {
            Employee manager = new Employee("manager", "manager123", "Phạm Thị Quản Lý", "manager@coffeeshop.com", "0901234570", "ADMIN");
            manager.setId(4L);
            return manager;
        }
        return null;
    }
    
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            handleLogin();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
} 