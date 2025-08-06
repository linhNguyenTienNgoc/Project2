package com.example.coffeapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {
    @FXML
    private Label welcomeText;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    protected void onLoginButtonClicked(ActionEvent event) throws IOException {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        System.out.println("Đăng nhập với: " + user + " / " + pass);
        //đúng mk
//        if (user.equals("") && pass.equals("")) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("fxml/employee-view.fxml"));
                Parent root = fxmlLoader.load();
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
//        }
        //sai mk
//        else {
//            System.out.println("Sai mk");
//        }
    }
}
