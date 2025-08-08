package com.cafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuScreenLauncher extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Load the menu screen FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/menu-screen.fxml"));
            Parent root = loader.load();
            
            // Get the controller
            com.cafe.controller.MenuController controller = loader.getController();
            
            // Create the scene
            Scene scene = new Scene(root, 1400, 900);
            
            // Set up the stage
            primaryStage.setTitle("Nopita Cafe - Menu Screen");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1200);
            primaryStage.setMinHeight(800);
            primaryStage.setMaximized(true);
            
            // Show the stage
            primaryStage.show();
            
            // Set up cleanup when stage is closed
            primaryStage.setOnCloseRequest(event -> {
                if (controller != null) {
                    controller.cleanup();
                }
            });
            
        } catch (IOException e) {
            System.err.println("Error loading menu screen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
