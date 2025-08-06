package com.cafe.util;

import com.cafe.model.entity.User;

/**
 * Quản lý session của người dùng hiện tại
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class SessionManager {
    
    private static User currentUser;
    private static long loginTime;
    
    /**
     * Đặt người dùng hiện tại
     */
    public static void setCurrentUser(User user) {
        currentUser = user;
        loginTime = System.currentTimeMillis();
        
        System.out.println("🔑 User logged in: " + user.getUsername() + " (" + user.getFullName() + ")");
    }
    
    /**
     * Lấy người dùng hiện tại
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Kiểm tra đã đăng nhập chưa
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Lấy ID người dùng hiện tại
     */
    public static int getCurrentUserId() {
        return currentUser != null ? currentUser.getUserId() : -1;
    }
    
    /**
     * Lấy tên người dùng hiện tại
     */
    public static String getCurrentUsername() {
        return currentUser != null ? currentUser.getUsername() : "";
    }
    
    /**
     * Lấy tên đầy đủ người dùng hiện tại
     */
    public static String getCurrentUserFullName() {
        return currentUser != null ? currentUser.getFullName() : "";
    }
    
    /**
     * Lấy role ID người dùng hiện tại
     */
    public static int getCurrentUserRoleId() {
        return currentUser != null ? currentUser.getRoleId() : -1;
    }
    
    /**
     * Kiểm tra role của người dùng hiện tại
     */
    public static boolean hasRole(int roleId) {
        return currentUser != null && currentUser.getRoleId() == roleId;
    }
    
    /**
     * Kiểm tra quyền admin
     */
    public static boolean isAdmin() {
        return hasRole(1); // Assuming role_id 1 is Admin
    }
    
    /**
     * Kiểm tra quyền manager
     */
    public static boolean isManager() {
        return hasRole(2); // Assuming role_id 2 is Manager
    }
    
    /**
     * Kiểm tra quyền cashier
     */
    public static boolean isCashier() {
        return hasRole(3); // Assuming role_id 3 is Cashier
    }
    
    /**
     * Kiểm tra quyền waiter
     */
    public static boolean isWaiter() {
        return hasRole(4); // Assuming role_id 4 is Waiter
    }
    
    /**
     * Kiểm tra quyền barista
     */
    public static boolean isBarista() {
        return hasRole(5); // Assuming role_id 5 is Barista
    }
    
    /**
     * Lấy thời gian đăng nhập
     */
    public static long getLoginTime() {
        return loginTime;
    }
    
    /**
     * Lấy thời gian đã đăng nhập (milliseconds)
     */
    public static long getSessionDuration() {
        return isLoggedIn() ? System.currentTimeMillis() - loginTime : 0;
    }
    
    /**
     * Đăng xuất
     */
    public static void logout() {
        if (currentUser != null) {
            System.out.println("🚪 User logged out: " + currentUser.getUsername());
            currentUser = null;
            loginTime = 0;
        }
    }
    
    /**
     * Reset session
     */
    public static void reset() {
        logout();
    }
    
    /**
     * Kiểm tra session có hết hạn không
     */
    public static boolean isSessionExpired(long timeoutMinutes) {
        if (!isLoggedIn()) {
            return true;
        }
        
        long sessionDurationMinutes = getSessionDuration() / (1000 * 60);
        return sessionDurationMinutes > timeoutMinutes;
    }
    
    /**
     * Refresh session (update login time)
     */
    public static void refreshSession() {
        if (isLoggedIn()) {
            loginTime = System.currentTimeMillis();
        }
    }
}