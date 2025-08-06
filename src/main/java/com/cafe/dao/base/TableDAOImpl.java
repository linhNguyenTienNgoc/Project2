package com.cafe.dao.base;

import com.cafe.model.entity.TableCafe;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TableDAOImpl implements TableDAO {
    private final Connection conn;

    public TableDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean addTable(TableCafe table) {
        String sql = "INSERT INTO tables (table_name, area_id, capacity, status, is_active) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table.getTableName());
            stmt.setInt(2, table.getAreaId());
            stmt.setInt(3, table.getCapacity());
            stmt.setString(4, table.getStatus());
            stmt.setBoolean(5, table.isActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean updateTable(TableCafe table) {
        String sql = "UPDATE tables SET table_name = ?, area_id = ?, capacity = ?, status = ?, is_active = ? WHERE table_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, table.getTableName());
            stmt.setInt(2, table.getAreaId());
            stmt.setInt(3, table.getCapacity());
            stmt.setString(4, table.getStatus());
            stmt.setBoolean(5, table.isActive());
            stmt.setInt(6, table.getTableId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean deleteTable(int tableId) {
        String sql = "DELETE FROM tables WHERE table_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tableId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public TableCafe getTableById(int tableId) {
        String sql = "SELECT * FROM tables WHERE table_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, tableId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new TableCafe(
                            rs.getInt("table_id"),
                            rs.getString("table_name"),
                            rs.getInt("area_id"),
                            rs.getInt("capacity"),
                            rs.getString("status"),
                            rs.getBoolean("is_active"),
                            rs.getTimestamp("created_at"),
                            rs.getTimestamp("updated_at")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<TableCafe> getAllTables() {
        List<TableCafe> list = new ArrayList<>();
        String sql = "SELECT * FROM tables";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
                list.add(table);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<TableCafe> getTablesByArea(int areaId) {
        List<TableCafe> list = new ArrayList<>();
        String sql = "SELECT * FROM tables WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            try (ResultSet rs = stmt.executeQuery()) {
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
                    list.add(table);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
