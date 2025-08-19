package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import com.cafe.util.AlertUtils;
import com.cafe.util.PasswordUtil;
import com.cafe.util.ValidationUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller hoàn chỉnh cho quản lý người dùng trong Admin Dashboard
 * 
 * @author Team 2_C2406L
 * @version 2.0.0
 */
public class AdminUserController implements Initializable, DashboardCommunicator {

    // =====================================================
    // FXML COMPONENTS - Search and Filter
    // =====================================================
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Button addUserButton;
    @FXML private Button exportButton;

    // =====================================================
    // FXML COMPONENTS - User Table
    // =====================================================
    
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    // =====================================================
    // FXML COMPONENTS - Statistics
    // =====================================================
    
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label adminUsersLabel;
    @FXML private Label staffUsersLabel;

    // =====================================================
    // FXML COMPONENTS - Form Section
    // =====================================================
    
    @FXML private VBox userFormSection;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button saveUserButton;
    @FXML private Button cancelButton;
    @FXML private Button resetFormButton;

    // =====================================================
    // STATE MANAGEMENT
    // =====================================================
    
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private ObservableList<User> filteredUserList = FXCollections.observableArrayList();
    private Object dashboardController;
    private User currentEditingUser = null;

    // =====================================================
    // INITIALIZATION
    // =====================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupUserTable();
            setupFilters();
            setupForm();
            setupEventHandlers();
            setupValidation();
            
            // Load initial data
            loadUsers();
            updateStatistics();

