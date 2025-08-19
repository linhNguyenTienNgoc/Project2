package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.dao.PromotionDAO;
import com.cafe.model.Promotion;
import com.cafe.model.enums.PromotionType;
import com.cafe.util.AlertUtils;
import com.cafe.util.DateUtils;
import com.cafe.util.PriceFormatter;
import com.cafe.util.ValidationUtils;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller cho quản lý khuyến mãi trong Admin Dashboard
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
    @FXML private ComboBox<PromotionType> typeCombo;
    @FXML private TextField discountValueField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField minOrderValueField;
    @FXML private TextField maxDiscountField;
    @FXML private TextField usageLimitField;
    @FXML private ComboBox<String> statusCombo;
    @FXML private Button savePromotionButton;
    @FXML private Button cancelButton;
    @FXML private Button resetFormButton;

    // Quick Actions
    @FXML private Button createWeekendPromoButton;
    @FXML private Button createBirthdayPromoButton;
    @FXML private Button createSeasonalPromoButton;
    @FXML private Button deactivateExpiredButton;

    // Data
    private ObservableList<Promotion> promotionList = FXCollections.observableArrayList();
    private PromotionDAO promotionDAO;
    private Object dashboardController;
    private Promotion currentEditingPromotion = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initializeDAO();
            setupPromotionTable();
            setupFilters();
            setupForm();
            setupEventHandlers();
            setupValidation();
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
        this.promotionDAO = new PromotionDAO();
    }

    private void setupPromotionTable() {
        // Setup columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        typeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType().getDisplayName()));
        
        discountColumn.setCellValueFactory(cellData -> {
            Promotion promo = cellData.getValue();
            String discount = promo.getType() == PromotionType.PERCENTAGE 
                ? promo.getDiscountValue() + "%" 
                : PriceFormatter.format(promo.getDiscountValue().doubleValue());
            return new javafx.beans.property.SimpleStringProperty(discount);
        });
        
        startDateColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getStartDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
            
        endDateColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getEndDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            ));
            
        statusColumn.setCellValueFactory(cellData -> {
            Promotion promo = cellData.getValue();
            String status = getPromotionStatus(promo);
            return new javafx.beans.property.SimpleStringProperty(status);
        });

        // Setup actions column
        setupActionsColumn();

        // Set data
        promotionTable.setItems(promotionList);

        // Selection handler
        promotionTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateForm(newSelection);
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(col -> new TableCell<Promotion, Void>() {
            private final Button editButton = new Button("Sửa");
            private final Button deleteButton = new Button("Xóa");
            private final Button activateButton = new Button("Kích hoạt");
            private final Button deactivateButton = new Button("Vô hiệu");

            {
                editButton.getStyleClass().addAll("btn", "btn-primary", "btn-sm");
                deleteButton.getStyleClass().addAll("btn", "btn-danger", "btn-sm");
                activateButton.getStyleClass().addAll("btn", "btn-success", "btn-sm");
                deactivateButton.getStyleClass().addAll("btn", "btn-warning", "btn-sm");

                editButton.setOnAction(e -> editPromotion(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deletePromotion(getTableView().getItems().get(getIndex())));
                activateButton.setOnAction(e -> togglePromotionStatus(getTableView().getItems().get(getIndex()), true));
                deactivateButton.setOnAction(e -> togglePromotionStatus(getTableView().getItems().get(getIndex()), false));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Promotion promotion = getTableView().getItems().get(getIndex());
                    boolean isActive = "ACTIVE".equals(promotion.getStatus());
                    
                    VBox buttons = new VBox(5);
                    buttons.getChildren().addAll(editButton, deleteButton);
                    
                    if (isActive) {
                        buttons.getChildren().add(deactivateButton);
                    } else {
                        buttons.getChildren().add(activateButton);
                    }
                    
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupFilters() {
        // Type filter
        typeFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "Giảm giá phần trăm", "Giảm giá cố định", "Mua 1 tặng 1", "Miễn phí ship"
        ));
        typeFilterCombo.setValue("Tất cả");

        // Status filter
        statusFilterCombo.setItems(FXCollections.observableArrayList(
            "Tất cả", "Đang hoạt động", "Chưa bắt đầu", "Đã hết hạn", "Đã vô hiệu"
        ));
        statusFilterCombo.setValue("Tất cả");
    }

    private void setupForm() {
        // Type combo
        typeCombo.setItems(FXCollections.observableArrayList(PromotionType.values()));
        typeCombo.setValue(PromotionType.PERCENTAGE);

        // Status combo
        statusCombo.setItems(FXCollections.observableArrayList(
            "ACTIVE", "INACTIVE", "DRAFT"
        ));
        statusCombo.setValue("ACTIVE");

        // Date pickers
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        startDatePicker.setConverter(DateUtils.createDateConverter(formatter));
        endDatePicker.setConverter(DateUtils.createDateConverter(formatter));
        
        // Set default dates
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30));
    }

    private void setupEventHandlers() {
        // Search and filter
        searchField.textProperty().addListener((obs, oldText, newText) -> filterPromotions());
        typeFilterCombo.setOnAction(e -> filterPromotions());
        statusFilterCombo.setOnAction(e -> filterPromotions());

        // Buttons
        refreshButton.setOnAction(e -> loadPromotions());
        addPromotionButton.setOnAction(e -> showAddPromotionForm());

        // Form buttons
        savePromotionButton.setOnAction(e -> savePromotion());
        cancelButton.setOnAction(e -> hidePromotionForm());
        resetFormButton.setOnAction(e -> resetForm());

        // Quick actions
        createWeekendPromoButton.setOnAction(e -> createWeekendPromotion());
        createBirthdayPromoButton.setOnAction(e -> createBirthdayPromotion());
        createSeasonalPromoButton.setOnAction(e -> createSeasonalPromotion());
        deactivateExpiredButton.setOnAction(e -> deactivateExpiredPromotions());

        // Type change handler
        typeCombo.setOnAction(e -> updateFormBasedOnType());
    }

    private void setupValidation() {
        // Real-time validation
        nameField.textProperty().addListener((obs, oldText, newText) -> validateName());
        discountValueField.textProperty().addListener((obs, oldText, newText) -> validateDiscountValue());
        minOrderValueField.textProperty().addListener((obs, oldText, newText) -> validateMinOrderValue());
        maxDiscountField.textProperty().addListener((obs, oldText, newText) -> validateMaxDiscount());
    }

    private void loadPromotions() {
        Task<List<Promotion>> loadTask = new Task<List<Promotion>>() {
            @Override
            protected List<Promotion> call() throws Exception {
                return promotionDAO.findAll();
            }

            @Override
            protected void succeeded() {
                promotionList.clear();
                promotionList.addAll(getValue());
                updateStatistics();
                Platform.runLater(() -> {
                    AlertUtils.showInfo("Thành công", "Đã tải danh sách khuyến mãi");
                });
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Không thể tải danh sách khuyến mãi: " + getException().getMessage());
                });
            }
        };

        new Thread(loadTask).start();
    }

    private void filterPromotions() {
        String searchText = searchField.getText().toLowerCase();
        String typeFilter = typeFilterCombo.getValue();
        String statusFilter = statusFilterCombo.getValue();

        ObservableList<Promotion> filteredList = FXCollections.observableArrayList();

        for (Promotion promotion : promotionList) {
            boolean matchesSearch = searchText.isEmpty() ||
                promotion.getName().toLowerCase().contains(searchText) ||
                promotion.getDescription().toLowerCase().contains(searchText);

            boolean matchesType = "Tất cả".equals(typeFilter) ||
                promotion.getType().getDisplayName().equals(typeFilter);

            boolean matchesStatus = "Tất cả".equals(statusFilter) ||
                getPromotionStatus(promotion).equals(statusFilter);

            if (matchesSearch && matchesType && matchesStatus) {
                filteredList.add(promotion);
            }
        }

        promotionTable.setItems(filteredList);
    }

    private String getPromotionStatus(Promotion promotion) {
        LocalDate now = LocalDate.now();
        
        if (!"ACTIVE".equals(promotion.getStatus())) {
            return promotion.getStatus().equals("ACTIVE") ? "Đang hoạt động" : "Đã vô hiệu";
        }
        
        if (promotion.getStartDate().isAfter(now)) {
            return "Chưa bắt đầu";
        } else if (promotion.getEndDate().isBefore(now)) {
            return "Đã hết hạn";
        } else {
            return "Đang hoạt động";
        }
    }

    private void updateStatistics() {
        int total = promotionList.size();
        long active = promotionList.stream().filter(p -> getPromotionStatus(p).equals("Đang hoạt động")).count();
        long expired = promotionList.stream().filter(p -> getPromotionStatus(p).equals("Đã hết hạn")).count();
        long upcoming = promotionList.stream().filter(p -> getPromotionStatus(p).equals("Chưa bắt đầu")).count();

        totalPromotionsLabel.setText(String.valueOf(total));
        activePromotionsLabel.setText(String.valueOf(active));
        expiredPromotionsLabel.setText(String.valueOf(expired));
        upcomingPromotionsLabel.setText(String.valueOf(upcoming));
    }

    private void showAddPromotionForm() {
        currentEditingPromotion = null;
        resetForm();
        promotionFormSection.setVisible(true);
        promotionFormSection.setManaged(true);
        nameField.requestFocus();
    }

    private void hidePromotionForm() {
        promotionFormSection.setVisible(false);
        promotionFormSection.setManaged(false);
        currentEditingPromotion = null;
        resetForm();
    }

    private void resetForm() {
        nameField.clear();
        descriptionField.clear();
        typeCombo.setValue(PromotionType.PERCENTAGE);
        discountValueField.clear();
        startDatePicker.setValue(LocalDate.now());
        endDatePicker.setValue(LocalDate.now().plusDays(30));
        minOrderValueField.clear();
        maxDiscountField.clear();
        usageLimitField.clear();
        statusCombo.setValue("ACTIVE");
        
        clearValidationStyles();
        updateFormBasedOnType();
    }

    private void populateForm(Promotion promotion) {
        currentEditingPromotion = promotion;
        nameField.setText(promotion.getName());
        descriptionField.setText(promotion.getDescription());
        typeCombo.setValue(promotion.getType());
        discountValueField.setText(promotion.getDiscountValue().toString());
        startDatePicker.setValue(promotion.getStartDate());
        endDatePicker.setValue(promotion.getEndDate());
        minOrderValueField.setText(promotion.getMinOrderValue() != null ? promotion.getMinOrderValue().toString() : "");
        maxDiscountField.setText(promotion.getMaxDiscount() != null ? promotion.getMaxDiscount().toString() : "");
        usageLimitField.setText(promotion.getUsageLimit() != null ? promotion.getUsageLimit().toString() : "");
        statusCombo.setValue(promotion.getStatus());
        
        promotionFormSection.setVisible(true);
        promotionFormSection.setManaged(true);
        updateFormBasedOnType();
    }

    private void updateFormBasedOnType() {
        PromotionType type = typeCombo.getValue();
        if (type != null) {
            switch (type) {
                case PERCENTAGE:
                    maxDiscountField.setDisable(false);
                    break;
                case FIXED_AMOUNT:
                    maxDiscountField.setDisable(true);
                    maxDiscountField.clear();
                    break;
                case BUY_ONE_GET_ONE:
                    discountValueField.setText("100");
                    discountValueField.setDisable(true);
                    maxDiscountField.setDisable(true);
                    break;
                case FREE_SHIPPING:
                    discountValueField.setText("0");
                    discountValueField.setDisable(true);
                    maxDiscountField.setDisable(true);
                    break;
            }
        }
    }

    private void editPromotion(Promotion promotion) {
        populateForm(promotion);
    }

    private void deletePromotion(Promotion promotion) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa khuyến mãi này?");
        confirmAlert.setContentText("Khuyến mãi: " + promotion.getName());

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return promotionDAO.delete(promotion.getId());
                }

                @Override
                protected void succeeded() {
                    if (getValue()) {
                        promotionList.remove(promotion);
                        updateStatistics();
                        Platform.runLater(() -> {
                            AlertUtils.showInfo("Thành công", "Đã xóa khuyến mãi");
                        });
                    } else {
                        Platform.runLater(() -> {
                            AlertUtils.showError("Lỗi", "Không thể xóa khuyến mãi");
                        });
                    }
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi xóa khuyến mãi: " + getException().getMessage());
                    });
                }
            };

            new Thread(deleteTask).start();
        }
    }

    private void togglePromotionStatus(Promotion promotion, boolean activate) {
        String newStatus = activate ? "ACTIVE" : "INACTIVE";
        promotion.setStatus(newStatus);

        Task<Boolean> updateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return promotionDAO.update(promotion);
            }

            @Override
            protected void succeeded() {
                if (getValue()) {
                    updateStatistics();
                    promotionTable.refresh();
                    Platform.runLater(() -> {
                        AlertUtils.showInfo("Thành công", 
                            activate ? "Đã kích hoạt khuyến mãi" : "Đã vô hiệu hóa khuyến mãi");
                    });
                } else {
                    Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Không thể cập nhật trạng thái khuyến mãi");
                    });
                }
            }

            @Override
            protected void failed() {
                Platform.runLater(() -> {
                    AlertUtils.showError("Lỗi", "Lỗi khi cập nhật trạng thái: " + getException().getMessage());
                });
            }
        };

        new Thread(updateTask).start();
    }

    private void savePromotion() {
        if (!validateForm()) {
            return;
        }

        Task<Boolean> saveTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                Promotion promotion = currentEditingPromotion != null ? currentEditingPromotion : new Promotion();
                
                promotion.setName(nameField.getText().trim());
                promotion.setDescription(descriptionField.getText().trim());
                promotion.setType(typeCombo.getValue());
                promotion.setDiscountValue(new BigDecimal(discountValueField.getText().trim()));
                promotion.setStartDate(startDatePicker.getValue());
                promotion.setEndDate(endDatePicker.getValue());
                promotion.setStatus(statusCombo.getValue());
                
                if (!minOrderValueField.getText().trim().isEmpty()) {
                    promotion.setMinOrderValue(new BigDecimal(minOrderValueField.getText().trim()));
                }
                
                if (!maxDiscountField.getText().trim().isEmpty()) {
                    promotion.setMaxDiscount(new BigDecimal(maxDiscountField.getText().trim()));
                }
                
                if (!usageLimitField.getText().trim().isEmpty()) {
                    promotion.setUsageLimit(Integer.parseInt(usageLimitField.getText().trim()));
                }

                if (currentEditingPromotion == null) {
                    promotion.setCreatedAt(LocalDateTime.now());
                    return promotionDAO.create(promotion) != null;
                } else {
                    return promotionDAO.update(promotion);
                }
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    if (getValue()) {
                        AlertUtils.showInfo("Thành công", 
                            currentEditingPromotion == null ? "Đã thêm khuyến mãi mới" : "Đã cập nhật khuyến mãi");
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
    }

    // Quick Actions
    private void createWeekendPromotion() {
        showAddPromotionForm();
        nameField.setText("Khuyến mãi cuối tuần");
        descriptionField.setText("Giảm giá đặc biệt cho thứ 7 và chủ nhật");
        typeCombo.setValue(PromotionType.PERCENTAGE);
        discountValueField.setText("15");
        LocalDate nextSaturday = LocalDate.now().plusDays((6 - LocalDate.now().getDayOfWeek().getValue()) % 7 + 1);
        startDatePicker.setValue(nextSaturday);
        endDatePicker.setValue(nextSaturday.plusDays(1));
    }

    private void createBirthdayPromotion() {
        showAddPromotionForm();
        nameField.setText("Khuyến mãi sinh nhật");
        descriptionField.setText("Ưu đãi đặc biệt dành cho khách hàng sinh nhật");
        typeCombo.setValue(PromotionType.PERCENTAGE);
        discountValueField.setText("20");
        minOrderValueField.setText("200000");
    }

    private void createSeasonalPromotion() {
        showAddPromotionForm();
        nameField.setText("Khuyến mãi mùa");
        descriptionField.setText("Ưu đãi theo mùa");
        typeCombo.setValue(PromotionType.FIXED_AMOUNT);
        discountValueField.setText("50000");
        minOrderValueField.setText("300000");
        endDatePicker.setValue(LocalDate.now().plusMonths(3));
    }

    private void deactivateExpiredPromotions() {
        long expiredCount = promotionList.stream()
            .filter(p -> "ACTIVE".equals(p.getStatus()) && p.getEndDate().isBefore(LocalDate.now()))
            .count();

        if (expiredCount == 0) {
            AlertUtils.showInfo("Thông báo", "Không có khuyến mãi hết hạn nào cần vô hiệu hóa");
            return;
        }

        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận");
        confirmAlert.setHeaderText("Vô hiệu hóa tất cả khuyến mãi đã hết hạn?");
        confirmAlert.setContentText("Sẽ vô hiệu hóa " + expiredCount + " khuyến mãi");

        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            Task<Void> deactivateTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    for (Promotion promotion : promotionList) {
                        if ("ACTIVE".equals(promotion.getStatus()) && promotion.getEndDate().isBefore(LocalDate.now())) {
                            promotion.setStatus("INACTIVE");
                            promotionDAO.update(promotion);
                        }
                    }
                    return null;
                }

                @Override
                protected void succeeded() {
                    Platform.runLater(() -> {
                        loadPromotions();
                        AlertUtils.showInfo("Thành công", "Đã vô hiệu hóa tất cả khuyến mãi hết hạn");
                    });
                }

                @Override
                protected void failed() {
                    Platform.runLater(() -> {
                        AlertUtils.showError("Lỗi", "Lỗi khi vô hiệu hóa khuyến mãi: " + getException().getMessage());
                    });
                }
            };

            new Thread(deactivateTask).start();
        }
    }

    // Validation Methods
    private boolean validateForm() {
        boolean isValid = true;

        if (!validateName()) isValid = false;
        if (!validateDiscountValue()) isValid = false;
        if (!validateDateRange()) isValid = false;
        if (!validateMinOrderValue()) isValid = false;
        if (!validateMaxDiscount()) isValid = false;

        return isValid;
    }

    private boolean validateName() {
        String name = nameField.getText().trim();
        if (name.isEmpty() || name.length() < 3) {
            setFieldError(nameField, "Tên khuyến mãi phải có ít nhất 3 ký tự");
            return false;
        }
        setFieldSuccess(nameField);
        return true;
    }

    private boolean validateDiscountValue() {
        String valueText = discountValueField.getText().trim();
        if (valueText.isEmpty()) {
            setFieldError(discountValueField, "Vui lòng nhập giá trị giảm giá");
            return false;
        }

        try {
            BigDecimal value = new BigDecimal(valueText);
            PromotionType type = typeCombo.getValue();
            
            if (type == PromotionType.PERCENTAGE && (value.compareTo(BigDecimal.ZERO) <= 0 || value.compareTo(new BigDecimal("100")) > 0)) {
                setFieldError(discountValueField, "Phần trăm giảm giá phải từ 1 đến 100");
                return false;
            }
            
            if (type == PromotionType.FIXED_AMOUNT && value.compareTo(BigDecimal.ZERO) <= 0) {
                setFieldError(discountValueField, "Số tiền giảm phải lớn hơn 0");
                return false;
            }
            
            setFieldSuccess(discountValueField);
            return true;
        } catch (NumberFormatException e) {
            setFieldError(discountValueField, "Giá trị giảm giá không hợp lệ");
            return false;
        }
    }

    private boolean validateDateRange() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            AlertUtils.showWarning("Cảnh báo", "Vui lòng chọn ngày bắt đầu và kết thúc");
            return false;
        }

        if (startDate.isAfter(endDate)) {
            AlertUtils.showWarning("Cảnh báo", "Ngày bắt đầu không thể sau ngày kết thúc");
            return false;
        }

        return true;
    }

    private boolean validateMinOrderValue() {
        String valueText = minOrderValueField.getText().trim();
        if (!valueText.isEmpty()) {
            try {
                BigDecimal value = new BigDecimal(valueText);
                if (value.compareTo(BigDecimal.ZERO) < 0) {
                    setFieldError(minOrderValueField, "Giá trị đơn hàng tối thiểu không thể âm");
                    return false;
                }
            } catch (NumberFormatException e) {
                setFieldError(minOrderValueField, "Giá trị đơn hàng không hợp lệ");
                return false;
            }
        }
        setFieldSuccess(minOrderValueField);
        return true;
    }

    private boolean validateMaxDiscount() {
        String valueText = maxDiscountField.getText().trim();
        if (!valueText.isEmpty()) {
            try {
                BigDecimal value = new BigDecimal(valueText);
                if (value.compareTo(BigDecimal.ZERO) <= 0) {
                    setFieldError(maxDiscountField, "Giá trị giảm tối đa phải lớn hơn 0");
                    return false;
                }
            } catch (NumberFormatException e) {
                setFieldError(maxDiscountField, "Giá trị giảm tối đa không hợp lệ");
                return false;
            }
        }
        setFieldSuccess(maxDiscountField);
        return true;
    }

    private void setFieldError(TextField field, String message) {
        field.getStyleClass().removeAll("field-success");
        field.getStyleClass().add("field-error");
        field.setTooltip(new Tooltip(message));
    }

    private void setFieldSuccess(TextField field) {
        field.getStyleClass().removeAll("field-error");
        field.getStyleClass().add("field-success");
        field.setTooltip(null);
    }

    private void clearValidationStyles() {
        nameField.getStyleClass().removeAll("field-error", "field-success");
        discountValueField.getStyleClass().removeAll("field-error", "field-success");
        minOrderValueField.getStyleClass().removeAll("field-error", "field-success");
        maxDiscountField.getStyleClass().removeAll("field-error", "field-success");
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
