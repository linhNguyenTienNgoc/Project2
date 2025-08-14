package com.cafe.dao.base;

import com.cafe.model.entity.User;

import java.util.List;

public interface UserDAO {
    List<User> getAllUsers();
    User getUserById(int id);
    User getUserByUsername(String username);
    boolean insertUser(User user);
    boolean updateUser(User user);
    boolean deleteUser(int id);
    List<User> getAllUsers(int offset, int limit);
    int getTotalUserCount();
}
