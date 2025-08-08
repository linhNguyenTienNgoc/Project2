package com.cafe.controller;

import com.cafe.model.entity.Category;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.Product;
import com.cafe.service.MenuService;
import com.cafe.service.OrderService;
import com.cafe.util.PriceFormatter;
import com.cafe.view.component.CategoryButton;
import com.cafe.view.component.OrderItemRow;
import com.cafe.view.component.ProductCard;
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
import javafx.util.Callback;

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

    // Data collections
    private final ObservableList<Category> categories = FXCollections.observableArrayList();
    private final ObservableList<Product> products = FXCollections.observableArrayList();
    private final ObservableList<OrderItemRow> orderItemRows = FXCollections.observableArrayList();

    // Search debouncing
    private final ScheduledExecutorService searchExecutor = Executors.newSingleThreadScheduledExecutor();
    private Timer searchTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupUI();
        setupBindings();
        setupEventHandlers();
        loadInitialData();
    }

    private void setupUI() {
        // Setup product container
        productContainer.setSpacing(10);
        productContainer.setPadding(new Insets(10));
        
        // Setup order items container
        orderItemsContainer.setSpacing(5);
        orderItemsContainer.setPadding(new Insets(10));
        
        // Setup category bar
        categoryBar.setSpacing(10);
        categoryBar.setPadding(new Insets(10));
        
        // Setup search field
        searchField.setPromptText("Tìm kiếm sản phẩm...");
        
        // Setup total amount label
        totalAmountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalAmountLabel.setTextFill(Color.BLACK);
        
        // Setup buttons
        placeOrderButton.setText("Đặt món");
        paymentButton.setText("Thanh toán");
        cancelButton.setText("Hủy bỏ");
        powerButton.setText("Tắt máy");
        
        // Setup loading indicator
        loadingIndicator.setVisible(false);
        loadingIndicator.setMaxSize(50, 50);
        
        // Setup status label
        statusLabel.setText("");
        statusLabel.setTextFill(Color.GRAY);
    }

    private void setupBindings() {
        // Bind total amount to label
        totalAmount.addListener((observable, oldValue, newValue) -> {
            totalAmountText.set(PriceFormatter.formatVND(newValue.doubleValue()));
            Platform.runLater(() -> totalAmountLabel.setText(totalAmountText.get()));
        });
        
        // Bind total amount to place order button
        placeOrderButton.disableProperty().bind(totalAmount.lessThanOrEqualTo(0));
    }

    private void setupEventHandlers() {
        // Search field handler with debouncing
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

                // Place order button
        placeOrderButton.setOnAction(event -> handlePlaceOrder());
        
        // Payment button
        paymentButton.setOnAction(event -> handlePayment());
        
        // Cancel button
        cancelButton.setOnAction(event -> handleCancel());

        // Power button
        powerButton.setOnAction(event -> handlePowerOff());
    }

    private void loadInitialData() {
        showLoading(true, "Đang tải dữ liệu...");
        
        // Load categories
        loadCategories();
        
        // Load all products initially
        loadProducts(null);
        
        // Initialize order
        initializeOrder();
        
        showLoading(false, "Sẵn sàng");
    }

    private void loadCategories() {
        try {
            List<Category> availableCategories = menuService.getAvailableCategories();
            categories.clear();
            categories.addAll(availableCategories);
            
            // Create category buttons
            categoryBar.getChildren().clear();
            
            // Add "Tất cả" button
            CategoryButton allButton = new CategoryButton();
            allButton.setText("Tất cả");
            allButton.setOnCategoryClick(category -> {
                selectedCategory = null;
                loadProducts(null);
                updateCategorySelection(null);
            });
            categoryBar.getChildren().add(allButton);
            
            // Add category buttons
            for (Category category : categories) {
                CategoryButton categoryButton = new CategoryButton(category);
                categoryButton.setOnCategoryClick(this::handleCategorySelection);
                categoryBar.getChildren().add(categoryButton);
            }
            
        } catch (Exception e) {
            showError("Lỗi khi tải danh mục: " + e.getMessage());
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
        } finally {
            showLoading(false, "Sẵn sàng");
        }
    }

    private void displayProducts() {
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
            ProductCard productCard = new ProductCard(product);
            productCard.setOnProductClick(this::handleProductClick);
            
            productGrid.add(productCard, column, row);
            
            column++;
            if (column >= maxColumns) {
                column = 0;
                row++;
            }
        }
        
        productContainer.getChildren().add(productGrid);
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
        for (javafx.scene.Node node : categoryBar.getChildren()) {
            if (node instanceof CategoryButton) {
                CategoryButton button = (CategoryButton) node;
                if (button.getCategory() == null && selectedCategory == null) {
                    button.setSelected(true);
                } else if (button.getCategory() != null && selectedCategory != null && 
                         button.getCategory().getCategoryId() == selectedCategory.getCategoryId()) {
                    button.setSelected(true);
                } else {
                    button.setSelected(false);
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
                updateOrderItemRow(product);
            } else {
                showError("Đã đạt giới hạn số lượng có sẵn");
            }
        } else {
            cartItems.put(product, 1);
            addOrderItemRow(product, 1);
        }
        
        updateTotalAmount();
    }

    private void addOrderItemRow(Product product, int quantity) {
        OrderItemRow orderItemRow = new OrderItemRow(product, quantity);
        orderItemRow.setOnRemove(this::handleRemoveFromCart);
        orderItemRow.setOnQuantityChange(this::handleQuantityChange);
        
        orderItemRows.add(orderItemRow);
        orderItemsContainer.getChildren().add(orderItemRow);
    }

    private void updateOrderItemRow(Product product) {
        for (OrderItemRow row : orderItemRows) {
            if (row.getProduct().getProductId().equals(product.getProductId())) {
                row.setQuantity(cartItems.get(product));
                break;
            }
        }
    }

    private void handleRemoveFromCart(Product product) {
        cartItems.remove(product);
        
        // Remove from order item rows
        OrderItemRow rowToRemove = null;
        for (OrderItemRow row : orderItemRows) {
            if (row.getProduct().getProductId().equals(product.getProductId())) {
                rowToRemove = row;
                break;
            }
        }
        
        if (rowToRemove != null) {
            orderItemRows.remove(rowToRemove);
            orderItemsContainer.getChildren().remove(rowToRemove);
        }
        
        updateTotalAmount();
    }

    private void handleQuantityChange(Product product, int newQuantity) {
        if (newQuantity <= 0) {
            handleRemoveFromCart(product);
        } else if (newQuantity <= product.getStockQuantity()) {
            cartItems.put(product, newQuantity);
            updateTotalAmount();
        } else {
            showError("Số lượng vượt quá tồn kho");
            // Reset to previous valid quantity
            updateOrderItemRow(product);
        }
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
        
        if (currentOrder == null) {
            showError("Chưa khởi tạo đơn hàng");
            return;
        }
        
        try {
            // Add all items to order
            for (Map.Entry<Product, Integer> entry : cartItems.entrySet()) {
                Product product = entry.getKey();
                Integer quantity = entry.getValue();
                
                if (!orderService.addProductToOrder(currentOrder, product, quantity)) {
                    showError("Lỗi khi thêm sản phẩm vào đơn hàng: " + product.getProductName());
                    return;
                }
            }
            
            // Place the order
            if (orderService.placeOrder(currentOrder)) {
                showSuccess("Đặt hàng thành công! Mã đơn hàng: " + currentOrder.getOrderId());
                handleClearCart();
                initializeOrder(); // Create new order for next use
            } else {
                showError("Lỗi khi đặt hàng");
            }
            
        } catch (Exception e) {
            showError("Lỗi khi đặt hàng: " + e.getMessage());
        }
    }

    private void handleClearCart() {
        cartItems.clear();
        orderItemRows.clear();
        orderItemsContainer.getChildren().clear();
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
            // TODO: Implement payment processing
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
            loadingIndicator.setVisible(show);
            statusLabel.setText(message);
        });
    }

    private void showError(String message) {
        Platform.runLater(() -> {
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
        });
    }

    private void showSuccess(String message) {
        Platform.runLater(() -> {
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
        });
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
