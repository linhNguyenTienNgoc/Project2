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
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.StringConverter;
import javafx.scene.input.MouseEvent;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Complete AdminMenuController with Full Screen Responsive Layout
 * 
 * Features:
 * - Full width table layout
 * - Responsive column management
 * - Complete CRUD operations
 * - Real-time search and filtering
 * - Image management
 * - Form validation
 * 
 * @author Team 2_C2406L
 * @version 3.0.0
 */
public class AdminMenuController implements Initializable, DashboardCommunicator {

    // =============================================
    // FXML COMPONENTS - TOP NAVIGATION
    // =============================================
    @FXML private TextField searchField;
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;

    // =============================================
    // FXML COMPONENTS - TOOLBAR
    // =============================================
    @FXML private Button addProductButton;
    @FXML private Button editProductButton;
    @FXML private Button deleteProductButton;
    @FXML private ComboBox<Category> categoryFilter;
    @FXML private ComboBox<String> statusFilter;

    // =============================================
    // FXML COMPONENTS - PRODUCTS TABLE
    // =============================================
    @FXML private TableView<Product> productsTable;
    @FXML private TableColumn<Product, Integer> productIdColumn;
    @FXML private TableColumn<Product, String> productNameColumn;
    @FXML private TableColumn<Product, String> categoryNameColumn;
    @FXML private TableColumn<Product, Double> sellingPriceColumn;
    @FXML private TableColumn<Product, Double> costPriceColumn;
    @FXML private TableColumn<Product, String> statusColumn;

    // =============================================
    // FXML COMPONENTS - TABLE FOOTER
    // =============================================
    @FXML private Label totalProductsLabel;
    @FXML private Label activeProductsLabel;
    @FXML private Label inactiveProductsLabel;
    @FXML private Label pageInfoLabel;
    @FXML private Button previousPageButton;
    @FXML private Button nextPageButton;

    // =============================================
    // FXML COMPONENTS - RIGHT PANEL PREVIEW
    // =============================================
    @FXML private ImageView productImageView;
    @FXML private Button changeImageButton;
    @FXML private Label previewProductName;
    @FXML private Label previewProductId;
    @FXML private Label previewCategory;
    @FXML private Label previewSellingPrice;
    @FXML private Label previewCostPrice;
    @FXML private Label previewStatus;
    @FXML private TextArea previewDescription;
    @FXML private Label previewCreatedDate;
    @FXML private Button quickEditButton;
    @FXML private Button toggleStatusButton;
    @FXML private Button duplicateProductButton;

    // =============================================
    // FXML COMPONENTS - FORM DIALOG
    // =============================================
    @FXML private VBox productFormDialog;
    @FXML private StackPane dialogOverlay;
    @FXML private Label dialogTitle;
    @FXML private Button closeDialogButton;
    @FXML private TextField productNameField;
    @FXML private ComboBox<Category> categoryComboBox;
    @FXML private TextArea descriptionField;
    @FXML private TextField sellingPriceField;
    @FXML private TextField costPriceField;
    @FXML private ImageView formProductImageView;
    @FXML private Button selectImageButton;
    @FXML private TextField imageUrlField;
    @FXML private CheckBox isAvailableCheckBox;
    @FXML private Button cancelFormButton;
    @FXML private Button resetFormButton;
    @FXML private Button saveProductButton;

    // =============================================
    // DATA COLLECTIONS
    // =============================================
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;

    // =============================================
    // SERVICES & STATE
    // =============================================
    private MenuService menuService;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private Product currentEditingProduct = null;
    private Object dashboardController;
    private Stage currentDialogStage = null;

    // =============================================
    // PAGINATION & FORMATTING
    // =============================================
    private int currentPage = 1;
    private final int itemsPerPage = 20;
    private int totalPages = 1;
    private final DecimalFormat priceFormatter = new DecimalFormat("#,###");
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    // =============================================
    // RESPONSIVE LAYOUT CONSTANTS
    // =============================================
    private static final double RIGHT_PANEL_WIDTH = 280.0;
    private static final double PADDING_MARGIN = 40.0;
    private static final double SCROLLBAR_WIDTH = 15.0;
    private static final double SMALL_SCREEN_THRESHOLD = 700.0;
    private static final double MEDIUM_SCREEN_THRESHOLD = 900.0;

    // =============================================
    // INITIALIZATION
    // =============================================

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🚀 Initializing AdminMenuController...");
            
            // Initialize core components
            initializeServices();
            setupTable();
            setupFilters();
            setupEventHandlers();
            setupFormDialog();
            
            // Load initial data
            loadInitialData();
            
            // Setup full screen layout
            Platform.runLater(this::setupFullScreenLayout);
            
