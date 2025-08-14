package com.cafe.dao.base;

import com.cafe.model.entity.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDAO {
    List<Order> getAllOrders();
    Order getOrderById(int id);
    Order getOrderByNumber(String orderNumber);
    boolean insertOrder(Order order);
    boolean updateOrder(Order order);
    boolean deleteOrder(int id);
    List<Order> getOrdersByCustomerId(int customerId);
    List<Order> getOrdersByUserId(int userId);
    
    // Additional methods for service layer
    boolean save(Order order);
    boolean update(Order order);
    Optional<Order> findById(Integer id);
}