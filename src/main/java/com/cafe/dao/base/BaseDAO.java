package com.cafe.dao.base;

import java.util.List;
import java.util.Optional;

/**
 * Base DAO interface với các methods chung cho tất cả DAO
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseDAO<T, ID> {
    
    /**
     * Lấy tất cả records
     * @return List of entities
     */
    List<T> findAll();
    
    /**
     * Lấy entity theo ID
     * @param id Entity ID
     * @return Optional of entity
     */
    Optional<T> findById(ID id);
    
    /**
     * Lưu entity (insert hoặc update)
     * @param entity Entity to save
     * @return true if successful
     */
    boolean save(T entity);
    
    /**
     * Insert entity mới
     * @param entity Entity to insert
     * @return true if successful
     */
    boolean insert(T entity);
    
    /**
     * Update entity
     * @param entity Entity to update
     * @return true if successful
     */
    boolean update(T entity);
    
    /**
     * Xóa entity theo ID
     * @param id Entity ID
     * @return true if successful
     */
    boolean deleteById(ID id);
    
    /**
     * Xóa entity
     * @param entity Entity to delete
     * @return true if successful
     */
    boolean delete(T entity);
    
    /**
     * Kiểm tra entity có tồn tại theo ID
     * @param id Entity ID
     * @return true if exists
     */
    boolean existsById(ID id);
    
    /**
     * Đếm tổng số records
     * @return Total count
     */
    long count();
    
    /**
     * Lấy danh sách có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of entities
     */
    List<T> findAll(int offset, int limit);
    
    /**
     * Tìm kiếm theo keyword
     * @param keyword Search keyword
     * @return List of entities
     */
    List<T> search(String keyword);
    
    /**
     * Tìm kiếm theo keyword có phân trang
     * @param keyword Search keyword
     * @param offset Offset
     * @param limit Limit
     * @return List of entities
     */
    List<T> search(String keyword, int offset, int limit);
    
    /**
     * Đếm số records tìm kiếm
     * @param keyword Search keyword
     * @return Count of search results
     */
    long countSearch(String keyword);
} 