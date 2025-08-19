package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.PromotionDAO;
import com.cafe.dao.base.PromotionDAOImpl;
import com.cafe.model.entity.Promotion;
import com.cafe.model.entity.Promotion.DiscountType;
import com.cafe.util.AlertUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.sql.Connection;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Simple AdminPromotionController để tương thích với model hiện có
 */
public class AdminPromotionController implements Initializable, DashboardCommunicator {

    // Search and Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;
    @FXML private Button refreshButton;
    @FXML private Button addPromotionButton;

    // Promotion Table
    @FXML private TableView<Promotion> promotionTable;
    @FXML private TableColumn<Promotion, Integer> idColumn;
    @FXML private TableColumn<Promotion, String> nameColumn;
    @FXML private TableColumn<Promotion, String> typeColumn;
    @FXML private TableColumn<Promotion, String> discountColumn;
    @FXML private TableColumn<Promotion, String> startDateColumn;
    @FXML private TableColumn<Promotion, String> endDateColumn;
    @FXML private TableColumn<Promotion, String> statusColumn;
    @FXML private TableColumn<Promotion, Void> actionsColumn;

    // Statistics
    @FXML private Label totalPromotionsLabel;
    @FXML private Label activePromotionsLabel;
    @FXML private Label expiredPromotionsLabel;
    @FXML private Label upcomingPromotionsLabel;

    // Form Section
    @FXML private VBox promotionFormSection;
    @FXML private TextField nameField;
    @FXML private TextArea descriptionField;
    @FXML private ComboBox<DiscountType> typeCombo;
    @FXML private TextField discountValueField;
    @FXML private TextField minOrderValueField;
    @FXML private TextField maxDiscountField;
    @FXML private TextField usageLimitField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button savePromotionButton;
    @FXML private Button cancelButton;
    @FXML private Button resetFormButton;

    // Data
    private ObservableList<Promotion> promotionList = FXCollections.observableArrayList();
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeDAO();
            setupPromotionTable();
            setupFilters();
            setupForm();
            setupEventHandlers();
            loadPromotions();
            updateStatistics();

