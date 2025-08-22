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
import javafx.scene.layout.Region;
import javafx.util.Callback;
import javafx.geometry.Pos;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.util.Duration;

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
    @FXML private Button editUserButton;
    @FXML private Button deleteUserButton;
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
    // FXML COMPONENTS - Quick Stats & Status
    // =====================================================
    
    @FXML private Label totalStaffLabel;
    @FXML private Label activeStaffLabel;
    @FXML private Label resultCountLabel;
    @FXML private Label statusLabel;
    @FXML private Label lastUpdateLabel;

    // =====================================================
    // FXML COMPONENTS - Form Section
    // =====================================================
    
    @FXML private VBox userFormSection;
    @FXML private VBox userFormOverlay;
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
    @FXML private Button closeFormButton;
    @FXML private Label formTitleLabel;

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
        // Setup table columns với PropertyValueFactory
        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        
        // Status column với custom cell factory
        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            return new javafx.beans.property.SimpleStringProperty(isActive ? "ACTIVE" : "INACTIVE");
        });
        
        // Created at column - placeholder
        createdAtColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("N/A"));

        // Setup actions column
        setupActionsColumn();

        // *** SỬ DỤNG CONSTRAINED_RESIZE_POLICY TRUYỀN THỐNG ***
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // *** THIẾT LẬP FIXED WIDTH CHO CỘT ACTIONS ***
        actionsColumn.setResizable(false);
        actionsColumn.setPrefWidth(120);
        actionsColumn.setMinWidth(120);
        actionsColumn.setMaxWidth(120);
        
        // *** THIẾT LẬP TỶ LỆ CHO CÁC CỘT KHÁC ***
        // Tổng tỷ lệ = 100%, trừ đi 120px cho actions column
        
        // ID Column - 6%
        idColumn.setPrefWidth(50);
        idColumn.setMinWidth(35);
        idColumn.setMaxWidth(80);
        
        // Username Column - 12%
        usernameColumn.setPrefWidth(100);
        usernameColumn.setMinWidth(80);
        usernameColumn.setMaxWidth(150);
        
        // Full Name Column - 18%
        fullNameColumn.setPrefWidth(150);
        fullNameColumn.setMinWidth(120);
        fullNameColumn.setMaxWidth(200);
        
        // Email Column - 22% (quan trọng nhất)
        emailColumn.setPrefWidth(180);
        emailColumn.setMinWidth(150);
        emailColumn.setMaxWidth(250);
        
        // Phone Column - 12%
        phoneColumn.setPrefWidth(100);
        phoneColumn.setMinWidth(80);
        phoneColumn.setMaxWidth(130);
        
        // Role Column - 10%
        roleColumn.setPrefWidth(80);
        roleColumn.setMinWidth(65);
        roleColumn.setMaxWidth(100);
        
        // Status Column - 8%
        statusColumn.setPrefWidth(70);
        statusColumn.setMinWidth(55);
        statusColumn.setMaxWidth(90);
        
        // Created At Column - 12%
        createdAtColumn.setPrefWidth(100);
        createdAtColumn.setMinWidth(80);
        createdAtColumn.setMaxWidth(120);

        // Set data source
        userTable.setItems(filteredUserList);

        // Selection handler
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editUserButton.setDisable(!hasSelection);
            deleteUserButton.setDisable(!hasSelection);
            
            if (newSelection != null) {
                populateForm(newSelection);
                System.out.println("Selected user: " + newSelection.getFullName());
            }
        });

        // Double-click to edit
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showEditUserForm(row.getItem());
                }
            });
            return row;
        });
        
        // *** RESPONSIVE LISTENER - SỬ DỤNG SCENE WIDTH ***
        Platform.runLater(() -> {
            if (userTable.getScene() != null) {
                setupResponsiveListener();
            } else {
                userTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        setupResponsiveListener();
                    }
                });
            }
        });
    }

    // *** PHƯƠNG THỨC THIẾT LẬP RESPONSIVE LISTENER ***
    private void setupResponsiveListener() {
        if (userTable.getScene() != null && userTable.getScene().getWindow() != null) {
            userTable.getScene().getWindow().widthProperty().addListener((obs, oldWidth, newWidth) -> {
                Platform.runLater(() -> adjustColumnsBasedOnWindowWidth(newWidth.doubleValue()));
            });
            
            // Trigger initial adjustment
            Platform.runLater(() -> adjustColumnsBasedOnWindowWidth(userTable.getScene().getWindow().getWidth()));
        }
    }

    // *** ĐIỀU CHỈNH CỘT DỰA TRÊN CHIỀU RỘNG CỬA SỔ ***
    private void adjustColumnsBasedOnWindowWidth(double windowWidth) {
        System.out.println("🔍 Window width: " + windowWidth);
        
        // Tính toán chiều rộng có sẵn cho table (trừ đi sidebar và padding)
        double availableWidth = windowWidth - 250; // 250px cho sidebar + padding
        
        if (availableWidth < 800) {
            // Màn hình rất nhỏ: Ẩn cột Created At và Phone
            createdAtColumn.setVisible(false);
            phoneColumn.setVisible(false);
            
            System.out.println("📱 Small screen mode: hiding Created At & Phone columns");
            
        } else if (availableWidth < 1000) {
            // Màn hình vừa: Chỉ ẩn cột Created At
            createdAtColumn.setVisible(false);
            phoneColumn.setVisible(true);
            
            System.out.println("💻 Medium screen mode: hiding Created At column");
            
        } else {
            // Màn hình lớn: Hiển thị tất cả
            createdAtColumn.setVisible(true);
            phoneColumn.setVisible(true);
            
            System.out.println("🖥️ Large screen mode: showing all columns");
        }
        
        // Force table to recalculate column widths
        userTable.refresh();
    }

    // *** RESPONSIVE BEHAVIOR CHO MÀN HÌNH NHỎ ***
    private void adjustColumnsForResponsive(double tableWidth) {
        // Ẩn cột ít quan trọng trên màn hình nhỏ
        if (tableWidth < 1000) {
            // Ẩn cột Created At
            createdAtColumn.setVisible(false);
            createdAtColumn.setPrefWidth(0);
            
            // Tái phân phối cho các cột còn lại
            double remainingWidth = tableWidth - 120 - 20;
            if (remainingWidth > 0) {
                idColumn.setPrefWidth(remainingWidth * 0.07);        // 7%
                usernameColumn.setPrefWidth(remainingWidth * 0.15);  // 15%
                fullNameColumn.setPrefWidth(remainingWidth * 0.20);  // 20%
                emailColumn.setPrefWidth(remainingWidth * 0.25);     // 25%
                phoneColumn.setPrefWidth(remainingWidth * 0.15);     // 15%
                roleColumn.setPrefWidth(remainingWidth * 0.10);      // 10%
                statusColumn.setPrefWidth(remainingWidth * 0.08);    // 8%
            }
        } else if (tableWidth < 800) {
            // Ẩn thêm cột Phone
            phoneColumn.setVisible(false);
            phoneColumn.setPrefWidth(0);
            
            double remainingWidth = tableWidth - 120 - 20;
            if (remainingWidth > 0) {
                idColumn.setPrefWidth(remainingWidth * 0.08);        // 8%
                usernameColumn.setPrefWidth(remainingWidth * 0.18);  // 18%
                fullNameColumn.setPrefWidth(remainingWidth * 0.25);  // 25%
                emailColumn.setPrefWidth(remainingWidth * 0.32);     // 32%
                roleColumn.setPrefWidth(remainingWidth * 0.12);      // 12%
                statusColumn.setPrefWidth(remainingWidth * 0.05);    // 5%
            }
        } else {
            // Hiển thị lại tất cả cột khi đủ không gian
            createdAtColumn.setVisible(true);
            phoneColumn.setVisible(true);
            
            // Gọi lại responsive adjustment
            if (userTable.getScene() != null && userTable.getScene().getWindow() != null) {
                adjustColumnsBasedOnWindowWidth(userTable.getScene().getWindow().getWidth());
            }
        }
    }

    // *** UPDATE PHƯƠNG THỨC setupActionsColumn() ĐỂ COMPACT HỢN ***
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<User, Void>() {
            private final HBox actionBox = new HBox(2); // Giảm spacing
            private final Button editButton = new Button("✏");
            private final Button deleteButton = new Button("🗑");
            private final Button resetPasswordButton = new Button("🔒");

            {
                // Style buttons với kích thước nhỏ gọn
                editButton.getStyleClass().add("table-btn-warning");
                deleteButton.getStyleClass().add("table-btn-danger");
                resetPasswordButton.getStyleClass().add("table-btn-info");
                
                // Set kích thước cố định
                editButton.setPrefSize(28, 24);
                deleteButton.setPrefSize(28, 24);
                resetPasswordButton.setPrefSize(28, 24);

                // Tooltips
                editButton.setTooltip(new Tooltip("Sửa"));
                deleteButton.setTooltip(new Tooltip("Xóa"));
                resetPasswordButton.setTooltip(new Tooltip("Reset"));

                // Event handlers
                editButton.setOnAction(e -> {
                    User user = getTableView().getItems().get(getIndex());
                    showEditUserForm(user);
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
                actionBox.setAlignment(javafx.geometry.Pos.CENTER);
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

        // Initially hide form overlay
        userFormOverlay.setVisible(false);
        userFormOverlay.setManaged(false);
        
        // Set initial form title
        formTitleLabel.setText("✨ Thêm nhân viên mới");
    }

    private void setupEventHandlers() {
        // Search and filter handlers
        searchField.textProperty().addListener((obs, oldText, newText) -> filterUsers());
        roleFilterCombo.setOnAction(e -> filterUsers());
        statusFilterCombo.setOnAction(e -> filterUsers());

        // Button handlers
        refreshButton.setOnAction(e -> loadUsers());
        addUserButton.setOnAction(e -> showAddUserForm());
        editUserButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                showEditUserForm(selectedUser);
            }
        });
        deleteUserButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                deleteUser(selectedUser);
            }
        });
        exportButton.setOnAction(e -> exportUsers());

        // Form button handlers
        saveUserButton.setOnAction(e -> saveUser());
        cancelButton.setOnAction(e -> hideFormOverlay());
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
                    filterUsers();
                    updateStatistics();
                    // Trigger resize sau khi load data
                    Platform.runLater(() -> {
                        if (userTable.getScene() != null && userTable.getScene().getWindow() != null) {
                            adjustColumnsBasedOnWindowWidth(userTable.getScene().getWindow().getWidth());
                        }
                    });
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
        
        // Cập nhật số lượng kết quả
        resultCountLabel.setText("Hiển thị " + filteredUserList.size() + " / " + userList.size() + " nhân viên");
    }

    private void updateStatistics() {
        int total = userList.size();
        long active = userList.stream().filter(User::isActive).count();
        long admins = userList.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long staff = userList.stream().filter(u -> "STAFF".equals(u.getRole())).count();

        // Cập nhật các label thống kê cũ
        totalUsersLabel.setText(String.valueOf(total));
        activeUsersLabel.setText(String.valueOf(active));
        adminUsersLabel.setText(String.valueOf(admins));
        staffUsersLabel.setText(String.valueOf(staff));
        
        // Cập nhật quick stats mới
        totalStaffLabel.setText(String.valueOf(total));
        activeStaffLabel.setText(String.valueOf(active));
        
        // Cập nhật thời gian
        lastUpdateLabel.setText("Cập nhật lần cuối: " + 
            java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    // =====================================================
    // FORM OPERATIONS
    // =====================================================

    @FXML
    private void showAddUserForm() {
        currentEditingUser = null;
        formTitleLabel.setText("✨ Thêm nhân viên mới");
        resetForm();
        showFormOverlay();
        usernameField.requestFocus();
    }

    @FXML
    private void hideUserForm() {
        hideFormOverlay();
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
    }

    // =====================================================
    // USER ACTIONS
    // =====================================================

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
    // FORM OVERLAY METHODS
    // =====================================================

    private void showEditUserForm(User user) {
        currentEditingUser = user;
        formTitleLabel.setText("✎ Chỉnh sửa nhân viên");
        populateForm(user);
        showFormOverlay();
        fullNameField.requestFocus();
    }

    private void showFormOverlay() {
        userFormOverlay.setVisible(true);
        userFormOverlay.setManaged(true);
        
        // Animation hiệu ứng fade in
        Platform.runLater(() -> {
            userFormOverlay.setOpacity(0);
            Timeline timeline = new Timeline(
                new KeyFrame(Duration.millis(300), 
                    new KeyValue(userFormOverlay.opacityProperty(), 1))
            );
            timeline.play();
        });
    }

    @FXML
    private void hideFormOverlay() {
        // Animation hiệu ứng fade out
        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(200), 
                new KeyValue(userFormOverlay.opacityProperty(), 0))
        );
        timeline.setOnFinished(e -> {
            userFormOverlay.setVisible(false);
            userFormOverlay.setManaged(false);
            resetForm();
            currentEditingUser = null;
        });
        timeline.play();
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