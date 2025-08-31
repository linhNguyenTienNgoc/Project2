package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.TableDAO;
import com.cafe.dao.base.TableDAOImpl;
import com.cafe.dao.base.AreaDAO;
import com.cafe.dao.base.AreaDAOImpl;
import com.cafe.model.entity.TableCafe;
import com.cafe.model.entity.Area;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
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
     * Get available tables count
     */
    public int getAvailableTablesCount() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return (int) tableDAO.getAllTables().stream()
                    .filter(table -> table.isActive())
                    .filter(table -> "available".equals(table.getStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting available tables count: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get occupied tables count
     */
    public int getOccupiedTablesCount() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return (int) tableDAO.getAllTables().stream()
                    .filter(table -> table.isActive())
                    .filter(table -> "occupied".equals(table.getStatus()))
                    .count();
        } catch (Exception e) {
            System.err.println("Error getting occupied tables count: " + e.getMessage());
            return 0;
        }
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

    /**
     * Lấy tất cả tables với join area name cho admin management
     */
    public List<TableCafe> getAllTables() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            String sql = "SELECT t.*, a.area_name " +
                        "FROM tables t " +
                        "LEFT JOIN areas a ON t.area_id = a.area_id " +
                        "ORDER BY t.table_id";
            
            List<TableCafe> tables = new ArrayList<>();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                while (rs.next()) {
                    TableCafe table = new TableCafe(
                        rs.getInt("table_id"),
                        rs.getString("table_name"),
                        rs.getInt("area_id"),
                        rs.getInt("capacity"),
                        rs.getString("status"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                    );
                    table.setAreaName(rs.getString("area_name"));
                    tables.add(table);
                }
            }
            return tables;
        } catch (Exception e) {
            System.err.println("Error loading all tables: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Thêm bàn mới
     */
    public boolean addTable(TableCafe table) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return tableDAO.addTable(table);
        } catch (Exception e) {
            System.err.println("Error adding table: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin bàn
     */
    public boolean updateTable(TableCafe table) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return tableDAO.updateTable(table);
        } catch (Exception e) {
            System.err.println("Error updating table: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa bàn (thực sự xóa khỏi DB)
     */
    public boolean deleteTable(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            return tableDAO.deleteTable(tableId);
        } catch (Exception e) {
            System.err.println("Error deleting table: " + e.getMessage());
            return false;
        }
    }

    /**
     * Vô hiệu hóa bàn (soft delete)
     */
    public boolean deactivateTable(int tableId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            TableDAO tableDAO = new TableDAOImpl(conn);
            Optional<TableCafe> tableOpt = Optional.ofNullable(tableDAO.getTableById(tableId));
            if (tableOpt.isPresent()) {
                TableCafe table = tableOpt.get();
                table.setActive(false);
                return tableDAO.updateTable(table);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deactivating table: " + e.getMessage());
            return false;
        }
    }
}