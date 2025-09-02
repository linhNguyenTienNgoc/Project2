package com.cafe;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


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
        setLoginScene();
        primaryStage.show();
    }
    
    /**
     * Hiển thị màn hình đăng nhập (static method) sau khi đăng xuất
     */
    public static void showLoginScreen() {
        try {
            setLoginScene();
        } catch (IOException e) {
            showErrorAlert("Lỗi tải giao diện", 
                          "Không thể tải giao diện đăng nhập: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Hàm private dùng chung để dựng Scene login
     */
    private static void setLoginScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(CafeManagementApplication.class.getResource(LOGIN_FXML));
        Scene scene = new Scene(fxmlLoader.load(), 400, 500);

        // Load CSS
        String css = Objects.requireNonNull(CafeManagementApplication.class.getResource("/css/login.css")).toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
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
            String css = Objects.requireNonNull(CafeManagementApplication.class.getResource("/css/dashboard.css")).toExternalForm();
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
     * Hiển thị màn hình Admin Dashboard
     */
    public static void showAdminDashboard() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(CafeManagementApplication.class.getResource("/fxml/admin/admin-dashboard.fxml"));
            
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            
            // Load CSS for admin dashboard
            String css = Objects.requireNonNull(CafeManagementApplication.class.getResource("/css/admin-dashboard.css")).toExternalForm();
            scene.getStylesheets().add(css);
            
            primaryStage.setTitle(APP_TITLE + " - Admin Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.setMaximized(true);
            primaryStage.centerOnScreen();
            
        } catch (IOException e) {
            showErrorAlert("Lỗi tải giao diện", 
                          "Không thể tải giao diện Admin: " + e.getMessage());
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
    
    /**
     * Refresh user information in current dashboard
     * Gọi method này khi thông tin user thay đổi
     */
    public static void refreshCurrentDashboardUserInfo() {
        try {
            // Lấy current scene và tìm controller
            if (primaryStage != null && primaryStage.getScene() != null) {
                // Có thể implement logic để tìm và refresh dashboard controller
                System.out.println("🔄 Refreshing dashboard user info...");
                
                // Note: Trong thực tế, có thể cần lưu reference đến current dashboard controller
                // hoặc sử dụng event system để notify dashboard controllers
            }
        } catch (Exception e) {
            System.err.println("❌ Error refreshing dashboard user info: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        launch();
    }
}