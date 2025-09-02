package com.cafe.controller.admin;

import com.cafe.model.entity.User;
import com.cafe.util.AlertUtils;
import com.cafe.util.PasswordUtil;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class UserFormDialogController implements Initializable {
    
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Label formTitleLabel;
    
    private User currentEditingUser;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupFormComponents();
    }
    
    private void setupFormComponents() {
        // Role combo for form
        roleCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "ADMIN", "STAFF"
        ));
        roleCombo.setValue("STAFF");

        // Status combo for form
        statusCombo.setItems(javafx.collections.FXCollections.observableArrayList(
            "ACTIVE", "INACTIVE"
        ));
        statusCombo.setValue("ACTIVE");
    }
    
    public void setCurrentEditingUser(User user) {
        this.currentEditingUser = user;
        updateFormTitle();
        if (user != null) {
            populateForm(user);
        }
    }
    
    private void updateFormTitle() {
        if (formTitleLabel != null) {
            formTitleLabel.setText(currentEditingUser == null ? "✨ Thêm nhân viên mới" : "✏️ Sửa thông tin nhân viên");
        }
    }
    
    private void populateForm(User user) {
        if (usernameField != null) usernameField.setText(user.getUsername());
        if (fullNameField != null) fullNameField.setText(user.getFullName());
        if (emailField != null) emailField.setText(user.getEmail());
        if (phoneField != null) phoneField.setText(user.getPhone());
        if (roleCombo != null) roleCombo.setValue(user.getRole());
        if (statusCombo != null) statusCombo.setValue(user.isActive() ? "ACTIVE" : "INACTIVE");
        
        // Clear password fields for editing
        if (passwordField != null) passwordField.clear();
        if (confirmPasswordField != null) confirmPasswordField.clear();
    }
    
    public User getFormData() {
        if (!validateForm()) {
            return null;
        }
        
        User user = currentEditingUser != null ? currentEditingUser : new User();
        
        user.setUsername(usernameField.getText().trim());
        user.setFullName(fullNameField.getText().trim());
        user.setEmail(emailField.getText().trim());
        user.setPhone(phoneField.getText().trim());
        
        // Ensure role is not null
        String role = roleCombo.getValue();
        if (role == null || role.trim().isEmpty()) {
            role = "STAFF"; // Default role
        }
        user.setRole(role);
        
        // Ensure status is not null
        String status = statusCombo.getValue();
        if (status == null || status.trim().isEmpty()) {
            status = "ACTIVE"; // Default status
        }
        user.setActive("ACTIVE".equals(status));

        // Set password only if provided
        if (passwordField != null && !passwordField.getText().isEmpty()) {
            user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
        }
        
        return user;
    }
    
    private boolean validateForm() {
        boolean isValid = true;

        if (usernameField == null || usernameField.getText().trim().isEmpty() || usernameField.getText().trim().length() < 3) {
            AlertUtils.showError("Lỗi", "Username phải có ít nhất 3 ký tự");
            isValid = false;
        }
        
        if (fullNameField == null || fullNameField.getText().trim().isEmpty()) {
            AlertUtils.showError("Lỗi", "Họ tên không được để trống");
            isValid = false;
        }
        
        if (emailField == null || emailField.getText().trim().isEmpty() || !emailField.getText().contains("@")) {
            AlertUtils.showError("Lỗi", "Email không hợp lệ");
            isValid = false;
        }
        
        if (currentEditingUser == null && (passwordField == null || passwordField.getText().isEmpty() || passwordField.getText().length() < 6)) {
            AlertUtils.showError("Lỗi", "Mật khẩu phải có ít nhất 6 ký tự");
            isValid = false;
        }
        
        if (currentEditingUser == null && (passwordField == null || confirmPasswordField == null || !passwordField.getText().equals(confirmPasswordField.getText()))) {
            AlertUtils.showError("Lỗi", "Mật khẩu xác nhận không khớp");
            isValid = false;
        }

        return isValid;
    }
}
