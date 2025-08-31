package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.CategoryDAO;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.CategoryDAOImpl;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class cho menu operations
 * Chứa business logic cho việc quản lý menu, products và categories
 */
public class MenuService {
    
    public MenuService() {
        // ✅ REMOVED: No longer getting connection in constructor
        // Connections will be managed per operation using try-with-resources
    }
    
    /**
     * Lấy tất cả categories đang active
     */
    public List<Category> getAvailableCategories() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAOImpl(conn);
            return categoryDAO.getAllCategories().stream()
                    .filter(category -> category.isActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading categories: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy products theo category
     */
    public List<Product> getProductsByCategory(Integer categoryId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            // ✅ HỦY CHECK HẾT HÀNG - Chỉ check active
            return productDAO.findByCategoryId(categoryId).stream()
                    .filter(product -> product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading products for category " + categoryId + ": " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy products theo category với phân trang
     */
    public List<Product> getProductsByCategory(Integer categoryId, int offset, int limit) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.findByCategoryId(categoryId).stream()
                    .filter(product -> product.getIsAvailable() && product.getIsActive())
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading products for category " + categoryId + ": " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Tìm kiếm products theo keyword
     */
    public List<Product> searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.searchProducts(keyword.trim(), null, true, null, null).stream()
                    .filter(product -> product.getIsAvailable() && product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Tìm kiếm products theo keyword và category
     */
    public List<Product> searchProducts(String keyword, Integer categoryId) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getProductsByCategory(categoryId);
        }
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            // ✅ HỦY CHECK HẾT HÀNG - Chỉ check active
            return productDAO.searchProducts(keyword.trim(), categoryId, true, null, null).stream()
                    .filter(product -> product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Get total products count
     */
    public int getTotalProductsCount() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.getAllProducts().size();
        } catch (Exception e) {
            System.err.println("Error getting total products count: " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Lấy product theo ID
     */
    public Optional<Product> getProductById(Integer productId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.findById(productId);
        } catch (Exception e) {
            System.err.println("Error getting product by ID " + productId + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Lấy product theo SKU
     */
    public Optional<Product> getProductBySku(String sku) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.findBySku(sku);
        } catch (Exception e) {
            System.err.println("Error getting product by SKU " + sku + ": " + e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * Kiểm tra product có thể order được không
     */
    public boolean canOrderProduct(Product product, int quantity) {
        // ✅ HỦY CHECK HẾT HÀNG - Luôn cho phép order
        return product != null;
    }
    
    /**
     * Lấy tất cả available products
     */
    public List<Product> getAllAvailableProducts() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            // ✅ HỦY CHECK HẾT HÀNG - Lấy tất cả sản phẩm active
            return productDAO.findAvailableProducts().stream()
                    .filter(product -> product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading all available products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy products có stock thấp
     */
    public List<Product> getLowStockProducts() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.findLowStockProducts().stream()
                    .filter(product -> product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error loading low stock products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Đếm số products theo category
     */
    public long countProductsByCategory(Integer categoryId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.countByCategoryId(categoryId);
        } catch (Exception e) {
            System.err.println("Error counting products for category " + categoryId + ": " + e.getMessage());
            return 0;
        }
    }
    
    /**
     * Đếm số available products
     */
    public long countAvailableProducts() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(conn);
            return productDAO.countAvailableProducts();
        } catch (Exception e) {
            System.err.println("Error counting available products: " + e.getMessage());
            return 0;
        }
    }
}
