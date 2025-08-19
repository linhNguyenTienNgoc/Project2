package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.CategoryDAO;
import com.cafe.dao.base.CategoryDAOImpl;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;
import com.cafe.service.MenuService;
import com.cafe.util.AlertUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho Admin Menu Management
 * Sử dụng TableView để hiển thị và quản lý menu items
 */
public class AdminMenuController implements Initializable, DashboardCommunicator {

    // Search and Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private Button refreshButton;

    // Table Components
    @FXML private TableView<Product> menuTable;
    @FXML private TableColumn<Product, Integer> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> categoryColumn;
    @FXML private TableColumn<Product, Double> priceColumn;
    @FXML private TableColumn<Product, String> stockColumn;
    @FXML private TableColumn<Product, String> statusColumn;
    @FXML private TableColumn<Product, Void> actionColumn;

    // Form Components
    @FXML private TextField productNameField;
    @FXML private ComboBox<Category> categoryCombo;
    @FXML private TextField priceField;
    @FXML private TextField descriptionField;
    @FXML private Button addButton;
    @FXML private Button updateButton;
    @FXML private Button clearButton;

    // Data
    private ObservableList<Product> productList = FXCollections.observableArrayList();
    private ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private MenuService menuService;
    private Product currentEditingProduct = null;
    
    // Dashboard communication
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize service
            menuService = new MenuService();
            
            // Setup table
            setupTable();
            setupForm();
            setupEventHandlers();
            
            // Load data
            loadCategories();
            loadProducts();
            
