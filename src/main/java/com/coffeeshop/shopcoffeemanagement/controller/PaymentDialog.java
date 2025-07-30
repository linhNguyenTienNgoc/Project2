package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.Invoice;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import com.coffeeshop.shopcoffeemanagement.service.PaymentService;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PaymentDialog {
    
    private Stage dialogStage;
    private Invoice invoice;
    private Employee cashier;
    private PaymentService paymentService;
    private boolean paymentCompleted = false;
    
    // UI Components
    private VBox mainContainer;
    private TextArea orderDetailsArea;
    private Label totalAmountLabel;
    private Label finalAmountLabel;
    private TextField discountField;
    private TextField tipField;
    private ComboBox<PaymentService.PaymentMethod> paymentMethodCombo;
    private VBox splitBillContainer;
    private List<SplitBillRow> splitBillRows;
    
    public PaymentDialog(Invoice invoice, Employee cashier) {
        this.invoice = invoice;
        this.cashier = cashier;
        this.paymentService = new PaymentService();
        this.splitBillRows = new ArrayList<>();
        createDialog();
    }
    
    private void createDialog() {
        dialogStage = new Stage();
        dialogStage.setTitle("Thanh toán - " + invoice.getInvoiceNumber());
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setResizable(false);
        
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(30));
        mainContainer.setPrefWidth(600);
        mainContainer.setPrefHeight(700);
        mainContainer.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        createHeader();
        createOrderDetails();
        createPaymentOptions();
        createSplitBillSection();
        createActionButtons();
        
        javafx.scene.Scene scene = new javafx.scene.Scene(mainContainer);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        dialogStage.setScene(scene);
    }
    
    private void createHeader() {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("💳 THANH TOÁN");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Label invoiceLabel = new Label("Hóa đơn: " + invoice.getInvoiceNumber());
        invoiceLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        invoiceLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        header.getChildren().addAll(titleLabel, invoiceLabel);
        mainContainer.getChildren().add(header);
    }
    
    private void createOrderDetails() {
        VBox detailsContainer = new VBox(10);
        detailsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label detailsLabel = new Label("📋 Chi tiết đơn hàng");
        detailsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        detailsLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        orderDetailsArea = new TextArea();
        orderDetailsArea.setEditable(false);
        orderDetailsArea.setPrefRowCount(6);
        orderDetailsArea.setStyle("-fx-font-family: 'Consolas', monospace; -fx-font-size: 12; -fx-background-color: white;");
        
        // Populate order details
        StringBuilder details = new StringBuilder();
        details.append("Bàn: ").append(invoice.getTableNumber()).append("\n");
        details.append("Nhân viên: ").append(cashier.getName()).append("\n");
        details.append("Thời gian: ").append(invoice.getOrderTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n\n");
        
        if (invoice.getDetails() != null) {
            for (var detail : invoice.getDetails()) {
                details.append(String.format("%-20s x%-3d %,.0f VNĐ\n", 
                    detail.getMenuItemName(), detail.getQuantity(), detail.getPrice()));
            }
        }
        
        orderDetailsArea.setText(details.toString());
        
        HBox totalBox = new HBox(10);
        totalBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label totalLabel = new Label("Tổng cộng:");
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        totalLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        totalAmountLabel = new Label(PaymentService.formatCurrency(invoice.getTotalAmount()));
        totalAmountLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        totalAmountLabel.setStyle("-fx-text-fill: #e74c3c;");
        
        totalBox.getChildren().addAll(totalLabel, totalAmountLabel);
        
        detailsContainer.getChildren().addAll(detailsLabel, orderDetailsArea, totalBox);
        mainContainer.getChildren().add(detailsContainer);
    }
    
    private void createPaymentOptions() {
        VBox optionsContainer = new VBox(15);
        optionsContainer.setStyle("-fx-background-color: #fff3cd; -fx-background-radius: 10; -fx-padding: 15;");
        
        Label optionsLabel = new Label("💰 Tùy chọn thanh toán");
        optionsLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        optionsLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        // Discount section
        VBox discountSection = new VBox(8);
        Label discountLabel = new Label("🎫 Giảm giá:");
        discountLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        HBox discountBox = new HBox(10);
        discountField = new TextField();
        discountField.setPromptText("Nhập % giảm giá (VD: 10)");
        discountField.setPrefWidth(150);
        
        Button applyDiscountBtn = new Button("Áp dụng");
        applyDiscountBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        applyDiscountBtn.setOnAction(e -> applyDiscount());
        
        discountBox.getChildren().addAll(discountField, applyDiscountBtn);
        discountSection.getChildren().addAll(discountLabel, discountBox);
        
        // Tip section
        VBox tipSection = new VBox(8);
        Label tipLabel = new Label("💝 Tip:");
        tipLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        HBox tipBox = new HBox(10);
        tipField = new TextField();
        tipField.setPromptText("Nhập số tiền tip");
        tipField.setPrefWidth(150);
        
        Button applyTipBtn = new Button("Thêm");
        applyTipBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        applyTipBtn.setOnAction(e -> addTip());
        
        tipBox.getChildren().addAll(tipField, applyTipBtn);
        tipSection.getChildren().addAll(tipLabel, tipBox);
        
        // Payment method
        VBox methodSection = new VBox(8);
        Label methodLabel = new Label("💳 Phương thức thanh toán:");
        methodLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        paymentMethodCombo = new ComboBox<>();
        paymentMethodCombo.getItems().addAll(PaymentService.PaymentMethod.values());
        paymentMethodCombo.setValue(PaymentService.PaymentMethod.CASH);
        paymentMethodCombo.setCellFactory(param -> new ListCell<PaymentService.PaymentMethod>() {
            @Override
            protected void updateItem(PaymentService.PaymentMethod item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getDisplayName());
                }
            }
        });
        paymentMethodCombo.setButtonCell(paymentMethodCombo.getCellFactory().call(null));
        
        methodSection.getChildren().addAll(methodLabel, paymentMethodCombo);
        
        // Final amount
        HBox finalAmountBox = new HBox(10);
        finalAmountBox.setAlignment(Pos.CENTER_RIGHT);
        
        Label finalLabel = new Label("Số tiền thanh toán:");
        finalLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        finalLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        finalAmountLabel = new Label(PaymentService.formatCurrency(invoice.getTotalAmount()));
        finalAmountLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        finalAmountLabel.setStyle("-fx-text-fill: #27ae60;");
        
        finalAmountBox.getChildren().addAll(finalLabel, finalAmountLabel);
        
        optionsContainer.getChildren().addAll(optionsLabel, discountSection, tipSection, methodSection, finalAmountBox);
        mainContainer.getChildren().add(optionsContainer);
    }
    
    private void createSplitBillSection() {
        VBox splitContainer = new VBox(10);
        splitContainer.setStyle("-fx-background-color: #e8f5e8; -fx-background-radius: 10; -fx-padding: 15;");
        
        HBox splitHeader = new HBox(10);
        splitHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label splitLabel = new Label("🔀 Chia bill");
        splitLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        splitLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Button addSplitBtn = new Button("➕ Thêm người");
        addSplitBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");
        addSplitBtn.setOnAction(e -> addSplitBillRow());
        
        splitHeader.getChildren().addAll(splitLabel, addSplitBtn);
        
        splitBillContainer = new VBox(8);
        
        splitContainer.getChildren().addAll(splitHeader, splitBillContainer);
        mainContainer.getChildren().add(splitContainer);
    }
    
    private void createActionButtons() {
        HBox buttonContainer = new HBox(15);
        buttonContainer.setAlignment(Pos.CENTER);
        
        Button splitBillBtn = new Button("🔀 Chia bill");
        splitBillBtn.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 25;");
        splitBillBtn.setOnAction(e -> processSplitBill());
        
        Button payBtn = new Button("💳 Thanh toán");
        payBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 25;");
        payBtn.setOnAction(e -> processPayment());
        
        Button cancelBtn = new Button("❌ Hủy");
        cancelBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 10; -fx-padding: 12 25;");
        cancelBtn.setOnAction(e -> dialogStage.close());
        
        buttonContainer.getChildren().addAll(splitBillBtn, payBtn, cancelBtn);
        mainContainer.getChildren().add(buttonContainer);
    }
    
    private void applyDiscount() {
        try {
            String discountText = discountField.getText();
            if (discountText.isEmpty()) {
                CoffeeShopApplication.showError("Lỗi", "Vui lòng nhập % giảm giá");
                return;
            }
            
            double discountPercent = Double.parseDouble(discountText);
            if (discountPercent <= 0 || discountPercent > 100) {
                CoffeeShopApplication.showError("Lỗi", "% giảm giá phải từ 1-100%");
                return;
            }
            
            BigDecimal discountAmount = invoice.getTotalAmount().multiply(BigDecimal.valueOf(discountPercent / 100));
            PaymentService.PaymentResult result = paymentService.applyDiscount(invoice, discountAmount, discountPercent + "%");
            
            if (result.isSuccess()) {
                updateFinalAmount();
                CoffeeShopApplication.showInfo("Thành công", result.getMessage());
            } else {
                CoffeeShopApplication.showError("Lỗi", result.getMessage());
            }
            
        } catch (NumberFormatException e) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng nhập số hợp lệ");
        }
    }
    
    private void addTip() {
        try {
            String tipText = tipField.getText();
            if (tipText.isEmpty()) {
                CoffeeShopApplication.showError("Lỗi", "Vui lòng nhập số tiền tip");
                return;
            }
            
            BigDecimal tipAmount = new BigDecimal(tipText);
            PaymentService.PaymentResult result = paymentService.addTip(invoice, tipAmount, "Tip");
            
            if (result.isSuccess()) {
                updateFinalAmount();
                CoffeeShopApplication.showInfo("Thành công", result.getMessage());
            } else {
                CoffeeShopApplication.showError("Lỗi", result.getMessage());
            }
            
        } catch (NumberFormatException e) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng nhập số hợp lệ");
        }
    }
    
    private void updateFinalAmount() {
        finalAmountLabel.setText(PaymentService.formatCurrency(invoice.getTotalAmount()));
    }
    
    private void addSplitBillRow() {
        SplitBillRow row = new SplitBillRow();
        splitBillRows.add(row);
        splitBillContainer.getChildren().add(row.getContainer());
    }
    
    private void processSplitBill() {
        if (splitBillRows.isEmpty()) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng thêm ít nhất 1 người để chia bill");
            return;
        }
        
        PaymentService.SplitBillRequest request = new PaymentService.SplitBillRequest(invoice);
        
        for (SplitBillRow row : splitBillRows) {
            if (row.isValid()) {
                request.addSplitItem(row.getCustomerName(), row.getAmount(), row.getPaymentMethod());
            } else {
                CoffeeShopApplication.showError("Lỗi", "Vui lòng điền đầy đủ thông tin cho tất cả người dùng");
                return;
            }
        }
        
        PaymentService.PaymentResult result = paymentService.processSplitBill(request, cashier);
        
        if (result.isSuccess()) {
            paymentCompleted = true;
            CoffeeShopApplication.showInfo("Thành công", result.getMessage());
            dialogStage.close();
        } else {
            CoffeeShopApplication.showError("Lỗi", result.getMessage());
        }
    }
    
    private void processPayment() {
        PaymentService.PaymentMethod method = paymentMethodCombo.getValue();
        if (method == null) {
            CoffeeShopApplication.showError("Lỗi", "Vui lòng chọn phương thức thanh toán");
            return;
        }
        
        PaymentService.PaymentResult result = paymentService.processPayment(invoice, method, invoice.getTotalAmount(), cashier);
        
        if (result.isSuccess()) {
            paymentCompleted = true;
            CoffeeShopApplication.showInfo("Thanh toán thành công", result.getMessage());
            dialogStage.close();
        } else {
            CoffeeShopApplication.showError("Lỗi thanh toán", result.getMessage());
        }
    }
    
    public boolean showAndWait() {
        dialogStage.showAndWait();
        return paymentCompleted;
    }
    
    private static class SplitBillRow {
        private VBox container;
        private TextField customerNameField;
        private TextField amountField;
        private ComboBox<PaymentService.PaymentMethod> paymentMethodCombo;
        private Button removeBtn;
        
        public SplitBillRow() {
            createRow();
        }
        
        private void createRow() {
            container = new VBox(8);
            container.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10;");
            
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            
            customerNameField = new TextField();
            customerNameField.setPromptText("Tên khách hàng");
            customerNameField.setPrefWidth(120);
            
            amountField = new TextField();
            amountField.setPromptText("Số tiền");
            amountField.setPrefWidth(100);
            
            paymentMethodCombo = new ComboBox<>();
            paymentMethodCombo.getItems().addAll(PaymentService.PaymentMethod.values());
            paymentMethodCombo.setValue(PaymentService.PaymentMethod.CASH);
            paymentMethodCombo.setPrefWidth(120);
            
            removeBtn = new Button("❌");
            removeBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 15; -fx-min-width: 30; -fx-min-height: 30;");
            removeBtn.setOnAction(e -> removeRow());
            
            row.getChildren().addAll(customerNameField, amountField, paymentMethodCombo, removeBtn);
            container.getChildren().add(row);
        }
        
        private void removeRow() {
            container.getParent().getChildrenUnmodifiable().remove(container);
        }
        
        public boolean isValid() {
            return !customerNameField.getText().isEmpty() && !amountField.getText().isEmpty();
        }
        
        public String getCustomerName() {
            return customerNameField.getText();
        }
        
        public BigDecimal getAmount() {
            try {
                return new BigDecimal(amountField.getText());
            } catch (NumberFormatException e) {
                return BigDecimal.ZERO;
            }
        }
        
        public PaymentService.PaymentMethod getPaymentMethod() {
            return paymentMethodCombo.getValue();
        }
        
        public VBox getContainer() {
            return container;
        }
    }
} 