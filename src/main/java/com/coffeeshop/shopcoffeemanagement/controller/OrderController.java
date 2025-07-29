package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.dao.CoffeeTableDAO;
import com.coffeeshop.shopcoffeemanagement.dao.MenuDAO;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderController {
    
    @FXML
    private ComboBox<CoffeeTable> tableComboBox;
    
    @FXML
    private ComboBox<String> categoryFilter;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private GridPane menuGrid;
    
    @FXML
    private VBox orderSummaryBox;
    
    @FXML
    private Label totalAmountLabel;
    
    @FXML
    private Button placeOrderButton;
    
    @FXML
    private Button clearOrderButton;
    
    @FXML
    private Button backToTablesButton;
    
    @FXML
    private Button quickOrderButton;
    
    private CoffeeTableDAO tableDAO;
    private MenuDAO menuDAO;
    private List<Menu> menuItems;
    private List<Menu> filteredItems;
    private List<OrderItem> orderItems;
    
    @FXML
    public void initialize() {
        tableDAO = new CoffeeTableDAO();
        menuDAO = new MenuDAO();
        orderItems = new ArrayList<>();
        
        loadTables();
        loadMenuItems();
        setupFilters();
        displayMenuItems();
        updateOrderSummary();
        
        // Tự động chọn bàn nếu có bàn được chọn từ màn hình bàn
        autoSelectTable();
    }
    
    private void autoSelectTable() {
        CoffeeTable selectedTable = CoffeeShopApplication.getSelectedTable();
        if (selectedTable != null) {
            // Tìm bàn trong ComboBox và chọn
            for (CoffeeTable table : tableComboBox.getItems()) {
                if (table.getId().equals(selectedTable.getId())) {
                    tableComboBox.setValue(table);
                    break;
                }
            }
            // Clear selected table sau khi đã chọn
            CoffeeShopApplication.setSelectedTable(null);
        }
    }
    
    private void loadTables() {
        try {
            List<CoffeeTable> tables = tableDAO.findAll();
            tableComboBox.getItems().addAll(tables);
            tableComboBox.setCellFactory(param -> new ListCell<CoffeeTable>() {
                @Override
                protected void updateItem(CoffeeTable item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getTableNumber() + " - " + item.getStatus() + " (" + item.getCapacity() + " người)");
                    }
                }
            });
            tableComboBox.setButtonCell(tableComboBox.getCellFactory().call(null));
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách bàn: " + e.getMessage());
        }
    }
    
    private void loadMenuItems() {
        try {
            menuItems = menuDAO.findAll();
            filteredItems = new ArrayList<>(menuItems);
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách menu: " + e.getMessage());
        }
    }
    
    private void setupFilters() {
        categoryFilter.getItems().addAll("Tất cả danh mục", "COFFEE", "TEA", "JUICE", "DESSERT", "FOOD", "SMOOTHIE");
        categoryFilter.setValue("Tất cả danh mục");
        
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
        
        int columns = 4;
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
        
        Text nameText = new Text(item.getName());
        nameText.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameText.setTextAlignment(TextAlignment.CENTER);
        nameText.setWrappingWidth(170);
        
        Text descriptionText = new Text(item.getDescription());
        descriptionText.setFont(Font.font("System", 11));
        descriptionText.setTextAlignment(TextAlignment.CENTER);
        descriptionText.setWrappingWidth(170);
        
        Text priceText = new Text(item.getFormattedPrice());
        priceText.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceText.setTextAlignment(TextAlignment.CENTER);
        
        Text categoryText = new Text(getCategoryText(item.getCategory()));
        categoryText.setFont(Font.font("System", 10));
        categoryText.setTextAlignment(TextAlignment.CENTER);
        
        Button addButton = new Button("➕ Thêm vào đơn");
        addButton.getStyleClass().add("primary-button");
        addButton.setPrefWidth(150);
        addButton.setOnAction(e -> addToOrder(item));
        
        container.getChildren().addAll(nameText, descriptionText, priceText, categoryText, addButton);
        
        return container;
    }
    
    private String getCategoryText(String category) {
        switch (category) {
            case "COFFEE": return "Cà phê";
            case "TEA": return "Trà";
            case "JUICE": return "Nước ép";
            case "DESSERT": return "Tráng miệng";
            case "FOOD": return "Đồ ăn";
            case "SMOOTHIE": return "Sinh tố";
            default: return category;
        }
    }
    
    private void addToOrder(Menu menu) {
        // Check if item already exists in order
        Optional<OrderItem> existingItem = orderItems.stream()
            .filter(item -> item.getMenu().getId().equals(menu.getId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            existingItem.get().incrementQuantity();
        } else {
            orderItems.add(new OrderItem(menu, 1));
        }
        
        updateOrderSummary();
        CoffeeShopApplication.showInfo("Thành công", "Đã thêm " + menu.getName() + " vào đơn hàng");
    }
    
    private void updateOrderSummary() {
        orderSummaryBox.getChildren().clear();
        
        if (orderItems.isEmpty()) {
            Label emptyLabel = new Label("Chưa có món nào trong đơn hàng");
            emptyLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-style: italic;");
            orderSummaryBox.getChildren().add(emptyLabel);
            totalAmountLabel.setText("0 VNĐ");
            placeOrderButton.setDisable(true);
            clearOrderButton.setDisable(true);
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        
        for (OrderItem item : orderItems) {
            HBox itemBox = createOrderItemBox(item);
            orderSummaryBox.getChildren().add(itemBox);
            total = total.add(item.getTotalPrice());
        }
        
        totalAmountLabel.setText(String.format("%,.0f VNĐ", total));
        placeOrderButton.setDisable(false);
        clearOrderButton.setDisable(false);
    }
    
    private HBox createOrderItemBox(OrderItem item) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(8));
        container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5;");
        
        VBox infoBox = new VBox(2);
        Label nameLabel = new Label(item.getMenu().getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        Label priceLabel = new Label(item.getMenu().getFormattedPrice());
        priceLabel.setFont(Font.font("System", 10));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        
        HBox quantityBox = new HBox(5);
        quantityBox.setAlignment(Pos.CENTER);
        
        Button minusBtn = new Button("-");
        minusBtn.setPrefWidth(25);
        minusBtn.setPrefHeight(25);
        minusBtn.setOnAction(e -> {
            if (item.getQuantity() > 1) {
                item.decrementQuantity();
            } else {
                orderItems.remove(item);
            }
            updateOrderSummary();
        });
        
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setPrefWidth(30);
        quantityLabel.setAlignment(Pos.CENTER);
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        Button plusBtn = new Button("+");
        plusBtn.setPrefWidth(25);
        plusBtn.setPrefHeight(25);
        plusBtn.setOnAction(e -> {
            item.incrementQuantity();
            updateOrderSummary();
        });
        
        quantityBox.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
        
        Label totalLabel = new Label(String.format("%,.0f VNĐ", item.getTotalPrice()));
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        totalLabel.setStyle("-fx-text-fill: #27ae60;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        container.getChildren().addAll(infoBox, spacer, quantityBox, totalLabel);
        
        return container;
    }
    
    @FXML
    private void placeOrder() {
        if (tableComboBox.getValue() == null) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng chọn bàn");
            return;
        }
        
        if (orderItems.isEmpty()) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng thêm món vào đơn hàng");
            return;
        }
        
        // Create order confirmation dialog
        StringBuilder orderDetails = new StringBuilder();
        orderDetails.append("Bàn: ").append(tableComboBox.getValue().getTableNumber()).append("\n\n");
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            orderDetails.append(item.getMenu().getName())
                       .append(" x").append(item.getQuantity())
                       .append(" = ").append(String.format("%,.0f VNĐ", item.getTotalPrice()))
                       .append("\n");
            total = total.add(item.getTotalPrice());
        }
        
        orderDetails.append("\nTổng cộng: ").append(String.format("%,.0f VNĐ", total));
        
        // Hiển thị dialog thanh toán
        showPaymentDialog(orderDetails.toString(), total);
    }
    
    private void showPaymentDialog(String orderDetails, BigDecimal total) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Thanh toán đơn hàng");
        alert.setHeaderText("Xác nhận đặt hàng");
        alert.setContentText(orderDetails + "\n\nChọn phương thức thanh toán:");
        
        ButtonType cashButton = new ButtonType("💵 Tiền mặt");
        ButtonType cardButton = new ButtonType("💳 Thẻ");
        ButtonType cancelButton = new ButtonType("❌ Hủy");
        
        alert.getButtonTypes().setAll(cashButton, cardButton, cancelButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == cashButton || result.get() == cardButton) {
                String paymentMethod = result.get() == cashButton ? "Tiền mặt" : "Thẻ";
                processPayment(orderDetails, total, paymentMethod);
            }
        }
    }
    
    private void processPayment(String orderDetails, BigDecimal total, String paymentMethod) {
        // TODO: Lưu đơn hàng vào database
        try {
            CoffeeTable selectedTable = tableComboBox.getValue();
            
            // Cập nhật trạng thái bàn thành OCCUPIED
            if (tableDAO.updateStatus(selectedTable.getId(), "OCCUPIED")) {
                selectedTable.setStatus("OCCUPIED");
                
                // Hiển thị thông báo thành công
                String successMessage = "✅ Đơn hàng đã được tạo thành công!\n\n" +
                                      "Bàn: " + selectedTable.getTableNumber() + "\n" +
                                      "Phương thức thanh toán: " + paymentMethod + "\n" +
                                      "Tổng tiền: " + String.format("%,.0f VNĐ", total) + "\n\n" +
                                      "Bàn đã được đánh dấu là có khách.";
                
                CoffeeShopApplication.showInfo("Thanh toán thành công", successMessage);
                
                // Xóa đơn hàng và quay về màn hình bàn
                clearOrder();
                backToTables();
            } else {
                CoffeeShopApplication.showError("Lỗi", "Không thể cập nhật trạng thái bàn");
            }
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể xử lý thanh toán: " + e.getMessage());
        }
    }
    
    @FXML
    private void clearOrder() {
        orderItems.clear();
        updateOrderSummary();
        CoffeeShopApplication.showInfo("Thành công", "Đã xóa đơn hàng");
    }
    
    @FXML
    private void backToTables() {
        try {
            // Chuyển về màn hình quản lý bàn
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/tables.fxml"));
            javafx.scene.Parent tablesRoot = loader.load();
            
            // Tạo scene mới
            javafx.scene.Scene tablesScene = new javafx.scene.Scene(tablesRoot);
            tablesScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            
            // Lấy stage hiện tại và thay đổi scene
            javafx.stage.Stage currentStage = (javafx.stage.Stage) menuGrid.getScene().getWindow();
            currentStage.setScene(tablesScene);
            currentStage.setTitle("Quản lý bàn");
            
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể quay về màn hình bàn: " + e.getMessage());
        }
    }
    
    @FXML
    private void showQuickOrder() {
        showQuickOrderDialog();
    }
    
    private void showQuickOrderDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Đặt hàng nhanh");
        dialog.setHeaderText("Chọn món phổ biến");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Text titleText = new Text("⚡ ĐẶT HÀNG NHANH");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        // Popular items grid
        GridPane popularGrid = new GridPane();
        popularGrid.setHgap(10);
        popularGrid.setVgap(10);
        popularGrid.setAlignment(Pos.CENTER);
        
        // Popular items
        String[] popularItems = {
            "Cà phê đen", "Cà phê sữa", "Cappuccino", "Latte",
            "Trà sữa trân châu", "Nước ép cam", "Bánh tiramisu", "Bánh cheesecake"
        };
        
        int col = 0;
        int row = 0;
        for (String itemName : popularItems) {
            Button itemButton = new Button(itemName);
            itemButton.setPrefWidth(120);
            itemButton.setPrefHeight(40);
            itemButton.getStyleClass().add("quick-order-button");
            
            itemButton.setOnAction(e -> {
                // Find the menu item and add to order
                Menu foundItem = menuItems.stream()
                    .filter(item -> item.getName().equals(itemName))
                    .findFirst()
                    .orElse(null);
                
                if (foundItem != null) {
                    addToOrder(foundItem);
                    CoffeeShopApplication.showInfo("Thành công", "Đã thêm " + itemName + " vào đơn hàng");
                } else {
                    CoffeeShopApplication.showError("Lỗi", "Không tìm thấy món " + itemName);
                }
            });
            
            popularGrid.add(itemButton, col, row);
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
        
        content.getChildren().addAll(titleText, popularGrid);
        dialog.getDialogPane().setContent(content);
        
        ButtonType closeButton = new ButtonType("Đóng", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.showAndWait();
    }
    
    // Inner class to represent order items
    private static class OrderItem {
        private Menu menu;
        private int quantity;
        
        public OrderItem(Menu menu, int quantity) {
            this.menu = menu;
            this.quantity = quantity;
        }
        
        public Menu getMenu() { return menu; }
        public int getQuantity() { return quantity; }
        
        public void incrementQuantity() { quantity++; }
        public void decrementQuantity() { if (quantity > 1) quantity--; }
        
        public BigDecimal getTotalPrice() {
            return menu.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
    }
} 