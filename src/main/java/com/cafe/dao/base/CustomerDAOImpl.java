package com.cafe.dao.base;

import com.cafe.model.entity.Customer;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of CustomerDAO interface
 */
public class CustomerDAOImpl implements CustomerDAO {
    private final Connection conn;

    public CustomerDAOImpl(Connection conn) {
        this.conn = conn;
    }

    /**
     * Get all customers
     */
    @Override
    public List<Customer> getAllCustomers() throws SQLException {
        List<Customer> customers = new ArrayList<>();
        String sql = """
            SELECT * FROM customers 
            WHERE is_active = TRUE 
            ORDER BY full_name
        """;

        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                customer.setAddress(rs.getString("address"));
                customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                customer.setTotalSpent(rs.getDouble("total_spent"));
                customer.setActive(rs.getBoolean("is_active"));
                customer.setCreatedAt(rs.getTimestamp("created_at"));

                customers.add(customer);
            }
        }

        return customers;
    }

    /**
     * Get new customers this month
     */
    @Override
    public int getNewCustomersThisMonth() throws SQLException {
        String sql = """
            SELECT COUNT(*) as new_customers
            FROM customers 
            WHERE YEAR(created_at) = YEAR(CURDATE()) 
            AND MONTH(created_at) = MONTH(CURDATE())
            AND is_active = TRUE
        """;

        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("new_customers");
            }
            return 0;
        }
    }

    /**
     * Get top customers by spending within date range
     */
    @Override
    public List<CustomerSpendingData> getTopCustomers(LocalDate startDate, LocalDate endDate, int limit) throws SQLException {
        List<CustomerSpendingData> result = new ArrayList<>();
        String sql = """
            SELECT 
                c.customer_id,
                c.full_name,
                c.phone,
                c.email,
                COUNT(o.order_id) as order_count,
                COALESCE(SUM(o.final_amount), 0) as total_spent,
                MAX(o.order_date) as last_order_date,
                COALESCE(AVG(o.final_amount), 0) as avg_order_value
            FROM customers c
            LEFT JOIN orders o ON c.customer_id = o.customer_id
            WHERE (o.order_date IS NULL OR DATE(o.order_date) BETWEEN ? AND ?)
            AND o.payment_status = 'paid'
            GROUP BY c.customer_id, c.full_name, c.phone, c.email
            HAVING order_count > 0
            ORDER BY total_spent DESC
            LIMIT ?
        """;

        try (PreparedStatement stmt = this.conn.prepareStatement(sql)) {

            stmt.setDate(1, Date.valueOf(startDate));
            stmt.setDate(2, Date.valueOf(endDate));
            stmt.setInt(3, limit);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CustomerSpendingData data = new CustomerSpendingData();
                    data.setCustomerId(rs.getInt("customer_id"));
                    data.setFullName(rs.getString("full_name"));
                    data.setPhone(rs.getString("phone"));
                    data.setEmail(rs.getString("email"));
                    data.setOrderCount(rs.getInt("order_count"));
                    data.setTotalSpent(rs.getDouble("total_spent"));
                    data.setAvgOrderValue(rs.getDouble("avg_order_value"));

                    Timestamp lastOrderTimestamp = rs.getTimestamp("last_order_date");
                    if (lastOrderTimestamp != null) {
                        data.setLastOrderDate(lastOrderTimestamp.toLocalDateTime().toLocalDate());
                    }

                    result.add(data);
                }
            }
        }

        return result;
    }

    /**
     * Get customer statistics
     */
    @Override
    public CustomerStatistics getCustomerStatistics() throws SQLException {
        CustomerStatistics stats = new CustomerStatistics();

        // Total customers
        String totalSql = "SELECT COUNT(*) as total FROM customers WHERE is_active = TRUE";

        // New customers this month
        String newSql = """
            SELECT COUNT(*) as new_this_month 
            FROM customers 
            WHERE YEAR(created_at) = YEAR(CURDATE()) 
            AND MONTH(created_at) = MONTH(CURDATE())
            AND is_active = TRUE
        """;

        // Active customers (customers with orders in last 3 months)
        String activeSql = """
            SELECT COUNT(DISTINCT c.customer_id) as active_customers
            FROM customers c
            JOIN orders o ON c.customer_id = o.customer_id
            WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)
            AND c.is_active = TRUE
        """;

        // Get total customers
        try (Statement stmt = this.conn.createStatement();
             ResultSet rs = stmt.executeQuery(totalSql)) {
                if (rs.next()) {
                    stats.setTotalCustomers(rs.getInt("total"));
                }
            }

            // Get new customers this month
            try (Statement stmt = this.conn.createStatement();
                 ResultSet rs = stmt.executeQuery(newSql)) {
                if (rs.next()) {
                    stats.setNewCustomersThisMonth(rs.getInt("new_this_month"));
                }
            }

            // Get active customers
            try (Statement stmt = this.conn.createStatement();
                 ResultSet rs = stmt.executeQuery(activeSql)) {
                if (rs.next()) {
                    stats.setActiveCustomers(rs.getInt("active_customers"));
                }
            }

        return stats;
    }
    
    @Override
    public boolean updateCustomerLoyaltyPoints(int customerId, int points) throws SQLException {
        String sql = "UPDATE customers SET loyalty_points = loyalty_points + ?, updated_at = CURRENT_TIMESTAMP WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, points);
            ps.setInt(2, customerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Customer getCustomerById(int customerId) throws SQLException {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                    customer.setTotalSpent(rs.getDouble("total_spent"));
                    customer.setActive(rs.getBoolean("is_active"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    return customer;
                }
            }
        }
        return null;
    }

    @Override
    public Customer findCustomerByPhone(String phone) throws SQLException {
        String sql = "SELECT * FROM customers WHERE phone = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                    customer.setTotalSpent(rs.getDouble("total_spent"));
                    customer.setActive(rs.getBoolean("is_active"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    return customer;
                }
            }
        }
        return null;
    }

    @Override
    public Customer findCustomerByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM customers WHERE email = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Customer customer = new Customer();
                    customer.setCustomerId(rs.getInt("customer_id"));
                    customer.setFullName(rs.getString("full_name"));
                    customer.setPhone(rs.getString("phone"));
                    customer.setEmail(rs.getString("email"));
                    customer.setAddress(rs.getString("address"));
                    customer.setLoyaltyPoints(rs.getInt("loyalty_points"));
                    customer.setTotalSpent(rs.getDouble("total_spent"));
                    customer.setActive(rs.getBoolean("is_active"));
                    customer.setCreatedAt(rs.getTimestamp("created_at"));
                    return customer;
                }
            }
        }
        return null;
    }

    @Override
    public boolean addCustomer(Customer customer) throws SQLException {
        return insertCustomer(customer);
    }

    @Override
    public boolean insertCustomer(Customer customer) throws SQLException {
        String sql = "INSERT INTO customers (full_name, phone, email, address, loyalty_points, total_spent, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getLoyaltyPoints());
            ps.setDouble(6, customer.getTotalSpent());
            ps.setBoolean(7, customer.isActive());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean updateCustomer(Customer customer) throws SQLException {
        String sql = "UPDATE customers SET full_name = ?, phone = ?, email = ?, address = ?, loyalty_points = ?, total_spent = ?, is_active = ?, updated_at = CURRENT_TIMESTAMP WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, customer.getFullName());
            ps.setString(2, customer.getPhone());
            ps.setString(3, customer.getEmail());
            ps.setString(4, customer.getAddress());
            ps.setInt(5, customer.getLoyaltyPoints());
            ps.setDouble(6, customer.getTotalSpent());
            ps.setBoolean(7, customer.isActive());
            ps.setInt(8, customer.getCustomerId());
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean deleteCustomer(int customerId) throws SQLException {
        String sql = "UPDATE customers SET is_active = FALSE, updated_at = CURRENT_TIMESTAMP WHERE customer_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, customerId);
            return ps.executeUpdate() > 0;
        }
    }
}
