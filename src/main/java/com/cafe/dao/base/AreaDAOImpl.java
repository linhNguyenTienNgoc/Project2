package com.cafe.dao.base;


import com.cafe.model.entity.Area;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AreaDAOImpl implements AreaDAO {
    private final Connection conn;

    public AreaDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean addArea(Area area) {
        String sql = "INSERT INTO areas (area_name, description, is_active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, area.getAreaName());
            stmt.setString(2, area.getDescription());
            stmt.setBoolean(3, area.isActive());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean updateArea(Area area) {
        String sql = "UPDATE areas SET area_name = ?, description = ?, is_active = ? WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, area.getAreaName());
            stmt.setString(2, area.getDescription());
            stmt.setBoolean(3, area.isActive());
            stmt.setInt(4, area.getAreaId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public boolean deleteArea(int areaId) {
        String sql = "DELETE FROM areas WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace(); return false;
        }
    }

    @Override
    public Area getAreaById(int areaId) {
        String sql = "SELECT * FROM areas WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Area(
                            rs.getInt("area_id"),
                            rs.getString("area_name"),
                            rs.getString("description"),
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
    public List<Area> getAllAreas() {
        List<Area> list = new ArrayList<>();
        String sql = "SELECT * FROM areas";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Area area = new Area(
                        rs.getInt("area_id"),
                        rs.getString("area_name"),
                        rs.getString("description"),
                        rs.getBoolean("is_active"),
                        rs.getTimestamp("created_at"),
                        rs.getTimestamp("updated_at")
                );
                list.add(area);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}