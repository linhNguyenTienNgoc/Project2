package com.cafe.dao.base;

import com.cafe.model.entity.Product;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface cho Product entity
 * Kế thừa BaseDAO và thêm các methods đặc thù cho Product
 */
public interface ProductDAO extends BaseDAO<Product, Integer> {
    
    /**
     * Tìm product theo SKU
     * @param sku SKU code
     * @return Optional of Product
     */
    Optional<Product> findBySku(String sku);
    
    /**
     * Tìm products theo category
     * @param categoryId Category ID
     * @return List of Products
     */
    List<Product> findByCategoryId(Integer categoryId);
    
    /**
     * Tìm products theo category name
     * @param categoryName Category name
     * @return List of Products
     */
    List<Product> findByCategoryName(String categoryName);
    
    /**
     * Tìm products đang available
     * @return List of available Products
     */
    List<Product> findAvailableProducts();
    
    /**
     * Tìm products đang available có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of available Products
     */
    List<Product> findAvailableProducts(int offset, int limit);
    
    /**
     * Tìm products có stock thấp
     * @return List of Products with low stock
     */
    List<Product> findLowStockProducts();
    
    /**
     * Tìm products hết hàng
     * @return List of out-of-stock Products
     */
    List<Product> findOutOfStockProducts();
    
    /**
     * Tìm products theo khoảng giá
     * @param minPrice Minimum price
     * @param maxPrice Maximum price
     * @return List of Products
     */
    List<Product> findByPriceRange(Double minPrice, Double maxPrice);
    
    /**
     * Đếm số products theo category
     * @param categoryId Category ID
     * @return Count of products
     */
    long countByCategoryId(Integer categoryId);
    
    /**
     * Đếm số products đang available
     * @return Count of available products
     */
    long countAvailableProducts();
    
    /**
     * Đếm số products có stock thấp
     * @return Count of low stock products
     */
    long countLowStockProducts();
    
    /**
     * Đếm số products hết hàng
     * @return Count of out-of-stock products
     */
    long countOutOfStockProducts();
    
    /**
     * Cập nhật stock quantity
     * @param productId Product ID
     * @param quantity Quantity to add/subtract
     * @return true if successful
     */
    boolean updateStockQuantity(Integer productId, Integer quantity);
    
    /**
     * Cập nhật trạng thái available
     * @param productId Product ID
     * @param isAvailable Available status
     * @return true if successful
     */
    boolean updateAvailableStatus(Integer productId, boolean isAvailable);
    
    /**
     * Kiểm tra SKU có tồn tại
     * @param sku SKU code
     * @return true if exists
     */
    boolean existsBySku(String sku);
    
    /**
     * Tìm kiếm products theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param categoryId Category ID filter
     * @param isAvailable Available status filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @return List of Products
     */
    List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice);
    
    /**
     * Tìm kiếm products theo nhiều tiêu chí có phân trang
     * @param keyword Search keyword
     * @param categoryId Category ID filter
     * @param isAvailable Available status filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @param offset Offset
     * @param limit Limit
     * @return List of Products
     */
    List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice, int offset, int limit);
    
    /**
     * Đếm số products theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param categoryId Category ID filter
     * @param isAvailable Available status filter
     * @param minPrice Minimum price filter
     * @param maxPrice Maximum price filter
     * @return Count of products
     */
    long countSearchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice);
}
