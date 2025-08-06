package main.java.com.cafe.dao.base;

import main.java.com.cafe.model.entity.Order;

import java.util.List;

public interface OrderDAO {
    List<Order> getAllOrders();
    Order getOrderById(int id);
    Order getOrderByNumber(String orderNumber);
    boolean insertOrder(Order order);
    boolean updateOrder(Order order);
    boolean deleteOrder(int id);
    List<Order> getOrdersByCustomerId(int customerId);
    List<Order> getOrdersByUserId(int userId);
}