package com.coffeeshop.coffeeshopmanagement.repository;

import com.coffeeshop.coffeeshopmanagement.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuRepository extends JpaRepository<Menu, Integer> {
} 