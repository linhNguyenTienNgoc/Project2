package com.cafe.dao.base;

import com.cafe.model.entity.User;

import java.util.List;
import java.util.Optional;

/**
 * DAO interface cho User entity
 * Kế thừa BaseDAO và thêm các methods đặc thù cho User
 */
public interface UserDAO extends BaseDAO<User, Integer> {
    
    /**
     * Tìm user theo username
     * @param username Username
     * @return Optional of User
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Tìm user theo email
     * @param email Email
     * @return Optional of User
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Tìm user theo phone
     * @param phone Phone number
     * @return Optional of User
     */
    Optional<User> findByPhone(String phone);
    
    /**
     * Tìm users theo role
     * @param roleId Role ID
     * @return List of Users
     */
    List<User> findByRoleId(Integer roleId);
    
    /**
     * Tìm users theo role name
     * @param roleName Role name
     * @return List of Users
     */
    List<User> findByRoleName(String roleName);
    
    /**
     * Tìm users đang active
     * @return List of active Users
     */
    List<User> findActiveUsers();
    
    /**
     * Tìm users đang active có phân trang
     * @param offset Offset
     * @param limit Limit
     * @return List of active Users
     */
    List<User> findActiveUsers(int offset, int limit);
    
    /**
     * Đếm số users theo role
     * @param roleId Role ID
     * @return Count of users
     */
    long countByRoleId(Integer roleId);
    
    /**
     * Đếm số users đang active
     * @return Count of active users
     */
    long countActiveUsers();
    
    /**
     * Cập nhật last login time
     * @param userId User ID
     * @return true if successful
     */
    boolean updateLastLogin(Integer userId);
    
    /**
     * Cập nhật password
     * @param userId User ID
     * @param newPassword New password
     * @return true if successful
     */
    boolean updatePassword(Integer userId, String newPassword);
    
    /**
     * Cập nhật trạng thái active
     * @param userId User ID
     * @param isActive Active status
     * @return true if successful
     */
    boolean updateActiveStatus(Integer userId, boolean isActive);
    
    /**
     * Kiểm tra username có tồn tại
     * @param username Username
     * @return true if exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Kiểm tra email có tồn tại
     * @param email Email
     * @return true if exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Kiểm tra phone có tồn tại
     * @param phone Phone number
     * @return true if exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Tìm kiếm users theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param roleId Role ID filter
     * @param isActive Active status filter
     * @return List of Users
     */
    List<User> searchUsers(String keyword, Integer roleId, Boolean isActive);
    
    /**
     * Tìm kiếm users theo nhiều tiêu chí có phân trang
     * @param keyword Search keyword
     * @param roleId Role ID filter
     * @param isActive Active status filter
     * @param offset Offset
     * @param limit Limit
     * @return List of Users
     */
    List<User> searchUsers(String keyword, Integer roleId, Boolean isActive, int offset, int limit);
    
    /**
     * Đếm số users theo nhiều tiêu chí
     * @param keyword Search keyword
     * @param roleId Role ID filter
     * @param isActive Active status filter
     * @return Count of users
     */
    long countSearchUsers(String keyword, Integer roleId, Boolean isActive);
}