            System.out.println("✅ AdminMenuController initialized successfully");
        } catch (Exception e) {
            System.err.println("❌ Error initializing AdminMenuController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo quản lý menu: " + e.getMessage());
        }
    }

    private void setupTable() {
        // Setup table columns
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        
        // Category column - hiển thị tên category
        categoryColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String categoryName = getCategoryName(product.getCategoryId());
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });
        
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        // Stock column - tạm thời hiển thị "Có sẵn"
        stockColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty("Có sẵn"));
        
        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String status = product.isActive() ? "Hoạt động" : "Ngừng bán";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
        
        // Action column - tạm thời để trống
        // TODO: Thêm edit/delete buttons nếu cần
        
        menuTable.setItems(productList);
        
        // Selection handler
        menuTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                loadProductToForm(newSelection);
            }
        });
    }

    private void setupForm() {
        // Setup category combo
        categoryCombo.setItems(categoryList);
        categoryCombo.setConverter(new javafx.util.StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getCategoryName() : "";
            }

            @Override
            public Category fromString(String string) {
                return categoryList.stream()
                    .filter(cat -> cat.getCategoryName().equals(string))
                    .findFirst().orElse(null);
            }
        });
        
        // Setup filter combo
        if (categoryFilter != null) {
            categoryFilter.setItems(categoryList);
            categoryFilter.setConverter(categoryCombo.getConverter());
        }
    }

    private void setupEventHandlers() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> refreshData());
        }
        
        if (addButton != null) {
            addButton.setOnAction(e -> addProduct());
        }
        
        if (updateButton != null) {
            updateButton.setOnAction(e -> updateProduct());
        }
        
        if (clearButton != null) {
            clearButton.setOnAction(e -> clearForm());
        }
        
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterProducts();
            });
        }
        
        if (categoryFilter != null) {
            categoryFilter.setOnAction(e -> filterProducts());
        }
    }

    private void loadCategories() {
        Task<List<Category>> loadTask = new Task<List<Category>>() {
            @Override
            protected List<Category> call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    CategoryDAO dao = new CategoryDAOImpl(connection);
                    return dao.getAllCategories();
                }
            }

            @Override
            protected void succeeded() {
                categoryList.clear();
                if (getValue() != null) {
                    categoryList.addAll(getValue());
                }
                System.out.println("✅ Loaded " + categoryList.size() + " categories");
            }

            @Override
            protected void failed() {
                System.err.println("❌ Failed to load categories: " + getException().getMessage());
                AlertUtils.showError("Lỗi", "Không thể tải danh mục");
            }
        };

        new Thread(loadTask).start();
    }

    private void loadProducts() {
        Task<List<Product>> loadTask = new Task<List<Product>>() {
            @Override
            protected List<Product> call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    return dao.findAll();
                }
            }

            @Override
            protected void succeeded() {
                productList.clear();
                if (getValue() != null) {
                    productList.addAll(getValue());
                }
                System.out.println("✅ Loaded " + productList.size() + " products");
            }

            @Override
            protected void failed() {
                System.err.println("❌ Failed to load products: " + getException().getMessage());
                AlertUtils.showError("Lỗi", "Không thể tải sản phẩm");
            }
        };

        new Thread(loadTask).start();
    }

    private void refreshData() {
        loadCategories();
        loadProducts();
        clearForm();
    }

    private void addProduct() {
        if (!validateForm()) return;
        
        Product product = createProductFromForm();
        
        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    return dao.save(product);
                }
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    AlertUtils.showInfo("Thành công", "Đã thêm sản phẩm mới");
                    loadProducts();
                    clearForm();
                } else {
                    AlertUtils.showError("Lỗi", "Không thể thêm sản phẩm");
                }
            }

            @Override
            protected void failed() {
                AlertUtils.showError("Lỗi", "Lỗi khi thêm sản phẩm: " + getException().getMessage());
            }
        };

        new Thread(saveTask).start();
    }

    private void updateProduct() {
        if (currentEditingProduct == null || !validateForm()) return;
        
        Product product = createProductFromForm();
        product.setProductId(currentEditingProduct.getProductId());
        
        Task<Boolean> updateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    return dao.update(product);
                }
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    AlertUtils.showInfo("Thành công", "Đã cập nhật sản phẩm");
                    loadProducts();
                    clearForm();
                } else {
                    AlertUtils.showError("Lỗi", "Không thể cập nhật sản phẩm");
                }
            }

            @Override
            protected void failed() {
                AlertUtils.showError("Lỗi", "Lỗi khi cập nhật sản phẩm: " + getException().getMessage());
            }
        };

        new Thread(updateTask).start();
    }

    private boolean validateForm() {
        if (productNameField.getText().trim().isEmpty()) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng nhập tên sản phẩm");
            return false;
        }
        
        if (categoryCombo.getValue() == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn danh mục");
            return false;
        }
        
        try {
            Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Cảnh báo", "Giá sản phẩm không hợp lệ");
            return false;
        }
        
        return true;
    }

    private Product createProductFromForm() {
        Product product = new Product();
        product.setProductName(productNameField.getText().trim());
        product.setCategoryId(categoryCombo.getValue().getCategoryId());
        product.setPrice(Double.parseDouble(priceField.getText().trim()));
        product.setDescription(descriptionField.getText().trim());
        product.setAvailable(true);
        product.setActive(true);
        return product;
    }

    private void loadProductToForm(Product product) {
        currentEditingProduct = product;
        productNameField.setText(product.getProductName());
        priceField.setText(String.valueOf(product.getPrice()));
        descriptionField.setText(product.getDescription());
        
        // Find and select category
        Category category = categoryList.stream()
            .filter(cat -> cat.getCategoryId() == product.getCategoryId())
            .findFirst().orElse(null);
        categoryCombo.setValue(category);
    }

    private void clearForm() {
        currentEditingProduct = null;
        productNameField.clear();
        priceField.clear();
        descriptionField.clear();
        categoryCombo.setValue(null);
    }

    private void filterProducts() {
        // TODO: Implement filtering logic
        // For now, just refresh the data
        loadProducts();
    }

    private String getCategoryName(int categoryId) {
        return categoryList.stream()
            .filter(cat -> cat.getCategoryId() == categoryId)
            .findFirst()
            .map(Category::getCategoryName)
            .orElse("N/A");
    }

    // Dashboard Communication
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminMenuController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}


