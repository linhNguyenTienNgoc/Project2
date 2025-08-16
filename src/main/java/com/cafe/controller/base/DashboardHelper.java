package com.cafe.controller.base;

import com.cafe.model.entity.Product;
import com.cafe.model.entity.TableCafe;

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
}