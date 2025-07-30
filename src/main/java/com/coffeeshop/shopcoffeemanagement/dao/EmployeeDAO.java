package com.coffeeshop.shopcoffeemanagement.dao;

import com.coffeeshop.shopcoffeemanagement.model.Employee;
import com.coffeeshop.shopcoffeemanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {
    
    private DatabaseConnection dbConnection;
    
    public EmployeeDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public Employee findByUsername(String username) {
        String sql = "SELECT * FROM Employee WHERE username = ? AND active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối database: " + e.getMessage());
        }
        
        return null;
    }
    
    public Employee findById(Long id) {
        String sql = "SELECT * FROM Employee WHERE id = ? AND active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToEmployee(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối database: " + e.getMessage());
        }
        
        return null;
    }
    
    public List<Employee> findAll() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM Employee WHERE active = 1 ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối database: " + e.getMessage());
        }
        
        return employees;
    }
    
    public boolean save(Employee employee) {
        String sql = "INSERT INTO Employee (username, password, name, email, phone, role, active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getName());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getRole());
            stmt.setBoolean(7, employee.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    employee.setId(rs.getLong(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean update(Employee employee) {
        String sql = "UPDATE Employee SET username = ?, password = ?, name = ?, email = ?, phone = ?, " +
                    "role = ?, active = ?, updated_at = GETDATE() WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, employee.getUsername());
            stmt.setString(2, employee.getPassword());
            stmt.setString(3, employee.getName());
            stmt.setString(4, employee.getEmail());
            stmt.setString(5, employee.getPhone());
            stmt.setString(6, employee.getRole());
            stmt.setBoolean(7, employee.isActive());
            stmt.setLong(8, employee.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean delete(Long id) {
        String sql = "UPDATE Employee SET active = 0, updated_at = GETDATE() WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getLong("id"));
        employee.setUsername(rs.getString("username"));
        employee.setPassword(rs.getString("password"));
        employee.setName(rs.getString("name"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setRole(rs.getString("role"));
        employee.setActive(rs.getBoolean("active"));
        employee.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        employee.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return employee;
    }
    
    // Removed demo data fallback - all data must come from database
} 