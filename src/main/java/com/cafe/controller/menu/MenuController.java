package com.cafe.controller.menu;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.base.DashboardHelper;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;
import com.cafe.service.MenuService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.concurrent.Task;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho menu layout - UPDATED với Dashboard Communication
 * Quản lý hiển thị danh sách sản phẩm và categories
 *
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced Communication)
 */
public class MenuController implements Initializable, DashboardCommunicator {

    @FXML private VBox menuLayoutContainer;
    @FXML private HBox categoryBar;
    @FXML private Button allCategoryBtn;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;
    @FXML private ScrollPane productScrollPane;
    @FXML private VBox productContainer;
    @FXML private GridPane productGrid;
    @FXML private VBox emptyStateContainer;
    @FXML private Label emptyStateLabel;

    // Services
    private MenuService menuService;

    // Current state
    private List<Category> categories;
    private List<Product> currentProducts;
    private Integer selectedCategoryId = null;
    private String currentSearchKeyword = "";

    // Grid configuration
    private static final int PRODUCTS_PER_ROW = 4;
    private static final double PRODUCT_CARD_WIDTH = 200;
    private static final double PRODUCT_CARD_HEIGHT = 280;

    // ✅ Dashboard communication
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialize service
            menuService = new MenuService();

            // Setup UI components
            setupSearchField();
            setupLoadingIndicator();

            // Load initial data
            loadInitialData();

