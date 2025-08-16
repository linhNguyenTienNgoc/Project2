package com.cafe.controller.base;

import com.cafe.model.entity.Product;
import com.cafe.model.entity.TableCafe;

import java.lang.reflect.Method;

/**
 * Helper class for type-safe dashboard communication
 *
 * @author Team 2_C2406L
 * @version 2.0.0
 */
public class DashboardHelper {

    /**
     * Safely call dashboard method if it implements DashboardEventHandler
     */
    public static void updateTableInfo(Object dashboard, String tableName, String status) {
        if (dashboard instanceof DashboardEventHandler) {
            ((DashboardEventHandler) dashboard).updateTableInfo(tableName, status);
        }
    }

    /**
     * Safely add product to order via dashboard
     */
    public static void addProductToOrder(Object dashboard, Product product, int quantity) {
        if (dashboard instanceof DashboardEventHandler) {
            ((DashboardEventHandler) dashboard).addProductToOrder(product, quantity);
        }
    }

    /**
     * Safely notify table selection
     */
    public static void notifyTableSelected(Object dashboard, TableCafe table) {
        if (dashboard instanceof DashboardEventHandler) {
            ((DashboardEventHandler) dashboard).onTableSelected(table);
        }
    }

    /**
     * Safely notify order status change
     */
    public static void notifyOrderStatusChanged(Object dashboard, String newStatus, int tableId) {
        if (dashboard instanceof DashboardEventHandler) {
            ((DashboardEventHandler) dashboard).onOrderStatusChanged(newStatus, tableId);
        }
    }
    /**
     * ✅ NEW: Show order panel for specific table
     */
    public static void showOrderPanel(Object dashboardController, int tableId) {
        try {
            if (dashboardController != null) {
                // Use reflection to call showOrderPanel method on dashboard
                Method method = dashboardController.getClass().getMethod("showOrderPanel", int.class);
                method.invoke(dashboardController, tableId);
            }
        } catch (Exception e) {
            System.err.println("Error showing order panel: " + e.getMessage());
        }
    }

    /**
     * ✅ NEW: Update table status through dashboard
     */
    public static void updateTableStatus(Object dashboardController, int tableId, String newStatus) {
        try {
            if (dashboardController != null) {
                // Use reflection to call updateTableStatus method on dashboard
                Method method = dashboardController.getClass().getMethod("updateTableStatus", int.class, String.class);
                method.invoke(dashboardController, tableId, newStatus);
            }
        } catch (Exception e) {
            System.err.println("Error updating table status through dashboard: " + e.getMessage());
        }
    }
}