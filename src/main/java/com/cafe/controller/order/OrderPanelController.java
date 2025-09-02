package com.cafe.controller.order;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.controller.base.DashboardHelper;
import com.cafe.controller.payment.PaymentController;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;
import com.cafe.service.OrderService;
import com.cafe.service.MenuService;
import com.cafe.service.PaymentService;
import com.cafe.util.SessionManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;

import java.net.URL;
import java.util.*;
import java.lang.reflect.Method;

/**
 * Controller cho order panel - UPDATED với Auto Table Status Updates
 * Quản lý đơn hàng hiện tại và tự động cập nhật trạng thái bàn
 *
 * @author Team 2_C2406L
 * @version 2.1.0 (Auto Table Status)
 */
public class OrderPanelController implements Initializable, DashboardCommunicator {

    @FXML private VBox orderPanel;
    @FXML private Label tableInfoLabel;
    @FXML private ScrollPane orderScrollPane;
    @FXML private VBox orderItemsContainer;
    @FXML private Label totalAmountLabel;
    @FXML private Button placeOrderButton;
    @FXML private Button paymentButton;
    @FXML private Button clearOrderButton;

    // ✅ Services - Complete Integration
    private OrderService orderService;
    private MenuService menuService;
    private PaymentService paymentService;

    // Current state
    private Order currentOrder;
    private int currentTableId = -1;
    private int currentUserId = -1; // Will be set from SessionManager
    private List<OrderDetail> currentOrderDetails = new ArrayList<>();
    
    // ✅ NEW: Flag to prevent auto-update when table just reserved
    private boolean skipAutoStatusUpdate = false;

    // ✅ Dashboard communication
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // ✅ Initialize services
            orderService = new OrderService();
            menuService = new MenuService();
            paymentService = new PaymentService();

            // ✅ Get current user ID from SessionManager
            initializeUserSession();

            // Setup button actions
            setupButtonActions();

            // Initialize UI state
            updateOrderDisplay();