            System.out.println("✅ AdminPromotionController initialized successfully");
        } catch (Exception e) {
            System.err.println("Error initializing AdminPromotionController: " + e.getMessage());
            e.printStackTrace();
            AlertUtils.showError("Lỗi khởi tạo", "Không thể khởi tạo giao diện quản lý khuyến mãi: " + e.getMessage());
        }
    }

    private void initializeDAO() {
        // DAO sẽ được khởi tạo với connection khi cần sử dụng
        // Không cần khởi tạo ở đây vì cần fresh connection cho mỗi operation
    }

    private void setupPromotionTable() {
        // Setup columns với basic implementation
        idColumn.setCellValueFactory(new PropertyValueFactory<>("promotionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("promotionName"));
        typeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDiscountType() != null ? 
                cellData.getValue().getDiscountType().toString() : ""));
        
        discountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.valueOf(cellData.getValue().getDiscountValue())));
        
        startDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate() != null ? 
                cellData.getValue().getStartDate().toString() : ""));
                
        endDateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndDate() != null ? 
                cellData.getValue().getEndDate().toString() : ""));
            
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().isActive() ? "ACTIVE" : "INACTIVE"));

        // Set data
        promotionTable.setItems(promotionList);
    }

    private void setupFilters() {
        // Type filter
        typeFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "PERCENTAGE", "FIXED_AMOUNT"
        ));
        typeFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "ACTIVE", "INACTIVE"
        ));
        statusFilterCombo.setValue("Tất cả");
    }

    private void setupForm() {
        if (typeCombo != null) {
            typeCombo.setItems(FXCollections.observableArrayList(DiscountType.values()));
            typeCombo.setValue(DiscountType.PERCENTAGE);
        }

        if (statusCombo != null) {
            statusCombo.setItems(FXCollections.observableArrayList("ACTIVE", "INACTIVE"));
            statusCombo.setValue("ACTIVE");
        }
    }

    private void setupEventHandlers() {
        if (refreshButton != null) {
            refreshButton.setOnAction(e -> loadPromotions());
        }
        
        if (addPromotionButton != null) {
            addPromotionButton.setOnAction(e -> showAddPromotionForm());
        }

        if (savePromotionButton != null) {
            savePromotionButton.setOnAction(e -> savePromotion());
        }
        
        if (cancelButton != null) {
            cancelButton.setOnAction(e -> hidePromotionForm());
        }
        
        if (resetFormButton != null) {
            resetFormButton.setOnAction(e -> resetForm());
        }
    }

    private void loadPromotions() {
        Task<List<Promotion>> loadTask = new Task<List<Promotion>>() {
            @Override
            protected List<Promotion> call() throws Exception {
                try (Connection connection = DatabaseConfig.getConnection()) {
                    PromotionDAO dao = new PromotionDAOImpl(connection);
                    return dao.getAllPromotions();
                }
            }

            @Override
            protected void succeeded() {
                promotionList.clear();
                if (getValue() != null) {
                    promotionList.addAll(getValue());
                }
                updateStatistics();
                Platform.runLater(() -> {
                    System.out.println("Đã tải " + promotionList.size() + " khuyến mãi");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    System.err.println("Lỗi khi tải danh sách khuyến mãi: " + getException().getMessage());
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void updateStatistics() {
        int total = promotionList.size();
        long active = promotionList.stream().filter(p -> p.isActive()).count();
        long expired = promotionList.stream().filter(p -> !p.isActive()).count();
        long upcoming = 0; // Simplified for now

        if (totalPromotionsLabel != null) totalPromotionsLabel.setText(String.valueOf(total));
        if (activePromotionsLabel != null) activePromotionsLabel.setText(String.valueOf(active));
        if (expiredPromotionsLabel != null) expiredPromotionsLabel.setText(String.valueOf(expired));
        if (upcomingPromotionsLabel != null) upcomingPromotionsLabel.setText(String.valueOf(upcoming));
    }

    private void showAddPromotionForm() {
        if (promotionFormSection != null) {
            promotionFormSection.setVisible(true);
            promotionFormSection.setManaged(true);
            resetForm();
        }
    }

    private void hidePromotionForm() {
        if (promotionFormSection != null) {
            promotionFormSection.setVisible(false);
            promotionFormSection.setManaged(false);
        }
    }

    private void resetForm() {
        if (nameField != null) nameField.clear();
        if (descriptionField != null) descriptionField.clear();
        if (discountValueField != null) discountValueField.clear();
        if (minOrderValueField != null) minOrderValueField.clear();
        if (maxDiscountField != null) maxDiscountField.clear();
        if (usageLimitField != null) usageLimitField.clear();
        
        if (typeCombo != null) typeCombo.setValue(DiscountType.PERCENTAGE);
        if (statusCombo != null) statusCombo.setValue("ACTIVE");
    }

    private void savePromotion() {
        // Simplified save method
        try {
            if (nameField == null || nameField.getText().trim().isEmpty()) {
                AlertUtils.showWarning("Cảnh báo", "Vui lòng nhập tên khuyến mãi");
                return;
            }

            Promotion promotion = new Promotion();
            promotion.setPromotionName(nameField.getText().trim());
            
            if (descriptionField != null) {
                promotion.setDescription(descriptionField.getText().trim());
            }
            
            if (typeCombo != null && typeCombo.getValue() != null) {
                promotion.setDiscountType(typeCombo.getValue());
            }
            
            if (discountValueField != null && !discountValueField.getText().trim().isEmpty()) {
                try {
                    double discountValue = Double.parseDouble(discountValueField.getText().trim());
                    promotion.setDiscountValue(discountValue);
                } catch (NumberFormatException e) {
                    AlertUtils.showWarning("Cảnh báo", "Giá trị giảm giá không hợp lệ");
                    return;
                }
            }
            
            promotion.setActive(statusCombo != null && "ACTIVE".equals(statusCombo.getValue()));

            // Simple create operation
            Task<Boolean> saveTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    try (Connection connection = DatabaseConfig.getConnection()) {
                        PromotionDAO dao = new PromotionDAOImpl(connection);
                        return dao.insertPromotion(promotion);
                    }
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        if (getValue() != null && getValue()) {
                            AlertUtils.showInfo("Thành công", "Đã thêm khuyến mãi mới");
                            hidePromotionForm();
                            loadPromotions();
                        } else {
                            AlertUtils.showError("Lỗi", "Không thể lưu khuyến mãi");
                        }
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi lưu khuyến mãi: " + getException().getMessage());
                    });
                }
            };

            new Thread(saveTask).start();
            
        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Lỗi khi lưu khuyến mãi: " + e.getMessage());
        }
    }

    // Dashboard Communication
    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
        System.out.println("✅ AdminPromotionController connected to Dashboard");
    }

    @Override
    public Object getDashboardController() {
        return dashboardController;
    }
}