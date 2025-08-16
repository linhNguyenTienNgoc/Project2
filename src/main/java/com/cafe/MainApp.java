package com.cafe;

import com.cafe.config.DatabaseConfig;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main application class
 * Entry point cho Cafe Management System
 */
public class MainApp extends Application {

    private static final String APP_TITLE = "Cafe Management System";
    private static final double MIN_WIDTH = 1200;
    private static final double MIN_HEIGHT = 800;
    private static final double DEFAULT_WIDTH = 1400;
    private static final double DEFAULT_HEIGHT = 900;

    @Override
    public void start(Stage primaryStage) {
        try {
            // Test database connection before starting UI
            testDatabaseConnection();

            // Load main dashboard FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            // Create scene
            Scene scene = new Scene(root, DEFAULT_WIDTH, DEFAULT_HEIGHT);

            // Load CSS styles
            loadStyles(scene);

            // Configure primary stage
            setupPrimaryStage(primaryStage, scene);

            // Show the stage
            primaryStage.show();

            System.out.println("✅ Application started successfully");

        } catch (Exception e) {
            System.err.println("❌ Error starting application: " + e.getMessage());
            e.printStackTrace();

            // Show error dialog and exit
            showErrorAndExit("Không thể khởi động ứng dụng: " + e.getMessage());
        }
    }

    /**
     * Test database connection
     */
    private void testDatabaseConnection() {
        try {
            DatabaseConfig dbConfig = DatabaseConfig.getInstance();
            boolean connected = dbConfig.testConnection();

            if (connected) {
                System.out.println("✅ Database connection successful");
                System.out.println("📊 " + dbConfig.getDatabaseInfo());
                System.out.println("🏊 " + dbConfig.getPoolInfo());
            } else {
                throw new RuntimeException("Cannot connect to database");
            }

        } catch (Exception e) {
            throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
        }
    }

    /**
     * Load CSS styles
     */
    private void loadStyles(Scene scene) {
        try {
            // Main CSS file
            String cssFile = getClass().getResource("/css/dashboard.css").toExternalForm();
            scene.getStylesheets().add(cssFile);

            System.out.println("✅ CSS styles loaded successfully");

        } catch (Exception e) {
            System.err.println("⚠️ Warning: Could not load CSS styles: " + e.getMessage());
            // Continue without styles
        }
    }

    /**
     * Setup primary stage
     */
    private void setupPrimaryStage(Stage primaryStage, Scene scene) {
        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(MIN_WIDTH);
        primaryStage.setMinHeight(MIN_HEIGHT);

        // Handle close request
        primaryStage.setOnCloseRequest(e -> {
            handleApplicationExit();
        });

        // Center on screen
        primaryStage.centerOnScreen();
    }

    /**
     * Handle application exit
     */
    private void handleApplicationExit() {
        try {
            System.out.println("🔄 Shutting down application...");

            // Close database connections
            DatabaseConfig.closePool();
            System.out.println("✅ Database connections closed");

            // Exit application
            Platform.exit();
            System.exit(0);

        } catch (Exception e) {
            System.err.println("❌ Error during shutdown: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Show error and exit application
     */
    private void showErrorAndExit(String message) {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("Lỗi khởi động");
            alert.setHeaderText("Ứng dụng không thể khởi động");
            alert.setContentText(message);
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Cannot show error dialog: " + e.getMessage());
        }

        System.exit(1);
    }

    /**
     * Main method
     */
    public static void main(String[] args) {
        // Set system properties for better JavaFX performance
        System.setProperty("javafx.preloader", "com.cafe.SplashScreenPreloader");
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");

        // Launch JavaFX application
        launch(args);
    }
}