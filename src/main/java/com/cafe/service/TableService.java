package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.TableDAO;
import com.cafe.dao.base.TableDAOImpl;
import com.cafe.dao.base.AreaDAO;
import com.cafe.dao.base.AreaDAOImpl;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.entity.Area;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableService {

    public TableService() {
        // ✅ REMOVED: No longer getting connection in constructor
        // Connections will be managed per operation using try-with-resources
    }

    /**
     * Lấy tất cả areas đang active
     */
    public List<Area> getAvailableAreas() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            AreaDAO areaDAO = new AreaDAOImpl(conn);
            return areaDAO.findActiveAreas();
        } catch (Exception e) {
            System.err.println("Error loading areas: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy tables theo area
     */
    public List<TableCafe> getTablesByArea(Integer areaId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            if (areaId == null) {
                return tableDAO.getAllTables().stream()
                        .filter(table -> table.isActive())
                        .collect(Collectors.toList());
            } else {
                return tableDAO.getTablesByArea(areaId).stream()
                        .filter(table -> table.isActive())
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            System.err.println("Error loading tables for area " + areaId + ": " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy tất cả tables đang active
     */
    public List<TableCafe> getAllAvailableTables() {
        return getTablesByArea(null);
    }

    /**
     * Cập nhật trạng thái bàn
     */
    public boolean updateTableStatus(int tableId, String newStatus) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            Optional<TableCafe> tableOpt = Optional.ofNullable(tableDAO.getTableById(tableId));
            if (tableOpt.isPresent()) {
                TableCafe table = tableOpt.get();
                table.setStatus(newStatus);
                return tableDAO.updateTable(table);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating table status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Kiểm tra bàn có trống không
     */
    public boolean isTableAvailable(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            TableCafe table = tableDAO.getTableById(tableId);
            return table != null && "available".equals(table.getStatus());
        } catch (Exception e) {
            System.err.println("Error checking table availability: " + e.getMessage());
            return false;
        }
    }

    /**
     * Lấy table theo ID
     */
    public Optional<TableCafe> getTableById(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return Optional.ofNullable(tableDAO.getTableById(tableId));
        } catch (Exception e) {
            System.err.println("Error getting table by ID: " + e.getMessage());
            return Optional.empty();
        }
    }
}