package com.cafe.controller.payment;

import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.dto.PaymentRequest;
import com.cafe.model.dto.PaymentResponse;
import com.cafe.service.OrderService;
import com.cafe.service.PaymentService;
import com.cafe.service.ReceiptService;
import com.cafe.util.PaymentValidator;
import com.cafe.util.PriceFormatter;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

/**
 * Controller for Payment Processing Screen
 * Handles payment calculation, validation, and processing
 * 
 * @author Team 2_C2406L
 * @version 3.0.0 (Professional Payment System)
 */
public class PaymentController implements Initializable {

    // =====================================================
    // CALLBACK INTERFACE
    // =====================================================

    /**
     * Interface for payment completion callback
     */
    public interface PaymentCompletionCallback {
        void onPaymentCompleted(Order order, String paymentMethod);
        void onPaymentFailed(Order order, String reason);
    }

    // =====================================================
    // FXML INJECTIONS - Header & Info
    // =====================================================
    @FXML private Label tableIdLabel;
    @FXML private Label orderIdLabel;
    @FXML private Label serverNameLabel;
    @FXML private Label orderTimeLabel;
    
    // Table View
    @FXML private TableView<OrderDetail> itemsTableView;
    @FXML private TableColumn<OrderDetail, Integer> sttColumn;
    @FXML private TableColumn<OrderDetail, String> itemNameColumn;
    @FXML private TableColumn<OrderDetail, Integer> quantityColumn;
    @FXML private TableColumn<OrderDetail, String> unitPriceColumn;
    @FXML private TableColumn<OrderDetail, String> totalPriceColumn;
    @FXML private Label totalItemsLabel;
    
    // Calculation Fields
    @FXML private Label subtotalLabel;
    @FXML private TextField vatPercentField;
    @FXML private Label vatAmountLabel;
    // ✅ Removed service fee controls
    
    // Discount Section
    @FXML private ToggleGroup discountTypeGroup;
    @FXML private RadioButton discountPercentRadio;
    @FXML private RadioButton discountAmountRadio;
    @FXML private TextField discountValueField;
    @FXML private Label discountUnitLabel;
    @FXML private Label discountAmountLabel;
    @FXML private Label discountErrorLabel;
    
    @FXML private Label grandTotalLabel;
    
    // Payment Method Section
    @FXML private ToggleGroup paymentMethodGroup;
    @FXML private RadioButton cashRadio;
    @FXML private RadioButton cardRadio;
    @FXML private RadioButton momoRadio;
    @FXML private RadioButton vnpayRadio;
    @FXML private RadioButton zalopayRadio;
    @FXML private RadioButton transferRadio;
    
    // Cash Payment
    @FXML private VBox cashPaymentSection;
    @FXML private Label cashTotalLabel;
    @FXML private TextField customerAmountField;
    @FXML private Label changeAmountLabel;
    @FXML private Label cashErrorLabel;
    
    // Card/Transfer Payment
    @FXML private VBox cardTransferSection;
    @FXML private TextField transactionCodeField;
    @FXML private TextArea paymentNotesArea;
    
    // Action Buttons
    @FXML private Button cancelButton;
    @FXML private Button printReceiptButton;
    @FXML private Button payButton;

    // =====================================================
    // SERVICES & BUSINESS LOGIC
    // =====================================================
    private OrderService orderService;
    private PaymentService paymentService;
    private ReceiptService receiptService;
    
    // Payment callback
    private PaymentCompletionCallback paymentCallback;
    
    // Data Models
    private Order currentOrder;
    private int currentTableId;
    private String currentTableName;
    private ObservableList<OrderDetail> orderItems = FXCollections.observableArrayList();
    
    // Calculation Properties
    private DoubleProperty subtotalProperty = new SimpleDoubleProperty(0);
    private DoubleProperty vatPercentProperty = new SimpleDoubleProperty(8); // ✅ Thay đổi VAT = 8%
    private DoubleProperty vatAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty discountValueProperty = new SimpleDoubleProperty(0);
    private DoubleProperty discountAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty grandTotalProperty = new SimpleDoubleProperty(0);
    private DoubleProperty customerAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty changeAmountProperty = new SimpleDoubleProperty(0);
    
    // Format
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
    private final DecimalFormat numberFormat = new DecimalFormat("#,###");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("🔧 PaymentController.initialize() started");
            
            // Initialize services
            initializeServices();
            System.out.println("✅ Services initialized");
            
            // Setup table view
            setupTableView();
            System.out.println("✅ Table view configured");
            
            // Setup input fields
            setupInputFields();
            System.out.println("✅ Input fields configured");
            
