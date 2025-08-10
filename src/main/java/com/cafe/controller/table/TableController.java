package com.cafe.controller.table;

import com.cafe.CafeManagementApplication;
import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.AreaDAO;
import com.cafe.dao.base.AreaDAOImpl;
import com.cafe.dao.base.TableDAO;
import com.cafe.dao.base.TableDAOImpl;
import com.cafe.model.entity.Area;
import com.cafe.model.entity.TableCafe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;

import java.net.URL;
import java.sql.Connection;
import java.util.List;
import java.util.ResourceBundle;

public class TableController implements Initializable {

    private AreaDAO areaDAO;
    private TableDAO tableDAO;

    private int currentAreaId = -1;

    @FXML
    private TextField newTableNameField;

    @FXML
    private TextField newPeopleCountField;

    @FXML
    private TextField newAreaField;

    @FXML
    private FlowPane tableContainer;

    @FXML
    private HBox areaContainer;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupDatabase();

        initializeTextFields();
        loadAreas();
    }

    private void setupDatabase() {
        try {
            // Test database connection first
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();

            if (dbConfig.testConnection()) {
                Connection connection = DatabaseConfig.getConnection();
                tableDAO = new TableDAOImpl(connection);
                areaDAO = new AreaDAOImpl(connection);

                System.out.println("✅ Database connection established successfully");

                // Optionally log pool info in debug mode
                if (DatabaseConfig.getPropertyAsBoolean("debug.enabled", false)) {
                    System.out.println("🏊 " + dbConfig.getPoolInfo());
                }

            } else {
                System.out.println("Không thể kết nối đến cơ sở dữ liệu!\nVui lòng kiểm tra:\n" +
                        "1. MySQL Server đã chạy chưa?\n" +
                        "2. Database 'cafe_management' đã tồn tại chưa?\n" +
                        "3. Username/Password có đúng không?");
            }

        } catch (Exception e) {
            System.out.println("Lỗi kết nối database: " + e.getMessage() +
                    "\n\nVui lòng kiểm tra cấu hình trong file database_config.properties");
            e.printStackTrace();
        }
    }

    private void initializeTextFields() {

        newPeopleCountField.setTextFormatter(new javafx.scene.control.TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d*")) {
                return change;
            }
            return null;
        }));
    }

    @FXML
    public void onUserButtonClicked(){
        try{
            CafeManagementApplication.showAdminDashboard();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void onCreateNewArea() {
        String areaName = newAreaField.getText().trim().isEmpty() ? "New Area" : newAreaField.getText().trim();
        areaDAO.addArea(new Area(areaName, "", false));
        loadAreas();

        newAreaField.clear();
    }

    @FXML
    private void onAddTableClicked() {
        String tableName = newTableNameField.getText().trim();
        int peopleCount = Integer.parseInt(newPeopleCountField.getText().trim());

        TableCafe newTable = new TableCafe(
                tableName,
                currentAreaId,
                peopleCount,
                "available",
                true
        );

        boolean success = tableDAO.addTable(newTable);

        if (!success) {
            System.out.println("Failed to add table.");
            return;
        }
        System.out.println("Table added successfully!");


        loadTables(currentAreaId);
    }

    @FXML
    public void onAreaSelected(ActionEvent event) {
        Button clickedButton = (Button) event.getSource();
        currentAreaId = (int) clickedButton.getUserData();

        for (var node : areaContainer.getChildren()) {
            if (node instanceof StackPane stackPane) {
                for (var child : stackPane.getChildren()) {
                    if (!(child instanceof Button areaBtn && areaBtn.getUserData() != null))
                        continue;
                    if ((int) areaBtn.getUserData() == currentAreaId)
                        areaBtn.setStyle("-fx-background-color: #ffb487; -fx-font-weight: bold; -fx-font-size: 25px; -fx-padding: 10 20 10 20;");
                    else
                        areaBtn.setStyle("-fx-background-color: #FBECE3; -fx-font-weight: bold; -fx-font-size: 25px; -fx-padding: 10 20 10 20;");
                }
            }
        }

        loadTables(currentAreaId);
    }

    private void loadTables(int areaId) {
        tableContainer.getChildren().clear();
        List<TableCafe> tables = tableDAO.getTablesByArea(areaId);

        for (TableCafe table : tables)
            createTableView(table);
    }

    private void loadAreas() {
        areaContainer.getChildren().clear();
        var areas = areaDAO.getAllAreas();
        if (currentAreaId == -1) currentAreaId = areas.getFirst().getAreaId();
        for (Area area : areas)
            createAreaView(area);
        loadTables(currentAreaId);
    }

    private AnchorPane createTableView(TableCafe table) {
        // Outer AnchorPane
        AnchorPane tablePane = new AnchorPane();
        tablePane.setPrefSize(400, 200);
        tablePane.setStyle("-fx-background-color: #A3B9D2; -fx-background-radius: 10;");

        // Table name
        TextField tableNameField = new TextField("# " + table.getTableName());
        tableNameField.setStyle("-fx-font-size: 36px; -fx-text-fill: white; "
                + "-fx-background-color: transparent; -fx-border-color: transparent;");
        tableNameField.setOnAction(t -> {
            updateTable(table, tableNameField.getText().trim());
            tableNameField.setEditable(false);
        });
        tableNameField.setEditable(false);
        tableNameField.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                tableNameField.setEditable(true);
                tableNameField.requestFocus();
                tableNameField.selectAll();
                tableNameField.setText(tableNameField.getText().substring(2));
            }
        });
        AnchorPane.setTopAnchor(tableNameField, 20.0);
        AnchorPane.setLeftAnchor(tableNameField, 20.0);

        // Close button
        Button closeButton = new Button("X");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; "
                + "-fx-font-size: 18px; -fx-font-weight: bold;");
        closeButton.setOnAction(e -> deleteTable(table.getTableId()));
        AnchorPane.setTopAnchor(closeButton, 10.0);
        AnchorPane.setRightAnchor(closeButton, 10.0);

        // Status box (bottom left)
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        statusBox.setStyle("-fx-background-color: black; -fx-background-radius: 30; -fx-padding: 4 16;");

        Circle statusCircle = new Circle(10);
        switch (table.getStatus().toLowerCase()) {
            case "available" -> statusCircle.setFill(Color.GREEN);
            case "occupied" -> statusCircle.setFill(Color.RED);
            case "reserved" -> statusCircle.setFill(Color.ORANGE);
            case "cleaning" -> statusCircle.setFill(Color.YELLOW);
            default -> statusCircle.setFill(Color.GRAY);
        }

        Label statusLabel = new Label(table.getStatus());
        statusLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        statusBox.getChildren().addAll(statusCircle, statusLabel);
        AnchorPane.setBottomAnchor(statusBox, 20.0);
        AnchorPane.setLeftAnchor(statusBox, 20.0);

        // People count (bottom right)
        HBox peopleBox = new HBox(5);
        peopleBox.setAlignment(Pos.CENTER_RIGHT);

        Text peopleIcon = new Text("👥");
        peopleIcon.setStyle("-fx-font-size: 28px;");

        Text peopleCount = new Text(String.valueOf(table.getCapacity()));
        peopleCount.setStyle("-fx-font-size: 28px; -fx-fill: white;");

        peopleBox.getChildren().addAll(peopleIcon, peopleCount);
        AnchorPane.setBottomAnchor(peopleBox, 20.0);
        AnchorPane.setRightAnchor(peopleBox, 20.0);

        // Add all children
        tablePane.getChildren().addAll(tableNameField, closeButton, statusBox, peopleBox);

        // Add to container
        tableContainer.getChildren().add(tablePane);

        return tablePane;
    }

    private StackPane createAreaView(Area area) {
        String baseStyle = "-fx-font-weight: bold; -fx-font-size: 25px; -fx-padding: 10 20 10 20;";
        String bgColor = (area.getAreaId() != currentAreaId) ? "#FBECE3" : "#ffb487";

        Button areaButton = new Button(area.getAreaName());
        areaButton.setStyle(baseStyle + " -fx-background-color: " + bgColor + ";");
        areaButton.setMaxWidth(Double.MAX_VALUE);
        areaButton.setUserData(area.getAreaId());
        areaButton.setOnAction(this::onAreaSelected);

        Button closeButton = new Button("×");
        closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: black; -fx-font-size: 20px; -fx-padding: 0;");
        StackPane.setAlignment(closeButton, Pos.TOP_RIGHT);
        closeButton.setOnAction(e -> deleteArea(area.getAreaId()));

        StackPane stackPane = new StackPane(areaButton, closeButton);
        stackPane.setMaxWidth(Double.MAX_VALUE);

        areaContainer.getChildren().add(stackPane);

        return stackPane;
    }

    private void deleteArea(int areaId) {
        areaDAO.deleteArea(areaId);
        loadAreas();
        newAreaField.clear();
    }

    private void deleteTable(int tableID) {
        tableDAO.deleteTable(tableID);
        loadTables(currentAreaId);
    }

    private void updateTable(TableCafe table, String newTableName) {
        table.setTableName(newTableName);
        tableDAO.updateTable(table);
        loadTables(currentAreaId);
    }
}