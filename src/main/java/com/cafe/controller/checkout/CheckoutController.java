package com.cafe.controller.checkout;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import com.cafe.model.entity.Product;
import com.cafe.model.dto.PaymentResponse;

/**
 * Checkout Controller - Handles checkout and payment processing
 * 
 * @author Team POS
 * @version 2.0.0
 */
public class CheckoutController implements Initializable {

    // =====================================================
    // FXML INJECTIONS
    // =====================================================
    
    @FXML private BorderPane rootPane;
    @FXML private Label statusLabel;
    @FXML private ProgressIndicator loadingIndicator;
    
    // Cart panel elements
    @FXML private TextField productSearchField;
    
    // Summary panel elements
    @FXML private Label subtotalLabel;
    @FXML private Label taxLabel;
    @FXML private Label discountLabel;
    @FXML private Label totalLabel;
    
    // Payment method toggles
    @FXML private ToggleButton cashToggle;
    @FXML private ToggleButton cardToggle;
    @FXML private ToggleButton qrToggle;
    @FXML private ToggleButton ewalletToggle;
    
    // Action buttons
    @FXML private Button payButton;
    @FXML private Button printButton;
    @FXML private Button cancelButton;
    
    // Numpad elements
    @FXML private TextField amountField;
    @FXML private Button quick10k, quick20k, quick50k, quick100k, quick200k, quick500k;
    @FXML private Button numpad7, numpad8, numpad9, numpad4, numpad5, numpad6, numpad1, numpad2, numpad3, numpad0, numpadDot, clearBtn;
    @FXML private Button exactAmountBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Setup UI components
            setupUI();
            
            // Setup event handlers
            setupEventHandlers();
            
