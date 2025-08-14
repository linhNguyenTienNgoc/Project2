package com.cafe.service;

import com.cafe.model.entity.Order;
import com.cafe.model.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho OrderService
 * Kiểm tra các chức năng chính của OrderService
 */
@DisplayName("OrderService Tests")
public class OrderServiceTest {
    
    private OrderService orderService;
    private Product testProduct;
    
    @BeforeEach
    void setUp() {
        orderService = new OrderService();
        
        // Tạo test product
        testProduct = new Product();
        testProduct.setProductId(1);
        testProduct.setProductName("Cà phê đen");
        testProduct.setPrice(25000.0);
        testProduct.setAvailable(true);
        testProduct.setActive(true);
        testProduct.setStockQuantity(100);
    }
    
    @Test
    @DisplayName("Test tạo đơn hàng mới")
    void testCreateOrder() {
        // Given
        Integer tableId = 1;
        Integer userId = 1;
        Integer customerId = null;
        
        // When
        Order order = orderService.createOrder(tableId, userId, customerId);
        
        // Then
        assertNotNull(order, "Order không được null");
        assertEquals(tableId, order.getTableId(), "TableId phải đúng");
        assertEquals(userId, order.getUserId(), "UserId phải đúng");
        assertEquals("pending", order.getOrderStatus(), "Order status phải là pending");
        assertEquals("pending", order.getPaymentStatus(), "Payment status phải là pending");
        assertEquals(0.0, order.getTotalAmount(), "Total amount phải là 0");
        assertNotNull(order.getOrderNumber(), "Order number không được null");
        assertTrue(order.getOrderNumber().startsWith("ORD"), "Order number phải bắt đầu bằng ORD");
    }
    
    @Test
    @DisplayName("Test tạo đơn hàng với tham số null")
    void testCreateOrderWithNullParameters() {
        // Given
        Integer tableId = null;
        Integer userId = 1;
        
        // When
        Order order = orderService.createOrder(tableId, userId, null);
        
        // Then
        assertNull(order, "Order phải là null khi tableId null");
    }
    
    @Test
    @DisplayName("Test thêm sản phẩm vào đơn hàng")
    void testAddProductToOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        assertNotNull(order, "Order phải được tạo thành công");
        
        // When
        boolean result = orderService.addProductToOrder(order, testProduct, 2, "Không đường");
        
