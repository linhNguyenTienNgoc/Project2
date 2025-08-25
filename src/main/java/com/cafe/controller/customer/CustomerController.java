package com.cafe.controller.customer;

import com.cafe.model.entity.Customer;
import com.cafe.service.CustomerService;
import com.cafe.util.AlertUtils;
import com.cafe.util.PriceFormatter;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.concurrent.Task;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Customer Management Controller
 * Quản lý CRUD cho khách hàng
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class CustomerController implements Initializable {

    // =====================================================
    // FXML INJECTIONS
    // =====================================================

    // Header components
    @FXML private Label totalCustomersLabel;
    @FXML private Label activeCustomersLabel;
    @FXML private Label vipCustomersLabel;
    
    // Toolbar components
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button refreshButton;
    
    // Table components
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> fullNameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, String> addressColumn;
    @FXML private TableColumn<Customer, String> loyaltyPointsColumn;
    @FXML private TableColumn<Customer, String> totalSpentColumn;
    @FXML private TableColumn<Customer, String> statusColumn;
    @FXML private Label resultCountLabel;
    
    // Form components
    @FXML private VBox customerFormOverlay;
    @FXML private Label formTitleLabel;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button saveCustomerButton;
    @FXML private Button resetFormButton;
    
    // Status components
    @FXML private Label statusLabel;
    @FXML private Label lastUpdateLabel;

    // =====================================================
    // DATA MANAGEMENT
    // =====================================================

    private CustomerService customerService;
    private ObservableList<Customer> customerList;
    private FilteredList<Customer> filteredCustomers;
    private Customer currentEditingCustomer;
    private boolean isEditMode = false;

    // =====================================================
    // INITIALIZATION
    // =====================================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🚀 Initializing CustomerController...");
            
            // Initialize service
            customerService = new CustomerService();
            customerList = FXCollections.observableArrayList();
            filteredCustomers = new FilteredList<>(customerList);
            
            // Setup UI components
            setupTableColumns();
            setupTableBehavior();
            setupFilterComboBoxes();
            setupSearchFunctionality();
            setupButtonStates();
            
            // Load initial data
            loadCustomerData();
            
            // Setup status display
            updateStatusLabels();
            updateLastUpdateTime();
            
            System.out.println("✅ CustomerController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing CustomerController: " + e.getMessage());
            e.printStackTrace();
            showErrorMessage("Lỗi khởi tạo", "Không thể khởi tạo giao diện quản lý khách hàng: " + e.getMessage());
        }
    }

    /**
     * Setup table columns with proper data binding
     */
    private void setupTableColumns() {
        // ID column
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        idColumn.setStyle("-fx-alignment: CENTER;");
        
        // Full name column
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        
        // Phone column
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        phoneColumn.setStyle("-fx-alignment: CENTER;");
        
        // Email column
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        emailColumn.setCellValueFactory(cellData -> {
            String email = cellData.getValue().getEmail();
            return new SimpleStringProperty(email != null ? email : "Chưa có");
        });
        
        // Address column
        addressColumn.setCellValueFactory(cellData -> {
            String address = cellData.getValue().getAddress();
            return new SimpleStringProperty(address != null ? address : "Chưa có");
        });
        
        // Loyalty points column
        loyaltyPointsColumn.setCellValueFactory(cellData -> {
            Integer points = cellData.getValue().getLoyaltyPoints();
            return new SimpleStringProperty(points + " điểm");
        });
        loyaltyPointsColumn.setStyle("-fx-alignment: CENTER;");
        
        // Total spent column
        totalSpentColumn.setCellValueFactory(cellData -> {
            Double amount = cellData.getValue().getTotalSpent();
            return new SimpleStringProperty(PriceFormatter.format(amount));
        });
        totalSpentColumn.setStyle("-fx-alignment: CENTER-RIGHT;");
        
        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            boolean isActive = cellData.getValue().isActive();
            return new SimpleStringProperty(isActive ? "Hoạt động" : "Không hoạt động");
        });
        statusColumn.setStyle("-fx-alignment: CENTER;");
        
        // Set table data
        customerTable.setItems(filteredCustomers);
        
        System.out.println("✅ Table columns configured successfully");
    }

    /**
     * Setup table behavior and styling
     */
    private void setupTableBehavior() {
        // Row factory for inactive customer styling
        customerTable.setRowFactory(tv -> {
            TableRow<Customer> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldCustomer, newCustomer) -> {
                if (newCustomer != null) {
                    // Apply inactive styling for inactive customers
                    if (!newCustomer.isActive()) {
                        row.getStyleClass().add("inactive-customer");
                    } else {
                        row.getStyleClass().remove("inactive-customer");
                    }
                }
            });
            return row;
        });
        
        // Selection listener
        customerTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editButton.setDisable(!hasSelection);
            deleteButton.setDisable(!hasSelection);
        });
        
        // Double-click to edit
        customerTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && customerTable.getSelectionModel().getSelectedItem() != null) {
                handleEditCustomer();
            }
        });
        
        System.out.println("✅ Table behavior configured successfully");
    }

    /**
     * Setup filter combo boxes
     */
    private void setupFilterComboBoxes() {
        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "Hoạt động", "Không hoạt động"
        ));
        statusFilterCombo.setValue("Tất cả");
        statusFilterCombo.setOnAction(e -> applyFilters());
        
        // Form status combo
        statusCombo.setItems(FXCollections.observableArrayList(
            "Hoạt động", "Không hoạt động"
        ));
        statusCombo.setValue("Hoạt động");
        
        System.out.println("✅ Filter combo boxes configured successfully");
    }

    /**
     * Setup search functionality
     */
    private void setupSearchFunctionality() {
        searchField.textProperty().addListener((obs, oldValue, newValue) -> applyFilters());
        
        System.out.println("✅ Search functionality configured successfully");
    }

    /**
     * Setup initial button states
     */
    private void setupButtonStates() {
        editButton.setDisable(true);
        deleteButton.setDisable(true);
    }

    // =====================================================
    // DATA OPERATIONS
    // =====================================================

    /**
     * Load customer data from service
     */
    private void loadCustomerData() {
        try {
            updateStatusLabel("Đang tải dữ liệu khách hàng...", "info");
            
            // Create background task
            Task<List<Customer>> loadTask = new Task<List<Customer>>() {
                @Override
                protected List<Customer> call() throws Exception {
                    return customerService.getAllCustomers();
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        try {
                            List<Customer> customers = getValue();
                            customerList.setAll(customers);
                            updateResultCount();
                            updateStatistics();
                            updateStatusLabel("✅ Dữ liệu đã được tải thành công", "success");
                            updateLastUpdateTime();
                            
                            System.out.println("✅ Loaded " + customers.size() + " customers successfully");
                        } catch (Exception e) {
                            handleLoadError("Lỗi xử lý dữ liệu", e);
                        }
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        handleLoadError("Lỗi tải dữ liệu", exception);
                    });
                }
            };
            
            // Run task in background
            Thread loadThread = new Thread(loadTask);
            loadThread.setDaemon(true);
            loadThread.start();
            
        } catch (Exception e) {
            handleLoadError("Lỗi hệ thống", e);
        }
    }

    /**
     * Handle data loading errors
     */
    private void handleLoadError(String title, Throwable error) {
        String message = "Không thể tải dữ liệu khách hàng: " + 
                        (error != null ? error.getMessage() : "Lỗi không xác định");
        System.err.println("❌ " + title + ": " + message);
        
        updateStatusLabel("❌ Lỗi tải dữ liệu", "error");
        showErrorMessage(title, message);
    }

    /**
     * Apply search and filter
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase().trim();
        String statusFilter = statusFilterCombo.getValue();
        
        filteredCustomers.setPredicate(customer -> {
            if (customer == null) return false;
            
            // Search filter (name or phone)
            boolean matchesSearch = searchText.isEmpty() || 
                                  (customer.getFullName() != null && customer.getFullName().toLowerCase().contains(searchText)) ||
                                  (customer.getPhone() != null && customer.getPhone().contains(searchText));
            
            // Status filter
            boolean matchesStatus = "Tất cả".equals(statusFilter) ||
                                  ("Hoạt động".equals(statusFilter) && customer.isActive()) ||
                                  ("Không hoạt động".equals(statusFilter) && !customer.isActive());
            
            return matchesSearch && matchesStatus;
        });
        
        updateResultCount();
    }

    /**
     * Update result count display
     */
    private void updateResultCount() {
        int total = customerList.size();
        int filtered = filteredCustomers.size();
        
        if (filtered == total) {
            resultCountLabel.setText("Hiển thị " + total + " khách hàng");
        } else {
            resultCountLabel.setText("Hiển thị " + filtered + "/" + total + " khách hàng");
        }
    }

    /**
     * Update statistics labels
     */
    private void updateStatistics() {
        int total = customerList.size();
        int active = (int) customerList.stream().filter(Customer::isActive).count();
        int vip = (int) customerList.stream().filter(Customer::isVIP).count();
        
        totalCustomersLabel.setText(String.valueOf(total));
        activeCustomersLabel.setText(String.valueOf(active));
        vipCustomersLabel.setText(String.valueOf(vip));
    }

    // =====================================================
    // EVENT HANDLERS
    // =====================================================

    @FXML
    private void handleAddCustomer() {
        try {
            System.out.println("🆕 Opening add customer form");
            
            isEditMode = false;
            currentEditingCustomer = null;
            formTitleLabel.setText("✨ Thêm khách hàng mới");
            
            clearForm();
            showCustomerForm();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening add form: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể mở form thêm khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleEditCustomer() {
        try {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null) {
                showWarningMessage("Chưa chọn khách hàng", "Vui lòng chọn khách hàng cần sửa.");
                return;
            }
            
            System.out.println("✏️ Opening edit form for customer: " + selectedCustomer.getFullName());
            
            isEditMode = true;
            currentEditingCustomer = selectedCustomer;
            formTitleLabel.setText("✏️ Chỉnh sửa khách hàng");
            
            populateForm(selectedCustomer);
            showCustomerForm();
            
        } catch (Exception e) {
            System.err.println("❌ Error opening edit form: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể mở form sửa khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCustomer() {
        try {
            Customer selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
            if (selectedCustomer == null) {
                showWarningMessage("Chưa chọn khách hàng", "Vui lòng chọn khách hàng cần xóa.");
                return;
            }
            
            String customerInfo = selectedCustomer.getFullName() + " (" + selectedCustomer.getPhone() + ")";
            
            // Confirm deletion
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Xác nhận xóa");
            confirmAlert.setHeaderText("Xóa khách hàng: " + customerInfo);
            confirmAlert.setContentText("Bạn có chắc chắn muốn xóa khách hàng này? Hành động này không thể hoàn tác.");
            
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                
                System.out.println("🗑️ Deleting customer: " + customerInfo);
                
                updateStatusLabel("Đang xóa khách hàng...", "info");
                
                // Delete in background
                Task<Boolean> deleteTask = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        return customerService.deleteCustomer(selectedCustomer.getCustomerId());
                    }
                    
                    @Override
                    protected void succeeded() {
                        Platform.runLater(() -> {
                            if (getValue()) {
                                customerList.remove(selectedCustomer);
                                updateResultCount();
                                updateStatistics();
                                updateStatusLabel("✅ Đã xóa khách hàng thành công", "success");
                                updateLastUpdateTime();
                                showSuccessMessage("Thành công", "Đã xóa khách hàng: " + customerInfo);
                                System.out.println("✅ Customer deleted successfully: " + customerInfo);
                            } else {
                                updateStatusLabel("❌ Lỗi xóa khách hàng", "error");
                                showErrorMessage("Lỗi", "Không thể xóa khách hàng. Vui lòng thử lại.");
                            }
                        });
                    }
                    
                    @Override
                    protected void failed() {
                        Platform.runLater(() -> {
                            Throwable exception = getException();
                            String error = exception != null ? exception.getMessage() : "Lỗi không xác định";
                            updateStatusLabel("❌ Lỗi xóa khách hàng", "error");
                            showErrorMessage("Lỗi xóa", "Không thể xóa khách hàng: " + error);
                        });
                    }
                };
                
                Thread deleteThread = new Thread(deleteTask);
                deleteThread.setDaemon(true);
                deleteThread.start();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error deleting customer: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể xóa khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefresh() {
        try {
            System.out.println("🔄 Refreshing customer data");
            loadCustomerData();
        } catch (Exception e) {
            System.err.println("❌ Error refreshing data: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể làm mới dữ liệu: " + e.getMessage());
        }
    }

    @FXML
    private void handleSaveCustomer() {
        try {
            if (!validateForm()) {
                return;
            }
            
            Customer customer = createCustomerFromForm();
            String action = isEditMode ? "Cập nhật" : "Thêm";
            
            System.out.println("💾 " + action + " customer: " + customer.getFullName());
            
            updateStatusLabel("Đang lưu thông tin khách hàng...", "info");
            
            // Save in background
            Task<Boolean> saveTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    if (isEditMode) {
                        customer.setCustomerId(currentEditingCustomer.getCustomerId());
                        return customerService.updateCustomer(customer);
                    } else {
                        return customerService.createCustomer(customer);
                    }
                }
                
                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        if (getValue()) {
                            hideCustomerForm();
                            loadCustomerData(); // Reload to get updated data
                            showSuccessMessage("Thành công", action + " khách hàng thành công!");
                            System.out.println("✅ Customer " + action.toLowerCase() + " successfully");
                        } else {
                            updateStatusLabel("❌ Lỗi lưu khách hàng", "error");
                            showErrorMessage("Lỗi", "Không thể " + action.toLowerCase() + " khách hàng. Vui lòng thử lại.");
                        }
                    });
                }
                
                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        Throwable exception = getException();
                        String error = exception != null ? exception.getMessage() : "Lỗi không xác định";
                        updateStatusLabel("❌ Lỗi lưu khách hàng", "error");
                        showErrorMessage("Lỗi " + action.toLowerCase(), "Không thể " + action.toLowerCase() + " khách hàng: " + error);
                    });
                }
            };
            
            Thread saveThread = new Thread(saveTask);
            saveThread.setDaemon(true);
            saveThread.start();
            
        } catch (Exception e) {
            System.err.println("❌ Error saving customer: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể lưu khách hàng: " + e.getMessage());
        }
    }

    @FXML
    private void handleResetForm() {
        try {
            if (isEditMode && currentEditingCustomer != null) {
                populateForm(currentEditingCustomer);
            } else {
                clearForm();
            }
            System.out.println("🔄 Form reset");
        } catch (Exception e) {
            System.err.println("❌ Error resetting form: " + e.getMessage());
            showErrorMessage("Lỗi", "Không thể reset form: " + e.getMessage());
        }
    }

    @FXML
    private void hideCustomerForm() {
        customerFormOverlay.setVisible(false);
        customerFormOverlay.setManaged(false);
        System.out.println("📝 Customer form hidden");
    }

    // =====================================================
    // FORM OPERATIONS
    // =====================================================

    /**
     * Show customer form
     */
    private void showCustomerForm() {
        customerFormOverlay.setVisible(true);
        customerFormOverlay.setManaged(true);
        fullNameField.requestFocus();
        System.out.println("📝 Customer form shown");
    }

    /**
     * Clear form fields
     */
    private void clearForm() {
        fullNameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        statusCombo.setValue("Hoạt động");
    }

    /**
     * Populate form with customer data
     */
    private void populateForm(Customer customer) {
        fullNameField.setText(customer.getFullName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail() != null ? customer.getEmail() : "");
        addressField.setText(customer.getAddress() != null ? customer.getAddress() : "");
        statusCombo.setValue(customer.isActive() ? "Hoạt động" : "Không hoạt động");
    }

    /**
     * Create customer object from form data
     */
    private Customer createCustomerFromForm() {
        Customer customer = new Customer();
        customer.setFullName(fullNameField.getText().trim());
        customer.setPhone(phoneField.getText().trim());
        
        String email = emailField.getText().trim();
        customer.setEmail(email.isEmpty() ? null : email);
        
        String address = addressField.getText().trim();
        customer.setAddress(address.isEmpty() ? null : address);
        
        customer.setActive("Hoạt động".equals(statusCombo.getValue()));
        
        return customer;
    }

    /**
     * Validate form data
     */
    private boolean validateForm() {
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        if (fullName.isEmpty()) {
            showWarningMessage("Thiếu thông tin", "Vui lòng nhập họ tên khách hàng.");
            fullNameField.requestFocus();
            return false;
        }
        
        if (phone.isEmpty()) {
            showWarningMessage("Thiếu thông tin", "Vui lòng nhập số điện thoại.");
            phoneField.requestFocus();
            return false;
        }
        
        if (!phone.matches("^[0-9+\\-\\s()]+$")) {
            showWarningMessage("Số điện thoại không hợp lệ", "Số điện thoại chỉ được chứa số, dấu +, -, khoảng trắng và dấu ngoặc.");
            phoneField.requestFocus();
            return false;
        }
        
        if (!email.isEmpty() && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            showWarningMessage("Email không hợp lệ", "Vui lòng nhập email đúng định dạng.");
            emailField.requestFocus();
            return false;
        }
        
        return true;
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Update status label
     */
    private void updateStatusLabel(String message, String type) {
        statusLabel.setText(message);
        statusLabel.getStyleClass().removeAll("status-ready", "status-error", "status-info");
        
        switch (type) {
            case "success":
                statusLabel.getStyleClass().add("status-ready");
                break;
            case "error":
                statusLabel.getStyleClass().add("status-error");
                break;
            case "info":
            default:
                statusLabel.getStyleClass().add("status-info");
                break;
        }
    }

    /**
     * Update status labels
     */
    private void updateStatusLabels() {
        updateStatusLabel("✅ Sẵn sàng", "success");
    }

    /**
     * Update last update time
     */
    private void updateLastUpdateTime() {
        lastUpdateLabel.setText("Cập nhật lần cuối: " + 
                              java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
    }

    /**
     * Show success message
     */
    private void showSuccessMessage(String title, String message) {
        AlertUtils.showSuccess(title, message);
    }

    /**
     * Show error message
     */
    private void showErrorMessage(String title, String message) {
        AlertUtils.showError(title, message);
    }

    /**
     * Show warning message
     */
    private void showWarningMessage(String title, String message) {
        AlertUtils.showWarning(title, message);
    }
}