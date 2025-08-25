package com.cafe.controller.promotion;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.service.PromotionService;
import com.cafe.model.entity.Promotion;
import com.cafe.model.entity.Promotion.DiscountType;
import com.cafe.util.AlertUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.Callback;

import java.net.URL;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller cho quản lý khuyến mãi (CRUD)
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PromotionController implements Initializable, DashboardCommunicator {

    // FXML Components - Stats
    @FXML private Label totalPromotionsLabel;
    @FXML private Label activePromotionsLabel;
    @FXML private Label expiredPromotionsLabel;
    @FXML private Label upcomingPromotionsLabel;

    // FXML Components - Buttons
    @FXML private Button addPromotionButton;
    @FXML private Button editPromotionButton;
    @FXML private Button deletePromotionButton;
    @FXML private Button refreshButton;

    // FXML Components - Search/Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<String> discountTypeFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;

    // FXML Components - Table
    @FXML private TableView<Promotion> promotionTableView;
    @FXML private TableColumn<Promotion, Integer> idColumn;
    @FXML private TableColumn<Promotion, String> nameColumn;
    @FXML private TableColumn<Promotion, String> discountTypeColumn;
    @FXML private TableColumn<Promotion, String> discountValueColumn;
    @FXML private TableColumn<Promotion, String> minOrderColumn;
    @FXML private TableColumn<Promotion, String> maxDiscountColumn;
    @FXML private TableColumn<Promotion, String> timeRangeColumn;
    @FXML private TableColumn<Promotion, String> usageColumn;
    @FXML private TableColumn<Promotion, String> statusColumn;
    @FXML private TableColumn<Promotion, Void> actionsColumn;

    // FXML Components - Status
    @FXML private Label statusLabel;
    @FXML private Label recordCountLabel;

    // Data members
    private PromotionService promotionService;
    private ObservableList<Promotion> promotionList;
    private FilteredList<Promotion> filteredPromotions;
    private Object dashboardController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeServices();
        setupTable();
        setupFilters();
        setupButtons();
        loadData();
    }

    private void initializeServices() {
        this.promotionService = new PromotionService();
        this.promotionList = FXCollections.observableArrayList();
        this.filteredPromotions = new FilteredList<>(promotionList);
    }

    private void setupTable() {
        promotionTableView.setItems(filteredPromotions);
        
        // Setup columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("promotionId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("promotionName"));
        
        // Discount type column
        discountTypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                getDiscountTypeText(cellData.getValue().getDiscountType())));
        setupDiscountTypeColumn();
        
        // Discount value column
        discountValueColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getFormattedDiscountValue()));
        
        // Min order column
        minOrderColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                String.format("%,.0f ₫", cellData.getValue().getMinOrderAmount())));
        
        // Max discount column
        maxDiscountColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getMaxDiscountAmount() > 0 ? 
                String.format("%,.0f ₫", cellData.getValue().getMaxDiscountAmount()) : "Không giới hạn"));
        
        // Time range column
        timeRangeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(getTimeRangeText(cellData.getValue())));
        
        // Usage column
        usageColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(getUsageText(cellData.getValue())));
        
        // Status column
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(getStatusText(cellData.getValue())));
        setupStatusColumn();

        // Actions column
        setupActionsColumn();

        // Selection listener
        promotionTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editPromotionButton.setDisable(!hasSelection);
            deletePromotionButton.setDisable(!hasSelection);
        });

        // Double-click handler to edit promotion
        promotionTableView.setRowFactory(tv -> {
            TableRow<Promotion> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    Promotion promotion = row.getItem();
                    editPromotion(promotion);
                }
            });
            return row;
        });

        // Add tooltip to guide users
        promotionTableView.setTooltip(new Tooltip("💡 Mẹo: Double-click vào khuyến mãi để chỉnh sửa nhanh!"));

        editPromotionButton.setDisable(true);
        deletePromotionButton.setDisable(true);
    }

    private void setupDiscountTypeColumn() {
        discountTypeColumn.setCellFactory(column -> new TableCell<Promotion, String>() {
            @Override
            protected void updateItem(String discountType, boolean empty) {
                super.updateItem(discountType, empty);
                if (empty || discountType == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label typeLabel = new Label(discountType);
                    if (discountType.contains("%")) {
                        typeLabel.getStyleClass().add("discount-type-percentage");
                    } else {
                        typeLabel.getStyleClass().add("discount-type-fixed");
                    }
                    setGraphic(typeLabel);
                    setText(null);
                }
            }
        });
    }

    private void setupStatusColumn() {
        statusColumn.setCellFactory(column -> new TableCell<Promotion, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label statusLabel = new Label(status);
                    switch (status.toLowerCase()) {
                        case "hoạt động":
                            statusLabel.getStyleClass().add("status-active");
                            break;
                        case "hết hạn":
                            statusLabel.getStyleClass().add("status-expired");
                            break;
                        case "sắp tới":
                            statusLabel.getStyleClass().add("status-upcoming");
                            break;
                        default:
                            statusLabel.getStyleClass().add("status-inactive");
                    }
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<Promotion, Void>, TableCell<Promotion, Void>>() {
            @Override
            public TableCell<Promotion, Void> call(TableColumn<Promotion, Void> param) {
                return new TableCell<Promotion, Void>() {
                    private final Button editBtn = new Button("✏️");
                    private final Button deleteBtn = new Button("🗑️");

                    {
                        editBtn.getStyleClass().addAll("action-button", "action-edit");
                        deleteBtn.getStyleClass().addAll("action-button", "action-delete");
                        
                        editBtn.setOnAction(e -> {
                            Promotion promotion = getTableView().getItems().get(getIndex());
                            editPromotion(promotion);
                        });
                        
                        deleteBtn.setOnAction(e -> {
                            Promotion promotion = getTableView().getItems().get(getIndex());
                            deletePromotion(promotion);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(5);
                            buttons.setAlignment(Pos.CENTER);
                            buttons.getChildren().addAll(editBtn, deleteBtn);
                            setGraphic(buttons);
                        }
                    }
                };
            }
        });
    }

    private void setupFilters() {
        // Discount type filter
        discountTypeFilterCombo.getItems().addAll("Tất cả", "Phần trăm", "Số tiền cố định");
        discountTypeFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.getItems().addAll("Tất cả", "Hoạt động", "Hết hạn", "Sắp tới", "Không hoạt động");
        statusFilterCombo.setValue("Tất cả");

        // Apply filters when changed
        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        discountTypeFilterCombo.valueProperty().addListener((obs, oldType, newType) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((obs, oldStatus, newStatus) -> applyFilters());
    }

    private void setupButtons() {
        addPromotionButton.setTooltip(new Tooltip("Thêm khuyến mãi mới"));
        editPromotionButton.setTooltip(new Tooltip("Chỉnh sửa khuyến mãi đã chọn"));
        deletePromotionButton.setTooltip(new Tooltip("Xóa khuyến mãi đã chọn"));
        refreshButton.setTooltip(new Tooltip("Làm mới danh sách"));
    }

    private void loadData() {
        updateStatus("Đang tải dữ liệu...");
        
        Task<List<Promotion>> loadTask = new Task<List<Promotion>>() {
            @Override
            protected List<Promotion> call() throws Exception {
                return promotionService.getAllPromotions();
            }
        };

        loadTask.setOnSucceeded(e -> {
            List<Promotion> promotions = loadTask.getValue();
            Platform.runLater(() -> {
                promotionList.setAll(promotions);
                updateStats();
                updateRecordCount();
                updateStatus("Sẵn sàng");
            });
        });

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi tải dữ liệu");
                AlertUtils.showError("Lỗi", "Không thể tải danh sách khuyến mãi: " + 
                               (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // Event handlers
    @FXML
    private void handleAddPromotion() {
        showPromotionDialog(null);
    }

    @FXML
    private void handleEditPromotion() {
        Promotion selected = promotionTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editPromotion(selected);
        }
    }

    @FXML
    private void handleDeletePromotion() {
        Promotion selected = promotionTableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deletePromotion(selected);
        }
    }

    @FXML
    private void handleRefresh() {
        loadData();
    }

    @FXML
    private void handleSearch() {
        applyFilters();
    }

    @FXML
    private void handleDiscountTypeFilter() {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter() {
        applyFilters();
    }

    // Helper methods
    private String getDiscountTypeText(DiscountType type) {
        if (type == null) return "";
        switch (type) {
            case PERCENTAGE: return "Phần trăm (%)";
            case FIXED_AMOUNT: return "Số tiền cố định (₫)";
            default: return type.toString();
        }
    }

    private String getTimeRangeText(Promotion promotion) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String start = promotion.getStartDate() != null ? 
            promotion.getStartDate().toLocalDateTime().format(formatter) : "Không giới hạn";
        String end = promotion.getEndDate() != null ? 
            promotion.getEndDate().toLocalDateTime().format(formatter) : "Không giới hạn";
        return start + " - " + end;
    }

    private String getUsageText(Promotion promotion) {
        int count = promotion.getUsageCount();
        int limit = promotion.getUsageLimit();
        if (limit > 0) {
            return count + " / " + limit;
        } else {
            return count + " / Không giới hạn";
        }
    }

    private String getStatusText(Promotion promotion) {
        if (!promotion.isActive()) {
            return "Không hoạt động";
        }
        
        Timestamp now = new Timestamp(System.currentTimeMillis());
        
        // Check if upcoming
        if (promotion.getStartDate() != null && now.before(promotion.getStartDate())) {
            return "Sắp tới";
        }
        
        // Check if expired
        if (promotion.getEndDate() != null && now.after(promotion.getEndDate())) {
            return "Hết hạn";
        }
        
        // Check if usage limit exceeded
        if (promotion.getUsageLimit() > 0 && promotion.getUsageCount() >= promotion.getUsageLimit()) {
            return "Hết lượt";
        }
        
        return "Hoạt động";
    }

    private void applyFilters() {
        filteredPromotions.setPredicate(promotion -> {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!promotion.getPromotionName().toLowerCase().contains(lowerCaseFilter)) {
                    return false;
                }
            }

            String selectedDiscountType = discountTypeFilterCombo.getValue();
            if (selectedDiscountType != null && !"Tất cả".equals(selectedDiscountType)) {
                String promotionType = getDiscountTypeText(promotion.getDiscountType());
                if (!promotionType.toLowerCase().contains(selectedDiscountType.toLowerCase())) {
                    return false;
                }
            }

            String selectedStatus = statusFilterCombo.getValue();
            if (selectedStatus != null && !"Tất cả".equals(selectedStatus)) {
                String promotionStatus = getStatusText(promotion);
                if (!promotionStatus.equals(selectedStatus)) {
                    return false;
                }
            }

            return true;
        });

        updateRecordCount();
    }

    private void editPromotion(Promotion promotion) {
        showPromotionDialog(promotion);
    }

    private void deletePromotion(Promotion promotion) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa khuyến mãi này?");
        confirmAlert.setContentText("Khuyến mãi: " + promotion.getPromotionName() + "\nLoại: " + getDiscountTypeText(promotion.getDiscountType()));

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateStatus("Đang xóa khuyến mãi...");

            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return promotionService.deactivatePromotion(promotion.getPromotionId());
                }
            };

            deleteTask.setOnSucceeded(e -> {
                boolean success = deleteTask.getValue();
                Platform.runLater(() -> {
                    if (success) {
                        promotionList.remove(promotion);
                        updateStats();
                        updateRecordCount();
                        updateStatus("Xóa khuyến mãi thành công");
                        AlertUtils.showInfo("Thành công", "Đã xóa khuyến mãi " + promotion.getPromotionName());
                    } else {
                        updateStatus("Lỗi xóa khuyến mãi");
                        AlertUtils.showError("Lỗi", "Không thể xóa khuyến mãi");
                    }
                });
            });

            deleteTask.setOnFailed(e -> {
                Throwable exception = deleteTask.getException();
                Platform.runLater(() -> {
                    updateStatus("Lỗi xóa khuyến mãi");
                    AlertUtils.showError("Lỗi", "Không thể xóa khuyến mãi: " + 
                                       (exception != null ? exception.getMessage() : "Lỗi không xác định"));
                });
            });

            Thread deleteThread = new Thread(deleteTask);
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }

    private void showPromotionDialog(Promotion existingPromotion) {
        Dialog<Promotion> dialog = new Dialog<>();
        dialog.setTitle(existingPromotion == null ? "🎉 Nopita Café - Thêm khuyến mãi mới" : "🎉 Nopita Café - Chỉnh sửa khuyến mãi");
        dialog.setHeaderText(existingPromotion == null ? "📝 Nhập thông tin khuyến mãi mới" : "✏️ Cập nhật thông tin khuyến mãi");
        dialog.setResizable(true);

        DialogPane dialogPane = dialog.getDialogPane();
        
        // Apply CSS stylesheet to dialog
        dialogPane.getStylesheets().add(getClass().getResource("/css/promotion.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setPrefWidth(600);
        dialogPane.setMinWidth(550);
        dialogPane.setPrefHeight(650);
        dialogPane.setMinHeight(600);
        
        // Create form
        GridPane formGrid = createPromotionForm(existingPromotion);
        formGrid.getStyleClass().add("form-container");
        
        // Set content
        dialogPane.setContent(formGrid);
        formGrid.setVisible(true);
        formGrid.setManaged(true);

        // Add buttons
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Apply styles to buttons
        Platform.runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setText(existingPromotion == null ? "➕ Thêm" : "✏️ Cập nhật");
            okButton.getStyleClass().addAll("btn", "btn-success");
            okButton.setDefaultButton(true);
            
            Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            cancelButton.setText("❌ Hủy");
            cancelButton.getStyleClass().addAll("btn", "btn-secondary");
            cancelButton.setCancelButton(true);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                return extractPromotionFromForm(formGrid, existingPromotion);
            }
            return null;
        });

        Optional<Promotion> result = dialog.showAndWait();
        result.ifPresent(promotion -> {
            if (existingPromotion == null) {
                addNewPromotion(promotion);
            } else {
                updateExistingPromotion(promotion);
            }
        });
    }

    private GridPane createPromotionForm(Promotion existingPromotion) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(15);
        grid.setPadding(new Insets(25, 30, 25, 30));

        int row = 0;

        // Tên khuyến mãi
        Label nameLabel = new Label("Tên khuyến mãi:");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.getStyleClass().add("form-field");
        nameField.setPromptText("Nhập tên khuyến mãi");
        if (existingPromotion != null) {
            nameField.setText(existingPromotion.getPromotionName());
        }
        grid.add(nameLabel, 0, row);
        grid.add(nameField, 1, row++);

        // Mô tả
        Label descLabel = new Label("Mô tả:");
        descLabel.getStyleClass().add("form-label");
        TextArea descArea = new TextArea();
        descArea.setId("descArea");
        descArea.getStyleClass().add("form-field");
        descArea.setPromptText("Nhập mô tả khuyến mãi");
        descArea.setPrefRowCount(3);
        if (existingPromotion != null) {
            descArea.setText(existingPromotion.getDescription());
        }
        grid.add(descLabel, 0, row);
        grid.add(descArea, 1, row++);

        // Loại giảm giá
        Label typeLabel = new Label("Loại giảm:");
        typeLabel.getStyleClass().add("form-label");
        ComboBox<DiscountType> typeCombo = new ComboBox<>();
        typeCombo.setId("typeCombo");
        typeCombo.getStyleClass().add("combo-box");
        typeCombo.getItems().addAll(DiscountType.values());
        typeCombo.setConverter(new javafx.util.StringConverter<DiscountType>() {
            @Override
            public String toString(DiscountType type) {
                return type == null ? "" : getDiscountTypeText(type);
            }

            @Override
            public DiscountType fromString(String string) {
                return null;
            }
        });
        if (existingPromotion != null) {
            typeCombo.setValue(existingPromotion.getDiscountType());
        } else {
            typeCombo.setValue(DiscountType.PERCENTAGE);
        }
        grid.add(typeLabel, 0, row);
        grid.add(typeCombo, 1, row++);

        // Giá trị giảm
        Label valueLabel = new Label("Giá trị:");
        valueLabel.getStyleClass().add("form-label");
        TextField valueField = new TextField();
        valueField.setId("valueField");
        valueField.getStyleClass().add("form-field");
        valueField.setPromptText("Nhập giá trị giảm");
        if (existingPromotion != null) {
            valueField.setText(String.valueOf(existingPromotion.getDiscountValue()));
        }
        grid.add(valueLabel, 0, row);
        grid.add(valueField, 1, row++);

        // Min order amount
        Label minOrderLabel = new Label("Đơn hàng tối thiểu:");
        minOrderLabel.getStyleClass().add("form-label");
        TextField minOrderField = new TextField();
        minOrderField.setId("minOrderField");
        minOrderField.getStyleClass().add("form-field");
        minOrderField.setPromptText("Nhập số tiền tối thiểu");
        if (existingPromotion != null) {
            minOrderField.setText(String.valueOf(existingPromotion.getMinOrderAmount()));
        }
        grid.add(minOrderLabel, 0, row);
        grid.add(minOrderField, 1, row++);

        // Max discount amount
        Label maxDiscountLabel = new Label("Giảm tối đa:");
        maxDiscountLabel.getStyleClass().add("form-label");
        TextField maxDiscountField = new TextField();
        maxDiscountField.setId("maxDiscountField");
        maxDiscountField.getStyleClass().add("form-field");
        maxDiscountField.setPromptText("Nhập số tiền giảm tối đa (0 = không giới hạn)");
        if (existingPromotion != null) {
            maxDiscountField.setText(String.valueOf(existingPromotion.getMaxDiscountAmount()));
        }
        grid.add(maxDiscountLabel, 0, row);
        grid.add(maxDiscountField, 1, row++);

        // Start date
        Label startDateLabel = new Label("Ngày bắt đầu:");
        startDateLabel.getStyleClass().add("form-label");
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setId("startDatePicker");
        startDatePicker.getStyleClass().add("date-picker");
        if (existingPromotion != null && existingPromotion.getStartDate() != null) {
            startDatePicker.setValue(existingPromotion.getStartDate().toLocalDateTime().toLocalDate());
        }
        grid.add(startDateLabel, 0, row);
        grid.add(startDatePicker, 1, row++);

        // End date
        Label endDateLabel = new Label("Ngày kết thúc:");
        endDateLabel.getStyleClass().add("form-label");
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setId("endDatePicker");
        endDatePicker.getStyleClass().add("date-picker");
        if (existingPromotion != null && existingPromotion.getEndDate() != null) {
            endDatePicker.setValue(existingPromotion.getEndDate().toLocalDateTime().toLocalDate());
        }
        grid.add(endDateLabel, 0, row);
        grid.add(endDatePicker, 1, row++);

        // Usage limit
        Label usageLimitLabel = new Label("Giới hạn số lần dùng:");
        usageLimitLabel.getStyleClass().add("form-label");
        TextField usageLimitField = new TextField();
        usageLimitField.setId("usageLimitField");
        usageLimitField.getStyleClass().add("form-field");
        usageLimitField.setPromptText("Nhập giới hạn (0 = không giới hạn)");
        if (existingPromotion != null) {
            usageLimitField.setText(String.valueOf(existingPromotion.getUsageLimit()));
        }
        grid.add(usageLimitLabel, 0, row);
        grid.add(usageLimitField, 1, row++);

        // Active checkbox
        Label activeLabel = new Label("Trạng thái:");
        activeLabel.getStyleClass().add("form-label");
        CheckBox activeCheck = new CheckBox("Khuyến mãi đang hoạt động");
        activeCheck.setId("activeCheck");
        activeCheck.getStyleClass().add("check-box");
        if (existingPromotion != null) {
            activeCheck.setSelected(existingPromotion.isActive());
        } else {
            activeCheck.setSelected(true);
        }
        grid.add(activeLabel, 0, row);
        grid.add(activeCheck, 1, row++);

        return grid;
    }

    private Promotion extractPromotionFromForm(GridPane formGrid, Promotion existingPromotion) {
        try {
            TextField nameField = (TextField) formGrid.lookup("#nameField");
            TextArea descArea = (TextArea) formGrid.lookup("#descArea");
            @SuppressWarnings("unchecked")
            ComboBox<DiscountType> typeCombo = (ComboBox<DiscountType>) formGrid.lookup("#typeCombo");
            TextField valueField = (TextField) formGrid.lookup("#valueField");
            TextField minOrderField = (TextField) formGrid.lookup("#minOrderField");
            TextField maxDiscountField = (TextField) formGrid.lookup("#maxDiscountField");
            DatePicker startDatePicker = (DatePicker) formGrid.lookup("#startDatePicker");
            DatePicker endDatePicker = (DatePicker) formGrid.lookup("#endDatePicker");
            TextField usageLimitField = (TextField) formGrid.lookup("#usageLimitField");
            CheckBox activeCheck = (CheckBox) formGrid.lookup("#activeCheck");

            if (nameField.getText().trim().isEmpty()) {
                AlertUtils.showError("Lỗi", "Vui lòng nhập tên khuyến mãi");
                return null;
            }

            if (typeCombo.getValue() == null) {
                AlertUtils.showError("Lỗi", "Vui lòng chọn loại giảm giá");
                return null;
            }

            Promotion promotion = existingPromotion == null ? new Promotion() : existingPromotion;
            promotion.setPromotionName(nameField.getText().trim());
            promotion.setDescription(descArea.getText().trim());
            promotion.setDiscountType(typeCombo.getValue());

            try {
                promotion.setDiscountValue(Double.parseDouble(valueField.getText().trim()));
            } catch (NumberFormatException e) {
                AlertUtils.showError("Lỗi", "Giá trị giảm không hợp lệ");
                return null;
            }

            try {
                promotion.setMinOrderAmount(minOrderField.getText().trim().isEmpty() ? 0 : 
                    Double.parseDouble(minOrderField.getText().trim()));
            } catch (NumberFormatException e) {
                promotion.setMinOrderAmount(0);
            }

            try {
                promotion.setMaxDiscountAmount(maxDiscountField.getText().trim().isEmpty() ? 0 : 
                    Double.parseDouble(maxDiscountField.getText().trim()));
            } catch (NumberFormatException e) {
                promotion.setMaxDiscountAmount(0);
            }

            if (startDatePicker.getValue() != null) {
                promotion.setStartDate(Timestamp.valueOf(startDatePicker.getValue().atStartOfDay()));
            }

            if (endDatePicker.getValue() != null) {
                promotion.setEndDate(Timestamp.valueOf(endDatePicker.getValue().atTime(23, 59, 59)));
            }

            try {
                promotion.setUsageLimit(usageLimitField.getText().trim().isEmpty() ? 0 : 
                    Integer.parseInt(usageLimitField.getText().trim()));
            } catch (NumberFormatException e) {
                promotion.setUsageLimit(0);
            }

            promotion.setActive(activeCheck.isSelected());

            return promotion;

        } catch (Exception e) {
            AlertUtils.showError("Lỗi", "Lỗi xử lý form: " + e.getMessage());
            return null;
        }
    }

    private void addNewPromotion(Promotion promotion) {
        updateStatus("Đang thêm khuyến mãi mới...");

        Task<Boolean> addTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return promotionService.createPromotion(promotion);
            }
        };

        addTask.setOnSucceeded(e -> {
            boolean success = addTask.getValue();
            Platform.runLater(() -> {
                if (success) {
                    loadData();
                    updateStatus("Thêm khuyến mãi thành công");
                    AlertUtils.showInfo("Thành công", "Đã thêm khuyến mãi " + promotion.getPromotionName());
                } else {
                    updateStatus("Lỗi thêm khuyến mãi");
                    AlertUtils.showError("Lỗi", "Không thể thêm khuyến mãi");
                }
            });
        });

        addTask.setOnFailed(e -> {
            Throwable exception = addTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi thêm khuyến mãi");
                AlertUtils.showError("Lỗi", "Không thể thêm khuyến mãi: " + 
                                   (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread addThread = new Thread(addTask);
        addThread.setDaemon(true);
        addThread.start();
    }

    private void updateExistingPromotion(Promotion promotion) {
        updateStatus("Đang cập nhật khuyến mãi...");

        Task<Boolean> updateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return promotionService.updatePromotion(promotion);
            }
        };

        updateTask.setOnSucceeded(e -> {
            boolean success = updateTask.getValue();
            Platform.runLater(() -> {
                if (success) {
                    loadData();
                    updateStatus("Cập nhật khuyến mãi thành công");
                    AlertUtils.showInfo("Thành công", "Đã cập nhật khuyến mãi " + promotion.getPromotionName());
                } else {
                    updateStatus("Lỗi cập nhật khuyến mãi");
                    AlertUtils.showError("Lỗi", "Không thể cập nhật khuyến mãi");
                }
            });
        });

        updateTask.setOnFailed(e -> {
            Throwable exception = updateTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi cập nhật khuyến mãi");
                AlertUtils.showError("Lỗi", "Không thể cập nhật khuyến mãi: " + 
                                   (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread updateThread = new Thread(updateTask);
        updateThread.setDaemon(true);
        updateThread.start();
    }

    private void updateStats() {
        long total = promotionList.size();
        long active = promotionList.stream().filter(p -> "Hoạt động".equals(getStatusText(p))).count();
        long expired = promotionList.stream().filter(p -> "Hết hạn".equals(getStatusText(p))).count();
        long upcoming = promotionList.stream().filter(p -> "Sắp tới".equals(getStatusText(p))).count();

        totalPromotionsLabel.setText(String.valueOf(total));
        activePromotionsLabel.setText(String.valueOf(active));
        expiredPromotionsLabel.setText(String.valueOf(expired));
        upcomingPromotionsLabel.setText(String.valueOf(upcoming));
    }

    private void updateRecordCount() {
        int total = promotionList.size();
        int filtered = filteredPromotions.size();
        
        if (total == filtered) {
            recordCountLabel.setText(total + " khuyến mãi");
        } else {
            recordCountLabel.setText(filtered + "/" + total + " khuyến mãi");
        }
    }

    private void updateStatus(String status) {
        statusLabel.setText(status);
    }

    public void refreshData() {
        Platform.runLater(this::loadData);
    }

    @Override
    public void setDashboardController(Object dashboardController) {
        this.dashboardController = dashboardController;
    }

    @Override
    public Object getDashboardController() {
        return this.dashboardController;
    }
}
