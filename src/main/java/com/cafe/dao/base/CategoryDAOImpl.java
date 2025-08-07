package com.cafe.dao.base;

import com.cafe.model.entity.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của CategoryDAO interface
 * Cải thiện với đầy đủ methods và error handling
 */
public class CategoryDAOImpl implements CategoryDAO {
    private final Connection conn;

    public CategoryDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // =====================================================
    // BASE DAO METHODS
    // =====================================================

    @Override
    public List<Category> findAll() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Category> findById(Integer id) {
        String sql = "SELECT * FROM categories WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Category entity) {
        if (entity.getCategoryId() == 0) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public boolean insert(Category entity) {
        String sql = "INSERT INTO categories (category_name, description, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setCategoryToPreparedStatement(entity, ps);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setCategoryId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Category entity) {
        String sql = "UPDATE categories SET category_name = ?, description = ?, is_active = ?, updated_at = ? " +
                    "WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setCategoryToPreparedStatement(entity, ps);
            ps.setInt(5, entity.getCategoryId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "UPDATE categories SET is_active = false WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Category entity) {
        return deleteById(entity.getCategoryId());
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_id = ?";
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
        String sql = "SELECT COUNT(*) FROM categories";
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
    public List<Category> findAll(int offset, int limit) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories ORDER BY category_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> search(String keyword) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE category_name LIKE ? OR description LIKE ? ORDER BY category_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> search(String keyword, int offset, int limit) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE category_name LIKE ? OR description LIKE ? " +
                    "ORDER BY category_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setInt(3, limit);
            ps.setInt(4, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name LIKE ? OR description LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
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
    // CATEGORY SPECIFIC METHODS
    // =====================================================

    @Override
    public Optional<Category> findByCategoryName(String categoryName) {
        String sql = "SELECT * FROM categories WHERE category_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findActiveCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = true ORDER BY category_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> findActiveCategories(int offset, int limit) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE is_active = true ORDER BY category_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> findByDescription(String description) {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE description LIKE ? ORDER BY category_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + description + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countProductsInCategory(Integer categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND is_active = true";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
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
    public long countActiveCategories() {
        String sql = "SELECT COUNT(*) FROM categories WHERE is_active = true";
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
    public boolean updateActiveStatus(Integer categoryId, boolean isActive) {
        String sql = "UPDATE categories SET is_active = ?, updated_at = ? WHERE category_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, categoryId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsByCategoryName(String categoryName) {
        String sql = "SELECT COUNT(*) FROM categories WHERE category_name = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
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
    public List<Category> searchCategories(String keyword, Boolean isActive) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM categories WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (category_name LIKE ? OR description LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (isActive != null) {
            sql.append("AND is_active = ? ");
            params.add(isActive);
        }
        
        sql.append("ORDER BY category_id");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Category> searchCategories(String keyword, Boolean isActive, int offset, int limit) {
        List<Category> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT * FROM categories WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (category_name LIKE ? OR description LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (isActive != null) {
            sql.append("AND is_active = ? ");
            params.add(isActive);
        }
        
        sql.append("ORDER BY category_id LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToCategory(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearchCategories(String keyword, Boolean isActive) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM categories WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (category_name LIKE ? OR description LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (isActive != null) {
            sql.append("AND is_active = ? ");
            params.add(isActive);
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
     * @deprecated Use insert(entity) instead
     */
    @Deprecated
    public boolean addCategory(Category category) {
        return insert(category);
    }

    /**
     * @deprecated Use update(entity) instead
     */
    @Deprecated
    public boolean updateCategory(Category category) {
        return update(category);
    }

    /**
     * @deprecated Use deleteById(id) instead
     */
    @Deprecated
    public boolean deleteCategory(int categoryId) {
        return deleteById(categoryId);
    }

    /**
     * @deprecated Use findById(id).orElse(null) instead
     */
    @Deprecated
    public Category getCategoryById(int categoryId) {
        return findById(categoryId).orElse(null);
    }

    /**
     * @deprecated Use findAll() instead
     */
    @Deprecated
    public List<Category> getAllCategories() {
        return findAll();
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Category object
     */
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        category.setActive(rs.getBoolean("is_active"));
        category.setCreatedAt(rs.getTimestamp("created_at"));
        category.setUpdatedAt(rs.getTimestamp("updated_at"));
        return category;
    }

    /**
     * Set Category data to PreparedStatement for INSERT/UPDATE
     */
    private void setCategoryToPreparedStatement(Category category, PreparedStatement ps) throws SQLException {
        ps.setString(1, category.getCategoryName());
        ps.setString(2, category.getDescription());
        ps.setBoolean(3, category.isActive());
        ps.setTimestamp(4, category.getCreatedAt());
        ps.setTimestamp(5, category.getUpdatedAt());
    }
}