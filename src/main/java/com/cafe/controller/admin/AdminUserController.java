package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.dao.UserDAO;
import com.cafe.model.User;
import com.cafe.model.enums.UserRole;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho quản lý người dùng trong Admin Dashboard
 */
public class AdminUserController implements Initializable, DashboardCommunicator {

    // Search and Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> roleFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Button addUserButton;
    @FXML private Button exportButton;

    // User Table
    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, UserRole> roleColumn;
    @FXML private TableColumn<User, String> statusColumn;
    @FXML private TableColumn<User, String> createdAtColumn;
    @FXML private TableColumn<User, Void> actionsColumn;

    // Statistics
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label adminUsersLabel;
    @FXML private Label staffUsersLabel;

    // Form Section
    @FXML private VBox userFormSection;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<UserRole> roleCombo;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button saveUserButton;
    @FXML private Button cancelButton;
    @FXML private Button resetFormButton;

    // Data
    private ObservableList<User> userList = FXCollections.observableArrayList();
    private UserDAO userDAO;
    private Object dashboardController;
    private User currentEditingUser = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeDAO();
            setupUserTable();
            setupFilters();
            setupEventHandlers();
            setupValidation();
            loadUsers();
            updateStatistics();

            System.out.println("✅ AdminUserController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminUserController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo giao diện quản lý người dùng: " + e.getMessage());
        }
    }

    private void initializeDAO() {
        this.userDAO = new UserDAO();
    }

    private void setupUserTable() {
        // Setup columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        createdAtColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));

        // Setup actions column
        setupActionsColumn();

        // Set data
        userTable.setItems(userList);

        // Selection handler
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final Button resetPasswordButton = new Button("Reset MK");

            {
                editButton.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
                deleteButton.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
                resetPasswordButton.getStyleClass().addAll("btn", "btn-warning", "btn-sm");

                editButton.setOnAction(e -> editUser(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteUser(getTableView().getItems().get(getIndex())));
                resetPasswordButton.setOnAction(e -> resetPassword(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new VBox(5, editButton, deleteButton, resetPasswordButton));
                }
            }
        });
    }

    private void setupFilters() {
        // Role filter
        roleFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ADMIN", "STAFF", "MANAGER"
        ));
        roleFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ACTIVE", "INACTIVE", "SUSPENDED"
        ));
        statusFilterCombo.setValue("Tất cả");

        // Role combo for form
        roleCombo.setItems(FXCollections.observableArrayList(UserRole.values()));
        roleCombo.setValue(UserRole.STAFF);

        // Status combo for form
        statusCombo.setItems(FXCollections.observableArrayList(
            "ACTIVE", "INACTIVE", "SUSPENDED"
        ));
        statusCombo.setValue("ACTIVE");
    }

    private void setupEventHandlers() {
        // Search and filter
        searchField.textProperty().addListener((obs, oldText, newText) -> filterUsers());
        roleFilterCombo.setOnAction(e -> filterUsers());
        statusFilterCombo.setOnAction(e -> filterUsers());

        // Buttons
        refreshButton.setOnAction(e -> loadUsers());
        addUserButton.setOnAction(e -> showAddUserForm());
        exportButton.setOnAction(e -> exportUsers());

        // Form buttons
        saveUserButton.setOnAction(e -> saveUser());
        cancelButton.setOnAction(e -> hideUserForm());
        resetFormButton.setOnAction(e -> resetForm());
    }

    private void setupValidation() {
        // Real-time validation
        usernameField.textProperty().addListener((obs, oldText, newText) -> validateUsername());
        emailField.textProperty().addListener((obs, oldText, newText) -> validateEmail());
        phoneField.textProperty().addListener((obs, oldText, newText) -> validatePhone());
        passwordField.textProperty().addListener((obs, oldText, newText) -> validatePassword());
        confirmPasswordField.textProperty().addListener((obs, oldText, newText) -> validateConfirmPassword());
    }

    private void loadUsers() {
        Task<List<User>> loadTask = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                return userDAO.findAll();
            }

            @Override
            protected void succeeded() {
                userList.clear();
                userList.addAll(getValue());
                updateStatistics();
                Platform.runLater(() -> {
                    AlertUtils.showInfo("Thành công", "Đã tải danh sách người dùng");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Không thể tải danh sách người dùng: " + getException().getMessage());
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String roleFilter = roleFilterCombo.getValue();
        String statusFilter = statusFilterCombo.getValue();

        ObservableList<User> filteredList = FXCollections.observableArrayList();

        for (User user : userList) {
            boolean matchesSearch = searchText.isEmpty() ||
                user.getUsername().toLowerCase().contains(searchText) ||
                user.getFullName().toLowerCase().contains(searchText) ||
                user.getEmail().toLowerCase().contains(searchText);

            boolean matchesRole = "Tất cả".equals(roleFilter) || 
                user.getRole().name().equals(roleFilter);

            boolean matchesStatus = "Tất cả".equals(statusFilter) || 
                user.getStatus().equals(statusFilter);

            if (matchesSearch && matchesRole && matchesStatus) {
                filteredList.add(user);
            }
        }

        userTable.setItems(filteredList);
    }

    private void updateStatistics() {
        int total = userList.size();
        long active = userList.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
        long admins = userList.stream().filter(u -> u.getRole() == UserRole.ADMIN).count();
        long staff = userList.stream().filter(u -> u.getRole() == UserRole.STAFF).count();

        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        adminUsersLabel.setText(String.valueOf(admins));
        staffUsersLabel.setText(String.valueOf(staff));
    }

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
        roleCombo.setValue(UserRole.STAFF);
        statusCombo.setValue("ACTIVE");
        
        // Clear validation styles
        clearValidationStyles();
    }

    private void populateForm(User user) {
        currentEditingUser = user;
        usernameField.setText(user.getUsername());
        fullNameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        phoneField.setText(user.getPhone());
        roleCombo.setValue(user.getRole());
        statusCombo.setValue(user.getStatus());
        
        // Clear password fields for editing
        passwordField.clear();
        confirmPasswordField.clear();
        
        userFormSection.setVisible(true);
        userFormSection.setManaged(true);
    }

    private void editUser(User user) {
        populateForm(user);
    }

    private void deleteUser(User user) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa người dùng này?");
        confirmAlert.setContentText("Người dùng: " + user.getFullName() + " (" + user.getUsername() + ")");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return userDAO.delete(user.getId());
                }

                @Override
                protected void succeeded() {
                    if (getValue()) {
                        userList.remove(user);
                        updateStatistics();
                        Platform.runLater(() -> {
                            AlertUtils.showInfo("Thành công", "Đã xóa người dùng");
                        });
                    } else {
                        Platform.runLater(() -> {
                            AlertUtils.showError("Lỗi", "Không thể xóa người dùng");
                        });
                    }
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
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Reset mật khẩu");
        dialog.setHeaderText("Nhập mật khẩu mới cho " + user.getFullName());
        dialog.setContentText("Mật khẩu mới:");

        dialog.showAndWait().ifPresent(newPassword -> {
            if (ValidationUtils.isValidPassword(newPassword)) {
                Task<Boolean> resetTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        String hashedPassword = PasswordUtil.hashPassword(newPassword);
                        user.setPassword(hashedPassword);
                        return userDAO.update(user);
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
        });
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
                user.setStatus(statusCombo.getValue());

                // Set password only if provided
                if (!passwordField.getText().isEmpty()) {
                    user.setPassword(PasswordUtil.hashPassword(passwordField.getText()));
                }

                if (currentEditingUser == null) {
                    user.setCreatedAt(LocalDateTime.now());
                    return userDAO.create(user) != null;
                } else {
                    return userDAO.update(user);
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

    private void exportUsers() {
        // TODO: Implement export functionality
        AlertUtils.showInfo("Thông báo", "Tính năng xuất dữ liệu sẽ được cập nhật sau");
    }

    // Dashboard Communication
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
