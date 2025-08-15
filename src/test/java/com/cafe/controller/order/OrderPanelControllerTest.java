package com.cafe.controller.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class cho OrderPanelController
 * Kiểm tra chức năng clearOrder() và việc xóa dữ liệu LOCAL MEMORY
 */
public class OrderPanelControllerTest {
    
    private OrderPanelController orderPanelController;
    
    @BeforeEach
    void setUp() {
        orderPanelController = new OrderPanelController();
    }
    
    @Test
    void testClearOrderFunctionality() {
        System.out.println("🧪 Running clearOrder() test...");
        
        // Test 1: Kiểm tra trạng thái ban đầu
        assertEquals(0, getOrderItemsCount(orderPanelController), "Order items should be empty initially");
        assertEquals(0.0, getTotalAmount(orderPanelController), "Total amount should be 0 initially");
        assertNull(getCurrentOrder(orderPanelController), "Current order should be null initially");
        assertEquals("--", getCurrentTableName(orderPanelController), "Table name should be '--' initially");
        assertNull(getCurrentTableId(orderPanelController), "Table ID should be null initially");
        assertEquals(1, getCurrentUserId(orderPanelController), "User ID should be 1 initially");
        
        // Test 2: Thêm dữ liệu test
        orderPanelController.addProductToOrder("Cà phê đen", 25000, 2);
        orderPanelController.addProductToOrder("Bánh tiramisu", 45000, 1);
        
        // Verify data was added
        assertEquals(2, getOrderItemsCount(orderPanelController), "Should have 2 items");
        assertEquals(95000.0, getTotalAmount(orderPanelController), "Total should be 95000");
        
        // Test 3: Gọi clearOrder()
        orderPanelController.clearOrder();
        
        // Test 4: Kiểm tra sau khi clear
        assertEquals(0, getOrderItemsCount(orderPanelController), "Order items should be empty after clear");
        assertEquals(0.0, getTotalAmount(orderPanelController), "Total amount should be 0 after clear");
        assertNull(getCurrentOrder(orderPanelController), "Current order should be null after clear");
        assertEquals("--", getCurrentTableName(orderPanelController), "Table name should be '--' after clear");
        assertNull(getCurrentTableId(orderPanelController), "Table ID should be null after clear");
        assertEquals(1, getCurrentUserId(orderPanelController), "User ID should be 1 after clear");
        
        System.out.println("✅ All clearOrder() tests passed!");
    }
    
    @Test
    void testClearOrderWithTableInfo() {
        System.out.println("🧪 Testing clearOrder() with table info...");
        
        // Set table info
        orderPanelController.updateTableInfo("Bàn 5", com.cafe.model.enums.TableStatus.OCCUPIED);
        
        // Verify table info was set
        assertEquals("Bàn 5", getCurrentTableName(orderPanelController), "Table name should be 'Bàn 5'");
        assertEquals(5, getCurrentTableId(orderPanelController), "Table ID should be 5");
        
        // Add some items
        orderPanelController.addProductToOrder("Cà phê sữa", 30000, 1);
        
        // Clear order
        orderPanelController.clearOrder();
        
        // Verify everything is reset
        assertEquals("--", getCurrentTableName(orderPanelController), "Table name should be reset to '--'");
        assertNull(getCurrentTableId(orderPanelController), "Table ID should be reset to null");
        assertEquals(0, getOrderItemsCount(orderPanelController), "Order items should be empty");
        assertEquals(0.0, getTotalAmount(orderPanelController), "Total amount should be 0");
        
        System.out.println("✅ Table info clear test passed!");
    }
    
    // Helper methods to access private fields using reflection
    private int getOrderItemsCount(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("orderItems");
            field.setAccessible(true);
            java.util.Map<?, ?> orderItems = (java.util.Map<?, ?>) field.get(controller);
            return orderItems.size();
        } catch (Exception e) {
            fail("Failed to access orderItems: " + e.getMessage());
            return -1;
        }
    }
    
    private double getTotalAmount(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("totalAmount");
            field.setAccessible(true);
            return (Double) field.get(controller);
        } catch (Exception e) {
            fail("Failed to access totalAmount: " + e.getMessage());
            return -1.0;
        }
    }
    
    private Object getCurrentOrder(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("currentOrder");
            field.setAccessible(true);
            return field.get(controller);
        } catch (Exception e) {
            fail("Failed to access currentOrder: " + e.getMessage());
            return null;
        }
    }
    
    private String getCurrentTableName(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("currentTableName");
            field.setAccessible(true);
            return (String) field.get(controller);
        } catch (Exception e) {
            fail("Failed to access currentTableName: " + e.getMessage());
            return null;
        }
    }
    
    private Integer getCurrentTableId(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("currentTableId");
            field.setAccessible(true);
            return (Integer) field.get(controller);
        } catch (Exception e) {
            fail("Failed to access currentTableId: " + e.getMessage());
            return null;
        }
    }
    
    private Integer getCurrentUserId(OrderPanelController controller) {
        try {
            java.lang.reflect.Field field = OrderPanelController.class.getDeclaredField("currentUserId");
            field.setAccessible(true);
            return (Integer) field.get(controller);
        } catch (Exception e) {
            fail("Failed to access currentUserId: " + e.getMessage());
            return null;
        }
    }
    
    // Helper method to call clearOrder() using reflection
    private void clearOrder(OrderPanelController controller) {
        try {
            java.lang.reflect.Method method = OrderPanelController.class.getDeclaredMethod("clearOrder");
            method.setAccessible(true);
            method.invoke(controller);
        } catch (Exception e) {
            fail("Failed to call clearOrder: " + e.getMessage());
        }
    }
}
