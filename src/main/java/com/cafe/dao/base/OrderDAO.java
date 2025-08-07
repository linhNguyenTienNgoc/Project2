package com.cafe.dao.base;

import com.cafe.model.entity.Order;
import com.cafe.model.enums.OrderStatus;
import com.cafe.model.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface cho Order entity
 * Kế thừa BaseDAO và thêm các methods đặc thù cho Order
 */
public interface OrderDAO extends BaseDAO<Order, Integer> {
    
    /**
     * Tìm order theo order number
     * @param orderNumber Order number
     * @return Optional of Order
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * Tìm orders theo customer
     * @param customerId Customer ID
     * @return List of Orders
     */
    List<Order> findByCustomerId(Integer customerId);
    
    /**
     * Tìm orders theo customer có phân trang
     * @param customerId Customer ID
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByCustomerId(Integer customerId, int offset, int limit);
    
    /**
     * Tìm orders theo user (waiter/barista)
     * @param userId User ID
     * @return List of Orders
     */
    List<Order> findByUserId(Integer userId);
    
    /**
     * Tìm orders theo user có phân trang
     * @param userId User ID
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByUserId(Integer userId, int offset, int limit);
    
    /**
     * Tìm orders theo table
     * @param tableId Table ID
     * @return List of Orders
     */
    List<Order> findByTableId(Integer tableId);
    
    /**
     * Tìm orders theo status
     * @param status Order status
     * @return List of Orders
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Tìm orders theo status có phân trang
     * @param status Order status
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByStatus(OrderStatus status, int offset, int limit);
    
    /**
     * Tìm orders theo payment status
     * @param paymentStatus Payment status
     * @return List of Orders
     */
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Tìm orders theo payment status có phân trang
     * @param paymentStatus Payment status
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByPaymentStatus(PaymentStatus paymentStatus, int offset, int limit);
    
    /**
     * Tìm orders theo khoảng thời gian
     * @param startDate Start date
     * @param endDate End date
     * @return List of Orders
     */
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Tìm orders theo khoảng thời gian có phân trang
     * @param startDate Start date
     * @param endDate End date
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int offset, int limit);
    
    /**
     * Tìm orders theo khoảng giá
     * @param minAmount Minimum amount
     * @param maxAmount Maximum amount
     * @return List of Orders
     */
    List<Order> findByAmountRange(Double minAmount, Double maxAmount);
    
    /**
     * Tìm orders theo khoảng giá có phân trang
     * @param minAmount Minimum amount
     * @param maxAmount Maximum amount
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> findByAmountRange(Double minAmount, Double maxAmount, int offset, int limit);
    
    /**
     * Tìm orders pending (chờ xử lý)
     * @return List of pending Orders
     */
    List<Order> findPendingOrders();
    
    /**
     * Tìm orders pending có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of pending Orders
     */
    List<Order> findPendingOrders(int offset, int limit);
    
    /**
     * Tìm orders completed (hoàn thành)
     * @return List of completed Orders
     */
    List<Order> findCompletedOrders();
    
    /**
     * Tìm orders completed có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of completed Orders
     */
    List<Order> findCompletedOrders(int offset, int limit);
    
    /**
     * Tìm orders cancelled (đã hủy)
     * @return List of cancelled Orders
     */
    List<Order> findCancelledOrders();
    
    /**
     * Tìm orders cancelled có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of cancelled Orders
     */
    List<Order> findCancelledOrders(int offset, int limit);
    
    /**
     * Tìm orders chưa thanh toán
     * @return List of unpaid Orders
     */
    List<Order> findUnpaidOrders();
    
    /**
     * Tìm orders chưa thanh toán có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of unpaid Orders
     */
    List<Order> findUnpaidOrders(int offset, int limit);
    
    /**
     * Tìm orders đã thanh toán
     * @return List of paid Orders
     */
    List<Order> findPaidOrders();
    
    /**
     * Tìm orders đã thanh toán có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of paid Orders
     */
    List<Order> findPaidOrders(int offset, int limit);
    
    /**
     * Đếm số orders theo customer
     * @param customerId Customer ID
     * @return Count of orders
     */
    long countByCustomerId(Integer customerId);
    
    /**
     * Đếm số orders theo user
     * @param userId User ID
     * @return Count of orders
     */
    long countByUserId(Integer userId);
    
    /**
     * Đếm số orders theo status
     * @param status Order status
     * @return Count of orders
     */
    long countByStatus(OrderStatus status);
    
    /**
     * Đếm số orders theo payment status
     * @param paymentStatus Payment status
     * @return Count of orders
     */
    long countByPaymentStatus(PaymentStatus paymentStatus);
    
    /**
     * Đếm số orders pending
     * @return Count of pending orders
     */
    long countPendingOrders();
    
    /**
     * Đếm số orders completed
     * @return Count of completed orders
     */
    long countCompletedOrders();
    
    /**
     * Đếm số orders cancelled
     * @return Count of cancelled orders
     */
    long countCancelledOrders();
    
    /**
     * Đếm số orders chưa thanh toán
     * @return Count of unpaid orders
     */
    long countUnpaidOrders();
    
    /**
     * Đếm số orders đã thanh toán
     * @return Count of paid orders
     */
    long countPaidOrders();
    
    /**
     * Cập nhật order status
     * @param orderId Order ID
     * @param status New status
     * @return true if successful
     */
    boolean updateOrderStatus(Integer orderId, OrderStatus status);
    
    /**
     * Cập nhật payment status
     * @param orderId Order ID
     * @param paymentStatus New payment status
     * @return true if successful
     */
    boolean updatePaymentStatus(Integer orderId, PaymentStatus paymentStatus);
    
    /**
     * Cập nhật total amount
     * @param orderId Order ID
     * @param totalAmount New total amount
     * @return true if successful
     */
    boolean updateTotalAmount(Integer orderId, Double totalAmount);
    
    /**
     * Kiểm tra order number có tồn tại
     * @param orderNumber Order number
     * @return true if exists
     */
    boolean existsByOrderNumber(String orderNumber);
    
    /**
     * Tìm kiếm orders theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param customerId Customer ID filter
     * @param userId User ID filter
     * @param tableId Table ID filter
     * @param status Order status filter
     * @param paymentStatus Payment status filter
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param minAmount Minimum amount filter
     * @param maxAmount Maximum amount filter
     * @return List of Orders
     */
    List<Order> searchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                           OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                           LocalDateTime endDate, Double minAmount, Double maxAmount);
    
    /**
     * Tìm kiếm orders theo nhiều tiêu chí có phân trang
     * @param keyword Search keyword
     * @param customerId Customer ID filter
     * @param userId User ID filter
     * @param tableId Table ID filter
     * @param status Order status filter
     * @param paymentStatus Payment status filter
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param minAmount Minimum amount filter
     * @param maxAmount Maximum amount filter
     * @param offset Offset
     * @param limit Limit
     * @return List of Orders
     */
    List<Order> searchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                           OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                           LocalDateTime endDate, Double minAmount, Double maxAmount, int offset, int limit);
    
    /**
     * Đếm số orders theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param customerId Customer ID filter
     * @param userId User ID filter
     * @param tableId Table ID filter
     * @param status Order status filter
     * @param paymentStatus Payment status filter
     * @param startDate Start date filter
     * @param endDate End date filter
     * @param minAmount Minimum amount filter
     * @param maxAmount Maximum amount filter
     * @return Count of orders
     */
    long countSearchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                          OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                          LocalDateTime endDate, Double minAmount, Double maxAmount);
}