        // Then
        assertTrue(result, "Thêm sản phẩm phải thành công");
        assertEquals(50000.0, order.getTotalAmount(), "Total amount phải là 50000");
        assertEquals(50000.0, order.getFinalAmount(), "Final amount phải bằng total amount");
    }
    
    @Test
    @DisplayName("Test thêm sản phẩm với số lượng 0")
    void testAddProductWithZeroQuantity() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        
        // When
        boolean result = orderService.addProductToOrder(order, testProduct, 0, null);
        
        // Then
        assertFalse(result, "Không thể thêm sản phẩm với số lượng 0");
    }
    
    @Test
    @DisplayName("Test thêm sản phẩm vào đơn hàng đã preparing")
    void testAddProductToPreparingOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 1, null);
        orderService.placeOrder(order);
        
        // When
        boolean result = orderService.addProductToOrder(order, testProduct, 1, null);
        
        // Then
        assertFalse(result, "Không thể thêm sản phẩm vào đơn hàng đã preparing");
    }
    
    @Test
    @DisplayName("Test xác nhận đơn hàng")
    void testPlaceOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 2, null);
        
        // When
        boolean result = orderService.placeOrder(order);
        
        // Then
        assertTrue(result, "Xác nhận đơn hàng phải thành công");
        assertEquals("preparing", order.getOrderStatus(), "Order status phải là preparing");
    }
    
    @Test
    @DisplayName("Test xác nhận đơn hàng trống")
    void testPlaceEmptyOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        
        // When
        boolean result = orderService.placeOrder(order);
        
        // Then
        assertFalse(result, "Không thể xác nhận đơn hàng trống");
    }
    
    @Test
    @DisplayName("Test hủy đơn hàng")
    void testCancelOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 1, null);
        orderService.placeOrder(order);
        
        // When
        boolean result = orderService.cancelOrder(order, "Khách không muốn nữa");
        
        // Then
        assertTrue(result, "Hủy đơn hàng phải thành công");
        assertEquals("cancelled", order.getOrderStatus(), "Order status phải là cancelled");
    }
    
    @Test
    @DisplayName("Test hoàn thành đơn hàng")
    void testCompleteOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 1, null);
        orderService.placeOrder(order);
        orderService.startPreparing(order);
        orderService.markAsServed(order);
        
        // When
        boolean result = orderService.completeOrder(order);
        
        // Then
        assertTrue(result, "Hoàn thành đơn hàng phải thành công");
        assertEquals("completed", order.getOrderStatus(), "Order status phải là completed");
    }
    
    @Test
    @DisplayName("Test thanh toán đơn hàng")
    void testProcessPayment() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 2, null);
        orderService.placeOrder(order);
        orderService.startPreparing(order);
        orderService.markAsServed(order);
        orderService.completeOrder(order);
        
        // When
        boolean result = orderService.processPayment(order, "cash", 60000);
        
        // Then
        assertTrue(result, "Thanh toán phải thành công");
        assertEquals("paid", order.getPaymentStatus(), "Payment status phải là paid");
        assertEquals("cash", order.getPaymentMethod(), "Payment method phải là cash");
    }
    
    @Test
    @DisplayName("Test thanh toán với số tiền không đủ")
    void testProcessPaymentInsufficientAmount() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 2, null);
        orderService.placeOrder(order);
        orderService.startPreparing(order);
        orderService.markAsServed(order);
        orderService.completeOrder(order);
        
        // When
        boolean result = orderService.processPayment(order, "cash", 10000);
        
        // Then
        assertFalse(result, "Không thể thanh toán với số tiền không đủ");
    }
    
    @Test
    @DisplayName("Test tính tiền thối")
    void testCalculateChange() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        orderService.addProductToOrder(order, testProduct, 2, null);
        double amountReceived = 60000;
        
        // When
        double change = orderService.calculateChange(order, amountReceived);
        
        // Then
        assertEquals(10000, change, "Tiền thối phải là 10000");
    }
    
    @Test
    @DisplayName("Test format tiền")
    void testFormatTotalAmount() {
        // Given
        double amount = 50000;
        
        // When
        String formatted = orderService.formatTotalAmount(amount);
        
        // Then
        assertEquals("50,000 VNĐ", formatted, "Format tiền phải đúng");
    }
    
    @Test
    @DisplayName("Test kiểm tra có thể chỉnh sửa đơn hàng")
    void testCanModifyOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        
        // When & Then
        assertTrue(orderService.canModifyOrder(order), "Có thể chỉnh sửa đơn hàng pending");
        
        orderService.addProductToOrder(order, testProduct, 1, null);
        orderService.placeOrder(order);
        
        assertFalse(orderService.canModifyOrder(order), "Không thể chỉnh sửa đơn hàng preparing");
    }
    
    @Test
    @DisplayName("Test kiểm tra có thể thanh toán")
    void testCanPayOrder() {
        // Given
        Order order = orderService.createOrder(1, 1, null);
        
        // When & Then
        assertFalse(orderService.canPayOrder(order), "Không thể thanh toán đơn hàng pending");
        
        orderService.addProductToOrder(order, testProduct, 1, null);
        orderService.placeOrder(order);
        orderService.startPreparing(order);
        orderService.markAsServed(order);
        orderService.completeOrder(order);
        
        assertTrue(orderService.canPayOrder(order), "Có thể thanh toán đơn hàng completed");
    }
    
    @Test
    @DisplayName("Test flow hoàn chỉnh của đơn hàng")
    void testCompleteOrderFlow() {
        // 1. Tạo đơn hàng
        Order order = orderService.createOrder(1, 1, null);
        assertNotNull(order);
        assertEquals("pending", order.getOrderStatus());
        
        // 2. Thêm sản phẩm
        boolean added = orderService.addProductToOrder(order, testProduct, 2, "Không đường");
        assertTrue(added);
        assertEquals(50000, order.getTotalAmount());
        
        // 3. Xác nhận đơn hàng
        boolean confirmed = orderService.placeOrder(order);
        assertTrue(confirmed);
        assertEquals("preparing", order.getOrderStatus());
        
        // 4. Bắt đầu chuẩn bị
        boolean preparing = orderService.startPreparing(order);
        assertTrue(preparing);
        assertEquals("ready", order.getOrderStatus());
        
        // 5. Đánh dấu đã phục vụ
        boolean served = orderService.markAsServed(order);
        assertTrue(served);
        assertEquals("served", order.getOrderStatus());
        
        // 6. Hoàn thành đơn hàng
        boolean completed = orderService.completeOrder(order);
        assertTrue(completed);
        assertEquals("completed", order.getOrderStatus());
        
        // 7. Thanh toán
        boolean paid = orderService.processPayment(order, "cash", 60000);
        assertTrue(paid);
        assertEquals("paid", order.getPaymentStatus());
        
        // 8. Kiểm tra tiền thối
        double change = orderService.calculateChange(order, 60000);
        assertEquals(10000, change);
    }
}
