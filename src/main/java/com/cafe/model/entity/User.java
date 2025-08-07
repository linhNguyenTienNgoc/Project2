package com.cafe.model.entity;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Entity class cho bảng users
 * Cải thiện với validation, business methods và đầy đủ fields
 */
public class User {
    private Integer userId;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private Integer roleId;
    private String avatarUrl;
    private Timestamp lastLogin;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relationship object
    private Role role;

    // Constructors
    public User() {
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public User(String username, String password, String fullName, String email, String phone, Integer roleId) {
        this();
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
    }

    public User(Integer userId, String username, String password, String fullName, String email, String phone, Integer roleId, Boolean isActive) {
        this();
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.roleId = roleId;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(Timestamp lastLogin) {
        this.lastLogin = lastLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Business methods
    public boolean isAdmin() {
        return role != null && "admin".equals(role.getRoleName());
    }
    
    public boolean isWaiter() {
        return role != null && "waiter".equals(role.getRoleName());
    }
    
    public boolean isBarista() {
        return role != null && "barista".equals(role.getRoleName());
    }
    
    public boolean canAccessAdminPanel() {
        return isAdmin();
    }
    
    public boolean canManageOrders() {
        return isAdmin() || isWaiter();
    }
    
    public boolean canManageProducts() {
        return isAdmin() || isBarista();
    }
    
    public boolean canViewReports() {
        return isAdmin();
    }
    
    public boolean canManageUsers() {
        return isAdmin();
    }
    
    public boolean canManageSettings() {
        return isAdmin();
    }

    // Validation methods
    public boolean isValidUsername() {
        return username != null && username.length() >= 3 && username.length() <= 50;
    }
    
    public boolean isValidPassword() {
        return password != null && password.length() >= 6;
    }
    
    public boolean isValidEmail() {
        return email != null && email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    public boolean isValidPhone() {
        return phone != null && phone.matches("^[0-9]{10,11}$");
    }
    
    public boolean isValidFullName() {
        return fullName != null && fullName.length() >= 2 && fullName.length() <= 100;
    }
    
    public boolean isValid() {
        return isValidUsername() && isValidPassword() && isValidFullName() && 
               (email == null || isValidEmail()) && (phone == null || isValidPhone());
    }

    // Utility methods
    public void updateLastLogin() {
        this.lastLogin = new Timestamp(System.currentTimeMillis());
    }
    
    public void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getDisplayName() {
        return fullName != null ? fullName : username;
    }
    
    public String getRoleDisplayName() {
        if (role != null) {
            switch (role.getRoleName()) {
                case "admin": return "Quản trị viên";
                case "waiter": return "Nhân viên phục vụ";
                case "barista": return "Nhân viên pha chế";
                default: return role.getRoleName();
            }
        }
        return "Không xác định";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && 
               Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username);
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", roleId=" + roleId +
                ", isActive=" + isActive +
                ", roleName='" + (role != null ? role.getRoleName() : "null") + '\'' +
                '}';
    }
}
