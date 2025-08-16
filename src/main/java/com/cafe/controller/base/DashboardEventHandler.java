package com.cafe.controller.base;

import com.cafe.model.entity.Product;
import com.cafe.model.entity.TableCafe;

/**
 * Interface for Dashboard to implement communication methods
 * This allows controllers to call specific dashboard methods
 *
 * @author Team 2_C2406L
 * @version 2.0.0
 */
public interface DashboardEventHandler {

    /**
     * Update table information
     * @param tableName Name of the table
     * @param status Status of the table
     */
    void updateTableInfo(String tableName, String status);

    /**
     * Add item to order (legacy method)
     * @param productName Name of the product
     * @param price Price of the product
     * @param quantity Quantity to add
     */
    void addToOrder(String productName, double price, int quantity);

    /**
     * Add product to order (enhanced method)
     * @param product Product object to add
     * @param quantity Quantity to add
     */
    void addProductToOrder(Product product, int quantity);

    /**
     * Handle table selection
     * @param table Selected table
     */
    void onTableSelected(TableCafe table);

    /**
     * Clear current order
     */
    void clearOrder();

    /**
     * Get current order total
     * @return Total amount of current order
     */
    double getOrderTotal();

    /**
     * Notify when order status changes
     * @param newStatus New order status
     * @param tableId Table ID affected
     */
    void onOrderStatusChanged(String newStatus, int tableId);
}