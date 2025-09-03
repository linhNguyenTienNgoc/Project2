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

import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ButtonBar;

import javafx.fxml.FXMLLoader;

import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;



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

    // =====================================================
    // FXML COMPONENTS - Statistics & Status
    // =====================================================
    
    @FXML private Label totalUsersLabel;
    @FXML private Label activeUsersLabel;
    @FXML private Label adminUsersLabel;
    @FXML private Label staffUsersLabel;
    @FXML private Label totalStaffLabel;
    @FXML private Label activeStaffLabel;
    @FXML private Label resultCountLabel;

    // =====================================================
    // FXML COMPONENTS - Right Panel Preview
    // =====================================================
    
    @FXML private ImageView userAvatarView;
    @FXML private Button changeAvatarButton;
    @FXML private Label previewFullName;
    @FXML private Label previewUsername;
    @FXML private Label previewRole;
    @FXML private Label previewEmail;
    @FXML private Label previewPhone;
    @FXML private Label previewStatus;
    @FXML private TextArea previewNotes;
    @FXML private Label previewCreatedDate;
    @FXML private Button resetPasswordButton;
    @FXML private Button toggleStatusButton;
    
    // =====================================================
    // FXML COMPONENTS - Form Section (for dialogs)
    // =====================================================
    // Form components are now handled by UserFormDialogController

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
            setupEventHandlers();
            setupPreviewPanel();
            
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
        roleColumn.setCellValueFactory(cellData -> {
            String role = cellData.getValue().getRole();
            System.out.println("🔍 Role for user " + cellData.getValue().getUsername() + ": '" + role + "'");
            return new javafx.beans.property.SimpleStringProperty(role != null ? role : "STAFF");
        });
        
        // Status column với custom cell factory
        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            return new javafx.beans.property.SimpleStringProperty(isActive ? "ACTIVE" : "INACTIVE");
        });
        
        // Created at column - placeholder
        createdAtColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("N/A"));

        // *** SỬ DỤNG UNCONSTRAINED_RESIZE_POLICY (thay thế deprecated CONSTRAINED_RESIZE_POLICY) ***
        userTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
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
                updatePreviewPanel(newSelection);
                System.out.println("Selected user: " + newSelection.getFullName());
            } else {
                updatePreviewPanel(null);
            }
        });

        // Double-click to edit
        userTable.setRowFactory(tv -> {
            TableRow<User> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    showEditUserDialog(row.getItem());
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





    private void setupFilters() {
        // Role filter
        roleFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ADMIN", "STAFF"
        ));
        roleFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ACTIVE", "INACTIVE"
        ));
        statusFilterCombo.setValue("Tất cả");
    }



    private void setupPreviewPanel() {
        // Setup preview panel components
        previewFullName.setText("Chưa chọn nhân viên");
        previewUsername.setText("-");
        previewRole.setText("-");
        previewEmail.setText("-");
        previewPhone.setText("-");
        previewStatus.setText("-");
        previewNotes.setText("Chưa có ghi chú");
        previewCreatedDate.setText("-");
        
        // Setup preview panel event handlers
        changeAvatarButton.setOnAction(e -> changeUserAvatar());
        resetPasswordButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                resetPassword(selectedUser);
            } else {
                AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn một nhân viên để đổi mật khẩu");
            }
        });
        toggleStatusButton.setOnAction(e -> toggleUserStatus());
    }

    private void setupEventHandlers() {
        // Search and filter handlers
        searchField.textProperty().addListener((obs, oldText, newText) -> filterUsers());
        roleFilterCombo.setOnAction(e -> filterUsers());
        statusFilterCombo.setOnAction(e -> filterUsers());

        // Button handlers
        refreshButton.setOnAction(e -> loadUsers());
        addUserButton.setOnAction(e -> showAddUserDialog());
        editUserButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                showEditUserDialog(selectedUser);
            }
        });
        deleteUserButton.setOnAction(e -> {
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                deleteUser(selectedUser);
            }
        });
    }



    // =====================================================
    // DATA OPERATIONS
    // =====================================================

    private void loadUsers() {
        System.out.println("🔄 Loading users...");
        Task<List<User>> loadTask = new Task<List<User>>() {
            @Override
            protected List<User> call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    UserDAO dao = new UserDAOImpl(connection);
                    List<User> users = dao.getAllUsers();
                    System.out.println("📊 Loaded " + users.size() + " users from database");
                    for (User user : users) {
                        System.out.println("  - " + user.getUsername() + " (" + user.getRole() + ")");
                    }
                    return users;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    userList.clear();
                    if (getValue() != null) {
                        userList.addAll(getValue());
                        System.out.println("✅ Added " + getValue().size() + " users to table");
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

        System.out.println("🔍 Filtering users - Search: '" + searchText + "', Role: '" + roleFilter + "', Status: '" + statusFilter + "'");
        System.out.println("📋 Total users in list: " + userList.size());

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
                System.out.println("  ✅ Added: " + user.getUsername() + " (" + user.getRole() + ", " + (user.isActive() ? "ACTIVE" : "INACTIVE") + ")");
            } else {
                System.out.println("  ❌ Filtered out: " + user.getUsername() + " (search:" + matchesSearch + ", role:" + matchesRole + ", status:" + matchesStatus + ")");
            }
        }
        
        System.out.println("📊 Filtered result: " + filteredUserList.size() + " users");
        
        // Cập nhật số lượng kết quả
        if (resultCountLabel != null) {
        resultCountLabel.setText("Hiển thị " + filteredUserList.size() + " / " + userList.size() + " nhân viên");
        }
    }

    private void updateStatistics() {
        int total = userList.size();
        long active = userList.stream().filter(User::isActive).count();
        long admins = userList.stream().filter(u -> "ADMIN".equals(u.getRole())).count();
        long staff = userList.stream().filter(u -> "STAFF".equals(u.getRole())).count();

        // Cập nhật các label thống kê
        if (totalUsersLabel != null) totalUsersLabel.setText(String.valueOf(total));
        if (activeUsersLabel != null) activeUsersLabel.setText(String.valueOf(active));
        if (adminUsersLabel != null) adminUsersLabel.setText(String.valueOf(admins));
        if (staffUsersLabel != null) staffUsersLabel.setText(String.valueOf(staff));
        if (totalStaffLabel != null) totalStaffLabel.setText(String.valueOf(total));
        if (activeStaffLabel != null) activeStaffLabel.setText(String.valueOf(active));
    }

    // =====================================================
    // PREVIEW PANEL OPERATIONS
    // =====================================================

    private void updatePreviewPanel(User user) {
        if (user != null) {
            System.out.println("👤 Updating preview for user: " + user.getFullName() + " (ID: " + user.getUserId() + ")");
            System.out.println("📷 User imageUrl: " + user.getImageUrl());
            
            previewFullName.setText(user.getFullName());
            previewUsername.setText(user.getUsername());
            previewRole.setText(user.getRole());
            previewEmail.setText(user.getEmail() != null ? user.getEmail() : "-");
            previewPhone.setText(user.getPhone() != null ? user.getPhone() : "-");
            previewStatus.setText(user.isActive() ? "ACTIVE" : "INACTIVE");
            previewNotes.setText("Chưa có ghi chú"); // Placeholder
            previewCreatedDate.setText("N/A"); // Placeholder
            
            // Load user image
            loadUserImage(user.getImageUrl());
        } else {
            previewFullName.setText("Chưa chọn nhân viên");
            previewUsername.setText("-");
            previewRole.setText("-");
            previewEmail.setText("-");
            previewPhone.setText("-");
            previewStatus.setText("-");
            previewNotes.setText("Chưa có ghi chú");
            previewCreatedDate.setText("-");
            
            // Load default image
            loadDefaultUserImage();
        }
    }

    @FXML
    private void showAddUserDialog() {
        currentEditingUser = null;
        showFormDialog();
    }

    private void showEditUserDialog(User user) {
        currentEditingUser = user;
        showFormDialog();
    }
    
    private void showFormDialog() {
        try {
            // Load dialog FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/admin/user-form-dialog.fxml"));
            DialogPane dialogPane = loader.load();
            
            // Get the dialog controller
            UserFormDialogController dialogController = loader.getController();
            
            // Create dialog
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle(currentEditingUser == null ? "Thêm nhân viên mới" : "Sửa thông tin nhân viên");
            dialog.setResizable(true);
            
            // Setup form in dialog controller
            dialogController.setCurrentEditingUser(currentEditingUser);
            
            // Set result converter
            dialog.setResultConverter(buttonType -> {
                if (buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                    return ButtonType.OK;
                }
                return null;
            });
            
            // Show dialog and wait for result
            ButtonType result = dialog.showAndWait().orElse(null);
            
            if (result == ButtonType.OK) {
                // Get form data from dialog controller
                User userData = dialogController.getFormData();
                if (userData != null) {
                    saveUserData(userData);
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể hiển thị form: " + e.getMessage());
        }
    }
    

    
    private void saveUserData(User userData) {
        Task<Boolean> saveTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try (Connection connection = DatabaseConfig.getConnection()) {
                        UserDAO dao = new UserDAOImpl(connection);
                    
                    if (currentEditingUser == null) {
                        return dao.insertUser(userData);
                    } else {
                        return dao.updateUser(userData);
                    }
                    }
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        if (getValue()) {
                        AlertUtils.showInfo("Thành công", 
                            currentEditingUser == null ? "Đã thêm người dùng mới" : "Đã cập nhật người dùng");
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
    

    


    private void changeUserAvatar() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn một nhân viên để đổi ảnh đại diện");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh đại diện cho " + selectedUser.getFullName());
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Get current window
        Window window = userTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            String imageUrl = selectedFile.toURI().toString();
            
            // Update user image in database
            Task<Boolean> updateTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        try (Connection connection = DatabaseConfig.getConnection()) {
                            UserDAO dao = new UserDAOImpl(connection);
                        return dao.updateUserImage(selectedUser.getUserId(), imageUrl);
                        }
                    }

                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            if (getValue()) {
                            // Update user object
                            selectedUser.setImageUrl(imageUrl);
                            
                            // Update preview image
                            loadUserImage(imageUrl);
                            
                            // Refresh table to show updated data
                            loadUsers();
                            
                            AlertUtils.showInfo("Thành công", "Đã cập nhật ảnh đại diện cho " + selectedUser.getFullName());
                            } else {
                            AlertUtils.showError("Lỗi", "Không thể cập nhật ảnh đại diện");
                            }
                        });
                    }

                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi cập nhật ảnh: " + getException().getMessage());
                        });
                    }
                };

            new Thread(updateTask).start();
        }
    }
    
    private void loadUserImage(String imageUrl) {
        System.out.println("🖼️ Loading user image: " + imageUrl);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            try {
                Image image = new Image(imageUrl, false);
                userAvatarView.setImage(image);
                System.out.println("✅ Successfully loaded user image: " + imageUrl);
            } catch (Exception e) {
                System.err.println("❌ Error loading user image: " + e.getMessage());
                // Load default image on error
                loadDefaultUserImage();
            }
        } else {
            System.out.println("🔄 No image URL, loading unique placeholder");
            // Load unique placeholder based on user ID
            loadUniqueUserPlaceholder();
        }
    }
    
    private void loadDefaultUserImage() {
        try {
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholders/user-placeholder.png"));
            userAvatarView.setImage(defaultImage);
        } catch (Exception e) {
            System.err.println("Error loading default user image: " + e.getMessage());
        }
    }
    
    private void loadUniqueUserPlaceholder() {
        try {
            // Get current selected user
            User selectedUser = userTable.getSelectionModel().getSelectedItem();
            if (selectedUser != null) {
                // Create unique placeholder based on user ID
                int userId = selectedUser.getUserId();
                System.out.println("🎨 Creating unique placeholder for user ID: " + userId);
                
                // For now, use the same placeholder but with different colors based on user ID
                // In the future, you can create different placeholder images
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/placeholders/user-placeholder.png"));
                userAvatarView.setImage(defaultImage);
                
                // Add a visual indicator that this is a unique placeholder
                // You could modify the ImageView style or add a label here
                System.out.println("✅ Loaded unique placeholder for user: " + selectedUser.getFullName());
            } else {
                // Fallback to default image
                loadDefaultUserImage();
            }
        } catch (Exception e) {
            System.err.println("Error loading unique user placeholder: " + e.getMessage());
            loadDefaultUserImage();
        }
    }



    private void toggleUserStatus() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn một nhân viên để đổi trạng thái");
            return;
        }
        
        String currentStatus = selectedUser.isActive() ? "ACTIVE" : "INACTIVE";
        String newStatus = selectedUser.isActive() ? "INACTIVE" : "ACTIVE";
        
        boolean confirmed = AlertUtils.showConfirmation(
            "Xác nhận đổi trạng thái", 
            "Bạn có chắc chắn muốn đổi trạng thái của " + selectedUser.getFullName() + 
            " từ " + currentStatus + " sang " + newStatus + "?"
        );
        
        if (confirmed) {
            Task<Boolean> toggleTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    UserDAO dao = new UserDAOImpl(connection);
                        selectedUser.setActive(!selectedUser.isActive());
                        return dao.updateUser(selectedUser);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                            // Update the user in the list
                            int index = userList.indexOf(selectedUser);
                            if (index >= 0) {
                                userList.set(index, selectedUser);
                            }
                            filterUsers();
                            updateStatistics();
                            updatePreviewPanel(selectedUser);
                            AlertUtils.showInfo("Thành công", "Đã đổi trạng thái của " + selectedUser.getFullName() + " thành " + newStatus);
                    } else {
                            AlertUtils.showError("Lỗi", "Không thể đổi trạng thái nhân viên");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi đổi trạng thái: " + getException().getMessage());
                });
            }
        };

            new Thread(toggleTask).start();
    }
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