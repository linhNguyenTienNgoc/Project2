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
            // Fallback to demo data
            menus = getDemoMenus();
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
    
    // Demo fallback methods
    private List<Menu> getDemoMenus() {
        List<Menu> menus = new ArrayList<>();
        
        // Coffee
        menus.add(new Menu("Cà phê đen", "Cà phê đen truyền thống, đậm đà hương vị", new java.math.BigDecimal("25000"), "COFFEE"));
        menus.add(new Menu("Cà phê sữa", "Cà phê sữa đặc, ngọt ngào", new java.math.BigDecimal("30000"), "COFFEE"));
        menus.add(new Menu("Cappuccino", "Cappuccino Ý, bọt sữa mịn", new java.math.BigDecimal("45000"), "COFFEE"));
        menus.add(new Menu("Latte", "Latte mượt mà, hương vị nhẹ nhàng", new java.math.BigDecimal("50000"), "COFFEE"));
        menus.add(new Menu("Espresso", "Espresso đậm đà, tinh khiết", new java.math.BigDecimal("35000"), "COFFEE"));
        
        // Tea
        menus.add(new Menu("Trà sữa trân châu", "Trà sữa trân châu đường đen", new java.math.BigDecimal("35000"), "TEA"));
        menus.add(new Menu("Trà đào", "Trà đào mát lạnh, thơm ngon", new java.math.BigDecimal("30000"), "TEA"));
        menus.add(new Menu("Trà chanh", "Trà chanh tươi, giải nhiệt", new java.math.BigDecimal("25000"), "TEA"));
        
        // Juice
        menus.add(new Menu("Nước ép cam", "Nước ép cam tươi, giàu vitamin C", new java.math.BigDecimal("40000"), "JUICE"));
        menus.add(new Menu("Nước ép táo", "Nước ép táo tươi, ngọt tự nhiên", new java.math.BigDecimal("45000"), "JUICE"));
        
        // Dessert
        menus.add(new Menu("Bánh tiramisu", "Bánh tiramisu Ý, hương vị đặc biệt", new java.math.BigDecimal("55000"), "DESSERT"));
        menus.add(new Menu("Bánh cheesecake", "Cheesecake mịn màng, béo ngậy", new java.math.BigDecimal("50000"), "DESSERT"));
        menus.add(new Menu("Bánh chocolate", "Bánh chocolate đậm đà", new java.math.BigDecimal("45000"), "DESSERT"));
        menus.add(new Menu("Kem vanilla", "Kem vanilla mát lạnh", new java.math.BigDecimal("30000"), "DESSERT"));
        menus.add(new Menu("Kem chocolate", "Kem chocolate đậm đà", new java.math.BigDecimal("35000"), "DESSERT"));
        
        // Food
        menus.add(new Menu("Bánh mì sandwich", "Bánh mì sandwich thịt nguội", new java.math.BigDecimal("35000"), "FOOD"));
        menus.add(new Menu("Bánh mì bơ", "Bánh mì bơ thơm béo", new java.math.BigDecimal("25000"), "FOOD"));
        
        // Smoothie
        menus.add(new Menu("Sinh tố dâu", "Sinh tố dâu tươi, mát lạnh", new java.math.BigDecimal("40000"), "SMOOTHIE"));
        menus.add(new Menu("Sinh tố xoài", "Sinh tố xoài chín, ngọt tự nhiên", new java.math.BigDecimal("45000"), "SMOOTHIE"));
        
        return menus;
    }
} 