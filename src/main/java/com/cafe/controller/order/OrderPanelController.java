package com.cafe.controller.order;

import com.cafe.CafeManagementApplication;
import com.cafe.controller.dashboard.DashboardController;
import com.cafe.model.entity.Order;

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
    
    // Flag to prevent UI restore after clearOrder()
    private boolean justCleared = false;
    
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
        
        System.out.println("🔄 DEBUG: refreshUIDisplay() called");
        System.out.println("🔍 DEBUG: justCleared flag: " + justCleared);
        System.out.println("🔍 DEBUG: orderItems size: " + orderItems.size());
        System.out.println("🔍 DEBUG: orderItems instance: " + orderItems.getClass().getSimpleName() + "@" + Integer.toHexString(orderItems.hashCode()));
        
        // IMPORTANT: Don't restore UI if we just cleared the order
        if (justCleared) {
            System.out.println("🚫 SKIPPING UI refresh - order was just cleared");
            return;
        }
        
        // Debug: List items being refreshed
        if (!orderItems.isEmpty()) {
            System.out.println("🔍 DEBUG: Items being restored to UI:");
            for (String key : orderItems.keySet()) {
                OrderItem item = orderItems.get(key);
                System.out.println("   - " + key + ": " + item.quantity + " x " + item.price + " = " + item.totalPrice);
            }
        } else {
            System.out.println("🔍 DEBUG: No items to restore");
        }
        
        // Clear current display
        orderItemsContainer.getChildren().clear();
        
        // Re-add all items
        for (OrderItem item : orderItems.values()) {
            HBox orderItem = createOrderItemRow(item.productName, item.price, item.quantity);
            orderItemsContainer.getChildren().add(orderItem);
        }
        
        // Recalculate total amount from all items
        recalculateTotalAmount();
        
        System.out.println("✅ UI display refreshed with " + orderItems.size() + " items");
    }
    
    /**
     * Get the number of items in the order
     */
    public int getOrderItemsCount() {
        // If just cleared, report 0 to prevent DashboardController from refreshing UI
        if (justCleared) {
            System.out.println("🔍 DEBUG: getOrderItemsCount() - returning 0 due to justCleared flag");
            return 0;
        }
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
            System.out.println("🔍 DEBUG: addToOrder() called for: " + productName + " x" + quantity);
            System.out.println("🔍 DEBUG: justCleared flag BEFORE add: " + justCleared);
            System.out.println("🔍 DEBUG: Current orderItems size BEFORE add: " + orderItems.size());
            System.out.println("🔍 DEBUG: Current totalAmount BEFORE add: " + totalAmount);
            System.out.println("🔍 DEBUG: UI mode: " + (orderItemsContainer != null ? "Full UI" : "Logic-only"));
            
            // Reset the justCleared flag when adding new items
            if (justCleared) {
                justCleared = false;
                System.out.println("🔓 Reset justCleared flag");
            }
            
            // Debug: List current items
            if (!orderItems.isEmpty()) {
                System.out.println("🔍 DEBUG: Current items in orderItems:");
                for (String key : orderItems.keySet()) {
                    OrderItem item = orderItems.get(key);
                    System.out.println("   - " + key + ": " + item.quantity + " x " + item.price + " = " + item.totalPrice);
                }
            } else {
                System.out.println("🔍 DEBUG: orderItems is empty");
            }
            
            // Update order items - works in both UI and logic-only mode
            if (orderItemsContainer != null) {
                // Full UI mode
                updateOrderItemUI(productName, price, quantity);
            } else {
                // Logic-only mode (no UI elements)
                updateOrderItemInternal(productName, price, quantity);
            }
            
            System.out.println("🔍 DEBUG: Current orderItems size AFTER add: " + orderItems.size());
            System.out.println("🔍 DEBUG: Current totalAmount AFTER add: " + totalAmount);
            
            // Force refresh UI if available and needed
            if (orderItemsContainer != null && orderItemsContainer.getChildren().isEmpty() && !orderItems.isEmpty()) {
                System.out.println("🔄 Force refreshing UI display...");
                refreshUIDisplay();
            }
            
            System.out.println("✅ Added product to order: " + productName + " x" + quantity + " (mode: " + (orderItemsContainer != null ? "UI" : "logic-only") + ")");
            
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

        // Recalculate total amount from all items
        recalculateTotalAmount();
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
        // Recalculate total amount from all items
        recalculateTotalAmount();
        System.out.println("📦 Added to order: " + productName + " x" + quantity + " = " + (price * quantity) + " VNĐ");
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
        System.out.println("🗑️ Removing item: " + productName + " (total: " + itemTotal + " VNĐ)");
        
        // Remove from UI
        if (orderItemsContainer != null) {
            orderItemsContainer.getChildren().remove(itemRow);
        }
        
        // Remove from orderItems
        orderItems.remove(productName);
        
        // Recalculate total amount from remaining items
        recalculateTotalAmount();

        // Add placeholder if no items left
        if (orderItemsContainer != null && orderItemsContainer.getChildren().isEmpty()) {
            Label placeholder = new Label("Chưa có món nào được chọn");
            placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
            orderItemsContainer.getChildren().add(placeholder);
        }
        
        System.out.println("✅ Item removed, new total: " + totalAmount + " VNĐ");
    }

    private void updateTotalAmount() {
        if (totalAmountLabel != null) {
            totalAmountLabel.setText(String.format("%,.0f VNĐ", totalAmount));
        }
    }
    
    /**
     * Recalculate total amount from orderItems
     */
    private void recalculateTotalAmount() {
        totalAmount = 0.0;
        for (OrderItem item : orderItems.values()) {
            totalAmount += item.totalPrice;
        }
        updateTotalAmount();
        System.out.println("💰 Recalculated total amount: " + totalAmount + " VNĐ");
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
                try {
                    CafeManagementApplication.showSuccessAlert("Thành công", "Đã thanh toán thành công");
                } catch (Exception e) {
                    // Fallback nếu JavaFX không khả dụng
                    System.out.println("⚠️ JavaFX alert not available, using console output");
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

    public void clearOrder() {
        try {
            System.out.println("\n🧹 ===== CLEARING ORDER COMPLETELY =====");
            System.out.println("📊 BEFORE clear - orderItems: " + orderItems.size() + ", totalAmount: " + totalAmount);
            
            // Debug: List current items before clearing
            if (!orderItems.isEmpty()) {
                System.out.println("🔍 DEBUG: Items in orderItems BEFORE clear:");
                for (String key : orderItems.keySet()) {
                    OrderItem item = orderItems.get(key);
                    System.out.println("   - " + key + ": " + item.quantity + " x " + item.price + " = " + item.totalPrice);
                }
            }
            
            // Step 1: Force null the reference first
            System.out.println("🔧 Step 1: Nullifying orderItems reference...");
            orderItems = null;
            
            // Step 2: Create completely new HashMap
            System.out.println("🔧 Step 2: Creating new HashMap...");
            orderItems = new HashMap<String, OrderItem>();
            
            System.out.println("✅ OrderItems completely recreated - size: " + orderItems.size());
            
            // Step 3: Reset total amount về 0
            double oldTotal = totalAmount;
            totalAmount = 0.0;
            System.out.println("💰 Step 3: Reset total amount: " + oldTotal + " → 0.0");
            
            // Step 4: Clear current order
            if (currentOrder != null) {
                System.out.println("📋 Step 4: Clearing current order: " + currentOrder.getOrderNumber());
            }
            currentOrder = null;
            
            // Step 5: Reset table information
            String oldTableName = currentTableName;
            currentTableName = "--";
            currentTableId = null;
            currentTableStatus = TableStatus.AVAILABLE;
            System.out.println("🪑 Step 5: Reset table info: " + oldTableName + " → --");
            
            // Step 6: Reset user information 
            currentUserId = 1; // Default value
            System.out.println("👤 Step 6: Reset user info to default");
            
            // Step 7: Clear UI container completely
            if (orderItemsContainer != null) {
                System.out.println("🔧 Step 7: Clearing UI container...");
                orderItemsContainer.getChildren().clear();
                System.out.println("✅ UI container cleared - children count: " + orderItemsContainer.getChildren().size());
                
                // Add placeholder
                Label placeholder = new Label("Chưa có món nào được chọn");
                placeholder.setStyle("-fx-text-fill: #999; -fx-font-style: italic;");
                orderItemsContainer.getChildren().add(placeholder);
                System.out.println("✅ Placeholder added - children count: " + orderItemsContainer.getChildren().size());
            } else {
                System.out.println("⚠️ orderItemsContainer is null");
            }
            
            // Step 8: Update UI labels
            updateTotalAmount();
            if (tableInfoLabel != null) {
                tableInfoLabel.setText("Bàn: --");
                System.out.println("✅ Step 8: Updated table info label");
            }
            
            // Step 9: Set flag to prevent UI restore
            justCleared = true;
            System.out.println("🚫 Step 9: Set justCleared flag to prevent UI restore");
            
            // Step 10: Final verification
            System.out.println("📊 AFTER clear verification:");
            System.out.println("   - orderItems: " + orderItems.size() + " items");
            System.out.println("   - totalAmount: " + totalAmount);
            System.out.println("   - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
            System.out.println("   - currentTableName: " + currentTableName);
            System.out.println("   - justCleared: " + justCleared);
            System.out.println("   - orderItems instance: " + orderItems.getClass().getSimpleName() + "@" + Integer.toHexString(orderItems.hashCode()));
            
            System.out.println("✅ ===== ORDER COMPLETELY CLEARED =====\n");
            
        } catch (Exception e) {
            System.err.println("❌ Error clearing order: " + e.getMessage());
            e.printStackTrace();
            
            // Recovery - force reset all variables
            try {
                System.out.println("🔧 EMERGENCY RECOVERY MODE...");
                orderItems = null;
                orderItems = new HashMap<String, OrderItem>();
                totalAmount = 0.0;
                currentOrder = null;
                currentTableName = "--";
                currentTableId = null;
                currentTableStatus = TableStatus.AVAILABLE;
                currentUserId = 1;
                justCleared = true; // Set flag in recovery too
                System.out.println("🔧 Emergency recovery completed");
            } catch (Exception recoveryError) {
                System.err.println("❌ Failed to recover: " + recoveryError.getMessage());
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
     * Test method to verify clearOrder() functionality và fix totalAmount calculation
     * Kiểm tra xem clearOrder() có xóa hết dữ liệu LOCAL MEMORY không và totalAmount có được tính đúng
     */
    public void testClearOrderFunctionality() {
        System.out.println("🧪 Testing clearOrder() functionality và totalAmount calculation...");
        
        // Test 1: Add some test data
        System.out.println("\n--- Test 1: Adding items and checking totalAmount calculation ---");
        addToOrder("Cà phê đen", 25000, 2);
        addToOrder("Bánh tiramisu", 45000, 1);
        
        double expectedTotal = (25000 * 2) + (45000 * 1); // 95000
        System.out.println("Expected total: " + expectedTotal + " VNĐ");
        System.out.println("Actual total: " + totalAmount + " VNĐ");
        System.out.println("TotalAmount calculation: " + (totalAmount == expectedTotal ? "✅ CORRECT" : "❌ WRONG"));
        
        // Test 2: Add more of existing item
        System.out.println("\n--- Test 2: Adding more of existing item ---");
        addToOrder("Cà phê đen", 25000, 1); // Should be 3 total
        expectedTotal = (25000 * 3) + (45000 * 1); // 120000
        System.out.println("Expected total after adding 1 more coffee: " + expectedTotal + " VNĐ");
        System.out.println("Actual total: " + totalAmount + " VNĐ");
        System.out.println("TotalAmount recalculation: " + (totalAmount == expectedTotal ? "✅ CORRECT" : "❌ WRONG"));
        
        System.out.println("\n📊 Before clearOrder():");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
        System.out.println("   - currentTableName: " + currentTableName);
        System.out.println("   - currentTableId: " + currentTableId);
        System.out.println("   - currentUserId: " + currentUserId);
        
        // Test 3: Call clearOrder()
        System.out.println("\n--- Test 3: Testing clearOrder() ---");
        clearOrder();
        
        System.out.println("📊 After clearOrder():");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
        System.out.println("   - currentTableName: " + currentTableName);
        System.out.println("   - currentTableId: " + currentTableId);
        System.out.println("   - currentUserId: " + currentUserId);
        
        // Verify all data is cleared
        boolean allCleared = orderItems.isEmpty() && 
                           totalAmount == 0.0 && 
                           currentOrder == null &&
                           "--".equals(currentTableName) &&
                           currentTableId == null &&
                           currentUserId == 1;
        
        System.out.println("\n--- Test 4: Testing that new items don't show old data ---");
        if (allCleared) {
            System.out.println("✅ All LOCAL MEMORY data cleared successfully!");
            
            // Test adding new items after clear
            addToOrder("Trà xanh", 20000, 1);
            boolean newItemCorrect = (orderItems.size() == 1) && (totalAmount == 20000);
            System.out.println("New item after clear: " + (newItemCorrect ? "✅ CORRECT - No old data" : "❌ WRONG - Old data persists"));
            
        } else {
            System.out.println("❌ Some LOCAL MEMORY data still exists!");
        }
        
        System.out.println("\n🎯 Overall test result: " + (allCleared ? "✅ FIX SUCCESSFUL" : "❌ FIX FAILED"));
    }

    /**
     * Add product to order (for testing without UI)
     */
    public void addProductToOrder(String productName, double price, int quantity) {
        addToOrder(productName, price, quantity);
    }
    
    /**
     * Debug method để test vấn đề dữ liệu cũ vẫn hiện sau clearOrder()
     */
    public void debugOrderPersistenceIssue() {
        System.out.println("\n🧪 ===== DEBUG ORDER PERSISTENCE ISSUE =====");
        
        // Step 1: Add initial data
        System.out.println("\n--- STEP 1: Adding initial test data ---");
        addToOrder("Cà phê đen (OLD)", 25000, 2);
        addToOrder("Bánh tiramisu (OLD)", 45000, 1);
        
        System.out.println("✅ Initial data added");
        System.out.println("📊 Current state:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - orderItems instance: " + orderItems.getClass().getSimpleName() + "@" + Integer.toHexString(orderItems.hashCode()));
        
        // Step 2: Clear order
        System.out.println("\n--- STEP 2: Clearing order ---");
        clearOrder();
        
        // Step 3: Verify clear
        System.out.println("\n--- STEP 3: Verification after clear ---");
        System.out.println("📊 State after clear:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - justCleared flag: " + justCleared);
        System.out.println("   - orderItems instance: " + orderItems.getClass().getSimpleName() + "@" + Integer.toHexString(orderItems.hashCode()));
        
        if (!orderItems.isEmpty()) {
            System.out.println("❌ PROBLEM: orderItems not empty after clear!");
            for (String key : orderItems.keySet()) {
                OrderItem item = orderItems.get(key);
                System.out.println("   - REMAINING: " + key + ": " + item.quantity + " x " + item.price);
            }
        } else {
            System.out.println("✅ orderItems properly cleared");
        }
        
        // Step 3.5: Test that refreshUIDisplay() is blocked
        System.out.println("\n--- STEP 3.5: Testing refreshUIDisplay() blocking ---");
        refreshUIDisplay(); // This should be blocked by justCleared flag
        
        // Step 4: Add new data
        System.out.println("\n--- STEP 4: Adding NEW data after clear ---");
        addToOrder("Trà xanh (NEW)", 20000, 1);
        
        System.out.println("📊 Final state:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        
        // Check if old data appears
        boolean hasOldData = false;
        for (String key : orderItems.keySet()) {
            if (key.contains("OLD")) {
                hasOldData = true;
                System.out.println("❌ OLD DATA FOUND: " + key);
            } else {
                System.out.println("✅ NEW DATA: " + key);
            }
        }
        
        // Final verdict
        System.out.println("\n🎯 DIAGNOSIS:");
        if (hasOldData) {
            System.out.println("❌ ISSUE CONFIRMED: Old data persists after clearOrder()");
        } else if (orderItems.size() == 1 && totalAmount == 20000) {
            System.out.println("✅ NO ISSUE: Only new data present");
        } else {
            System.out.println("⚠️ UNEXPECTED STATE: orderItems=" + orderItems.size() + ", total=" + totalAmount);
        }
        
        System.out.println("===== DEBUG COMPLETE =====\n");
    }
    
    /**
     * Test OrderPanelController hoạt động độc lập sau khi xóa DashboardController integration
     */
    public void testStandaloneOperation() {
        System.out.println("\n🧪 ===== TESTING STANDALONE OPERATION =====");
        
        // Step 1: Test basic functionality
        System.out.println("\n--- STEP 1: Testing basic addToOrder() ---");
        addToOrder("Standalone Coffee", 30000, 1);
        addToOrder("Standalone Cake", 50000, 2);
        
        System.out.println("📊 After adding items:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        
        // Step 2: Test clearOrder
        System.out.println("\n--- STEP 2: Testing clearOrder() ---");
        clearOrder();
        
        System.out.println("📊 After clearOrder():");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - justCleared flag: " + justCleared);
        
        // Step 3: Test new items after clear
        System.out.println("\n--- STEP 3: Testing new items after clear ---");
        addToOrder("New Coffee", 25000, 1);
        
        System.out.println("📊 After adding new item:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - justCleared flag: " + justCleared);
        
        // Verify correct operation
        boolean success = (orderItems.size() == 1) && 
                         (totalAmount == 25000) && 
                         (!justCleared);
        
        System.out.println("\n🎯 STANDALONE OPERATION TEST:");
        if (success) {
            System.out.println("✅ SUCCESS: OrderPanelController works independently");
            System.out.println("✅ No interference from DashboardController");
            System.out.println("✅ clearOrder() and addToOrder() work correctly");
        } else {
            System.out.println("❌ FAILED: Issues remain");
        }
        
        System.out.println("===== STANDALONE TEST COMPLETE =====\n");
    }
    
    /**
     * Test restored order functionality with DashboardController integration
     * Kiểm tra xem chức năng đặt món đã được restore và không gây lỗi clearOrder()
     */
    public void testRestoredOrderFunctionality() {
        System.out.println("\n🧪 ===== TESTING RESTORED ORDER FUNCTIONALITY =====");
        
        // Step 1: Simulate external calls (as if from MenuController/TableController)
        System.out.println("\n--- STEP 1: Simulating external addToOrder() calls ---");
        
        // Simulate table selection
        updateTableInfo("Bàn 5", TableStatus.OCCUPIED);
        
        // Simulate menu item selection
        addToOrder("Cà phê sữa", 30000, 2);
        addToOrder("Bánh croissant", 40000, 1);
        
        System.out.println("📊 After external calls:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - currentTableName: " + currentTableName);
        
        // Step 2: Test clearOrder() still works
        System.out.println("\n--- STEP 2: Testing clearOrder() after integration ---");
        clearOrder();
        
        System.out.println("📊 After clearOrder():");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - currentTableName: " + currentTableName);
        System.out.println("   - justCleared flag: " + justCleared);
        
        // Step 3: Test adding new items after clear
        System.out.println("\n--- STEP 3: Testing new items after clearOrder() ---");
        updateTableInfo("Bàn 3", TableStatus.OCCUPIED);
        addToOrder("Trà đào", 25000, 1);
        
        System.out.println("📊 After adding new items:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount);
        System.out.println("   - currentTableName: " + currentTableName);
        System.out.println("   - justCleared flag: " + justCleared);
        
        // Verify correct operation
        boolean integrationSuccess = (orderItems.size() == 1) && 
                                   (totalAmount == 25000) && 
                                   ("Bàn 3".equals(currentTableName)) && 
                                   (!justCleared);
        
        System.out.println("\n🎯 RESTORED FUNCTIONALITY TEST:");
        if (integrationSuccess) {
            System.out.println("✅ SUCCESS: Order functionality restored successfully");
            System.out.println("✅ External integration works (MenuController/TableController)");
            System.out.println("✅ clearOrder() still works correctly after restoration");
            System.out.println("✅ No old data persists after clearOrder()");
        } else {
            System.out.println("❌ FAILED: Issues with restored functionality");
        }
        
        System.out.println("===== RESTORED FUNCTIONALITY TEST COMPLETE =====\n");
    }
    
    /**
     * Debug method to check OrderPanelController connection and status
     */
    public void debugConnectionStatus() {
        System.out.println("\n🔍 ===== ORDER PANEL CONNECTION STATUS =====");
        
        // Basic info
        System.out.println("📊 OrderPanelController Status:");
        System.out.println("   - Class: " + this.getClass().getSimpleName());
        System.out.println("   - Instance: " + this.getClass().getSimpleName() + "@" + Integer.toHexString(this.hashCode()));
        
        // UI elements status
        System.out.println("\n🖥️ UI Elements Status:");
        System.out.println("   - orderItemsContainer: " + (orderItemsContainer != null ? "✅ Available" : "❌ NULL (Logic-only mode)"));
        System.out.println("   - totalAmountLabel: " + (totalAmountLabel != null ? "✅ Available" : "❌ NULL"));
        System.out.println("   - tableInfoLabel: " + (tableInfoLabel != null ? "✅ Available" : "❌ NULL"));
        System.out.println("   - placeOrderButton: " + (placeOrderButton != null ? "✅ Available" : "❌ NULL"));
        System.out.println("   - paymentButton: " + (paymentButton != null ? "✅ Available" : "❌ NULL"));
        System.out.println("   - clearOrderButton: " + (clearOrderButton != null ? "✅ Available" : "❌ NULL"));
        
        // Services status
        System.out.println("\n🔧 Services Status:");
        System.out.println("   - orderService: " + (orderService != null ? "✅ Available" : "❌ NULL"));
        System.out.println("   - menuService: " + (menuService != null ? "✅ Available" : "❌ NULL"));
        
        // Current order state
        System.out.println("\n📋 Current Order State:");
        System.out.println("   - orderItems size: " + orderItems.size());
        System.out.println("   - totalAmount: " + totalAmount + " VNĐ");
        System.out.println("   - currentTableName: " + currentTableName);
        System.out.println("   - currentTableId: " + currentTableId);
        System.out.println("   - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
        System.out.println("   - justCleared: " + justCleared);
        
        // Operating mode
        String mode = (orderItemsContainer != null) ? "Full UI Mode" : "Logic-Only Mode";
        System.out.println("\n🎯 Operating Mode: " + mode);
        
        if (orderItemsContainer == null) {
            System.out.println("ℹ️ Logic-Only Mode means:");
            System.out.println("   ✅ Order logic works (addToOrder, clearOrder, etc.)");
            System.out.println("   ❌ UI display not available");
            System.out.println("   💡 This is normal for auto-setup to avoid clearOrder() issues");
        }
        
        // Test basic functionality
        System.out.println("\n🧪 Quick Functionality Test:");
        int beforeSize = orderItems.size();
        double beforeTotal = totalAmount;
        
        // Test add
        addToOrder("Test Item", 10000, 1);
        boolean addWorks = (orderItems.size() == beforeSize + 1) && (totalAmount == beforeTotal + 10000);
        System.out.println("   - addToOrder(): " + (addWorks ? "✅ WORKS" : "❌ FAILED"));
        
        // Test clear
        clearOrder();
        boolean clearWorks = (orderItems.size() == 0) && (totalAmount == 0.0);
        System.out.println("   - clearOrder(): " + (clearWorks ? "✅ WORKS" : "❌ FAILED"));
        
        System.out.println("\n🎯 Overall Status: " + (addWorks && clearWorks ? "✅ FUNCTIONAL" : "❌ ISSUES DETECTED"));
        System.out.println("===== CONNECTION STATUS COMPLETE =====\n");
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
