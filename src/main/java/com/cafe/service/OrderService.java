package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class cho order operations
 * Chứa business logic cho việc quản lý orders và order details
 * 
 * Flow xử lý đơn hàng:
 * 1. Khách vào bàn → createOrder() (pending)
 * 2. Thêm món → addProductToOrder() (kiểm tra stock)
 * 3. Xác nhận đơn → placeOrder() (preparing)
 * 4. Hoàn thành → completeOrder() (completed)
 * 5. Thanh toán → processPayment() (paid)
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class OrderService {
    
    private final OrderDAO orderDAO;
    private final MenuService menuService;
    
    public OrderService() {
        try {
            this.orderDAO = new OrderDAOImpl(DatabaseConfig.getConnection());
            this.menuService = new MenuService();
        } catch (Exception e) {
            System.err.println("Error initializing OrderService: " + e.getMessage());
            throw new RuntimeException("Failed to initialize OrderService", e);
        }
    }
    
    /**
     * Tạo order mới khi khách vào bàn
     * @param tableId ID của bàn
     * @param userId ID của nhân viên tạo order
     * @param customerId ID của khách hàng (có thể null)
     * @return Order mới được tạo hoặc null nếu lỗi
     */
    public Order createOrder(Integer tableId, Integer userId, Integer customerId) {
        if (tableId == null || userId == null) {
            System.err.println("TableId and UserId cannot be null");
            return null;
        }
        
        Order order = new Order();
        order.setOrderNumber(generateOrderNumber());
        order.setTableId(tableId);
        order.setUserId(userId);
        order.setCustomerId(customerId);
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));
        order.setOrderStatus("pending");
        order.setPaymentStatus("pending");
        order.setTotalAmount(0.0);
        order.setDiscountAmount(0.0);
        order.setFinalAmount(0.0);
        order.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        try {
            if (orderDAO.save(order)) {
                System.out.println("Created new order: " + order.getOrderNumber());
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
     * @param order Order cần thêm sản phẩm
     * @param product Sản phẩm cần thêm
     * @param quantity Số lượng
     * @param notes Ghi chú cho sản phẩm (có thể null)
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addProductToOrder(Order order, Product product, int quantity, String notes) {
        if (order == null || product == null || quantity <= 0) {
            System.err.println("Invalid parameters for addProductToOrder");
            return false;
        }
        
        // Kiểm tra trạng thái order
        if (!"pending".equals(order.getOrderStatus())) {
            System.err.println("Cannot add product to order with status: " + order.getOrderStatus());
            return false;
        }
        
        // Kiểm tra stock
        if (!menuService.canOrderProduct(product, quantity)) {
            System.err.println("Insufficient stock for product: " + product.getProductName());
            return false;
        }
        
        try {
            // Tạo OrderDetail
            OrderDetail orderDetail = new OrderDetail(
                order.getOrderId(), 
                product.getProductId(), 
                quantity, 
                product.getPrice(), 
                notes
            );
            
            // Cập nhật order total amount
            double itemTotal = product.getPrice() * quantity;
            order.setTotalAmount(order.getTotalAmount() + itemTotal);
            order.calculateFinalAmount();
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            // Lưu order detail và update order
            // TODO: Implement OrderDetailDAO.save(orderDetail)
            orderDAO.update(order);
            
            System.out.println("Added " + quantity + "x " + product.getProductName() + " to order " + order.getOrderNumber());
            return true;
        } catch (Exception e) {
            System.err.println("Error adding product to order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Thêm product vào order (overload method không có notes)
     * @param order Order cần thêm sản phẩm
     * @param product Sản phẩm cần thêm
     * @param quantity Số lượng
     * @return true nếu thêm thành công, false nếu thất bại
     */
    public boolean addProductToOrder(Order order, Product product, int quantity) {
        return addProductToOrder(order, product, quantity, null);
    }
    
    /**
     * Xóa sản phẩm khỏi order
     * @param order Order cần xóa sản phẩm
     * @param productId ID sản phẩm cần xóa
     * @return true nếu xóa thành công, false nếu thất bại
     */
    public boolean removeProductFromOrder(Order order, int productId) {
        if (order == null || !"pending".equals(order.getOrderStatus())) {
            return false;
        }
        
        try {
            // TODO: Implement OrderDetailDAO.deleteByOrderAndProduct(order.getOrderId(), productId)
            // TODO: Recalculate order total amount
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error removing product from order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật số lượng sản phẩm trong order
     * @param order Order cần cập nhật
     * @param productId ID sản phẩm
     * @param newQuantity Số lượng mới
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateProductQuantity(Order order, int productId, int newQuantity) {
        if (order == null || newQuantity <= 0 || !"pending".equals(order.getOrderStatus())) {
            return false;
        }
        
        try {
            // TODO: Implement OrderDetailDAO.updateQuantity(order.getOrderId(), productId, newQuantity)
            // TODO: Recalculate order total amount
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating product quantity: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Place order (xác nhận đơn hàng)
     * @param order Order cần xác nhận
     * @return true nếu xác nhận thành công, false nếu thất bại
     */
    public boolean placeOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        if (!"pending".equals(order.getOrderStatus())) {
            System.err.println("Cannot place order with status: " + order.getOrderStatus());
            return false;
        }
        
        if (order.getTotalAmount() <= 0) {
            System.err.println("Cannot place order with zero total amount");
            return false;
        }
        
        try {
            order.setOrderStatus("preparing");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            System.out.println("Order " + order.getOrderNumber() + " has been confirmed");
            return true;
        } catch (Exception e) {
            System.err.println("Error placing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chuyển order sang trạng thái preparing (đang chuẩn bị)
     * @param order Order cần chuyển trạng thái
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean startPreparing(Order order) {
        if (order == null || !"preparing".equals(order.getOrderStatus())) {
            return false;
        }
        
        try {
            order.setOrderStatus("ready");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error starting preparation: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chuyển order sang trạng thái served (đã phục vụ)
     * @param order Order cần chuyển trạng thái
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean markAsServed(Order order) {
        if (order == null || !"ready".equals(order.getOrderStatus())) {
            return false;
        }
        
        try {
            order.setOrderStatus("served");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            orderDAO.update(order);
            return true;
        } catch (Exception e) {
            System.err.println("Error marking as served: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Complete order (hoàn thành đơn hàng)
     * @param order Order cần hoàn thành
     * @return true nếu hoàn thành thành công, false nếu thất bại
     */
    public boolean completeOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBeCompleted()) {
            System.err.println("Cannot complete order with status: " + order.getOrderStatus());
            return false;
        }
        
        try {
            order.setOrderStatus("completed");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            System.out.println("Order " + order.getOrderNumber() + " has been completed");
            return true;
        } catch (Exception e) {
            System.err.println("Error completing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cancel order (hủy đơn hàng)
     * @param order Order cần hủy
     * @param reason Lý do hủy (có thể null)
     * @return true nếu hủy thành công, false nếu thất bại
     */
    public boolean cancelOrder(Order order, String reason) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBeCancelled()) {
            System.err.println("Cannot cancel order with status: " + order.getOrderStatus());
            return false;
        }
        
        try {
            order.setOrderStatus("cancelled");
            order.setNotes(reason != null ? reason : "Order cancelled");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            System.out.println("Order " + order.getOrderNumber() + " has been cancelled");
            return true;
        } catch (Exception e) {
            System.err.println("Error cancelling order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Process payment (xử lý thanh toán)
     * @param order Order cần thanh toán
     * @param paymentMethod Phương thức thanh toán
     * @param amountReceived Số tiền khách đưa
     * @return true nếu thanh toán thành công, false nếu thất bại
     */
    public boolean processPayment(Order order, String paymentMethod, double amountReceived) {
        if (order == null) {
            return false;
        }
        
        if (!order.canBePaid()) {
            System.err.println("Cannot process payment for order with status: " + order.getOrderStatus());
            return false;
        }
        
        if (amountReceived < order.getFinalAmount()) {
            System.err.println("Insufficient payment amount");
            return false;
        }
        
        try {
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("paid");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            orderDAO.update(order);
            System.out.println("Payment processed for order " + order.getOrderNumber() + 
                             ". Amount: " + formatTotalAmount(order.getFinalAmount()) +
                             ", Method: " + paymentMethod);
            return true;
        } catch (Exception e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy order theo ID
     * @param orderId ID của order
     * @return Optional<Order> chứa order nếu tìm thấy
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
     * Lấy tất cả orders theo trạng thái
     * @param status Trạng thái cần lọc
     * @return List<Order> danh sách orders
     */
    public List<Order> getOrdersByStatus(String status) {
        try {
            // TODO: Implement orderDAO.findByStatus(status)
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error getting orders by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy orders theo bàn
     * @param tableId ID của bàn
     * @return List<Order> danh sách orders
     */
    public List<Order> getOrdersByTable(int tableId) {
        try {
            // TODO: Implement orderDAO.findByTable(tableId)
            return new ArrayList<>();
        } catch (Exception e) {
            System.err.println("Error getting orders by table: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy order đang active (pending/preparing/ready/served) theo bàn
     * @param tableId ID của bàn
     * @return Optional<Order> order đang active
     */
    public Optional<Order> getActiveOrderByTable(int tableId) {
        try {
            // TODO: Implement orderDAO.findActiveByTable(tableId)
            return Optional.empty();
        } catch (Exception e) {
            System.err.println("Error getting active order by table: " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Generate order number
     * @return String order number
     */
    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }
    
    /**
     * Tính total amount từ list products
     * @param productQuantities Map<Product, Integer> sản phẩm và số lượng
     * @return double tổng tiền
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
     * @param amount Số tiền cần format
     * @return String đã format
     */
    public String formatTotalAmount(double amount) {
        return String.format("%,.0f VNĐ", amount);
    }
    
    /**
     * Tính tiền thối cho khách
     * @param order Order cần tính
     * @param amountReceived Số tiền khách đưa
     * @return double số tiền thối
     */
    public double calculateChange(Order order, double amountReceived) {
        if (order == null || amountReceived < order.getFinalAmount()) {
            return 0.0;
        }
        return amountReceived - order.getFinalAmount();
    }
    
    /**
     * Kiểm tra xem order có thể chỉnh sửa không
     * @param order Order cần kiểm tra
     * @return true nếu có thể chỉnh sửa
     */
    public boolean canModifyOrder(Order order) {
        return order != null && "pending".equals(order.getOrderStatus());
    }
    
    /**
     * Kiểm tra xem order có thể thanh toán không
     * @param order Order cần kiểm tra
     * @return true nếu có thể thanh toán
     */
    public boolean canPayOrder(Order order) {
        return order != null && order.canBePaid();
    }

    /**
     * Update order in database
     * @param order Order cần cập nhật
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateOrder(Order order) {
        if (order == null) {
            return false;
        }
        
        try {
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("Error updating order: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update order status
     * @param order Order cần cập nhật status
     * @return true nếu cập nhật thành công, false nếu thất bại
     */
    public boolean updateOrderStatus(Order order) {
        return updateOrder(order);
    }
}
