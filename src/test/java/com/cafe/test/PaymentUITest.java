package com.cafe.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import com.cafe.controller.payment.PaymentController;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Test class để debug giao diện thanh toán
 * Kiểm tra việc load FXML và hiển thị payment window
 */
public class PaymentUITest extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            System.out.println("🔧 PaymentUITest started");
            
            // Test 1: Kiểm tra resource path
            testResourcePaths();
            
            // Test 2: Load FXML
            testFXMLLoading();
            
            // Test 3: Tạo mock order data và test giao diện
            testPaymentUIWithMockData(primaryStage);
            
        } catch (Exception e) {
            System.err.println("❌ PaymentUITest failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 1: Kiểm tra các resource path có tồn tại không
     */
    private void testResourcePaths() {
        System.out.println("\n🔍 Testing resource paths...");
        
        try {
            // Test FXML path
            java.net.URL fxmlUrl = getClass().getResource("/fxml/payment/payment.fxml");
            if (fxmlUrl == null) {
                System.err.println("❌ FXML not found: /fxml/payment/payment.fxml");
            } else {
                System.out.println("✅ FXML found: " + fxmlUrl);
            }
            
            // Test CSS path
            java.net.URL cssUrl = getClass().getResource("/css/payment.css");
            if (cssUrl == null) {
                System.err.println("❌ CSS not found: /css/payment.css");
            } else {
                System.out.println("✅ CSS found: " + cssUrl);
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error testing resource paths: " + e.getMessage());
        }
    }
    
    /**
     * Test 2: Kiểm tra việc load FXML
     */
    private void testFXMLLoading() {
        System.out.println("\n🔍 Testing FXML loading...");
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment/payment.fxml"));
            BorderPane root = loader.load();
            System.out.println("✅ FXML loaded successfully");
            
            PaymentController controller = loader.getController();
            if (controller == null) {
                System.err.println("❌ PaymentController is null");
            } else {
                System.out.println("✅ PaymentController obtained: " + controller.getClass().getName());
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test 3: Test giao diện payment với mock data
     */
    private void testPaymentUIWithMockData(Stage primaryStage) {
        System.out.println("\n🔍 Testing Payment UI with mock data...");
        
        try {
            // Tạo mock order
            Order mockOrder = createMockOrder();
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/payment/payment.fxml"));
            BorderPane paymentRoot = loader.load();
            
            // Get controller và init data
            PaymentController paymentController = loader.getController();
            paymentController.initData(mockOrder, 5, 10.0, 5.0);
            
            // Tạo scene và hiển thị
            Scene scene = new Scene(paymentRoot, 950, 750);
            
            // Load CSS
            try {
                scene.getStylesheets().add(getClass().getResource("/css/payment.css").toExternalForm());
                System.out.println("✅ CSS loaded successfully");
            } catch (Exception e) {
                System.err.println("⚠️ Warning: Could not load CSS: " + e.getMessage());
            }
            
            primaryStage.setTitle("Payment UI Test");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(700);
            primaryStage.show();
            
            System.out.println("✅ Payment UI displayed successfully");
            
        } catch (Exception e) {
            System.err.println("❌ Error testing Payment UI: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Tạo mock order data để test
     */
    private Order createMockOrder() {
        Order order = new Order();
        order.setOrderId(1);
        order.setOrderNumber("ORD-TEST-001");
        order.setTableId(5);
        order.setUserId(1);
        order.setOrderDate(Timestamp.valueOf(LocalDateTime.now()));
        order.setOrderStatus("CONFIRMED");
        order.setPaymentStatus("PENDING");
        order.setTotalAmount(150000.0);
        order.setFinalAmount(150000.0);
        order.setNotes("Test order for UI debugging");
        
        System.out.println("✅ Created mock order: " + order.getOrderNumber());
        return order;
    }
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting PaymentUITest...");
        launch(args);
    }
}
