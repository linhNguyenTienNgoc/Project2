package com.cafe.controller.customer;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.service.CustomerService;
import com.cafe.model.entity.Customer;
import com.cafe.util.AlertUtils;
import com.cafe.util.ValidationUtils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho quản lý khách hàng
 */
public class CustomerController implements Initializable, DashboardCommunicator {

    // Search and Filter
    @FXML private TextField searchField;
    @FXML private Button refreshButton;
    @FXML private Button addCustomerButton;
    @FXML private Button exportButton;

    // Customer Table
    @FXML private TableView<Customer> customerTable;
    @FXML private TableColumn<Customer, Integer> idColumn;
    @FXML private TableColumn<Customer, String> fullNameColumn;
    @FXML private TableColumn<Customer, String> phoneColumn;
    @FXML private TableColumn<Customer, String> emailColumn;
    @FXML private TableColumn<Customer, Integer> loyaltyPointsColumn;
    @FXML private TableColumn<Customer, String> membershipLevelColumn;
    @FXML private TableColumn<Customer, Void> actionsColumn;

    // Statistics
    @FXML private Label totalCustomersLabel;
    @FXML private Label vipCustomersLabel;
    @FXML private Label avgLoyaltyPointsLabel;
    @FXML private Label newCustomersLabel;

    // Form Section
    @FXML private VBox customerFormSection;
    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private TextField loyaltyPointsField;
    @FXML private ComboBox<String> membershipLevelCombo;
    @FXML private Button saveCustomerButton;
    @FXML private Button cancelButton;
    @FXML private Button resetFormButton;

    // Data
    private CustomerService customerService = new CustomerService();
    private ObservableList<Customer> customerList = FXCollections.observableArrayList();
    private Object dashboardController;
    private Customer currentEditingCustomer = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupCustomerTable();
            setupEventHandlers();
            setupValidation();
            loadCustomers();
            updateStatistics();

