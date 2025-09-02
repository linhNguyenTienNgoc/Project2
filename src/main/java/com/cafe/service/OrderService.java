package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.dao.base.OrderDetailDAO;
import com.cafe.dao.base.OrderDetailDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.entity.Product;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Complete Order Service với OrderDetailDAO Integration
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
 * @version 2.0.0 (Complete Integration)
 */
public class OrderService {

    private final MenuService menuService;

    public OrderService() {
        // ✅ REMOVED: No longer getting connection in constructor
        // Connections will be managed per operation using try-with-resources
            this.menuService = new MenuService();
        System.out.println("✅ OrderService initialized with proper connection management");
    }

    // =====================================================
    // ✅ CORE ORDER MANAGEMENT
    // =====================================================

    /**
     * Tạo order mới khi khách vào bàn
     * @param tableId ID của bàn
     * @param userId ID của nhân viên tạo order
     * @param customerId ID của khách hàng (có thể null)
     * @return Order mới được tạo hoặc null nếu lỗi
     */
    public Order createOrder(Integer tableId, Integer userId, Integer customerId) {
        if (tableId == null || userId == null) {
            System.err.println("❌ TableId and UserId cannot be null");
            return null;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            // Check if there's already an active order for this table
            Optional<Order> existingOrder = getActiveOrderByTable(tableId, conn);
            if (existingOrder.isPresent()) {
                System.out.println("⚠️ Table " + tableId + " already has an active order: " + existingOrder.get().getOrderNumber());
                return existingOrder.get();
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

            if (orderDAO.save(order)) {
                System.out.println("✅ Order created successfully: " + order.getOrderNumber());
                return order;
            } else {
                System.err.println("❌ Failed to save order to database");
                return null;
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating order: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ✅ COMPLETE: Thêm product vào order với OrderDetailDAO
     */
    public boolean addProductToOrder(Order order, Product product, int quantity, String notes) {
        if (order == null || product == null || quantity <= 0) {
            System.err.println("❌ Invalid parameters for addProductToOrder");
            return false;
        }

        // Kiểm tra trạng thái order
        if (!"pending".equals(order.getOrderStatus())) {
            System.err.println("❌ Cannot add product to order with status: " + order.getOrderStatus());
            return false;
        }

        // Kiểm tra stock availability
        if (!menuService.canOrderProduct(product, quantity)) {
            System.err.println("❌ Insufficient stock for product: " + product.getProductName());
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            // Check if product already exists in order
            List<OrderDetail> existingDetails = orderDetailDAO.findByOrderId(order.getOrderId());
            Optional<OrderDetail> existingDetail = existingDetails.stream()
                    .filter(detail -> detail.getProductId() == product.getProductId())
                    .findFirst();

            if (existingDetail.isPresent()) {
                // Update existing order detail quantity
                OrderDetail detail = existingDetail.get();
                int newQuantity = detail.getQuantity() + quantity;
                detail.setQuantity(newQuantity);
                detail.calculateTotalPrice();

                if (!orderDetailDAO.update(detail)) {
                    System.err.println("❌ Failed to update existing order detail");
                    return false;
                }

                System.out.println("✅ Updated existing order detail: " + product.getProductName() + " (+" + quantity + " = " + newQuantity + ")");
            } else {
                // Create new order detail
                OrderDetail orderDetail = new OrderDetail(
                        order.getOrderId(),
                        product.getProductId(),
                        quantity,
                        product.getPrice(),
                        notes
                );

                if (!orderDetailDAO.save(orderDetail)) {
                    System.err.println("❌ Failed to save order detail");
                    return false;
                }

                System.out.println("✅ Added new order detail: " + product.getProductName() + " x" + quantity);
            }

            // Recalculate and update order total
            if (!recalculateOrderTotal(order, conn)) {
                System.err.println("❌ Failed to recalculate order total");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("❌ Error adding product to order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ COMPLETE: Xóa sản phẩm khỏi order
     */
    public boolean removeProductFromOrder(Order order, int productId) {
        if (order == null || !"pending".equals(order.getOrderStatus())) {
            System.err.println("❌ Cannot remove product from order with status: " + order.getOrderStatus());
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            if (!orderDetailDAO.deleteByOrderAndProduct(order.getOrderId(), productId)) {
                System.err.println("❌ Failed to delete order detail");
                return false;
            }

            System.out.println("✅ Removed product " + productId + " from order " + order.getOrderNumber());

            // Recalculate order total
            return recalculateOrderTotal(order, conn);
        } catch (Exception e) {
            System.err.println("❌ Error removing product from order: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ COMPLETE: Cập nhật số lượng sản phẩm trong order
     */
    public boolean updateProductQuantity(Order order, int productId, int newQuantity) {
        if (order == null || newQuantity < 0 || !"pending".equals(order.getOrderStatus())) {
            System.err.println("❌ Invalid parameters for quantity update");
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            if (newQuantity == 0) {
                // Remove product if quantity is 0
                return removeProductFromOrder(order, productId);
            }

            if (!orderDetailDAO.updateQuantity(order.getOrderId(), productId, newQuantity)) {
                System.err.println("❌ Failed to update product quantity");
                return false;
            }

            System.out.println("✅ Updated product " + productId + " quantity to " + newQuantity);

            // Recalculate order total
            return recalculateOrderTotal(order, conn);
        } catch (Exception e) {
            System.err.println("❌ Error updating product quantity: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * ✅ NEW: Recalculate order total from order details
     */
    private boolean recalculateOrderTotal(Order order, Connection conn) {
        try {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            List<OrderDetail> orderDetails = orderDetailDAO.findByOrderId(order.getOrderId());

            double totalAmount = orderDetails.stream()
                    .mapToDouble(OrderDetail::getTotalPrice)
                    .sum();

            order.setTotalAmount(totalAmount);
            order.calculateFinalAmount();
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (!orderDAO.update(order)) {
                System.err.println("❌ Failed to update order total");
                return false;
            }

            System.out.println("✅ Order total recalculated: " + String.format("%.0f VND", totalAmount));
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error recalculating order total: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // ✅ ORDER STATUS MANAGEMENT
    // =====================================================

    /**
     * Place order (xác nhận đơn hàng)
     */
    public boolean placeOrder(Order order) {
        if (order == null) {
            return false;
        }

        if (!"pending".equals(order.getOrderStatus())) {
            System.err.println("❌ Cannot place order with status: " + order.getOrderStatus());
            return false;
        }

        if (order.getTotalAmount() <= 0) {
            System.err.println("❌ Cannot place order with zero total amount");
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setOrderStatus("preparing");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (orderDAO.update(order)) {
                System.out.println("✅ Order placed successfully: " + order.getOrderNumber());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error placing order: " + e.getMessage());
            return false;
        }
    }

    /**
     * Chuyển order sang trạng thái ready (sẵn sàng phục vụ)
     */
    public boolean markOrderReady(Order order) {
        if (order == null || !"preparing".equals(order.getOrderStatus())) {
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setOrderStatus("ready");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("❌ Error marking order ready: " + e.getMessage());
            return false;
        }
    }

    /**
     * Chuyển order sang trạng thái served (đã phục vụ)
     */
    public boolean markAsServed(Order order) {
        if (order == null || !"ready".equals(order.getOrderStatus())) {
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setOrderStatus("served");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("❌ Error marking as served: " + e.getMessage());
            return false;
        }
    }

    /**
     * Complete order (hoàn thành đơn hàng)
     */
    public boolean completeOrder(Order order) {
        if (order == null) {
            return false;
        }

        if (!order.canBeCompleted()) {
            System.err.println("❌ Cannot complete order with status: " + order.getOrderStatus());
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setOrderStatus("completed");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (orderDAO.update(order)) {
                System.out.println("✅ Order completed: " + order.getOrderNumber());
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error completing order: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cancel order (hủy đơn hàng)
     */
    public boolean cancelOrder(Order order, String reason) {
        if (order == null) {
            return false;
        }

        if (!order.canBeCancelled()) {
            System.err.println("❌ Cannot cancel order with status: " + order.getOrderStatus());
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            // ✅ FIXED: Set both order_status and payment_status to 'cancelled' to satisfy constraint
            order.setOrderStatus("cancelled");
            order.setPaymentStatus("cancelled");
            order.setNotes(reason != null ? reason : "Order cancelled");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (orderDAO.update(order)) {
                System.out.println("✅ Order cancelled: " + order.getOrderNumber() + " - " + reason);
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error cancelling order: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // ✅ PAYMENT MANAGEMENT
    // =====================================================

    /**
     * Process payment (xử lý thanh toán)
     */
    public boolean processPayment(Order order, String paymentMethod, double amountReceived) {
        if (order == null) {
            return false;
        }

        if (!order.canBePaid()) {
            System.err.println("❌ Cannot process payment for order with status: " + order.getOrderStatus());
            return false;
        }

        if (amountReceived < order.getFinalAmount()) {
            System.err.println("❌ Insufficient payment amount. Required: " + order.getFinalAmount() + ", Received: " + amountReceived);
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("paid");
            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));

            if (orderDAO.update(order)) {
                System.out.println("✅ Payment processed successfully for order: " + order.getOrderNumber());
                System.out.println("💰 Amount: " + order.getFinalAmount() + " VND, Method: " + paymentMethod);

                double change = calculateChange(order, amountReceived);
                if (change > 0) {
                    System.out.println("💵 Change: " + change + " VND");
                }

                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("❌ Error processing payment: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tính tiền thối cho khách
     */
    public double calculateChange(Order order, double amountReceived) {
        if (order == null || amountReceived < order.getFinalAmount()) {
            return 0.0;
        }
        return amountReceived - order.getFinalAmount();
    }

    // =====================================================
    // ✅ QUERY METHODS - IMPLEMENTED
    // =====================================================

    /**
     * ✅ IMPLEMENTED: Lấy order theo ID
     */
    public Optional<Order> getOrderById(Integer orderId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.findById(orderId);
        } catch (Exception e) {
            System.err.println("❌ Error getting order by ID " + orderId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * ✅ IMPLEMENTED: Lấy tất cả orders theo trạng thái
     */
    public List<Order> getOrdersByStatus(String status) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> status.equals(order.getOrderStatus()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Error getting orders by status: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ IMPLEMENTED: Lấy orders theo bàn
     */
    public List<Order> getOrdersByTable(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getTableId() == tableId)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Error getting orders by table: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ IMPLEMENTED: Lấy order đang active (pending/preparing/ready/served) theo bàn
     */
    public Optional<Order> getActiveOrderByTable(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            return getActiveOrderByTable(tableId, conn);
        } catch (Exception e) {
            System.err.println("❌ Error getting active order by table: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * ✅ NEW: Overloaded method for internal use with existing connection
     */
    private Optional<Order> getActiveOrderByTable(int tableId, Connection conn) {
        try {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            List<String> activeStatuses = Arrays.asList("pending", "preparing", "ready", "served");

            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getTableId() == tableId)
                    .filter(order -> activeStatuses.contains(order.getOrderStatus()))
                    .findFirst();
        } catch (Exception e) {
            System.err.println("❌ Error getting active order by table: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * ✅ NEW: Get order details for an order
     */
    public List<OrderDetail> getOrderDetails(int orderId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn);
            return orderDetailDAO.findByOrderId(orderId);
        } catch (Exception e) {
            System.err.println("❌ Error getting order details: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ NEW: Get orders by date range
     */
    public List<Order> getOrdersByDateRange(Timestamp startDate, Timestamp endDate) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getAllOrders().stream()
                    .filter(order -> order.getOrderDate().after(startDate) && order.getOrderDate().before(endDate))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("❌ Error getting orders by date range: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * ✅ NEW: Get orders by user (staff member)
     */
    public List<Order> getOrdersByUser(int userId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            return orderDAO.getOrdersByUserId(userId);
        } catch (Exception e) {
            System.err.println("❌ Error getting orders by user: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // =====================================================
    // ✅ UTILITY METHODS
    // =====================================================

    /**
     * Generate unique order number
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

    /**
     * Kiểm tra xem order có thể chỉnh sửa không
     */
    public boolean canModifyOrder(Order order) {
        return order != null && "pending".equals(order.getOrderStatus());
    }

    /**
     * Kiểm tra xem order có thể thanh toán không
     */
    public boolean canPayOrder(Order order) {
        return order != null && order.canBePaid();
    }

    /**
     * Update order in database
     */
    public boolean updateOrder(Order order) {
        if (order == null) {
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn); // ✅ Initialize OrderDetailDAO

            order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            return orderDAO.update(order);
        } catch (Exception e) {
            System.err.println("❌ Error updating order: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ NEW: Get order statistics
     */
    public Map<String, Object> getOrderStatistics() {
        Map<String, Object> stats = new HashMap<>();
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDAO orderDAO = new OrderDAOImpl(conn);
            List<Order> allOrders = orderDAO.getAllOrders();

            stats.put("totalOrders", allOrders.size());
            stats.put("pendingOrders", allOrders.stream().filter(o -> "pending".equals(o.getOrderStatus())).count());
            stats.put("preparingOrders", allOrders.stream().filter(o -> "preparing".equals(o.getOrderStatus())).count());
            stats.put("completedOrders", allOrders.stream().filter(o -> "completed".equals(o.getOrderStatus())).count());
            stats.put("totalRevenue", allOrders.stream()
                    .filter(o -> "completed".equals(o.getOrderStatus()) && "paid".equals(o.getPaymentStatus()))
                    .mapToDouble(Order::getFinalAmount)
                    .sum());

        } catch (Exception e) {
            System.err.println("❌ Error getting order statistics: " + e.getMessage());
        }
        return stats;
    }

    /**
     * ✅ NEW: Clear all order details for an order
     */
    public boolean clearOrderDetails(int orderId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(conn);
            return orderDetailDAO.deleteByOrderId(orderId);
        } catch (Exception e) {
            System.err.println("❌ Error clearing order details: " + e.getMessage());
            return false;
        }
    }

    /**
     * ✅ NEW: Convenient overload methods
     */
    public boolean addProductToOrder(Order order, Product product, int quantity) {
        return addProductToOrder(order, product, quantity, null);
    }

    public boolean updateOrderStatus(Order order) {
        return updateOrder(order);
    }
}