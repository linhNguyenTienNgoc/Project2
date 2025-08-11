package com.cafe.controller;

import com.cafe.CafeManagementApplication;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.Product;
import com.cafe.service.MenuService;
import com.cafe.service.OrderService;
import com.cafe.util.PriceFormatter;
// View components handled inline for simplicity
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MenuController implements Initializable {

    // Services
    private final MenuService menuService = new MenuService();
    private final OrderService orderService = new OrderService();

    // Current state
    private Category selectedCategory;
    private Order currentOrder;
    private final Map<Product, Integer> cartItems = new HashMap<>();
    private final SimpleDoubleProperty totalAmount = new SimpleDoubleProperty(0.0);
    private final SimpleStringProperty totalAmountText = new SimpleStringProperty("0 ₫");

    // UI Components
    @FXML private VBox menuContainer;
    @FXML private HBox categoryBar;
    @FXML private TextField searchField;
    @FXML private Button searchBtn;
    @FXML private ScrollPane productScrollPane;
    @FXML private VBox productContainer;
    @FXML private VBox orderCartContainer;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalAmountLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button paymentButton;
    @FXML private Button cancelButton;
    @FXML private Button powerButton;
    @FXML private ProgressIndicator loadingIndicator;
    @FXML private Label statusLabel;
    
    // Navigation buttons
    @FXML private Button menuTab;
    @FXML private Button tableTab;
    @FXML private Button menuListTab;
    
    // Table search
    @FXML private TextField tableSearchField;

    // Data collections
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    // Order item rows handled as HBox components

    // Search debouncing
    private final ScheduledExecutorService searchExecutor = Executors.newSingleThreadScheduledExecutor();
    private Timer searchTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Verify FXML injection
            verifyFXMLInjection();
            
            setupUI();
            setupBindings();
            setupEventHandlers();
            loadInitialData();
            
            System.out.println("✅ MenuController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing MenuController: " + e.getMessage());
            e.printStackTrace();
            // Show user-friendly error message
            showError("Lỗi khởi tạo Menu: " + e.getMessage());
        }
    }
    
    /**
     * Verify that all required FXML elements are properly injected
     */
    private void verifyFXMLInjection() {
        StringBuilder missingElements = new StringBuilder();
        
        if (menuContainer == null) missingElements.append("menuContainer, ");
        if (categoryBar == null) missingElements.append("categoryBar, ");
        if (searchField == null) missingElements.append("searchField, ");
        if (searchBtn == null) missingElements.append("searchBtn, ");
        if (productScrollPane == null) missingElements.append("productScrollPane, ");
        if (productContainer == null) missingElements.append("productContainer, ");
        if (orderCartContainer == null) missingElements.append("orderCartContainer, ");
        if (orderItemsContainer == null) missingElements.append("orderItemsContainer, ");
        if (totalAmountLabel == null) missingElements.append("totalAmountLabel, ");
        if (placeOrderButton == null) missingElements.append("placeOrderButton, ");
        if (paymentButton == null) missingElements.append("paymentButton, ");
        if (cancelButton == null) missingElements.append("cancelButton, ");
        if (powerButton == null) missingElements.append("powerButton, ");
        if (menuTab == null) missingElements.append("menuTab, ");
        if (tableTab == null) missingElements.append("tableTab, ");
        if (menuListTab == null) missingElements.append("menuListTab, ");
        if (tableSearchField == null) missingElements.append("tableSearchField, ");
        
        if (missingElements.length() > 0) {
            String missing = missingElements.substring(0, missingElements.length() - 2);
            System.err.println("⚠️ Warning: Missing FXML elements: " + missing);
            System.err.println("This may cause NullPointerException in some features");
        }
    }

    private void setupUI() {
        // Setup product container
        if (productContainer != null) {
            productContainer.setSpacing(10);
            productContainer.setPadding(new Insets(10));
        }
        
        // Setup order items container
        if (orderItemsContainer != null) {
            orderItemsContainer.setSpacing(5);
            orderItemsContainer.setPadding(new Insets(10));
        }
        
        // Setup category bar
        if (categoryBar != null) {
            categoryBar.setSpacing(10);
            categoryBar.setPadding(new Insets(10));
        }
        
        // Setup search field
        if (searchField != null) {
            searchField.setPromptText("Tìm kiếm sản phẩm...");
        }
        
        // Setup table search field
        if (tableSearchField != null) {
            tableSearchField.setPromptText("Tìm bàn...");
        }
        
        // Setup total amount label
        if (totalAmountLabel != null) {
            totalAmountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            totalAmountLabel.setTextFill(Color.BLACK);
        }
        
        // Setup navigation tabs
        if (menuTab != null) {
            menuTab.setText("Menu");
            menuTab.getStyleClass().add("active-tab");
        }
        if (tableTab != null) {
            tableTab.setText("Bàn");
        }
        if (menuListTab != null) {
            menuListTab.setText("Thực đơn");
        }
        
        // Setup buttons
        if (placeOrderButton != null) placeOrderButton.setText("Đặt món");
        if (paymentButton != null) paymentButton.setText("Thanh toán");
        if (cancelButton != null) cancelButton.setText("Hủy bỏ");
        if (powerButton != null) powerButton.setText("⏻");
        
        // Setup search button
        if (searchBtn != null) {
            searchBtn.setText("🔍");
            searchBtn.setTooltip(new Tooltip("Tìm kiếm sản phẩm"));
        }
        
        // Setup loading indicator
        if (loadingIndicator != null) {
            loadingIndicator.setVisible(false);
            loadingIndicator.setMaxSize(50, 50);
        }
        
        // Setup status label
        if (statusLabel != null) {
            statusLabel.setText("Sẵn sàng");
            statusLabel.setTextFill(Color.GRAY);
        }
    }

    private void setupBindings() {
        // Bind total amount to label
        totalAmount.addListener((observable, oldValue, newValue) -> {
            totalAmountText.set(PriceFormatter.formatVND(newValue.doubleValue()));
            Platform.runLater(() -> {
                if (totalAmountLabel != null) {
                    totalAmountLabel.setText(totalAmountText.get());
                }
            });
        });
        
        // Bind total amount to place order button
        if (placeOrderButton != null) {
            placeOrderButton.disableProperty().bind(totalAmount.lessThanOrEqualTo(0));
        }
    }

    private void setupEventHandlers() {
        // Search field handler with debouncing
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (searchTimer != null) {
                    searchTimer.cancel();
                }
                searchTimer = new Timer();
                searchTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> performSearch(newValue));
                    }
                }, 300); // 300ms delay
            });
        }

        // Search button
        if (searchBtn != null) {
            searchBtn.setOnAction(event -> {
                String keyword = searchField != null ? searchField.getText() : "";
                performSearch(keyword);
            });
        }

        // Navigation buttons - with null safety
        if (menuTab != null) {
            menuTab.setOnAction(event -> handleMenuTab());
        }
        
        if (tableTab != null) {
            tableTab.setOnAction(event -> handleTableTab());
        }
        
        if (menuListTab != null) {
            menuListTab.setOnAction(event -> handleMenuListTab());
        }

        // Table search field
        if (tableSearchField != null) {
            tableSearchField.textProperty().addListener((observable, oldValue, newValue) -> {
                handleTableSearch(newValue);
            });
        }

        // Place order button
        if (placeOrderButton != null) {
            placeOrderButton.setOnAction(event -> handlePlaceOrder());
        }
        
        // Payment button
        if (paymentButton != null) {
            paymentButton.setOnAction(event -> handlePayment());
        }
        
        // Cancel button
        if (cancelButton != null) {
            cancelButton.setOnAction(event -> handleCancel());
        }

        // Power button
        if (powerButton != null) {
            powerButton.setOnAction(event -> handlePowerOff());
        }
    }

    private void loadInitialData() {
        showLoading(true, "Đang tải dữ liệu...");
        
        try {
            // Load categories
            loadCategories();
            
            // Load all products initially
            loadProducts(null);
            
            // Initialize order
            initializeOrder();
            
            showLoading(false, "Sẵn sàng");
        } catch (Exception e) {
            showError("Lỗi khi tải dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        try {
            List<Category> availableCategories = menuService.getAvailableCategories();
            categories.clear();
            categories.addAll(availableCategories);
            
            if (categoryBar != null) {
                // Create category buttons
                categoryBar.getChildren().clear();
                
                // Add "Tất cả" button
                Button allButton = new Button("Tất cả");
                allButton.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;");
                allButton.setOnAction(event -> {
                    selectedCategory = null;
                    loadProducts(null);
                    updateCategorySelection(null);
                });
                categoryBar.getChildren().add(allButton);
                
                // Add category buttons
                for (Category category : categories) {
                    Button categoryButton = new Button(category.getCategoryName());
                    categoryButton.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-background-radius: 20; -fx-padding: 8 16;");
                    categoryButton.setOnAction(event -> handleCategorySelection(category));
                    categoryBar.getChildren().add(categoryButton);
                }
            }
            
        } catch (Exception e) {
            showError("Lỗi khi tải danh mục: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadProducts(Integer categoryId) {
        showLoading(true, "Đang tải sản phẩm...");
        
        try {
            List<Product> productList;
            if (categoryId != null) {
                productList = menuService.getProductsByCategory(categoryId);
            } else {
                productList = menuService.getAllAvailableProducts();
            }
            
            products.clear();
            products.addAll(productList);
            
            displayProducts();
            
        } catch (Exception e) {
            showError("Lỗi khi tải sản phẩm: " + e.getMessage());
            e.printStackTrace();
        } finally {
            showLoading(false, "Sẵn sàng");
        }
    }

    private void displayProducts() {
        if (productContainer == null) return;
        
        productContainer.getChildren().clear();
        
        if (products.isEmpty()) {
            Label emptyLabel = new Label("Không có sản phẩm nào");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            productContainer.getChildren().add(emptyLabel);
            return;
        }
        
        // Create product grid
        GridPane productGrid = new GridPane();
        productGrid.setHgap(15);
        productGrid.setVgap(15);
        productGrid.setPadding(new Insets(10));
        
        int column = 0;
        int row = 0;
        int maxColumns = 4; // Adjust based on screen size
        
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.add(productCard, column, row);
            
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
        
        productContainer.getChildren().add(productGrid);
    }

    private VBox createProductCard(Product product) {
        VBox card = new VBox();
        card.setSpacing(8);
        card.setPadding(new Insets(10));
        card.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        card.setMinWidth(170);
        card.setMaxWidth(170);
        card.setMinHeight(220);
        card.setMaxHeight(220);
        
        // Product name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(150);
        
        // Product price
        Label priceLabel = new Label(product.getFormattedPrice());
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E67E22; -fx-font-size: 16px;");
        
        // Stock status
        Label stockLabel = new Label(product.isOutOfStock() ? "HẾT HÀNG" : "CÒN HÀNG");
        stockLabel.setStyle(product.isOutOfStock() ? 
            "-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 6;" :
            "-fx-background-color: #27AE60; -fx-text-fill: white; -fx-background-radius: 10; -fx-padding: 2 6;");
        stockLabel.setMaxWidth(80);
        
        card.getChildren().addAll(nameLabel, priceLabel, stockLabel);
        
        // Click handler
        card.setOnMouseClicked(event -> handleProductClick(product));
        
        // Hover effects
        card.setOnMouseEntered(event -> {
            card.setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #2E86AB; -fx-border-width: 2; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        
        card.setOnMouseExited(event -> {
            card.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8; -fx-cursor: hand;");
        });
        
        return card;
    }

    private void performSearch(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // If search is empty, load products for current category
            loadProducts(selectedCategory != null ? selectedCategory.getCategoryId() : null);
            return;
        }
        
        showLoading(true, "Đang tìm kiếm...");
        
        try {
            List<Product> searchResults;
            if (selectedCategory != null) {
                searchResults = menuService.searchProducts(keyword.trim(), selectedCategory.getCategoryId());
            } else {
                searchResults = menuService.searchProducts(keyword.trim());
            }
            
            products.clear();
            products.addAll(searchResults);
            displayProducts();
            
        } catch (Exception e) {
            showError("Lỗi khi tìm kiếm: " + e.getMessage());
        } finally {
            showLoading(false, "Sẵn sàng");
        }
    }

    private void handleCategorySelection(Category category) {
        selectedCategory = category;
        loadProducts(category.getCategoryId());
        updateCategorySelection(category);
    }

    private void updateCategorySelection(Category selectedCategory) {
        // Simple implementation - reset all button styles and highlight selected
        if (categoryBar != null) {
            for (javafx.scene.Node node : categoryBar.getChildren()) {
                if (node instanceof Button) {
                    Button button = (Button) node;
                    if (selectedCategory == null && button.getText().equals("Tất cả")) {
                        button.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;");
                    } else if (selectedCategory != null && button.getText().equals(selectedCategory.getCategoryName())) {
                        button.setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-background-radius: 20; -fx-padding: 8 16;");
                    } else {
                        button.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-background-radius: 20; -fx-padding: 8 16;");
                    }
                }
            }
        }
    }

    private void handleProductClick(Product product) {
        if (!product.canBeOrdered()) {
            showError("Sản phẩm này hiện không khả dụng");
            return;
        }
        
        // Check if product is already in cart
        if (cartItems.containsKey(product)) {
            int currentQuantity = cartItems.get(product);
            if (currentQuantity < product.getStockQuantity()) {
                cartItems.put(product, currentQuantity + 1);
                updateOrderDisplay();
            } else {
                showError("Đã đạt giới hạn số lượng có sẵn");
            }
        } else {
            cartItems.put(product, 1);
            updateOrderDisplay();
        }
        
        updateTotalAmount();
    }

    private void updateOrderDisplay() {
        if (orderItemsContainer == null) return;
        
        orderItemsContainer.getChildren().clear();
        
        for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            
            HBox orderItem = createOrderItemRow(product, quantity);
            orderItemsContainer.getChildren().add(orderItem);
        }
    }

    private HBox createOrderItemRow(Product product, int quantity) {
        HBox row = new HBox();
        row.setSpacing(10);
        row.setPadding(new Insets(8, 10, 8, 10));
        row.setStyle("-fx-background-color: white; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;");
        
        // Product name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #495057;");
        nameLabel.setPrefWidth(120);
        nameLabel.setMaxWidth(120);
        
        // Quantity spinner
        Spinner<Integer> quantitySpinner = new Spinner<>(1, 99, quantity);
        quantitySpinner.setPrefWidth(60);
        quantitySpinner.setMaxWidth(60);
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal != oldVal) {
                cartItems.put(product, newVal);
                updateTotalAmount();
            }
        });
        
        // Total price
        Label priceLabel = new Label(String.format("%,.0f VNĐ", product.getPrice() * quantity));
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E67E22;");
        priceLabel.setPrefWidth(100);
        priceLabel.setMaxWidth(100);
        
        // Remove button
        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-background-radius: 50; -fx-font-weight: bold;");
        removeButton.setPrefSize(25, 25);
        removeButton.setMaxSize(25, 25);
        removeButton.setOnAction(event -> {
            cartItems.remove(product);
            updateOrderDisplay();
            updateTotalAmount();
        });
        
        row.getChildren().addAll(nameLabel, quantitySpinner, priceLabel, removeButton);
        return row;
    }

    private void updateTotalAmount() {
        double total = orderService.calculateTotalAmount(cartItems);
        totalAmount.set(total);
    }

    private void handlePlaceOrder() {
        if (cartItems.isEmpty()) {
            showError("Giỏ hàng trống");
            return;
        }
        
        try {
            showSuccess("Đặt hàng thành công! Tổng tiền: " + totalAmountText.get());
            handleClearCart();
            
        } catch (Exception e) {
            showError("Lỗi khi đặt hàng: " + e.getMessage());
        }
    }

    private void handleClearCart() {
        cartItems.clear();
        updateOrderDisplay();
        updateTotalAmount();
    }

    private void handlePayment() {
        if (cartItems.isEmpty()) {
            showError("Giỏ hàng trống!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận thanh toán");
        alert.setHeaderText("Thanh toán đơn hàng");
        alert.setContentText("Tổng tiền: " + totalAmountText.get() + "\nBạn có chắc chắn muốn thanh toán?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            showSuccess("Thanh toán thành công!");
            handleClearCart();
        }
    }

    private void handleCancel() {
        if (cartItems.isEmpty()) {
            showError("Giỏ hàng trống!");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận hủy");
        alert.setHeaderText("Hủy đơn hàng");
        alert.setContentText("Bạn có chắc chắn muốn hủy đơn hàng này?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            handleClearCart();
            showSuccess("Đã hủy đơn hàng!");
        }
    }

    private void initializeOrder() {
        try {
            // For demo purposes, using table ID 1 and user ID 1
            // In real application, these would come from session/context
            currentOrder = orderService.createOrder(1, 1);
        } catch (Exception e) {
            showError("Lỗi khi khởi tạo đơn hàng: " + e.getMessage());
        }
    }

    private void handlePowerOff() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Tắt máy");
        alert.setContentText("Bạn có chắc chắn muốn tắt máy?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    private void showLoading(boolean show, String message) {
        Platform.runLater(() -> {
            if (loadingIndicator != null) {
                loadingIndicator.setVisible(show);
            }
            if (statusLabel != null) {
                statusLabel.setText(message);
            }
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText("Lỗi: " + message);
                statusLabel.setTextFill(Color.RED);
                
                // Auto-clear error after 5 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            statusLabel.setText("Sẵn sàng");
                            statusLabel.setTextFill(Color.GRAY);
                        });
                    }
                }, 5000);
            }
        });
    }

    private void showSuccess(String message) {
        Platform.runLater(() -> {
            if (statusLabel != null) {
                statusLabel.setText(message);
                statusLabel.setTextFill(Color.GREEN);
                
                // Auto-clear success after 3 seconds
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(() -> {
                            statusLabel.setText("Sẵn sàng");
                            statusLabel.setTextFill(Color.GRAY);
                        });
                    }
                }, 3000);
            }
        });
    }

    // ===== NAVIGATION HANDLERS =====
    
    /**
     * Xử lý khi click vào tab Menu
     */
    private void handleMenuTab() {
        // Already on menu tab, just refresh
        loadProducts(null);
        updateNavigationTabs(menuTab);
        showSuccess("Đã chuyển đến Menu");
    }
    
    /**
     * Xử lý khi click vào tab Bàn
     */
    private void handleTableTab() {
        try {
            // Navigate to table management screen
            CafeManagementApplication.showTable();
        } catch (Exception e) {
            showError("Không thể chuyển đến màn hình quản lý bàn: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý khi click vào tab Thực đơn
     */
    private void handleMenuListTab() {
        try {
            // Navigate to menu list screen (if exists)
            // For now, just show a message
            showSuccess("Chức năng Thực đơn đang được phát triển");
        } catch (Exception e) {
            showError("Không thể chuyển đến màn hình thực đơn: " + e.getMessage());
        }
    }
    
    /**
     * Xử lý tìm kiếm bàn
     */
    private void handleTableSearch(String searchText) {
        if (searchText == null || searchText.trim().isEmpty()) {
            // Clear search, show all tables
            showSuccess("Hiển thị tất cả bàn");
            return;
        }
        
        try {
            // TODO: Implement table search functionality
            // This would typically search through available tables
            showSuccess("Tìm kiếm bàn: " + searchText);
        } catch (Exception e) {
            showError("Lỗi tìm kiếm bàn: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật trạng thái active của navigation tabs
     */
    private void updateNavigationTabs(Button activeTab) {
        // Reset all tabs - with null safety
        if (menuTab != null) {
            menuTab.getStyleClass().remove("active-tab");
            menuTab.getStyleClass().add("nav-tab");
        }
        if (tableTab != null) {
            tableTab.getStyleClass().remove("active-tab");
            tableTab.getStyleClass().add("nav-tab");
        }
        if (menuListTab != null) {
            menuListTab.getStyleClass().remove("active-tab");
            menuListTab.getStyleClass().add("nav-tab");
        }
        
        // Set active tab - with null safety
        if (activeTab != null) {
            activeTab.getStyleClass().remove("nav-tab");
            activeTab.getStyleClass().add("active-tab");
        }
    }

    // Cleanup method to be called when controller is destroyed
    public void cleanup() {
        if (searchExecutor != null && !searchExecutor.isShutdown()) {
            searchExecutor.shutdown();
        }
        if (searchTimer != null) {
            searchTimer.cancel();
        }
    }
}
