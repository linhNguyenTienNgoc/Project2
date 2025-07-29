package com.coffeeshop.shopcoffeemanagement.dao;

import com.coffeeshop.shopcoffeemanagement.model.CoffeeTable;
import com.coffeeshop.shopcoffeemanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CoffeeTableDAO {
    
    private DatabaseConnection dbConnection;
    
    public CoffeeTableDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<CoffeeTable> findAll() {
        List<CoffeeTable> tables = new ArrayList<>();
        String sql = "SELECT * FROM CoffeeTable WHERE active = 1 ORDER BY table_number";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tables.add(mapResultSetToCoffeeTable(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // Fallback to demo data
            tables = getDemoTables();
        }
        
        return tables;
    }
    
    public CoffeeTable findById(Long id) {
        String sql = "SELECT * FROM CoffeeTable WHERE id = ? AND active = 1";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCoffeeTable(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    public boolean updateStatus(Long tableId, String status) {
        String sql = "UPDATE CoffeeTable SET status = ?, updated_at = GETDATE() WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setLong(2, tableId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean save(CoffeeTable table) {
        String sql = "INSERT INTO CoffeeTable (table_number, capacity, status, location, active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, table.getTableNumber());
            stmt.setInt(2, table.getCapacity());
            stmt.setString(3, table.getStatus());
            stmt.setString(4, table.getLocation());
            stmt.setBoolean(5, table.isActive());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    table.setId(rs.getLong(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private CoffeeTable mapResultSetToCoffeeTable(ResultSet rs) throws SQLException {
        CoffeeTable table = new CoffeeTable();
        table.setId(rs.getLong("id"));
        table.setTableNumber(rs.getString("table_number"));
        table.setCapacity(rs.getInt("capacity"));
        table.setStatus(rs.getString("status"));
        table.setLocation(rs.getString("location"));
        table.setActive(rs.getBoolean("active"));
        table.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        table.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return table;
    }
    
    // Demo fallback methods
    private List<CoffeeTable> getDemoTables() {
        List<CoffeeTable> tables = new ArrayList<>();
        tables.add(new CoffeeTable("T01", 4, "Khu vực A - Gần cửa sổ"));
        tables.add(new CoffeeTable("T02", 4, "Khu vực A - Gần cửa sổ"));
        tables.add(new CoffeeTable("T03", 6, "Khu vực B - Giữa quán"));
        tables.add(new CoffeeTable("T04", 6, "Khu vực B - Giữa quán"));
        tables.add(new CoffeeTable("T05", 8, "Khu vực C - Góc yên tĩnh"));
        tables.add(new CoffeeTable("T06", 4, "Khu vực A - Gần cửa sổ"));
        tables.add(new CoffeeTable("T07", 4, "Khu vực B - Giữa quán"));
        tables.add(new CoffeeTable("T08", 6, "Khu vực C - Góc yên tĩnh"));
        tables.add(new CoffeeTable("T09", 4, "Khu vực A - Gần cửa sổ"));
        tables.add(new CoffeeTable("T10", 8, "Khu vực C - Góc yên tĩnh"));
        
        // Set status cho một số bàn
        tables.get(4).setStatus("OCCUPIED"); // T05
        tables.get(6).setStatus("RESERVED"); // T07
        
        return tables;
    }
} 