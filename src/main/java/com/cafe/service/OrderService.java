package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.Product;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class cho order operations
 * Chứa business logic cho việc quản lý orders và order details
 */
public class OrderService {
    
    private final OrderDAO orderDAO;
    private final MenuService menuService;
    
    public OrderService() {
        try {
            // Sử dụng connection pool, nhưng không giữ connection lâu
            this.orderDAO = new OrderDAOImpl(DatabaseConfig.getConnection());
            this.menuService = new MenuService();
        } catch (Exception e) {
            System.err.println("Error initializing OrderService: " + e.getMessage());
            throw new RuntimeException("Failed to initialize OrderService", e);
        }
    }
    
    /**
     * Tạo order mới
     */
    public Order createOrder(Integer tableId, Integer userId) {
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setTableId(tableId);
        order.setUserId(userId);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setOrderStatus("pending");
        order.setPaymentStatus("pending");
        order.setTotalAmount(0.0);
        order.setDiscountAmount(0.0);
        order.setFinalAmount(0.0);
        
        try {
            if (orderDAO.save(order)) {
                return order;
            } else {
                System.err.println("Failed to save order");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Error creating order: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Thêm product vào order
     */
    public boolean addProductToOrder(Order order, Product product, int quantity) {
        if (order == null || product == null || quantity <= 0) {
            return false;
        }
        
        // Kiểm tra stock
        if (!menuService.canOrderProduct(product, quantity)) {
            return false;
        }
        
        try {
            // Update order total amount
            double itemTotal = product.getPrice() * quantity;
            order.setTotalAmount(order.getTotalAmount() + itemTotal);
            order.calculateFinalAmount();
            
            // Update order in database
            orderDAO.update(order);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error adding product to order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Place order (xác nhận order)
     */
    public boolean placeOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        try {
            order.setOrderStatus("confirmed");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Lưu order
            orderDAO.update(order);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cancel order
     */
    public boolean cancelOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBeCancelled()) {
            return false;
        }
        
        try {
            order.setOrderStatus("cancelled");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Complete order
     */
    public boolean completeOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBeCompleted()) {
            return false;
        }
        
        try {
            order.setOrderStatus("completed");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error completing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process payment
     */
    public boolean processPayment(Order order, String paymentMethod) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBePaid()) {
            return false;
        }
        
        try {
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("paid");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy order theo ID
     */
    public Optional<Order> getOrderById(Integer orderId) {
        try {
            return orderDAO.findById(orderId);
        } catch (Exception e) {
            System.err.println("Error getting order by ID " + orderId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Generate order number
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }
    
    /**
     * Tính total amount từ list products
     */
    public double calculateTotalAmount(Map<Product, Integer> productQuantities) {
        return productQuantities.entrySet().stream()
                .mapToDouble(entry -> {
                    Product product = entry.getKey();
                    Integer quantity = entry.getValue();
                    return product.getPrice() * quantity;
                })
                .sum();
    }
    
    /**
     * Format total amount
     */
    public String formatTotalAmount(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }
}
