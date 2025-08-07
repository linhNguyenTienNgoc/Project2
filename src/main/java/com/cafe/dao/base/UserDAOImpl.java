package com.cafe.dao.base;

import com.cafe.model.entity.User;
import com.cafe.model.entity.Role;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation của UserDAO interface
 * Cải thiện với đầy đủ methods và error handling
 */
public class UserDAOImpl implements UserDAO {
    private final Connection conn;

    public UserDAOImpl(Connection conn) {
        this.conn = conn;
    }

    // =====================================================
    // BASE DAO METHODS
    // =====================================================

    @Override
    public List<User> findAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "ORDER BY u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public Optional<User> findById(Integer id) {
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean save(User entity) {
        if (entity.getUserId() == null) {
            return insert(entity);
        } else {
            return update(entity);
        }
    }

    @Override
    public boolean insert(User entity) {
        String sql = "INSERT INTO users (username, password, full_name, email, phone, role_id, avatar_url, is_active, created_at, updated_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getFullName());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getPhone());
            ps.setInt(6, entity.getRoleId());
            ps.setString(7, entity.getAvatarUrl());
            ps.setBoolean(8, entity.getIsActive());
            ps.setTimestamp(9, entity.getCreatedAt());
            ps.setTimestamp(10, entity.getUpdatedAt());
            
            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    entity.setUserId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(User entity) {
        String sql = "UPDATE users SET username = ?, password = ?, full_name = ?, email = ?, phone = ?, " +
                    "role_id = ?, avatar_url = ?, is_active = ?, updated_at = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, entity.getUsername());
            ps.setString(2, entity.getPassword());
            ps.setString(3, entity.getFullName());
            ps.setString(4, entity.getEmail());
            ps.setString(5, entity.getPhone());
            ps.setInt(6, entity.getRoleId());
            ps.setString(7, entity.getAvatarUrl());
            ps.setBoolean(8, entity.getIsActive());
            ps.setTimestamp(9, entity.getUpdatedAt());
            ps.setInt(10, entity.getUserId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean deleteById(Integer id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(User entity) {
        return deleteById(entity.getUserId());
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
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
        String sql = "SELECT COUNT(*) FROM users";
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
    public List<User> findAll(int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "ORDER BY u.user_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> search(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ? " +
                    "ORDER BY u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> search(String keyword, int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ? " +
                    "ORDER BY u.user_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, limit);
            ps.setInt(5, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearch(String keyword) {
        String sql = "SELECT COUNT(*) FROM users u " +
                    "WHERE u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?";
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
    // USER SPECIFIC METHODS
    // =====================================================

    @Override
    public Optional<User> findByUsername(String username) {
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<User> findByRoleId(Integer roleId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.role_id = ? ORDER BY u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> findByRoleName(String roleName) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE r.role_name = ? ORDER BY u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roleName);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> findActiveUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.is_active = true ORDER BY u.user_id";
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> findActiveUsers(int offset, int limit) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.*, r.role_name FROM users u " +
                    "LEFT JOIN roles r ON u.role_id = r.role_id " +
                    "WHERE u.is_active = true ORDER BY u.user_id LIMIT ? OFFSET ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countByRoleId(Integer roleId) {
        String sql = "SELECT COUNT(*) FROM users WHERE role_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roleId);
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
    public long countActiveUsers() {
        String sql = "SELECT COUNT(*) FROM users WHERE is_active = true";
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
    public boolean updateLastLogin(Integer userId) {
        String sql = "UPDATE users SET last_login = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updatePassword(Integer userId, String newPassword) {
        String sql = "UPDATE users SET password = ?, updated_at = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean updateActiveStatus(Integer userId, boolean isActive) {
        String sql = "UPDATE users SET is_active = ?, updated_at = ? WHERE user_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isActive);
            ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
            ps.setInt(3, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean existsByUsername(String username) {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
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
    public boolean existsByEmail(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
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
    public boolean existsByPhone(String phone) {
        String sql = "SELECT COUNT(*) FROM users WHERE phone = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
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
    public List<User> searchUsers(String keyword, Integer roleId, Boolean isActive) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.*, r.role_name FROM users u ");
        sql.append("LEFT JOIN roles r ON u.role_id = r.role_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (roleId != null) {
            sql.append("AND u.role_id = ? ");
            params.add(roleId);
        }
        
        if (isActive != null) {
            sql.append("AND u.is_active = ? ");
            params.add(isActive);
        }
        
        sql.append("ORDER BY u.user_id");
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<User> searchUsers(String keyword, Integer roleId, Boolean isActive, int offset, int limit) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT u.*, r.role_name FROM users u ");
        sql.append("LEFT JOIN roles r ON u.role_id = r.role_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        int paramIndex = 1;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (roleId != null) {
            sql.append("AND u.role_id = ? ");
            params.add(roleId);
        }
        
        if (isActive != null) {
            sql.append("AND u.is_active = ? ");
            params.add(isActive);
        }
        
        sql.append("ORDER BY u.user_id LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public long countSearchUsers(String keyword, Integer roleId, Boolean isActive) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) FROM users u ");
        sql.append("LEFT JOIN roles r ON u.role_id = r.role_id ");
        sql.append("WHERE 1=1 ");
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (u.username LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String searchPattern = "%" + keyword + "%";
            params.add(searchPattern);
            params.add(searchPattern);
            params.add(searchPattern);
        }
        
        if (roleId != null) {
            sql.append("AND u.role_id = ? ");
            params.add(roleId);
        }
        
        if (isActive != null) {
            sql.append("AND u.is_active = ? ");
            params.add(isActive);
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
    public List<User> getAllUsers() {
        return findAll();
    }

    /**
     * @deprecated Use findById(id).orElse(null) instead
     */
    @Deprecated
    public User getUserById(int id) {
        return findById(id).orElse(null);
    }

    /**
     * @deprecated Use findByUsername(username).orElse(null) instead
     */
    @Deprecated
    public User getUserByUsername(String username) {
        return findByUsername(username).orElse(null);
    }

    /**
     * @deprecated Use insert(entity) instead
     */
    @Deprecated
    public boolean insertUser(User user) {
        return insert(user);
    }

    /**
     * @deprecated Use update(entity) instead
     */
    @Deprecated
    public boolean updateUser(User user) {
        return update(user);
    }

    /**
     * @deprecated Use deleteById(id) instead
     */
    @Deprecated
    public boolean deleteUser(int id) {
        return deleteById(id);
    }

    /**
     * @deprecated Use findAll(offset, limit) instead
     */
    @Deprecated
    public List<User> getAllUsers(int offset, int limit) {
        return findAll(offset, limit);
    }

    /**
     * @deprecated Use count() instead
     */
    @Deprecated
    public int getTotalUserCount() {
        return (int) count();
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to User object
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setRoleId(rs.getInt("role_id"));
        user.setAvatarUrl(rs.getString("avatar_url"));
        user.setLastLogin(rs.getTimestamp("last_login"));
        user.setIsActive(rs.getBoolean("is_active"));
        user.setCreatedAt(rs.getTimestamp("created_at"));
        user.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Load role information
        Role role = new Role();
        role.setRoleId(rs.getInt("role_id"));
        role.setRoleName(rs.getString("role_name"));
        user.setRole(role);
        
        return user;
    }
}
