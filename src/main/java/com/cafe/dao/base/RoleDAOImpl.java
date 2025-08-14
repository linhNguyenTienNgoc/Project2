package com.cafe.dao.base;

import com.cafe.model.entity.Role;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoleDAOImpl implements RoleDAO {

    private Connection conn;

    public RoleDAOImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public List<Role>   getAllRoles() throws SQLException {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT * FROM roles";
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Role r = new Role();
            r.setRoleId(rs.getInt("role_id"));
            r.setRoleName(rs.getString("role_name"));
            roles.add(r);
        }
        return roles;
    }

    @Override
    public Role getRoleById(int id) throws SQLException {
        String sql = "SELECT * FROM roles WHERE role_id = ?";
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, id);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Role r = new Role();
            r.setRoleId(rs.getInt("role_id"));
            r.setRoleName(rs.getString("role_name"));
            return r;
        }
        return null;
    }
}
