package com.coffeeshop.shopcoffeemanagement;

import com.coffeeshop.shopcoffeemanagement.model.Employee;
import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

public class CoffeeShopApplication extends Application {
    
    private static Stage primaryStage;
    private static Employee currentUser;
    private static CoffeeTable selectedTable;
    
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        
        FXMLLoader fxmlLoader = new FXMLLoader(CoffeeShopApplication.class.getResource("/fxml/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        
        stage.setTitle("Quản Lý Quán Cà Phê - Đăng Nhập");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.centerOnScreen();
        
        // Thêm icon cho ứng dụng
        try {
            Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/coffee-icon.png")));
            stage.getIcons().add(icon);
        } catch (Exception e) {
            System.out.println("Không thể tải icon: " + e.getMessage());
        }
        
        stage.show();
    }
    
    public static void showMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CoffeeShopApplication.class.getResource("/fxml/main.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            
            primaryStage.setTitle("Quản Lý Quán Cà Phê - " + currentUser.getName());
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải giao diện chính: " + e.getMessage());
        }
    }
    
    public static void showLoginWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(CoffeeShopApplication.class.getResource("/fxml/login.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 400, 500);
            
            primaryStage.setTitle("Quản Lý Quán Cà Phê - Đăng Nhập");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            e.printStackTrace();
            showError("Lỗi", "Không thể tải giao diện đăng nhập: " + e.getMessage());
        }
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
    
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    public static Employee getCurrentUser() {
        return currentUser;
    }
    
    public static void setCurrentUser(Employee user) {
        currentUser = user;
    }
    
    public static CoffeeTable getSelectedTable() {
        return selectedTable;
    }
    
    public static void setSelectedTable(CoffeeTable table) {
        selectedTable = table;
    }
    
    public static void logout() {
        currentUser = null;
        showLoginWindow();
    }
    
    public static void main(String[] args) {
        launch();
    }
} 