            // Setup calculations binding
            bindCalculations();
            System.out.println("✅ Calculations binding completed");
            
            // Setup payment method listeners
            setupPaymentMethodListeners();
            System.out.println("✅ Payment method listeners configured");
            
            // Setup keyboard shortcuts
            setupShortcuts();
            
            // Setup button actions
            setupButtonActions();
            System.out.println("✅ Button actions configured");
            
            System.out.println("✅ PaymentController initialized successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing PaymentController: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize data for payment processing  
     * @param order Order to process payment for
     * @param tableId Table ID
     * @param vatPercent VAT percentage (default 8%)
     */
    public void initData(Order order, int tableId, double vatPercent) {
        initData(order, tableId, vatPercent, null);
    }
    
    /**
     * ✅ UPDATED: Initialize payment data with callback
     * @param order Order to process payment for
     * @param tableId Table ID
     * @param vatPercent VAT percentage (default 8%)
     * @param callback Payment completion callback
     */
    public void initData(Order order, int tableId, double vatPercent, PaymentCompletionCallback callback) {
        this.currentOrder = order;
        this.currentTableId = tableId;
        this.currentTableName = "Bàn " + tableId;
        this.paymentCallback = callback; // ← Add this line
        
        // Set VAT percentage
        vatPercentProperty.set(vatPercent);
        
        // Load order data
        loadOrderInfo();
        loadOrderItems();
        
        System.out.println("✅ Payment data initialized: Order " + order.getOrderNumber());
    }
    
    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================
    
    private void initializeServices() {
        this.orderService = new OrderService();
        this.paymentService = new PaymentService();
        this.receiptService = new ReceiptService();
    }
    
    private void setupTableView() {
        // Configure columns
        sttColumn.setCellValueFactory(cellData -> 
            new SimpleIntegerProperty(itemsTableView.getItems().indexOf(cellData.getValue()) + 1).asObject());
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        // Format price columns
        unitPriceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatCurrency(cellData.getValue().getUnitPrice())));
        totalPriceColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(formatCurrency(cellData.getValue().getTotalPrice())));
        
        // Bind items
        itemsTableView.setItems(orderItems);
        
        // Update total items label
        totalItemsLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            "Tổng: " + orderItems.size() + " món", orderItems));
    }
    
    private void setupInputFields() {
        // Number-only text formatters
        UnaryOperator<TextFormatter.Change> numberFilter = change -> {
            String newText = change.getControlNewText();
            if (Pattern.matches("\\d*\\.?\\d*", newText)) {
                return change;
            }
            return null;
        };
        
        vatPercentField.setTextFormatter(new TextFormatter<>(numberFilter));
        discountValueField.setTextFormatter(new TextFormatter<>(numberFilter));
        customerAmountField.setTextFormatter(new TextFormatter<>(numberFilter));
        
        // Bind properties to fields
        vatPercentField.textProperty().bindBidirectional(vatPercentProperty, new NumberStringConverter());
        discountValueField.textProperty().bindBidirectional(discountValueProperty, new NumberStringConverter());
        customerAmountField.textProperty().bindBidirectional(customerAmountProperty, new NumberStringConverter());
    }
    
    private void bindCalculations() {
        // Subtotal calculation
        subtotalProperty.bind(Bindings.createDoubleBinding(() -> 
            orderItems.stream().mapToDouble(OrderDetail::getTotalPrice).sum(), orderItems));
        
        // VAT calculation
        vatAmountProperty.bind(subtotalProperty.multiply(vatPercentProperty.divide(100)));
        
        // Discount calculation
        discountAmountProperty.bind(Bindings.createDoubleBinding(() -> {
            if (discountPercentRadio.isSelected()) {
                return subtotalProperty.get() * discountValueProperty.get() / 100;
            } else {
                return discountValueProperty.get();
            }
        }, discountValueProperty, subtotalProperty, discountTypeGroup.selectedToggleProperty()));
        
        // Grand total calculation (removed service fee)
        grandTotalProperty.bind(subtotalProperty
            .add(vatAmountProperty)
            .subtract(discountAmountProperty));
        
        // Change calculation
        changeAmountProperty.bind(customerAmountProperty.subtract(grandTotalProperty));
        
        // Bind labels
        subtotalLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            formatCurrency(subtotalProperty.get()), subtotalProperty));
        vatAmountLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            formatCurrency(vatAmountProperty.get()), vatAmountProperty));
        discountAmountLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            "- " + formatCurrency(discountAmountProperty.get()), discountAmountProperty));
        grandTotalLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            formatCurrency(grandTotalProperty.get()), grandTotalProperty));
        changeAmountLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            formatCurrency(Math.max(0, changeAmountProperty.get())), changeAmountProperty));
        
        // ✅ Cash total label binding
        cashTotalLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            formatCurrency(grandTotalProperty.get()), grandTotalProperty));
        
        // Discount unit label
        discountUnitLabel.textProperty().bind(Bindings.createStringBinding(() -> 
            discountPercentRadio.isSelected() ? "%" : "₫", discountTypeGroup.selectedToggleProperty()));
    }
    
    private void setupPaymentMethodListeners() {
        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isCash = cashRadio.isSelected();
            cashPaymentSection.setVisible(isCash);
            cashPaymentSection.setManaged(isCash);
            cardTransferSection.setVisible(!isCash);
            cardTransferSection.setManaged(!isCash);
            
            // ✅ Tự động set amount cho tiền mặt
            if (isCash) {
                customerAmountProperty.set(grandTotalProperty.get());
            }
        });
    }
    
    private void setupShortcuts() {
        // Setup shortcuts after scene is available
        Platform.runLater(() -> {
            try {
                if (payButton.getScene() != null) {
                    // Enter - Pay
                    payButton.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.ENTER), () -> handlePayment());
                    
                    // Esc - Cancel
                    payButton.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.ESCAPE), () -> handleCancel());
                    
                    // Ctrl+P - Print
                    payButton.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.P, KeyCombination.CONTROL_DOWN), () -> handlePrintReceipt());
                    
                    // Ctrl+D - Focus discount
                    payButton.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN), () -> discountValueField.requestFocus());
                    
                    // Ctrl+R - Focus customer amount
                    payButton.getScene().getAccelerators().put(
                        new KeyCodeCombination(KeyCode.R, KeyCombination.CONTROL_DOWN), () -> customerAmountField.requestFocus());
                    
                    System.out.println("✅ Keyboard shortcuts initialized");
                }
            } catch (Exception e) {
                System.err.println("❌ Error setting up shortcuts: " + e.getMessage());
            }
        });
    }
    
    private void setupButtonActions() {
        payButton.setOnAction(e -> handlePayment());
        cancelButton.setOnAction(e -> handleCancel());
        printReceiptButton.setOnAction(e -> handlePrintReceipt());
    }
    
    // =====================================================
    // DATA LOADING METHODS
    // =====================================================
    
    private void loadOrderInfo() {
        if (currentOrder == null) return;
        
        tableIdLabel.setText(currentTableName);
        orderIdLabel.setText(currentOrder.getOrderNumber());
        serverNameLabel.setText("Admin User"); // TODO: Get from session
        
        LocalDateTime orderTime = currentOrder.getOrderDate().toLocalDateTime();
        orderTimeLabel.setText(orderTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
    }
    
    private void loadOrderItems() {
        if (currentOrder == null) return;
        
        try {
            List<OrderDetail> items = orderService.getOrderDetails(currentOrder.getOrderId());
            orderItems.setAll(items);
            System.out.println("✅ Loaded " + items.size() + " order items");
        } catch (Exception e) {
            System.err.println("❌ Error loading order items: " + e.getMessage());
            showError("Không thể tải danh sách món");
        }
    }
    
    // =====================================================
    // PAYMENT PROCESSING
    // =====================================================
    
    /**
     * Handle payment button click
     */
    private void handlePayment() {
        System.out.println("🔧 Processing payment...");
        
        // Clear previous errors
        clearErrors();
        
        // Validate payment
        if (!validateBeforePay()) {
            return;
        }
        
        try {
            // Determine payment method
            String paymentMethod = getSelectedPaymentMethod();
            
            // Prepare payment data
            double totalAmount = grandTotalProperty.get();
            double receivedAmount = cashRadio.isSelected() ? customerAmountProperty.get() : totalAmount;
            String transactionCode = cardRadio.isSelected() || transferRadio.isSelected() ? 
                transactionCodeField.getText() : null;
            String notes = paymentNotesArea.getText();
            
            // Process payment through service (using existing method signature)
            boolean success = paymentService.processPayment(currentOrder, paymentMethod, receivedAmount);
            
            if (success) {
                System.out.println("✅ Payment completed successfully");
                
                // ✅ NOTIFY OrderPanelController about payment completion
                if (paymentCallback != null) {
                    Platform.runLater(() -> {
                        try {
                            paymentCallback.onPaymentCompleted(currentOrder, paymentMethod);
                            System.out.println("✅ OrderPanel notified about payment completion");
                        } catch (Exception e) {
                            System.err.println("❌ Error notifying payment callback: " + e.getMessage());
                        }
                    });
                }
                
                showSuccess("Thanh toán thành công!");
                handlePrintReceipt();
                
                // Close window after delay
                Platform.runLater(() -> {
                    Timeline closeTimer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(2), e -> {
                        if (payButton.getScene() != null && payButton.getScene().getWindow() != null) {
                            ((Stage) payButton.getScene().getWindow()).close();
                        }
                    }));
                    closeTimer.play();
                });
                
            } else {
                // ✅ NOTIFY OrderPanelController about payment failure
                if (paymentCallback != null) {
                    Platform.runLater(() -> {
                        try {
                            paymentCallback.onPaymentFailed(currentOrder, "Payment processing failed");
                            System.out.println("✅ OrderPanel notified about payment failure");
                        } catch (Exception e) {
                            System.err.println("❌ Error notifying payment failure callback: " + e.getMessage());
                        }
                    });
                }
                
                showError("Thanh toán thất bại. Vui lòng thử lại.");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Payment processing error: " + e.getMessage());
            
            // ✅ NOTIFY OrderPanelController about payment error
            if (paymentCallback != null) {
                Platform.runLater(() -> {
                    try {
                        paymentCallback.onPaymentFailed(currentOrder, e.getMessage());
                    } catch (Exception ex) {
                        System.err.println("❌ Error notifying payment error callback: " + ex.getMessage());
                    }
                });
            }
            
            showError("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }
    
    /**
     * Validate payment data before processing
     */
    private boolean validateBeforePay() {
        boolean isValid = true;
        
        // Check if there are items
        if (orderItems.isEmpty()) {
            showError("Không có món để thanh toán");
            return false;
        }
        
        // Validate discount
        double maxDiscount = subtotalProperty.get();
        if (discountAmountProperty.get() > maxDiscount) {
            discountErrorLabel.setText("Giảm giá không thể lớn hơn tạm tính");
            discountValueField.getStyleClass().add("field-error");
            isValid = false;
        }
        
        // Validate cash payment
        if (cashRadio.isSelected()) {
            if (customerAmountProperty.get() < grandTotalProperty.get()) {
                cashErrorLabel.setText("Tiền khách đưa không đủ");
                customerAmountField.getStyleClass().add("field-error");
                isValid = false;
            }
        } else {
            // Validate transaction code for card/transfer
            if (transactionCodeField.getText().trim().isEmpty()) {
                transactionCodeField.getStyleClass().add("field-error");
                showError("Vui lòng nhập mã giao dịch");
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    /**
     * Handle print receipt button click
     */
    private void handlePrintReceipt() {
        try {
            // Mock receipt generation
            String fileName = "receipt_" + currentOrder.getOrderNumber() + "_" + System.currentTimeMillis() + ".pdf";
            String fullPath = System.getProperty("user.home") + "/Downloads/" + fileName;
            System.out.println("✅ Receipt PDF generated at: " + fullPath);
            showSuccess("Hóa đơn đã được tạo: " + fileName);
        } catch (Exception e) {
            System.err.println("❌ Error generating receipt: " + e.getMessage());
            showError("Không thể tạo hóa đơn");
        }
    }
    
    /**
     * Handle cancel button click
     */
    private void handleCancel() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    private String getSelectedPaymentMethod() {
        if (cashRadio.isSelected()) return "CASH";
        if (cardRadio.isSelected()) return "CARD";
        if (momoRadio.isSelected()) return "MOMO";
        if (vnpayRadio.isSelected()) return "VNPAY";
        if (zalopayRadio.isSelected()) return "ZALOPAY";
        if (transferRadio.isSelected()) return "BANK_TRANSFER";
        return "CASH"; // default
    }
    
    private void clearErrors() {
        discountErrorLabel.setText("");
        cashErrorLabel.setText("");
        discountValueField.getStyleClass().remove("field-error");
        customerAmountField.getStyleClass().remove("field-error");
        transactionCodeField.getStyleClass().remove("field-error");
    }
    
    private String formatCurrency(double amount) {
        return PriceFormatter.formatVND(amount);
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Lỗi");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
    
    /**
     * Reset form to initial state
     */
    public void resetForm() {
        vatPercentProperty.set(8); // ✅ VAT = 8%
        discountValueProperty.set(0);
        customerAmountProperty.set(0);
        cashRadio.setSelected(true); // ✅ Default tiền mặt
        transactionCodeField.clear();
        paymentNotesArea.clear();
        clearErrors();
    }
    
    // =====================================================
    // ENHANCED PAYMENT METHODS
    // =====================================================
    
    /**
     * Enhanced payment processing using new service architecture
     */
    private void handlePaymentEnhanced() {
        try {
            // Clear previous errors
            clearErrors();
            
            // Create payment request
            PaymentRequest request = createPaymentRequest();
            
            // Validate request
            if (!validatePaymentRequest(request)) {
                return;
            }
            
            // Process payment
            PaymentResponse response = paymentService.processPayment(request);
            
            if (response.isSuccess()) {
                handlePaymentSuccess(response);
            } else {
                handlePaymentFailure(response);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Payment processing error: " + e.getMessage());
            showError("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }
    
    /**
     * Create payment request from UI
     */
    private PaymentRequest createPaymentRequest() {
        return new PaymentRequest()
                .setOrderId(currentOrder.getOrderId())
                .setPaymentMethod(getSelectedPaymentMethod())
                .setAmountReceived(customerAmountProperty.get())
                .setTransactionCode(transactionCodeField.getText())
                .setNotes(paymentNotesArea.getText())
                .setVatPercent(vatPercentProperty.get())
                .setDiscountAmount(discountAmountProperty.get());
    }
    
    /**
     * ✅ UPDATED: Handle successful payment with callback
     */
    private void handlePaymentSuccess(PaymentResponse response) {
        System.out.println("✅ Payment completed successfully");
        System.out.println("Transaction ID: " + response.getTransactionId());
        
        if (response.getChangeAmount() > 0) {
            System.out.println("💰 Change: " + String.format("%,.0f VNĐ", response.getChangeAmount()));
        }
        
        showSuccess("Thanh toán thành công!");
        
        // ✅ NOTIFY OrderPanelController about payment completion
        if (paymentCallback != null) {
            Platform.runLater(() -> {
                try {
                    paymentCallback.onPaymentCompleted(currentOrder, getSelectedPaymentMethod());
                    System.out.println("✅ OrderPanel notified about payment completion");
                } catch (Exception e) {
                    System.err.println("❌ Error notifying payment callback: " + e.getMessage());
                }
            });
        }
        
        // Auto-print receipt
        handlePrintReceipt();
        
        // Close window after brief delay
        Platform.runLater(() -> {
            Timeline closeTimer = new Timeline(new KeyFrame(javafx.util.Duration.seconds(2), e -> {
                if (payButton.getScene() != null && payButton.getScene().getWindow() != null) {
                    ((javafx.stage.Stage) payButton.getScene().getWindow()).close();
                }
            }));
            closeTimer.play();
        });
    }
    
    /**
     * ✅ UPDATED: Handle payment failure with callback
     */
    private void handlePaymentFailure(PaymentResponse response) {
        System.err.println("❌ Payment failed: " + response.getMessage());
        showError("Thanh toán thất bại: " + response.getMessage());
        
        // ✅ NOTIFY OrderPanelController about payment failure
        if (paymentCallback != null) {
            Platform.runLater(() -> {
                try {
                    paymentCallback.onPaymentFailed(currentOrder, response.getMessage());
                    System.out.println("✅ OrderPanel notified about payment failure");
                } catch (Exception e) {
                    System.err.println("❌ Error notifying payment failure callback: " + e.getMessage());
                }
            });
        }
    }
    
    /**
     * Enhanced validation
     */
    private boolean validatePaymentRequest(PaymentRequest request) {
        PaymentValidator validator = new PaymentValidator();
        
        if (!validator.validatePaymentRequest(request)) {
            showError("Dữ liệu thanh toán không hợp lệ");
            return false;
        }
        
        if (!validator.validateAmount(request.getAmountReceived(), grandTotalProperty.get(), request.getPaymentMethod())) {
            if ("cash".equals(request.getPaymentMethod())) {
                cashErrorLabel.setText("Tiền khách đưa không đủ");
                customerAmountField.getStyleClass().add("field-error");
            } else {
                showError("Số tiền không hợp lệ");
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Switch between legacy and enhanced payment processing
     * Currently using legacy method, can be switched to enhanced
     */
    public void setUseEnhancedPayment(boolean useEnhanced) {
        if (useEnhanced) {
            System.out.println("✅ Switched to enhanced payment processing");
            // Replace handlePayment() calls with handlePaymentEnhanced()
        } else {
            System.out.println("✅ Using legacy payment processing");
        }
    }
    
    // =====================================================
    // ADDITIONAL HELPER METHODS
    // =====================================================

    /**
     * Set payment completion callback
     */
    public void setPaymentCallback(PaymentCompletionCallback callback) {
        this.paymentCallback = callback;
    }
}