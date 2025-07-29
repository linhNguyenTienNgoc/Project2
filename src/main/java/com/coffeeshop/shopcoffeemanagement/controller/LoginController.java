package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import com.coffeeshop.shopcoffeemanagement.service.EmployeeService;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class LoginController {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Label errorLabel;
    
    private EmployeeService employeeService;
    
    @FXML
    public void initialize() {
        // Khởi tạo service khi cần thiết
        
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
            
            // Khởi tạo service khi cần thiết
            if (employeeService == null) {
                employeeService = new EmployeeService();
            }
            
            // Thực hiện đăng nhập
            Employee employee = employeeService.authenticate(username, password);
            
            if (employee != null) {
                // Đăng nhập thành công
                CoffeeShopApplication.setCurrentUser(employee);
                CoffeeShopApplication.showMainWindow();
            } else {
                // Đăng nhập thất bại
                showError("Tên đăng nhập hoặc mật khẩu không đúng");
                passwordField.clear();
                passwordField.requestFocus();
            }
            
        } catch (Exception e) {
            showError("Lỗi kết nối: " + e.getMessage());
        } finally {
            loginButton.setDisable(false);
        }
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