package com.cafe.controller.admin;

import com.cafe.controller.base.DashboardCommunicator;
import com.cafe.service.TableService;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.entity.Area;
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
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller đầy đủ cho quản lý bàn trong Admin Dashboard
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class AdminTableController implements Initializable, DashboardCommunicator {

    // FXML Components - Stats
    @FXML private Label totalTablesLabel;
    @FXML private Label availableTablesLabel;
    @FXML private Label occupiedTablesLabel;
    @FXML private Label reservedTablesLabel;

    // FXML Components - Buttons
    @FXML private Button addTableButton;
    @FXML private Button editTableButton;
    @FXML private Button deleteTableButton;
    @FXML private Button refreshButton;

    // FXML Components - Search/Filter
    @FXML private TextField searchField;
    @FXML private ComboBox<Area> areaFilterCombo;
    @FXML private ComboBox<String> statusFilterCombo;

    // FXML Components - Table
    @FXML private TableView<TableCafe> tableView;
    @FXML private TableColumn<TableCafe, Integer> idColumn;
    @FXML private TableColumn<TableCafe, String> nameColumn;
    @FXML private TableColumn<TableCafe, String> areaColumn;
    @FXML private TableColumn<TableCafe, Integer> capacityColumn;
    @FXML private TableColumn<TableCafe, String> statusColumn;
    @FXML private TableColumn<TableCafe, Boolean> activeColumn;
    @FXML private TableColumn<TableCafe, Void> actionsColumn;

    // FXML Components - Status
    @FXML private Label statusLabel;
    @FXML private Label recordCountLabel;

    // Data members
    private TableService tableService;
    private ObservableList<TableCafe> tableList;
    private FilteredList<TableCafe> filteredTables;
    private List<Area> availableAreas;
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
        this.tableService = new TableService();
        this.tableList = FXCollections.observableArrayList();
        this.filteredTables = new FilteredList<>(tableList);
    }

    private void setupTable() {
        tableView.setItems(filteredTables);
        
        // Setup columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("tableId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("tableName"));
        areaColumn.setCellValueFactory(new PropertyValueFactory<>("areaName"));
        capacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        activeColumn.setCellValueFactory(new PropertyValueFactory<>("active"));

        // Custom cell factories
        setupStatusColumn();
        setupActiveColumn();
        setupActionsColumn();

        // Selection listener
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean hasSelection = newSelection != null;
            editTableButton.setDisable(!hasSelection);
            deleteTableButton.setDisable(!hasSelection);
        });

        // Double-click handler to edit table
        tableView.setRowFactory(tv -> {
            TableRow<TableCafe> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    TableCafe table = row.getItem();
                    editTable(table);
                }
            });
            return row;
        });

        // Add tooltip to guide users about double-click
        tableView.setTooltip(new Tooltip("💡 Mẹo: Double-click vào bàn để chỉnh sửa nhanh!"));

        editTableButton.setDisable(true);
        deleteTableButton.setDisable(true);
    }

    private void setupStatusColumn() {
        statusColumn.setCellFactory(column -> new TableCell<TableCafe, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    getStyleClass().removeAll("status-available", "status-occupied", "status-reserved", "status-cleaning");
                } else {
                    Label statusLabel = new Label(getStatusText(status));
                    statusLabel.getStyleClass().add("status-" + status);
                    setGraphic(statusLabel);
                    setText(null);
                }
            }
        });
    }

    private void setupActiveColumn() {
        activeColumn.setCellFactory(column -> new TableCell<TableCafe, Boolean>() {
            @Override
            protected void updateItem(Boolean active, boolean empty) {
                super.updateItem(active, empty);
                if (empty || active == null) {
                    setText(null);
                    getStyleClass().removeAll("active-yes", "active-no");
                } else {
                    setText(active ? "✓" : "✗");
                    getStyleClass().removeAll("active-yes", "active-no");
                    getStyleClass().add(active ? "active-yes" : "active-no");
                }
            }
        });
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<TableCafe, Void>, TableCell<TableCafe, Void>>() {
            @Override
            public TableCell<TableCafe, Void> call(TableColumn<TableCafe, Void> param) {
                return new TableCell<TableCafe, Void>() {
                    private final Button editBtn = new Button("✏️");
                    private final Button deleteBtn = new Button("🗑️");

                    {
                        editBtn.getStyleClass().addAll("action-button", "action-edit");
                        deleteBtn.getStyleClass().addAll("action-button", "action-delete");
                        
                        editBtn.setOnAction(e -> {
                            TableCafe table = getTableView().getItems().get(getIndex());
                            editTable(table);
                        });
                        
                        deleteBtn.setOnAction(e -> {
                            TableCafe table = getTableView().getItems().get(getIndex());
                            deleteTable(table);
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
        statusFilterCombo.getItems().addAll("Tất cả", "Trống", "Đang dùng", "Đã đặt", "Dọn dẹp");
        statusFilterCombo.setValue("Tất cả");

        areaFilterCombo.setConverter(new javafx.util.StringConverter<Area>() {
            @Override
            public String toString(Area area) {
                return area == null ? "Tất cả" : area.getAreaName();
            }

            @Override
            public Area fromString(String string) {
                return null;
            }
        });

        // Apply filters when changed
        searchField.textProperty().addListener((obs, oldText, newText) -> applyFilters());
        areaFilterCombo.valueProperty().addListener((obs, oldArea, newArea) -> applyFilters());
        statusFilterCombo.valueProperty().addListener((obs, oldStatus, newStatus) -> applyFilters());
    }

    private void setupButtons() {
        addTableButton.setTooltip(new Tooltip("Thêm bàn mới"));
        editTableButton.setTooltip(new Tooltip("Chỉnh sửa bàn đã chọn"));
        deleteTableButton.setTooltip(new Tooltip("Xóa bàn đã chọn"));
        refreshButton.setTooltip(new Tooltip("Làm mới danh sách"));
    }

    private void loadData() {
        updateStatus("Đang tải dữ liệu...");
        
        Task<Void> loadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                availableAreas = tableService.getAvailableAreas();
                List<TableCafe> tables = tableService.getAllTables();
                
                Platform.runLater(() -> {
                    areaFilterCombo.getItems().clear();
                    areaFilterCombo.getItems().add(null);
                    areaFilterCombo.getItems().addAll(availableAreas);
                    areaFilterCombo.setValue(null);

                    tableList.setAll(tables);
                    updateStats();
                    updateRecordCount();
                    updateStatus("Sẵn sàng");
                });
                return null;
            }
        };

        loadTask.setOnFailed(e -> {
            Throwable exception = loadTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi tải dữ liệu");
                AlertUtils.showError("Lỗi", "Không thể tải danh sách bàn: " + 
                                   (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    // Event handlers
    @FXML
    private void handleAddTable() {
        showTableDialog(null);
    }

    @FXML
    private void handleEditTable() {
        TableCafe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            editTable(selected);
        }
    }

    @FXML
    private void handleDeleteTable() {
        TableCafe selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            deleteTable(selected);
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
    private void handleAreaFilter() {
        applyFilters();
    }

    @FXML
    private void handleStatusFilter() {
        applyFilters();
    }

    // Helper methods
    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "available": return "Trống";
            case "occupied": return "Đang dùng";
            case "reserved": return "Đã đặt";
            case "cleaning": return "Dọn dẹp";
            default: return status;
        }
    }

    private String getStatusInEnglish(String vietnameseStatus) {
        switch (vietnameseStatus) {
            case "Trống": return "available";
            case "Đang dùng": return "occupied";
            case "Đã đặt": return "reserved";
            case "Dọn dẹp": return "cleaning";
            default: return vietnameseStatus;
        }
    }

    private void applyFilters() {
        filteredTables.setPredicate(table -> {
            String searchText = searchField.getText();
            if (searchText != null && !searchText.trim().isEmpty()) {
                String lowerCaseFilter = searchText.toLowerCase();
                if (!table.getTableName().toLowerCase().contains(lowerCaseFilter)) {
                    return false;
                }
            }

            Area selectedArea = areaFilterCombo.getValue();
            if (selectedArea != null) {
                if (table.getAreaId() != selectedArea.getAreaId()) {
                    return false;
                }
            }

            String selectedStatus = statusFilterCombo.getValue();
            if (selectedStatus != null && !"Tất cả".equals(selectedStatus)) {
                String statusInEnglish = getStatusInEnglish(selectedStatus);
                if (!statusInEnglish.equals(table.getStatus())) {
                    return false;
                }
            }

            return true;
        });

        updateRecordCount();
    }

    private void editTable(TableCafe table) {
        showTableDialog(table);
    }

    private void deleteTable(TableCafe table) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa bàn này?");
        confirmAlert.setContentText("Bàn: " + table.getTableName() + "\nKhu vực: " + table.getAreaName());

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            updateStatus("Đang xóa bàn...");

            Task<Boolean> deleteTask = new Task<Boolean>() {
                @Override
                protected Boolean call() throws Exception {
                    return tableService.deleteTable(table.getTableId());
                }
            };

            deleteTask.setOnSucceeded(e -> {
                boolean success = deleteTask.getValue();
                Platform.runLater(() -> {
                    if (success) {
                        tableList.remove(table);
                        updateStats();
                        updateRecordCount();
                        updateStatus("Xóa bàn thành công");
                        AlertUtils.showInfo("Thành công", "Đã xóa bàn " + table.getTableName());
                    } else {
                        updateStatus("Lỗi xóa bàn");
                        AlertUtils.showError("Lỗi", "Không thể xóa bàn: Có thể bàn đang được sử dụng");
                    }
                });
            });

            deleteTask.setOnFailed(e -> {
                Throwable exception = deleteTask.getException();
                Platform.runLater(() -> {
                    updateStatus("Lỗi xóa bàn");
                    AlertUtils.showError("Lỗi", "Không thể xóa bàn: " + 
                                       (exception != null ? exception.getMessage() : "Lỗi không xác định"));
                });
            });

            Thread deleteThread = new Thread(deleteTask);
            deleteThread.setDaemon(true);
            deleteThread.start();
        }
    }

    private void updateStats() {
        long total = tableList.size();
        long available = tableList.stream().filter(t -> "available".equals(t.getStatus())).count();
        long occupied = tableList.stream().filter(t -> "occupied".equals(t.getStatus())).count();
        long reserved = tableList.stream().filter(t -> "reserved".equals(t.getStatus())).count();

        totalTablesLabel.setText(String.valueOf(total));
        availableTablesLabel.setText(String.valueOf(available));
        occupiedTablesLabel.setText(String.valueOf(occupied));
        reservedTablesLabel.setText(String.valueOf(reserved));
    }

    private void updateRecordCount() {
        int total = tableList.size();
        int filtered = filteredTables.size();
        
        if (total == filtered) {
            recordCountLabel.setText(total + " bàn");
        } else {
            recordCountLabel.setText(filtered + "/" + total + " bàn");
        }
    }

    private void updateStatus(String status) {
        statusLabel.setText(status);
    }

    private void showTableDialog(TableCafe existingTable) {
        Dialog<TableCafe> dialog = new Dialog<>();
        dialog.setTitle(existingTable == null ? "☕ Nopita Café - Thêm bàn mới" : "☕ Nopita Café - Chỉnh sửa bàn");
        dialog.setHeaderText(existingTable == null ? "📝 Nhập thông tin bàn mới" : "✏️ Cập nhật thông tin bàn");
        dialog.setResizable(false);

        DialogPane dialogPane = dialog.getDialogPane();
        
        // Apply CSS stylesheet to dialog
        dialogPane.getStylesheets().add(getClass().getResource("/css/table-layout.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setPrefWidth(500);
        dialogPane.setMinWidth(450);
        dialogPane.setPrefHeight(450);
        dialogPane.setMinHeight(400);
        
        // Create form first to ensure proper layout
        GridPane formGrid = createTableForm(existingTable);
        formGrid.getStyleClass().add("form-container");
        
        // Debug output
        System.out.println("Form grid created with " + formGrid.getChildren().size() + " children");
        System.out.println("Form grid style classes: " + formGrid.getStyleClass());
        
        // Explicitly set background to ensure visibility
        formGrid.setStyle("-fx-background-color: white; -fx-opacity: 1;");
        
        // Set content and make visible
        dialogPane.setContent(formGrid);
        formGrid.setVisible(true);
        formGrid.setManaged(true);
        
        // Force layout update
        formGrid.autosize();
        dialogPane.autosize();

        // Add buttons after content is set
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        // Apply styles to buttons
        Platform.runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            okButton.setText(existingTable == null ? "➕ Thêm" : "✏️ Cập nhật");
            okButton.getStyleClass().addAll("btn", "btn-success");
            okButton.setDefaultButton(true);
            
            Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
            cancelButton.setText("❌ Hủy");
            cancelButton.getStyleClass().addAll("btn", "btn-secondary");
            cancelButton.setCancelButton(true);
        });

        // Get form fields
        TextField nameField = (TextField) formGrid.lookup("#nameField");
        @SuppressWarnings("unchecked")
        ComboBox<Area> areaCombo = (ComboBox<Area>) formGrid.lookup("#areaCombo");
        @SuppressWarnings("unchecked")
        Spinner<Integer> capacitySpinner = (Spinner<Integer>) formGrid.lookup("#capacitySpinner");
        @SuppressWarnings("unchecked")
        ComboBox<String> statusCombo = (ComboBox<String>) formGrid.lookup("#statusCombo");
        CheckBox activeCheck = (CheckBox) formGrid.lookup("#activeCheck");

        // Setup validation after dialog is fully initialized
        Platform.runLater(() -> {
            Button okButton = (Button) dialogPane.lookupButton(ButtonType.OK);
            
            // Initial validation state
            updateOkButtonState(okButton, nameField, areaCombo);
            
            // Validation listeners
            nameField.textProperty().addListener((obs, oldText, newText) -> 
                updateOkButtonState(okButton, nameField, areaCombo));
            areaCombo.valueProperty().addListener((obs, oldArea, newArea) -> 
                updateOkButtonState(okButton, nameField, areaCombo));
                
            // Focus on name field
            nameField.requestFocus();
            
            // Optional: Add smooth fade-in animation
            formGrid.getStyleClass().add("show");
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == ButtonType.OK) {
                // Validate form data
                if (nameField.getText().trim().isEmpty()) {
                    nameField.getStyleClass().add("error");
                    return null;
                }
                if (areaCombo.getValue() == null) {
                    areaCombo.getStyleClass().add("error");
                    return null;
                }
                
                // Clear any error styling
                nameField.getStyleClass().remove("error");
                areaCombo.getStyleClass().remove("error");
                
                // Create table object
                TableCafe table = existingTable == null ? new TableCafe() : existingTable;
                table.setTableName(nameField.getText().trim());
                table.setAreaId(areaCombo.getValue().getAreaId());
                table.setCapacity(capacitySpinner.getValue());
                table.setStatus(getStatusInEnglish(statusCombo.getValue()));
                table.setActive(activeCheck.isSelected());
                return table;
            }
            return null;
        });

        System.out.println("Opening table dialog for: " + (existingTable == null ? "new table" : existingTable.getTableName()));
        
        Optional<TableCafe> result = dialog.showAndWait();
        result.ifPresent(table -> {
            System.out.println("Dialog result received: " + table.getTableName());
            if (existingTable == null) {
                System.out.println("Adding new table");
                addNewTable(table);
            } else {
                System.out.println("Updating existing table");
                updateExistingTable(table);
            }
        });
        
        if (!result.isPresent()) {
            System.out.println("Dialog was cancelled");
        }
    }

    private GridPane createTableForm(TableCafe existingTable) {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(25, 30, 25, 30));
        
        // Set visibility and management explicitly
        grid.setVisible(true);
        grid.setManaged(true);

        int row = 0;
        
        System.out.println("Creating table form for: " + (existingTable == null ? "new table" : existingTable.getTableName()));

        // Table name
        Label nameLabel = new Label("Tên bàn:");
        nameLabel.getStyleClass().add("form-label");
        TextField nameField = new TextField();
        nameField.setId("nameField");
        nameField.getStyleClass().add("form-field");
        nameField.setPromptText("Nhập tên bàn (VD: Bàn 01, Bàn VIP...)");
        if (existingTable != null) {
            nameField.setText(existingTable.getTableName());
        }
        grid.add(nameLabel, 0, row);
        grid.add(nameField, 1, row++);

        // Area combo
        Label areaLabel = new Label("Khu vực:");
        areaLabel.getStyleClass().add("form-label");
        ComboBox<Area> areaCombo = new ComboBox<>();
        areaCombo.setId("areaCombo");
        areaCombo.getStyleClass().add("combo-box");
        areaCombo.setPromptText("Chọn khu vực");
        areaCombo.getItems().addAll(availableAreas);
        areaCombo.setConverter(new javafx.util.StringConverter<Area>() {
            @Override
            public String toString(Area area) {
                return area == null ? "" : area.getAreaName();
            }

            @Override
            public Area fromString(String string) {
                return null;
            }
        });
        if (existingTable != null) {
            areaCombo.setValue(availableAreas.stream()
                .filter(area -> area.getAreaId() == existingTable.getAreaId())
                .findFirst().orElse(null));
        }
        grid.add(areaLabel, 0, row);
        grid.add(areaCombo, 1, row++);

        // Capacity spinner
        Label capacityLabel = new Label("Sức chứa:");
        capacityLabel.getStyleClass().add("form-label");
        Spinner<Integer> capacitySpinner = new Spinner<>(1, 20, 4);
        capacitySpinner.setId("capacitySpinner");
        capacitySpinner.getStyleClass().add("spinner");
        capacitySpinner.setEditable(true);
        if (existingTable != null) {
            capacitySpinner.getValueFactory().setValue(existingTable.getCapacity());
        }
        grid.add(capacityLabel, 0, row);
        grid.add(capacitySpinner, 1, row++);

        // Status combo
        Label statusLabel = new Label("Trạng thái:");
        statusLabel.getStyleClass().add("form-label");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setId("statusCombo");
        statusCombo.getStyleClass().add("combo-box");
        statusCombo.setPromptText("Chọn trạng thái");
        statusCombo.getItems().addAll("Trống", "Đang dùng", "Đã đặt", "Dọn dẹp");
        if (existingTable != null) {
            statusCombo.setValue(getStatusText(existingTable.getStatus()));
        } else {
            statusCombo.setValue("Trống");
        }
        grid.add(statusLabel, 0, row);
        grid.add(statusCombo, 1, row++);

        // Active checkbox
        Label activeLabel = new Label("Hoạt động:");
        activeLabel.getStyleClass().add("form-label");
        CheckBox activeCheck = new CheckBox("Bàn đang được sử dụng");
        activeCheck.setId("activeCheck");
        activeCheck.getStyleClass().add("check-box");
        if (existingTable != null) {
            activeCheck.setSelected(existingTable.isActive());
        } else {
            activeCheck.setSelected(true);
        }
        grid.add(activeLabel, 0, row);
        grid.add(activeCheck, 1, row++);

        // Ensure all controls are visible
        grid.getChildren().forEach(child -> {
            child.setVisible(true);
            child.setManaged(true);
        });
        
        System.out.println("Form created with " + grid.getChildren().size() + " controls");
        System.out.println("Controls: nameField=" + nameField.getText() + ", areaCombo items=" + areaCombo.getItems().size());

        return grid;
    }

    private void addNewTable(TableCafe table) {
        updateStatus("Đang thêm bàn mới...");

        Task<Boolean> addTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return tableService.addTable(table);
            }
        };

        addTask.setOnSucceeded(e -> {
            boolean success = addTask.getValue();
            Platform.runLater(() -> {
                if (success) {
                    loadData();
                    updateStatus("Thêm bàn thành công");
                    AlertUtils.showInfo("Thành công", "Đã thêm bàn " + table.getTableName());
                } else {
                    updateStatus("Lỗi thêm bàn");
                    AlertUtils.showError("Lỗi", "Không thể thêm bàn: Có thể tên bàn đã tồn tại");
                }
            });
        });

        addTask.setOnFailed(e -> {
            Throwable exception = addTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi thêm bàn");
                AlertUtils.showError("Lỗi", "Không thể thêm bàn: " + 
                                   (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread addThread = new Thread(addTask);
        addThread.setDaemon(true);
        addThread.start();
    }

    private void updateExistingTable(TableCafe table) {
        updateStatus("Đang cập nhật bàn...");

        Task<Boolean> updateTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return tableService.updateTable(table);
            }
        };

        updateTask.setOnSucceeded(e -> {
            boolean success = updateTask.getValue();
            Platform.runLater(() -> {
                if (success) {
                    loadData();
                    updateStatus("Cập nhật bàn thành công");
                    AlertUtils.showInfo("Thành công", "Đã cập nhật bàn " + table.getTableName());
                } else {
                    updateStatus("Lỗi cập nhật bàn");
                    AlertUtils.showError("Lỗi", "Không thể cập nhật bàn: Có thể tên bàn đã tồn tại");
                }
            });
        });

        updateTask.setOnFailed(e -> {
            Throwable exception = updateTask.getException();
            Platform.runLater(() -> {
                updateStatus("Lỗi cập nhật bàn");
                AlertUtils.showError("Lỗi", "Không thể cập nhật bàn: " + 
                                   (exception != null ? exception.getMessage() : "Lỗi không xác định"));
            });
        });

        Thread updateThread = new Thread(updateTask);
        updateThread.setDaemon(true);
        updateThread.start();
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

    // Helper method for button validation
    private void updateOkButtonState(Button okButton, TextField nameField, ComboBox<Area> areaCombo) {
        boolean nameValid = nameField.getText() != null && !nameField.getText().trim().isEmpty();
        boolean areaValid = areaCombo.getValue() != null;
        boolean isValid = nameValid && areaValid;
        
        System.out.println("Validation - Name: '" + nameField.getText() + "' (" + nameValid + "), Area: " + areaCombo.getValue() + " (" + areaValid + "), Valid: " + isValid);
        
        okButton.setDisable(!isValid);
        
        // Visual feedback
        if (!nameValid) {
            nameField.getStyleClass().add("error");
        } else {
            nameField.getStyleClass().remove("error");
        }
        
        if (!areaValid) {
            areaCombo.getStyleClass().add("error");
        } else {
            areaCombo.getStyleClass().remove("error");
        }
    }
}