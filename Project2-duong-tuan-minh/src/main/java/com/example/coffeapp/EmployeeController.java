package com.example.coffeapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.io.IOException;

public class EmployeeController {
    @FXML private VBox employeeForm;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker birthDateField;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField shiftField;
    @FXML private TextField idField;
    @FXML private TableView<Employee> staffTable;
    @FXML private TableColumn<Employee, Integer> idColumn;
    @FXML private TableColumn<Employee, String> usernameColumn;
    @FXML private TableColumn<Employee, String> fullNameColumn;
    @FXML private TableColumn<Employee, String> dobColumn;
    @FXML private TableColumn<Employee, String> genderColumn;
    @FXML private TableColumn<Employee, String> shiftColumn;
    @FXML
    private void onaddEmployeeButtonClicked() {
        // Toggle visibility of the form
        boolean showing = employeeForm.isVisible();
        employeeForm.setVisible(!showing);
        employeeForm.setManaged(!showing); // hide space when invisible
    }
    @FXML
    private void onInlineAddClicked() {
        String id = idField.getText();
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String birthDate = (birthDateField.getValue() != null) ? birthDateField.getValue().toString() : "";
        String gender = genderField.getValue();
        String shift = shiftField.getText();

        System.out.println("Add inline: " + id + " - " + username + " - " + fullName);

        // TODO: Add to staffTable
        // e.g., staffTable.getItems().add(new Employee(...));

        // Clear input
        idField.clear();
        usernameField.clear();
        fullNameField.clear();
        birthDateField.setValue(null);
        genderField.setValue(null);
        shiftField.clear();
    }

    @FXML
    private void onSaveEmployeeClicked() {
        String username = usernameField.getText();
        String fullName = fullNameField.getText();
        String birthDate = (birthDateField.getValue() != null) ? birthDateField.getValue().toString() : "";
        String gender = genderField.getValue();
        String shift = shiftField.getText();

        System.out.println("Saved: " + username + " - " + fullName + " - " + birthDate + " - " + gender + " - " + shift);

        // Optionally: Add to table, clear fields, hide form
        usernameField.clear();
        fullNameField.clear();
        birthDateField.setValue(null);
        genderField.setValue(null);
        shiftField.clear();

        employeeForm.setVisible(false);
        employeeForm.setManaged(false);
    }
    @FXML
    protected void onTableButtonClicked(ActionEvent event) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/table-view.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
        fullNameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        dobColumn.setCellValueFactory(new PropertyValueFactory<>("dob"));
        genderColumn.setCellValueFactory(new PropertyValueFactory<>("gender"));
        shiftColumn.setCellValueFactory(new PropertyValueFactory<>("shift"));
        staffTable.setItems(getEmployeeList());
    }

    private ObservableList<Employee> getEmployeeList() {
        ObservableList<Employee> list = FXCollections.observableArrayList();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT user_id, username, full_name, dob, gender, shift FROM users")) {
            while (rs.next()) {
                list.add(new Employee(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("full_name"),
                    rs.getString("dob"),
                    rs.getString("gender"),
                    rs.getString("shift")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
