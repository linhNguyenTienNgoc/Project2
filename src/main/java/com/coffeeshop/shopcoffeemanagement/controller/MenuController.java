package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.Menu;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import com.coffeeshop.shopcoffeemanagement.dao.MenuDAO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MenuController {
    
    @FXML
    private GridPane menuGrid;
    
    @FXML
    private ComboBox<String> categoryFilter;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private Button addMenuItemButton;
    
    private List<Menu> menuItems;
    private List<Menu> filteredItems;
    private MenuDAO menuDAO;
    
    @FXML
    public void initialize() {
        menuDAO = new MenuDAO();
        loadMenuItems();
        setupFilters();
        displayMenuItems();
    }
    
    private void loadMenuItems() {
        try {
            menuItems = menuDAO.findAll();
            filteredItems = new ArrayList<>(menuItems);
            if (menuItems.isEmpty()) {
                CoffeeShopApplication.showInfo("Thông báo", "Chưa có món nào trong menu. Vui lòng thêm món mới.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách menu: " + e.getMessage());
            menuItems = new ArrayList<>();
            filteredItems = new ArrayList<>();
        }
    }
    
    private void setupFilters() {
        // Thêm các danh mục vào ComboBox
        categoryFilter.getItems().addAll("Tất cả danh mục", "COFFEE", "TEA", "JUICE", "DESSERT", "FOOD", "SMOOTHIE");
        categoryFilter.setValue("Tất cả danh mục");
        
        // Thêm event handlers
        categoryFilter.setOnAction(e -> filterMenuItems());
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMenuItems());
    }
    
    private void filterMenuItems() {
        String selectedCategory = categoryFilter.getValue();
        String searchText = searchField.getText().toLowerCase();
        
        filteredItems.clear();
        
        for (Menu item : menuItems) {
            boolean categoryMatch = "Tất cả danh mục".equals(selectedCategory) || 
                                  selectedCategory.equals(item.getCategory());
            boolean searchMatch = item.getName().toLowerCase().contains(searchText) ||
                                item.getDescription().toLowerCase().contains(searchText);
            
            if (categoryMatch && searchMatch) {
                filteredItems.add(item);
            }
        }
        
        displayMenuItems();
    }
    
    private void displayMenuItems() {
        menuGrid.getChildren().clear();
        
        int columns = 4; // Số cột hiển thị
        int row = 0;
        int col = 0;
        
        for (Menu item : filteredItems) {
            VBox menuNode = createMenuItemNode(item);
            menuGrid.add(menuNode, col, row);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    private VBox createMenuItemNode(Menu item) {
        VBox container = new VBox(8);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(15));
        container.getStyleClass().add("menu-item");
        container.setMinWidth(200);
        container.setMaxWidth(200);
        
        // Tạo text hiển thị tên món
        Text nameText = new Text(item.getName());
        nameText.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameText.setTextAlignment(TextAlignment.CENTER);
        nameText.setWrappingWidth(170);
        
        // Tạo text hiển thị mô tả
        Text descriptionText = new Text(item.getDescription());
        descriptionText.setFont(Font.font("System", 11));
        descriptionText.setTextAlignment(TextAlignment.CENTER);
        descriptionText.setWrappingWidth(170);
        
        // Tạo text hiển thị giá
        Text priceText = new Text(item.getFormattedPrice());
        priceText.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceText.setTextAlignment(TextAlignment.CENTER);
        
        // Tạo text hiển thị danh mục
        Text categoryText = new Text(getCategoryText(item.getCategory()));
        categoryText.setFont(Font.font("System", 10));
        categoryText.setTextAlignment(TextAlignment.CENTER);
        
        // Tạo buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        Button editButton = new Button("Sửa");
        editButton.getStyleClass().add("primary-button");
        editButton.setPrefWidth(60);
        editButton.setOnAction(e -> editMenuItem(item));
        
        Button deleteButton = new Button("Xóa");
        deleteButton.getStyleClass().add("danger-button");
        deleteButton.setPrefWidth(60);
        deleteButton.setOnAction(e -> deleteMenuItem(item));
        
        buttonBox.getChildren().addAll(editButton, deleteButton);
        
        // Thêm các thành phần vào container
        container.getChildren().addAll(nameText, descriptionText, priceText, categoryText, buttonBox);
        
        return container;
    }
    
    private String getCategoryText(String category) {
        switch (category) {
            case "COFFEE":
                return "Cà phê";
            case "TEA":
                return "Trà";
            case "JUICE":
                return "Nước ép";
            case "DESSERT":
                return "Tráng miệng";
            case "FOOD":
                return "Đồ ăn";
            case "SMOOTHIE":
                return "Sinh tố";
            default:
                return category;
        }
    }
    
    @FXML
    private void showAddMenuItemDialog() {
        showMenuItemDialog(null);
    }
    
    private void showMenuItemDialog(Menu menu) {
        Dialog<Menu> dialog = new Dialog<>();
        dialog.setTitle(menu == null ? "Thêm món mới" : "Chỉnh sửa món");
        dialog.setHeaderText(menu == null ? "Nhập thông tin món mới" : "Chỉnh sửa thông tin món");
        
        // Setup dialog content
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Text titleText = new Text(menu == null ? "➕ THÊM MÓN MỚI" : "✏️ CHỈNH SỬA MÓN");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        TextField nameField = new TextField();
        nameField.setPromptText("Tên món");
        nameField.setPrefWidth(300);
        
        TextArea descriptionField = new TextArea();
        descriptionField.setPromptText("Mô tả món");
        descriptionField.setPrefWidth(300);
        descriptionField.setPrefRowCount(3);
        descriptionField.setWrapText(true);
        
        TextField priceField = new TextField();
        priceField.setPromptText("Giá (VNĐ)");
        priceField.setPrefWidth(300);
        
        ComboBox<String> categoryComboBox = new ComboBox<>();
        categoryComboBox.getItems().addAll("Cà phê", "Trà", "Nước ép", "Bánh", "Đồ ăn nhẹ", "Khác");
        categoryComboBox.setPromptText("Chọn danh mục");
        categoryComboBox.setPrefWidth(300);
        
        CheckBox activeCheckBox = new CheckBox("Món đang bán");
        activeCheckBox.setSelected(true);
        
        // Set current values if editing
        if (menu != null) {
            nameField.setText(menu.getName());
            descriptionField.setText(menu.getDescription());
            priceField.setText(menu.getPrice().toString());
            categoryComboBox.setValue(menu.getCategory());
            activeCheckBox.setSelected(menu.isAvailable());
        }
        
        content.getChildren().addAll(titleText, nameField, descriptionField, priceField, categoryComboBox, activeCheckBox);
        dialog.getDialogPane().setContent(content);
        
        // Setup buttons
        ButtonType saveButtonType = new ButtonType("💾 Lưu", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("❌ Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
        
        // Validation
        Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);
        
        // Enable save button only when required fields are filled
        javafx.beans.value.ChangeListener<String> validationListener = (observable, oldValue, newValue) -> {
            boolean isValid = !nameField.getText().trim().isEmpty() &&
                            !priceField.getText().trim().isEmpty() &&
                            categoryComboBox.getValue() != null;
            
            // Validate price is numeric
            try {
                if (!priceField.getText().trim().isEmpty()) {
                    new BigDecimal(priceField.getText().trim());
                }
            } catch (NumberFormatException e) {
                isValid = false;
            }
            
            saveButton.setDisable(!isValid);
        };
        
        nameField.textProperty().addListener(validationListener);
        priceField.textProperty().addListener(validationListener);
        categoryComboBox.valueProperty().addListener((observable, oldValue, newValue) -> validationListener.changed(null, null, null));
        
        // Handle result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    Menu newMenu = menu != null ? menu : new Menu();
                    newMenu.setName(nameField.getText().trim());
                    newMenu.setDescription(descriptionField.getText().trim());
                    newMenu.setPrice(new BigDecimal(priceField.getText().trim()));
                    newMenu.setCategory(categoryComboBox.getValue());
                    newMenu.setAvailable(activeCheckBox.isSelected());
                    
                    boolean success;
                    if (menu == null) {
                        // Create new menu item
                        success = menuDAO.save(newMenu);
                    } else {
                        // Update existing menu item
                        success = menuDAO.update(newMenu);
                    }
                    
                    if (success) {
                        loadMenuItems();
                        CoffeeShopApplication.showInfo("Thành công", 
                            menu == null ? "Đã thêm món mới" : "Đã cập nhật thông tin món");
                        return newMenu;
                    } else {
                        CoffeeShopApplication.showError("Lỗi", "Không thể lưu thông tin món");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoffeeShopApplication.showError("Lỗi", "Không thể lưu thông tin món: " + e.getMessage());
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    private void editMenuItem(Menu item) {
        // TODO: Implement edit menu item dialog
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng sửa món sẽ được phát triển sau");
    }
    
    private void deleteMenuItem(Menu item) {
        CoffeeShopApplication.showConfirmation("Xóa món", 
            "Bạn có chắc chắn muốn xóa món '" + item.getName() + "' không?", 
            () -> {
                try {
                    if (menuDAO.delete(item.getId())) {
                        menuItems.remove(item);
                        filterMenuItems();
                        CoffeeShopApplication.showInfo("Thành công", "Đã xóa món " + item.getName());
                    } else {
                        CoffeeShopApplication.showError("Lỗi", "Không thể xóa món " + item.getName());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    CoffeeShopApplication.showError("Lỗi", "Không thể xóa món: " + e.getMessage());
                }
            });
    }
} 