package com.cafe.controller.order;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;
import com.cafe.model.enums.TableStatus;
import com.cafe.service.OrderService;
import com.cafe.service.MenuService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class OrderPanelController implements Initializable {

    @FXML private VBox orderPanel;
    @FXML private Label tableInfoLabel;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalAmountLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button paymentButton;
    @FXML private Button clearOrderButton;

    // Database services
    private OrderService orderService;
    private MenuService menuService;
    
    // Current state
    private double totalAmount = 0.0;
    private String currentTableName = "--";
    private TableStatus currentTableStatus = TableStatus.AVAILABLE;
    private Integer currentTableId = null;
    private Integer currentUserId = 1; // TODO: Get from session
    
    // Order management
    private Order currentOrder = null;
    private Map<String, OrderItem> orderItems = new HashMap<>();
    
    private DashboardController dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Debug FXML injection
            System.out.println("🔍 OrderPanelController.initialize() called");
            System.out.println("🔍 FXML elements check:");
            System.out.println("   - orderItemsContainer: " + (orderItemsContainer != null ? "✅ Available" : "❌ NULL"));
            System.out.println("   - totalAmountLabel: " + (totalAmountLabel != null ? "✅ Available" : "❌ NULL"));
            System.out.println("   - tableInfoLabel: " + (tableInfoLabel != null ? "✅ Available" : "❌ NULL"));
            
            // Initialize database services
            initializeServices();
            
            // Test database connection and menu data
            testDatabaseConnection();
            
            setupEventHandlers();
            clearOrder();
            
            // Verify FXML injection
            verifyFXMLInjection();
            
            System.out.println("✅ OrderPanelController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing OrderPanelController: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi khởi tạo Order Panel: " + e.getMessage());
        }
    }
    
    /**
     * Initialize database services
     */
    public void initializeServices() {
        try {
            this.orderService = new OrderService();
            this.menuService = new MenuService();
            System.out.println("✅ Database services initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing database services: " + e.getMessage());
            throw new RuntimeException("Failed to initialize database services", e);
        }
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }
    
    /**
     * Kiểm tra và sửa vấn đề FXML injection
     */
    public void verifyFXMLInjection() {
        System.out.println("🔍 Verifying FXML injection...");
        System.out.println("   - orderItemsContainer: " + (orderItemsContainer != null ? "✅" : "❌"));
        System.out.println("   - totalAmountLabel: " + (totalAmountLabel != null ? "✅" : "❌"));
        System.out.println("   - tableInfoLabel: " + (tableInfoLabel != null ? "✅" : "❌"));
        System.out.println("   - placeOrderButton: " + (placeOrderButton != null ? "✅" : "❌"));
        System.out.println("   - paymentButton: " + (paymentButton != null ? "✅" : "❌"));
        System.out.println("   - clearOrderButton: " + (clearOrderButton != null ? "✅" : "❌"));
        
        if (orderItemsContainer == null) {
            System.err.println("⚠️ orderItemsContainer is null - FXML injection may have failed");
        }
    }
    
    /**
     * Force refresh UI display
     */
    public void refreshUIDisplay() {
        if (orderItemsContainer == null) {
            System.err.println("❌ Cannot refresh UI - orderItemsContainer is null");
            return;
        }
        
        System.out.println("🔄 Refreshing UI display...");
        
        // Clear current display
        orderItemsContainer.getChildren().clear();
        
        // Re-add all items
        for (OrderItem item : orderItems.values()) {
            HBox orderItem = createOrderItemRow(item.productName, item.price, item.quantity);
            orderItemsContainer.getChildren().add(orderItem);
        }
        
        // Update total amount
        updateTotalAmount();
        
        System.out.println("✅ UI display refreshed");
    }
    
    /**
     * Get the number of items in the order
     */
    public int getOrderItemsCount() {
        return orderItems.size();
    }
    
    /**
     * Get the total amount
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    private void setupEventHandlers() {
        // Only setup event handlers if FXML elements are available
        if (placeOrderButton != null) {
            placeOrderButton.setOnAction(e -> placeOrder());
        }
        if (paymentButton != null) {
            paymentButton.setOnAction(e -> processPayment());
        }
        if (clearOrderButton != null) {
            clearOrderButton.setOnAction(e -> clearOrder());
        }
    }

    public void updateTableInfo(String tableName, TableStatus status) {
        currentTableName = tableName;
        currentTableStatus = status;
        
        // Extract table ID from table name (e.g., "Bàn 1" -> 1)
        try {
            if (tableName != null && tableName.startsWith("Bàn ")) {
                currentTableId = Integer.parseInt(tableName.substring(4));
            } else {
                currentTableId = null;
            }
        } catch (NumberFormatException e) {
            currentTableId = null;
        }
        
        // Update UI only if FXML elements are available
        if (tableInfoLabel != null) {
            tableInfoLabel.setText("Bàn: " + tableName);
        }
        
        // Clear current order when switching tables
        if (currentOrder != null) {
            clearOrder();
        }
        
        System.out.println("✅ Updated table info: " + tableName + " (ID: " + currentTableId + ")");
    }

    public void addToOrder(String productName, double price, int quantity) {
        try {
            // Update UI and orderItems to save items temporarily in memory
            updateOrderItemUI(productName, price, quantity);
            
            // Force refresh UI if needed
            if (orderItemsContainer != null && orderItemsContainer.getChildren().isEmpty() && !orderItems.isEmpty()) {
                System.out.println("🔄 Force refreshing UI display...");
                refreshUIDisplay();
            }
            
            System.out.println("✅ Added product to order: " + productName + " x" + quantity);
            
        } catch (Exception e) {
            System.err.println("Error adding to order: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi thêm món: " + e.getMessage());
        }
    }
    
    /**
     * Find product by name
     */
    private Product findProductByName(String productName) {
        try {
            List<Product> products = menuService.getAllAvailableProducts();
            return products.stream()
                    .filter(p -> p.getProductName().equals(productName))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            System.err.println("Error finding product: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Update order item in UI
     */
    private void updateOrderItemUI(String productName, double price, int quantity) {
        System.out.println("🔍 updateOrderItemUI called for: " + productName);
        System.out.println("🔍 orderItemsContainer: " + (orderItemsContainer != null ? "✅ Available" : "❌ NULL"));
        
        // Update UI only if FXML elements are available
        if (orderItemsContainer == null) {
            System.out.println("⚠️ orderItemsContainer is null, updating internal state only");
            // If no UI, just update internal state
            updateOrderItemInternal(productName, price, quantity);
            return;
        }

        System.out.println("🔍 orderItemsContainer children count: " + orderItemsContainer.getChildren().size());
        
        // Check if we need to clear placeholder
        if (orderItemsContainer.getChildren().size() == 1) {
            Node firstChild = orderItemsContainer.getChildren().get(0);
            if (firstChild instanceof Label) {
                Label label = (Label) firstChild;
                if ("Chưa có món nào được chọn".equals(label.getText())) {
                    System.out.println("🧹 Clearing placeholder label");
                    orderItemsContainer.getChildren().clear();
                }
            }
        }

        // Check if product already exists in order
        OrderItem existingItem = orderItems.get(productName);
        if (existingItem != null) {
            System.out.println("📝 Updating existing item: " + productName);
            // Update existing item
            existingItem.quantity += quantity;
            existingItem.totalPrice = existingItem.price * existingItem.quantity;
            
            // Update UI row
            updateOrderItemRow(existingItem);
        } else {
            System.out.println("🆕 Creating new item: " + productName);
            // Create new item
            OrderItem newItem = new OrderItem(productName, price, quantity);
            orderItems.put(productName, newItem);
            
            // Create UI row
            HBox orderItem = createOrderItemRow(productName, price, quantity);
            orderItemsContainer.getChildren().add(orderItem);
            System.out.println("✅ Added new UI row for: " + productName);
        }

        totalAmount += price * quantity;
        updateTotalAmount();
    }
    
    /**
     * Update order item internal state (for testing without UI)
     */
    private void updateOrderItemInternal(String productName, double price, int quantity) {
        OrderItem existingItem = orderItems.get(productName);
        if (existingItem != null) {
            existingItem.quantity += quantity;
            existingItem.totalPrice = existingItem.price * existingItem.quantity;
        } else {
            OrderItem newItem = new OrderItem(productName, price, quantity);
            orderItems.put(productName, newItem);
        }
        totalAmount += price * quantity;
        System.out.println("📦 Added to order: " + productName + " x" + quantity + " = " + (price * quantity) + " VNĐ");
        System.out.println("💰 Total amount: " + totalAmount + " VNĐ");
    }
    
    /**
     * Update existing order item row
     */
    private void updateOrderItemRow(OrderItem item) {
        // Only update UI if FXML elements are available
        if (orderItemsContainer == null) return;
        
        // Find and update the existing row
        for (int i = 0; i < orderItemsContainer.getChildren().size(); i++) {
            Node node = orderItemsContainer.getChildren().get(i);
            if (node instanceof HBox) {
                HBox row = (HBox) node;
                Label nameLabel = (Label) row.getChildren().get(0);
                if (nameLabel.getText().equals(item.productName)) {
                    // Update quantity and price
                    Label quantityLabel = (Label) row.getChildren().get(1);
                    Label priceLabel = (Label) row.getChildren().get(2);
                    
                    quantityLabel.setText(String.valueOf(item.quantity));
                    priceLabel.setText(String.format("%,.0f VNĐ", item.totalPrice));
                    break;
                }
            }
        }
    }

    private HBox createOrderItemRow(String productName, double price, int quantity) {
        HBox row = new HBox(8);
        row.setStyle("-fx-padding: 5; -fx-background-color: #f9f9f9; -fx-background-radius: 4;");

        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-size: 11px; -fx-pref-width: 100;");

        Label quantityLabel = new Label(String.valueOf(quantity));
        quantityLabel.setStyle("-fx-font-size: 11px; -fx-alignment: center; -fx-pref-width: 30;");

        Label priceLabel = new Label(String.format("%,.0f VNĐ", price * quantity));
        priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #E67E22; -fx-font-weight: bold; -fx-pref-width: 80;");

        Button removeButton = new Button("×");
        removeButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
        removeButton.setOnAction(e -> removeOrderItem(row, productName, price * quantity));

        row.getChildren().addAll(nameLabel, quantityLabel, priceLabel, removeButton);
        return row;
    }

    private void removeOrderItem(HBox itemRow, String productName, double itemTotal) {
        if (orderItemsContainer != null) {
            orderItemsContainer.getChildren().remove(itemRow);
        }
        orderItems.remove(productName);
        totalAmount -= itemTotal;
        updateTotalAmount();

        if (orderItemsContainer != null && orderItemsContainer.getChildren().isEmpty()) {
            Label placeholder = new Label("Chưa có món nào được chọn");
            placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            orderItemsContainer.getChildren().add(placeholder);
        }
    }

    private void updateTotalAmount() {
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
        }
    }

    public void placeOrder() {
        if (currentTableName.equals("--")) {
            showError("Vui lòng chọn bàn trước khi đặt món");
            return;
        }

        if (currentOrder == null || orderItems.isEmpty()) {
            showError("Vui lòng chọn món trước khi đặt");
            return;
        }

        try {
            // Update order status to "preparing"
            currentOrder.setOrderStatus("preparing");
            currentOrder.setTotalAmount(totalAmount);
            currentOrder.setFinalAmount(totalAmount);
            
            boolean updated = orderService.updateOrderStatus(currentOrder);
            if (updated) {
                System.out.println("✅ Order placed successfully: " + currentOrder.getOrderNumber());
                System.out.println("📋 Order details:");
                System.out.println("   - Table: " + currentTableName);
                System.out.println("   - Total items: " + orderItems.size());
                System.out.println("   - Total amount: " + totalAmount + " VNĐ");
                System.out.println("   - Status: " + currentOrder.getOrderStatus());
            } else {
                showError("Không thể cập nhật đơn hàng");
            }
        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi đặt món: " + e.getMessage());
        }
    }

    public void processPayment() {
        if (currentTableId == null) {
            showError("Vui lòng chọn bàn trước khi thanh toán");
            return;
        }
        
        if (orderItems.isEmpty() || totalAmount <= 0) {
            showError("Không có món nào để thanh toán");
            return;
        }

        try {
            // Create order if not exists
            if (currentOrder == null) {
                currentOrder = orderService.createOrder(currentTableId, currentUserId, null);
                if (currentOrder == null) {
                    showError("Không thể tạo đơn hàng mới");
                    return;
                }
                System.out.println("✅ Created new order: " + currentOrder.getOrderNumber());
            }
            
            // Add all items from orderItems to the order in database
            for (OrderItem item : orderItems.values()) {
                Product product = findProductByName(item.productName);
                if (product != null) {
                    boolean added = orderService.addProductToOrder(currentOrder, product, item.quantity, null);
                    if (!added) {
                        showError("Không thể thêm sản phẩm " + item.productName + " vào đơn hàng");
                        return;
                    }
                }
            }
            
            // Update order status to "paid"
            currentOrder.setOrderStatus("paid");
            currentOrder.setPaymentStatus("paid");
            
            boolean updated = orderService.updateOrderStatus(currentOrder);
            if (updated) {
                if (CafeManagementApplication.class != null) {
                    CafeManagementApplication.showSuccessAlert("Thành công", "Đã thanh toán thành công");
                } else {
                    System.out.println("✅ Payment processed successfully: " + currentOrder.getOrderNumber());
                }
                System.out.println("✅ Payment processed successfully: " + currentOrder.getOrderNumber());
                clearOrder();
            } else {
                showError("Không thể cập nhật trạng thái thanh toán");
            }
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            e.printStackTrace();
            showError("Lỗi thanh toán: " + e.getMessage());
        }
    }

    private void clearOrder() {
        try {
            System.out.println("🧹 Clearing order completely...");
            
            // Clear UI container safely
            if (orderItemsContainer != null) {
                orderItemsContainer.getChildren().clear();
                System.out.println("✅ Cleared UI container");
            } else {
                System.out.println("⚠️ orderItemsContainer is null");
            }
            
            // Clear order items safely
            if (orderItems != null) {
                int itemCount = orderItems.size();
                orderItems.clear();
                System.out.println("✅ Cleared " + itemCount + " order items");
            } else {
                System.out.println("⚠️ orderItems is null");
            }
            
            // Reset total amount
            double oldTotal = totalAmount;
            totalAmount = 0.0;
            System.out.println("💰 Reset total amount: " + oldTotal + " → 0.0");
            
            // Clear current order
            if (currentOrder != null) {
                System.out.println("📋 Clearing current order: " + currentOrder.getOrderNumber());
            }
            currentOrder = null;
            
            // Reset table information
            String oldTableName = currentTableName;
            currentTableName = "--";
            currentTableId = null;
            currentTableStatus = TableStatus.AVAILABLE;
            System.out.println("🪑 Reset table info: " + oldTableName + " → --");
            
            // Reset user information (will be re-initialized from session when needed)
            currentUserId = 1; // Default value, will be updated from session
            System.out.println("👤 Reset user info to default");
            
            // Update UI safely
            updateTotalAmount();
            
            // Update table info display
            if (tableInfoLabel != null) {
                tableInfoLabel.setText("Bàn: --");
                System.out.println("✅ Updated table info label");
            }
            
            // Add placeholder if UI is available
            if (orderItemsContainer != null) {
                Label placeholder = new Label("Chưa có món nào được chọn");
                placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
                orderItemsContainer.getChildren().add(placeholder);
                System.out.println("✅ Added placeholder label");
            }
            
            System.out.println("✅ Order and table info cleared completely");
            
        } catch (Exception e) {
            System.err.println("❌ Error clearing order: " + e.getMessage());
            e.printStackTrace();
            // Try to recover from error
            try {
                if (orderItems != null) orderItems.clear();
                totalAmount = 0.0;
                currentOrder = null;
                currentTableName = "--";
                currentTableId = null;
                currentTableStatus = TableStatus.AVAILABLE;
            } catch (Exception recoveryError) {
                System.err.println("❌ Failed to recover from clear order error: " + recoveryError.getMessage());
            }
        }
    }

    private void showError(String message) {
        System.err.println("❌ Error: " + message);
        try {
            if (CafeManagementApplication.class != null) {
                CafeManagementApplication.showErrorAlert("Lỗi", message);
            }
        } catch (Exception e) {
            // Ignore JavaFX errors in non-JavaFX environment
            System.err.println("⚠️ JavaFX alert error (ignored): " + e.getMessage());
        }
    }

    /**
     * Get current table name
     */
    public String getCurrentTableName() {
        return currentTableName;
    }

    /**
     * Get current table status
     */
    public TableStatus getCurrentTableStatus() {
        return currentTableStatus;
    }

    /**
     * Test database connection and menu data
     */
    public void testDatabaseConnection() {
        try {
            System.out.println("🔍 Testing database connection...");
            
            // Test menu service
            List<Product> products = menuService.getAllAvailableProducts();
            System.out.println("✅ Found " + products.size() + " available products");
            
            if (!products.isEmpty()) {
                Product firstProduct = products.get(0);
                System.out.println("📦 Sample product: " + firstProduct.getProductName() + " - " + firstProduct.getPrice() + " VNĐ");
            }
            
            // Test order service
            System.out.println("✅ OrderService initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Get available products for menu display
     */
    public List<Product> getAvailableProducts() {
        try {
            return menuService.getAllAvailableProducts();
        } catch (Exception e) {
            System.err.println("Error getting available products: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Get products by category
     */
    public List<Product> getProductsByCategory(Integer categoryId) {
        try {
            return menuService.getProductsByCategory(categoryId);
        } catch (Exception e) {
            System.err.println("Error getting products by category: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Add product to order (for testing without UI)
     */
    public void addProductToOrder(String productName, double price, int quantity) {
        addToOrder(productName, price, quantity);
    }


    
    /**
     * Inner class to represent order item
     */
    private static class OrderItem {
        String productName;
        double price;
        int quantity;
        double totalPrice;
        
        OrderItem(String productName, double price, int quantity) {
            this.productName = productName;
            this.price = price;
            this.quantity = quantity;
            this.totalPrice = price * quantity;
        }
    }
}
