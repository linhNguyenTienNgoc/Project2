package com.cafe.dao.base;
import com.cafe.model.entity.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {
    private final Connection conn;

    public OrderDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Order getOrderByNumber(String orderNumber) {
        String sql = "SELECT * FROM orders WHERE order_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractOrder(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertOrder(Order order) {
        String sql = """
            INSERT INTO orders (order_number, table_id, customer_id, user_id, order_date, total_amount, discount_amount, final_amount, 
                                payment_method, payment_status, order_status, notes, created_at, updated_at) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getOrderNumber());
            ps.setInt(2, order.getTableId());
            if (order.getCustomerId() != null) {
                ps.setInt(3, order.getCustomerId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, order.getUserId());
            ps.setTimestamp(5, order.getOrderDate());
            ps.setDouble(6, order.getTotalAmount());
            ps.setDouble(7, order.getDiscountAmount());
            ps.setDouble(8, order.getFinalAmount());
            ps.setString(9, order.getPaymentMethod());
            ps.setString(10, order.getPaymentStatus());
            ps.setString(11, order.getOrderStatus());
            ps.setString(12, order.getNotes());
            ps.setTimestamp(13, order.getCreatedAt());
            ps.setTimestamp(14, order.getUpdatedAt());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                // Lấy generated ID
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
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
    public boolean updateOrder(Order order) {
        String sql = """
            UPDATE orders 
            SET order_number=?, table_id=?, customer_id=?, user_id=?, order_date=?, total_amount=?, discount_amount=?, final_amount=?, 
                payment_method=?, payment_status=?, order_status=?, notes=?, updated_at=?
            WHERE order_id=?
            """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getOrderNumber());
            ps.setInt(2, order.getTableId());
            if (order.getCustomerId() != null) {
                ps.setInt(3, order.getCustomerId());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setInt(4, order.getUserId());
            ps.setTimestamp(5, order.getOrderDate());
            ps.setDouble(6, order.getTotalAmount());
            ps.setDouble(7, order.getDiscountAmount());
            ps.setDouble(8, order.getFinalAmount());
            ps.setString(9, order.getPaymentMethod());
            ps.setString(10, order.getPaymentStatus());
            ps.setString(11, order.getOrderStatus());
            ps.setString(12, order.getNotes());
            ps.setTimestamp(13, order.getUpdatedAt());
            ps.setInt(14, order.getOrderId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteOrder(int id) {
        Connection conn = null;
        try {
            conn = this.conn;
            conn.setAutoCommit(false);
            
            // 1. Xóa order details trước
            String deleteDetailsSQL = "DELETE FROM order_details WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteDetailsSQL)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // 2. Xóa order promotions nếu có
            String deletePromotionsSQL = "DELETE FROM order_promotions WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deletePromotionsSQL)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
            
            // 3. Lấy table_id trước khi xóa order
            Integer tableId = null;
            String getTableSQL = "SELECT table_id FROM orders WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(getTableSQL)) {
                stmt.setInt(1, id);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    tableId = rs.getInt("table_id");
                }
            }
            
            // 4. Xóa order chính
            String deleteOrderSQL = "DELETE FROM orders WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(deleteOrderSQL)) {
                stmt.setInt(1, id);
                int result = stmt.executeUpdate();
                
                if (result > 0) {
                    // 5. Cập nhật trạng thái bàn nếu cần
                    if (tableId != null) {
                        updateTableStatusAfterDelete(conn, tableId);
                    }
                    
                    conn.commit();
                    return true;
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
    
    // Cập nhật trạng thái bàn sau khi xóa
    private void updateTableStatusAfterDelete(Connection conn, int tableId) throws SQLException {
        // Kiểm tra xem bàn còn order active khác không
        String checkSQL = """
            SELECT COUNT(*) FROM orders 
            WHERE table_id = ? 
            AND order_status IN ('pending', 'confirmed', 'preparing', 'ready', 'served')
        """;
        
        try (PreparedStatement stmt = conn.prepareStatement(checkSQL)) {
            stmt.setInt(1, tableId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Không còn order active -> set available
                String updateSQL = "UPDATE tables SET status = 'available' WHERE table_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSQL)) {
                    updateStmt.setInt(1, tableId);
                    updateStmt.executeUpdate();
                }
            }
        }
    }

    @Override
    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(extractOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===================== Helper Methods =======================

    private Order extractOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setOrderNumber(rs.getString("order_number"));
        o.setTableId(rs.getInt("table_id"));
        int customerId = rs.getInt("customer_id");
        o.setCustomerId(rs.wasNull() ? null : customerId);
        o.setUserId(rs.getInt("user_id"));
        o.setOrderDate(rs.getTimestamp("order_date"));
        o.setTotalAmount(rs.getDouble("total_amount"));
        o.setDiscountAmount(rs.getDouble("discount_amount"));
        o.setFinalAmount(rs.getDouble("final_amount"));
        o.setPaymentMethod(rs.getString("payment_method"));
        o.setPaymentStatus(rs.getString("payment_status"));
        o.setOrderStatus(rs.getString("order_status"));
        o.setNotes(rs.getString("notes"));
        o.setCreatedAt(rs.getTimestamp("created_at"));
        o.setUpdatedAt(rs.getTimestamp("updated_at"));
        return o;
    }



    // Additional methods implementation
    @Override
    public boolean save(Order order) {
        return insertOrder(order);
    }

    @Override
    public boolean update(Order order) {
        return updateOrder(order);
    }

    @Override
    public java.util.Optional<Order> findById(Integer id) {
        Order order = getOrderById(id);
        return order != null ? java.util.Optional.of(order) : java.util.Optional.empty();
    }
}