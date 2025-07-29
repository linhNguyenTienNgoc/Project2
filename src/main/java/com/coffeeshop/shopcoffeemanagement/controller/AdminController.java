package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.dao.EmployeeDAO;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.List;
import java.util.Optional;

public class AdminController {
    
    @FXML
    private TableView<Employee> staffTable;
    
    @FXML
    private TableColumn<Employee, String> usernameColumn;
    
    @FXML
    private TableColumn<Employee, String> fullNameColumn;
    
    @FXML
    private TableColumn<Employee, String> roleColumn;
    
    @FXML
    private TableColumn<Employee, String> statusColumn;
    
    @FXML
    private Button addStaffButton;
    
    @FXML
    private Button editStaffButton;
    
    @FXML
    private Button deleteStaffButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TextField searchField;
    
    private EmployeeDAO employeeDAO;
    private List<Employee> allEmployees;
    
    @FXML
    public void initialize() {
        employeeDAO = new EmployeeDAO();
        setupTable();
        setupSearch();
        loadStaffData();
    }
    
    private void setupTable() {
        // Setup columns
        usernameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUsername()));
        
        fullNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFullName()));
        
        roleColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRole()));
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().isActive() ? "Hoạt động" : "Không hoạt động"));
        
        // Setup table selection
        staffTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            editStaffButton.setDisable(newValue == null);
            deleteStaffButton.setDisable(newValue == null);
        });
        
        // Double click to edit
        staffTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                editSelectedStaff();
            }
        });
    }
    
    private void setupSearch() {
        searchField.setPromptText("Tìm kiếm nhân viên...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterStaff();
        });
    }
    
    private void loadStaffData() {
        try {
            allEmployees = employeeDAO.findAll();
            staffTable.getItems().setAll(allEmployees);
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách nhân viên: " + e.getMessage());
        }
    }
    
    private void filterStaff() {
        String searchText = searchField.getText().toLowerCase();
        List<Employee> filteredEmployees = allEmployees.stream()
            .filter(employee -> 
                employee.getUsername().toLowerCase().contains(searchText) ||
                employee.getFullName().toLowerCase().contains(searchText) ||
                employee.getRole().toLowerCase().contains(searchText))
            .toList();
        
        staffTable.getItems().setAll(filteredEmployees);
    }
    
    @FXML
    private void addNewStaff() {
        showStaffDialog(null);
    }
    
    @FXML
    private void editSelectedStaff() {
        Employee selectedEmployee = staffTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            showStaffDialog(selectedEmployee);
        }
    }
    
    @FXML
    private void deleteSelectedStaff() {
        Employee selectedEmployee = staffTable.getSelectionModel().getSelectedItem();
        if (selectedEmployee != null) {
            CoffeeShopApplication.showConfirmation("Xóa nhân viên", 
                "Bạn có chắc chắn muốn xóa nhân viên '" + selectedEmployee.getFullName() + "' không?\n" +
                "Hành động này không thể hoàn tác.", 
                () -> {
                    try {
                        if (employeeDAO.delete(selectedEmployee.getId())) {
                            loadStaffData();
                            CoffeeShopApplication.showInfo("Thành công", "Đã xóa nhân viên " + selectedEmployee.getFullName());
                        } else {
                            CoffeeShopApplication.showError("Lỗi", "Không thể xóa nhân viên");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        CoffeeShopApplication.showError("Lỗi", "Không thể xóa nhân viên: " + e.getMessage());
                    }
                });
        }
    }
    
    @FXML
    private void refreshStaffData() {
        loadStaffData();
        CoffeeShopApplication.showInfo("Thành công", "Đã làm mới danh sách nhân viên");
    }
    
    private void showStaffDialog(Employee employee) {
        Dialog<Employee> dialog = new Dialog<>();
        dialog.setTitle(employee == null ? "Thêm nhân viên mới" : "Chỉnh sửa nhân viên");
        dialog.setHeaderText(employee == null ? "Nhập thông tin nhân viên mới" : "Chỉnh sửa thông tin nhân viên");
        
        // Setup dialog content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Text titleText = new Text(employee == null ? "➕ THÊM NHÂN VIÊN MỚI" : "✏️ CHỈNH SỬA NHÂN VIÊN");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Tên đăng nhập");
        usernameField.setPrefWidth(300);
        usernameField.setDisable(employee != null); // Không cho phép sửa username
        
        TextField fullNameField = new TextField();
        fullNameField.setPromptText("Họ và tên");
        fullNameField.setPrefWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(employee == null ? "Mật khẩu" : "Mật khẩu mới (để trống nếu không đổi)");
        passwordField.setPrefWidth(300);
        
        ComboBox<String> roleComboBox = new ComboBox<>();
        roleComboBox.getItems().addAll("STAFF", "ADMIN");
        roleComboBox.setPromptText("Chọn vai trò");
        roleComboBox.setPrefWidth(300);
        
        CheckBox activeCheckBox = new CheckBox("Tài khoản hoạt động");
        activeCheckBox.setSelected(true);
        
        // Set current values if editing
        if (employee != null) {
            usernameField.setText(employee.getUsername());
            fullNameField.setText(employee.getFullName());
            roleComboBox.setValue(employee.getRole());
            activeCheckBox.setSelected(employee.isActive());
        }
        
        content.getChildren().addAll(titleText, usernameField, fullNameField, passwordField, roleComboBox, activeCheckBox);
        dialog.getDialogPane().setContent(content);
        
        // Setup buttons
        ButtonType saveButtonType = new ButtonType("💾 Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("❌ Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Validation
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Enable save button only when required fields are filled
        javafx.beans.value.ChangeListener<String> validationListener = (observable, oldValue, newValue) -> {
            boolean isValid = !usernameField.getText().trim().isEmpty() &&
                            !fullNameField.getText().trim().isEmpty() &&
                            roleComboBox.getValue() != null &&
                            (employee != null || !passwordField.getText().trim().isEmpty());
            saveButton.setDisable(!isValid);
        };
        
        usernameField.textProperty().addListener(validationListener);
        fullNameField.textProperty().addListener(validationListener);
        passwordField.textProperty().addListener(validationListener);
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validationListener.changed(null, null, null));
        
        // Handle result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Employee newEmployee = employee != null ? employee : new Employee();
                    newEmployee.setUsername(usernameField.getText().trim());
                    newEmployee.setFullName(fullNameField.getText().trim());
                    newEmployee.setRole(roleComboBox.getValue());
                    newEmployee.setActive(activeCheckBox.isSelected());
                    
                    // Only update password if provided
                    if (!passwordField.getText().trim().isEmpty()) {
                        newEmployee.setPassword(passwordField.getText().trim());
                    }
                    
                    boolean success;
                    if (employee == null) {
                        // Create new employee
                        success = employeeDAO.save(newEmployee);
                    } else {
                        // Update existing employee
                        success = employeeDAO.update(newEmployee);
                    }
                    
                    if (success) {
                        loadStaffData();
                        CoffeeShopApplication.showInfo("Thành công", 
                            employee == null ? "Đã thêm nhân viên mới" : "Đã cập nhật thông tin nhân viên");
                        return newEmployee;
                    } else {
                        CoffeeShopApplication.showError("Lỗi", "Không thể lưu thông tin nhân viên");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoffeeShopApplication.showError("Lỗi", "Không thể lưu thông tin nhân viên: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
} 