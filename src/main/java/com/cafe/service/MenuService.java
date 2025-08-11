package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.CategoryDAO;
import com.cafe.dao.base.ProductDAO;
import com.cafe.dao.base.CategoryDAOImpl;
import com.cafe.dao.base.ProductDAOImpl;
import com.cafe.model.entity.Category;
import com.cafe.model.entity.Product;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class cho menu operations
 * Chứa business logic cho việc quản lý menu, products và categories
 */
public class MenuService {
    
    private final ProductDAO productDAO;
    private final CategoryDAO categoryDAO;
    
    public MenuService() {
        try {
            this.productDAO = new ProductDAOImpl(DatabaseConfig.getConnection());
            this.categoryDAO = new CategoryDAOImpl(DatabaseConfig.getConnection());
        } catch (Exception e) {
            System.err.println("Error initializing MenuService: " + e.getMessage());
            throw new RuntimeException("Failed to initialize MenuService", e);
        }
    }
    
    /**
     * Lấy tất cả categories đang active
     */
    public List<Category> getAvailableCategories() {
        try {
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
        try {
            return productDAO.findByCategoryId(categoryId).stream()
                    .filter(product -> product.getIsAvailable() && product.getIsActive())
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
        try {
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
        
        try {
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
        
        try {
            return productDAO.searchProducts(keyword.trim(), categoryId, true, null, null).stream()
                    .filter(product -> product.getIsAvailable() && product.getIsActive())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.err.println("Error searching products: " + e.getMessage());
            return List.of();
        }
    }
    
    /**
     * Lấy product theo ID
     */
    public Optional<Product> getProductById(Integer productId) {
        try {
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
        try {
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
        if (product == null || !product.getIsAvailable() || !product.getIsActive()) {
            return false;
        }
        
        if (product.getStockQuantity() == null || product.getStockQuantity() < quantity) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Lấy tất cả available products
     */
    public List<Product> getAllAvailableProducts() {
        try {
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
        try {
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
        try {
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
        try {
            return productDAO.countAvailableProducts();
        } catch (Exception e) {
            System.err.println("Error counting available products: " + e.getMessage());
            return 0;
        }
    }
}
