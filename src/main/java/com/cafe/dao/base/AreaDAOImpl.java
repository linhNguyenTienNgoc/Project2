package com.cafe.dao.base;

import com.cafe.model.entity.Area;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AreaDAOImpl implements AreaDAO {
    private final Connection conn;

    public AreaDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean save(Area area) {
        String sql = "INSERT INTO areas (area_name, description, is_active) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, area.getAreaName());
            stmt.setString(2, area.getDescription());
            stmt.setBoolean(3, area.isActive());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        area.setAreaId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean update(Area area) {
        String sql = "UPDATE areas SET area_name = ?, description = ?, is_active = ? WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, area.getAreaName());
            stmt.setString(2, area.getDescription());
            stmt.setBoolean(3, area.isActive());
            stmt.setInt(4, area.getAreaId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(int areaId) {
        String sql = "UPDATE areas SET is_active = false WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public Optional<Area> findById(Integer areaId) {
        String sql = "SELECT * FROM areas WHERE area_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, areaId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractArea(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public List<Area> findAll() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT * FROM areas ORDER BY area_name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                areas.add(extractArea(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areas;
    }

    @Override
    public List<Area> findActiveAreas() {
        List<Area> areas = new ArrayList<>();
        String sql = "SELECT * FROM areas WHERE is_active = true ORDER BY area_name";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                areas.add(extractArea(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return areas;
    }

    private Area extractArea(ResultSet rs) throws SQLException {
        Area area = new Area();
        area.setAreaId(rs.getInt("area_id"));
        area.setAreaName(rs.getString("area_name"));
        area.setDescription(rs.getString("description"));
        area.setActive(rs.getBoolean("is_active"));
        area.setCreatedAt(rs.getTimestamp("created_at"));
        area.setUpdatedAt(rs.getTimestamp("updated_at"));
        return area;
    }
}
