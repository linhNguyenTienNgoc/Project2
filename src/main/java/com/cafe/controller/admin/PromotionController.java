package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller cho màn hình quản lý khuyến mãi
 * 
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced with Dashboard Communication)
 */
public class PromotionController implements Initializable, DashboardCommunicator {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> statusFilter;
    @FXML private TableView<Object> promotionTable;
    
    @FXML private TableColumn<Object, String> codeColumn;
    @FXML private TableColumn<Object, String> nameColumn;
    @FXML private TableColumn<Object, String> typeColumn;
    @FXML private TableColumn<Object, String> valueColumn;
    @FXML private TableColumn<Object, String> startDateColumn;
    @FXML private TableColumn<Object, String> endDateColumn;
    @FXML private TableColumn<Object, String> statusColumn;
    @FXML private TableColumn<Object, String> actionColumn;
    
    private ObservableList<Object> promotionList = FXCollections.observableArrayList();
    
    // ✅ Dashboard communication
    private Object dashboardController;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        setupFilters();
        loadSampleData();
        setupEventHandlers();
        
        System.out.println("✅ PromotionController initialized successfully");
    }
    
    /**
     * Setup table columns
     */
    private void setupTable() {
        codeColumn.setCellValueFactory(new PropertyValueFactory<>("code"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));
        startDateColumn.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        endDateColumn.setCellValueFactory(new PropertyValueFactory<>("endDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Setup action column with buttons
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final HBox buttonBox = new HBox(5, editButton, deleteButton);
            
            {
                editButton.setStyle("-fx-background-color: #2E86AB; -fx-text-fill: white; -fx-font-size: 10px;");
                deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-font-size: 10px;");
                
                editButton.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    editPromotion(item);
                });
                
                deleteButton.setOnAction(e -> {
                    Object item = getTableView().getItems().get(getIndex());
                    deletePromotion(item);
                });
            }
            
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttonBox);
                }
            }
        });
        
        promotionTable.setItems(promotionList);
    }
    
    /**
     * Setup filters
     */
    private void setupFilters() {
        statusFilter.getItems().addAll("Tất cả", "Đang hoạt động", "Đã hết hạn", "Chưa bắt đầu");
        statusFilter.setValue("Tất cả");
    }
    
    /**
     * Load sample data
     */
    private void loadSampleData() {
        // Add sample promotion data
        promotionList.add(new PromotionData("KM001", "Giảm 20% cà phê", "Phần trăm", "20%", "01/12/2024", "31/12/2024", "Đang hoạt động"));
        promotionList.add(new PromotionData("KM002", "Mua 2 tặng 1", "Sản phẩm", "Tặng 1", "15/12/2024", "15/01/2025", "Đang hoạt động"));
        promotionList.add(new PromotionData("KM003", "Giảm 50K", "Tiền mặt", "50,000", "01/01/2025", "31/01/2025", "Chưa bắt đầu"));
    }
    
    /**
     * Setup event handlers
     */
    private void setupEventHandlers() {
        searchField.textProperty().addListener((obs, oldText, newText) -> {
            filterPromotions();
        });
        
        statusFilter.setOnAction(e -> {
            filterPromotions();
        });
    }
    
    /**
     * Filter promotions based on search text and status
     */
    private void filterPromotions() {
        String searchText = searchField.getText().toLowerCase();
        String selectedStatus = statusFilter.getValue();
        
        // TODO: Implement actual filtering logic
        System.out.println("Filtering promotions: " + searchText + ", Status: " + selectedStatus);
    }
    
    /**
     * Edit promotion
     */
    private void editPromotion(Object promotion) {
        // TODO: Implement edit promotion dialog
        System.out.println("Edit promotion: " + promotion);
    }
    
    /**
     * Delete promotion
     */
    private void deletePromotion(Object promotion) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Xác nhận xóa");
        alert.setHeaderText(null);
        alert.setContentText("Bạn có chắc chắn muốn xóa khuyến mãi này không?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                promotionList.remove(promotion);
                System.out.println("Deleted promotion: " + promotion);
            }
        });
    }
    
    // =====================================================
    // DASHBOARD COMMUNICATION IMPLEMENTATION
    // =====================================================
    
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ PromotionController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
    
    /**
     * Sample data class for promotions
     */
    public static class PromotionData {
        private String code, name, type, value, startDate, endDate, status;
        
        public PromotionData(String code, String name, String type, String value, 
                           String startDate, String endDate, String status) {
            this.code = code;
            this.name = name;
            this.type = type;
            this.value = value;
            this.startDate = startDate;
            this.endDate = endDate;
            this.status = status;
        }
        
        // Getters
        public String getCode() { return code; }
        public String getName() { return name; }
        public String getType() { return type; }
        public String getValue() { return value; }
        public String getStartDate() { return startDate; }
        public String getEndDate() { return endDate; }
        public String getStatus() { return status; }
    }
}
