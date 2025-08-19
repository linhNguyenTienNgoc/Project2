package com.cafe.util;

import com.cafe.model.entity.User;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.util.Callback;

/**
 * Helper class cho việc tạo custom cell factories cho User Table
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class UserTableHelper {

    /**
     * Tạo cell factory cho cột status với màu sắc phù hợp
     */
    public static Callback<TableColumn<User, String>, TableCell<User, String>> createStatusCellFactory() {
        return column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    setText(status);
                    
                    // Apply styling based on status
                    if ("ACTIVE".equals(status)) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-weight: bold;");
                        setText("✅ " + status);
                    } else {
                        setStyle("-fx-text-fill: #dc3545; -fx-font-weight: bold;");
                        setText("❌ " + status);
                    }
                }
            }
        };
    }

    /**
     * Tạo cell factory cho cột role với màu sắc và icon phù hợp
     */
    public static Callback<TableColumn<User, String>, TableCell<User, String>> createRoleCellFactory() {
        return column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String role, boolean empty) {
                super.updateItem(role, empty);
                
                if (empty || role == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    String displayText = getRoleDisplayText(role);
                    setText(displayText);
                    setStyle(getRoleStyle(role));
                }
            }
            
            private String getRoleDisplayText(String role) {
                switch (role.toUpperCase()) {
                    case "ADMIN": return "👑 Admin";
                    case "MANAGER": return "👨‍💼 Manager";
                    case "STAFF": return "👥 Staff";
                    case "CASHIER": return "💰 Cashier";
                    case "WAITER": return "🍽️ Waiter";
                    case "BARISTA": return "☕ Barista";
                    default: return role;
                }
            }
            
            private String getRoleStyle(String role) {
                switch (role.toUpperCase()) {
                    case "ADMIN": return "-fx-text-fill: #dc3545; -fx-font-weight: bold;";
                    case "MANAGER": return "-fx-text-fill: #fd7e14; -fx-font-weight: bold;";
                    case "STAFF": return "-fx-text-fill: #17a2b8; -fx-font-weight: bold;";
                    case "CASHIER": return "-fx-text-fill: #28a745; -fx-font-weight: bold;";
                    case "WAITER": return "-fx-text-fill: #6f42c1; -fx-font-weight: bold;";
                    case "BARISTA": return "-fx-text-fill: #e83e8c; -fx-font-weight: bold;";
                    default: return "-fx-text-fill: #6c757d; -fx-font-weight: normal;";
                }
            }
        };
    }

    /**
     * Tạo cell factory cho cột ID với formatting đặc biệt
     */
    public static Callback<TableColumn<User, Integer>, TableCell<User, Integer>> createIdCellFactory() {
        return column -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer id, boolean empty) {
                super.updateItem(id, empty);
                
                if (empty || id == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText("#" + String.format("%04d", id));
                    setStyle("-fx-font-family: monospace; -fx-text-fill: #6c757d;");
                }
            }
        };
    }

    /**
     * Tạo cell factory cho cột email với validation visual
     */
    public static Callback<TableColumn<User, String>, TableCell<User, String>> createEmailCellFactory() {
        return column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String email, boolean empty) {
                super.updateItem(email, empty);
                
                if (empty || email == null || email.trim().isEmpty()) {
                    setText("❌ Chưa có");
                    setStyle("-fx-text-fill: #dc3545; -fx-font-style: italic;");
                } else {
                    setText(email);
                    if (ValidationUtils.isValidEmail(email)) {
                        setStyle("-fx-text-fill: #28a745;");
                    } else {
                        setStyle("-fx-text-fill: #ffc107;");
                        setTooltip(new Tooltip("Email không hợp lệ"));
                    }
                }
            }
        };
    }

    /**
     * Tạo cell factory cho cột phone với formatting
     */
    public static Callback<TableColumn<User, String>, TableCell<User, String>> createPhoneCellFactory() {
        return column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String phone, boolean empty) {
                super.updateItem(phone, empty);
                
                if (empty || phone == null || phone.trim().isEmpty()) {
                    setText("❌ Chưa có");
                    setStyle("-fx-text-fill: #dc3545; -fx-font-style: italic;");
                } else {
                    setText(formatPhoneNumber(phone));
                    if (ValidationUtils.isValidPhone(phone)) {
                        setStyle("-fx-text-fill: #28a745; -fx-font-family: monospace;");
                    } else {
                        setStyle("-fx-text-fill: #ffc107; -fx-font-family: monospace;");
                        setTooltip(new Tooltip("Số điện thoại không hợp lệ"));
                    }
                }
            }
            
            private String formatPhoneNumber(String phone) {
                // Simple formatting for Vietnamese phone numbers
                if (phone != null && phone.length() >= 10) {
                    String cleaned = phone.replaceAll("[^0-9]", "");
                    if (cleaned.length() == 10) {
                        return cleaned.substring(0, 4) + "-" + 
                               cleaned.substring(4, 7) + "-" + 
                               cleaned.substring(7);
                    }
                }
                return phone;
            }
        };
    }

    /**
     * Tạo checkbox cell factory cho selection column
     */
    public static Callback<TableColumn<User, Boolean>, TableCell<User, Boolean>> createSelectionCellFactory() {
        return column -> new TableCell<User, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            
            {
                checkBox.setOnAction(event -> {
                    User user = getTableView().getItems().get(getIndex());
                    // Handle selection logic here if needed
                    System.out.println("Selected user: " + user.getUsername());
                });
            }
            
            @Override
            protected void updateItem(Boolean selected, boolean empty) {
                super.updateItem(selected, empty);
                
                if (empty) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(selected != null && selected);
                    setGraphic(checkBox);
                    setAlignment(Pos.CENTER);
                }
            }
        };
    }

    /**
     * Tạo tooltip factory cho rows
     */
    public static Callback<TableRow<User>, Tooltip> createRowTooltipFactory() {
        return row -> {
            if (row.getItem() != null) {
                User user = row.getItem();
                StringBuilder tooltipText = new StringBuilder();
                tooltipText.append("👤 ").append(user.getFullName()).append("\n");
                tooltipText.append("🔑 ").append(user.getUsername()).append("\n");
                tooltipText.append("📧 ").append(user.getEmail() != null ? user.getEmail() : "Chưa có").append("\n");
                tooltipText.append("📱 ").append(user.getPhone() != null ? user.getPhone() : "Chưa có").append("\n");
                tooltipText.append("🎭 ").append(user.getRole()).append("\n");
                tooltipText.append("📊 ").append(user.isActive() ? "Đang hoạt động" : "Không hoạt động");
                
                Tooltip tooltip = new Tooltip(tooltipText.toString());
                tooltip.setStyle("-fx-font-size: 12px; -fx-max-width: 300px; -fx-wrap-text: true;");
                return tooltip;
            }
            return null;
        };
    }

    /**
     * Tạo custom row factory với conditional styling
     */
    public static Callback<TableView<User>, TableRow<User>> createRowFactory() {
        return tableView -> {
            TableRow<User> row = new TableRow<User>() {
                @Override
                protected void updateItem(User user, boolean empty) {
                    super.updateItem(user, empty);
                    
                    if (empty || user == null) {
                        setStyle("");
                        setTooltip(null);
                    } else {
                        // Apply row styling based on user status
                        if (!user.isActive()) {
                            setStyle("-fx-background-color: rgba(220, 53, 69, 0.1); -fx-text-fill: #6c757d;");
                        } else if ("ADMIN".equals(user.getRole())) {
                            setStyle("-fx-background-color: rgba(220, 53, 69, 0.05);");
                        } else {
                            setStyle("");
                        }
                        
                        // Set tooltip
                        Tooltip tooltip = createRowTooltipFactory().call(this);
                        setTooltip(tooltip);
                    }
                }
            };
            
            // Double-click handler
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    User user = row.getItem();
                    System.out.println("Double-clicked user: " + user.getUsername());
                    // Trigger edit action
                }
            });
            
            return row;
        };
    }

    /**
     * Utility method để tạo styled label
     */
    public static Label createStyledLabel(String text, String style) {
        Label label = new Label(text);
        label.setStyle(style);
        return label;
    }

    /**
     * Utility method để tạo action button
     */
    public static Button createActionButton(String text, String style, Runnable action) {
        Button button = new Button(text);
        button.setStyle(style);
        button.setOnAction(e -> action.run());
        return button;
    }
}
