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
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách menu: " + e.getMessage());
            // Fallback to demo data
            menuItems = new ArrayList<>();
            menuItems.add(new Menu("Cà phê đen", "Cà phê đen truyền thống, đậm đà hương vị", new BigDecimal("25000"), "COFFEE"));
            menuItems.add(new Menu("Cà phê sữa", "Cà phê sữa đặc, ngọt ngào", new BigDecimal("30000"), "COFFEE"));
            menuItems.add(new Menu("Cappuccino", "Cappuccino Ý, bọt sữa mịn", new BigDecimal("45000"), "COFFEE"));
            menuItems.add(new Menu("Latte", "Latte mượt mà, hương vị nhẹ nhàng", new BigDecimal("50000"), "COFFEE"));
            menuItems.add(new Menu("Espresso", "Espresso đậm đà, tinh khiết", new BigDecimal("35000"), "COFFEE"));
            menuItems.add(new Menu("Trà sữa trân châu", "Trà sữa trân châu đường đen", new BigDecimal("35000"), "TEA"));
            menuItems.add(new Menu("Trà đào", "Trà đào mát lạnh, thơm ngon", new BigDecimal("30000"), "TEA"));
            menuItems.add(new Menu("Trà chanh", "Trà chanh tươi, giải nhiệt", new BigDecimal("25000"), "TEA"));
            menuItems.add(new Menu("Nước ép cam", "Nước ép cam tươi, giàu vitamin C", new BigDecimal("40000"), "JUICE"));
            menuItems.add(new Menu("Nước ép táo", "Nước ép táo tươi, ngọt tự nhiên", new BigDecimal("45000"), "JUICE"));
            menuItems.add(new Menu("Bánh tiramisu", "Bánh tiramisu Ý, hương vị đặc biệt", new BigDecimal("55000"), "DESSERT"));
            menuItems.add(new Menu("Bánh cheesecake", "Cheesecake mịn màng, béo ngậy", new BigDecimal("50000"), "DESSERT"));
            menuItems.add(new Menu("Bánh chocolate", "Bánh chocolate đậm đà", new BigDecimal("45000"), "DESSERT"));
            menuItems.add(new Menu("Bánh mì sandwich", "Bánh mì sandwich thịt nguội", new BigDecimal("35000"), "FOOD"));
            menuItems.add(new Menu("Bánh mì bơ", "Bánh mì bơ thơm béo", new BigDecimal("25000"), "FOOD"));
            menuItems.add(new Menu("Kem vanilla", "Kem vanilla mát lạnh", new BigDecimal("30000"), "DESSERT"));
            menuItems.add(new Menu("Kem chocolate", "Kem chocolate đậm đà", new BigDecimal("35000"), "DESSERT"));
            menuItems.add(new Menu("Sinh tố dâu", "Sinh tố dâu tươi, mát lạnh", new BigDecimal("40000"), "SMOOTHIE"));
            menuItems.add(new Menu("Sinh tố xoài", "Sinh tố xoài chín, ngọt tự nhiên", new BigDecimal("45000"), "SMOOTHIE"));
            menuItems.add(new Menu("Cà phê đá xay", "Cà phê đá xay mát lạnh", new BigDecimal("55000"), "COFFEE"));
            
            filteredItems = new ArrayList<>(menuItems);
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
        // TODO: Implement add menu item dialog
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng thêm món sẽ được phát triển sau");
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