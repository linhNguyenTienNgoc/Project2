package com.coffeeshop.shopcoffeemanagement;

import com.coffeeshop.shopcoffeemanagement.model.Employee;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class DemoApplication extends Application {
    
    private static Stage primaryStage;
    private static Employee currentUser;
    
    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        
        // Load login screen
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/demo-login.fxml"));
        Parent root = loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
        
        stage.setTitle("Coffee Shop Management - Demo");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
    
    public static void showMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(DemoApplication.class.getResource("/fxml/main.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(DemoApplication.class.getResource("/css/main.css").toExternalForm());
            
            primaryStage.setTitle("Coffee Shop Management - " + (currentUser != null ? currentUser.getName() : "Demo"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            
        } catch (Exception e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể mở cửa sổ chính: " + e.getMessage());
        }
    }
    
    public static void setCurrentUser(Employee user) {
        currentUser = user;
    }
    
    public static Employee getCurrentUser() {
        return currentUser;
    }
    
    public static void showError(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showInfo(String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showConfirmation(String title, String message, Runnable onConfirm) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                onConfirm.run();
            }
        });
    }
    
    public static void main(String[] args) {
        launch(args);
    }
} 