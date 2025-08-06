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

import java.io.IOException;

public class EmployeeController {
    @FXML private VBox employeeForm;
    @FXML private TextField usernameField;
    @FXML private TextField fullNameField;
    @FXML private DatePicker birthDateField;
    @FXML private ComboBox<String> genderField;
    @FXML private TextField shiftField;
    @FXML private TextField idField;
    @FXML private TableView<?> staffTable;
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
}
