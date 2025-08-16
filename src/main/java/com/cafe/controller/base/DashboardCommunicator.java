package com.cafe.controller.base;

/**
 * Enhanced interface for communication between controllers and Dashboard
 *
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced)
 */
public interface DashboardCommunicator {

    /**
     * Set dashboard controller reference
     * @param dashboardController Dashboard controller instance
     */
    void setDashboardController(Object dashboardController);

    /**
     * Get dashboard controller reference
     * @return Dashboard controller instance
     */
    Object getDashboardController();
}