package com.cafe.controller.payment;

import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Promotion;
import com.cafe.model.dto.PaymentRequest;
import com.cafe.model.dto.PaymentResponse;
import com.cafe.service.OrderService;
import com.cafe.service.PaymentService;
import com.cafe.service.ReceiptService;
import com.cafe.service.PromotionService;
import com.cafe.service.QRCodeService;
import com.cafe.service.CustomerService;
import com.cafe.model.entity.Customer;
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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.util.converter.NumberStringConverter;
import javafx.fxml.FXMLLoader;
import java.util.Optional;

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
    
    // ✅ ENHANCED: Promotion Section (Replace manual discount)
    @FXML private ComboBox<Promotion> promotionComboBox;
    @FXML private Button applyPromotionButton;
    @FXML private Label appliedPromotionLabel;
    @FXML private Label discountAmountLabel;
    @FXML private HBox discountRow;
    
    // Legacy discount section (keep for backward compatibility)
    @FXML private ToggleGroup discountTypeGroup;
    @FXML private RadioButton discountPercentRadio;
    @FXML private RadioButton discountAmountRadio;
    @FXML private TextField discountValueField;
    @FXML private Label discountUnitLabel;
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
    
    // ✅ ENHANCED: Cash Payment (Auto-fill)
    @FXML private VBox cashPaymentSection;
    @FXML private Label cashTotalLabel;
    @FXML private TextField customerAmountField; // Read-only for cash
    @FXML private Label changeAmountLabel;
    
    // ✅ NEW: Card Payment Section
    @FXML private VBox cardPaymentSection;
    // @FXML private TextField transactionCodeField; // Removed for educational project
    
    // ✅ ENHANCED: QR Code Section
    @FXML private VBox qrCodeSection;
    @FXML private VBox qrCodeContainer;
    @FXML private ImageView qrCodeImageView;
    @FXML private Label qrCodeInstructionLabel;
    @FXML private TextField qrTransactionCodeField;
    
    // Legacy fields (keep for compatibility)
    @FXML private Label cashErrorLabel;
    @FXML private VBox cardTransferSection;
    @FXML private TextArea paymentNotesArea;
    
    // Customer Information Section
    @FXML private TextField customerNameField;
    @FXML private ComboBox<String> customerPhoneField;
    
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
    private PromotionService promotionService; // ✅ NEW
    private QRCodeService qrCodeService; // ✅ NEW
    private CustomerService customerService; // ✅ NEW
    
    // Payment callback
    private PaymentCompletionCallback paymentCallback;
    
    // Data Models
    private Order currentOrder;
    private int currentTableId;
    private String currentTableName;
    private ObservableList<OrderDetail> orderItems = FXCollections.observableArrayList();
    private ObservableList<Promotion> availablePromotions = FXCollections.observableArrayList(); // ✅ NEW
    private ObservableList<String> phoneSuggestions = FXCollections.observableArrayList(); // ✅ NEW
    
    // Calculation Properties
    private DoubleProperty subtotalProperty = new SimpleDoubleProperty(0);
    private DoubleProperty vatPercentProperty = new SimpleDoubleProperty(8); // ✅ Thay đổi VAT = 8%
    private DoubleProperty vatAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty discountValueProperty = new SimpleDoubleProperty(0);
    private DoubleProperty discountAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty grandTotalProperty = new SimpleDoubleProperty(0);
    private DoubleProperty customerAmountProperty = new SimpleDoubleProperty(0);
    private DoubleProperty changeAmountProperty = new SimpleDoubleProperty(0);
    
    // ✅ NEW: Selected promotion
    private ObjectProperty<Promotion> selectedPromotionProperty = new SimpleObjectProperty<>();
    
    // Customer Information Properties
    private StringProperty customerNameProperty = new SimpleStringProperty("");
    private StringProperty customerPhoneProperty = new SimpleStringProperty("");
    
    // Format
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
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
            
            // ✅ NEW: Setup promotion section
            setupPromotionSection();
            System.out.println("✅ Promotion section configured");
            
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
     * ✅ ENHANCED: Initialize payment data with callback and auto-fill setup
     * @param order Order to process payment for
     * @param tableId Table ID
     * @param vatPercent VAT percentage (default 8%)
     * @param callback Payment completion callback
     */
    public void initData(Order order, int tableId, double vatPercent, PaymentCompletionCallback callback) {
        this.currentOrder = order;
        this.currentTableId = tableId;
        this.currentTableName = "Bàn " + tableId;
        this.paymentCallback = callback;
        
        // ✅ FIXED VAT: Set to 8% and disable editing
        vatPercentProperty.set(8.0);
        if (vatPercentField != null) {
            vatPercentField.setText("8");
            vatPercentField.setDisable(true); // ✅ Cannot change VAT
        }
        
        // Load order data
        loadOrderInfo();
        loadOrderItems();
        
        // ✅ Setup cash auto-fill after data is loaded
        setupCashAutoFill();
        
        // Initialize customer information
        initializeCustomerInfo();
        
        System.out.println("✅ Enhanced payment data initialized for order: " + order.getOrderNumber());
    }
    
    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================
    
    private void initializeServices() {
        this.orderService = new OrderService();
        this.paymentService = new PaymentService();
        this.receiptService = new ReceiptService();
        this.promotionService = new PromotionService(); // ✅ NEW
        this.qrCodeService = new QRCodeService(); // ✅ NEW
        this.customerService = new CustomerService(); // ✅ NEW
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
    
    // ✅ NEW: Setup promotion section
    private void setupPromotionSection() {
        // Load available promotions
        loadAvailablePromotions();
        
        // Setup promotion combo box
        promotionComboBox.setItems(availablePromotions);
        promotionComboBox.setPromptText("Chọn khuyến mãi...");
        
        // Custom cell factory for promotion display
        promotionComboBox.setCellFactory(listView -> new ListCell<Promotion>() {
            @Override
            protected void updateItem(Promotion promotion, boolean empty) {
                super.updateItem(promotion, empty);
                if (empty || promotion == null) {
                    setText(null);
                } else {
                    setText(promotion.getPromotionName() + " - " + promotion.getFormattedDiscountValue());
                }
            }
        });
        
        promotionComboBox.setButtonCell(new ListCell<Promotion>() {
            @Override
            protected void updateItem(Promotion promotion, boolean empty) {
                super.updateItem(promotion, empty);
                if (empty || promotion == null) {
                    setText("Chọn khuyến mãi...");
                } else {
                    setText(promotion.getPromotionName() + " - " + promotion.getFormattedDiscountValue());
                }
            }
        });
        
        // Apply promotion button action
        applyPromotionButton.setOnAction(e -> applySelectedPromotion());
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
        
        // Setup customer information fields
        setupCustomerFields();
    }
    
    private void setupCustomerFields() {
        // Bind customer properties to fields
        customerNameField.textProperty().bindBidirectional(customerNameProperty);
        
        // Setup phone ComboBox
        customerPhoneField.setItems(phoneSuggestions);
        customerPhoneField.setEditable(true);
        
        // Bind phone property to ComboBox value
        customerPhoneField.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                customerPhoneProperty.set(newVal.trim());
                searchCustomerByPhone(newVal.trim());
            }
        });
        
        // Setup phone field listener for auto-fill customer name and auto-save new customer
        customerPhoneField.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty()) {
                customerPhoneProperty.set(newVal.trim());
                searchCustomerByPhone(newVal.trim());
            } else {
                customerPhoneProperty.set("");
                customerNameProperty.set("");
                phoneSuggestions.clear();
            }
        });
        
        // Setup name field listener for auto-save when both phone and name are provided
        customerNameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.trim().isEmpty() && 
                customerPhoneProperty.get() != null && !customerPhoneProperty.get().trim().isEmpty()) {
                // Auto-save customer if both phone and name are provided
                saveCustomerIfNew();
            }
        });
    }
    
    private void clearCustomerFields() {
        customerNameProperty.set("");
        customerPhoneProperty.set("");
        phoneSuggestions.clear();
        customerPhoneField.setValue(null);
    }
    
    /**
     * Auto-save customer if phone number is new
     */
    private void saveCustomerIfNew() {
        String phone = customerPhoneProperty.get().trim();
        String name = customerNameProperty.get().trim();
        
        if (phone.isEmpty() || name.isEmpty()) {
            return;
        }
        
        try {
            // Check if customer already exists
            if (!isExistingCustomer(phone)) {
                // Save new customer
                saveNewCustomer(name, phone);
                System.out.println("✅ Auto-saved new customer: " + name + " - " + phone);
            }
        } catch (Exception e) {
            System.err.println("❌ Error auto-saving customer: " + e.getMessage());
        }
    }
    
    /**
     * Check if customer exists by phone
     */
    private boolean isExistingCustomer(String phone) {
        try {
            return customerService.findCustomerByPhone(phone).isPresent();
        } catch (Exception e) {
            System.err.println("❌ Error checking existing customer: " + e.getMessage());
            return false; // Default to false to allow creation
        }
    }
    
    /**
     * Save new customer to database
     */
    private void saveNewCustomer(String name, String phone) {
        try {
            Customer newCustomer = new Customer();
            newCustomer.setFullName(name);
            newCustomer.setPhone(phone);
            newCustomer.setEmail(null); // Set to null instead of empty string to avoid duplicate key error
            newCustomer.setAddress(null); // Set to null instead of empty string
            newCustomer.setLoyaltyPoints(0); // Start with 0 points
            newCustomer.setTotalSpent(0.0); // Start with 0 spent
            newCustomer.setActive(true); // Active by default
            
            boolean success = customerService.createCustomer(newCustomer);
            if (success) {
                System.out.println("✅ Successfully saved new customer: " + name + " - " + phone);
            } else {
                System.err.println("❌ Failed to save new customer: " + name + " - " + phone);
            }
        } catch (Exception e) {
            System.err.println("❌ Error saving new customer: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void searchCustomerByPhone(String phone) {
        try {
            System.out.println("🔍 Searching customer by phone: " + phone);
            
            if (phone.length() >= 3) { // Start searching from 3 digits
                // Search for customers with similar phone numbers
                List<Customer> matchingCustomers = searchCustomersByPhonePattern(phone);
                
                if (!matchingCustomers.isEmpty()) {
                    // Update phone suggestions dropdown
                    List<String> phoneNumbers = matchingCustomers.stream()
                            .map(Customer::getPhone)
                            .collect(java.util.stream.Collectors.toList());
                    phoneSuggestions.setAll(phoneNumbers);
                    
                    if (matchingCustomers.size() == 1) {
                        // Exact match or single result - auto-fill
                        Customer customer = matchingCustomers.get(0);
                        customerNameProperty.set(customer.getFullName());
                        System.out.println("✅ Found existing customer: " + customer.getFullName());
                    } else {
                        // Multiple matches - show first one but indicate there are more
                        Customer customer = matchingCustomers.get(0);
                        customerNameProperty.set(customer.getFullName() + " (+" + (matchingCustomers.size() - 1) + " khác)");
                        System.out.println("✅ Found " + matchingCustomers.size() + " matching customers, showing: " + customer.getFullName());
                    }
                } else if (phone.length() >= 10) {
                    // No matches found, auto-generate placeholder name for new customer
                    phoneSuggestions.clear();
                    customerNameProperty.set("Khách hàng " + phone.substring(phone.length() - 4));
                    System.out.println("ℹ️ New customer detected, placeholder name set");
                } else {
                    // Clear name field and suggestions if phone is too short
                    customerNameProperty.set("");
                    phoneSuggestions.clear();
                }
            } else {
                // Clear name field and suggestions if phone is too short
                customerNameProperty.set("");
                phoneSuggestions.clear();
            }
        } catch (Exception e) {
            System.err.println("❌ Error searching customer by phone: " + e.getMessage());
            // Fallback to placeholder
            if (phone.length() >= 10) {
                customerNameProperty.set("Khách hàng " + phone.substring(phone.length() - 4));
            }
            phoneSuggestions.clear();
        }
    }
    
    /**
     * Search customers by phone pattern (partial match)
     */
    private List<Customer> searchCustomersByPhonePattern(String phonePattern) {
        try {
            List<Customer> allCustomers = customerService.getAllCustomers();
            return allCustomers.stream()
                    .filter(customer -> customer.getPhone() != null && 
                            customer.getPhone().contains(phonePattern))
                    .limit(5) // Limit to 5 results for performance
                    .collect(java.util.stream.Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Error searching customers by phone pattern: " + e.getMessage());
            return List.of();
        }
    }
    
    private void searchCustomer() {
        // TODO: Implement customer search functionality
        // This could open a dialog to search for existing customers
        System.out.println("🔍 Customer search functionality to be implemented");
    }
    
    private void initializeCustomerInfo() {
        // Clear customer fields
        clearCustomerFields();
        
        System.out.println("✅ Customer information initialized");
    }
    
    private void bindCalculations() {
        // Subtotal calculation
        subtotalProperty.bind(Bindings.createDoubleBinding(() -> 
            orderItems.stream().mapToDouble(OrderDetail::getTotalPrice).sum(), orderItems));
        
        // VAT calculation
        vatAmountProperty.bind(subtotalProperty.multiply(vatPercentProperty.divide(100)));
        
        // Discount calculation - simplified for promotion-based discounts
        // Note: discountAmountProperty is now set directly in promotion methods, not bound
        
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
        
        // Discount unit label - simplified for promotion-based discounts
        if (discountUnitLabel != null) {
            discountUnitLabel.setText("₫");
        }
    }
    
    private void setupPaymentMethodListeners() {
        // ✅ Setup ToggleGroups for RadioButtons
        if (paymentMethodGroup != null) {
            cashRadio.setToggleGroup(paymentMethodGroup);
            cardRadio.setToggleGroup(paymentMethodGroup);
            momoRadio.setToggleGroup(paymentMethodGroup);
            vnpayRadio.setToggleGroup(paymentMethodGroup);
            zalopayRadio.setToggleGroup(paymentMethodGroup);
            transferRadio.setToggleGroup(paymentMethodGroup);
            cashRadio.setSelected(true); // Default selection
        }
        
        // Legacy discount controls - disabled for promotion-based system
        if (discountPercentRadio != null) discountPercentRadio.setVisible(false);
        if (discountAmountRadio != null) discountAmountRadio.setVisible(false);
        if (discountValueField != null) discountValueField.setVisible(false);
        
        paymentMethodGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            boolean isCash = cashRadio.isSelected();
            
            // Legacy compatibility
            if (cashPaymentSection != null) {
                cashPaymentSection.setVisible(isCash);
                cashPaymentSection.setManaged(isCash);
            }
            if (cardTransferSection != null) {
                cardTransferSection.setVisible(!isCash);
                cardTransferSection.setManaged(!isCash);
            }
            
            // ✅ Enhanced payment sections handling
            updatePaymentSections();
            
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
            
            // Check if it's an electronic payment method
            boolean isElectronic = isElectronicPaymentMethod(paymentMethod);
            
            if (isElectronic) {
                // Show QR code popup for electronic payments
                showQRCodePopup();
                return; // Don't process payment yet, wait for user to complete in popup
            }
            
            // Prepare payment data for non-electronic methods
            double totalAmount = grandTotalProperty.get();
            double receivedAmount = cashRadio.isSelected() ? customerAmountProperty.get() : totalAmount;
            // For educational project - no transaction code needed
            String transactionCode = null;
            
            // Prepare customer information notes (optional)
            StringBuilder customerNotes = new StringBuilder();
            if (!customerNameProperty.get().trim().isEmpty()) {
                customerNotes.append("Khách hàng: ").append(customerNameProperty.get()).append("\n");
            }
            if (!customerPhoneProperty.get().trim().isEmpty()) {
                customerNotes.append("SĐT: ").append(customerPhoneProperty.get()).append("\n");
            }
            if (paymentNotesArea != null && !paymentNotesArea.getText().trim().isEmpty()) {
                customerNotes.append("Ghi chú thanh toán: ").append(paymentNotesArea.getText());
            }
            
            String notes = customerNotes.toString();
            
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
                
                // Note: Success message is shown by OrderPanelController
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
        
        // Customer information is optional - no validation required
        // Auto-save customer if both phone and name are provided
        if (!customerNameProperty.get().trim().isEmpty() && !customerPhoneProperty.get().trim().isEmpty()) {
            saveCustomerIfNew();
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
            // For educational project - no transaction code validation needed
            // All payment methods (CARD, MOMO, VNPAY, ZALOPAY, BANK_TRANSFER) work without transaction code
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
            // Note: Receipt generation is logged to console
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
        
        // Clear customer field errors
        customerNameField.getStyleClass().remove("field-error");
        customerPhoneField.getStyleClass().remove("field-error");
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
        // transactionCodeField.clear(); // Removed for educational project
        paymentNotesArea.clear();
        
        // Reset customer information
        clearCustomerFields();
        
        clearErrors();
    }
    
    // =====================================================
    // ENHANCED PAYMENT METHODS
    // =====================================================
    

    
    /**
     * Create payment request from UI
     */
    private PaymentRequest createPaymentRequest() {
        return new PaymentRequest()
                .setOrderId(currentOrder.getOrderId())
                .setPaymentMethod(getSelectedPaymentMethod())
                .setAmountReceived(customerAmountProperty.get())
                .setTransactionCode(null) // No transaction code for educational project
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
        
        // Note: Success message is shown by OrderPanelController
        
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
    
    // =====================================================
    // PROMOTION METHODS
    // =====================================================
    
    /**
     * Load available promotions from database
     */
    private void loadAvailablePromotions() {
        try {
            List<Promotion> promotions = promotionService.getActivePromotions();
            availablePromotions.setAll(promotions);
            System.out.println("✅ Loaded " + promotions.size() + " available promotions");
        } catch (Exception e) {
            System.err.println("❌ Error loading promotions: " + e.getMessage());
            // Load sample promotions for demo
            loadSamplePromotions();
        }
    }
    
    /**
     * Load sample promotions for demo
     */
    private void loadSamplePromotions() {
        Promotion promo1 = new Promotion("Giảm 10%", "Giảm giá 10% cho đơn hàng từ 100k", 
            Promotion.DiscountType.PERCENTAGE, 10, 100000);
        Promotion promo2 = new Promotion("Giảm 20k", "Giảm 20,000đ cho đơn hàng từ 150k", 
            Promotion.DiscountType.FIXED_AMOUNT, 20000, 150000);
        Promotion promo3 = new Promotion("Giảm 15%", "Giảm giá 15% cho khách VIP", 
            Promotion.DiscountType.PERCENTAGE, 15, 50000);
        
        availablePromotions.addAll(promo1, promo2, promo3);
        System.out.println("✅ Loaded sample promotions");
    }
    
    /**
     * Apply selected promotion
     */
    private void applySelectedPromotion() {
        Promotion selectedPromotion = promotionComboBox.getValue();
        if (selectedPromotion == null) {
            showError("Vui lòng chọn khuyến mãi");
            return;
        }
        
        double currentSubtotal = subtotalProperty.get();
        
        // Check if promotion can be applied
        if (!selectedPromotion.canApplyToOrder(currentSubtotal)) {
            showError("Không thể áp dụng khuyến mãi này. Đơn hàng tối thiểu: " + 
                formatCurrency(selectedPromotion.getMinOrderAmount()));
            return;
        }
        
        // Calculate discount amount
        double discountAmount = selectedPromotion.calculateDiscountAmount(currentSubtotal);
        
        // Apply discount
        discountAmountProperty.set(discountAmount);
        selectedPromotionProperty.set(selectedPromotion);
        
        // Update UI
        appliedPromotionLabel.setText("Đã áp dụng: " + selectedPromotion.getPromotionName());
        appliedPromotionLabel.setVisible(true);
        
        // Show discount row in summary
        discountRow.setVisible(true);
        discountRow.setManaged(true);
        
        // Disable promotion selection
        promotionComboBox.setDisable(true);
        applyPromotionButton.setText("Hủy KM");
        applyPromotionButton.setOnAction(e -> removePromotion());
        
        // Note: Promotion application is logged to console
        System.out.println("✅ Applied promotion: " + selectedPromotion.getPromotionName() + 
            " - Discount: " + formatCurrency(discountAmount));
    }
    
    /**
     * Remove applied promotion
     */
    private void removePromotion() {
        discountAmountProperty.set(0);
        selectedPromotionProperty.set(null);
        
        appliedPromotionLabel.setVisible(false);
        
        // Hide discount row in summary
        discountRow.setVisible(false);
        discountRow.setManaged(false);
        
        promotionComboBox.setDisable(false);
        promotionComboBox.setValue(null);
        
        applyPromotionButton.setText("Áp dụng");
        applyPromotionButton.setOnAction(e -> applySelectedPromotion());
        
        // Note: Promotion removal is logged to console
        System.out.println("✅ Removed promotion");
    }
    
    // =====================================================
    // QR CODE METHODS
    // =====================================================
    
    /**
     * Check if payment method is electronic (requires QR code popup)
     */
    private boolean isElectronicPaymentMethod(String paymentMethod) {
        return "MOMO".equals(paymentMethod) || 
               "VNPAY".equals(paymentMethod) || 
               "ZALOPAY".equals(paymentMethod) || 
               "BANK_TRANSFER".equals(paymentMethod);
    }
    
    /**
     * Show QR code popup for electronic payments
     */
    private void showQRCodePopup() {
        try {
            String paymentMethod = getSelectedPaymentMethod();
            double amount = grandTotalProperty.get();
            
            // Create QR code content
            String qrContent = createQRContent(paymentMethod, amount);
            
            // Create popup stage
            Stage qrPopup = new Stage();
            qrPopup.setTitle("Mã QR Thanh Toán");
            qrPopup.initModality(Modality.APPLICATION_MODAL);
            qrPopup.initOwner(payButton.getScene().getWindow());
            qrPopup.setResizable(false);
            
            // Create main container
            VBox mainContainer = new VBox(20);
            mainContainer.setAlignment(Pos.CENTER);
            mainContainer.setPadding(new Insets(30));
            mainContainer.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15;");
            
            // Title
            Label titleLabel = new Label("📱 " + getPaymentMethodDisplayName(paymentMethod));
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #5d2b1c;");
            
            // Amount
            Label amountLabel = new Label("Số tiền: " + formatCurrency(amount));
            amountLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600; -fx-text-fill: #2d1810;");
            
            // QR Code ImageView
            ImageView qrImageView = new ImageView();
            qrImageView.setFitWidth(200);
            qrImageView.setFitHeight(200);
            qrImageView.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 10; -fx-border-color: #d2b48c; -fx-border-radius: 10; -fx-border-width: 2; -fx-padding: 10;");
            
            // Generate QR code image
            Image qrImage = qrCodeService.generateQRCodeImage(qrContent);
            qrImageView.setImage(qrImage);
            
            // Instructions
            Label instructionLabel = new Label("Bấm 'Hoàn thành thanh toán' để xác nhận");
            instructionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8b4513; -fx-font-style: italic;");
            
            // Complete payment button
            Button completeButton = new Button("Hoàn thành thanh toán");
            completeButton.setStyle("-fx-background-color: #228b22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 6px; -fx-padding: 10 20 10 20; -fx-font-size: 14px;");
            completeButton.setOnAction(e -> {
                // Complete payment directly (educational project)
                try {
                    String selectedMethod = getSelectedPaymentMethod();
                    double paymentAmount = grandTotalProperty.get();
                    
                    System.out.println("🔍 DEBUG: Starting payment processing...");
                    System.out.println("🔍 DEBUG: Order ID: " + currentOrder.getOrderId());
                    System.out.println("🔍 DEBUG: Payment Method: " + selectedMethod);
                    System.out.println("🔍 DEBUG: Payment Amount: " + paymentAmount);
                    
                    // Process payment using existing service
                    boolean success = paymentService.processPayment(currentOrder, selectedMethod, paymentAmount);
                    
                    System.out.println("🔍 DEBUG: Payment result: " + success);
                    
                    if (success) {
                        showSuccess("Thanh toán thành công!");
                        System.out.println("✅ Payment completed successfully");
                        
                        // Close QR popup first
                        qrPopup.close();
                        
                        // Close payment window
                        Stage qrStage = (Stage) ((Button) e.getSource()).getScene().getWindow();
                        Stage paymentStage = (Stage) qrStage.getOwner();
                        if (paymentStage != null) {
                            paymentStage.close();
                        }
                        
                        // Notify callback for dashboard refresh
                        if (paymentCallback != null) {
                            Platform.runLater(() -> {
                                try {
                                    paymentCallback.onPaymentCompleted(currentOrder, selectedMethod);
                                    System.out.println("✅ OrderPanel notified about payment completion");
                                } catch (Exception ex) {
                                    System.err.println("❌ Error notifying payment callback: " + ex.getMessage());
                                }
                            });
                        }
                    } else {
                        System.err.println("❌ Payment processing failed");
                        showError("Không thể xử lý thanh toán. Vui lòng thử lại.");
                    }
                } catch (Exception ex) {
                    System.err.println("❌ Error completing payment: " + ex.getMessage());
                    ex.printStackTrace();
                    showError("Có lỗi xảy ra khi xử lý thanh toán: " + ex.getMessage());
                }
            });
            
            // Close button
            Button closeButton = new Button("Đóng");
            closeButton.setStyle("-fx-background-color: #5d2b1c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16 8 16; -fx-background-radius: 6px;");
            closeButton.setOnAction(e -> qrPopup.close());
            
            // Button container
            HBox buttonContainer = new HBox(10);
            buttonContainer.setAlignment(Pos.CENTER);
            buttonContainer.getChildren().addAll(completeButton, closeButton);
            
            // Add all components
            mainContainer.getChildren().addAll(titleLabel, amountLabel, qrImageView, instructionLabel, buttonContainer);
            
            // Create scene
            Scene scene = new Scene(mainContainer);
            scene.setFill(Color.TRANSPARENT);
            qrPopup.setScene(scene);
            
            // Center the popup
            qrPopup.centerOnScreen();
            
            // Show popup
            qrPopup.show();
            
            System.out.println("✅ QR Code popup shown for " + paymentMethod + " - Amount: " + formatCurrency(amount));
            
        } catch (Exception e) {
            System.err.println("❌ Error showing QR code popup: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get display name for payment method
     */
    private String getPaymentMethodDisplayName(String paymentMethod) {
        switch (paymentMethod) {
            case "MOMO": return "MoMo";
            case "VNPAY": return "VNPay";
            case "ZALOPAY": return "ZaloPay";
            case "BANK_TRANSFER": return "Chuyển khoản";
            default: return paymentMethod;
        }
    }
    
    /**
     * Create QR content for payment
     */
    private String createQRContent(String paymentMethod, double amount) {
        // Create QR content based on payment method
        String baseUrl = "https://payment.cafe.com/";
        String orderId = currentOrder != null ? currentOrder.getOrderNumber() : "ORD" + System.currentTimeMillis();
        
        switch (paymentMethod) {
            case "MOMO":
                return baseUrl + "momo?amount=" + (int)amount + "&orderId=" + orderId;
            case "VNPAY":
                return baseUrl + "vnpay?amount=" + (int)amount + "&orderId=" + orderId;
            case "ZALOPAY":
                return baseUrl + "zalopay?amount=" + (int)amount + "&orderId=" + orderId;
            case "BANK_TRANSFER":
                return baseUrl + "bank?amount=" + (int)amount + "&orderId=" + orderId;
            default:
                return "Payment: " + formatCurrency(amount) + " - Order: " + orderId;
        }
    }
    
    /**
     * Update payment sections based on selected method
     */
    private void updatePaymentSections() {
        boolean isCash = cashRadio.isSelected();
        boolean isCard = cardRadio.isSelected();
        boolean isElectronic = momoRadio.isSelected() || vnpayRadio.isSelected() || 
                              zalopayRadio.isSelected() || transferRadio.isSelected();
        
        // Show/hide sections
        if (cashPaymentSection != null) {
            cashPaymentSection.setVisible(isCash);
            cashPaymentSection.setManaged(isCash);
        }
        
        if (cardPaymentSection != null) {
            cardPaymentSection.setVisible(isCard);
            cardPaymentSection.setManaged(isCard);
        }
        
        // Hide QR section in payment panel (we use popup instead)
        if (qrCodeSection != null) {
            qrCodeSection.setVisible(false);
            qrCodeSection.setManaged(false);
        }
        
        // Note: QR code popup will be shown when payment button is clicked, not here
        
        // ✅ Auto-fill cash amount when cash is selected
        if (isCash && customerAmountField != null) {
            customerAmountProperty.set(grandTotalProperty.get());
            customerAmountField.setDisable(true); // Read-only for cash
        } else if (customerAmountField != null) {
            customerAmountField.setDisable(false);
        }
        
        System.out.println("✅ Payment sections updated - Cash: " + isCash + 
            ", Card: " + isCard + ", Electronic: " + isElectronic);
    }
    
    /**
     * Generate QR code for electronic payments
     */
    private void generateQRCode() {
        String paymentMethod = getSelectedPaymentMethod();
        double amount = grandTotalProperty.get();
        String orderNumber = currentOrder != null ? currentOrder.getOrderNumber() : "ORDER_" + System.currentTimeMillis();
        
        try {
            // Generate QR code image
            Image qrImage = qrCodeService.generatePaymentQRCode(paymentMethod, amount, orderNumber);
            if (qrCodeImageView != null) {
                qrCodeImageView.setImage(qrImage);
            }
            
            // Update instruction label
            if (qrCodeInstructionLabel != null) {
                qrCodeInstructionLabel.setText(getQRInstructionText(paymentMethod));
            }
            
            System.out.println("✅ QR Code generated for " + paymentMethod + " - Amount: " + formatCurrency(amount));
            
        } catch (Exception e) {
            System.err.println("❌ Error generating QR code: " + e.getMessage());
            showError("Không thể tạo mã QR. Vui lòng thử lại.");
        }
    }
    
    /**
     * Get QR instruction text
     */
    private String getQRInstructionText(String paymentMethod) {
        switch (paymentMethod.toLowerCase()) {
            case "momo":
                return "Mở ứng dụng MoMo, quét mã QR để thanh toán";
            case "vnpay":
                return "Sử dụng ứng dụng ngân hàng hoặc VNPay để quét mã";
            case "zalopay":
                return "Mở ứng dụng ZaloPay, quét mã QR để thanh toán";
            case "bank_transfer":
                return "Chuyển khoản theo thông tin QR hoặc nhập mã giao dịch";
            default:
                return "Quét mã QR để thanh toán";
        }
    }
    
    // =====================================================
    // ENHANCED PAYMENT METHODS FROM PAYMENT.TXT
    // =====================================================
    
    /**
     * ✅ NEW: Setup auto-fill for cash payments (from payment.txt)
     */
    private void setupCashAutoFill() {
        // Auto-fill customer amount when cash is selected
        cashRadio.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal && customerAmountField != null) {
                // Set amount = total amount for cash
                customerAmountProperty.set(grandTotalProperty.get());
                customerAmountField.setText(String.format("%.0f", grandTotalProperty.get()));
                customerAmountField.setDisable(true); // ✅ Read-only for cash
                customerAmountField.setPromptText("Tự động điền");
            } else if (customerAmountField != null) {
                customerAmountField.setDisable(false); // Enable for other methods
            }
        });
        
        // Update cash amount when total changes
        grandTotalProperty.addListener((obs, oldVal, newVal) -> {
            if (cashRadio.isSelected() && customerAmountField != null) {
                customerAmountProperty.set(newVal.doubleValue());
                customerAmountField.setText(String.format("%.0f", newVal.doubleValue()));
            }
        });
        
        // Initially set for cash (default selection)
        if (cashRadio.isSelected()) {
            customerAmountProperty.set(grandTotalProperty.get());
            if (customerAmountField != null) {
                customerAmountField.setText(String.format("%.0f", grandTotalProperty.get()));
                customerAmountField.setDisable(true);
            }
        }
        
        System.out.println("✅ Cash auto-fill configured");
    }
    
    /**
     * ✅ NEW: Process cash payment with auto amount (from payment.txt)
     */
    private void processCashPayment() {
        double totalAmount = grandTotalProperty.get();
        double receivedAmount = customerAmountProperty.get();
        
        // For cash, received amount should equal total (auto-filled)
        if (Math.abs(receivedAmount - totalAmount) > 0.01) {
            showError("Số tiền khách đưa không đúng");
            return;
        }
        
        // Create and process payment request
        PaymentRequest request = new PaymentRequest()
                .setOrderId(currentOrder.getOrderId())
                .setPaymentMethod("CASH")
                .setAmountReceived(receivedAmount)
                .setVatPercent(vatPercentProperty.get())
                .setDiscountAmount(discountAmountProperty.get());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        if (response.isSuccess()) {
            handlePaymentSuccess(response);
        } else {
            handlePaymentFailure(response);
        }
    }
    
    /**
     * ✅ NEW: Process card payment (from payment.txt)
     */
    private void processCardPayment() {
        // For educational project - no transaction code validation needed
        // Process payment directly
        
        double totalAmount = grandTotalProperty.get();
        
        // Create and process payment request
        PaymentRequest request = new PaymentRequest()
                .setOrderId(currentOrder.getOrderId())
                .setPaymentMethod("CARD")
                .setAmountReceived(totalAmount)
                .setTransactionCode(null) // No transaction code for educational project
                .setVatPercent(vatPercentProperty.get())
                .setDiscountAmount(discountAmountProperty.get());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        if (response.isSuccess()) {
            handlePaymentSuccess(response);
        } else {
            handlePaymentFailure(response);
        }
    }
    
    /**
     * ✅ NEW: Process electronic payment with QR code (from payment.txt)
     */
    private void processElectronicPayment() {
        String transactionCode = qrTransactionCodeField.getText().trim();
        String paymentMethod = getSelectedPaymentMethod();
        
        if (transactionCode.length() < 6) {
            showError("Mã giao dịch phải có ít nhất 6 ký tự");
            return;
        }
        
        double totalAmount = grandTotalProperty.get();
        
        // Create and process payment request
        PaymentRequest request = new PaymentRequest()
                .setOrderId(currentOrder.getOrderId())
                .setPaymentMethod(paymentMethod.toUpperCase())
                .setAmountReceived(totalAmount)
                .setTransactionCode(transactionCode)
                .setVatPercent(vatPercentProperty.get())
                .setDiscountAmount(discountAmountProperty.get());
        
        PaymentResponse response = paymentService.processPayment(request);
        
        if (response.isSuccess()) {
            handlePaymentSuccess(response);
        } else {
            handlePaymentFailure(response);
        }
    }
    
    /**
     * ✅ ENHANCED: Validation before payment (from payment.txt)
     */
    private boolean validateBeforePaymentEnhanced() {
        if (currentOrder == null) {
            showError("Không có đơn hàng để thanh toán");
            return false;
        }
        
        if (orderItems.isEmpty()) {
            showError("Đơn hàng trống");
            return false;
        }
        
        if (grandTotalProperty.get() <= 0) {
            showError("Tổng tiền không hợp lệ");
            return false;
        }
        
        return true;
    }
    
    /**
     * ✅ NEW: Get payment method display name (from payment.txt)
     */
    private String getPaymentMethodName(String method) {
        switch (method.toUpperCase()) {
            case "CASH": return "Tiền mặt";
            case "CARD": return "Thẻ tín dụng";
            case "MOMO": return "MoMo";
            case "VNPAY": return "VNPay";
            case "ZALOPAY": return "ZaloPay";
            case "BANK_TRANSFER": return "Chuyển khoản";
            default: return method;
        }
    }
    
    /**
     * ✅ ENHANCED: Payment processing using enhanced methods (from payment.txt)
     */
    private void handlePaymentEnhanced() {
        try {
            System.out.println("🔧 Processing enhanced payment...");
            
            // Clear previous errors
            clearErrors();
            
            // Validate before payment
            if (!validateBeforePaymentEnhanced()) {
                return;
            }
            
            String paymentMethod = getSelectedPaymentMethod();
            
            if (cashRadio.isSelected()) {
                // ✅ CASH: Use auto-filled amount
                processCashPayment();
            } else if (cardRadio.isSelected()) {
                // ✅ CARD: Manual transaction code
                processCardPayment();
            } else {
                // ✅ ELECTRONIC: QR code payment
                processElectronicPayment();
            }
            
        } catch (Exception e) {
            System.err.println("❌ Payment processing error: " + e.getMessage());
            showError("Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }
}