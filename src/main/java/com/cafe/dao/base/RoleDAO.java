package com.cafe.dao.base;

import com.cafe.model.entity.Role;

import java.sql.SQLException;
import java.util.List;

public interface RoleDAO {
    List<Role> getAllRoles() throws SQLException;;
    Role getRoleById(int id)throws SQLException;;
}
