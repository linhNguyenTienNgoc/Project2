package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình quản lý người dùng
 * 
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced with Dashboard Communication)
 */
public class UserController implements Initializable, DashboardCommunicator {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilter;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Object> userTable;
    
    @FXML private TableColumn<Object, String> userIdColumn;
    @FXML private TableColumn<Object, String> usernameColumn;
    @FXML private TableColumn<Object, String> fullNameColumn;
    @FXML private TableColumn<Object, String> emailColumn;
    @FXML private TableColumn<Object, String> phoneColumn;
    @FXML private TableColumn<Object, String> roleColumn;
    @FXML private TableColumn<Object, String> statusColumn;
    @FXML private TableColumn<Object, String> createdDateColumn;
    @FXML private TableColumn<Object, String> actionColumn;
    
    private ObservableList<Object> userList = FXCollections.observableArrayList();
    
    // ✅ Dashboard communication
    private Object dashboardController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        loadSampleData();
        setupEventHandlers();
        
        System.out.println("✅ UserController initialized successfully");
    }
    
    /**
     * Setup table columns
     */
    private void setupTable() {
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdDateColumn.setCellValueFactory(new PropertyValueFactory<>("createdDate"));
        
        // Setup action column with buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final Button resetButton = new Button("Reset MK");
            private final HBox buttonBox = new HBox(3, editButton, deleteButton, resetButton);
            
            {
                editButton.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-font-size: 9px;");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 9px;");
                resetButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #212529; -fx-font-size: 9px;");
                
                editButton.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    editUser(item);
                });
                
                deleteButton.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    deleteUser(item);
                });
                
                resetButton.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    resetPassword(item);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
        
        userTable.setItems(userList);
    }
    
    /**
     * Setup filters
     */
    private void setupFilters() {
        roleFilter.getItems().addAll("Tất cả", "Admin", "Manager", "Cashier", "Waiter", "Barista");
        roleFilter.setValue("Tất cả");
        
        statusFilter.getItems().addAll("Tất cả", "Đang hoạt động", "Đã khóa");
        statusFilter.setValue("Tất cả");
    }
    
    /**
     * Load sample data
     */
    private void loadSampleData() {
        // Add sample user data
        userList.add(new UserData("1", "admin", "Nguyễn Tiến Ngọc Linh", "admin@cafe.com", "0123456789", "Admin", "Đang hoạt động", "01/01/2024"));
        userList.add(new UserData("2", "manager", "Trần Xuân Quang Minh", "manager@cafe.com", "0123456790", "Manager", "Đang hoạt động", "02/01/2024"));
        userList.add(new UserData("3", "cashier1", "Vũ Hoàng Nam", "cashier1@cafe.com", "0123456791", "Cashier", "Đang hoạt động", "03/01/2024"));
        userList.add(new UserData("4", "waiter1", "Dương Tuấn Minh", "waiter1@cafe.com", "0123456792", "Waiter", "Đang hoạt động", "04/01/2024"));
        userList.add(new UserData("5", "barista1", "Nguyễn Thị Nguyệt Nhi", "barista1@cafe.com", "0123456793", "Barista", "Đang hoạt động", "05/01/2024"));
        userList.add(new UserData("6", "cashier2", "Lê Văn An", "cashier2@cafe.com", "0123456794", "Cashier", "Đã khóa", "06/01/2024"));
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterUsers();
        });
        
        roleFilter.setOnAction(e -> {
            filterUsers();
        });
        
        statusFilter.setOnAction(e -> {
            filterUsers();
        });
    }
    
    /**
     * Filter users based on search text, role and status
     */
    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();
        String selectedStatus = statusFilter.getValue();
        
        // TODO: Implement actual filtering logic
        System.out.println("Filtering users: " + searchText + ", Role: " + selectedRole + ", Status: " + selectedStatus);
    }
    
    /**
     * Edit user
     */
    private void editUser(Object user) {
        // TODO: Implement edit user dialog
        System.out.println("Edit user: " + user);
    }
    
    /**
     * Delete user
     */
    private void deleteUser(Object user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa người dùng này không?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                userList.remove(user);
                System.out.println("Deleted user: " + user);
            }
        });
    }
    
    /**
     * Reset password
     */
    private void resetPassword(Object user) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận reset mật khẩu");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn reset mật khẩu của người dùng này không?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // TODO: Implement password reset logic
                System.out.println("Reset password for user: " + user);
                
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Thành công");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Mật khẩu đã được reset thành công!");
                successAlert.showAndWait();
            }
        });
    }
    
    // =====================================================
    // DASHBOARD COMMUNICATION IMPLEMENTATION
    // =====================================================
    
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ UserController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
    
    /**
     * Sample data class for users
     */
    public static class UserData {
        private String userId, username, fullName, email, phone, role, status, createdDate;
        
        public UserData(String userId, String username, String fullName, String email, 
                       String phone, String role, String status, String createdDate) {
            this.userId = userId;
            this.username = username;
            this.fullName = fullName;
            this.email = email;
            this.phone = phone;
            this.role = role;
            this.status = status;
            this.createdDate = createdDate;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public String getUsername() { return username; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getPhone() { return phone; }
        public String getRole() { return role; }
        public String getStatus() { return status; }
        public String getCreatedDate() { return createdDate; }
    }
}
