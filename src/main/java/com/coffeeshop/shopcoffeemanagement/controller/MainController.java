package com.coffeeshop.shopcoffeemanagement.controller;

import com.coffeeshop.shopcoffeemanagement.CoffeeShopApplication;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainController {
    
    @FXML
    private Label userLabel;
    
    @FXML
    private StackPane contentArea;
    
    @FXML
    public void initialize() {
        // Hiển thị thông tin người dùng
        Employee currentUser = CoffeeShopApplication.getCurrentUser();
        if (currentUser != null) {
            userLabel.setText("Nhân viên: " + currentUser.getName() + " (" + currentUser.getRole() + ")");
        }
    }
    
    @FXML
    private void handleLogout() {
        CoffeeShopApplication.showConfirmation("Đăng xuất", "Bạn có chắc chắn muốn đăng xuất?", () -> {
            CoffeeShopApplication.logout();
        });
    }
    
    @FXML
    private void showDashboard() {
        loadContent("/fxml/dashboard.fxml");
    }
    
    @FXML
    private void showTables() {
        loadContent("/fxml/tables.fxml");
    }
    
    @FXML
    private void showOrders() {
        loadContent("/fxml/order.fxml");
    }
    
    @FXML
    private void showMenu() {
        loadContent("/fxml/menu.fxml");
    }
    
    @FXML
    private void showInvoices() {
        loadContent("/fxml/invoices.fxml");
    }
    
    @FXML
    private void showReports() {
        loadContent("/fxml/reports.fxml");
    }
    
    @FXML
    private void showSettings() {
        loadContent("/fxml/settings.fxml");
    }
    
    private void loadContent(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node content = loader.load();
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);
        } catch (IOException e) {
            e.printStackTrace();
            CoffeeShopApplication.showError("Lỗi", "Không thể tải giao diện: " + e.getMessage());
        }
    }
} 