package com.cafe.controller.admin;

import com.cafe.CafeManagementApplication;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.UserDAO;
import com.cafe.dao.base.UserDAOImpl;
import com.cafe.model.entity.User;
import com.cafe.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình dashboard của Admin
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class AdminDashboardController implements Initializable {
    
    // Navigation Buttons
    @FXML private Button employeeTab;
    @FXML private Button menuTab;
    @FXML private Button tableTab;
    @FXML private Button reportTab;
    @FXML private Button promotionTab;
    @FXML private Button logoutButton;
    
    // Table Components
    @FXML private TableView<User> employeeTable;
    @FXML private TableColumn<User, String> employeeIdColumn;
    @FXML private TableColumn<User, String> usernameColumn;
    @FXML private TableColumn<User, String> employeeNameColumn;
    @FXML private TableColumn<User, String> dobColumn;
    @FXML private TableColumn<User, String> genderColumn;
    @FXML private TableColumn<User, String> workShiftColumn;
    @FXML private TableColumn<User, Void> actionColumn;
    
    // Pagination Components
    @FXML private Button firstPageBtn;
    @FXML private Button prevPageBtn;
    @FXML private Button nextPageBtn;
    @FXML private Button lastPageBtn;
    @FXML private Label currentPageLabel;
    @FXML private Label totalPagesLabel;
    
    // Action Buttons
    @FXML private Button addEmployeeBtn;
    
    // Data
    private UserDAO userDAO;
    private ObservableList<User> employeeList;
    private int currentPage = 1;
    private int totalPages = 1;
    private int pageSize = 10;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupDatabase();
        setupTable();
        setupPagination();
        loadEmployees();
        checkUserPermissions();
    }
    
    /**
     * Khởi tạo kết nối database
     */
    private void setupDatabase() {
        try {
            Connection connection = DatabaseConfig.getConnection();
            userDAO = new UserDAOImpl(connection);
            System.out.println("✅ Admin Dashboard: Database connection established");
        } catch (Exception e) {
            showError("Lỗi kết nối database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Thiết lập bảng nhân viên
     */
    private void setupTable() {
        // Khởi tạo danh sách
        employeeList = FXCollections.observableArrayList();
        
        // Thiết lập các cột
        employeeIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        employeeNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("email")); // Tạm thời dùng email thay cho dateOfBirth
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("phone")); // Tạm thời dùng phone thay cho gender
        workShiftColumn.setCellValueFactory(new PropertyValueFactory<>("roleId")); // Tạm thời dùng roleId thay cho workShift
        
        // Thiết lập cột action
        setupActionColumn();
        
        // Gán dữ liệu cho bảng
        employeeTable.setItems(employeeList);
        
        // Thiết lập selection listener
        employeeTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Có thể thêm logic xử lý khi chọn nhân viên
                }
            }
        );
    }
    
    /**
     * Thiết lập cột action với nút Sửa
     */
    private void setupActionColumn() {
        actionColumn.setCellFactory(param -> new TableCell<User, Void>() {
            private final Button editButton = new Button("Sửa");
            
            {
                editButton.setStyle("-fx-background-color: #5d2b1c; -fx-text-fill: #fff9c4; -fx-background-radius: 5; -fx-padding: 5 10;");
                editButton.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    handleEditEmployee(user);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editButton);
                }
            }
        });
    }
    
    /**
     * Thiết lập phân trang
     */
    private void setupPagination() {
        updatePaginationControls();
    }
    
    /**
     * Tải danh sách nhân viên
     */
    private void loadEmployees() {
        try {
            // Tính toán offset cho phân trang
            int offset = (currentPage - 1) * pageSize;
            
            // Lấy danh sách nhân viên từ database
            List<User> users = userDAO.getAllUsers(offset, pageSize);
            int totalUsers = userDAO.getTotalUserCount();
            
            // Cập nhật danh sách
            employeeList.clear();
            employeeList.addAll(users);
            
            // Cập nhật phân trang
            totalPages = (int) Math.ceil((double) totalUsers / pageSize);
            updatePaginationControls();
            
            System.out.println("✅ Loaded " + users.size() + " employees (Page " + currentPage + "/" + totalPages + ")");
            
        } catch (Exception e) {
            showError("Lỗi khi tải danh sách nhân viên: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Cập nhật điều khiển phân trang
     */
    private void updatePaginationControls() {
        currentPageLabel.setText(String.valueOf(currentPage));
        totalPagesLabel.setText(totalPages + " trang");
        
        // Cập nhật trạng thái các nút
        firstPageBtn.setDisable(currentPage == 1);
        prevPageBtn.setDisable(currentPage == 1);
        nextPageBtn.setDisable(currentPage == totalPages);
        lastPageBtn.setDisable(currentPage == totalPages);
    }
    
    /**
     * Kiểm tra quyền người dùng
     */
    private void checkUserPermissions() {
        User currentUser = SessionManager.getCurrentUser();
        if (currentUser == null || !"Admin".equalsIgnoreCase(currentUser.getRole())) {
            showError("Bạn không có quyền truy cập màn hình này!");
            handleLogout(null);
        }
    }
    
    // ========== NAVIGATION HANDLERS ==========
    
    @FXML
    private void handleEmployeeTab(ActionEvent event) {
        setActiveTab(employeeTab);
        // Đã ở tab nhân viên rồi, không cần làm gì thêm
    }
    
    @FXML
    private void handleMenuTab(ActionEvent event) {
        setActiveTab(menuTab);
        showInfo("Tính năng quản lý Menu đang được phát triển!");
    }
    
    @FXML
    private void handleTableTab(ActionEvent event) {
        setActiveTab(tableTab);
        showInfo("Tính năng quản lý Bàn đang được phát triển!");
    }
    
    @FXML
    private void handleReportTab(ActionEvent event) {
        setActiveTab(reportTab);
        showInfo("Tính năng Báo cáo đang được phát triển!");
    }
    
    @FXML
    private void handlePromotionTab(ActionEvent event) {
        setActiveTab(promotionTab);
        showInfo("Tính năng Khuyến mãi đang được phát triển!");
    }
    
    /**
     * Đặt tab active
     */
    private void setActiveTab(Button activeTab) {
        // Reset tất cả tabs
        Button[] tabs = {employeeTab, menuTab, tableTab, reportTab, promotionTab};
        for (Button tab : tabs) {
            tab.setStyle("-fx-background-color: transparent; -fx-text-fill: #fff9c4; -fx-background-radius: 5; -fx-padding: 8 15;");
        }
        
        // Đặt tab được chọn
        activeTab.setStyle("-fx-background-color: #f5d6ae; -fx-text-fill: #4e342e; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
    }
    
    // ========== PAGINATION HANDLERS ==========
    
    @FXML
    private void handleFirstPage(ActionEvent event) {
        if (currentPage != 1) {
            currentPage = 1;
            loadEmployees();
        }
    }
    
    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            loadEmployees();
        }
    }
    
    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            loadEmployees();
        }
    }
    
    @FXML
    private void handleLastPage(ActionEvent event) {
        if (currentPage != totalPages) {
            currentPage = totalPages;
            loadEmployees();
        }
    }
    
    // ========== EMPLOYEE MANAGEMENT HANDLERS ==========
    
    @FXML
    private void handleAddEmployee(ActionEvent event) {
        showInfo("Tính năng thêm nhân viên đang được phát triển!");
    }
    
    private void handleEditEmployee(User user) {
        if (user != null) {
            showInfo("Đang chỉnh sửa nhân viên: " + user.getFullName());
            // TODO: Mở dialog chỉnh sửa nhân viên
        }
    }
    
    // ========== UTILITY HANDLERS ==========
    
    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        CafeManagementApplication.showLoginScreen();
    }
    
    // ========== UTILITY METHODS ==========
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 