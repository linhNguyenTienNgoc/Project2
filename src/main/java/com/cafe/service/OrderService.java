package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;

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
            this.orderDAO = new OrderDAOImpl(DatabaseConfig.getInstance().getConnection());
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
        
        // Tạo order detail
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(order.getOrderId());
        detail.setProductId(product.getProductId());
        detail.setQuantity(quantity);
        detail.setUnitPrice(product.getPrice());
        detail.setTotalPrice(product.getPrice() * quantity);
        
        try {
            // Lưu order detail
            // Note: Cần implement OrderDetailDAO
            // orderDetailDAO.save(detail);
            
            // Cập nhật total amount của order
            updateOrderTotal(order);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error adding product to order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa product khỏi order
     */
    public boolean removeProductFromOrder(Order order, Integer productId) {
        if (order == null || productId == null) {
            return false;
        }
        
        try {
            // Xóa order detail
            // Note: Cần implement OrderDetailDAO
            // orderDetailDAO.deleteByOrderAndProduct(order.getOrderId(), productId);
            
            // Cập nhật total amount của order
            updateOrderTotal(order);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error removing product from order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật quantity của product trong order
     */
    public boolean updateProductQuantity(Order order, Integer productId, int newQuantity) {
        if (order == null || productId == null || newQuantity < 0) {
            return false;
        }
        
        if (newQuantity == 0) {
            return removeProductFromOrder(order, productId);
        }
        
        // Kiểm tra stock
        Optional<Product> productOpt = menuService.getProductById(productId);
        if (productOpt.isEmpty() || !menuService.canOrderProduct(productOpt.get(), newQuantity)) {
            return false;
        }
        
        try {
            // Cập nhật order detail
            // Note: Cần implement OrderDetailDAO
            // orderDetailDAO.updateQuantity(order.getOrderId(), productId, newQuantity);
            
            // Cập nhật total amount của order
            updateOrderTotal(order);
            
            return true;
        } catch (Exception e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Tính toán total amount của order
     */
    private void updateOrderTotal(Order order) {
        try {
            // Lấy tất cả order details
            // List<OrderDetail> details = orderDetailDAO.findByOrderId(order.getOrderId());
            
            // Tính tổng
            // double total = details.stream()
            //     .mapToDouble(detail -> detail.getTotalPrice() != null ? detail.getTotalPrice() : 0.0)
            //     .sum();
            
            // Cập nhật order
            // order.setTotalAmount(total);
            // order.calculateFinalAmount();
            // orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("Error updating order total: " + e.getMessage());
        }
    }
    
    /**
     * Place order (xác nhận order)
     */
    public boolean placeOrder(Order order) {
        if (order == null || order.getOrderId() == null) {
            return false;
        }
        
        try {
            order.setOrderStatus("confirmed");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Lưu order
            orderDAO.update(order);
            
            // Cập nhật stock cho tất cả products trong order
            // updateStockForOrder(order);
            
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
        if (order == null || order.getOrderId() == null) {
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
        if (order == null || order.getOrderId() == null) {
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
        if (order == null || order.getOrderId() == null) {
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
     * Lấy orders theo table
     */
    public List<Order> getOrdersByTable(Integer tableId) {
        try {
            // Note: Cần implement method này trong OrderDAO
            // return orderDAO.findByTableId(tableId);
            return List.of();
        } catch (Exception e) {
            System.err.println("Error getting orders by table " + tableId + ": " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy pending orders
     */
    public List<Order> getPendingOrders() {
        try {
            // Note: Cần implement method này trong OrderDAO
            // return orderDAO.findByStatus("pending");
            return List.of();
        } catch (Exception e) {
            System.err.println("Error getting pending orders: " + e.getMessage());
            return List.of();
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
                    return (product.getPrice() != null ? product.getPrice() : 0.0) * quantity;
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
