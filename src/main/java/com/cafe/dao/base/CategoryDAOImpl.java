package com.cafe.dao.base;

import com.cafe.model.entity.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAOImpl implements CategoryDAO {
    private final Connection conn;

    public CategoryDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO categories (category_name, description, is_active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setBoolean(3, category.isActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean updateCategory(Category category) {
        String sql = "UPDATE categories SET category_name = ?, description = ?, is_active = ? WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category.getCategoryName());
            stmt.setString(2, category.getDescription());
            stmt.setBoolean(3, category.isActive());
            stmt.setInt(4, category.getCategoryId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean deleteCategory(int categoryId) {
        String sql = "DELETE FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Category(
                            rs.getInt("category_id"),
                            rs.getString("category_name"),
                            rs.getString("description"),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Category category = new Category(
                        rs.getInt("category_id"),
                        rs.getString("category_name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> findAll() {
        return getAllCategories();
    }
}