            System.out.println("✅ OrderPanelController initialized with Auto Table Status Updates");

        } catch (Exception e) {
            System.err.println("❌ Error initializing OrderPanelController: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ OrderPanelController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }

    /**
     * ✅ NEW: Initialize user session and get current user ID
     */
    private void initializeUserSession() {
        try {
            if (SessionManager.isLoggedIn()) {
                currentUserId = SessionManager.getCurrentUserId();
                String username = SessionManager.getCurrentUsername();
                String fullName = SessionManager.getCurrentUserFullName();
                String role = SessionManager.getCurrentUserRole();
                
                System.out.println("✅ User session initialized:");
                System.out.println("  - User ID: " + currentUserId);
                System.out.println("  - Username: " + username);
                System.out.println("  - Full Name: " + fullName);
                System.out.println("  - Role: " + role);
                
                // Validate user ID
                if (currentUserId <= 0) {
                    System.err.println("⚠️ Invalid user ID from session: " + currentUserId);
                    currentUserId = 1; // Fallback to default
                    System.out.println("🔄 Using fallback user ID: " + currentUserId);
                }
            } else {
                System.err.println("⚠️ No user session found - user not logged in");
                currentUserId = 1; // Fallback to default
                System.out.println("🔄 Using fallback user ID: " + currentUserId);
            }
        } catch (Exception e) {
            System.err.println("❌ Error initializing user session: " + e.getMessage());
            currentUserId = 1; // Fallback to default
            System.out.println("🔄 Using fallback user ID due to error: " + currentUserId);
        }
    }

    /**
     * Setup button actions
     */
    private void setupButtonActions() {
        placeOrderButton.setOnAction(e -> handlePlaceOrder());
        paymentButton.setOnAction(e -> handlePayment());
        clearOrderButton.setOnAction(e -> handleClearOrder());
    }

    // =====================================================
    // ✅ CORE ORDER MANAGEMENT WITH AUTO TABLE STATUS
    // =====================================================

    /**
     * ✅ ENHANCED: Set current table và load existing order
     */
    public void setCurrentTable(int tableId) {
        System.out.println("🏢 Setting current table: " + tableId);

        // ⚠️ QUAN TRỌNG: Clear cache trước khi set table mới
        if (this.currentTableId != tableId) {
            clearOrderState();
            System.out.println("🔄 Cache cleared for new table selection");
        }

        this.currentTableId = tableId;
        this.skipAutoStatusUpdate = false; // Reset flag for normal operation
        updateTableInfo();

        // ✅ Load existing order for this table
        loadExistingOrderForTable();
    }

    /**
     * ✅ NEW: Set current table for just-reserved table (skip auto-update)
     */
    public void setCurrentTableForReserved(int tableId) {
        System.out.println("🏢 Setting current table (reserved): " + tableId);

        this.currentTableId = tableId;
        this.skipAutoStatusUpdate = true; // Skip auto-update to preserve "reserved" status
        updateTableInfo();

        // ✅ Load existing order for this table
        loadExistingOrderForTable();
    }

    /**
     * ✅ NEW: Enable auto-update when user starts ordering (reserved → occupied)
     */
    public void enableAutoStatusUpdate() {
        this.skipAutoStatusUpdate = false;
        System.out.println("🔄 Auto-update enabled for table " + currentTableId);
        
        // Trigger status update now that auto-update is enabled
        updateTableStatusBasedOnOrder();
    }

    /**
     * ✅ ENHANCED: Load existing order with automatic table status detection
     */
    private void loadExistingOrderForTable() {
        if (currentTableId <= 0) {
            System.out.println("⚠️ No valid table selected");
            return;
        }

        Task<Optional<Order>> loadOrderTask = new Task<Optional<Order>>() {
            @Override
            protected Optional<Order> call() throws Exception {
                System.out.println("🔍 Looking for active order for table: " + currentTableId);
                return orderService.getActiveOrderByTable(currentTableId);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Optional<Order> orderOpt = getValue();
                    if (orderOpt.isPresent()) {
                        currentOrder = orderOpt.get();
                        System.out.println("✅ Found existing order: " + currentOrder.getOrderNumber());
                        loadOrderDetails();

                        // ✅ Auto-update table status based on order existence
                        updateTableStatusBasedOnOrder();
                    } else {
                        System.out.println("📋 No existing order for table " + currentTableId);
                        // Clear current order state
                        currentOrder = null;
                        currentOrderDetails.clear();
                        updateOrderDisplay();

                        // ✅ Auto-update table status based on order existence
                        updateTableStatusBasedOnOrder();
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Error loading existing order: " + getException().getMessage());
                    // Continue with no order
                    currentOrder = null;
                    currentOrderDetails.clear();
                    updateOrderDisplay();
                });
            }
        };

        new Thread(loadOrderTask).start();
    }

    /**
     * ✅ FIXED: Update table status based on current order state - RESPECTS RESERVED STATUS
     */
    private void updateTableStatusBasedOnOrder() {
        // ✅ SKIP auto-update if table was just reserved
        if (skipAutoStatusUpdate) {
            System.out.println("⏭️ Table " + currentTableId + " auto-update skipped (just reserved)");
            return;
        }
        
        // Get current table status first to make informed decisions
        String currentTableStatus = getCurrentTableStatus();
        System.out.println("🔍 Debug: currentTableStatus = " + currentTableStatus + ", currentOrder = " + (currentOrder != null ? "exists" : "null"));
        
        if (currentOrder == null) {
            // ✅ FIXED: Don't auto-change reserved/cleaning tables to available
            // Only change to available if currently occupied
            if ("occupied".equalsIgnoreCase(currentTableStatus)) {
                updateTableStatusIfNeeded("available");
            } else {
                // Preserve reserved/available/cleaning status when no order
                System.out.println("⏭️ Table " + currentTableId + " status preserved: " + currentTableStatus + " (no order)");
            }
            return;
        }

        String orderStatus = currentOrder.getOrderStatus();
        String paymentStatus = currentOrder.getPaymentStatus();
        String targetStatus;
        
        switch (orderStatus.toLowerCase()) {
            case "pending":
            case "confirmed":
            case "preparing":
            case "ready":
                // ✅ ENHANCED: Check if order has been paid but not completed yet
                if ("paid".equals(paymentStatus)) {
                    // Order paid but food not served → keep table as cleaning until manually cleared
                    targetStatus = "cleaning";
                    System.out.println("🍽️ Order paid but not served - table set to cleaning");
                } else {
                    // ✅ Normal workflow: reserved → occupied when order is active
                    targetStatus = "occupied";
                }
                break;
            case "completed":
                targetStatus = "cleaning";
                break;
            case "cancelled":
                // ✅ From any status → available when order is cancelled
                targetStatus = "available";
                break;
            default:
                // ✅ Default: respect current status for unknown order states
                if ("reserved".equalsIgnoreCase(currentTableStatus)) {
                    targetStatus = "reserved"; // Keep reserved
                } else {
                    targetStatus = "occupied"; // Default to occupied if order exists
                }
        }
        
        // Only update if status actually needs to change
        updateTableStatusIfNeeded(targetStatus);
    }

    /**
     * ✅ NEW: Get current table status from TableController
     */
    private String getCurrentTableStatus() {
        try {
            if (dashboardController != null) {
                Method getCurrentTableControllerMethod = dashboardController.getClass().getMethod("getCurrentTableController");
                Object tableController = getCurrentTableControllerMethod.invoke(dashboardController);
                
                if (tableController != null) {
                    Method getTableByIdMethod = tableController.getClass().getMethod("getTableById", int.class);
                    Object table = getTableByIdMethod.invoke(tableController, currentTableId);
                    
                    if (table != null) {
                        Method getStatusMethod = table.getClass().getMethod("getStatus");
                        return (String) getStatusMethod.invoke(table);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not get current table status: " + e.getMessage());
        }
        
        return "available"; // Default fallback
    }

    /**
     * ✅ OPTIMIZED: Update table status only if it's different from current status
     */
    private void updateTableStatusIfNeeded(String newStatus) {
        try {
            // Get current table status using the dedicated method
            String currentStatus = getCurrentTableStatus();
            
            // Only update if status is actually different
            if (newStatus.equalsIgnoreCase(currentStatus)) {
                System.out.println("⏭️ Table " + currentTableId + " status unchanged: " + currentStatus);
                return;
            }
            
            // Update status through Dashboard
            DashboardHelper.updateTableStatus(dashboardController, currentTableId, newStatus);
            System.out.println("✅ Table " + currentTableId + " status updated: " + currentStatus + " → " + newStatus);
            
        } catch (Exception e) {
            System.err.println("❌ Error updating table status: " + e.getMessage());
        }
    }

    /**
     * ✅ LEGACY: Update table status through Dashboard (for backward compatibility)
     */
    private void updateTableStatus(String newStatus) {
        updateTableStatusIfNeeded(newStatus);
    }

    /**
     * ✅ COMPLETE: Load order details for current order
     */
    private void loadOrderDetails() {
        if (currentOrder == null) {
            currentOrderDetails.clear();
            updateOrderDisplay();
            return;
        }

        Task<List<OrderDetail>> loadDetailsTask = new Task<List<OrderDetail>>() {
            @Override
            protected List<OrderDetail> call() throws Exception {
                return orderService.getOrderDetails(currentOrder.getOrderId());
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    currentOrderDetails = getValue();
                    System.out.println("✅ Loaded " + currentOrderDetails.size() + " order details");
                    updateOrderDisplay();
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Error loading order details: " + getException().getMessage());
                    currentOrderDetails.clear();
                    updateOrderDisplay();
                });
            }
        };

        new Thread(loadDetailsTask).start();
    }

    /**
     * ✅ ENHANCED: Add product with automatic table status update and permission check
     */
    public void addProduct(Product product, int quantity) {
        if (product == null || quantity <= 0) {
            System.err.println("❌ Invalid product or quantity");
            return;
        }

        if (currentTableId <= 0) {
            showError("Vui lòng chọn bàn trước khi thêm món");
            return;
        }

        // ✅ Check if table is in cleaning status - prevent new orders
        String currentTableStatus = getCurrentTableStatus();
        if ("cleaning".equalsIgnoreCase(currentTableStatus)) {
            showError("Bàn đang được dọn dẹp. Vui lòng hoàn thành dọn dẹp trước khi tạo order mới.");
            return;
        }



        Task<Boolean> addProductTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // ✅ Create order if not exists
                    if (currentOrder == null) {
                        System.out.println("📋 Creating new order for table " + currentTableId);
                        int userId = getCurrentUserId(); // Get current user ID from session
                        System.out.println("🔍 Using user ID: " + userId + " for order creation");
                        currentOrder = orderService.createOrder(currentTableId, userId, null);
                        if (currentOrder == null) {
                            throw new Exception("Không thể tạo đơn hàng mới");
                        }
                        System.out.println("✅ Created order: " + currentOrder.getOrderNumber() + " by user " + userId);
                        
                        // ✅ FIXED: When adding first item, change table status to "reserved"
                        updateTableStatusIfNeeded("reserved");
                        System.out.println("🔄 Table " + currentTableId + " status changed to 'reserved' (first item added)");
                    }

                    // ✅ Add product to order using OrderService
                    boolean success = orderService.addProductToOrder(currentOrder, product, quantity);
                    if (!success) {
                        throw new Exception("Không thể thêm sản phẩm vào đơn hàng");
                    }

                    return true;
                } catch (Exception e) {
                    System.err.println("❌ Error in addProduct task: " + e.getMessage());
                    throw e;
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Boolean success = getValue();
                    if (success) {
                        System.out.println("✅ Product added successfully: " + product.getProductName() + " x" + quantity);

                        // ✅ Don't clear cache after adding products - let loadOrderDetails() handle UI updates

                        // ✅ Enable auto-update when user actually starts ordering (reserved → occupied)
                        if (skipAutoStatusUpdate) {
                            enableAutoStatusUpdate();
                        }

                        // ✅ Don't auto-update table status when adding products
                        // Table status will be updated to "occupied" only when placing order

                        // ✅ Reload order details to get updated data
                        loadOrderDetails();

                        // Note: Success message is already shown by MenuController
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("❌ Failed to add product: " + getException().getMessage());
                    showError("Lỗi thêm sản phẩm: " + getException().getMessage());
                });
            }
        };

        new Thread(addProductTask).start();
    }

    /**
     * ✅ NEW: Remove product from order
     */
    public void removeProduct(int productId) {
        if (currentOrder == null) {
            return;
        }

        Task<Boolean> removeProductTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return orderService.removeProductFromOrder(currentOrder, productId);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Boolean success = getValue();
                    if (success) {
                        System.out.println("✅ Product removed successfully");
                        loadOrderDetails(); // Refresh

                        // ✅ Check if order is now empty and update table status
                        if (currentOrderDetails.isEmpty()) {
                            updateTableStatusIfNeeded("available");
                        }

                        // Note: Success message is logged to console
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi xóa sản phẩm: " + getException().getMessage());
                });
            }
        };

        new Thread(removeProductTask).start();
    }

    /**
     * ✅ NEW: Update product quantity in order
     */
    public void updateProductQuantity(int productId, int newQuantity) {
        if (currentOrder == null || newQuantity < 0) {
            return;
        }

        Task<Boolean> updateQuantityTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return orderService.updateProductQuantity(currentOrder, productId, newQuantity);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Boolean success = getValue();
                    if (success) {
                        System.out.println("✅ Quantity updated successfully");
                        loadOrderDetails(); // Refresh
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi cập nhật số lượng: " + getException().getMessage());
                });
            }
        };

        new Thread(updateQuantityTask).start();
    }

    // =====================================================
    // ✅ UI UPDATE METHODS
    // =====================================================

    /**
     * Update table info display
     */
    private void updateTableInfo() {
        if (currentTableId > 0) {
            tableInfoLabel.setText("Bàn: " + currentTableId);
        } else {
            tableInfoLabel.setText("Bàn: --");
        }
    }

    /**
     * ✅ ENHANCED: Update order display với OrderService data
     */
    private void updateOrderDisplay() {
        orderItemsContainer.getChildren().clear();

        if (currentOrderDetails.isEmpty()) {
            Label emptyLabel = new Label("Chưa có món nào được chọn");
            emptyLabel.setStyle("-fx-text-fill: #999; -fx-font-style: italic; -fx-alignment: center;");
            orderItemsContainer.getChildren().add(emptyLabel);

            totalAmountLabel.setText("0 VNĐ");
            placeOrderButton.setDisable(true);
            paymentButton.setDisable(true);

        } else {
            // ✅ Display order items from OrderDetails
            for (OrderDetail orderDetail : currentOrderDetails) {
                HBox itemRow = createOrderItemRow(orderDetail);
                orderItemsContainer.getChildren().add(itemRow);
            }

            // ✅ Update total amount from current order
            double totalAmount = currentOrder != null ? currentOrder.getTotalAmount() : 0.0;
            totalAmountLabel.setText(formatAmount(totalAmount));

            // ✅ Enable/disable buttons based on order status
            boolean canModify = currentOrder != null && orderService.canModifyOrder(currentOrder);
            boolean canPay = currentOrder != null && orderService.canPayOrder(currentOrder);

            // ✅ DEBUG: Log button states
            System.out.println("🔧 Button states debug:");
            System.out.println("  - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
            System.out.println("  - orderStatus: " + (currentOrder != null ? currentOrder.getOrderStatus() : "null"));
            System.out.println("  - paymentStatus: " + (currentOrder != null ? currentOrder.getPaymentStatus() : "null"));
            System.out.println("  - finalAmount: " + (currentOrder != null ? currentOrder.getFinalAmount() : 0));
            System.out.println("  - canModify: " + canModify);
            System.out.println("  - canPay: " + canPay);
            System.out.println("  - orderDetails.size(): " + currentOrderDetails.size());

            placeOrderButton.setDisable(!canModify || currentOrderDetails.isEmpty());
            paymentButton.setDisable(!canPay);
            
            System.out.println("  - placeOrderButton.disabled: " + placeOrderButton.isDisabled());
            System.out.println("  - paymentButton.disabled: " + paymentButton.isDisabled());
        }
    }

    /**
     * ✅ ENHANCED: Create order item row UI from OrderDetail
     */
    private HBox createOrderItemRow(OrderDetail orderDetail) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(5));
        row.setStyle("-fx-border-color: #e0e0e0; -fx-border-width: 0 0 1 0;");

        // ✅ Get product info (productName might be loaded with OrderDetail)
        String productName = orderDetail.getProductName();
        if (productName == null || productName.isEmpty()) {
            productName = "Sản phẩm " + orderDetail.getProductId(); // Fallback
        }

        // Product info
        VBox productInfo = new VBox(2);
        Label nameLabel = new Label(productName);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        Label priceLabel = new Label(formatAmount(orderDetail.getUnitPrice()));
        priceLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        productInfo.getChildren().addAll(nameLabel, priceLabel);

        // Quantity controls
        HBox quantityControls = createQuantityControls(orderDetail);

        // Total price for this item
        Label totalLabel = new Label(formatAmount(orderDetail.getTotalPrice()));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #E67E22; -fx-min-width: 80; -fx-alignment: center-right;");

        // Remove button
        Button removeBtn = new Button("X");
        removeBtn.setPrefSize(25, 25);
        removeBtn.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
        removeBtn.setOnAction(e -> removeProduct(orderDetail.getProductId()));

        row.getChildren().addAll(productInfo, quantityControls, totalLabel, removeBtn);
        HBox.setHgrow(productInfo, Priority.ALWAYS);

        return row;
    }

    /**
     * ✅ NEW: Create quantity controls for order detail
     */
    private HBox createQuantityControls(OrderDetail orderDetail) {
        HBox quantityControls = new HBox(5);
        quantityControls.setAlignment(Pos.CENTER);

        Button minusBtn = new Button("-");
        minusBtn.setPrefSize(25, 25);
        minusBtn.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-weight: bold;");
        minusBtn.setOnAction(e -> {
            int newQuantity = orderDetail.getQuantity() - 1;
            updateProductQuantity(orderDetail.getProductId(), newQuantity);
        });

        Label quantityLabel = new Label(String.valueOf(orderDetail.getQuantity()));
        quantityLabel.setStyle("-fx-font-weight: bold; -fx-min-width: 25; -fx-alignment: center;");

        Button plusBtn = new Button("+");
        plusBtn.setPrefSize(25, 25);
        plusBtn.setStyle("-fx-background-color: #28a745; -fx-text-fill: white; -fx-font-weight: bold;");
        plusBtn.setOnAction(e -> {
            int newQuantity = orderDetail.getQuantity() + 1;
            updateProductQuantity(orderDetail.getProductId(), newQuantity);
        });

        quantityControls.getChildren().addAll(minusBtn, quantityLabel, plusBtn);
        return quantityControls;
    }

    // =====================================================
    // ✅ ORDER ACTIONS WITH AUTO TABLE STATUS
    // =====================================================

    /**
     * ✅ ENHANCED: Handle place order with table status update
     */
    private void handlePlaceOrder() {
        if (currentOrder == null || currentOrderDetails.isEmpty()) {
            showError("Không có món nào để đặt");
            return;
        }

        if (currentTableId <= 0) {
            showError("Vui lòng chọn bàn trước khi đặt hàng");
            return;
        }

        // ✅ Check if table is in cleaning status - prevent placing orders
        String currentTableStatus = getCurrentTableStatus();
        if ("cleaning".equalsIgnoreCase(currentTableStatus)) {
            showError("Bàn đang được dọn dẹp. Vui lòng hoàn thành dọn dẹp trước khi đặt món.");
            return;
        }

        Task<Boolean> placeOrderTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return orderService.placeOrder(currentOrder);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Boolean success = getValue();
                    if (success) {
                        showInfo("Đã đặt hàng thành công!");

                        // ✅ Update table status to occupied (confirmed order)
                        updateTableStatusIfNeeded("occupied");

                        // ✅ Update button states
                        placeOrderButton.setDisable(true);
                        paymentButton.setDisable(false);
                        
                        // ✅ DEBUG: Confirm button states after place order
                        System.out.println("🔧 Post-place order button states:");
                        System.out.println("  - placeOrderButton.disabled: " + placeOrderButton.isDisabled());
                        System.out.println("  - paymentButton.disabled: " + paymentButton.isDisabled());

                        System.out.println("✅ Order placed: " + currentOrder.getOrderNumber());
                    } else {
                        showError("Không thể đặt hàng");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi đặt hàng: " + getException().getMessage());
                });
            }
        };

        new Thread(placeOrderTask).start();
    }

    /**
     * ✅ ENHANCED: Handle payment action with modern PaymentController and permission check
     */
    private void handlePayment() {
        if (currentOrder == null) {
            showError("Không có đơn hàng để thanh toán");
            return;
        }

        if (currentOrderDetails.isEmpty()) {
            showError("Đơn hàng trống, không thể thanh toán");
            return;
        }



        // ✅ Show modern payment window
        showModernPaymentWindow();
    }

    /**
     * ✅ UPDATED: Show modern payment window with callback
     */
    private void showModernPaymentWindow() {
        try {
            System.out.println("🔧 showModernPaymentWindow() started");
            System.out.println("  - currentOrder: " + (currentOrder != null ? currentOrder.getOrderNumber() : "null"));
            System.out.println("  - currentTableId: " + currentTableId);
            System.out.println("  - currentOrderDetails.size(): " + currentOrderDetails.size());

            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment/payment.fxml"));
            BorderPane paymentRoot = loader.load();
            PaymentController paymentController = loader.getController();

            // ✅ Initialize with callback
            PaymentController.PaymentCompletionCallback callback = new PaymentController.PaymentCompletionCallback() {
                @Override
                public void onPaymentCompleted(Order order, String paymentMethod) {
                    System.out.println("✅ Payment completion callback received");
                    System.out.println("  - Order: " + order.getOrderNumber());
                    System.out.println("  - Payment Method: " + paymentMethod);
                    
                    // Handle payment completion on OrderPanel side
                    handlePaymentCompletionCallback(order, paymentMethod);
                }

                @Override
                public void onPaymentFailed(Order order, String reason) {
                    System.err.println("❌ Payment failure callback received");
                    System.err.println("  - Order: " + order.getOrderNumber());
                    System.err.println("  - Reason: " + reason);
                    
                    // Handle payment failure on OrderPanel side
                    handlePaymentFailureCallback(order, reason);
                }
            };

            // Initialize payment controller with callback
            paymentController.initData(currentOrder, currentTableId, 8.0, callback);

            // Create and show payment window
            Stage paymentStage = new Stage();
            paymentStage.setTitle("Thanh toán - " + currentOrder.getOrderNumber());
            paymentStage.initModality(Modality.APPLICATION_MODAL);
            paymentStage.setResizable(true);
            paymentStage.setMinWidth(900);
            paymentStage.setMinHeight(700);
            
            Scene paymentScene = new Scene(paymentRoot, 950, 750);
            paymentScene.getStylesheets().add(getClass().getResource("/css/payment.css").toExternalForm());
            
            paymentStage.setScene(paymentScene);
            paymentStage.centerOnScreen();
            paymentStage.show(); // ✅ Use show() instead of showAndWait() since we have callback
            
        } catch (Exception e) {
            System.err.println("❌ Error showing payment window: " + e.getMessage());
            e.printStackTrace();
            showError("Không thể mở cửa sổ thanh toán: " + e.getMessage());
        }
    }

    /**
     * ✅ ENHANCED: Handle payment completion callback with comprehensive error handling
     */
    private void handlePaymentCompletionCallback(Order order, String paymentMethod) {
        Platform.runLater(() -> {
            try {
                System.out.println("🎯 Processing payment completion callback...");
                System.out.println("📋 Order details before update:");
                System.out.println("  - Order ID: " + order.getOrderId());
                System.out.println("  - Order Number: " + order.getOrderNumber());
                System.out.println("  - Current Status: " + order.getOrderStatus());
                System.out.println("  - Current Payment Status: " + order.getPaymentStatus());
                System.out.println("  - Payment Method: " + paymentMethod);
                System.out.println("  - Final Amount: " + order.getFinalAmount());
                
                // 1. ✅ ENHANCED: Update order with comprehensive error handling
                if (order != null) {
                    String originalStatus = order.getOrderStatus();
                    String originalPaymentStatus = order.getPaymentStatus();
                    
                    // Set payment info first
                    order.setPaymentStatus("paid");
                    order.setPaymentMethod(paymentMethod);
                    
                    // ✅ Use updateOrder to save payment info first
                    boolean orderUpdated = orderService.updateOrder(order);
                    if (orderUpdated) {
                        System.out.println("✅ Order payment info updated in database");
                        
                        // ✅ Enhanced completion logic with detailed logging
                        boolean canComplete = order.canBeCompleted();
                        System.out.println("🔍 Order completion check:");
                        System.out.println("  - Can be completed: " + canComplete);
                        System.out.println("  - Order status: " + order.getOrderStatus());
                        System.out.println("  - Payment status: " + order.getPaymentStatus());
                        
                        if (canComplete) {
                            boolean completed = orderService.completeOrder(order);
                            if (completed) {
                                System.out.println("✅ Order marked as completed via completeOrder()");
                            } else {
                                System.out.println("⚠️ Order payment recorded but completeOrder() failed");
                            }
                        } else {
                            // ✅ For orders paid before being served, mark as completed directly
                            System.out.println("🔄 Marking order as completed directly (early payment workflow)");
                            order.setOrderStatus("completed");
                            boolean finalUpdate = orderService.updateOrder(order);
                            if (finalUpdate) {
                                System.out.println("✅ Order marked as completed directly (paid before served)");
                            } else {
                                System.err.println("❌ Failed to mark order as completed directly");
                                // Rollback payment status if completion failed
                                order.setOrderStatus(originalStatus);
                                order.setPaymentStatus(originalPaymentStatus);
                                orderService.updateOrder(order);
                                System.out.println("🔄 Payment status rolled back due to completion failure");
                            }
                        }
                    } else {
                        System.err.println("❌ Failed to update order payment info in database");
                        System.err.println("🔄 Rolling back payment status changes");
                        order.setPaymentStatus(originalPaymentStatus);
                        order.setPaymentMethod(null);
                        return; // Exit early if payment update failed
                    }
                }
                
                // 2. ✅ UPDATE TABLE STATUS TO CLEANING
                String previousTableStatus = getCurrentTableStatus();
                updateTableStatusIfNeeded("cleaning");
                System.out.println("✅ Table status updated: " + previousTableStatus + " → CLEANING");
                
                // 3. Reset order panel state
                currentOrder = null;
                currentOrderDetails.clear();
                updateOrderDisplay();
                System.out.println("🔄 Order panel state reset");
                
                // 4. Update UI button states
                resetButtonStates();
                
                // 5. Show comprehensive success message
                String tableName = getCurrentTableName();
                String successMessage = String.format(
                    "Thanh toán hoàn tất!\n" +
                    "• Order: %s\n" +
                    "• Phương thức: %s\n" +
                    "• Bàn %s → Trạng thái dọn dẹp",
                    order.getOrderNumber(),
                    getPaymentMethodDisplayName(paymentMethod),
                    tableName
                );
                showInfo(successMessage);
                
                System.out.println("✅ Payment completion handling finished successfully");
                System.out.println("📊 Final state summary:");
                System.out.println("  - Order completed and payment recorded");
                System.out.println("  - Table status: cleaning");
                System.out.println("  - UI state: reset for next customer");
                
            } catch (Exception e) {
                System.err.println("❌ Error handling payment completion: " + e.getMessage());
                e.printStackTrace();
                showError("Lỗi xử lý sau thanh toán: " + e.getMessage());
                
                // ✅ Enhanced error details for debugging
                System.err.println("💡 Debug info for payment completion error:");
                System.err.println("  - Order ID: " + (order != null ? order.getOrderId() : "null"));
                System.err.println("  - Payment Method: " + paymentMethod);
                System.err.println("  - Table ID: " + currentTableId);
                System.err.println("  - Exception: " + e.getClass().getSimpleName());
            }
        });
    }
    
    /**
     * ✅ NEW: Get payment method display name
     */
    private String getPaymentMethodDisplayName(String method) {
        if (method == null) return "Không xác định";
        switch (method.toUpperCase()) {
            case "CASH": return "Tiền mặt";
            case "CARD": return "Thẻ tín dụng/ghi nợ";
            case "MOMO": return "Ví MoMo";
            case "VNPAY": return "VNPay";
            case "ZALOPAY": return "ZaloPay";
            case "BANK_TRANSFER": return "Chuyển khoản";
            default: return method;
        }
    }

    /**
     * ✅ ENHANCED: Handle payment failure callback with detailed logging
     */
    private void handlePaymentFailureCallback(Order order, String reason) {
        Platform.runLater(() -> {
            try {
                System.err.println("🎯 Processing payment failure callback...");
                System.err.println("📋 Payment failure details:");
                System.err.println("  - Order: " + (order != null ? order.getOrderNumber() : "null"));
                System.err.println("  - Reason: " + reason);
                System.err.println("  - Table ID: " + currentTableId);
                System.err.println("  - Current Order Status: " + (order != null ? order.getOrderStatus() : "null"));
                System.err.println("  - Current Payment Status: " + (order != null ? order.getPaymentStatus() : "null"));
                
                // Show comprehensive error message to user
                String errorMessage = String.format(
                    "Thanh toán thất bại!\n\n" +
                    "Lý do: %s\n\n" +
                    "Bạn có thể:\n" +
                    "• Thử lại với cùng phương thức\n" +
                    "• Chọn phương thức thanh toán khác\n" +
                    "• Kiểm tra thông tin giao dịch",
                    reason
                );
                showError(errorMessage);
                
                // Keep order and table status unchanged - user can try again
                System.out.println("⚠️ Order and table status preserved for retry");
                System.out.println("🔄 Payment can be retried with same or different method");
                
            } catch (Exception e) {
                System.err.println("❌ Error handling payment failure: " + e.getMessage());
                e.printStackTrace();
                
                // Fallback error message
                showError("Thanh toán thất bại và có lỗi xử lý. Vui lòng thử lại.");
            }
        });
    }

    /**
     * ✅ DEPRECATED: Legacy method - replaced by callback
     */
    private void handlePaymentCompleted() {
        Platform.runLater(() -> {
            try {
                // ✅ Auto-update table status to cleaning after payment
                updateTableStatusIfNeeded("cleaning");
                
                // ✅ Complete the order and reset
                completeOrderAndReset();
                
                // Note: Success message is already shown by main payment completion callback
                
                System.out.println("✅ Payment completed for order: " + currentOrder.getOrderNumber());
                
            } catch (Exception e) {
                System.err.println("❌ Error handling payment completion: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * ✅ ENHANCED: Complete order and reset state with table status
     */
    private void completeOrderAndReset() {
        if (currentOrder == null) return;

        Task<Boolean> completeTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return orderService.completeOrder(currentOrder);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    // ✅ Reset state
                    currentOrder = null;
                    currentOrderDetails.clear();
                    // Keep currentTableId for potential next order

                    // ✅ Update UI
                    updateOrderDisplay();

                    // ✅ Table status already updated to "cleaning" in processPayment

                    System.out.println("✅ Order completed and state reset");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("⚠️ Failed to complete order, but payment was successful");
                });
            }
        };

        new Thread(completeTask).start();
    }

    /**
     * ✅ ENHANCED: Handle clear order with table status update
     */
    private void handleClearOrder() {
        if (currentOrder == null && currentOrderDetails.isEmpty()) {
            showInfo("Không có đơn hàng nào để xóa");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận");
        alert.setHeaderText("Xóa đơn hàng");
        alert.setContentText("Bạn có chắc chắn muốn xóa toàn bộ đơn hàng không?");

        alert.showAndWait().ifPresent(buttonType -> {
            if (buttonType == ButtonType.OK) {
                clearOrderCompletely();
            }
        });
    }

    /**
     * ✅ ENHANCED: Clear order completely with table status update
     */
    private void clearOrderCompletely() {
        Task<Boolean> clearTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                if (currentOrder != null) {
                    // ✅ Cancel order in database
                    return orderService.cancelOrder(currentOrder, "Đơn hàng bị hủy bởi nhân viên");
                }
                return true;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    // ✅ Clear cache completely
                    clearOrderState();

                    // ✅ Update table status to available when order is cleared
                    updateTableStatusIfNeeded("available");

                    // ✅ Update UI
                    updateOrderDisplay();

                    showInfo("Đã xóa đơn hàng");
                    System.out.println("✅ Order cleared successfully, table set to available");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi xóa đơn hàng: " + getException().getMessage());
                });
            }
        };

        new Thread(clearTask).start();
    }

    // =====================================================
    // ✅ UTILITY METHODS
    // =====================================================

    /**
     * Format amount to currency string
     */
    private String formatAmount(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }

    /**
     * Show error message
     */
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show info message
     */
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thông báo");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    // =====================================================
    // ✅ PUBLIC GETTER METHODS
    // =====================================================

    /**
     * Get current order (public method)
     */
    public Order getCurrentOrder() {
        return currentOrder;
    }

    /**
     * Check if has items in order
     */
    public boolean hasItems() {
        return !currentOrderDetails.isEmpty();
    }

    /**
     * Get total amount
     */
    public double getTotalAmount() {
        return currentOrder != null ? currentOrder.getTotalAmount() : 0.0;
    }

    /**
     * Get current table ID
     */
    public int getCurrentTableId() {
        return currentTableId;
    }

    /**
     * ✅ NEW: Get current user ID (refreshed from session)
     */
    public int getCurrentUserId() {
        // Refresh user ID from session in case it changed
        if (SessionManager.isLoggedIn()) {
            int sessionUserId = SessionManager.getCurrentUserId();
            if (sessionUserId > 0) {
                currentUserId = sessionUserId;
            }
        }
        return currentUserId;
    }

    /**
     * ✅ NEW: Refresh user session (call when needed)
     */
    public void refreshUserSession() {
        initializeUserSession();
    }

    /**
     * ✅ NEW: Get current user info for display
     */
    public String getCurrentUserInfo() {
        if (SessionManager.isLoggedIn()) {
            String fullName = SessionManager.getCurrentUserFullName();
            String role = SessionManager.getCurrentUserRole();
            return fullName + " (" + role + ")";
        }
        return "Chưa đăng nhập";
    }





    /**
     * Get order details count
     */
    public int getOrderDetailsCount() {
        return currentOrderDetails.size();
    }

    /**
     * ✅ NEW: Get order status for external monitoring
     */
    public String getOrderStatus() {
        return currentOrder != null ? currentOrder.getOrderStatus() : "none";
    }

    /**
     * ✅ NEW: Quick method to check if can modify order
     */
    public boolean canModifyOrder() {
        return currentOrder != null && orderService.canModifyOrder(currentOrder);
    }
    
    /**
     * ✅ DEBUG: Kiểm tra resource paths và dependencies
     */
    private void debugResourcePaths() {
        System.out.println("🔍 =====DEBUGGING PAYMENT RESOURCES=====");
        
        try {
            // Test FXML path
            java.net.URL fxmlUrl = getClass().getResource("/fxml/payment/payment.fxml");
            if (fxmlUrl == null) {
                System.err.println("❌ FXML không tìm thấy: /fxml/payment/payment.fxml");
                // Thử các path khác
                fxmlUrl = getClass().getResource("../../../fxml/payment/payment.fxml");
                System.out.println("🔍 Trying relative path: " + (fxmlUrl != null ? "FOUND" : "NOT FOUND"));
            } else {
                System.out.println("✅ FXML found: " + fxmlUrl);
            }
            
            // Test CSS path
            java.net.URL cssUrl = getClass().getResource("/css/payment.css");
            if (cssUrl == null) {
                System.err.println("❌ CSS không tìm thấy: /css/payment.css");
            } else {
                System.out.println("✅ CSS found: " + cssUrl);
            }
            
            // Test current order data
            if (currentOrder == null) {
                System.err.println("❌ CRITICAL: currentOrder is NULL");
                return;
            }
            
            System.out.println("✅ Order data:");
            System.out.println("  - Order ID: " + currentOrder.getOrderId());
            System.out.println("  - Order Number: " + currentOrder.getOrderNumber());
            System.out.println("  - Table ID: " + currentOrder.getTableId());
            System.out.println("  - Status: " + currentOrder.getOrderStatus());
            System.out.println("  - Payment Status: " + currentOrder.getPaymentStatus());
            System.out.println("  - Total Amount: " + currentOrder.getTotalAmount());
            
            // Test order details
            System.out.println("✅ Order Details:");
            System.out.println("  - Details count: " + currentOrderDetails.size());
            for (int i = 0; i < Math.min(3, currentOrderDetails.size()); i++) {
                OrderDetail detail = currentOrderDetails.get(i);
                System.out.println("  - Item " + (i+1) + ": " + detail.getProductName() + 
                                   " x" + detail.getQuantity() + " = " + detail.getTotalPrice());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error in debugResourcePaths: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("🔍 =================================");
    }
    
    /**
     * ✅ DEBUG: Test method để debug payment window riêng
     */
    public void debugPaymentWindow() {
        System.out.println("🔧 DEBUG: Testing payment window separately...");
        
        if (currentOrder == null) {
            System.err.println("❌ No current order for debugging");
            return;
        }
        
        showModernPaymentWindow();
    }
    
    // =====================================================
    // ENHANCED HELPER METHODS
    // =====================================================
    
    /**
     * ✅ NEW: Reset button states after payment completion
     */
    private void resetButtonStates() {
        if (placeOrderButton != null) {
            placeOrderButton.setDisable(false);
        }
        if (paymentButton != null) {
            paymentButton.setDisable(true);
        }
        if (clearOrderButton != null) {
            clearOrderButton.setDisable(false);
        }
        
        System.out.println("✅ Button states reset after payment completion");
    }

    /**
     * ✅ NEW: Get current table name for display
     */
    private String getCurrentTableName() {
        try {
            if (dashboardController != null && currentTableId != -1) {
                // Use reflection to get table name from TableController
                Method getCurrentTableControllerMethod = dashboardController.getClass().getMethod("getCurrentTableController");
                Object tableController = getCurrentTableControllerMethod.invoke(dashboardController);
                
                if (tableController != null) {
                    Method getTableByIdMethod = tableController.getClass().getMethod("getTableById", int.class);
                    Object table = getTableByIdMethod.invoke(tableController, currentTableId);
                    
                    if (table != null) {
                        Method getTableNameMethod = table.getClass().getMethod("getTableName");
                        return (String) getTableNameMethod.invoke(table);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️ Could not get current table name: " + e.getMessage());
        }
        
        return "bàn hiện tại"; // Fallback
    }
    
    /**
     * ⚠️ PHƯƠNG THỨC QUAN TRỌNG: Clear cache hoàn toàn
     */
    private void clearOrderCache() {
        System.out.println("🔄 Clearing order cache completely...");
        
        // Clear order details list
        currentOrderDetails.clear();
        
        // Clear UI forms
        clearOrderForm();
        
        // Clear temporary data
        clearTempData();
        
        // Note: Don't clear currentOrder here - let loadOrderDetails() handle it
        System.out.println("✅ Order cache cleared completely");
    }
    
    /**
     * Clear order completely (including currentOrder)
     */
    private void clearOrderState() {
        System.out.println("🔄 Clearing order completely...");
        
        // Clear current order
        currentOrder = null;
        
        // Clear order details list
        currentOrderDetails.clear();
        
        // Clear UI forms
        clearOrderForm();
        
        // Clear temporary data
        clearTempData();
        
        System.out.println("✅ Order cleared completely");
    }
    
    /**
     * Clear form UI
     */
    private void clearOrderForm() {
        // Clear order details display
        if (orderItemsContainer != null) {
            orderItemsContainer.getChildren().clear();
        }
        
        // Reset totals
        if (totalAmountLabel != null) {
            totalAmountLabel.setText("0 VNĐ");
        }
        
        // Clear any other UI elements
        System.out.println("✅ Order form UI cleared");
    }
    
    /**
     * Clear temporary data
     */
    private void clearTempData() {
        // Reset any counters or temporary variables
        skipAutoStatusUpdate = false;
        
        System.out.println("✅ Temporary data cleared");
    }
}