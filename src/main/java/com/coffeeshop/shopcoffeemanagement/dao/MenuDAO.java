package com.coffeeshop.shopcoffeemanagement.dao;

import com.coffeeshop.shopcoffeemanagement.model.Menu;
import com.coffeeshop.shopcoffeemanagement.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAO {
    
    private DatabaseConnection dbConnection;
    
    public MenuDAO() {
        this.dbConnection = DatabaseConnection.getInstance();
    }
    
    public List<Menu> findAll() {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE available = 1 ORDER BY category, name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                menus.add(mapResultSetToMenu(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể kết nối database: " + e.getMessage());
        }
        
        return menus;
    }
    
    public List<Menu> findByCategory(String category) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE category = ? AND available = 1 ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, category);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                menus.add(mapResultSetToMenu(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return menus;
    }
    
    public List<Menu> searchByName(String searchTerm) {
        List<Menu> menus = new ArrayList<>();
        String sql = "SELECT * FROM Menu WHERE name LIKE ? AND available = 1 ORDER BY name";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                menus.add(mapResultSetToMenu(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return menus;
    }
    
    public boolean delete(Long id) {
        String sql = "UPDATE Menu SET available = 0, updated_at = GETDATE() WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean save(Menu menu) {
        String sql = "INSERT INTO Menu (name, description, price, category, available, image_url, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, GETDATE(), GETDATE())";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, menu.getName());
            stmt.setString(2, menu.getDescription());
            stmt.setBigDecimal(3, menu.getPrice());
            stmt.setString(4, menu.getCategory());
            stmt.setBoolean(5, menu.isAvailable());
            stmt.setString(6, menu.getImageUrl());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    menu.setId(rs.getLong(1));
                }
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    public boolean update(Menu menu) {
        String sql = "UPDATE Menu SET name = ?, description = ?, price = ?, category = ?, available = ?, updated_at = GETDATE() WHERE id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, menu.getName());
            stmt.setString(2, menu.getDescription());
            stmt.setBigDecimal(3, menu.getPrice());
            stmt.setString(4, menu.getCategory());
            stmt.setBoolean(5, menu.isAvailable());
            stmt.setLong(6, menu.getId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    private Menu mapResultSetToMenu(ResultSet rs) throws SQLException {
        Menu menu = new Menu();
        menu.setId(rs.getLong("id"));
        menu.setName(rs.getString("name"));
        menu.setDescription(rs.getString("description"));
        menu.setPrice(rs.getBigDecimal("price"));
        menu.setCategory(rs.getString("category"));
        menu.setAvailable(rs.getBoolean("available"));
        menu.setImageUrl(rs.getString("image_url"));
        menu.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        menu.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return menu;
    }
    
    // Removed demo data fallback - all data must come from database
} 