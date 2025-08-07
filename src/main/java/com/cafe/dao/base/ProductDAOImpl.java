package com.cafe.dao.base;

import com.cafe.model.entity.Product;
import com.cafe.model.entity.Category;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của ProductDAO interface
 * Cải thiện với đầy đủ methods và error handling
 */
public class ProductDAOImpl implements ProductDAO {
    private final Connection conn;

    public ProductDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // =====================================================
    // BASE DAO METHODS
    // =====================================================

    @Override
    public List<Product> findAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<Product> findById(Integer id) {
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean save(Product entity) {
        if (entity.getProductId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public boolean insert(Product entity) {
        String sql = "INSERT INTO products (product_name, category_id, sku, price, cost_price, description, image_url, stock_quantity, min_stock_level, is_available, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProductToPreparedStatement(entity, ps);
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setProductId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Product entity) {
        String sql = "UPDATE products SET product_name = ?, category_id = ?, sku = ?, price = ?, cost_price = ?, " +
                    "description = ?, image_url = ?, stock_quantity = ?, min_stock_level = ?, is_available = ?, " +
                    "is_active = ?, updated_at = ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setProductToPreparedStatement(entity, ps);
            ps.setInt(13, entity.getProductId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "UPDATE products SET is_active = false WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Product entity) {
        return deleteById(entity.getProductId());
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM products WHERE product_id = ?";
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
        String sql = "SELECT COUNT(*) FROM products WHERE is_active = true";
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
    public List<Product> findAll(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_active = true ORDER BY p.product_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_active = true AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?) " +
                    "ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> search(String keyword, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_active = true AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?) " +
                    "ORDER BY p.product_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM products p " +
                    "WHERE p.is_active = true AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?)";
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
    // PRODUCT SPECIFIC METHODS
    // =====================================================

    @Override
    public Optional<Product> findBySku(String sku) {
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.sku = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.category_id = ? AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByCategoryName(String categoryName) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE c.category_name = ? AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findAvailableProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_available = true AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findAvailableProducts(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.is_available = true AND p.is_active = true ORDER BY p.product_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.stock_quantity <= p.min_stock_level AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findOutOfStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.stock_quantity <= 0 AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT p.*, c.category_name FROM products p " +
                    "LEFT JOIN categories c ON p.category_id = c.category_id " +
                    "WHERE p.price BETWEEN ? AND ? AND p.is_active = true ORDER BY p.product_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, minPrice);
            ps.setDouble(2, maxPrice);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countByCategoryId(Integer categoryId) {
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
    public long countAvailableProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_available = true AND is_active = true";
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
    public long countLowStockProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= min_stock_level AND is_active = true";
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
    public long countOutOfStockProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE stock_quantity <= 0 AND is_active = true";
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
    public boolean updateStockQuantity(Integer productId, Integer quantity) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity + ?, updated_at = ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateAvailableStatus(Integer productId, boolean isAvailable) {
        String sql = "UPDATE products SET is_available = ?, updated_at = ? WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isAvailable);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, productId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsBySku(String sku) {
        String sql = "SELECT COUNT(*) FROM products WHERE sku = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
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
    public List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, c.category_name FROM products p ");
        sql.append("LEFT JOIN categories c ON p.category_id = c.category_id ");
        sql.append("WHERE p.is_active = true ");
        
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        
        if (isAvailable != null) {
            sql.append("AND p.is_available = ? ");
            params.add(isAvailable);
        }
        
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        
        sql.append("ORDER BY p.product_id");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.*, c.category_name FROM products p ");
        sql.append("LEFT JOIN categories c ON p.category_id = c.category_id ");
        sql.append("WHERE p.is_active = true ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        
        if (isAvailable != null) {
            sql.append("AND p.is_available = ? ");
            params.add(isAvailable);
        }
        
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
        }
        
        sql.append("ORDER BY p.product_id LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM products p ");
        sql.append("LEFT JOIN categories c ON p.category_id = c.category_id ");
        sql.append("WHERE p.is_active = true ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (p.product_name LIKE ? OR p.description LIKE ? OR p.sku LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (categoryId != null) {
            sql.append("AND p.category_id = ? ");
            params.add(categoryId);
        }
        
        if (isAvailable != null) {
            sql.append("AND p.is_available = ? ");
            params.add(isAvailable);
        }
        
        if (minPrice != null) {
            sql.append("AND p.price >= ? ");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append("AND p.price <= ? ");
            params.add(maxPrice);
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
    public List<Product> getAllProducts() {
        return findAll();
    }

    /**
     * @deprecated Use findById(id).orElse(null) instead
     */
    @Deprecated
    public Product getProductById(int id) {
        return findById(id).orElse(null);
    }

    /**
     * @deprecated Use insert(entity) instead
     */
    @Deprecated
    public boolean insertProduct(Product product) {
        return insert(product);
    }

    /**
     * @deprecated Use update(entity) instead
     */
    @Deprecated
    public boolean updateProduct(Product product) {
        return update(product);
    }

    /**
     * @deprecated Use deleteById(id) instead
     */
    @Deprecated
    public boolean deleteProduct(int id) {
        return deleteById(id);
    }

    /**
     * @deprecated Use search(keyword) instead
     */
    @Deprecated
    public List<Product> searchProductsByName(String keyword) {
        return search(keyword);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Product object
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setProductId(rs.getInt("product_id"));
        product.setProductName(rs.getString("product_name"));
        product.setCategoryId(rs.getInt("category_id"));
        product.setSku(rs.getString("sku"));
        product.setPrice(rs.getDouble("price"));
        product.setCostPrice(rs.getDouble("cost_price"));
        product.setDescription(rs.getString("description"));
        product.setImageUrl(rs.getString("image_url"));
        product.setStockQuantity(rs.getInt("stock_quantity"));
        product.setMinStockLevel(rs.getInt("min_stock_level"));
        product.setIsAvailable(rs.getBoolean("is_available"));
        product.setIsActive(rs.getBoolean("is_active"));
        product.setCreatedAt(rs.getTimestamp("created_at"));
        product.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Load category information
        Category category = new Category();
        category.setCategoryId(rs.getInt("category_id"));
        category.setCategoryName(rs.getString("category_name"));
        product.setCategory(category);
        
        return product;
    }

    /**
     * Set Product data to PreparedStatement for INSERT/UPDATE
     */
    private void setProductToPreparedStatement(Product product, PreparedStatement ps) throws SQLException {
        ps.setString(1, product.getProductName());
        ps.setInt(2, product.getCategoryId());
        ps.setString(3, product.getSku());
        ps.setDouble(4, product.getPrice());
        ps.setDouble(5, product.getCostPrice());
        ps.setString(6, product.getDescription());
        ps.setString(7, product.getImageUrl());
        ps.setInt(8, product.getStockQuantity());
        ps.setInt(9, product.getMinStockLevel());
        ps.setBoolean(10, product.getIsAvailable());
        ps.setBoolean(11, product.getIsActive());
        ps.setTimestamp(12, product.getCreatedAt());
        ps.setTimestamp(13, product.getUpdatedAt());
    }
}