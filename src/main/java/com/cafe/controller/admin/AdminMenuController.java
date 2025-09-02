package com.cafe.controller.admin;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.CategoryDAO;
import com.cafe.dao.base.CategoryDAOImpl;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;
import com.cafe.util.AlertUtils;
import com.cafe.util.SessionManager;

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
import javafx.scene.layout.Priority;
import javafx.scene.Scene;
import javafx.stage.Window;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.StringConverter;
import javafx.geometry.Pos;
import javafx.scene.Node;

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
 * - Fixed dialog management (no more duplicates)
 * 
 * @author Team 2_C2406L
 * @version 4.0.0 - IMPROVED
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
    // FXML COMPONENTS - FORM DIALOG (UNUSED - Removed)
    // =============================================
    // Form dialog components are created programmatically

    // =============================================
    // PROGRAMMATIC FORM FIELDS (Created dynamically)
    // =============================================
    private TextField productNameField;
    private ComboBox<Category> categoryComboBox;
    private TextArea descriptionField;
    private TextField sellingPriceField;
    private TextField costPriceField;
    private ImageView formProductImageView;
    private TextField imageUrlField;
    private CheckBox isAvailableCheckBox;
    private Button saveProductButton;

    // =============================================
    // DATA COLLECTIONS
    // =============================================
    private final ObservableList<Product> productList = FXCollections.observableArrayList();
    private final ObservableList<Category> categoryList = FXCollections.observableArrayList();
    private FilteredList<Product> filteredProducts;

    // =============================================
    // SERVICES & STATE
    // =============================================
    private Product currentEditingProduct = null;
    private Object dashboardController;

    // =============================================
    // IMPROVED DIALOG MANAGEMENT
    // =============================================
    private Stage productFormStage = null;
    private boolean isFormInitialized = false;
    private Label dialogTitleLabel;

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
            setupTable();
            setupFilters();
            setupEventHandlers();
            setupFormDialog();
            
            // Load initial data
            loadInitialData();
            
            // Setup full screen layout
            Platform.runLater(this::setupFullScreenLayout);
            
            // Add cleanup on application close
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
        // Force table to use constrained resize policy with flexible last column
        productsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        
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
        
        // Enhanced selection handler with debug logging
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
        
        // Simple double-click handler for quick edit
        productsTable.setRowFactory(tv -> {
            TableRow<Product> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Product product = row.getItem();
                    if (product != null) {
                        System.out.println("🖱️ Double-clicked on: " + product.getProductName());
                        currentEditingProduct = product;
                        showEditProductForm();
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
        categoryFilter.setConverter(createCategoryConverter());

        // Status filter setup
        statusFilter.getItems().addAll("Tất cả", "Đang bán", "Ngừng bán");
        statusFilter.setValue("Tất cả");
        
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
    // EVENT HANDLERS SETUP - FIXED
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

        // Toolbar button handlers - FIXED
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> {
                System.out.println("🔄 Refresh button clicked");
                refreshData();
            });
        }
        
        if (addProductButton != null) {
            addProductButton.setOnAction(e -> {
                System.out.println("➕ Add product button clicked");
                showAddProductForm();
            });
        }
        
        if (editProductButton != null) {
            editProductButton.setOnAction(e -> {
                System.out.println("✏️ Edit product button clicked");
                showEditProductForm();
            });
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

        // Preview panel handlers - FIXED
        if (changeImageButton != null) {
            changeImageButton.setOnAction(e -> changeProductImage());
        }
        if (quickEditButton != null) {
            quickEditButton.setOnAction(e -> {
                System.out.println("⚡ Quick edit button clicked");
                showEditProductForm();
            });
        }
        if (toggleStatusButton != null) {
            toggleStatusButton.setOnAction(e -> toggleProductStatus());
        }
        if (duplicateProductButton != null) {
            duplicateProductButton.setOnAction(e -> duplicateProduct());
        }

        // Logout handler
        if (logoutButton != null) {
            logoutButton.setOnAction(e -> logout());
        }
        
        System.out.println("✅ Event handlers configured");
    }

    private void setupFormDialog() {
        // Form dialog is created programmatically - no FXML setup needed
        System.out.println("✅ Form dialog setup complete (programmatic creation)");
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
        
        // Hide form if open
        if (productFormStage != null && productFormStage.isShowing()) {
            hideProductForm();
        }
    }

    // =============================================
    // IMPROVED DIALOG MANAGEMENT - NO DUPLICATES
    // =============================================

    /**
     * Single method to show form dialog - removes confusion
     */
    private void showProductForm(Product productToEdit) {
        try {
            System.out.println("📝 Opening product form...");
            
            // Set current editing product
            currentEditingProduct = productToEdit;
            boolean isEdit = (productToEdit != null);
            
            // Initialize dialog if first time
            if (!isFormInitialized) {
                initializeFormDialog();
            }
            
            // Update dialog title
            updateDialogTitle(isEdit, productToEdit);
            
            // Load product data to form
            if (isEdit) {
                loadProductToForm(productToEdit);
            } else {
                resetFormFields();
            }
            
            // Show dialog
            if (productFormStage != null) {
                productFormStage.show();
                productFormStage.toFront();
                
                // Focus first field
                Platform.runLater(() -> {
                    if (productNameField != null) {
                        productNameField.requestFocus();
                    }
                });
            }
            
            System.out.println("✅ Product form opened successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error opening product form: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể mở form sản phẩm: " + e.getMessage());
        }
    }

    /**
     * Initialize form dialog once - reuse afterwards
     */
    private void initializeFormDialog() {
        try {
            System.out.println("🔧 Initializing form dialog...");
            
            // Create stage once
            productFormStage = new Stage();
            productFormStage.initModality(Modality.APPLICATION_MODAL);
            
            // Set owner
            if (productsTable != null && productsTable.getScene() != null) {
                productFormStage.initOwner(productsTable.getScene().getWindow());
            }
            
            // Always create programmatically for consistency
            createProgrammaticDialog();
            
            // Setup event handlers once
            setupDialogEventHandlers();
            
            isFormInitialized = true;
            System.out.println("✅ Form dialog initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing form dialog: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi", "Không thể khởi tạo form dialog: " + e.getMessage());
        }
    }

    /**
     * Create dialog programmatically - consistent approach
     */
    private void createProgrammaticDialog() {
        try {
            System.out.println("🔨 Creating dialog programmatically...");
            
            VBox dialogRoot = new VBox();
            dialogRoot.getStyleClass().add("product-form-dialog");
            
            // === HEADER ===
            HBox header = createDialogHeader();
            
            // === CONTENT ===
            ScrollPane content = createDialogContent();
            VBox.setVgrow(content, Priority.ALWAYS);
            
            // === FOOTER ===
            HBox footer = createDialogFooter();
            
            dialogRoot.getChildren().addAll(header, content, footer);
            
            // Create scene
            Scene scene = new Scene(dialogRoot, 600, 500);
            
            // Add CSS
            try {
                String cssUrl = getClass().getResource("/css/menu-style.css").toExternalForm();
                scene.getStylesheets().add(cssUrl);
            } catch (Exception e) {
                System.err.println("⚠️ Could not load CSS: " + e.getMessage());
            }
            
            productFormStage.setScene(scene);
            productFormStage.setResizable(true);
            productFormStage.setMinWidth(500);
            productFormStage.setMinHeight(400);
            
            System.out.println("✅ Dialog created programmatically");
            
        } catch (Exception e) {
            System.err.println("❌ Error creating programmatic dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Create dialog header with title and close button
     */
    private HBox createDialogHeader() {
        HBox header = new HBox();
        header.getStyleClass().add("dialog-header");
        header.setAlignment(Pos.CENTER_LEFT);
        header.setSpacing(10);
        
        // Title label (will be updated dynamically)
        dialogTitleLabel = new Label();
        dialogTitleLabel.getStyleClass().add("dialog-title");
        
        // Spacer
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Close button
        Button closeBtn = new Button("✕");
        closeBtn.getStyleClass().add("close-button");
        closeBtn.setOnAction(e -> hideProductForm());
        
        header.getChildren().addAll(dialogTitleLabel, spacer, closeBtn);
        return header;
    }

    /**
     * Create scrollable form content
     */
    private ScrollPane createDialogContent() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.getStyleClass().add("dialog-content");
        scrollPane.setFitToWidth(true);
        
        VBox formContainer = new VBox();
        formContainer.getStyleClass().add("form-container");
        formContainer.setSpacing(20);
        
        // === BASIC INFO SECTION ===
        VBox basicSection = createBasicInfoSection();
        
        // === PRICING SECTION ===  
        VBox pricingSection = createPricingSection();
        
        // === IMAGE SECTION ===
        VBox imageSection = createImageSection();
        
        // === STATUS SECTION ===
        VBox statusSection = createStatusSection();
        
        formContainer.getChildren().addAll(
            basicSection, pricingSection, imageSection, statusSection
        );
        
        scrollPane.setContent(formContainer);
        return scrollPane;
    }

    /**
     * Create basic info section
     */
    private VBox createBasicInfoSection() {
        VBox section = new VBox();
        section.getStyleClass().add("form-section");
        section.setSpacing(15);
        
        Label title = new Label("📋 Thông tin cơ bản");
        title.getStyleClass().add("section-title");
        
        // Product name
        VBox nameGroup = new VBox(5);
        Label nameLabel = new Label("Tên món *");
        nameLabel.getStyleClass().add("form-label");
        
        productNameField = new TextField();
        productNameField.setPromptText("Nhập tên món ăn...");
        productNameField.getStyleClass().add("form-field");
        
        nameGroup.getChildren().addAll(nameLabel, productNameField);
        
        // Category
        VBox categoryGroup = new VBox(5);
        Label categoryLabel = new Label("Danh mục *");
        categoryLabel.getStyleClass().add("form-label");
        
        categoryComboBox = new ComboBox<>();
        categoryComboBox.setPromptText("Chọn danh mục");
        categoryComboBox.getStyleClass().add("form-combobox");
        categoryComboBox.setItems(categoryList);
        categoryComboBox.setConverter(createCategoryConverter());
        
        categoryGroup.getChildren().addAll(categoryLabel, categoryComboBox);
        
        // Description
        VBox descGroup = new VBox(5);
        Label descLabel = new Label("Mô tả");
        descLabel.getStyleClass().add("form-label");
        
        descriptionField = new TextArea();
        descriptionField.setPromptText("Mô tả về món ăn...");
        descriptionField.getStyleClass().add("form-textarea");
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        
        descGroup.getChildren().addAll(descLabel, descriptionField);
        
        section.getChildren().addAll(title, nameGroup, categoryGroup, descGroup);
        return section;
    }

    /**
     * Create pricing section
     */
    private VBox createPricingSection() {
        VBox section = new VBox();
        section.getStyleClass().add("form-section");
        section.setSpacing(15);
        
        Label title = new Label("💰 Thông tin giá cả");
        title.getStyleClass().add("section-title");
        
        HBox priceRow = new HBox(15);
        
        // Selling price
        VBox sellPriceGroup = new VBox(5);
        Label sellLabel = new Label("Giá bán *");
        sellLabel.getStyleClass().add("form-label");
        
        sellingPriceField = new TextField();
        sellingPriceField.setPromptText("0");
        sellingPriceField.getStyleClass().addAll("form-field", "price-field");
        HBox.setHgrow(sellPriceGroup, Priority.ALWAYS);
        
        sellPriceGroup.getChildren().addAll(sellLabel, sellingPriceField);
        
        // Cost price
        VBox costPriceGroup = new VBox(5);
        Label costLabel = new Label("Giá vốn");
        costLabel.getStyleClass().add("form-label");
        
        costPriceField = new TextField();
        costPriceField.setPromptText("0");
        costPriceField.getStyleClass().addAll("form-field", "price-field");
        HBox.setHgrow(costPriceGroup, Priority.ALWAYS);
        
        costPriceGroup.getChildren().addAll(costLabel, costPriceField);
        
        priceRow.getChildren().addAll(sellPriceGroup, costPriceGroup);
        section.getChildren().addAll(title, priceRow);
        
        return section;
    }

    /**
     * Create image section
     */
    private VBox createImageSection() {
        VBox section = new VBox();
        section.getStyleClass().add("form-section");
        section.setSpacing(15);
        
        Label title = new Label("🖼️ Hình ảnh sản phẩm");
        title.getStyleClass().add("section-title");
        
        HBox imageRow = new HBox(15);
        imageRow.setAlignment(Pos.CENTER_LEFT);
        
        // Image preview
        VBox imagePreview = new VBox(10);
        imagePreview.setAlignment(Pos.CENTER);
        
        formProductImageView = new ImageView();
        formProductImageView.setFitHeight(120);
        formProductImageView.setFitWidth(120);
        formProductImageView.setPreserveRatio(true);
        formProductImageView.getStyleClass().add("form-image-preview");
        
        Button selectImageBtn = new Button("📁 Chọn ảnh");
        selectImageBtn.getStyleClass().add("outline-button");
        selectImageBtn.setOnAction(e -> selectImageFile());
        
        imagePreview.getChildren().addAll(formProductImageView, selectImageBtn);
        
        // URL field
        VBox urlGroup = new VBox(5);
        HBox.setHgrow(urlGroup, Priority.ALWAYS);
        
        Label urlLabel = new Label("URL ảnh");
        urlLabel.getStyleClass().add("form-label");
        
        imageUrlField = new TextField();
        imageUrlField.setPromptText("https://...");
        imageUrlField.getStyleClass().add("form-field");
        
        // Real-time image loading
        imageUrlField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                loadImageFromUrl(newVal.trim());
            }
        });
        
        Label hint = new Label("💡 Bạn có thể nhập URL ảnh hoặc chọn file từ máy tính");
        hint.getStyleClass().add("form-hint");
        
        urlGroup.getChildren().addAll(urlLabel, imageUrlField, hint);
        
        imageRow.getChildren().addAll(imagePreview, urlGroup);
        section.getChildren().addAll(title, imageRow);
        
        return section;
    }

    /**
     * Create status section
     */
    private VBox createStatusSection() {
        VBox section = new VBox();
        section.getStyleClass().add("form-section");
        section.setSpacing(15);
        
        Label title = new Label("⚙️ Cài đặt");
        title.getStyleClass().add("section-title");
        
        isAvailableCheckBox = new CheckBox("Sản phẩm đang bán");
        isAvailableCheckBox.getStyleClass().add("form-checkbox");
        isAvailableCheckBox.setSelected(true);
        
        section.getChildren().addAll(title, isAvailableCheckBox);
        return section;
    }

    /**
     * Create dialog footer with action buttons
     */
    private HBox createDialogFooter() {
        HBox footer = new HBox();
        footer.getStyleClass().add("dialog-footer");
        footer.setAlignment(Pos.CENTER_RIGHT);
        footer.setSpacing(10);
        
        Button cancelBtn = new Button("❌ Hủy");
        cancelBtn.getStyleClass().add("outline-button");
        cancelBtn.setOnAction(e -> hideProductForm());
        
        Button resetBtn = new Button("🔄 Đặt lại");
        resetBtn.getStyleClass().add("secondary-button");
        resetBtn.setOnAction(e -> resetFormFields());
        
        saveProductButton = new Button("💾 Lưu");
        saveProductButton.getStyleClass().add("primary-button");
        saveProductButton.setOnAction(e -> saveProduct());
        
        footer.getChildren().addAll(cancelBtn, resetBtn, saveProductButton);
        return footer;
    }

    /**
     * Update dialog title based on operation
     */
    private void updateDialogTitle(boolean isEdit, Product product) {
        if (productFormStage != null) {
            if (isEdit && product != null) {
                productFormStage.setTitle("✏️ Sửa món: " + product.getProductName());
                if (dialogTitleLabel != null) {
                    dialogTitleLabel.setText("✏️ Sửa món: " + product.getProductName());
                }
            } else {
                productFormStage.setTitle("➕ Thêm món mới");
                if (dialogTitleLabel != null) {
                    dialogTitleLabel.setText("➕ Thêm món mới");
                }
            }
        }
    }

    /**
     * Hide form dialog properly
     */
    private void hideProductForm() {
        try {
            System.out.println("❌ Hiding product form...");
            
            if (productFormStage != null) {
                productFormStage.hide(); // Use hide() instead of close() to reuse
            }
            
            // Reset state
            currentEditingProduct = null;
            
            // Clear validation styles
            clearAllValidationStyles();
            
            System.out.println("✅ Product form hidden successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error hiding product form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Setup dialog event handlers once during initialization
     */
    private void setupDialogEventHandlers() {
        // Window close handler
        if (productFormStage != null) {
            productFormStage.setOnCloseRequest(e -> {
                currentEditingProduct = null;
                clearAllValidationStyles();
            });
        }
        
        // Form validation on field changes
        setupRealTimeValidation();
    }

    /**
     * Setup real-time validation as user types
     */
    private void setupRealTimeValidation() {
        // Will be set up when fields are created in createBasicInfoSection()
        // This method is called during initialization, fields are created later
        Platform.runLater(() -> {
            // Product name validation
            if (productNameField != null) {
                productNameField.textProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null && !newVal.trim().isEmpty()) {
                        removeErrorStyle(productNameField);
                    }
                });
            }
            
            // Price validation
            if (sellingPriceField != null) {
                sellingPriceField.textProperty().addListener((obs, oldVal, newVal) -> {
                    try {
                        if (newVal != null && !newVal.trim().isEmpty()) {
                            double price = Double.parseDouble(newVal.trim());
                            if (price > 0) {
                                removeErrorStyle(sellingPriceField);
                            }
                        }
                    } catch (NumberFormatException e) {
                        // Invalid number - don't show error until save
                    }
                });
            }
            
            // Category validation
            if (categoryComboBox != null) {
                categoryComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        removeErrorStyle(categoryComboBox);
                    }
                });
            }
        });
    }

    // =============================================
    // SIMPLIFIED PUBLIC METHODS
    // =============================================

    /**
     * Show add product form
     */
    public void showAddProductForm() {
        showProductForm(null);
    }

    /**
     * Show edit product form
     */
    public void showEditProductForm() {
        Product selectedProduct = productsTable.getSelectionModel().getSelectedItem();
        if (selectedProduct == null) {
            AlertUtils.showWarning("Chú ý", "Vui lòng chọn sản phẩm cần sửa");
            return;
        }
        showProductForm(selectedProduct);
    }

    // =============================================
    // PRODUCT CRUD OPERATIONS - UPDATED
    // =============================================

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

        // Create new product (not editing existing)
        Product duplicatedProduct = new Product();
        duplicatedProduct.setProductName(original.getProductName() + " (Copy)");
        duplicatedProduct.setCategoryId(original.getCategoryId());
        duplicatedProduct.setPrice(original.getPrice());
        duplicatedProduct.setCostPrice(original.getCostPrice());
        duplicatedProduct.setDescription(original.getDescription());
        duplicatedProduct.setImageUrl(original.getImageUrl());
        duplicatedProduct.setAvailable(original.isAvailable());
        
        currentEditingProduct = null; // Ensure we're creating new
        showProductForm(duplicatedProduct);
    }

    // =============================================
    // FORM VALIDATION AND UTILITIES - IMPROVED
    // =============================================

    /**
     * Improved form validation with visual feedback
     */
    private boolean validateFormWithFeedback() {
        boolean isValid = true;
        
        // Clear previous errors
        clearAllValidationStyles();
        
        // Validate product name
        if (productNameField == null || productNameField.getText().trim().isEmpty()) {
            if (productNameField != null) {
                addErrorStyle(productNameField);
                Platform.runLater(() -> {
                    productNameField.requestFocus();
                    AlertUtils.showWarning("Cảnh báo", "Vui lòng nhập tên sản phẩm");
                });
            }
            return false;
        }
        
        // Validate category
        if (categoryComboBox == null || categoryComboBox.getValue() == null) {
            if (categoryComboBox != null) {
                addErrorStyle(categoryComboBox);
                Platform.runLater(() -> {
                    categoryComboBox.requestFocus();
                    AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn danh mục");
                });
            }
            return false;
        }
        
        // Validate selling price
        if (sellingPriceField != null) {
            try {
                double sellingPrice = Double.parseDouble(sellingPriceField.getText().trim());
                if (sellingPrice <= 0) {
                    addErrorStyle(sellingPriceField);
                    Platform.runLater(() -> {
                        sellingPriceField.requestFocus();
                        AlertUtils.showWarning("Cảnh báo", "Giá bán phải lớn hơn 0");
                    });
                    return false;
                }
            } catch (NumberFormatException e) {
                addErrorStyle(sellingPriceField);
                Platform.runLater(() -> {
                    sellingPriceField.requestFocus();
                    AlertUtils.showWarning("Cảnh báo", "Giá bán không hợp lệ");
                });
                return false;
            }
        }
        
        // Validate cost price (optional)
        if (costPriceField != null) {
            String costPriceText = costPriceField.getText().trim();
            if (!costPriceText.isEmpty()) {
                try {
                    double costPrice = Double.parseDouble(costPriceText);
                    if (costPrice < 0) {
                        addErrorStyle(costPriceField);
                        Platform.runLater(() -> {
                            costPriceField.requestFocus();
                            AlertUtils.showWarning("Cảnh báo", "Giá vốn không được âm");
                        });
                        return false;
                    }
                } catch (NumberFormatException e) {
                    addErrorStyle(costPriceField);
                    Platform.runLater(() -> {
                        costPriceField.requestFocus();
                        AlertUtils.showWarning("Cảnh báo", "Giá vốn không hợp lệ");
                    });
                    return false;
                }
            }
        }
        
        return isValid;
    }

    private Product createProductFromForm() {
        Product product = new Product();
        
        if (productNameField != null) {
            product.setProductName(productNameField.getText().trim());
        }
        if (categoryComboBox != null && categoryComboBox.getValue() != null) {
            product.setCategoryId(categoryComboBox.getValue().getCategoryId());
        }
        if (sellingPriceField != null) {
            product.setPrice(Double.parseDouble(sellingPriceField.getText().trim()));
        }
        
        if (costPriceField != null) {
            String costPriceText = costPriceField.getText().trim();
            if (!costPriceText.isEmpty()) {
                product.setCostPrice(Double.parseDouble(costPriceText));
            } else {
                product.setCostPrice(0.0);
            }
        }
        
        if (descriptionField != null) {
            product.setDescription(descriptionField.getText().trim());
        }
        if (imageUrlField != null) {
            product.setImageUrl(imageUrlField.getText().trim());
        }
        if (isAvailableCheckBox != null) {
            product.setAvailable(isAvailableCheckBox.isSelected());
        }
        
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

            // Set category
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

    /**
     * Reset form fields without nullifying references
     */
    private void resetFormFields() {
        try {
            System.out.println("🔄 Resetting form fields...");
            
            // Clear text fields
            if (productNameField != null) {
                productNameField.clear();
            }
            if (sellingPriceField != null) {
                sellingPriceField.clear();
            }
            if (costPriceField != null) {
                costPriceField.clear();
            }
            if (descriptionField != null) {
                descriptionField.clear();
            }
            if (imageUrlField != null) {
                imageUrlField.clear();
            }
            
            // Reset combobox
            if (categoryComboBox != null) {
                categoryComboBox.setValue(null);
            }
            
            // Reset checkbox
            if (isAvailableCheckBox != null) {
                isAvailableCheckBox.setSelected(true);
            }
            
            // Clear image
            if (formProductImageView != null) {
                Image defaultImage = new Image("file:src/main/resources/images/no-image.png");
                formProductImageView.setImage(defaultImage);
            }
            
            // Clear validation styles
            clearAllValidationStyles();
            
            System.out.println("✅ Form fields reset successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error resetting form: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Clear all validation styles from form fields
     */
    private void clearAllValidationStyles() {
        try {
            if (productNameField != null) {
                productNameField.getStyleClass().removeAll("error-field", "success-field");
                productNameField.setStyle(""); // Clear inline styles
            }
            if (sellingPriceField != null) {
                sellingPriceField.getStyleClass().removeAll("error-field", "success-field");
                sellingPriceField.setStyle("");
            }
            if (costPriceField != null) {
                costPriceField.getStyleClass().removeAll("error-field", "success-field");
                costPriceField.setStyle("");
            }
            if (categoryComboBox != null) {
                categoryComboBox.getStyleClass().removeAll("error-field", "success-field");
                categoryComboBox.setStyle("");
            }
            if (descriptionField != null) {
                descriptionField.getStyleClass().removeAll("error-field", "success-field");
                descriptionField.setStyle("");
            }
            if (imageUrlField != null) {
                imageUrlField.getStyleClass().removeAll("error-field", "success-field");
                imageUrlField.setStyle("");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error clearing validation styles: " + e.getMessage());
        }
    }

    /**
     * Remove error styling from field
     */
    private void removeErrorStyle(Node field) {
        field.getStyleClass().remove("error-field");
        Platform.runLater(() -> {
            field.setStyle("-fx-border-color: #d2691e; -fx-background-color: white;");
        });
    }

    /**
     * Add error styling to field
     */
    private void addErrorStyle(Node field) {
        field.getStyleClass().add("error-field");
        Platform.runLater(() -> {
            field.setStyle("-fx-border-color: #ef4444; -fx-border-width: 2; -fx-background-color: #fef2f2;");
        });
    }

    /**
     * Create category converter for ComboBox
     */
    private StringConverter<Category> createCategoryConverter() {
        return new StringConverter<Category>() {
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
        };
    }

    /**
     * Improved save product method with loading state
     */
    private void saveProduct() {
        if (!validateFormWithFeedback()) {
            return;
        }

        Product product = createProductFromForm();
        boolean isUpdate = (currentEditingProduct != null);

        if (isUpdate) {
            product.setProductId(currentEditingProduct.getProductId());
        }

        // Show loading state
        if (saveProductButton != null) {
            saveProductButton.setDisable(true);
            saveProductButton.setText("💾 Đang lưu...");
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
                    // Restore button state
                    if (saveProductButton != null) {
                        saveProductButton.setDisable(false);
                        saveProductButton.setText("💾 Lưu");
                    }
                    
                    if (getValue()) {
                        String action = isUpdate ? "cập nhật" : "thêm";
                        AlertUtils.showInfo("Thành công", 
                            "Đã " + action + " sản phẩm '" + product.getProductName() + "' thành công");
                        
                        hideProductForm();
                        loadProducts(); // Refresh data
                        
                        // Select the saved/updated product
                        Platform.runLater(() -> selectProductInTable(product));
                        
                    } else {
                        AlertUtils.showError("Lỗi", "Không thể lưu sản phẩm");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    // Restore button state
                    if (saveProductButton != null) {
                        saveProductButton.setDisable(false);
                        saveProductButton.setText("💾 Lưu");
                    }
                    
                    AlertUtils.showError("Lỗi", "Lỗi khi lưu sản phẩm: " + getException().getMessage());
                });
            }
        };

        new Thread(saveTask).start();
    }

    /**
     * Select product in table after save/update
     */
    private void selectProductInTable(Product product) {
        if (product == null || productsTable == null) return;
        
        for (Product p : productsTable.getItems()) {
            if (p.getProductName().equals(product.getProductName())) {
                productsTable.getSelectionModel().select(p);
                productsTable.scrollTo(p);
                break;
            }
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
        previewCreatedDate.setText(product.getCreatedAt() != null ? 
            product.getCreatedAt().format(dateFormatter) : 
            LocalDateTime.now().format(dateFormatter));

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

        // Get current window
        Window window = null;
        if (productFormStage != null) {
            window = productFormStage;
        } else if (productsTable != null && productsTable.getScene() != null) {
            window = productsTable.getScene().getWindow();
        }

        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            String imageUrl = selectedFile.toURI().toString();
            if (imageUrlField != null) {
                imageUrlField.setText(imageUrl);
            }
            loadImageFromUrl(imageUrl);
        }
    }

    private void changeProductImage() {
        if (currentEditingProduct == null) return;
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh sản phẩm");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        Window window = productsTable.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(window);

        if (selectedFile != null) {
            String imageUrl = selectedFile.toURI().toString();
            currentEditingProduct.setImageUrl(imageUrl);
            
            // Save to database
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
                            loadImageFromUrl(imageUrl);
                            AlertUtils.showInfo("Thành công", "Đã cập nhật ảnh sản phẩm");
                        } else {
                            AlertUtils.showError("Lỗi", "Không thể cập nhật ảnh sản phẩm");
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
            // Close any open dialogs
            if (productFormStage != null) {
                productFormStage.close();
            }
            
            // Clear session
            SessionManager.clearSession();
            
            // Return to login screen
            CafeManagementApplication.showLoginScreen();
            
            System.out.println("✅ Logged out successfully");
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
    // CLEANUP ON APPLICATION CLOSE
    // =============================================

    /**
     * Cleanup resources properly
     */
    public void cleanupResources() {
        try {
            System.out.println("🧹 Cleaning up AdminMenuController resources...");
            
            // Close form dialog
            if (productFormStage != null) {
                productFormStage.close();
                productFormStage = null;
            }
            
            // Clear references
            currentEditingProduct = null;
            
            // Clear table selection
            if (productsTable != null) {
                productsTable.getSelectionModel().clearSelection();
            }
            
            isFormInitialized = false;
            
            System.out.println("✅ Resources cleaned up successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error cleaning up resources: " + e.getMessage());
            e.printStackTrace();
        }
    }
}