package com.cafe.dao.base;
import com.cafe.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductDAO {
    List<Product> getAllProducts();
    Product getProductById(int id);
    boolean insertProduct(Product product);
    boolean updateProduct(Product product);
    boolean deleteProduct(int id);
    List<Product> searchProductsByName(String keyword);
    
    // Additional methods needed for MenuService
    List<Product> findAll();
    Optional<Product> findById(Integer id);
    List<Product> findByCategoryId(Integer categoryId);
    List<Product> searchProducts(String keyword, Integer categoryId, Boolean isAvailable, Double minPrice, Double maxPrice);
    Optional<Product> findBySku(String sku);
    List<Product> findAvailableProducts();
    List<Product> findLowStockProducts();
    long countByCategoryId(Integer categoryId);
    long countAvailableProducts();
}
