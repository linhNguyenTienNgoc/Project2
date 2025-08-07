package com.cafe.dao.base;

import com.cafe.model.entity.Order;
import com.cafe.model.entity.Customer;
import com.cafe.model.entity.User;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.enums.OrderStatus;
import com.cafe.model.enums.PaymentStatus;
import com.cafe.model.enums.PaymentMethod;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của OrderDAO interface
 * Cải thiện với đầy đủ methods và error handling
 */
public class OrderDAOImpl implements OrderDAO {
    private final Connection conn;

    public OrderDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // =====================================================
    // BASE DAO METHODS
    // =====================================================

    @Override
    public List<Order> findAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Order> findById(Integer id) {
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Order entity) {
        if (entity.getOrderId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public boolean insert(Order entity) {
        String sql = "INSERT INTO orders (order_number, table_id, customer_id, user_id, total_amount, " +
                    "discount_amount, tax_amount, final_amount, payment_method, payment_status, order_status, " +
                    "notes, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setOrderToPreparedStatement(entity, ps);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setOrderId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Order entity) {
        String sql = "UPDATE orders SET order_number = ?, table_id = ?, customer_id = ?, user_id = ?, " +
                    "total_amount = ?, discount_amount = ?, tax_amount = ?, final_amount = ?, payment_method = ?, " +
                    "payment_status = ?, order_status = ?, notes = ?, updated_at = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setOrderToPreparedStatement(entity, ps);
            ps.setInt(14, entity.getOrderId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Order entity) {
        return deleteById(entity.getOrderId());
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public List<Order> findAll(int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> search(String keyword) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ? " +
                    "ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> search(String keyword, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ? " +
                    "ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "WHERE o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // ORDER SPECIFIC METHODS
    // =====================================================

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Order> findByCustomerId(Integer customerId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.customer_id = ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByCustomerId(Integer customerId, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.customer_id = ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByUserId(Integer userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByUserId(Integer userId, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.user_id = ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByTableId(Integer tableId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.table_id = ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tableId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_status = ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByStatus(OrderStatus status, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.order_status = ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByPaymentStatus(PaymentStatus paymentStatus) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.payment_status = ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus.name());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByPaymentStatus(PaymentStatus paymentStatus, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.payment_status = ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus.name());
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.created_at BETWEEN ? AND ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByDateRange(LocalDateTime startDate, LocalDateTime endDate, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.created_at BETWEEN ? AND ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(startDate));
            ps.setTimestamp(2, Timestamp.valueOf(endDate));
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByAmountRange(Double minAmount, Double maxAmount) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.final_amount BETWEEN ? AND ? ORDER BY o.created_at DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minAmount);
            ps.setDouble(2, maxAmount);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findByAmountRange(Double minAmount, Double maxAmount, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name " +
                    "FROM orders o " +
                    "LEFT JOIN customers c ON o.customer_id = c.customer_id " +
                    "LEFT JOIN users u ON o.user_id = u.user_id " +
                    "LEFT JOIN tables t ON o.table_id = t.table_id " +
                    "WHERE o.final_amount BETWEEN ? AND ? ORDER BY o.created_at DESC LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minAmount);
            ps.setDouble(2, maxAmount);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> findPendingOrders() {
        return findByStatus(OrderStatus.PENDING);
    }

    @Override
    public List<Order> findPendingOrders(int offset, int limit) {
        return findByStatus(OrderStatus.PENDING, offset, limit);
    }

    @Override
    public List<Order> findCompletedOrders() {
        return findByStatus(OrderStatus.COMPLETED);
    }

    @Override
    public List<Order> findCompletedOrders(int offset, int limit) {
        return findByStatus(OrderStatus.COMPLETED, offset, limit);
    }

    @Override
    public List<Order> findCancelledOrders() {
        return findByStatus(OrderStatus.CANCELLED);
    }

    @Override
    public List<Order> findCancelledOrders(int offset, int limit) {
        return findByStatus(OrderStatus.CANCELLED, offset, limit);
    }

    @Override
    public List<Order> findUnpaidOrders() {
        return findByPaymentStatus(PaymentStatus.UNPAID);
    }

    @Override
    public List<Order> findUnpaidOrders(int offset, int limit) {
        return findByPaymentStatus(PaymentStatus.UNPAID, offset, limit);
    }

    @Override
    public List<Order> findPaidOrders() {
        return findByPaymentStatus(PaymentStatus.PAID);
    }

    @Override
    public List<Order> findPaidOrders(int offset, int limit) {
        return findByPaymentStatus(PaymentStatus.PAID, offset, limit);
    }

    @Override
    public long countByCustomerId(Integer customerId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countByUserId(Integer userId) {
        String sql = "SELECT COUNT(*) FROM orders WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countByStatus(OrderStatus status) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countByPaymentStatus(PaymentStatus paymentStatus) {
        String sql = "SELECT COUNT(*) FROM orders WHERE payment_status = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus.name());
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countPendingOrders() {
        return countByStatus(OrderStatus.PENDING);
    }

    @Override
    public long countCompletedOrders() {
        return countByStatus(OrderStatus.COMPLETED);
    }

    @Override
    public long countCancelledOrders() {
        return countByStatus(OrderStatus.CANCELLED);
    }

    @Override
    public long countUnpaidOrders() {
        return countByPaymentStatus(PaymentStatus.UNPAID);
    }

    @Override
    public long countPaidOrders() {
        return countByPaymentStatus(PaymentStatus.PAID);
    }

    @Override
    public boolean updateOrderStatus(Integer orderId, OrderStatus status) {
        String sql = "UPDATE orders SET order_status = ?, updated_at = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePaymentStatus(Integer orderId, PaymentStatus paymentStatus) {
        String sql = "UPDATE orders SET payment_status = ?, updated_at = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, paymentStatus.name());
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateTotalAmount(Integer orderId, Double totalAmount) {
        String sql = "UPDATE orders SET total_amount = ?, updated_at = ? WHERE order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, totalAmount);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, orderId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        String sql = "SELECT COUNT(*) FROM orders WHERE order_number = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, orderNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Order> searchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                                  OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                                  LocalDateTime endDate, Double minAmount, Double maxAmount) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name ");
        sql.append("FROM orders o ");
        sql.append("LEFT JOIN customers c ON o.customer_id = c.customer_id ");
        sql.append("LEFT JOIN users u ON o.user_id = u.user_id ");
        sql.append("LEFT JOIN tables t ON o.table_id = t.table_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (customerId != null) {
            sql.append("AND o.customer_id = ? ");
            params.add(customerId);
        }
        
        if (userId != null) {
            sql.append("AND o.user_id = ? ");
            params.add(userId);
        }
        
        if (tableId != null) {
            sql.append("AND o.table_id = ? ");
            params.add(tableId);
        }
        
        if (status != null) {
            sql.append("AND o.order_status = ? ");
            params.add(status.name());
        }
        
        if (paymentStatus != null) {
            sql.append("AND o.payment_status = ? ");
            params.add(paymentStatus.name());
        }
        
        if (startDate != null) {
            sql.append("AND o.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate));
        }
        
        if (endDate != null) {
            sql.append("AND o.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate));
        }
        
        if (minAmount != null) {
            sql.append("AND o.final_amount >= ? ");
            params.add(minAmount);
        }
        
        if (maxAmount != null) {
            sql.append("AND o.final_amount <= ? ");
            params.add(maxAmount);
        }
        
        sql.append("ORDER BY o.created_at DESC");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Order> searchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                                  OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                                  LocalDateTime endDate, Double minAmount, Double maxAmount, int offset, int limit) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT o.*, c.customer_name, c.phone, u.username, u.full_name, t.table_number, t.table_name ");
        sql.append("FROM orders o ");
        sql.append("LEFT JOIN customers c ON o.customer_id = c.customer_id ");
        sql.append("LEFT JOIN users u ON o.user_id = u.user_id ");
        sql.append("LEFT JOIN tables t ON o.table_id = t.table_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (customerId != null) {
            sql.append("AND o.customer_id = ? ");
            params.add(customerId);
        }
        
        if (userId != null) {
            sql.append("AND o.user_id = ? ");
            params.add(userId);
        }
        
        if (tableId != null) {
            sql.append("AND o.table_id = ? ");
            params.add(tableId);
        }
        
        if (status != null) {
            sql.append("AND o.order_status = ? ");
            params.add(status.name());
        }
        
        if (paymentStatus != null) {
            sql.append("AND o.payment_status = ? ");
            params.add(paymentStatus.name());
        }
        
        if (startDate != null) {
            sql.append("AND o.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate));
        }
        
        if (endDate != null) {
            sql.append("AND o.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate));
        }
        
        if (minAmount != null) {
            sql.append("AND o.final_amount >= ? ");
            params.add(minAmount);
        }
        
        if (maxAmount != null) {
            sql.append("AND o.final_amount <= ? ");
            params.add(maxAmount);
        }
        
        sql.append("ORDER BY o.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearchOrders(String keyword, Integer customerId, Integer userId, Integer tableId, 
                                OrderStatus status, PaymentStatus paymentStatus, LocalDateTime startDate, 
                                LocalDateTime endDate, Double minAmount, Double maxAmount) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM orders o ");
        sql.append("LEFT JOIN customers c ON o.customer_id = c.customer_id ");
        sql.append("LEFT JOIN users u ON o.user_id = u.user_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (o.order_number LIKE ? OR c.customer_name LIKE ? OR u.full_name LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (customerId != null) {
            sql.append("AND o.customer_id = ? ");
            params.add(customerId);
        }
        
        if (userId != null) {
            sql.append("AND o.user_id = ? ");
            params.add(userId);
        }
        
        if (tableId != null) {
            sql.append("AND o.table_id = ? ");
            params.add(tableId);
        }
        
        if (status != null) {
            sql.append("AND o.order_status = ? ");
            params.add(status.name());
        }
        
        if (paymentStatus != null) {
            sql.append("AND o.payment_status = ? ");
            params.add(paymentStatus.name());
        }
        
        if (startDate != null) {
            sql.append("AND o.created_at >= ? ");
            params.add(Timestamp.valueOf(startDate));
        }
        
        if (endDate != null) {
            sql.append("AND o.created_at <= ? ");
            params.add(Timestamp.valueOf(endDate));
        }
        
        if (minAmount != null) {
            sql.append("AND o.final_amount >= ? ");
            params.add(minAmount);
        }
        
        if (maxAmount != null) {
            sql.append("AND o.final_amount <= ? ");
            params.add(maxAmount);
        }
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // =====================================================
    // LEGACY METHODS (for backward compatibility)
    // =====================================================

    /**
     * @deprecated Use findAll() instead
     */
    @Deprecated
    public List<Order> getAllOrders() {
        return findAll();
    }

    /**
     * @deprecated Use findById(id).orElse(null) instead
     */
    @Deprecated
    public Order getOrderById(int id) {
        return findById(id).orElse(null);
    }

    /**
     * @deprecated Use findByOrderNumber(orderNumber).orElse(null) instead
     */
    @Deprecated
    public Order getOrderByNumber(String orderNumber) {
        return findByOrderNumber(orderNumber).orElse(null);
    }

    /**
     * @deprecated Use insert(entity) instead
     */
    @Deprecated
    public boolean insertOrder(Order order) {
        return insert(order);
    }

    /**
     * @deprecated Use update(entity) instead
     */
    @Deprecated
    public boolean updateOrder(Order order) {
        return update(order);
    }

    /**
     * @deprecated Use deleteById(id) instead
     */
    @Deprecated
    public boolean deleteOrder(int id) {
        return deleteById(id);
    }

    /**
     * @deprecated Use findByCustomerId(customerId) instead
     */
    @Deprecated
    public List<Order> getOrdersByCustomerId(int customerId) {
        return findByCustomerId(customerId);
    }

    /**
     * @deprecated Use findByUserId(userId) instead
     */
    @Deprecated
    public List<Order> getOrdersByUserId(int userId) {
        return findByUserId(userId);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Order object
     */
    private Order mapResultSetToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setOrderNumber(rs.getString("order_number"));
        order.setTableId(rs.getInt("table_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        order.setUserId(rs.getInt("user_id"));
        order.setTotalAmount(rs.getDouble("total_amount"));
        order.setDiscountAmount(rs.getDouble("discount_amount"));
        order.setTaxAmount(rs.getDouble("tax_amount"));
        order.setFinalAmount(rs.getDouble("final_amount"));
        
        String paymentMethodStr = rs.getString("payment_method");
        if (paymentMethodStr != null) {
            order.setPaymentMethod(PaymentMethod.valueOf(paymentMethodStr));
        }
        
        String paymentStatusStr = rs.getString("payment_status");
        if (paymentStatusStr != null) {
            order.setPaymentStatus(PaymentStatus.valueOf(paymentStatusStr));
        }
        
        String orderStatusStr = rs.getString("order_status");
        if (orderStatusStr != null) {
            order.setOrderStatus(OrderStatus.valueOf(orderStatusStr));
        }
        
        order.setNotes(rs.getString("notes"));
        order.setCreatedAt(rs.getTimestamp("created_at"));
        order.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Load related entities
        Customer customer = new Customer();
        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setCustomerName(rs.getString("customer_name"));
        customer.setPhone(rs.getString("phone"));
        order.setCustomer(customer);
        
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setFullName(rs.getString("full_name"));
        order.setUser(user);
        
        TableCafe table = new TableCafe();
        table.setTableId(rs.getInt("table_id"));
        table.setTableNumber(rs.getString("table_number"));
        table.setTableName(rs.getString("table_name"));
        order.setTable(table);
        
        return order;
    }

    /**
     * Set Order data to PreparedStatement for INSERT/UPDATE
     */
    private void setOrderToPreparedStatement(Order order, PreparedStatement ps) throws SQLException {
        ps.setString(1, order.getOrderNumber());
        ps.setInt(2, order.getTableId());
        ps.setInt(3, order.getCustomerId());
        ps.setInt(4, order.getUserId());
        ps.setDouble(5, order.getTotalAmount());
        ps.setDouble(6, order.getDiscountAmount());
        ps.setDouble(7, order.getTaxAmount());
        ps.setDouble(8, order.getFinalAmount());
        ps.setString(9, order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null);
        ps.setString(10, order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null);
        ps.setString(11, order.getOrderStatus() != null ? order.getOrderStatus().name() : null);
        ps.setString(12, order.getNotes());
        ps.setTimestamp(13, order.getCreatedAt());
        ps.setTimestamp(14, order.getUpdatedAt());
    }
} 