package com.coffeeshop.shopcoffeemanagement.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.function.Consumer;

public class TouchFriendlyController {
    
    private static TouchFriendlyController instance;
    private Stage primaryStage;
    
    public static TouchFriendlyController getInstance() {
        if (instance == null) {
            instance = new TouchFriendlyController();
        }
        return instance;
    }
    
    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
    
    /**
     * Hiển thị thông báo popup touch-friendly
     */
    public void showTouchNotification(String message, String type) {
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setAutoFix(true);
        
        VBox container = new VBox(10);
        container.setPadding(new Insets(20));
        container.setAlignment(Pos.CENTER);
        
        // Set background color based on type
        String backgroundColor;
        switch (type.toLowerCase()) {
            case "success":
                backgroundColor = "#4ecdc4";
                break;
            case "error":
                backgroundColor = "#ff6b6b";
                break;
            case "warning":
                backgroundColor = "#ffa726";
                break;
            case "info":
                backgroundColor = "#667eea";
                break;
            default:
                backgroundColor = "#667eea";
                break;
        }
        
        container.setStyle(String.format(
            "-fx-background-color: %s; -fx-background-radius: 15; -fx-border-radius: 15; " +
            "-fx-border-color: rgba(255,255,255,0.3); -fx-border-width: 2; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0, 0, 5);",
            backgroundColor
        ));
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        messageLabel.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(300);
        
        container.getChildren().add(messageLabel);
        popup.getContent().add(container);
        
        // Position popup
        if (primaryStage != null) {
            popup.show(primaryStage, 
                primaryStage.getX() + primaryStage.getWidth() / 2 - 150,
                primaryStage.getY() + primaryStage.getHeight() / 2 - 50);
        }
        
        // Auto hide after 3 seconds
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(3), container);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> popup.hide());
        fadeOut.play();
    }
    
    /**
     * Hiển thị dialog xác nhận touch-friendly
     */
    public void showTouchConfirmation(String title, String message, Consumer<Boolean> callback) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Customize buttons for touch
        ButtonType confirmButton = new ButtonType("✅ Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("❌ Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(confirmButton, cancelButton);
        
        // Style the dialog
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 15;");
        
        // Make buttons larger for touch
        Button confirmBtn = (Button) dialogPane.lookupButton(confirmButton);
        Button cancelBtn = (Button) dialogPane.lookupButton(cancelButton);
        
        if (confirmBtn != null) {
            confirmBtn.setStyle("-fx-font-size: 16; -fx-padding: 15 30; -fx-min-width: 120px; -fx-min-height: 50px;");
        }
        if (cancelBtn != null) {
            cancelBtn.setStyle("-fx-font-size: 16; -fx-padding: 15 30; -fx-min-width: 120px; -fx-min-height: 50px;");
        }
        
        alert.showAndWait().ifPresent(result -> {
            callback.accept(result == confirmButton);
        });
    }
    
    /**
     * Hiển thị input dialog touch-friendly
     */
    public void showTouchInput(String title, String message, String defaultValue, Consumer<String> callback) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        messageLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        TextField inputField = new TextField(defaultValue);
        inputField.setStyle("-fx-font-size: 16; -fx-padding: 15 20; -fx-min-height: 50px; -fx-background-radius: 10;");
        inputField.setPromptText("Nhập thông tin...");
        
        content.getChildren().addAll(messageLabel, inputField);
        dialog.getDialogPane().setContent(content);
        
        ButtonType confirmButton = new ButtonType("✅ Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("❌ Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButton, cancelButton);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButton) {
                return inputField.getText();
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(result -> {
            if (result != null && !result.trim().isEmpty()) {
                callback.accept(result);
            }
        });
    }
    
    /**
     * Tạo button touch-friendly với animation
     */
    public Button createTouchButton(String text, String styleClass, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setFont(Font.font("System", FontWeight.BOLD, 16));
        button.setMinWidth(120);
        button.setMinHeight(50);
        button.setPadding(new Insets(15, 25, 15, 25));
        
        // Add touch animations
        button.setOnMousePressed(e -> {
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(100), button);
            scaleDown.setToX(0.95);
            scaleDown.setToY(0.95);
            scaleDown.play();
        });
        
        button.setOnMouseReleased(e -> {
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(100), button);
            scaleUp.setToX(1.0);
            scaleUp.setToY(1.0);
            scaleUp.play();
            
            if (action != null) {
                action.run();
            }
        });
        
        return button;
    }
    
    /**
     * Tạo card touch-friendly với hover effects
     */
    public VBox createTouchCard(String title, String content, String icon, Runnable onClick) {
        VBox card = new VBox(10);
        card.setPadding(new Insets(20));
        card.setAlignment(Pos.CENTER);
        card.setStyle(
            "-fx-background-color: white; -fx-background-radius: 15; " +
            "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3); " +
            "-fx-cursor: hand; -fx-transition: all 0.3s ease;"
        );
        card.setMinWidth(200);
        card.setMinHeight(150);
        
        // Icon
        Label iconLabel = new Label(icon);
        iconLabel.setFont(Font.font("System", FontWeight.BOLD, 32));
        iconLabel.setStyle("-fx-text-fill: #667eea;");
        
        // Title
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setStyle("-fx-text-fill: #2c3e50; -fx-text-alignment: center;");
        titleLabel.setWrapText(true);
        
        // Content
        Label contentLabel = new Label(content);
        contentLabel.setFont(Font.font("System", 14));
        contentLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-text-alignment: center;");
        contentLabel.setWrapText(true);
        
        card.getChildren().addAll(iconLabel, titleLabel, contentLabel);
        
        // Add hover effects
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: #f8f9ff; -fx-background-radius: 15; " +
                "-fx-border-color: #667eea; -fx-border-radius: 15; -fx-border-width: 2; " +
                "-fx-effect: dropshadow(gaussian, rgba(102,126,234,0.3), 12, 0, 0, 5); " +
                "-fx-cursor: hand; -fx-transition: all 0.3s ease;"
            );
            
            ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), card);
            scaleUp.setToX(1.05);
            scaleUp.setToY(1.05);
            scaleUp.play();
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; -fx-background-radius: 15; " +
                "-fx-border-color: #e0e0e0; -fx-border-radius: 15; -fx-border-width: 1; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 8, 0, 0, 3); " +
                "-fx-cursor: hand; -fx-transition: all 0.3s ease;"
            );
            
            ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), card);
            scaleDown.setToX(1.0);
            scaleDown.setToY(1.0);
            scaleDown.play();
        });
        
        // Add click handler
        if (onClick != null) {
            card.setOnMouseClicked(e -> onClick.run());
        }
        
        return card;
    }
    
    /**
     * Hiển thị loading indicator touch-friendly
     */
    public void showTouchLoading(String message) {
        Popup popup = new Popup();
        popup.setAutoHide(false);
        
        VBox container = new VBox(15);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.setStyle(
            "-fx-background-color: rgba(0,0,0,0.8); -fx-background-radius: 15; " +
            "-fx-border-color: rgba(255,255,255,0.3); -fx-border-radius: 15; -fx-border-width: 1;"
        );
        
        // Loading spinner
        ProgressIndicator spinner = new ProgressIndicator();
        spinner.setStyle("-fx-progress-color: white;");
        
        // Message
        Label messageLabel = new Label(message);
        messageLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        messageLabel.setStyle("-fx-text-fill: white; -fx-text-alignment: center;");
        
        container.getChildren().addAll(spinner, messageLabel);
        popup.getContent().add(container);
        
        // Position and show
        if (primaryStage != null) {
            popup.show(primaryStage, 
                primaryStage.getX() + primaryStage.getWidth() / 2 - 100,
                primaryStage.getY() + primaryStage.getHeight() / 2 - 50);
        }
        
        // Store popup reference for hiding later
        popup.setUserData(popup);
    }
    
    /**
     * Ẩn loading indicator
     */
    public void hideTouchLoading() {
        // Implementation would store popup reference and hide it
        // For now, this is a placeholder
    }
    
    /**
     * Tạo swipe gesture handler
     */
    public void addSwipeGesture(Region region, Consumer<String> swipeHandler) {
        final double[] startX = {0};
        final double[] startY = {0};
        
        region.setOnMousePressed(e -> {
            startX[0] = e.getSceneX();
            startY[0] = e.getSceneY();
        });
        
        region.setOnMouseReleased(e -> {
            double deltaX = e.getSceneX() - startX[0];
            double deltaY = e.getSceneY() - startY[0];
            double minSwipeDistance = 50;
            
            if (Math.abs(deltaX) > Math.abs(deltaY) && Math.abs(deltaX) > minSwipeDistance) {
                if (deltaX > 0) {
                    swipeHandler.accept("right");
                } else {
                    swipeHandler.accept("left");
                }
            } else if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > minSwipeDistance) {
                if (deltaY > 0) {
                    swipeHandler.accept("down");
                } else {
                    swipeHandler.accept("up");
                }
            }
        });
    }
} 