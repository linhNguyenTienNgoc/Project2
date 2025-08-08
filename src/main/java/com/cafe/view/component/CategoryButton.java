package com.cafe.view.component;

import com.cafe.model.entity.Category;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.function.Consumer;

/**
 * Custom component cho category button
 * Sử dụng cho category tabs trong menu
 */
public class CategoryButton extends Button {
    
    private Category category;
    private boolean isSelected;
    private Consumer<Category> onCategoryClick;
    
    // CSS classes
    private static final String BUTTON_STYLE = "category-button";
    private static final String SELECTED_STYLE = "category-button-selected";
    
    public CategoryButton() {
        initializeComponent();
        setupStyles();
        setupEventHandlers();
    }
    
    public CategoryButton(Category category) {
        this();
        setCategory(category);
    }
    
    private void initializeComponent() {
        setAlignment(Pos.CENTER);
        setPadding(new Insets(8, 16, 8, 16));
        setMinHeight(40);
        setMaxHeight(40);
        setFont(Font.font("System", FontWeight.BOLD, 14));
        setTextFill(Color.web("#8B4513"));
    }
    
    private void setupStyles() {
        getStyleClass().add(BUTTON_STYLE);
    }
    
    private void setupEventHandlers() {
        setOnAction(event -> {
            if (onCategoryClick != null && category != null) {
                onCategoryClick.accept(category);
            }
        });
        
        // Hover effects
        setOnMouseEntered(event -> {
            if (!isSelected) {
                setStyle("-fx-background-color: #F5DEB3; -fx-border-color: #D2691E; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20;");
            }
        });
        
        setOnMouseExited(event -> {
            if (!isSelected) {
                setStyle("-fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 0;");
            }
        });
    }
    
    /**
     * Set category data
     */
    public void setCategory(Category category) {
        this.category = category;
        updateDisplay();
    }
    
    /**
     * Update display based on category data
     */
    private void updateDisplay() {
        if (category == null) {
            setText("");
            return;
        }
        
        setText(category.getCategoryName());
    }
    
    /**
     * Set selected state
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
        updateSelectionStyle();
    }
    
    /**
     * Update selection style
     */
    private void updateSelectionStyle() {
        if (isSelected) {
            setStyle("-fx-background-color: #8B4513; -fx-text-fill: white; -fx-border-color: #D2691E; -fx-border-width: 2; -fx-border-radius: 20; -fx-background-radius: 20;");
            getStyleClass().add(SELECTED_STYLE);
        } else {
            setStyle("-fx-background-color: transparent; -fx-text-fill: #8B4513; -fx-border-color: transparent; -fx-border-width: 0;");
            getStyleClass().remove(SELECTED_STYLE);
        }
    }
    
    /**
     * Set click handler
     */
    public void setOnCategoryClick(Consumer<Category> handler) {
        this.onCategoryClick = handler;
    }
    
    /**
     * Get category
     */
    public Category getCategory() {
        return category;
    }
    
    /**
     * Check if button is selected
     */
    public boolean isSelected() {
        return isSelected;
    }
    
    /**
     * Get category ID
     */
    public Integer getCategoryId() {
        return category != null ? category.getCategoryId() : null;
    }
    
    /**
     * Get category name
     */
    public String getCategoryName() {
        return category != null ? category.getCategoryName() : "";
    }
    
    /**
     * Update category data (for real-time updates)
     */
    public void updateCategory(Category updatedCategory) {
        this.category = updatedCategory;
        updateDisplay();
    }
    
    /**
     * Disable button
     */
    public void disableButton() {
        setDisable(true);
        setStyle("-fx-background-color: #F5F5F5; -fx-text-fill: #999999; -fx-opacity: 0.6;");
    }
    
    /**
     * Enable button
     */
    public void enableButton() {
        setDisable(false);
        updateSelectionStyle();
    }
}
