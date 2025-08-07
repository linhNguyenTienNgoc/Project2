package com.cafe.dao.base;

import com.cafe.model.entity.Category;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface cho Category entity
 * Kế thừa BaseDAO và thêm các methods đặc thù cho Category
 */
public interface CategoryDAO extends BaseDAO<Category, Integer> {
    
    /**
     * Tìm category theo name
     * @param categoryName Category name
     * @return Optional of Category
     */
    Optional<Category> findByCategoryName(String categoryName);
    
    /**
     * Tìm categories đang active
     * @return List of active Categories
     */
    List<Category> findActiveCategories();
    
    /**
     * Tìm categories đang active có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of active Categories
     */
    List<Category> findActiveCategories(int offset, int limit);
    
    /**
     * Tìm categories theo description
     * @param description Description keyword
     * @return List of Categories
     */
    List<Category> findByDescription(String description);
    
    /**
     * Đếm số products trong category
     * @param categoryId Category ID
     * @return Count of products
     */
    long countProductsInCategory(Integer categoryId);
    
    /**
     * Đếm số categories đang active
     * @return Count of active categories
     */
    long countActiveCategories();
    
    /**
     * Cập nhật trạng thái active
     * @param categoryId Category ID
     * @param isActive Active status
     * @return true if successful
     */
    boolean updateActiveStatus(Integer categoryId, boolean isActive);
    
    /**
     * Kiểm tra category name có tồn tại
     * @param categoryName Category name
     * @return true if exists
     */
    boolean existsByCategoryName(String categoryName);
    
    /**
     * Tìm kiếm categories theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param isActive Active status filter
     * @return List of Categories
     */
    List<Category> searchCategories(String keyword, Boolean isActive);
    
    /**
     * Tìm kiếm categories theo nhiều tiêu chí có phân trang
     * @param keyword Search keyword
     * @param isActive Active status filter
     * @param offset Offset
     * @param limit Limit
     * @return List of Categories
     */
    List<Category> searchCategories(String keyword, Boolean isActive, int offset, int limit);
    
    /**
     * Đếm số categories theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param isActive Active status filter
     * @return Count of categories
     */
    long countSearchCategories(String keyword, Boolean isActive);
}