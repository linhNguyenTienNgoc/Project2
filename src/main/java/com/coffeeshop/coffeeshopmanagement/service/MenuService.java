package com.coffeeshop.coffeeshopmanagement.service;

import com.coffeeshop.coffeeshopmanagement.entity.Menu;
import java.util.List;
import java.util.Optional;

public interface MenuService {
    List<Menu> getAllMenuItems();
    Optional<Menu> getMenuItemById(Integer id);
    Menu saveMenuItem(Menu menu);
    void deleteMenuItem(Integer id);
    List<Menu> searchMenuItems(String keyword);
    long countMenuItems();
} 