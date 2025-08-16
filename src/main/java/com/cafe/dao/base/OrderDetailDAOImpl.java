package com.cafe.dao.base;

import com.cafe.model.entity.OrderDetail;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDetailDAOImpl implements OrderDetailDAO {
    private final Connection conn;

    public OrderDetailDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(OrderDetail orderDetail) {
        String sql = """
            INSERT INTO order_details (order_id, product_id, quantity, unit_price, total_price, notes) 
            VALUES (?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, orderDetail.getOrderId());
            stmt.setInt(2, orderDetail.getProductId());
            stmt.setInt(3, orderDetail.getQuantity());
            stmt.setDouble(4, orderDetail.getUnitPrice());
            stmt.setDouble(5, orderDetail.getTotalPrice());
            stmt.setString(6, orderDetail.getNotes());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        orderDetail.setOrderDetailId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(OrderDetail orderDetail) {
        String sql = """
            UPDATE order_details 
            SET order_id = ?, product_id = ?, quantity = ?, unit_price = ?, total_price = ?, notes = ?
            WHERE order_detail_id = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderDetail.getOrderId());
            stmt.setInt(2, orderDetail.getProductId());
            stmt.setInt(3, orderDetail.getQuantity());
            stmt.setDouble(4, orderDetail.getUnitPrice());
            stmt.setDouble(5, orderDetail.getTotalPrice());
            stmt.setString(6, orderDetail.getNotes());
            stmt.setInt(7, orderDetail.getOrderDetailId());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int orderDetailId) {
        String sql = "DELETE FROM order_details WHERE order_detail_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderDetailId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<OrderDetail> findById(Integer orderDetailId) {
        String sql = "SELECT * FROM order_details WHERE order_detail_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderDetailId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractOrderDetail(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<OrderDetail> findByOrderId(Integer orderId) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        String sql = """
            SELECT od.*, p.product_name, c.category_name 
            FROM order_details od
            JOIN products p ON od.product_id = p.product_id
            JOIN categories c ON p.category_id = c.category_id
            WHERE od.order_id = ?
            ORDER BY od.order_detail_id
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail orderDetail = extractOrderDetail(rs);
                    orderDetail.setProductName(rs.getString("product_name"));
                    orderDetail.setCategoryName(rs.getString("category_name"));
                    orderDetails.add(orderDetail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderDetails;
    }

    @Override
    public boolean deleteByOrderId(Integer orderId) {
        String sql = "DELETE FROM order_details WHERE order_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            return stmt.executeUpdate() >= 0; // Allow 0 affected rows
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteByOrderAndProduct(Integer orderId, Integer productId) {
        String sql = "DELETE FROM order_details WHERE order_id = ? AND product_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.setInt(2, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateQuantity(Integer orderId, Integer productId, Integer newQuantity) {
        String sql = """
            UPDATE order_details 
            SET quantity = ?, total_price = quantity * unit_price 
            WHERE order_id = ? AND product_id = ?
            """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newQuantity);
            stmt.setInt(2, orderId);
            stmt.setInt(3, productId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private OrderDetail extractOrderDetail(ResultSet rs) throws SQLException {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderDetailId(rs.getInt("order_detail_id"));
        orderDetail.setOrderId(rs.getInt("order_id"));
        orderDetail.setProductId(rs.getInt("product_id"));
        orderDetail.setQuantity(rs.getInt("quantity"));
        orderDetail.setUnitPrice(rs.getDouble("unit_price"));
        orderDetail.setTotalPrice(rs.getDouble("total_price"));
        orderDetail.setNotes(rs.getString("notes"));
        orderDetail.setCreatedAt(rs.getTimestamp("created_at"));
        return orderDetail;
    }
}
