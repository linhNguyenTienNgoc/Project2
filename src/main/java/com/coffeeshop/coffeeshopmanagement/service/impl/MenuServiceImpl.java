package com.coffeeshop.coffeeshopmanagement.service.impl;

import com.coffeeshop.coffeeshopmanagement.entity.Menu;
import com.coffeeshop.coffeeshopmanagement.repository.MenuRepository;
import com.coffeeshop.coffeeshopmanagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class MenuServiceImpl implements MenuService {
    
    @Autowired
    private MenuRepository menuRepository;

    @Override
    public List<Menu> getAllMenuItems() {
        return menuRepository.findAll();
    }

    @Override
    public Optional<Menu> getMenuItemById(Integer id) {
        return menuRepository.findById(id);
    }

    @Override
    public Menu saveMenuItem(Menu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public void deleteMenuItem(Integer id) {
        menuRepository.deleteById(id);
    }

    @Override
    public List<Menu> searchMenuItems(String keyword) {
        // Simple search implementation - can be enhanced with JPA Specification or QueryDSL
        List<Menu> allItems = menuRepository.findAll();
        return allItems.stream()
                .filter(item -> item.getName().toLowerCase().contains(keyword.toLowerCase()) ||
                               (item.getDescription() != null && 
                                item.getDescription().toLowerCase().contains(keyword.toLowerCase())))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public long countMenuItems() {
        return menuRepository.count();
    }
} 