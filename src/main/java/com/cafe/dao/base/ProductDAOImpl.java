package com.cafe.dao.base;

import com.cafe.model.entity.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ProductDAOImpl implements ProductDAO {
    private final Connection conn;

    public ProductDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return extractProductFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean insertProduct(Product product) {
        String sql = "INSERT INTO products (product_name, category_id, price, cost_price, description, image_url, is_available, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setProductToPreparedStatement(product, ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateProduct(Product product) {
        String sql = "UPDATE products SET product_name=?, category_id=?, price=?, cost_price=?, description=?, image_url=?, is_available=?, is_active=? WHERE product_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            setProductToPreparedStatement(product, ps);
            ps.setInt(9, product.getProductId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteProduct(int id) {
        String sql = "UPDATE products SET is_active = FALSE WHERE product_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Product> searchProductsByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE product_name LIKE ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Product p = extractProductFromResultSet(rs);
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
// ========================== Helper Methods ==========================

    private Product extractProductFromResultSet(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setProductName(rs.getString("product_name"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setPrice(rs.getDouble("price"));
        p.setCostPrice(rs.getDouble("cost_price"));
        p.setDescription(rs.getString("description"));
        p.setImageUrl(rs.getString("image_url"));
        p.setAvailable(rs.getBoolean("is_available"));
        p.setActive(rs.getBoolean("is_active"));
        
        // Set additional fields with defaults if they don't exist
        try {
            p.setStockQuantity(rs.getInt("stock_quantity"));
        } catch (SQLException e) {
            p.setStockQuantity(10); // Default stock quantity
        }
        
        try {
            p.setSku(rs.getString("sku"));
        } catch (SQLException e) {
            p.setSku("SKU" + p.getProductId()); // Default SKU
        }
        
        return p;
    }

    private void setProductToPreparedStatement(Product product, PreparedStatement ps) throws SQLException {
        ps.setString(1, product.getProductName());
        ps.setInt(2, product.getCategoryId());
        ps.setDouble(3, product.getPrice());
        ps.setDouble(4, product.getCostPrice());
        ps.setString(5, product.getDescription());
        ps.setString(6, product.getImageUrl());
        ps.setBoolean(7, product.isAvailable());
        ps.setBoolean(8, product.isActive());
    }

    // ========================== Additional Methods ==========================
    
    @Override
    public List<Product> findAll() {
        return getAllProducts();
    }

    @Override
    public java.util.Optional<Product> findById(Integer id) {
        Product product = getProductById(id);
        return product != null ? java.util.Optional.of(product) : java.util.Optional.empty();
    }

    @Override
    public List<Product> findByCategoryId(Integer categoryId) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE category_id = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    list.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (product_name LIKE ? OR description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }

        if (categoryId != null) {
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }

        if (isAvailable != null) {
            sql.append(" AND is_available = ?");
            params.add(isAvailable);
        }

        sql.append(" AND is_active = TRUE ORDER BY product_name");

        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    list.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public java.util.Optional<Product> findBySku(String sku) {
        if (sku == null || sku.trim().isEmpty()) {
            return java.util.Optional.empty();
        }
        
        String sql = "SELECT * FROM products WHERE sku = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sku);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = extractProductFromResultSet(rs);
                    return java.util.Optional.of(product);
                }
            }
        } catch (SQLException e) {
            // Column 'sku' might not exist, so we'll handle it gracefully
            System.out.println("SKU search not available - column might not exist");
        }
        return java.util.Optional.empty();
    }

    @Override
    public List<Product> findAvailableProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_available = TRUE AND is_active = TRUE ORDER BY product_name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                list.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Product> findLowStockProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE is_active = TRUE ORDER BY product_name";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Product product = extractProductFromResultSet(rs);
                if (product.isLowStock()) {
                    list.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countByCategoryId(Integer categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND is_active = TRUE";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public long countAvailableProducts() {
        String sql = "SELECT COUNT(*) FROM products WHERE is_available = TRUE AND is_active = TRUE";
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
}