            // FIXED: Add cleanup on application close
            Platform.runLater(() -> {
                if (productsTable != null && productsTable.getScene() != null && productsTable.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) productsTable.getScene().getWindow();
                    stage.setOnCloseRequest(event -> cleanupResources());
                }
            });
            
            System.out.println("✅ AdminMenuController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing AdminMenuController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo quản lý menu: " + e.getMessage());
        }
    }

    private void initializeServices() {
        System.out.println("🔧 Initializing services...");
        menuService = new MenuService();
        try (Connection connection = DatabaseConfig.getConnection()) {
            productDAO = new ProductDAOImpl(connection);
            categoryDAO = new CategoryDAOImpl(connection);
            System.out.println("✅ Services initialized successfully");
        } catch (Exception e) {
            throw new RuntimeException("Cannot initialize DAO services", e);
        }
    }

    // =============================================
    // TABLE SETUP AND RESPONSIVE CONFIGURATION
    // =============================================

    private void setupTable() {
        System.out.println("📊 Setting up table...");
        
        // Setup basic column value factories
        setupColumnValueFactories();
        
        // Setup cell factories for formatting
        setupCellFactories();
        
        // Configure responsive table behavior
        configureResponsiveTable();
        
        // Setup data binding
        setupTableDataBinding();
        
        System.out.println("✅ Table setup complete");
    }

    private void setupColumnValueFactories() {
        productIdColumn.setCellValueFactory(new PropertyValueFactory<>("productId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        
        // Category column - display category name
        categoryNameColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String categoryName = getCategoryName(product.getCategoryId());
            return new javafx.beans.property.SimpleStringProperty(categoryName);
        });
        
        // Price columns
        sellingPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        costPriceColumn.setCellValueFactory(new PropertyValueFactory<>("costPrice"));
        
        // Status column
        statusColumn.setCellValueFactory(cellData -> {
            Product product = cellData.getValue();
            String status = product.isAvailable() ? "Đang bán" : "Ngừng bán";
            return new javafx.beans.property.SimpleStringProperty(status);
        });
    }

    private void setupCellFactories() {
        // Price column formatting
        sellingPriceColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(priceFormatter.format(price) + " đ");
                }
            }
        });

        costPriceColumn.setCellFactory(column -> new TableCell<Product, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(priceFormatter.format(price) + " đ");
                }
            }
        });
        
        // Status column styling
        statusColumn.setCellFactory(column -> new TableCell<Product, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(status);
                    if ("Đang bán".equals(status)) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                    }
                }
            }
        });
    }

    private void configureResponsiveTable() {
        // Force table to use constrained resize policy
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Remove explicit width/height constraints that might conflict
        productsTable.setPrefWidth(Region.USE_COMPUTED_SIZE);
        productsTable.setPrefHeight(Region.USE_COMPUTED_SIZE);
        
        // Setup column constraints
        setupColumnConstraints();
        
        // Setup responsive listeners
        setupResponsiveListeners();
        
        // Force refresh on layout changes
        productsTable.layoutBoundsProperty().addListener((obs, oldBounds, newBounds) -> 
            Platform.runLater(this::forceTableRefresh));
    }

    private void setupColumnConstraints() {
        // ID Column - 8%
        productIdColumn.setResizable(true);
        productIdColumn.setMinWidth(50);
        productIdColumn.setPrefWidth(60);
        productIdColumn.setMaxWidth(100);
        
        // Product Name Column - 35% (Main content)
        productNameColumn.setResizable(true);
        productNameColumn.setMinWidth(180);
        productNameColumn.setPrefWidth(300);
        productNameColumn.setMaxWidth(Region.USE_COMPUTED_SIZE);
        
        // Category Column - 15%
        categoryNameColumn.setResizable(true);
        categoryNameColumn.setMinWidth(100);
        categoryNameColumn.setPrefWidth(120);
        categoryNameColumn.setMaxWidth(180);
        
        // Selling Price Column - 20%
        sellingPriceColumn.setResizable(true);
        sellingPriceColumn.setMinWidth(100);
        sellingPriceColumn.setPrefWidth(120);
        sellingPriceColumn.setMaxWidth(150);
        
        // Cost Price Column - 15%
        costPriceColumn.setResizable(true);
        costPriceColumn.setMinWidth(100);
        costPriceColumn.setPrefWidth(120);
        costPriceColumn.setMaxWidth(150);
        
        // Status Column - 12%
        statusColumn.setResizable(true);
        statusColumn.setMinWidth(90);
        statusColumn.setPrefWidth(110);
        statusColumn.setMaxWidth(140);
        
        System.out.println("✅ Column constraints configured for full width distribution");
    }

    private void setupResponsiveListeners() {
        Platform.runLater(() -> {
            if (productsTable.getScene() != null) {
                addWindowResizeListener();
            } else {
                productsTable.sceneProperty().addListener((obs, oldScene, newScene) -> {
                    if (newScene != null) {
                        addWindowResizeListener();
                    }
                });
            }
        });
    }

    private void addWindowResizeListener() {
        if (productsTable.getScene() != null && productsTable.getScene().getWindow() != null) {
            Window window = productsTable.getScene().getWindow();
            
            // Listen to width changes
            window.widthProperty().addListener((obs, oldWidth, newWidth) -> 
                Platform.runLater(() -> {
                    adjustTableForScreenSize(newWidth.doubleValue());
                    forceTableRefresh();
                }));
            
            // Listen to height changes
            window.heightProperty().addListener((obs, oldHeight, newHeight) -> 
                Platform.runLater(this::forceTableRefresh));
            
            // Trigger initial adjustment
            Platform.runLater(() -> {
                adjustTableForScreenSize(window.getWidth());
                forceTableRefresh();
            });
            
            System.out.println("✅ Window resize listeners attached");
        }
    }

    private void adjustTableForScreenSize(double windowWidth) {
        double availableWidth = windowWidth - RIGHT_PANEL_WIDTH - PADDING_MARGIN - SCROLLBAR_WIDTH;
        
        System.out.println("🖥️ Adjusting table: Window=" + windowWidth + " Available=" + availableWidth);
        
        if (availableWidth < SMALL_SCREEN_THRESHOLD) {
            configureSmallScreen(availableWidth);
        } else if (availableWidth < MEDIUM_SCREEN_THRESHOLD) {
            configureMediumScreen(availableWidth);
        } else {
            configureLargeScreen(availableWidth);
        }
        
        // Force table to use calculated width
        Platform.runLater(() -> {
            productsTable.setMaxWidth(availableWidth);
            productsTable.setPrefWidth(availableWidth);
            forceTableRefresh();
        });
    }

    private void configureSmallScreen(double availableWidth) {
        // Hide cost price and category columns
        costPriceColumn.setVisible(false);
        categoryNameColumn.setVisible(false);
        
        // Redistribute remaining width (4 columns)
        double remainingWidth = availableWidth - 50;
        productIdColumn.setPrefWidth(50);
        productNameColumn.setPrefWidth(remainingWidth * 0.6);     // 60%
        sellingPriceColumn.setPrefWidth(remainingWidth * 0.25);   // 25%
        statusColumn.setPrefWidth(remainingWidth * 0.15);         // 15%
        
        System.out.println("📱 Small screen mode: 4 columns visible");
    }

    private void configureMediumScreen(double availableWidth) {
        // Hide only cost price column
        costPriceColumn.setVisible(false);
        categoryNameColumn.setVisible(true);
        
        // Redistribute width (5 columns)
        double remainingWidth = availableWidth - 50;
        productIdColumn.setPrefWidth(50);
        productNameColumn.setPrefWidth(remainingWidth * 0.45);    // 45%
        categoryNameColumn.setPrefWidth(remainingWidth * 0.2);    // 20%
        sellingPriceColumn.setPrefWidth(remainingWidth * 0.2);    // 20%
        statusColumn.setPrefWidth(remainingWidth * 0.15);         // 15%
        
        System.out.println("💻 Medium screen mode: 5 columns visible");
    }

    private void configureLargeScreen(double availableWidth) {
        // Show all columns
        costPriceColumn.setVisible(true);
        categoryNameColumn.setVisible(true);
        
        // Full width distribution (6 columns)
        double remainingWidth = availableWidth - 60;
        productIdColumn.setPrefWidth(60);
        productNameColumn.setPrefWidth(remainingWidth * 0.35);    // 35%
        categoryNameColumn.setPrefWidth(remainingWidth * 0.15);   // 15%
        sellingPriceColumn.setPrefWidth(remainingWidth * 0.2);    // 20%
        costPriceColumn.setPrefWidth(remainingWidth * 0.15);      // 15%
        statusColumn.setPrefWidth(remainingWidth * 0.15);         // 15%
        
        System.out.println("🖥️ Large screen mode: All 6 columns visible");
    }

    private void forceTableRefresh() {
        if (productsTable != null) {
            productsTable.refresh();
            productsTable.autosize();
            productsTable.requestLayout();
        }
    }

    private void setupTableDataBinding() {
        // Setup filtered list
        filteredProducts = new FilteredList<>(productList, p -> true);
        productsTable.setItems(filteredProducts);
        
        // FIXED: Enhanced selection handler with debug logging
        productsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            System.out.println("📋 Table selection changed:");
            System.out.println("  Old: " + (oldSelection != null ? oldSelection.getProductName() : "null"));
            System.out.println("  New: " + (newSelection != null ? newSelection.getProductName() : "null"));
            
            if (newSelection != null) {
                currentEditingProduct = newSelection;
                loadProductToPreview(newSelection);
                enableEditButtons(true);
                System.out.println("✅ Product selected: " + newSelection.getProductName());
            } else {
                currentEditingProduct = null;
                clearPreview();
                enableEditButtons(false);
                System.out.println("❌ No product selected");
            }
        });
        
        // FIXED: Add double-click handler for quick edit
        productsTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Product product = row.getItem();
                    if (product != null) {
                        System.out.println("🖱️ Double-clicked on: " + product.getProductName());
                        currentEditingProduct = product;
                        showEditProductDialog();
                    }
                }
            });
            return row;
        });
        
        System.out.println("✅ Table data binding configured with selection handlers");
    }

    private void setupFullScreenLayout() {
        Platform.runLater(() -> {
            try {
                if (productsTable.getScene() != null) {
                    Window window = productsTable.getScene().getWindow();
                    if (window != null) {
                        double windowWidth = window.getWidth();
                        System.out.println("🎯 Setting up full screen layout: " + windowWidth + "px");
                        
                        adjustTableForScreenSize(windowWidth);
                        forceTableRefresh();
                        
                        // Additional refresh after delay
                        Platform.runLater(this::forceTableRefresh);
                        System.out.println("✅ Full screen layout setup complete");
                    } else {
                        Platform.runLater(this::setupFullScreenLayout);
                    }
                } else {
                    Platform.runLater(this::setupFullScreenLayout);
                }
            } catch (Exception e) {
                System.err.println("❌ Error in setupFullScreenLayout: " + e.getMessage());
            }
        });
    }

    // =============================================
    // FILTERS AND SEARCH SETUP
    // =============================================

    private void setupFilters() {
        System.out.println("🔍 Setting up filters...");
        
        // Category filter setup
        categoryFilter.setItems(categoryList);
        categoryFilter.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getCategoryName() : "Tất cả";
            }

            @Override
            public Category fromString(String string) {
                return categoryList.stream()
                    .filter(cat -> cat.getCategoryName().equals(string))
                    .findFirst().orElse(null);
            }
        });

        // Status filter setup
        statusFilter.getItems().addAll("Tất cả", "Đang bán", "Ngừng bán");
        statusFilter.setValue("Tất cả");

        // Form category combobox
        categoryComboBox.setItems(categoryList);
        categoryComboBox.setConverter(categoryFilter.getConverter());
        
        System.out.println("✅ Filters configured");
    }

    private void applyFilters() {
        filteredProducts.setPredicate(product -> {
            // Search filter
            String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
            if (!searchText.isEmpty()) {
                if (!product.getProductName().toLowerCase().contains(searchText)) {
                    return false;
                }
            }

            // Category filter
            Category selectedCategory = categoryFilter != null ? categoryFilter.getValue() : null;
            if (selectedCategory != null) {
                if (product.getCategoryId() != selectedCategory.getCategoryId()) {
                    return false;
                }
            }

            // Status filter
            String selectedStatus = statusFilter != null ? statusFilter.getValue() : "Tất cả";
            if (!"Tất cả".equals(selectedStatus)) {
                boolean isAvailable = "Đang bán".equals(selectedStatus);
                if (product.isAvailable() != isAvailable) {
                    return false;
                }
            }

            return true;
        });

        updateTableFooter();
    }

    // =============================================
    // EVENT HANDLERS SETUP
    // =============================================

    private void setupEventHandlers() {
        System.out.println("🎮 Setting up event handlers...");
        
        // Search and filter handlers
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldValue, newValue) -> {
                System.out.println("🔍 Search text changed: " + newValue);
                applyFilters();
            });
        }
        if (categoryFilter != null) {
            categoryFilter.setOnAction(e -> {
                System.out.println("📂 Category filter changed");
                applyFilters();
            });
        }
        if (statusFilter != null) {
            statusFilter.setOnAction(e -> {
                System.out.println("📊 Status filter changed");
                applyFilters();
            });
        }

        // Toolbar button handlers with debug logging
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                System.out.println("🔄 Refresh button clicked");
                refreshData();
            });
        }
        
        if (addProductButton != null) {
            addProductButton.setOnAction(e -> {
                System.out.println("➕ Add product button clicked");
                showAddProductDialog();
            });
        } else {
            System.err.println("❌ addProductButton is null!");
        }
        
        if (editProductButton != null) {
            editProductButton.setOnAction(e -> {
                System.out.println("✏️ Edit product button clicked");
                System.out.println("Current editing product: " + (currentEditingProduct != null ? currentEditingProduct.getProductName() : "null"));
                showEditProductDialog();
            });
        } else {
            System.err.println("❌ editProductButton is null!");
        }
        
        if (deleteProductButton != null) {
            deleteProductButton.setOnAction(e -> {
                System.out.println("🗑️ Delete product button clicked");
                deleteSelectedProduct();
            });
        }

        // Pagination handlers
        if (previousPageButton != null) {
            previousPageButton.setOnAction(e -> previousPage());
        }
        if (nextPageButton != null) {
            nextPageButton.setOnAction(e -> nextPage());
        }

        // Preview panel handlers
        if (changeImageButton != null) {
            changeImageButton.setOnAction(e -> changeProductImage());
        }
        if (quickEditButton != null) {
            quickEditButton.setOnAction(e -> {
                System.out.println("⚡ Quick edit button clicked");
                showEditProductDialog();
            });
        }
        if (toggleStatusButton != null) {
            toggleStatusButton.setOnAction(e -> toggleProductStatus());
        }
        if (duplicateProductButton != null) {
            duplicateProductButton.setOnAction(e -> duplicateProduct());
        }

        // Form dialog handlers
        if (closeDialogButton != null) {
            closeDialogButton.setOnAction(e -> {
                System.out.println("❌ Close dialog button clicked");
                hideFormDialog();
            });
        }
        if (selectImageButton != null) {
            selectImageButton.setOnAction(e -> selectImageFile());
        }
        if (cancelFormButton != null) {
            cancelFormButton.setOnAction(e -> {
                System.out.println("❌ Cancel form button clicked");
                hideFormDialog();
            });
        }
        if (resetFormButton != null) {
            resetFormButton.setOnAction(e -> {
                System.out.println("🔄 Reset form button clicked");
                resetForm();
            });
        }
        if (saveProductButton != null) {
            saveProductButton.setOnAction(e -> {
                System.out.println("💾 Save product button clicked");
                saveProduct();
            });
        }

        // Image URL field listener
        if (imageUrlField != null) {
            imageUrlField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null && !newVal.trim().isEmpty()) {
                    loadImageFromUrl(newVal.trim());
                }
            });
        }

        // Logout handler
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> logout());
        }
        
        System.out.println("✅ Event handlers configured");
    }

    private void setupFormDialog() {
        if (productFormDialog != null) {
            productFormDialog.setVisible(false);
            productFormDialog.setManaged(false);
        }
    }

    // =============================================
    // DATA LOADING METHODS
    // =============================================

    private void loadInitialData() {
        System.out.println("📊 Loading initial data...");
        loadCategories();
        loadProducts();
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
                Platform.runLater(() -> {
                    categoryList.clear();
                    if (getValue() != null) {
                        categoryList.addAll(getValue());
                    }
                    System.out.println("✅ Loaded " + categoryList.size() + " categories");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Failed to load categories: " + getException().getMessage());
                    AlertUtils.showError("Lỗi", "Không thể tải danh mục");
                });
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
                Platform.runLater(() -> {
                    productList.clear();
                    if (getValue() != null) {
                        productList.addAll(getValue());
                    }
                    updateTableFooter();
                    
                    // Trigger responsive adjustment after loading
                    Platform.runLater(() -> {
                        if (productsTable.getScene() != null && productsTable.getScene().getWindow() != null) {
                            adjustTableForScreenSize(productsTable.getScene().getWindow().getWidth());
                            forceTableRefresh();
                        }
                    });
                    
                    System.out.println("✅ Loaded " + productList.size() + " products");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Failed to load products: " + getException().getMessage());
                    AlertUtils.showError("Lỗi", "Không thể tải sản phẩm");
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void refreshData() {
        System.out.println("🔄 Refreshing data...");
        loadCategories();
        loadProducts();
        clearPreview();
        hideFormDialog();
    }

    // =============================================
    // PRODUCT CRUD OPERATIONS
    // =============================================

    private void showAddProductDialog() {
        try {
            System.out.println("➕ Showing add product dialog...");
            
            currentEditingProduct = null;
            
            if (dialogTitle != null) {
                dialogTitle.setText("➕ Thêm món mới");
            }
            
            resetForm();
            showFormDialog();
            
            System.out.println("✅ Add product dialog shown successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error showing add product dialog: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở form thêm sản phẩm: " + e.getMessage());
        }
    }

    private void showEditProductDialog() {
        try {
            System.out.println("✏️ Showing edit product dialog...");
            
            // FIXED: Better validation of selected product
            Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
            
            if (selectedProduct == null && currentEditingProduct == null) {
                System.out.println("❌ No product selected for editing");
                AlertUtils.showWarning("Chú ý", "Vui lòng chọn sản phẩm cần sửa");
                return;
            }
            
            // Use selected product if available, otherwise use currentEditingProduct
            Product productToEdit = selectedProduct != null ? selectedProduct : currentEditingProduct;
            currentEditingProduct = productToEdit;
            
            System.out.println("📝 Editing product: " + productToEdit.getProductName());
            
            if (dialogTitle != null) {
                dialogTitle.setText("✏️ Sửa món: " + productToEdit.getProductName());
            }
            
            loadProductToForm(productToEdit);
            showFormDialog();
            
            System.out.println("✅ Edit product dialog shown successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error showing edit product dialog: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở form sửa sản phẩm: " + e.getMessage());
        }
    }

    private void saveProduct() {
        if (!validateForm()) return;

        Product product = createProductFromForm();
        boolean isUpdate = currentEditingProduct != null;

        if (isUpdate) {
            product.setProductId(currentEditingProduct.getProductId());
        }

        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    if (isUpdate) {
                        return dao.update(product);
                    } else {
                        return dao.save(product);
                    }
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        String action = isUpdate ? "cập nhật" : "thêm";
                        AlertUtils.showInfo("Thành công", "Đã " + action + " sản phẩm thành công");
                        hideFormDialog();
                        loadProducts();
                    } else {
                        AlertUtils.showError("Lỗi", "Không thể lưu sản phẩm");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Lỗi khi lưu sản phẩm: " + getException().getMessage());
                });
            }
        };

        new Thread(saveTask).start();
    }

    private void deleteSelectedProduct() {
        if (currentEditingProduct == null) {
            AlertUtils.showWarning("Chú ý", "Vui lòng chọn sản phẩm cần xóa");
            return;
        }

        boolean confirmed = AlertUtils.showConfirmation("Xác nhận xóa", 
            "Bạn có chắc chắn muốn xóa sản phẩm '" + currentEditingProduct.getProductName() + "'?");
        
        if (!confirmed) return;

        Task<Boolean> deleteTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    return dao.delete(currentEditingProduct.getProductId());
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        AlertUtils.showInfo("Thành công", "Đã xóa sản phẩm thành công");
                        loadProducts();
                        clearPreview();
                    } else {
                        AlertUtils.showError("Lỗi", "Không thể xóa sản phẩm");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Lỗi khi xóa sản phẩm: " + getException().getMessage());
                });
            }
        };

        new Thread(deleteTask).start();
    }

    private void toggleProductStatus() {
        if (currentEditingProduct == null) return;

        boolean newStatus = !currentEditingProduct.isAvailable();
        currentEditingProduct.setAvailable(newStatus);

        Task<Boolean> updateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    ProductDAO dao = new ProductDAOImpl(connection);
                    return dao.update(currentEditingProduct);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        String status = newStatus ? "kích hoạt" : "vô hiệu hóa";
                        AlertUtils.showInfo("Thành công", "Đã " + status + " sản phẩm");
                        loadProducts();
                        loadProductToPreview(currentEditingProduct);
                    } else {
                        AlertUtils.showError("Lỗi", "Không thể cập nhật trạng thái");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Lỗi khi cập nhật trạng thái: " + getException().getMessage());
                });
            }
        };

        new Thread(updateTask).start();
    }

    private void duplicateProduct() {
        if (currentEditingProduct == null) return;

        Product original = productsTable.getSelectionModel().getSelectedItem();
        if (original == null) return;

        currentEditingProduct = null; // Create new product
        if (dialogTitle != null) {
            dialogTitle.setText("📋 Nhân bản sản phẩm");
        }
        
        loadProductToForm(original);
        
        // Modify name for duplicate
        if (productNameField != null) {
            productNameField.setText(original.getProductName() + " (Copy)");
        }
        
        showFormDialog();
    }

    // =============================================
    // FORM VALIDATION AND UTILITIES
    // =============================================

    private boolean validateForm() {
        if (productNameField.getText().trim().isEmpty()) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng nhập tên sản phẩm");
            productNameField.requestFocus();
            return false;
        }

        if (categoryComboBox.getValue() == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn danh mục");
            categoryComboBox.requestFocus();
            return false;
        }

        try {
            double sellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
            if (sellingPrice <= 0) {
                AlertUtils.showWarning("Cảnh báo", "Giá bán phải lớn hơn 0");
                sellingPriceField.requestFocus();
                return false;
            }
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Cảnh báo", "Giá bán không hợp lệ");
            sellingPriceField.requestFocus();
            return false;
        }

        try {
            String costPriceText = costPriceField.getText().trim();
            if (!costPriceText.isEmpty()) {
                double costPrice = Double.parseDouble(costPriceText);
                if (costPrice < 0) {
                    AlertUtils.showWarning("Cảnh báo", "Giá vốn không được âm");
                    costPriceField.requestFocus();
                    return false;
                }
            }
        } catch (NumberFormatException e) {
            AlertUtils.showWarning("Cảnh báo", "Giá vốn không hợp lệ");
            costPriceField.requestFocus();
            return false;
        }

        return true;
    }

    private Product createProductFromForm() {
        Product product = new Product();
        product.setProductName(productNameField.getText().trim());
        product.setCategoryId(categoryComboBox.getValue().getCategoryId());
        product.setPrice(Double.parseDouble(sellingPriceField.getText().trim()));
        
        String costPriceText = costPriceField.getText().trim();
        if (!costPriceText.isEmpty()) {
            product.setCostPrice(Double.parseDouble(costPriceText));
        } else {
            product.setCostPrice(0.0);
        }
        
        product.setDescription(descriptionField.getText().trim());
        product.setImageUrl(imageUrlField.getText().trim());
        product.setAvailable(isAvailableCheckBox.isSelected());
        product.setActive(true);
        
        return product;
    }

    private void loadProductToForm(Product product) {
        try {
            if (product == null) {
                System.err.println("❌ Cannot load null product to form");
                return;
            }

            System.out.println("📋 Loading product to form: " + product.getProductName());

            if (productNameField != null) {
                productNameField.setText(product.getProductName());
            }
            if (sellingPriceField != null) {
                sellingPriceField.setText(String.valueOf(product.getPrice()));
            }
            if (costPriceField != null) {
                costPriceField.setText(String.valueOf(product.getCostPrice()));
            }
            if (descriptionField != null) {
                descriptionField.setText(product.getDescription() != null ? product.getDescription() : "");
            }
            if (imageUrlField != null) {
                imageUrlField.setText(product.getImageUrl() != null ? product.getImageUrl() : "");
            }
            if (isAvailableCheckBox != null) {
                isAvailableCheckBox.setSelected(product.isAvailable());
            }

            // FIXED: Better category selection
            if (categoryComboBox != null) {
                Category category = categoryList.stream()
                    .filter(cat -> cat.getCategoryId() == product.getCategoryId())
                    .findFirst().orElse(null);
                
                if (category != null) {
                    categoryComboBox.setValue(category);
                    System.out.println("📂 Category set: " + category.getCategoryName());
                } else {
                    System.err.println("❌ Category not found for ID: " + product.getCategoryId());
                }
            }

            // Load image
            loadImageFromUrl(product.getImageUrl());
            
            System.out.println("✅ Product loaded to form successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading product to form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void resetForm() {
        try {
            System.out.println("🔄 Resetting form...");
            
            if (productNameField != null) productNameField.clear();
            if (sellingPriceField != null) sellingPriceField.clear();
            if (costPriceField != null) costPriceField.clear();
            if (descriptionField != null) descriptionField.clear();
            if (imageUrlField != null) imageUrlField.clear();
            if (isAvailableCheckBox != null) isAvailableCheckBox.setSelected(true);
            if (categoryComboBox != null) categoryComboBox.setValue(null);
            
            // Reset form image
            if (formProductImageView != null) {
                try {
                    formProductImageView.setImage(new Image("file:src/main/resources/images/no-image.png"));
                } catch (Exception e) {
                    System.err.println("❌ Could not load default image: " + e.getMessage());
                }
            }
            
            // FIXED: Clear any CSS conflicts in form fields
            clearFormCSSConflicts();
            
            System.out.println("✅ Form reset successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error resetting form: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Clear CSS conflicts that cause LinearGradient parsing errors
     */
    private void clearFormCSSConflicts() {
        try {
            // Clear any problematic CSS that might cause LinearGradient conflicts
            if (productNameField != null) {
                productNameField.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            if (sellingPriceField != null) {
                sellingPriceField.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            if (costPriceField != null) {
                costPriceField.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            if (descriptionField != null) {
                descriptionField.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            if (imageUrlField != null) {
                imageUrlField.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            if (categoryComboBox != null) {
                categoryComboBox.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
            }
            
            System.out.println("✅ Form CSS conflicts cleared");
        } catch (Exception e) {
            System.err.println("❌ Error clearing CSS conflicts: " + e.getMessage());
        }
    }

    // =============================================
    // PREVIEW PANEL METHODS
    // =============================================

    private void loadProductToPreview(Product product) {
        if (product == null) {
            clearPreview();
            return;
        }

        previewProductName.setText(product.getProductName());
        previewProductId.setText(String.valueOf(product.getProductId()));
        previewCategory.setText(getCategoryName(product.getCategoryId()));
        previewSellingPrice.setText(priceFormatter.format(product.getPrice()) + " đ");
        previewCostPrice.setText(priceFormatter.format(product.getCostPrice()) + " đ");
        previewStatus.setText(product.isAvailable() ? "Đang bán" : "Ngừng bán");
        previewDescription.setText(product.getDescription() != null ? product.getDescription() : "Chưa có mô tả");
        previewCreatedDate.setText(LocalDateTime.now().format(dateFormatter)); // TODO: Use actual creation date

        // Apply status styling
        if (product.isAvailable()) {
            previewStatus.setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
            toggleStatusButton.setText("⏸️ Ngừng bán");
        } else {
            previewStatus.setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
            toggleStatusButton.setText("▶️ Bắt đầu bán");
        }

        // Load product image
        loadImageFromUrl(product.getImageUrl());
    }

    private void clearPreview() {
        previewProductName.setText("Chưa chọn sản phẩm");
        previewProductId.setText("-");
        previewCategory.setText("-");
        previewSellingPrice.setText("-");
        previewCostPrice.setText("-");
        previewStatus.setText("-");
        previewDescription.setText("Chưa có mô tả");
        previewCreatedDate.setText("-");
        
        // Clear image
        if (productImageView != null) {
            productImageView.setImage(new Image("file:src/main/resources/images/no-image.png"));
        }
    }

    // =============================================
    // IMAGE HANDLING METHODS
    // =============================================

    private void loadImageFromUrl(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            // Load default image
            Image defaultImage = new Image("file:src/main/resources/images/no-image.png");
            if (productImageView != null) {
                productImageView.setImage(defaultImage);
            }
            if (formProductImageView != null) {
                formProductImageView.setImage(defaultImage);
            }
            return;
        }

        Task<Image> imageTask = new Task<Image>() {
            @Override
            protected Image call() throws Exception {
                return new Image(imageUrl, true); // Load in background
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Image image = getValue();
                    if (productImageView != null) {
                        productImageView.setImage(image);
                    }
                    if (formProductImageView != null) {
                        formProductImageView.setImage(image);
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    // Load default image on failure
                    Image defaultImage = new Image("file:src/main/resources/images/no-image.png");
                    if (productImageView != null) {
                        productImageView.setImage(defaultImage);
                    }
                    if (formProductImageView != null) {
                        formProductImageView.setImage(defaultImage);
                    }
                });
            }
        };

        new Thread(imageTask).start();
    }

    private void selectImageFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Stage stage = (Stage) selectImageButton.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            String imageUrl = selectedFile.toURI().toString();
            imageUrlField.setText(imageUrl);
            loadImageFromUrl(imageUrl);
        }
    }

    private void changeProductImage() {
        if (currentEditingProduct == null) return;
        selectImageFile();
        // TODO: Save image URL to current product
    }

    // =============================================
    // DIALOG MANAGEMENT
    // =============================================

    private void showFormDialog() {
        try {
            System.out.println("📝 Attempting to show form dialog...");
            
            if (productFormDialog == null) {
                System.err.println("❌ productFormDialog is null!");
                AlertUtils.showError("Lỗi", "Form dialog không được khởi tạo");
                return;
            }
            
            // FIXED: Reset form state before showing
            resetForm();
            
            // FIXED: Ensure proper dialog visibility and state
            productFormDialog.setVisible(true);
            productFormDialog.setManaged(true);
            productFormDialog.toFront();
            
            // FIXED: Force CSS refresh to avoid LinearGradient conflicts
            productFormDialog.setStyle(productFormDialog.getStyle());
            
            // FIXED: Alternative method if above doesn't work
            if (productFormDialog.getParent() == null) {
                System.out.println("🔧 Dialog not in scene graph, attempting alternative show method...");
                
                // Create a fresh dialog stage each time to avoid conflicts
                if (currentDialogStage != null) {
                    currentDialogStage.close();
                }
                
                Stage dialogStage = new Stage();
                currentDialogStage = dialogStage;
                dialogStage.setTitle("Quản lý sản phẩm");
                
                // Create a fresh scene with the dialog
                Scene dialogScene = new Scene(productFormDialog, 600, 500);
                dialogScene.getStylesheets().add(getClass().getResource("/css/menu-style.css").toExternalForm());
                dialogStage.setScene(dialogScene);
                dialogStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
                dialogStage.show();
                
                System.out.println("✅ Form dialog shown as new window");
            } else {
                System.out.println("✅ Form dialog shown in main window");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error showing form dialog: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể hiển thị form: " + e.getMessage());
        }
    }

    private void hideFormDialog() {
        try {
            System.out.println("❌ Hiding form dialog...");
            
            // Close alternative dialog stage if it exists
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
                System.out.println("✅ Alternative dialog stage closed");
            }
            
            if (productFormDialog != null) {
                // FIXED: Proper cleanup to avoid memory leaks
                productFormDialog.setVisible(false);
                productFormDialog.setManaged(false);
                
                // FIXED: Clear any CSS conflicts
                productFormDialog.setStyle("");
                
                // If it's in a separate stage, close that stage
                if (productFormDialog.getScene() != null && 
                    productFormDialog.getScene().getWindow() instanceof Stage) {
                    Stage stage = (Stage) productFormDialog.getScene().getWindow();
                    if (stage.getOwner() != null) { // It's a modal dialog
                        stage.close();
                    }
                }
                
                System.out.println("✅ Form dialog hidden");
            }
            
            // FIXED: Reset editing state
            currentEditingProduct = null;
            
            // FIXED: Force garbage collection to clean up CSS conflicts
            System.gc();
            
        } catch (Exception e) {
            System.err.println("❌ Error hiding form dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // =============================================
    // UI STATE MANAGEMENT
    // =============================================

    private void enableEditButtons(boolean enabled) {
        System.out.println("🎛️ Setting edit buttons enabled: " + enabled);
        
        if (editProductButton != null) {
            editProductButton.setDisable(!enabled);
        }
        if (deleteProductButton != null) {
            deleteProductButton.setDisable(!enabled);
        }
        if (quickEditButton != null) {
            quickEditButton.setDisable(!enabled);
        }
        if (toggleStatusButton != null) {
            toggleStatusButton.setDisable(!enabled);
        }
        if (duplicateProductButton != null) {
            duplicateProductButton.setDisable(!enabled);
        }
        
        System.out.println("✅ Edit buttons state updated");
    }

    private void updateTableFooter() {
        int total = filteredProducts.size();
        int active = (int) filteredProducts.stream().filter(Product::isAvailable).count();
        int inactive = total - active;

        if (totalProductsLabel != null) {
            totalProductsLabel.setText("Tổng: " + total + " món");
        }
        if (activeProductsLabel != null) {
            activeProductsLabel.setText("Đang bán: " + active + " món");
        }
        if (inactiveProductsLabel != null) {
            inactiveProductsLabel.setText("Ngừng bán: " + inactive + " món");
        }

        // Update pagination
        totalPages = (int) Math.ceil((double) total / itemsPerPage);
        if (totalPages == 0) totalPages = 1;
        
        if (pageInfoLabel != null) {
            pageInfoLabel.setText("Trang " + currentPage + "/" + totalPages);
        }
        
        if (previousPageButton != null) {
            previousPageButton.setDisable(currentPage <= 1);
        }
        if (nextPageButton != null) {
            nextPageButton.setDisable(currentPage >= totalPages);
        }
    }

    // =============================================
    // PAGINATION METHODS
    // =============================================

    private void previousPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTableFooter();
        }
    }

    private void nextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateTableFooter();
        }
    }

    // =============================================
    // UTILITY METHODS
    // =============================================

    private String getCategoryName(int categoryId) {
        return categoryList.stream()
            .filter(cat -> cat.getCategoryId() == categoryId)
            .findFirst()
            .map(Category::getCategoryName)
            .orElse("N/A");
    }

    private void logout() {
        boolean confirmed = AlertUtils.showConfirmation("Xác nhận đăng xuất", 
            "Bạn có chắc chắn muốn đăng xuất?");
        
        if (confirmed) {
            // TODO: Implement logout logic
            System.out.println("Logging out...");
        }
    }

    // =============================================
    // DASHBOARD COMMUNICATION
    // =============================================

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminMenuController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }

    // =============================================
    // DIALOG OVERLAY HANDLER METHODS
    // =============================================

    // Method to prevent dialog from closing when clicking inside dialog
    @FXML
    private void preventDialogClose(MouseEvent event) {
        event.consume(); // Prevent event from bubbling up
    }

    // Method to hide dialog when clicking overlay background
    @FXML
    private void hideFormDialogOverlay(MouseEvent event) {
        // Only hide if clicked on overlay background, not on dialog content
        if (event.getTarget() == dialogOverlay) {
            hideFormDialog();
        }
    }

    // Updated dialog management methods for overlay approach
    private void showFormDialogOverlay() {
        try {
            System.out.println("📝 Showing form dialog overlay...");
            
            if (dialogOverlay == null) {
                System.err.println("❌ dialogOverlay is null! Using alternative method...");
                showFormDialogAlternative();
                return;
            }
            
            if (productFormDialog == null) {
                System.err.println("❌ productFormDialog is null!");
                AlertUtils.showError("Lỗi", "Form dialog không được khởi tạo");
                return;
            }
            
            // Show overlay with dialog
            dialogOverlay.setVisible(true);
            dialogOverlay.setManaged(true);
            dialogOverlay.toFront();
            
            // Focus on first field
            Platform.runLater(() -> {
                if (productNameField != null) {
                    productNameField.requestFocus();
                }
            });
            
            System.out.println("✅ Form dialog overlay shown successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error showing form dialog overlay: " + e.getMessage());
            e.printStackTrace();
            showFormDialogAlternative();
        }
    }

    // Alternative method if overlay doesn't work
    private void showFormDialogAlternative() {
        try {
            System.out.println("🔧 Using alternative dialog method...");
            
            // Create new stage for dialog
            Stage dialogStage = new Stage();
            dialogStage.setTitle(currentEditingProduct == null ? "Thêm món mới" : "Sửa món");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            // Get current stage as owner
            if (productsTable != null && productsTable.getScene() != null) {
                dialogStage.initOwner(productsTable.getScene().getWindow());
            }
            
            // Create dialog content
            VBox dialogContent = createDialogContent();
            Scene dialogScene = new Scene(dialogContent, 550, 500);
            
            // Add CSS
            try {
                dialogScene.getStylesheets().add(getClass().getResource("/css/menu-style.css").toExternalForm());
            } catch (Exception e) {
                System.err.println("❌ Could not load CSS for dialog: " + e.getMessage());
            }
            
            dialogStage.setScene(dialogScene);
            dialogStage.setResizable(false);
            
            // Store reference for closing
            currentDialogStage = dialogStage;
            
            dialogStage.show();
            
            // Focus on first field
            Platform.runLater(() -> {
                if (productNameField != null) {
                    productNameField.requestFocus();
                }
            });
            
            System.out.println("✅ Alternative dialog shown successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error showing alternative dialog: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể hiển thị form: " + e.getMessage());
        }
    }

    private VBox createDialogContent() {
        VBox container = new VBox();
        container.setSpacing(0);
        container.getStyleClass().add("product-form-dialog");
        
        // Header
        HBox header = new HBox();
        header.setSpacing(10);
        header.setAlignment(Pos.CENTER_LEFT);
        header.getStyleClass().add("dialog-header");
        
        Label title = new Label(currentEditingProduct == null ? "➕ Thêm món mới" : "✏️ Sửa món");
        title.getStyleClass().add("dialog-title");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(e -> {
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
            }
            currentEditingProduct = null;
        });
        
        header.getChildren().addAll(title, spacer, closeBtn);
        
        // Content (reuse existing form fields)
        ScrollPane content = createFormContent();
        
        // Footer
        HBox footer = new HBox();
        footer.setSpacing(8);
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.getStyleClass().add("dialog-footer");
        
        Button cancelBtn = new Button("❌ Hủy");
        cancelBtn.getStyleClass().add("outline-button");
        cancelBtn.setOnAction(e -> {
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
            }
            currentEditingProduct = null;
        });
        
        Button resetBtn = new Button("🔄 Đặt lại");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setOnAction(e -> resetForm());
        
        Button saveBtn = new Button("💾 Lưu");
        saveBtn.getStyleClass().add("primary-button");
        saveBtn.setOnAction(e -> {
            saveProduct();
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
            }
        });
        
        footer.getChildren().addAll(cancelBtn, resetBtn, saveBtn);
        
        container.getChildren().addAll(header, content, footer);
        
        return container;
    }

    private ScrollPane createFormContent() {
        // Create form content programmatically if FXML fields are not available
        // This is a fallback method
        
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(400);
        scrollPane.getStyleClass().add("dialog-content");
        
        VBox formContainer = new VBox();
        formContainer.setSpacing(15);
        formContainer.getStyleClass().add("form-container");
        
        // Add form sections programmatically here if needed
        // For now, return empty container
        scrollPane.setContent(formContainer);
        
        return scrollPane;
    }
    
    /**
     * Clean up resources to prevent memory leaks and CSS conflicts
     */
    public void cleanupResources() {
        try {
            System.out.println("🧹 Cleaning up AdminMenuController resources...");
            
            // Close any open dialogs
            if (currentDialogStage != null) {
                currentDialogStage.close();
                currentDialogStage = null;
            }
            
            // Hide form dialog
            if (productFormDialog != null) {
                productFormDialog.setVisible(false);
                productFormDialog.setManaged(false);
                productFormDialog.setStyle("");
            }
            
            // Clear references
            currentEditingProduct = null;
            
            // Clear table selection
            if (productsTable != null) {
                productsTable.getSelectionModel().clearSelection();
            }
            
            // Force garbage collection
            System.gc();
            
            System.out.println("✅ AdminMenuController resources cleaned up successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error cleaning up resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}