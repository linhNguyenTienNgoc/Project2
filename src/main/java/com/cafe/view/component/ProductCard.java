package com.cafe.view.component;

import com.cafe.model.entity.Product;
import com.cafe.util.ImageLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.util.function.Consumer;

/**
 * Custom component cho hiển thị product card
 * Bao gồm image, name, price và stock status
 */
public class ProductCard extends VBox {
    
    private Product product;
    private ImageView productImage;
    private Label productName;
    private Label productPrice;
    private Label stockStatus;
    private Consumer<Product> onProductClick;
    
    // CSS classes
    private static final String CARD_STYLE = "product-card";
    private static final String IMAGE_STYLE = "product-image";
    private static final String NAME_STYLE = "product-name";
    private static final String PRICE_STYLE = "product-price";
    private static final String STOCK_STYLE = "stock-status";
    
    public ProductCard() {
        initializeComponents();
        setupStyles();
        setupEventHandlers();
    }
    
    public ProductCard(Product product) {
        this();
        setProduct(product);
    }
    
    private void initializeComponents() {
        // Product image
        productImage = new ImageView();
        productImage.setFitWidth(150);
        productImage.setFitHeight(150);
        productImage.setPreserveRatio(true);
        productImage.setSmooth(true);
        productImage.setCache(true);
        
        // Product name
        productName = new Label();
        productName.setWrapText(true);
        productName.setTextAlignment(TextAlignment.CENTER);
        productName.setFont(Font.font("System", FontWeight.BOLD, 14));
        productName.setMaxWidth(150);
        
        // Product price
        productPrice = new Label();
        productPrice.setTextAlignment(TextAlignment.CENTER);
        productPrice.setFont(Font.font("System", FontWeight.BOLD, 16));
        productPrice.setTextFill(Color.web("#E67E22"));
        
        // Stock status
        stockStatus = new Label();
        stockStatus.setTextAlignment(TextAlignment.CENTER);
        stockStatus.setFont(Font.font("System", FontWeight.NORMAL, 10));
        stockStatus.setPadding(new Insets(2, 6, 2, 6));
        stockStatus.setMaxWidth(80);
        
        // Layout
        setAlignment(Pos.CENTER);
        setSpacing(8);
        setPadding(new Insets(10));
        setMinWidth(170);
        setMaxWidth(170);
        setMinHeight(220);
        setMaxHeight(220);
        
        getChildren().addAll(productImage, productName, productPrice, stockStatus);
    }
    
    private void setupStyles() {
        getStyleClass().add(CARD_STYLE);
        productImage.getStyleClass().add(IMAGE_STYLE);
        productName.getStyleClass().add(NAME_STYLE);
        productPrice.getStyleClass().add(PRICE_STYLE);
        stockStatus.getStyleClass().add(STOCK_STYLE);
    }
    
    private void setupEventHandlers() {
        // Click event
        setOnMouseClicked(this::handleClick);
        
        // Hover effects
        setOnMouseEntered(event -> {
            setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E67E22; -fx-border-width: 2;");
        });
        
        setOnMouseExited(event -> {
            setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1;");
        });
    }
    
    private void handleClick(MouseEvent event) {
        if (onProductClick != null && product != null) {
            onProductClick.accept(product);
        }
    }
    
    /**
     * Set product data
     */
    public void setProduct(Product product) {
        this.product = product;
        updateDisplay();
    }
    
    /**
     * Update display based on product data
     */
    private void updateDisplay() {
        if (product == null) {
            clearDisplay();
            return;
        }
        
        // Set product name
        productName.setText(product.getProductName());
        
        // Set product price
        productPrice.setText(product.getFormattedPrice());
        
        // Load product image
        loadProductImage();
        
        // Update stock status
        updateStockStatus();
        
        // Update card style based on availability
        updateCardStyle();
    }
    
    /**
     * Load product image
     */
    private void loadProductImage() {
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            try {
                Image image = ImageLoader.loadProductImage(product.getImageUrl());
                productImage.setImage(image);
            } catch (Exception e) {
                setDefaultImage();
            }
        } else {
            setDefaultImage();
        }
    }
    
    /**
     * Set default image
     */
    private void setDefaultImage() {
        try {
            // Load default product image from resources
            Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
            productImage.setImage(defaultImage);
        } catch (Exception e) {
            // Create a placeholder image
            productImage.setImage(createPlaceholderImage());
        }
    }
    
    /**
     * Create placeholder image
     */
    private Image createPlaceholderImage() {
        // Create a simple placeholder image
        return new Image(getClass().getResourceAsStream("/images/icons/default-product-icon.png"));
    }
    
    /**
     * Update stock status display
     */
    private void updateStockStatus() {
        if (product.isOutOfStock()) {
            stockStatus.setText("HẾT HÀNG");
            stockStatus.setStyle("-fx-background-color: #E74C3C; -fx-text-fill: white; -fx-background-radius: 10;");
        } else if (product.isLowStock()) {
            stockStatus.setText("SẮP HẾT");
            stockStatus.setStyle("-fx-background-color: #F39C12; -fx-text-fill: white; -fx-background-radius: 10;");
        } else {
            stockStatus.setText("CÒN HÀNG");
            stockStatus.setStyle("-fx-background-color: #27AE60; -fx-text-fill: white; -fx-background-radius: 10;");
        }
    }
    
    /**
     * Update card style based on availability
     */
    private void updateCardStyle() {
        if (!product.getIsAvailable() || product.isOutOfStock()) {
            setStyle("-fx-background-color: #F5F5F5; -fx-opacity: 0.6;");
            setDisable(true);
        } else {
            setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 8; -fx-background-radius: 8;");
            setDisable(false);
        }
    }
    
    /**
     * Clear display
     */
    private void clearDisplay() {
        productName.setText("");
        productPrice.setText("");
        stockStatus.setText("");
        productImage.setImage(null);
        setStyle("-fx-background-color: transparent;");
    }
    
    /**
     * Set click handler
     */
    public void setOnProductClick(Consumer<Product> handler) {
        this.onProductClick = handler;
    }
    
    /**
     * Get product
     */
    public Product getProduct() {
        return product;
    }
    
    /**
     * Check if product is available for ordering
     */
    public boolean isAvailableForOrder() {
        return product != null && product.getIsAvailable() && product.getIsActive() && !product.isOutOfStock();
    }
    
    /**
     * Update stock display (for real-time updates)
     */
    public void updateStockDisplay() {
        if (product != null) {
            updateStockStatus();
            updateCardStyle();
        }
    }
}
