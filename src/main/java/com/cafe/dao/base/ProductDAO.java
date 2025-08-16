package com.cafe.dao.base;

import com.cafe.model.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    // Basic CRUD operations
    boolean save(Product product);
    boolean update(Product product);
    boolean delete(int productId);
    Optional<Product> findById(Integer productId);
    List<Product> findAll();

    // Specific queries for menu operations
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> findAvailableProducts();
    List<Product> findLowStockProducts();
    Optional<Product> findBySku(String sku);

    // Search operations
    List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable,
                                 Integer minStock, Integer maxStock);

    // Count operations
    long countByCategoryId(Integer categoryId);
    long countAvailableProducts();
}