package com.cafe.controller.user;

import com.cafe.CafeManagementApplication;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.AreaDAOImpl;
import com.cafe.dao.base.TableDAOImpl;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.IOException;
import java.util.ResourceBundle;

public class UserController {

    private UserDAO userDAO;

    @FXML private VBox userForm;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField idField;
    @FXML private TextField passwordField;
    @FXML private TableView<User> staffTable;
    @FXML private TableColumn<User, Integer> idColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> fullNameColumn;
    @FXML private TableColumn<User, String> emailColumn;
    @FXML private TableColumn<User, String> phoneColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> updateColumn;
    @FXML private TableColumn<User, Void> deleteColumn;
    @FXML private Button inlineSubmit;

    // ID of employee being update, if -1 then form is in creating mode
    private int updateID = -1;

    @FXML
    public void initialize() {
        setupDatabase();
        setupRoleComboBox();

        idColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        roleColumn.setCellValueFactory(new PropertyValueFactory<>("role"));
        addButtonToTable(updateColumn, "Update", this::toggleUpdateID);
        addButtonToTable(deleteColumn, "Delete", this::deleteUser);
        staffTable.setItems(getUserList());
    }
    
    private void setupRoleComboBox() {
        if (roleComboBox != null) {
            roleComboBox.getItems().addAll("Admin", "Manager", "Cashier", "Waiter", "Barista");
        }
    }

    private void setupDatabase() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();

            if (dbConfig.testConnection()) {
                // Connection will be managed per operation
                System.out.println("✅ Database connection test successful");
            } else {
                // Database connection failed
                System.err.println("❌ Database connection test failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addButtonToTable(TableColumn<User, Void> column, String buttonText, java.util.function.Consumer<User> action) {
        column.setCellFactory(new Callback<>() {

            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button(buttonText);

                    {
                        btn.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            action.accept(user);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    @FXML
    private void onInlineSubmitClicked() {
        // Validation
        if (!validateInput()) {
            return;
        }
        
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String username = usernameField.getText().trim();
            String fullName = fullNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = roleComboBox.getValue().trim();
            String password = passwordField.getText().trim();

            try (Connection connection = DatabaseConfig.getConnection()) {
                UserDAO userDAO = new UserDAOImpl(connection);
                if (updateID != -1) {
                    // Update existing user
                    String finalPassword = password.isEmpty() ? 
                        staffTable.getItems().stream()
                            .filter(u -> u.getUserId() == updateID)
                            .findFirst()
                            .map(User::getPassword)
                            .orElse("") : password;
                    userDAO.updateUser(new User(id, username, fullName, email, phone, role, true, finalPassword));
                    System.out.println("✅ User updated successfully: " + username);
                } else {
                    // Create new user
                    userDAO.insertUser(new User(id, username, fullName, email, phone, role, true, password));
                    System.out.println("✅ User created successfully: " + username);
                }
            } catch (Exception e) {
                System.err.println("❌ Error updating/inserting user: " + e.getMessage());
                e.printStackTrace();
                showError("Lỗi khi lưu người dùng: " + e.getMessage());
                return;
            }
            
            staffTable.setItems(getUserList());

            if (updateID == -1) {
                clearForm();
            }
            
        } catch (NumberFormatException e) {
            showError("ID phải là số!");
        } catch (Exception e) {
            showError("Lỗi không xác định: " + e.getMessage());
        }
    }
    
    private boolean validateInput() {
        if (idField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập ID!");
            return false;
        }
        if (usernameField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            return false;
        }
        if (fullNameField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập họ tên!");
            return false;
        }
        if (roleComboBox.getValue() == null) {
            showError("Vui lòng chọn vai trò!");
            return false;
        }
        if (updateID == -1 && passwordField.getText().trim().isEmpty()) {
            showError("Vui lòng nhập mật khẩu cho người dùng mới!");
            return false;
        }
        return true;
    }
    
    private void clearForm() {
        idField.clear();
        usernameField.clear();
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        roleComboBox.setValue(null);
        passwordField.clear();
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void showTable(){
        CafeManagementApplication.showTable();
    }

    private ObservableList<User> getUserList() {
        ObservableList<User> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseConfig.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM users")) {

            while (rs.next()) {
                list.add(new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("full_name"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("role"),
                        rs.getBoolean("is_active"),
                        rs.getString("password")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void deleteUser(User user) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(connection);
            userDAO.deleteUser(user.getUserId());
            staffTable.setItems(getUserList());
            System.out.println("✅ User deleted successfully: " + user.getUsername());
        } catch (Exception e) {
            System.err.println("❌ Error deleting user: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void toggleUpdateID(User user) {
        updateID = updateID == user.getUserId() ? -1 : user.getUserId();
        if (updateID != -1) {
            idField.setText(String.valueOf(user.getUserId()));
            usernameField.setText(user.getUsername());
            fullNameField.setText(user.getFullName());
            emailField.setText(user.getEmail());
            phoneField.setText(user.getPhone());
            roleComboBox.getSelectionModel().select(roleComboBox.getItems().indexOf(user.getRole()));
            staffTable.getSelectionModel().select(user);
        } else {
            idField.clear();
            usernameField.clear();
            fullNameField.clear();
            emailField.clear();
            phoneField.clear();
            roleComboBox.setValue(null);
            passwordField.clear();
        }
        inlineSubmit.setText(updateID == -1 ? "Create" : "Update");
        idField.setDisable(updateID != -1);
    }
}
