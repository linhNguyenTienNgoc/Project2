package com.cafe.util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertUtils {

    /**
     * Show error alert
     */
    public static void showError(String title, String message) {
        showError(null, title, message);
    }

    public static void showError(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText("Đã xảy ra lỗi");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show information alert
     */
    public static void showInfo(String title, String message) {
        showInfo(null, title, message);
    }

    public static void showInfo(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    /**
     * Show warning alert
     */
    public static void showWarning(String title, String message) {
        showWarning(null, title, message);
    }

    public static void showWarning(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText("Cảnh báo");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Show confirmation alert
     */
    public static boolean showConfirmation(String title, String message) {
        return showConfirmation(null, title, message);
    }

    public static boolean showConfirmation(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText("Xác nhận");
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Show success alert
     */
    public static void showSuccess(String title, String message) {
        showSuccess(null, title, message);
    }

    public static void showSuccess(Stage owner, String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.setTitle(title);
        alert.setHeaderText("Thành công");
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    /**
     * Show confirm alert (alias for showConfirmation)
     */
    public static boolean showConfirm(String title, String message) {
        return showConfirmation(title, message);
    }
    
    /**
     * Show text input dialog
     */
    public static String showTextInput(String title, String headerText, String promptText) {
        return showTextInput(null, title, headerText, promptText);
    }
    
    public static String showTextInput(Stage owner, String title, String headerText, String promptText) {
        TextInputDialog dialog = new TextInputDialog();
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.setTitle(title);
        dialog.setHeaderText(headerText);
        dialog.setContentText(promptText);
        
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }
}