            System.out.println("✅ MenuController initialized successfully");

        } catch (Exception e) {
            System.err.println("❌ Error initializing MenuController: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể khởi tạo menu. Vui lòng kiểm tra kết nối database.");
        }
    }

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ MenuController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }

    /**
     * Setup search field behavior
     */
    private void setupSearchField() {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            currentSearchKeyword = newValue != null ? newValue.trim() : "";
            // Delay search to avoid too many requests
            delayedSearch();
        });

        searchBtn.setOnAction(e -> performSearch());
    }

    /**
     * Setup loading indicator
     */
    private void setupLoadingIndicator() {
        loadingIndicator.setVisible(false);
        loadingIndicator.setManaged(false);
    }

    /**
     * Load initial data (categories and products)
     */
    private void loadInitialData() {
        showLoading(true);

        Task<Void> loadDataTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Load categories
                    updateMessage("Đang tải danh mục...");
                    categories = menuService.getAvailableCategories();

                    // Load all products initially
                    updateMessage("Đang tải sản phẩm...");
                    currentProducts = menuService.getAllAvailableProducts();

                    return null;
                } catch (Exception e) {
                    System.err.println("Error loading initial data: " + e.getMessage());
                    throw e;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    try {
                        buildCategoryBar();
                        displayProducts(currentProducts);
                        showLoading(false);
                        updateStatus("Đã tải " + currentProducts.size() + " sản phẩm");
                    } catch (Exception e) {
                        System.err.println("Error updating UI: " + e.getMessage());
                        showError("Lỗi hiển thị dữ liệu");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Không thể tải dữ liệu menu");
                });
            }
        };

        statusLabel.textProperty().bind(loadDataTask.messageProperty());
        new Thread(loadDataTask).start();
    }

    /**
     * Build category buttons dynamically
     */
    private void buildCategoryBar() {
        categoryBar.getChildren().clear();

        // Add "All" button
        Button allBtn = createCategoryButton("Tất cả", null, true);
        categoryBar.getChildren().add(allBtn);

        // Add category buttons
        for (Category category : categories) {
            Button categoryBtn = createCategoryButton(category.getCategoryName(), category.getCategoryId(), false);
            categoryBar.getChildren().add(categoryBtn);
        }
    }

    /**
     * Create category button
     */
    private Button createCategoryButton(String text, Integer categoryId, boolean isActive) {
        Button btn = new Button(text);

        // Set style based on active state
        if (isActive) {
            btn.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;");
        } else {
            btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
        }

        // Set action
        btn.setOnAction(e -> selectCategory(categoryId, btn));

        return btn;
    }

    /**
     * Select category and filter products
     */
    private void selectCategory(Integer categoryId, Button selectedButton) {
        // Update active button styling
        categoryBar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
            }
        });

        selectedButton.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;");

        // Update selected category
        selectedCategoryId = categoryId;

        // Load products for selected category
        loadProductsByCategory(categoryId);
    }

    /**
     * Load products by category
     */
    private void loadProductsByCategory(Integer categoryId) {
        showLoading(true);

        Task<List<Product>> loadProductsTask = new Task<List<Product>>() {
            @Override
            protected List<Product> call() throws Exception {
                if (categoryId == null) {
                    updateMessage("Đang tải tất cả sản phẩm...");
                    return menuService.getAllAvailableProducts();
                } else {
                    updateMessage("Đang tải sản phẩm theo danh mục...");
                    return menuService.getProductsByCategory(categoryId);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Product> products = getValue();
                    currentProducts = products;
                    displayProducts(products);
                    showLoading(false);
                    updateStatus("Đã tải " + products.size() + " sản phẩm");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Không thể tải sản phẩm");
                });
            }
        };

        statusLabel.textProperty().bind(loadProductsTask.messageProperty());
        new Thread(loadProductsTask).start();
    }

    /**
     * Display products in grid
     */
    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear();

        if (products == null || products.isEmpty()) {
            showEmptyState(true);
            return;
        }

        showEmptyState(false);

        // Configure grid
        productGrid.setHgap(15);
        productGrid.setVgap(15);
        productGrid.setPadding(new Insets(10));

        // Add products to grid
        int row = 0;
        int col = 0;

        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.add(productCard, col, row);

            col++;
            if (col >= PRODUCTS_PER_ROW) {
                col = 0;
                row++;
            }
        }
    }

    /**
     * ✅ ENHANCED: Create product card UI component với Dashboard communication
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(10);
        card.setPrefWidth(PRODUCT_CARD_WIDTH);
        card.setPrefHeight(PRODUCT_CARD_HEIGHT);
        card.setAlignment(Pos.CENTER);
        card.setStyle("""
            -fx-background-color: white;
            -fx-border-color: #e0e0e0;
            -fx-border-width: 1;
            -fx-border-radius: 8;
            -fx-background-radius: 8;
            -fx-padding: 15;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
            """);

        // Product image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(160);
        imageView.setFitHeight(120);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

        // Try to load product image
        try {
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                Image image = new Image(product.getImageUrl(), false);
                imageView.setImage(image);
            } else {
                // Use placeholder image
                imageView.setStyle("-fx-background-color: #f5f5f5;");
            }
        } catch (Exception e) {
            // Use placeholder on error
            imageView.setStyle("-fx-background-color: #f5f5f5;");
        }

        // Product name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #333;");
        nameLabel.setWrapText(true);
        nameLabel.setAlignment(Pos.CENTER);
        nameLabel.setMaxWidth(PRODUCT_CARD_WIDTH - 30);

        // Product price
        Label priceLabel = new Label(product.getFormattedPrice());
        priceLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #E67E22;");

        // ✅ ENHANCED: Add to order button với Dashboard communication
        Button addButton = createAddToOrderButton(product);

        // Hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle(card.getStyle() + "; -fx-border-color: #8B4513; -fx-effect: dropshadow(gaussian, rgba(139,69,19,0.3), 8, 0, 0, 3);");
        });

        card.setOnMouseExited(e -> {
            card.setStyle("""
                -fx-background-color: white;
                -fx-border-color: #e0e0e0;
                -fx-border-width: 1;
                -fx-border-radius: 8;
                -fx-background-radius: 8;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 5, 0, 0, 2);
                """);
        });

        card.getChildren().addAll(imageView, nameLabel, priceLabel, addButton);

        return card;
    }

    /**
     * ✅ NEW: Create add to order button với enhanced functionality
     */
    private Button createAddToOrderButton(Product product) {
        Button addButton = new Button("Thêm vào đơn");

        // Base button styling
        String baseStyle = """
            -fx-font-weight: bold;
            -fx-padding: 8 12;
            -fx-background-radius: 5;
            -fx-min-width: 120;
            """;

        // ✅ HỦY CHECK HẾT HÀNG - Luôn cho phép order
        addButton.setStyle(baseStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");

        // ✅ Set click action với Dashboard communication
        addButton.setOnAction(e -> handleAddToOrder(product));

        // Enhanced hover effects
        addButton.setOnMouseEntered(e -> {
            addButton.setStyle(baseStyle + "-fx-background-color: #218838; -fx-text-fill: white; -fx-cursor: hand;");
        });

        addButton.setOnMouseExited(e -> {
            addButton.setStyle(baseStyle + "-fx-background-color: #28a745; -fx-text-fill: white;");
        });

        return addButton;
    }

    /**
     * ✅ NEW: Handle add to order với Dashboard communication
     */
    private void handleAddToOrder(Product product) {
        try {
            System.out.println("🛒 Adding product to order: " + product.getProductName());

            // ✅ Communicate with Dashboard/OrderPanel
            DashboardHelper.addProductToOrder(dashboardController, product, 1);

            // ✅ Visual feedback
            showAddToOrderSuccess(product);

            // ✅ Log the action
            System.out.println("✅ Product added successfully: " + product.getProductName() + " - " + product.getFormattedPrice());

        } catch (Exception e) {
            System.err.println("❌ Error adding product to order: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể thêm sản phẩm vào đơn hàng: " + product.getProductName());
        }
    }

    /**
     * ✅ NEW: Show success feedback when product added
     */
    private void showAddToOrderSuccess(Product product) {
        // Option 1: Show temporary success message
        Platform.runLater(() -> {
            updateStatus("✅ Đã thêm " + product.getProductName() + " vào đơn hàng");

            // Reset status after 3 seconds
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Platform.runLater(() -> {
                        updateStatus("Sẵn sàng");
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });

        // Option 2: Could also show toast notification
        // showToastNotification("Đã thêm " + product.getProductName());
    }

    /**
     * Perform search
     */
    private void performSearch() {
        if (currentSearchKeyword.isEmpty()) {
            loadProductsByCategory(selectedCategoryId);
            return;
        }

        showLoading(true);

        Task<List<Product>> searchTask = new Task<List<Product>>() {
            @Override
            protected List<Product> call() throws Exception {
                updateMessage("Đang tìm kiếm...");
                return menuService.searchProducts(currentSearchKeyword, selectedCategoryId);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    List<Product> products = getValue();
                    currentProducts = products;
                    displayProducts(products);
                    showLoading(false);
                    updateStatus("Tìm thấy " + products.size() + " sản phẩm");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showLoading(false);
                    showError("Lỗi tìm kiếm");
                });
            }
        };

        statusLabel.textProperty().bind(searchTask.messageProperty());
        new Thread(searchTask).start();
    }

    /**
     * Delayed search to avoid too many requests
     */
    private void delayedSearch() {
        // Simple debounce implementation
        Task<Void> delayTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(500); // 500ms delay
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> performSearch());
            }
        };

        new Thread(delayTask).start();
    }

    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        loadingIndicator.setManaged(show);
    }

    /**
     * Show/hide empty state
     */
    private void showEmptyState(boolean show) {
        emptyStateContainer.setVisible(show);
        emptyStateContainer.setManaged(show);
        productGrid.setVisible(!show);
        productGrid.setManaged(!show);

        if (show) {
            if (currentSearchKeyword.isEmpty()) {
                emptyStateLabel.setText("Không có sản phẩm nào trong danh mục này");
            } else {
                emptyStateLabel.setText("Không tìm thấy sản phẩm với từ khóa: " + currentSearchKeyword);
            }
        }
    }

    /**
     * Update status message
     */
    private void updateStatus(String message) {
        statusLabel.textProperty().unbind();
        statusLabel.setText(message);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // =====================================================
    // ✅ PUBLIC METHODS FOR EXTERNAL ACCESS
    // =====================================================

    /**
     * Refresh products (public method for external calls)
     */
    public void refreshProducts() {
        loadProductsByCategory(selectedCategoryId);
    }

    /**
     * Get current products
     */
    public List<Product> getCurrentProducts() {
        return currentProducts;
    }

    /**
     * Get selected category ID
     */
    public Integer getSelectedCategoryId() {
        return selectedCategoryId;
    }

    /**
     * Search products externally
     */
    public void searchProducts(String keyword) {
        searchField.setText(keyword);
        performSearch();
    }

    /**
     * Select category externally
     */
    public void selectCategoryById(Integer categoryId) {
        selectedCategoryId = categoryId;
        loadProductsByCategory(categoryId);

        // Update button styling
        categoryBar.getChildren().forEach(node -> {
            if (node instanceof Button) {
                Button btn = (Button) node;
                // Reset and set active based on category
                // This could be enhanced to properly track which button corresponds to which category
            }
        });
    }

    /**
     * ✅ NEW: Quick add product method for testing
     */
    public void quickAddProduct(String productName) {
        if (currentProducts != null) {
            currentProducts.stream()
                    .filter(p -> p.getProductName().toLowerCase().contains(productName.toLowerCase()))
                    .findFirst()
                    .ifPresent(this::handleAddToOrder);
        }
    }
}