            System.out.println("✅ AdminUserController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing AdminUserController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo giao diện quản lý người dùng: " + e.getMessage());
        }
    }

    // =====================================================
    // SETUP METHODS
    // =====================================================

    private void setupUserTable() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Status column with custom cell factory
        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            return new javafx.beans.property.SimpleStringProperty(isActive ? "ACTIVE" : "INACTIVE");
        });
        
        // Created at column - placeholder
        createdAtColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("N/A"));

        // Setup actions column
        setupActionsColumn();

        // Set data source
        userTable.setItems(filteredUserList);

        // Selection handler
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });

        // Double-click to edit
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    editUser(row.getItem());
                }
            });
            return row;
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final HBox actionBox = new HBox(5);
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");
            private final Button resetPasswordButton = new Button("🔑");

            {
                // Style buttons
                editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5;");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5;");
                resetPasswordButton.setStyle("-fx-background-color: #17a2b8; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 5;");

                // Tooltips
                editButton.setTooltip(new Tooltip("Chỉnh sửa người dùng"));
                deleteButton.setTooltip(new Tooltip("Xóa người dùng"));
                resetPasswordButton.setTooltip(new Tooltip("Reset mật khẩu"));

                // Event handlers
                editButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    editUser(user);
                });

                deleteButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    deleteUser(user);
                });

                resetPasswordButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    resetPassword(user);
                });

                actionBox.getChildren().addAll(editButton, deleteButton, resetPasswordButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void setupFilters() {
        // Role filter
        roleFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ADMIN", "MANAGER", "STAFF", "CASHIER", "WAITER", "BARISTA"
        ));
        roleFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ACTIVE", "INACTIVE"
        ));
        statusFilterCombo.setValue("Tất cả");
    }

    private void setupForm() {
        // Role combo for form
        roleCombo.setItems(FXCollections.observableArrayList(
            "ADMIN", "MANAGER", "STAFF", "CASHIER", "WAITER", "BARISTA"
        ));
        roleCombo.setValue("STAFF");

        // Status combo for form
        statusCombo.setItems(FXCollections.observableArrayList(
            "ACTIVE", "INACTIVE"
        ));
        statusCombo.setValue("ACTIVE");

        // Initially hide form
        userFormSection.setVisible(false);
        userFormSection.setManaged(false);
    }

    private void setupEventHandlers() {
        // Search and filter handlers
        searchField.textProperty().addListener((obs, oldText, newText) -> filterUsers());
        roleFilterCombo.setOnAction(e -> filterUsers());
        statusFilterCombo.setOnAction(e -> filterUsers());

        // Button handlers
        refreshButton.setOnAction(e -> loadUsers());
        addUserButton.setOnAction(e -> showAddUserForm());
        exportButton.setOnAction(e -> exportUsers());

        // Form button handlers
        saveUserButton.setOnAction(e -> saveUser());
        cancelButton.setOnAction(e -> hideUserForm());
        resetFormButton.setOnAction(e -> resetForm());
    }

    private void setupValidation() {
        // Real-time validation for form fields
        usernameField.textProperty().addListener((obs, oldText, newText) -> validateUsername());
        emailField.textProperty().addListener((obs, oldText, newText) -> validateEmail());
        phoneField.textProperty().addListener((obs, oldText, newText) -> validatePhone());
        passwordField.textProperty().addListener((obs, oldText, newText) -> validatePassword());
        confirmPasswordField.textProperty().addListener((obs, oldText, newText) -> validateConfirmPassword());
    }

    // =====================================================
    // DATA OPERATIONS
    // =====================================================

    private void loadUsers() {
        Task<List<User>> loadTask = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    UserDAO dao = new UserDAOImpl(connection);
                    return dao.getAllUsers();
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    userList.clear();
                    if (getValue() != null) {
                        userList.addAll(getValue());
                    }
                    filterUsers(); // Apply current filters
                    updateStatistics();
                    System.out.println("✅ Loaded " + userList.size() + " users");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Failed to load users: " + getException().getMessage());
                    AlertUtils.showError("Lỗi", "Không thể tải danh sách người dùng: " + getException().getMessage());
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase().trim();
        String roleFilter = roleFilterCombo.getValue();
        String statusFilter = statusFilterCombo.getValue();

        filteredUserList.clear();

        for (User user : userList) {
            // Search filter
            boolean matchesSearch = searchText.isEmpty() ||
                user.getUsername().toLowerCase().contains(searchText) ||
                user.getFullName().toLowerCase().contains(searchText) ||
                (user.getEmail() != null && user.getEmail().toLowerCase().contains(searchText));

            // Role filter
            boolean matchesRole = "Tất cả".equals(roleFilter) || 
                user.getRole().equals(roleFilter);

            // Status filter
            boolean matchesStatus = "Tất cả".equals(statusFilter) || 
                (user.isActive() && "ACTIVE".equals(statusFilter)) ||
                (!user.isActive() && "INACTIVE".equals(statusFilter));

            if (matchesSearch && matchesRole && matchesStatus) {
                filteredUserList.add(user);
            }
        }
    }

    private void updateStatistics() {
        int total = userList.size();
        long active = userList.stream().filter(User::isActive).count();
        long admins = userList.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long staff = userList.stream().filter(u -> "STAFF".equals(u.getRole())).count();

        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        adminUsersLabel.setText(String.valueOf(admins));
        staffUsersLabel.setText(String.valueOf(staff));
    }

    // =====================================================
    // FORM OPERATIONS
    // =====================================================

    private void showAddUserForm() {
        currentEditingUser = null;
        resetForm();
        userFormSection.setVisible(true);
        userFormSection.setManaged(true);
        usernameField.requestFocus();
    }

    private void hideUserForm() {
        userFormSection.setVisible(false);
        userFormSection.setManaged(false);
        currentEditingUser = null;
        resetForm();
    }

    private void resetForm() {
        usernameField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleCombo.setValue("STAFF");
        statusCombo.setValue("ACTIVE");
        
        clearValidationStyles();
    }

    private void populateForm(User user) {
        currentEditingUser = user;
        usernameField.setText(user.getUsername());
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        roleCombo.setValue(user.getRole());
        statusCombo.setValue(user.isActive() ? "ACTIVE" : "INACTIVE");
        
        // Clear password fields for editing
        passwordField.clear();
        confirmPasswordField.clear();
        
        userFormSection.setVisible(true);
        userFormSection.setManaged(true);
    }

    // =====================================================
    // USER ACTIONS
    // =====================================================

    private void editUser(User user) {
        populateForm(user);
    }

    private void deleteUser(User user) {
        boolean confirmed = AlertUtils.showConfirmation(
            "Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa người dùng: " + user.getFullName() + " (" + user.getUsername() + ")?"
        );

        if (confirmed) {
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try (Connection connection = DatabaseConfig.getConnection()) {
                        UserDAO dao = new UserDAOImpl(connection);
                        return dao.deleteUser(user.getUserId());
                    }
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        if (getValue()) {
                            userList.remove(user);
                            filterUsers();
                            updateStatistics();
                            AlertUtils.showInfo("Thành công", "Đã xóa người dùng thành công");
                        } else {
                            AlertUtils.showError("Lỗi", "Không thể xóa người dùng");
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi xóa người dùng: " + getException().getMessage());
                    });
                }
            };

            new Thread(deleteTask).start();
        }
    }

    private void resetPassword(User user) {
        String newPassword = AlertUtils.showTextInput(
            "Reset mật khẩu", 
            "Nhập mật khẩu mới cho " + user.getFullName(), 
            "Mật khẩu mới:"
        );

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (ValidationUtils.isValidPassword(newPassword)) {
                Task<Boolean> resetTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String hashedPassword = PasswordUtil.hashPassword(newPassword);
                        try (Connection connection = DatabaseConfig.getConnection()) {
                            UserDAO dao = new UserDAOImpl(connection);
                            return dao.updatePassword(user.getUserId(), hashedPassword);
                        }
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            if (getValue()) {
                                AlertUtils.showInfo("Thành công", "Đã reset mật khẩu cho " + user.getFullName());
                            } else {
                                AlertUtils.showError("Lỗi", "Không thể reset mật khẩu");
                            }
                        });
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                            AlertUtils.showError("Lỗi", "Lỗi khi reset mật khẩu: " + getException().getMessage());
                        });
                    }
                };

                new Thread(resetTask).start();
            } else {
                AlertUtils.showError("Lỗi", "Mật khẩu không hợp lệ. Mật khẩu phải có ít nhất 6 ký tự.");
            }
        }
    }

    private void saveUser() {
        if (!validateForm()) {
            return;
        }

        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                User user = currentEditingUser != null ? currentEditingUser : new User();
                
                user.setUsername(usernameField.getText().trim());
                user.setFullName(fullNameField.getText().trim());
                user.setEmail(emailField.getText().trim());
                user.setPhone(phoneField.getText().trim());
                user.setRole(roleCombo.getValue());
                user.setActive("ACTIVE".equals(statusCombo.getValue()));

                // Set password only if provided
                if (!passwordField.getText().isEmpty()) {
                    user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
                }

                try (Connection connection = DatabaseConfig.getConnection()) {
                    UserDAO dao = new UserDAOImpl(connection);
                    
                    if (currentEditingUser == null) {
                        return dao.insertUser(user);
                    } else {
                        return dao.updateUser(user);
                    }
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        AlertUtils.showInfo("Thành công", 
                            currentEditingUser == null ? "Đã thêm người dùng mới" : "Đã cập nhật người dùng");
                        hideUserForm();
                        loadUsers();
                    } else {
                        AlertUtils.showError("Lỗi", "Không thể lưu thông tin người dùng");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Lỗi khi lưu người dùng: " + getException().getMessage());
                });
            }
        };

        new Thread(saveTask).start();
    }

    private void exportUsers() {
        AlertUtils.showInfo("Thông báo", "Tính năng xuất dữ liệu sẽ được cập nhật sau");
    }

    // =====================================================
    // VALIDATION METHODS
    // =====================================================

    private boolean validateForm() {
        boolean isValid = true;

        if (!validateUsername()) isValid = false;
        if (!validateEmail()) isValid = false;
        if (!validatePhone()) isValid = false;
        if (currentEditingUser == null && !validatePassword()) isValid = false;
        if (currentEditingUser == null && !validateConfirmPassword()) isValid = false;

        return isValid;
    }

    private boolean validateUsername() {
        String username = usernameField.getText().trim();
        if (username.isEmpty() || username.length() < 3) {
            setFieldError(usernameField, "Tên đăng nhập phải có ít nhất 3 ký tự");
            return false;
        }
        setFieldSuccess(usernameField);
        return true;
    }

    private boolean validateEmail() {
        String email = emailField.getText().trim();
        if (!ValidationUtils.isValidEmail(email)) {
            setFieldError(emailField, "Email không hợp lệ");
            return false;
        }
        setFieldSuccess(emailField);
        return true;
    }

    private boolean validatePhone() {
        String phone = phoneField.getText().trim();
        if (!ValidationUtils.isValidPhone(phone)) {
            setFieldError(phoneField, "Số điện thoại không hợp lệ");
            return false;
        }
        setFieldSuccess(phoneField);
        return true;
    }

    private boolean validatePassword() {
        String password = passwordField.getText();
        if (currentEditingUser == null && (password.isEmpty() || password.length() < 6)) {
            setFieldError(passwordField, "Mật khẩu phải có ít nhất 6 ký tự");
            return false;
        }
        setFieldSuccess(passwordField);
        return true;
    }

    private boolean validateConfirmPassword() {
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        if (currentEditingUser == null && !password.equals(confirmPassword)) {
            setFieldError(confirmPasswordField, "Mật khẩu xác nhận không khớp");
            return false;
        }
        setFieldSuccess(confirmPasswordField);
        return true;
    }

    private void setFieldError(TextField field, String message) {
        field.getStyleClass().removeAll("field-success");
        field.getStyleClass().add("field-error");
        field.setTooltip(new Tooltip(message));
    }

    private void setFieldSuccess(TextField field) {
        field.getStyleClass().removeAll("field-error");
        field.getStyleClass().add("field-success");
        field.setTooltip(null);
    }

    private void clearValidationStyles() {
        usernameField.getStyleClass().removeAll("field-error", "field-success");
        emailField.getStyleClass().removeAll("field-error", "field-success");
        phoneField.getStyleClass().removeAll("field-error", "field-success");
        passwordField.getStyleClass().removeAll("field-error", "field-success");
        confirmPasswordField.getStyleClass().removeAll("field-error", "field-success");
    }

    // =====================================================
    // DASHBOARD COMMUNICATION
    // =====================================================

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminUserController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}