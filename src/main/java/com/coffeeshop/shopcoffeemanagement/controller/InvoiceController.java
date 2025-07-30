package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.Invoice;
import com.coffeeshop.shopcoffeemanagement.model.InvoiceDetail;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class InvoiceController {
    
    @FXML
    private TableView<Invoice> invoiceTable;
    
    @FXML
    private TableColumn<Invoice, String> invoiceNumberColumn;
    
    @FXML
    private TableColumn<Invoice, String> tableNumberColumn;
    
    @FXML
    private TableColumn<Invoice, String> customerNameColumn;
    
    @FXML
    private TableColumn<Invoice, String> totalAmountColumn;
    
    @FXML
    private TableColumn<Invoice, String> statusColumn;
    
    @FXML
    private TableColumn<Invoice, String> createdAtColumn;
    
    @FXML
    private Button viewInvoiceButton;
    
    @FXML
    private Button exportInvoiceButton;
    
    @FXML
    private Button printInvoiceButton;
    
    @FXML
    private Button refreshButton;
    
    @FXML
    private TextField searchField;
    
    @FXML
    private ComboBox<String> statusFilter;
    
    private List<Invoice> allInvoices;
    
    @FXML
    public void initialize() {
        setupTable();
        setupFilters();
        loadInvoiceData();
    }
    
    private void setupTable() {
        // Setup columns
        invoiceNumberColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getInvoiceNumber()));
        
        tableNumberColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getTableNumber()));
        
        customerNameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCustomerName()));
        
        totalAmountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(String.format("%,.0f VNĐ", cellData.getValue().getTotalAmount())));
        
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(getStatusText(cellData.getValue().getStatus())));
        
        createdAtColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))));
        
        // Setup table selection
        invoiceTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            boolean hasSelection = newValue != null;
            viewInvoiceButton.setDisable(!hasSelection);
            exportInvoiceButton.setDisable(!hasSelection);
            printInvoiceButton.setDisable(!hasSelection);
        });
        
        // Double click to view
        invoiceTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                viewSelectedInvoice();
            }
        });
    }
    
    private void setupFilters() {
        // Setup status filter
        statusFilter.getItems().addAll("Tất cả", "Chưa thanh toán", "Đã thanh toán", "Đã hủy");
        statusFilter.setValue("Tất cả");
        
        // Setup search field
        searchField.setPromptText("Tìm kiếm hóa đơn...");
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterInvoices();
        });
        
        // Setup status filter
        statusFilter.valueProperty().addListener((observable, oldValue, newValue) -> {
            filterInvoices();
        });

        
    }
    
    private void loadInvoiceData() {
        try {
            // TODO: Load from database using InvoiceDAO
            // For now, show empty list
            allInvoices = new ArrayList<>();
            invoiceTable.getItems().setAll(allInvoices);
            CoffeeShopApplication.showInfo("Thông báo", "Chưa có hóa đơn nào trong hệ thống.");
        } catch (Exception e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải danh sách hóa đơn: " + e.getMessage());
            allInvoices = new ArrayList<>();
            invoiceTable.getItems().setAll(allInvoices);
        }
    }
    
    private void filterInvoices() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        
        List<Invoice> filteredInvoices = allInvoices.stream()
            .filter(invoice -> {
                // Filter by search text
                boolean matchesSearch = invoice.getInvoiceNumber().toLowerCase().contains(searchText) ||
                                      invoice.getTableNumber().toLowerCase().contains(searchText) ||
                                      invoice.getCustomerName().toLowerCase().contains(searchText);
                
                // Filter by status
                boolean matchesStatus = "Tất cả".equals(selectedStatus) ||
                                      getStatusText(invoice.getStatus()).equals(selectedStatus);
                
                return matchesSearch && matchesStatus;
            })
            .toList();
        
        invoiceTable.getItems().setAll(filteredInvoices);
    }
    
    @FXML
    private void viewSelectedInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice != null) {
            showInvoiceDetails(selectedInvoice);
        }
    }
    
    @FXML
    private void exportSelectedInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice != null) {
            exportInvoiceToFile(selectedInvoice);
        }
    }
    
    @FXML
    private void printSelectedInvoice() {
        Invoice selectedInvoice = invoiceTable.getSelectionModel().getSelectedItem();
        if (selectedInvoice != null) {
            printInvoice(selectedInvoice);
        }
    }
    
    @FXML
    private void refreshInvoiceData() {
        loadInvoiceData();
        CoffeeShopApplication.showInfo("Thành công", "Đã làm mới danh sách hóa đơn");
    }
    
    private void showInvoiceDetails(Invoice invoice) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Chi tiết hóa đơn " + invoice.getInvoiceNumber());
        dialog.setHeaderText("Thông tin hóa đơn");
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Text titleText = new Text("🧾 CHI TIẾT HÓA ĐƠN");
        titleText.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleText.setTextAlignment(TextAlignment.CENTER);
        
        VBox detailsBox = new VBox(10);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        detailsBox.getChildren().addAll(
            createDetailRow("Số hóa đơn:", invoice.getInvoiceNumber()),
            createDetailRow("Bàn:", invoice.getTableNumber()),
            createDetailRow("Khách hàng:", invoice.getCustomerName()),
            createDetailRow("Ngày tạo:", invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
            createDetailRow("Trạng thái:", getStatusText(invoice.getStatus())),
            createDetailRow("Tổng tiền:", String.format("%,.0f VNĐ", invoice.getTotalAmount()))
        );
        
        // Add invoice details
        if (invoice.getDetails() != null && !invoice.getDetails().isEmpty()) {
            Text detailsTitle = new Text("\n📋 CHI TIẾT MÓN:");
            detailsTitle.setFont(Font.font("System", FontWeight.BOLD, 14));
            detailsBox.getChildren().add(detailsTitle);
            
            for (InvoiceDetail detail : invoice.getDetails()) {
                String detailText = String.format("• %s x%d = %,.0f VNĐ", 
                    detail.getMenuItemName(), detail.getQuantity(), detail.getPrice());
                Text detailLabel = new Text(detailText);
                detailLabel.setFont(Font.font("System", 12));
                detailsBox.getChildren().add(detailLabel);
            }
        }
        
        content.getChildren().addAll(titleText, detailsBox);
        dialog.getDialogPane().setContent(content);
        
        ButtonType closeButton = new ButtonType("Đóng", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(closeButton);
        
        dialog.showAndWait();
    }
    
    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        
        Text labelText = new Text(label);
        labelText.setFont(Font.font("System", FontWeight.BOLD, 12));
        labelText.setWrappingWidth(100);
        
        Text valueText = new Text(value);
        valueText.setFont(Font.font("System", 12));
        
        row.getChildren().addAll(labelText, valueText);
        return row;
    }
    
    private void exportInvoiceToFile(Invoice invoice) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Xuất hóa đơn");
        fileChooser.setInitialFileName("HoaDon_" + invoice.getInvoiceNumber() + ".txt");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Text Files", "*.txt")
        );
        
        Stage stage = (Stage) invoiceTable.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);
        
        if (file != null) {
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("=".repeat(50));
                writer.println("           QUÁN CÀ PHÊ ABC");
                writer.println("=".repeat(50));
                writer.println("Số hóa đơn: " + invoice.getInvoiceNumber());
                writer.println("Bàn: " + invoice.getTableNumber());
                writer.println("Khách hàng: " + invoice.getCustomerName());
                writer.println("Ngày: " + invoice.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
                writer.println("-".repeat(50));
                writer.println("CHI TIẾT MÓN:");
                
                if (invoice.getDetails() != null) {
                    for (InvoiceDetail detail : invoice.getDetails()) {
                        writer.printf("%-20s x%-5d %,.0f VNĐ%n", 
                            detail.getMenuItemName(), detail.getQuantity(), detail.getPrice());
                    }
                }
                
                writer.println("-".repeat(50));
                writer.printf("TỔNG CỘNG: %,.0f VNĐ%n", invoice.getTotalAmount());
                writer.println("=".repeat(50));
                writer.println("Cảm ơn quý khách!");
                writer.println("Hẹn gặp lại!");
                
                CoffeeShopApplication.showInfo("Thành công", "Đã xuất hóa đơn thành công!");
                
            } catch (Exception e) {
                e.printStackTrace();
                CoffeeShopApplication.showError("Lỗi", "Không thể xuất hóa đơn: " + e.getMessage());
            }
        }
    }
    
    private void printInvoice(Invoice invoice) {
        // TODO: Implement actual printing
        CoffeeShopApplication.showInfo("Thông báo", "Tính năng in hóa đơn sẽ được phát triển sau");
    }
    
    private String getStatusText(String status) {
        switch (status) {
            case "PENDING":
                return "Chưa thanh toán";
            case "PAID":
                return "Đã thanh toán";
            case "CANCELLED":
                return "Đã hủy";
            default:
                return "Không xác định";
        }
    }
    
    private List<Invoice> createDemoInvoices() {
        // Return empty list - all data should come from database
        return new java.util.ArrayList<>();
    }
} 