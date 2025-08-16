package com.cafe.dao.base;

import com.cafe.model.entity.OrderDetail;
import java.util.List;
import java.util.Optional;

public interface OrderDetailDAO {
    boolean save(OrderDetail orderDetail);
    boolean update(OrderDetail orderDetail);
    boolean delete(int orderDetailId);
    Optional<OrderDetail> findById(Integer orderDetailId);
    List<OrderDetail> findByOrderId(Integer orderId);
    boolean deleteByOrderId(Integer orderId);
    boolean deleteByOrderAndProduct(Integer orderId, Integer productId);
    boolean updateQuantity(Integer orderId, Integer productId, Integer newQuantity);
}