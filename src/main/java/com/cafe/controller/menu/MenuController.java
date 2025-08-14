package com.cafe.controller.menu;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;
import com.cafe.service.MenuService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho menu layout
 * Quản lý hiển thị sản phẩm và tương tác với order panel
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class MenuController implements Initializable {
    
    // FXML Elements
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
    private final MenuService menuService = new MenuService();
    
    // Current state
    private Category selectedCategory;
    private List<Product> currentProducts;
    
    // Reference to parent dashboard controller for order panel communication
    private DashboardController dashboardController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            verifyFXMLInjection();
            setupEventHandlers();
            loadCategories();
            loadProducts();
            
            System.out.println("✅ MenuController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing MenuController: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khởi tạo Menu: " + e.getMessage());
        }
    }
    
    /**
     * Verify that all required FXML elements are properly injected
     */
    private void verifyFXMLInjection() {
        StringBuilder missingElements = new StringBuilder();
        
        if (menuLayoutContainer == null) missingElements.append("menuLayoutContainer, ");
        if (categoryBar == null) missingElements.append("categoryBar, ");
        if (allCategoryBtn == null) missingElements.append("allCategoryBtn, ");
        if (searchField == null) missingElements.append("searchField, ");
        if (searchBtn == null) missingElements.append("searchBtn, ");
        if (loadingIndicator == null) missingElements.append("loadingIndicator, ");
        if (statusLabel == null) missingElements.append("statusLabel, ");
        if (productScrollPane == null) missingElements.append("productScrollPane, ");
        if (productContainer == null) missingElements.append("productContainer, ");
        if (productGrid == null) missingElements.append("productGrid, ");
        if (emptyStateContainer == null) missingElements.append("emptyStateContainer, ");
        if (emptyStateLabel == null) missingElements.append("emptyStateLabel, ");
        
        if (missingElements.length() > 0) {
            String missing = missingElements.substring(0, missingElements.length() - 2);
            System.err.println("⚠️ Warning: Missing FXML elements: " + missing);
        }
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        // Category buttons - allCategoryBtn is already in FXML
        allCategoryBtn.setOnAction(e -> selectCategory((Category) null));
        
        // Search button
        searchBtn.setOnAction(e -> performSearch());
        
        // Search field enter key
        searchField.setOnAction(e -> performSearch());
    }
    
    /**
     * Load categories and create dynamic category buttons
     */
    private void loadCategories() {
        try {
            System.out.println("🔄 Loading categories from database...");
            List<Category> categories = menuService.getAvailableCategories();
            System.out.println("📋 Found " + categories.size() + " categories: " + 
                categories.stream().map(Category::getCategoryName).collect(java.util.stream.Collectors.joining(", ")));
            createCategoryButtons(categories);
            updateStatus("Đã tải " + categories.size() + " danh mục");
            System.out.println("✅ Categories loaded successfully");
        } catch (Exception e) {
            System.err.println("❌ Error loading categories: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi tải danh mục: " + e.getMessage());
        }
    }
    
    /**
     * Create dynamic category buttons
     */
    private void createCategoryButtons(List<Category> categories) {
        // Clear existing buttons except allCategoryBtn
        categoryBar.getChildren().clear();
        categoryBar.getChildren().add(allCategoryBtn);
        
        // Create buttons for each category
        for (Category category : categories) {
            Button categoryBtn = new Button(category.getCategoryName());
            categoryBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;");
            categoryBtn.setOnAction(e -> selectCategory(category));
            categoryBar.getChildren().add(categoryBtn);
        }
    }
    
    /**
     * Load products
     */
    private void loadProducts() {
        showLoading(true);
        
        try {
            System.out.println("🔄 Loading products from database...");
            currentProducts = menuService.getAllAvailableProducts();
            System.out.println("📦 Found " + currentProducts.size() + " products");
            displayProducts(currentProducts);
            
            showLoading(false);
            updateStatus("Đã tải " + currentProducts.size() + " sản phẩm");
            System.out.println("✅ Products loaded successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error loading products: " + e.getMessage());
            e.printStackTrace();
            showLoading(false);
            showError("Lỗi tải sản phẩm: " + e.getMessage());
        }
    }
    
    /**
     * Load products by category
     */
    private void loadProductsByCategory(Category category) {
        showLoading(true);
        
        try {
            currentProducts = menuService.getProductsByCategory(category.getCategoryId());
            displayProducts(currentProducts);
            
            showLoading(false);
            updateStatus("Đã tải " + currentProducts.size() + " sản phẩm cho danh mục " + category.getCategoryName());
            
        } catch (Exception e) {
            showLoading(false);
            showError("Lỗi tải sản phẩm: " + e.getMessage());
        }
    }
    
    /**
     * Select category and filter products
     */
    private void selectCategory(Category category) {
        selectedCategory = category;
        
        // Update button styles
        updateCategoryButtonStyles(category);
        
        // Filter products
        if (category == null) {
            loadProducts(); // Load all products
        } else {
            loadProductsByCategory(category);
        }
    }
    
    /**
     * Select category by name (for backward compatibility)
     */
    private void selectCategoryByName(String categoryName) {
        if (categoryName == null) {
            selectCategory((Category) null);
        } else {
            // Find category by name
            List<Category> categories = menuService.getAvailableCategories();
            Category foundCategory = categories.stream()
                    .filter(cat -> cat.getCategoryName().toLowerCase().contains(categoryName.toLowerCase()))
                    .findFirst()
                    .orElse(null);
            selectCategory(foundCategory);
        }
    }
    
    /**
     * Update category button styles
     */
    private void updateCategoryButtonStyles(Category selectedCategory) {
        // Reset all buttons
        String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20; -fx-border-color: #8B4513; -fx-border-width: 1; -fx-border-radius: 20;";
        String activeStyle = "-fx-background-color: #8B4513; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 20;";
        
        // Update allCategoryBtn
        allCategoryBtn.setStyle(selectedCategory == null ? activeStyle : defaultStyle);
        
        // Update dynamic category buttons
        for (javafx.scene.Node node : categoryBar.getChildren()) {
            if (node instanceof Button && node != allCategoryBtn) {
                Button btn = (Button) node;
                if (selectedCategory != null && btn.getText().equals(selectedCategory.getCategoryName())) {
                    btn.setStyle(activeStyle);
                } else {
                    btn.setStyle(defaultStyle);
                }
            }
        }
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            loadProducts();
            return;
        }
        
        showLoading(true);
        
        try {
            List<Product> searchResults = menuService.searchProducts(keyword);
            displayProducts(searchResults);
            
            showLoading(false);
            updateStatus("Tìm thấy " + searchResults.size() + " sản phẩm");
            
        } catch (Exception e) {
            showLoading(false);
            showError("Lỗi tìm kiếm: " + e.getMessage());
        }
    }
    
    /**
     * Display products in grid
     */
    private void displayProducts(List<Product> products) {
        productGrid.getChildren().clear();
        
        if (products.isEmpty()) {
            // Show empty state
            productGrid.setVisible(false);
            emptyStateContainer.setVisible(true);
            emptyStateLabel.setText("Không tìm thấy sản phẩm nào");
            updateStatus("Không tìm thấy sản phẩm nào");
            return;
        }
        
        // Hide empty state and show products
        emptyStateContainer.setVisible(false);
        productGrid.setVisible(true);
        
        int col = 0;
        int row = 0;
        int maxCols = 4;
        
        for (Product product : products) {
            VBox productCard = createProductCard(product);
            productGrid.add(productCard, col, row);
            
            col++;
            if (col >= maxCols) {
                col = 0;
                row++;
            }
        }
    }
    
    /**
     * Create product card
     */
    private VBox createProductCard(Product product) {
        VBox card = new VBox(8);
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setPadding(new Insets(15));
        card.setPrefWidth(140);
        card.setPrefHeight(180);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8;");
        
        // Product image placeholder
        Rectangle imagePlaceholder = new Rectangle(80, 80);
        imagePlaceholder.setFill(Color.web("#f0f0f0"));
        imagePlaceholder.setArcWidth(8);
        imagePlaceholder.setArcHeight(8);
        
        // Product name
        Label nameLabel = new Label(product.getProductName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        nameLabel.setStyle("-fx-text-fill: #333;");
        nameLabel.setWrapText(true);
        nameLabel.setMaxWidth(120);
        
        // Product price
        Label priceLabel = new Label(product.getFormattedPrice());
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        priceLabel.setStyle("-fx-text-fill: #E67E22;");
        
        // Add to order button
        Button addButton = new Button("+");
        addButton.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 15;");
        addButton.setOnAction(e -> addToOrder(product));
        
        card.getChildren().addAll(imagePlaceholder, nameLabel, priceLabel, addButton);
        
        // Add hover effect
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #8B4513; -fx-border-width: 2; -fx-border-radius: 8; -fx-cursor: hand;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-color: #ddd; -fx-border-width: 1; -fx-border-radius: 8;");
        });
        
        return card;
    }
    
    /**
     * Add product to order
     */
    private void addToOrder(Product product) {
        System.out.println("🛒 MenuController: Adding product to order: " + product.getProductName());
        
        if (dashboardController != null) {
            System.out.println("✅ MenuController: DashboardController found, calling addToOrder");
            dashboardController.addToOrder(product.getProductName(), product.getPrice(), 1);
            updateStatus("Đã thêm " + product.getProductName() + " vào đơn hàng");
        } else {
            System.err.println("❌ MenuController: DashboardController is null!");
            showError("Không thể kết nối với Order Panel");
        }
    }
    
    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        loadingIndicator.setVisible(show);
        if (show) {
            updateStatus("Đang tải...");
        }
    }
    
    /**
     * Update status label
     */
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    /**
     * Show error message
     */
    private void showError(String message) {
        CafeManagementApplication.showErrorAlert("Lỗi", message);
    }
    
    /**
     * Set reference to dashboard controller
     */
    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
}

