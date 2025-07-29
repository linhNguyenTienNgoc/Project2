package com.coffeeshop.coffeeshopmanagement.service;

import com.coffeeshop.coffeeshopmanagement.entity.CoffeeTable;
import java.util.List;
import java.util.Optional;

public interface CoffeeTableService {
    List<CoffeeTable> getAllTables();
    Optional<CoffeeTable> getTableById(Integer id);
    CoffeeTable saveTable(CoffeeTable table);
    void deleteTable(Integer id);
    List<CoffeeTable> getTablesByStatus(String status);
    void updateTableStatus(Integer tableId, String status);
    long countTables();
    long countTablesByStatus(String status);
} 