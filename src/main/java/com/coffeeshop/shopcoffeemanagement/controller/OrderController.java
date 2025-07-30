package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.dao.CoffeeTableDAO;
import com.coffeeshop.shopcoffeemanagement.dao.MenuDAO;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import com.coffeeshop.shopcoffeemanagement.model.Menu;
import com.coffeeshop.shopcoffeemanagement.model.Invoice;
import com.coffeeshop.shopcoffeemanagement.model.InvoiceDetail;
import com.coffeeshop.shopcoffeemanagement.service.PaymentService;
import com.coffeeshop.shopcoffeemanagement.service.PerformanceOptimizer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;

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
    
    @FXML
    private Button editOrderButton;
    
    @FXML
    private Button saveOrderButton;
    
    private CoffeeTableDAO tableDAO;
    private MenuDAO menuDAO;
    private List<Menu> menuItems;
    private List<Menu> filteredItems;
    private List<OrderItem> orderItems;
    private boolean isEditMode = false;
    private PerformanceOptimizer.Debouncer searchDebouncer;
    private PerformanceOptimizer.Throttler filterThrottler;
    
    @FXML
    public void initialize() {
        // Initialize performance optimizers
        searchDebouncer = new PerformanceOptimizer.Debouncer(300); // 300ms delay
        filterThrottler = new PerformanceOptimizer.Throttler(100); // 100ms interval
        tableDAO = new CoffeeTableDAO();
        menuDAO = new MenuDAO();
        orderItems = new ArrayList<>();
        
        loadTables();
        loadMenuItems();
        setupFilters();
        setupKeyboardShortcuts();
        displayMenuItems();
        updateOrderSummary();
        
        // Tự động chọn bàn nếu có bàn được chọn từ màn hình bàn
        autoSelectTable();
    }
    
    private void setupKeyboardShortcuts() {
        // Keyboard shortcuts cho nhanh chóng
        searchField.setOnKeyPressed(this::handleKeyPress);
        menuGrid.setOnKeyPressed(this::handleKeyPress);
        
        // Enter để đặt hàng
        placeOrderButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                placeOrder();
            }
        });
        
        // Escape để quay lại
        backToTablesButton.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                backToTables();
            }
        });
    }
    
    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case ENTER:
                if (event.getSource() == searchField) {
                    // Tìm kiếm khi nhấn Enter
                    filterMenuItems();
                }
                break;
            case F1:
                // F1 để đặt hàng nhanh
                showQuickOrder();
                break;
            case F2:
                // F2 để đặt hàng
                if (!orderItems.isEmpty()) {
                    placeOrder();
                }
                break;
            case F3:
                // F3 để xóa đơn hàng
                clearOrder();
                break;
            case F4:
                // F4 để quay lại bàn
                backToTables();
                break;
        }
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
                        setText(item.getTableNumber() + " (" + getStatusText(item.getStatus()) + ")");
                    }
                }
            });
            
            // Auto-select bàn đầu tiên nếu có
            if (!tables.isEmpty()) {
                tableComboBox.setValue(tables.get(0));
            }
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
            CoffeeShopApplication.showError("Lỗi", "Không thể tải menu: " + e.getMessage());
        }
    }
    
    private void setupFilters() {
        // Setup category filter
        categoryFilter.getItems().addAll("Tất cả", "Cà phê", "Trà", "Nước ép", "Tráng miệng", "Đồ ăn", "Sinh tố");
        categoryFilter.setValue("Tất cả");
        
        // Setup search field
        searchField.setPromptText("Tìm kiếm món... (Enter để tìm)");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchDebouncer.debounce(this::filterMenuItems);
        });
        
        // Setup category filter
        categoryFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterThrottler.throttle(this::filterMenuItems);
        });
    }
    
    private void filterMenuItems() {
        String searchText = searchField.getText().toLowerCase();
        String selectedCategory = categoryFilter.getValue();
        
        filteredItems = menuItems.stream()
            .filter(item -> {
                // Filter by search text
                boolean matchesSearch = item.getName().toLowerCase().contains(searchText) ||
                                      item.getDescription().toLowerCase().contains(searchText);
                
                // Filter by category
                boolean matchesCategory = "Tất cả".equals(selectedCategory) ||
                                        getCategoryText(item.getCategory()).equals(selectedCategory);
                
                return matchesSearch && matchesCategory;
            })
            .toList();
        
        displayMenuItems();
    }
    
    private void displayMenuItems() {
        menuGrid.getChildren().clear();
        
        int columns = 4; // Tăng số cột để hiển thị nhiều món hơn
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
        container.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);");
        container.setMinWidth(200);
        container.setMinHeight(180);
        
        // Hover effect
        container.setOnMouseEntered(e -> {
            container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 12; -fx-border-color: #3498db; -fx-border-radius: 12; -fx-border-width: 2; -fx-effect: dropshadow(gaussian, rgba(52,152,219,0.3), 12, 0, 0, 5);");
        });
        
        container.setOnMouseExited(e -> {
            container.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #e0e0e0; -fx-border-radius: 12; -fx-border-width: 1; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3);");
        });
        
        // Click to add to order
        container.setOnMouseClicked(e -> {
            if (e.getClickCount() == 1) {
                addToOrder(item);
            }
        });
        
        // Double click for quick add
        container.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                quickAddToOrder(item);
            }
        });
        
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        
        Label descriptionLabel = new Label(item.getDescription());
        descriptionLabel.setFont(Font.font("System", 11));
        descriptionLabel.setStyle("-fx-text-fill: #7f8c8d;");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setTextAlignment(TextAlignment.CENTER);
        
        Label priceLabel = new Label(String.format("%,.0f VNĐ", item.getPrice()));
        priceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        Label categoryLabel = new Label(getCategoryText(item.getCategory()));
        categoryLabel.setFont(Font.font("System", 10));
        categoryLabel.setStyle("-fx-text-fill: #95a5a6; -fx-background-color: #ecf0f1; -fx-padding: 2 6; -fx-background-radius: 8;");
        
        // Quick add button
        Button quickAddBtn = new Button("+");
        quickAddBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 30; -fx-min-height: 30;");
        quickAddBtn.setOnAction(e -> quickAddToOrder(item));
        
        container.getChildren().addAll(nameLabel, descriptionLabel, priceLabel, categoryLabel, quickAddBtn);
        
        return container;
    }
    
    private void quickAddToOrder(Menu item) {
        // Thêm nhanh với số lượng 1
        addToOrder(item);
        
        // Hiển thị feedback
        showQuickAddFeedback(item.getName());
    }
    
    private void showQuickAddFeedback(String itemName) {
        // Hiển thị thông báo nhanh
        Label feedback = new Label("✓ " + itemName + " đã thêm!");
        feedback.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold; -fx-font-size: 12;");
        feedback.setAlignment(Pos.CENTER);
        
        // Tạo popup nhỏ
        Popup popup = new Popup();
        popup.getContent().add(feedback);
        
        // Hiển thị popup trong 1 giây
        popup.show(CoffeeShopApplication.getPrimaryStage());
        
        // Tự động ẩn sau 1 giây
        new Thread(() -> {
            try {
                Thread.sleep(1000);
                javafx.application.Platform.runLater(() -> popup.hide());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
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
        // Kiểm tra xem món đã có trong đơn hàng chưa
        Optional<OrderItem> existingItem = orderItems.stream()
            .filter(item -> item.getMenu().getId().equals(menu.getId()))
            .findFirst();
        
        if (existingItem.isPresent()) {
            existingItem.get().incrementQuantity();
        } else {
            orderItems.add(new OrderItem(menu, 1));
        }
        
        updateOrderSummary();
        updateButtonStates();
    }
    
    private void updateButtonStates() {
        boolean hasItems = !orderItems.isEmpty();
        placeOrderButton.setDisable(!hasItems);
        clearOrderButton.setDisable(!hasItems);
        editOrderButton.setDisable(!hasItems);
    }
    
    private void updateOrderSummary() {
        orderSummaryBox.getChildren().clear();
        
        if (orderItems.isEmpty()) {
            Label emptyLabel = new Label("Chưa có món nào trong đơn hàng");
            emptyLabel.setStyle("-fx-text-fill: #95a5a6; -fx-font-style: italic;");
            emptyLabel.setAlignment(Pos.CENTER);
            orderSummaryBox.getChildren().add(emptyLabel);
            totalAmountLabel.setText("0 VNĐ");
            return;
        }
        
        BigDecimal total = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            HBox itemBox = createOrderItemBox(item);
            orderSummaryBox.getChildren().add(itemBox);
            total = total.add(item.getTotalPrice());
        }
        
        totalAmountLabel.setText(String.format("%,.0f VNĐ", total));
        updateButtonStates();
    }
    
    private HBox createOrderItemBox(OrderItem item) {
        HBox container = new HBox(10);
        container.setAlignment(Pos.CENTER_LEFT);
        container.setPadding(new Insets(8));
        container.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 8; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1;");
        
        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label nameLabel = new Label(item.getMenu().getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Label priceLabel = new Label(String.format("%,.0f VNĐ", item.getMenu().getPrice()));
        priceLabel.setFont(Font.font("System", 10));
        priceLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        infoBox.getChildren().addAll(nameLabel, priceLabel);
        
        HBox quantityBox = new HBox(5);
        quantityBox.setAlignment(Pos.CENTER);
        
        Button minusBtn = new Button("-");
        minusBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 25; -fx-min-height: 25;");
        minusBtn.setOnAction(e -> {
            item.decrementQuantity();
            if (item.getQuantity() <= 0) {
                orderItems.remove(item);
            }
            updateOrderSummary();
        });
        
        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));
        quantityLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        quantityLabel.setStyle("-fx-text-fill: #2c3e50;");
        quantityLabel.setMinWidth(30);
        quantityLabel.setAlignment(Pos.CENTER);
        
        Button plusBtn = new Button("+");
        plusBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 25; -fx-min-height: 25;");
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
        
        // Hiển thị dialog thanh toán cải tiến
        showEnhancedPaymentDialog(orderDetails.toString(), total);
    }
    
    private void showEnhancedPaymentDialog(String orderDetails, BigDecimal total) {
        // Create invoice for payment
        Invoice invoice = createInvoiceFromOrder(total);
        
        // Show payment dialog
        PaymentDialog paymentDialog = new PaymentDialog(invoice, CoffeeShopApplication.getCurrentUser());
        boolean paymentSuccess = paymentDialog.showAndWait();
        
        if (paymentSuccess) {
            processPaymentSuccess(invoice);
        }
    }
    
    private Invoice createInvoiceFromOrder(BigDecimal total) {
        // Create a temporary invoice for payment processing
        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("TEMP_" + System.currentTimeMillis());
        invoice.setTable(tableComboBox.getValue());
        invoice.setEmployee(CoffeeShopApplication.getCurrentUser());
        invoice.setTotalAmount(total);
        invoice.setStatus("PENDING");
        invoice.setOrderTime(java.time.LocalDateTime.now());
        
        // Add order details
        for (OrderItem item : orderItems) {
            InvoiceDetail detail = new InvoiceDetail();
            detail.setMenu(item.getMenu());
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getMenu().getPrice());
            detail.calculateTotal();
            invoice.addDetail(detail);
        }
        
        return invoice;
    }
    
    private void processPaymentSuccess(Invoice invoice) {
        try {
            CoffeeTable selectedTable = tableComboBox.getValue();
            
            // Update table status
            if (tableDAO.updateStatus(selectedTable.getId(), "OCCUPIED")) {
                selectedTable.setStatus("OCCUPIED");
                
                // Show success message
                StringBuilder successMessage = new StringBuilder();
                successMessage.append("✅ Thanh toán thành công!\n\n");
                successMessage.append("Bàn: ").append(selectedTable.getTableNumber()).append("\n");
                successMessage.append("Phương thức: ").append(invoice.getPaymentMethod()).append("\n");
                successMessage.append("Tổng tiền: ").append(PaymentService.formatCurrency(invoice.getTotalAmount())).append("\n");
                
                if (invoice.getTransactionId() != null) {
                    successMessage.append("Mã giao dịch: ").append(invoice.getTransactionId()).append("\n");
                }
                
                CoffeeShopApplication.showInfo("Thanh toán thành công", successMessage.toString());
                
                // Clear order and return to tables
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
    
    private void processEnhancedPayment(String orderDetails, BigDecimal total, String paymentMethod, BigDecimal discountAmount) {
        try {
            CoffeeTable selectedTable = tableComboBox.getValue();
            
            // Cập nhật trạng thái bàn thành OCCUPIED
            if (tableDAO.updateStatus(selectedTable.getId(), "OCCUPIED")) {
                selectedTable.setStatus("OCCUPIED");
                
                // Hiển thị thông báo thành công với thông tin chi tiết
                StringBuilder successMessage = new StringBuilder();
                successMessage.append("✅ Đơn hàng đã được tạo thành công!\n\n");
                successMessage.append("Bàn: ").append(selectedTable.getTableNumber()).append("\n");
                successMessage.append("Phương thức thanh toán: ").append(paymentMethod).append("\n");
                
                if (discountAmount.compareTo(BigDecimal.ZERO) > 0) {
                    successMessage.append("Giảm giá: ").append(String.format("%,.0f VNĐ", discountAmount)).append("\n");
                }
                
                successMessage.append("Tổng tiền: ").append(String.format("%,.0f VNĐ", total)).append("\n\n");
                successMessage.append("Bàn đã được đánh dấu là có khách.");
                
                CoffeeShopApplication.showInfo("Thanh toán thành công", successMessage.toString());
                
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
        if (!orderItems.isEmpty()) {
            CoffeeShopApplication.showConfirmation("Xác nhận", 
                "Bạn có chắc chắn muốn quay lại? Đơn hàng hiện tại sẽ bị mất.", 
                () -> {
                    clearOrder();
                    loadTablesScene();
                });
        } else {
            loadTablesScene();
        }
    }
    
    private void loadTablesScene() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/tables.fxml"));
            javafx.scene.Parent tablesRoot = loader.load();
            
            javafx.scene.Scene tablesScene = new javafx.scene.Scene(tablesRoot);
            tablesScene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
            
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
        
        // Popular items
        List<Menu> popularItems = menuItems.stream()
            .filter(item -> item.getCategory().equals("COFFEE") || item.getCategory().equals("TEA"))
            .limit(8)
            .toList();
        
        GridPane quickGrid = new GridPane();
        quickGrid.setHgap(10);
        quickGrid.setVgap(10);
        
        int col = 0;
        int row = 0;
        for (Menu item : popularItems) {
            VBox itemBox = createQuickOrderItem(item);
            quickGrid.add(itemBox, col, row);
            
            col++;
            if (col >= 4) {
                col = 0;
                row++;
            }
        }
        
        content.getChildren().add(quickGrid);
        dialog.getDialogPane().setContent(content);
        
        ButtonType closeButton = new ButtonType("Đóng", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.showAndWait();
    }
    
    private VBox createQuickOrderItem(Menu item) {
        VBox container = new VBox(5);
        container.setAlignment(Pos.CENTER);
        container.setPadding(new Insets(10));
        container.setStyle("-fx-background-color: #e8f5e8; -fx-background-radius: 8; -fx-border-color: #27ae60; -fx-border-radius: 8; -fx-border-width: 1;");
        
        Label nameLabel = new Label(item.getName());
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        nameLabel.setStyle("-fx-text-fill: #2c3e50;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(TextAlignment.CENTER);
        
        Label priceLabel = new Label(String.format("%,.0f VNĐ", item.getPrice()));
        priceLabel.setFont(Font.font("System", 10));
        priceLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        Button addBtn = new Button("Thêm");
        addBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15;");
        addBtn.setOnAction(e -> {
            addToOrder(item);
            showQuickAddFeedback(item.getName());
        });
        
        container.getChildren().addAll(nameLabel, priceLabel, addBtn);
        
        return container;
    }
    
    @FXML
    private void editOrder() {
        // Toggle edit mode
        isEditMode = !isEditMode;
        editOrderButton.setText(isEditMode ? "Lưu chỉnh sửa" : "Chỉnh sửa đơn hàng");
        
        if (isEditMode) {
            CoffeeShopApplication.showInfo("Chế độ chỉnh sửa", "Bạn có thể chỉnh sửa số lượng món trong đơn hàng");
        } else {
            CoffeeShopApplication.showInfo("Thành công", "Đã lưu chỉnh sửa đơn hàng");
        }
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "AVAILABLE":
                return "Trống";
            case "OCCUPIED":
                return "Có khách";
            case "RESERVED":
                return "Đã đặt";
            case "CLEANING":
                return "Đang dọn";
            default:
                return status;
        }
    }
    
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