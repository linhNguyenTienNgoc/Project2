package com.cafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.Objects;

/**
 * Main Application Class for Cafe Management System
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class CafeManagementApplication extends Application {
    
    private static final String APP_TITLE = "Cafe Management System v1.0.0";
    private static final String LOGIN_FXML = "/fxml/auth/login.fxml";
    
    private static Stage primaryStage;
    
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        try {
            showLoginScene();
        } catch (Exception e) {
            showErrorAlert("Lỗi khởi động ứng dụng", 
                          "Không thể khởi động ứng dụng: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị màn hình đăng nhập
     */
    public void showLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource(LOGIN_FXML));
        
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);
        
        // Load CSS
        String css = Objects.requireNonNull(getClass().getResource("/css/login.css")).toExternalForm();
        scene.getStylesheets().add(css);
        
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }
    
    /**
     * Hiển thị màn hình chính (Dashboard)
     */
    public static void showDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(CafeManagementApplication.class.getResource("/fxml/dashboard/dashboard.fxml"));
            
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            
            // Load CSS
            String css = Objects.requireNonNull(CafeManagementApplication.class
                    .getResource("/css/dashboard.css")).toExternalForm();
            scene.getStylesheets().add(css);
            
            primaryStage.setTitle(APP_TITLE + " - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            showErrorAlert("Lỗi tải giao diện", 
                          "Không thể tải giao diện chính: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Hiển thị thông báo lỗi
     */
    public static void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Hiển thị thông báo thành công
     */
    public static void showSuccessAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Hiển thị thông báo xác nhận
     */
    public static boolean showConfirmAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get().getButtonData().isDefaultButton();
    }
    
    /**
     * Lấy Primary Stage
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Đóng ứng dụng
     */
    public static void exitApplication() {
        if (showConfirmAlert("Thoát ứng dụng", "Bạn có chắc chắn muốn thoát không?")) {
            primaryStage.close();
            System.exit(0);
        }
    }
    
    public static void main(String[] args) {
        launch();
    }
}