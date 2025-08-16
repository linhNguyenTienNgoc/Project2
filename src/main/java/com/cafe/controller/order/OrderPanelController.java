package com.cafe.controller.order;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;
import com.cafe.service.OrderService;
import com.cafe.service.MenuService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.concurrent.Task;

import java.net.URL;
import java.util.*;

/**
 * Controller cho order panel - UPDATED với OrderService Integration
 * Quản lý đơn hàng hiện tại và các thao tác order
 *
 * @author Team 2_C2406L
 * @version 2.0.0 (Complete Integration)
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

    // Current state
    private Order currentOrder;
    private int currentTableId = -1;
    private int currentUserId = 1; // TODO: Get from session
    private List<OrderDetail> currentOrderDetails = new ArrayList<>();

    // ✅ Dashboard communication
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // ✅ Initialize services
            orderService = new OrderService();
            menuService = new MenuService();

            // Setup button actions
            setupButtonActions();

            // Initialize UI state
            updateOrderDisplay();

            System.out.println("✅ OrderPanelController initialized with OrderService integration");

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
     * Setup button actions
     */
    private void setupButtonActions() {
        placeOrderButton.setOnAction(e -> handlePlaceOrder());
        paymentButton.setOnAction(e -> handlePayment());
        clearOrderButton.setOnAction(e -> handleClearOrder());
    }

    // =====================================================
    // ✅ CORE ORDER MANAGEMENT WITH ORDERSERVICE
    // =====================================================

    /**
     * ✅ COMPLETE: Set current table và load existing order
     */
    public void setCurrentTable(int tableId) {
        System.out.println("🏢 Setting current table: " + tableId);

        this.currentTableId = tableId;
        updateTableInfo();

        // ✅ Load existing order for this table
        loadExistingOrderForTable();
    }

    /**
     * ✅ NEW: Load existing order for current table
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
                    } else {
                        System.out.println("📋 No existing order for table " + currentTableId);
                        // Clear current order state
                        currentOrder = null;
                        currentOrderDetails.clear();
                        updateOrderDisplay();
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
     * ✅ COMPLETE: Add product to current order với OrderService
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

        Task<Boolean> addProductTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                try {
                    // ✅ Create order if not exists
                    if (currentOrder == null) {
                        System.out.println("📋 Creating new order for table " + currentTableId);
                        currentOrder = orderService.createOrder(currentTableId, currentUserId, null);
                        if (currentOrder == null) {
                            throw new Exception("Không thể tạo đơn hàng mới");
                        }
                        System.out.println("✅ Created order: " + currentOrder.getOrderNumber());
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

                        // ✅ Reload order details to get updated data
                        loadOrderDetails();

                        showInfo("Đã thêm " + product.getProductName() + " vào đơn hàng");
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
                        showInfo("Đã xóa sản phẩm khỏi đơn hàng");
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

            placeOrderButton.setDisable(!canModify || currentOrderDetails.isEmpty());
            paymentButton.setDisable(!canPay);
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
    // ✅ ORDER ACTIONS WITH ORDERSERVICE
    // =====================================================

    /**
     * ✅ COMPLETE: Handle place order action
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

                        // ✅ Update button states
                        placeOrderButton.setDisable(true);
                        paymentButton.setDisable(false);

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
     * ✅ COMPLETE: Handle payment action
     */
    private void handlePayment() {
        if (currentOrder == null) {
            showError("Không có đơn hàng để thanh toán");
            return;
        }

        // ✅ Show enhanced payment dialog
        showPaymentDialog();
    }

    /**
     * ✅ ENHANCED: Show payment dialog với better UX
     */
    private void showPaymentDialog() {
        Dialog<Map<String, Object>> dialog = new Dialog<>();
        dialog.setTitle("Thanh toán");
        dialog.setHeaderText("Đơn hàng: " + (currentOrder != null ? currentOrder.getOrderNumber() : ""));

        // Create payment form
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        // Order summary
        Label orderSummaryLabel = new Label("Chi tiết đơn hàng:");
        orderSummaryLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox orderSummary = new VBox(5);
        for (OrderDetail detail : currentOrderDetails) {
            String itemText = String.format("%s x%d = %s",
                    detail.getProductName() != null ? detail.getProductName() : "Sản phẩm " + detail.getProductId(),
                    detail.getQuantity(),
                    formatAmount(detail.getTotalPrice()));
            Label itemLabel = new Label(itemText);
            itemLabel.setStyle("-fx-font-size: 12px;");
            orderSummary.getChildren().add(itemLabel);
        }

        Separator separator = new Separator();

        Label totalLabel = new Label("Tổng tiền: " + formatAmount(currentOrder.getFinalAmount()));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #E67E22;");

        Label methodLabel = new Label("Phương thức thanh toán:");
        methodLabel.setStyle("-fx-font-weight: bold;");
        ComboBox<String> methodCombo = new ComboBox<>();
        methodCombo.getItems().addAll("cash", "card", "momo", "vnpay", "zalopay");
        methodCombo.setValue("cash");
        methodCombo.setMaxWidth(Double.MAX_VALUE);

        Label amountLabel = new Label("Số tiền khách đưa:");
        amountLabel.setStyle("-fx-font-weight: bold;");
        TextField amountField = new TextField();
        amountField.setText(String.valueOf(currentOrder.getFinalAmount()));
        amountField.setMaxWidth(Double.MAX_VALUE);

        content.getChildren().addAll(
                orderSummaryLabel, orderSummary, separator, totalLabel,
                methodLabel, methodCombo, amountLabel, amountField
        );

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // ✅ Enhanced result processing
        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, Object> result = new HashMap<>();
                result.put("method", methodCombo.getValue());
                try {
                    result.put("amount", Double.parseDouble(amountField.getText()));
                    return result;
                } catch (NumberFormatException e) {
                    showError("Số tiền không hợp lệ");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(result -> {
            String method = (String) result.get("method");
            Double amount = (Double) result.get("amount");
            if (method != null && amount != null) {
                processPayment(method, amount);
            }
        });
    }

    /**
     * ✅ COMPLETE: Process payment với OrderService
     */
    private void processPayment(String method, double amountReceived) {
        Task<Boolean> paymentTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return orderService.processPayment(currentOrder, method, amountReceived);
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    Boolean success = getValue();
                    if (success) {
                        double change = orderService.calculateChange(currentOrder, amountReceived);
                        String message = "Thanh toán thành công!";
                        if (change > 0) {
                            message += "\nTiền thối: " + formatAmount(change);
                        }
                        showInfo(message);

                        // ✅ Complete the order and clear
                        completeOrderAndReset();
                    } else {
                        showError("Không thể xử lý thanh toán");
                    }
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    showError("Lỗi thanh toán: " + getException().getMessage());
                });
            }
        };

        new Thread(paymentTask).start();
    }

    /**
     * ✅ NEW: Complete order and reset state
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
                    currentTableId = -1;

                    // ✅ Update UI
                    updateTableInfo();
                    updateOrderDisplay();

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
     * ✅ ENHANCED: Handle clear order action
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
     * ✅ NEW: Clear order completely
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
                    // ✅ Reset state
                    currentOrder = null;
                    currentOrderDetails.clear();

                    // ✅ Update UI
                    updateOrderDisplay();

                    showInfo("Đã xóa đơn hàng");
                    System.out.println("✅ Order cleared successfully");
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
}