            System.out.println("✅ CustomerController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing CustomerController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo giao diện quản lý khách hàng: " + e.getMessage());
        }
    }

    private void setupCustomerTable() {
        // Setup table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        loyaltyPointsColumn.setCellValueFactory(new PropertyValueFactory<>("loyaltyPoints"));
        membershipLevelColumn.setCellValueFactory(new PropertyValueFactory<>("membershipLevel"));

        // Setup actions column
        setupActionsColumn();

        // Set table data
        customerTable.setItems(customerList);
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(column -> new TableCell<Customer, Void>() {
            private final Button editButton = new Button("✏️");
            private final Button deleteButton = new Button("🗑️");

            {
                editButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5 10;");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10;");

                editButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    editCustomer(customer);
                });

                deleteButton.setOnAction(event -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    deleteCustomer(customer);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new javafx.scene.layout.HBox(5, editButton, deleteButton));
                }
            }
        });
    }

    private void setupEventHandlers() {
        // Search functionality
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterCustomers(newVal));

        // Button handlers
        refreshButton.setOnAction(e -> refreshData());
        addCustomerButton.setOnAction(e -> showAddCustomerForm());
        exportButton.setOnAction(e -> exportCustomerData());

        // Form buttons
        saveCustomerButton.setOnAction(e -> saveCustomer());
        cancelButton.setOnAction(e -> hideForm());
        resetFormButton.setOnAction(e -> resetForm());
    }

    private void setupValidation() {
        // Setup membership level combo
        membershipLevelCombo.getItems().addAll("Bronze", "Silver", "Gold", "Platinum", "Diamond");
        membershipLevelCombo.setValue("Bronze");

        // Set initial form state
        hideForm();
    }

    private void loadCustomers() {
        try {
            List<Customer> customers = customerService.getAllCustomers();
            customerList.clear();
            customerList.addAll(customers);
            System.out.println("✅ Loaded " + customers.size() + " customers");
        } catch (Exception e) {
            System.err.println("❌ Error loading customers: " + e.getMessage());
            AlertUtils.showError("Lỗi tải dữ liệu", "Không thể tải danh sách khách hàng: " + e.getMessage());
        }
    }

    private void updateStatistics() {
        try {
            int totalCustomers = customerList.size();
            int vipCustomers = (int) customerList.stream()
                    .filter(c -> "Gold".equals(c.getMembershipLevel()) || 
                                "Platinum".equals(c.getMembershipLevel()) || 
                                "Diamond".equals(c.getMembershipLevel()))
                    .count();
            double avgLoyaltyPoints = customerList.stream()
                    .mapToInt(Customer::getLoyaltyPoints)
                    .average()
                    .orElse(0.0);
            int newCustomers = (int) customerList.stream()
                    .filter(c -> "Bronze".equals(c.getMembershipLevel()))
                    .count();

            totalCustomersLabel.setText(String.valueOf(totalCustomers));
            vipCustomersLabel.setText(String.valueOf(vipCustomers));
            avgLoyaltyPointsLabel.setText(String.format("%.0f", avgLoyaltyPoints));
            newCustomersLabel.setText(String.valueOf(newCustomers));
        } catch (Exception e) {
            System.err.println("❌ Error updating statistics: " + e.getMessage());
        }
    }

    private void filterCustomers(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            customerTable.setItems(customerList);
        } else {
            ObservableList<Customer> filteredList = customerList.filtered(customer ->
                    customer.getFullName().toLowerCase().contains(searchText.toLowerCase()) ||
                    customer.getPhone().contains(searchText) ||
                    customer.getEmail().toLowerCase().contains(searchText.toLowerCase())
            );
            customerTable.setItems(filteredList);
        }
    }

    private void showAddCustomerForm() {
        currentEditingCustomer = null;
        resetForm();
        customerFormSection.setVisible(true);
        customerFormSection.setManaged(true);
        saveCustomerButton.setText("➕ Thêm khách hàng");
    }

    private void editCustomer(Customer customer) {
        currentEditingCustomer = customer;
        fillForm(customer);
        customerFormSection.setVisible(true);
        customerFormSection.setManaged(true);
        saveCustomerButton.setText("💾 Cập nhật");
    }

    private void fillForm(Customer customer) {
        fullNameField.setText(customer.getFullName());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
        addressField.setText(customer.getAddress());
        loyaltyPointsField.setText(String.valueOf(customer.getLoyaltyPoints()));
        membershipLevelCombo.setValue(customer.getMembershipLevel());
    }

    private void saveCustomer() {
        try {
            if (!validateForm()) {
                return;
            }

            String fullName = fullNameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();
            String address = addressField.getText().trim();
            int loyaltyPoints = Integer.parseInt(loyaltyPointsField.getText().trim());
            String membershipLevel = membershipLevelCombo.getValue();

            Customer customer;
            if (currentEditingCustomer != null) {
                // Update existing customer
                customer = currentEditingCustomer;
                customer.setFullName(fullName);
                customer.setPhone(phone);
                customer.setEmail(email);
                customer.setAddress(address);
                customer.setLoyaltyPoints(loyaltyPoints);
                customer.setMembershipLevel(membershipLevel);

                if (customerService.updateCustomer(customer)) {
                    AlertUtils.showSuccess("Thành công", "Đã cập nhật thông tin khách hàng");
                } else {
                    AlertUtils.showError("Lỗi", "Không thể cập nhật khách hàng");
                    return;
                }
            } else {
                // Create new customer
                customer = new Customer(0, fullName, phone, email, address, loyaltyPoints, membershipLevel);
                if (customerService.createCustomer(customer)) {
                    AlertUtils.showSuccess("Thành công", "Đã thêm khách hàng mới");
                } else {
                    AlertUtils.showError("Lỗi", "Không thể thêm khách hàng");
                    return;
                }
            }

            loadCustomers();
            updateStatistics();
            hideForm();

        } catch (NumberFormatException e) {
            AlertUtils.showError("Lỗi", "Điểm tích lũy phải là số!");
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Lỗi khi lưu khách hàng: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        if (fullNameField.getText().trim().isEmpty()) {
            AlertUtils.showError("Lỗi", "Vui lòng nhập họ tên khách hàng!");
            return false;
        }
        if (phoneField.getText().trim().isEmpty()) {
            AlertUtils.showError("Lỗi", "Vui lòng nhập số điện thoại!");
            return false;
        }
        if (!ValidationUtils.isValidPhone(phoneField.getText().trim())) {
            AlertUtils.showError("Lỗi", "Số điện thoại không hợp lệ!");
            return false;
        }
        if (!emailField.getText().trim().isEmpty() && !ValidationUtils.isValidEmail(emailField.getText().trim())) {
            AlertUtils.showError("Lỗi", "Email không hợp lệ!");
            return false;
        }
        try {
            Integer.parseInt(loyaltyPointsField.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtils.showError("Lỗi", "Điểm tích lũy phải là số!");
            return false;
        }
        return true;
    }

    private void deleteCustomer(Customer customer) {
        if (AlertUtils.showConfirm("Xác nhận xóa", 
                "Bạn có chắc chắn muốn xóa khách hàng '" + customer.getFullName() + "'?")) {
            try {
                if (customerService.deleteCustomer(customer.getCustomerId())) {
                    AlertUtils.showSuccess("Thành công", "Đã xóa khách hàng");
                    loadCustomers();
                    updateStatistics();
                } else {
                    AlertUtils.showError("Lỗi", "Không thể xóa khách hàng");
                }
            } catch (Exception e) {
                AlertUtils.showError("Lỗi", "Lỗi khi xóa khách hàng: " + e.getMessage());
            }
        }
    }

    private void refreshData() {
        loadCustomers();
        updateStatistics();
        AlertUtils.showInfo("Thông báo", "Đã làm mới dữ liệu");
    }

    private void exportCustomerData() {
        AlertUtils.showInfo("Thông báo", "Tính năng xuất dữ liệu sẽ được cập nhật sau");
    }

    private void hideForm() {
        customerFormSection.setVisible(false);
        customerFormSection.setManaged(false);
        currentEditingCustomer = null;
    }

    private void resetForm() {
        fullNameField.clear();
        phoneField.clear();
        emailField.clear();
        addressField.clear();
        loyaltyPointsField.setText("0");
        membershipLevelCombo.setValue("Bronze");
    }

    // Dashboard Communication
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ CustomerController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}