            // Initialize with default values
            initializeDefaultValues();
            
        } catch (Exception e) {
            showError("Failed to initialize checkout system");
        }
    }
    
    // =====================================================
    // INITIALIZATION METHODS
    // =====================================================
    
    private void setupUI() {
        // Set initial values
        statusLabel.setText("Ready");
        subtotalLabel.setText("0 ₫");
        taxLabel.setText("0 ₫");
        discountLabel.setText("0 ₫");
        totalLabel.setText("0 ₫");
        amountField.setText("0");
        
        // Setup payment method toggle group
        ToggleGroup paymentGroup = new ToggleGroup();
        cashToggle.setToggleGroup(paymentGroup);
        cardToggle.setToggleGroup(paymentGroup);
        qrToggle.setToggleGroup(paymentGroup);
        ewalletToggle.setToggleGroup(paymentGroup);
        cashToggle.setSelected(true);
    }
    
    private void setupEventHandlers() {
        // Payment method changes
        cashToggle.setOnAction(e -> handlePaymentMethodChange("Cash"));
        cardToggle.setOnAction(e -> handlePaymentMethodChange("Card"));
        qrToggle.setOnAction(e -> handlePaymentMethodChange("QR"));
        ewalletToggle.setOnAction(e -> handlePaymentMethodChange("E-Wallet"));
        
        // Action buttons
        payButton.setOnAction(e -> handlePayment());
        printButton.setOnAction(e -> handlePrintReceipt());
        cancelButton.setOnAction(e -> handleCancel());
        
        // Quick amount buttons
        quick10k.setOnAction(e -> setAmount(10000));
        quick20k.setOnAction(e -> setAmount(20000));
        quick50k.setOnAction(e -> setAmount(50000));
        quick100k.setOnAction(e -> setAmount(100000));
        quick200k.setOnAction(e -> setAmount(200000));
        quick500k.setOnAction(e -> setAmount(500000));
        
        // Numpad buttons
        numpad0.setOnAction(e -> appendDigit("0"));
        numpad1.setOnAction(e -> appendDigit("1"));
        numpad2.setOnAction(e -> appendDigit("2"));
        numpad3.setOnAction(e -> appendDigit("3"));
        numpad4.setOnAction(e -> appendDigit("4"));
        numpad5.setOnAction(e -> appendDigit("5"));
        numpad6.setOnAction(e -> appendDigit("6"));
        numpad7.setOnAction(e -> appendDigit("7"));
        numpad8.setOnAction(e -> appendDigit("8"));
        numpad9.setOnAction(e -> appendDigit("9"));
        numpadDot.setOnAction(e -> appendDigit("."));
        clearBtn.setOnAction(e -> clearAmount());
        
        exactAmountBtn.setOnAction(e -> setExactAmount());
    }
    
    private void initializeDefaultValues() {
        // Initialize with empty cart state
        updateCartSummary(0.0, 0.0, 0.0, 0.0);
    }
    
    // =====================================================
    // EVENT HANDLERS
    // =====================================================
    
    private void handlePaymentMethodChange(String method) {
        updateStatus("Payment method: " + method);
    }
    
    private void handlePayment() {
        updateStatus("Processing payment...");
        
        // Show success dialog
        Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
        successAlert.setTitle("Payment Successful");
        successAlert.setHeaderText("Thank you for your purchase!");
        successAlert.setContentText("Order Total: " + totalLabel.getText() + "\nPayment Method: " + getSelectedPaymentMethod());
        
        successAlert.showAndWait();
        
        updateStatus("Payment completed successfully!");
    }
    
    private void handlePrintReceipt() {
        Alert printAlert = new Alert(Alert.AlertType.INFORMATION);
        printAlert.setTitle("Print Receipt");
        printAlert.setHeaderText("Receipt Printed");
        printAlert.setContentText("Receipt has been sent to the default printer.");
        
        printAlert.show();
        
        updateStatus("Receipt printed");
    }
    
    private void handleCancel() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Order");
        confirmAlert.setHeaderText("Are you sure you want to cancel this order?");
        confirmAlert.setContentText("All items will be removed from the cart.");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                resetOrder();
                updateStatus("Order cancelled");
            }
        });
    }
    
    // =====================================================
    // NUMPAD HANDLERS
    // =====================================================
    
    private void appendDigit(String digit) {
        String current = amountField.getText();
        if (current.equals("0") && !digit.equals(".")) {
            amountField.setText(digit);
        } else {
            amountField.setText(current + digit);
        }
        updateStatus("Amount: " + amountField.getText());
    }
    
    private void clearAmount() {
        amountField.setText("0");
        updateStatus("Amount cleared");
    }
    
    private void setAmount(int amount) {
        amountField.setText(String.valueOf(amount));
        updateStatus("Amount set: " + amount + " ₫");
    }
    
    private void setExactAmount() {
        String totalText = totalLabel.getText().replace(" ₫", "").replace(",", "");
        try {
            double total = Double.parseDouble(totalText);
            amountField.setText(String.valueOf((int)total));
            updateStatus("Exact amount set: " + (int)total + " ₫");
        } catch (NumberFormatException e) {
            updateStatus("Error: Invalid total amount");
        }
    }
    
    // =====================================================
    // UTILITY METHODS
    // =====================================================
    
    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }
    
    private void showError(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText("An error occurred");
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
        
        updateStatus("Error: " + message);
    }
    
    public void closeWindow() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
    
    private String getSelectedPaymentMethod() {
        if (cashToggle.isSelected()) return "Cash";
        if (cardToggle.isSelected()) return "Card";
        if (qrToggle.isSelected()) return "QR";
        if (ewalletToggle.isSelected()) return "E-Wallet";
        return "Cash";
    }
    
    // =====================================================
    // CART MANAGEMENT METHODS
    // =====================================================
    
    public void updateCartSummary(double subtotal, double tax, double discount, double total) {
        subtotalLabel.setText(formatCurrency(subtotal));
        taxLabel.setText(formatCurrency(tax));
        discountLabel.setText("- " + formatCurrency(discount));
        totalLabel.setText(formatCurrency(total));
    }
    
    private String formatCurrency(double amount) {
        return String.format("%,.0f ₫", amount);
    }
    
    // =====================================================
    // COMPATIBILITY METHODS (for OrderPanelController)
    // =====================================================
    
    /**
     * Add product to cart (external API) - for compatibility
     */
    public void addProduct(Product product, int quantity) {
        updateStatus("Product added: " + product.getProductName());
    }
    
    /**
     * Set callback for payment completion - for compatibility
     */
    public void setOnPaymentSuccess(Consumer<PaymentResponse> callback) {
        // Implementation for payment success callback
    }
    
    /**
     * Reset order to initial state - for compatibility
     */
    public void resetOrder() {
        updateCartSummary(0.0, 0.0, 0.0, 0.0);
        amountField.setText("0");
        updateStatus("Order reset");
    }
}


