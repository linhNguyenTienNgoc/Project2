package com.cafe.view.component;

import com.cafe.model.entity.Product;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Custom component cho hiển thị order item row
 * Bao gồm product name, quantity controls, price và remove button
 */
public class OrderItemRow extends HBox {
    
    private Product product;
    private int quantity;
    private Spinner<Integer> quantitySpinner;
    private Label productName;
    private Label totalPrice;
    private Button removeButton;
    
    private Consumer<Product> onRemove;
    private BiConsumer<Product, Integer> onQuantityChange;
    
    // CSS classes
    private static final String ROW_STYLE = "order-item-row";
    private static final String NAME_STYLE = "order-item-name";
    private static final String PRICE_STYLE = "order-item-price";
    private static final String QUANTITY_STYLE = "order-item-quantity";
    private static final String REMOVE_BUTTON_STYLE = "remove-button";
    
    public OrderItemRow() {
        initializeComponents();
        setupStyles();
        setupEventHandlers();
    }
    
    public OrderItemRow(Product product, int quantity) {
        this();
        setProduct(product, quantity);
    }
    
    private void initializeComponents() {
        // Product name
        productName = new Label();
        productName.setFont(Font.font("System", FontWeight.BOLD, 14));
        productName.setWrapText(true);
        productName.setMaxWidth(200);
        
        // Quantity spinner
        quantitySpinner = new Spinner<>();
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(80);
        quantitySpinner.setMaxWidth(80);
        
        // Total price
        totalPrice = new Label();
        totalPrice.setFont(Font.font("System", FontWeight.BOLD, 14));
        totalPrice.setTextFill(Color.web("#E67E22"));
        totalPrice.setAlignment(Pos.CENTER_RIGHT);
        totalPrice.setPrefWidth(100);
        totalPrice.setMaxWidth(100);
        
        // Remove button
        removeButton = new Button("×");
        removeButton.setFont(Font.font("System", FontWeight.BOLD, 16));
        removeButton.setPrefWidth(30);
        removeButton.setMaxWidth(30);
        removeButton.setPrefHeight(30);
        removeButton.setMaxHeight(30);
        
        // Layout
        setAlignment(Pos.CENTER_LEFT);
        setSpacing(10);
        setPadding(new Insets(8, 10, 8, 10));
        setMinHeight(50);
        setMaxHeight(50);
        
        // Add components
        getChildren().addAll(productName, quantitySpinner, totalPrice, removeButton);
        
        // Set HBox constraints
        HBox.setHgrow(productName, Priority.ALWAYS);
        HBox.setHgrow(totalPrice, Priority.NEVER);
    }
    
    private void setupStyles() {
        getStyleClass().add(ROW_STYLE);
        productName.getStyleClass().add(NAME_STYLE);
        totalPrice.getStyleClass().add(PRICE_STYLE);
        quantitySpinner.getStyleClass().add(QUANTITY_STYLE);
        removeButton.getStyleClass().add(REMOVE_BUTTON_STYLE);
    }
    
    private void setupEventHandlers() {
        // Remove button click
        removeButton.setOnAction(event -> {
            if (onRemove != null && product != null) {
                onRemove.accept(product);
            }
        });
        
        // Quantity change
        quantitySpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && newVal != oldVal && onQuantityChange != null && product != null) {
                onQuantityChange.accept(product, newVal);
            }
        });
        
        // Hover effects
        setOnMouseEntered(event -> {
            setStyle("-fx-background-color: #F8F9FA; -fx-border-color: #E67E22; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;");
        });
        
        setOnMouseExited(event -> {
            setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;");
        });
    }
    
    /**
     * Set product and quantity
     */
    public void setProduct(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        updateDisplay();
    }
    
    /**
     * Update display based on product and quantity
     */
    private void updateDisplay() {
        if (product == null) {
            clearDisplay();
            return;
        }
        
        // Set product name
        productName.setText(product.getProductName());
        
        // Setup quantity spinner
        setupQuantitySpinner();
        
        // Update total price
        updateTotalPrice();
    }
    
    /**
     * Setup quantity spinner
     */
    private void setupQuantitySpinner() {
        // Set spinner value factory
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 99, quantity);
        quantitySpinner.setValueFactory(valueFactory);
        
        // Set initial value
        quantitySpinner.getValueFactory().setValue(quantity);
    }
    
    /**
     * Update total price display
     */
    private void updateTotalPrice() {
        if (product.getPrice() != null) {
            double total = product.getPrice() * quantity;
            totalPrice.setText(String.format("%,.0f VNĐ", total));
        } else {
            totalPrice.setText("0 VNĐ");
        }
    }
    
    /**
     * Clear display
     */
    private void clearDisplay() {
        productName.setText("");
        totalPrice.setText("");
        quantitySpinner.getValueFactory().setValue(1);
    }
    
    /**
     * Set remove handler
     */
    public void setOnRemove(Consumer<Product> handler) {
        this.onRemove = handler;
    }
    
    /**
     * Set quantity change handler
     */
    public void setOnQuantityChange(BiConsumer<Product, Integer> handler) {
        this.onQuantityChange = handler;
    }
    
    /**
     * Get product
     */
    public Product getProduct() {
        return product;
    }
    
    /**
     * Get current quantity
     */
    public int getQuantity() {
        return quantitySpinner.getValue();
    }
    
    /**
     * Set quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        if (quantitySpinner.getValueFactory() != null) {
            quantitySpinner.getValueFactory().setValue(quantity);
        }
        updateTotalPrice();
    }
    
    /**
     * Get total price
     */
    public double getTotalPrice() {
        if (product != null && product.getPrice() != null) {
            return product.getPrice() * getQuantity();
        }
        return 0.0;
    }
    
    /**
     * Update product data (for real-time updates)
     */
    public void updateProduct(Product updatedProduct) {
        this.product = updatedProduct;
        updateDisplay();
    }
    
    /**
     * Disable quantity controls
     */
    public void disableQuantityControls() {
        quantitySpinner.setDisable(true);
        removeButton.setDisable(true);
    }
    
    /**
     * Enable quantity controls
     */
    public void enableQuantityControls() {
        quantitySpinner.setDisable(false);
        removeButton.setDisable(false);
    }
    
    /**
     * Highlight row (for selection)
     */
    public void highlight() {
        setStyle("-fx-background-color: #E8F5E8; -fx-border-color: #27AE60; -fx-border-width: 2; -fx-border-radius: 4; -fx-background-radius: 4;");
    }
    
    /**
     * Remove highlight
     */
    public void removeHighlight() {
        setStyle("-fx-background-color: white; -fx-border-color: #E0E0E0; -fx-border-width: 1; -fx-border-radius: 4; -fx-background-radius: 4;");
    }
}
