package com.coffeeshop.coffeeshopmanagement.service.impl;

import com.coffeeshop.coffeeshopmanagement.entity.CoffeeTable;
import com.coffeeshop.coffeeshopmanagement.repository.CoffeeTableRepository;
import com.coffeeshop.coffeeshopmanagement.service.CoffeeTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CoffeeTableServiceImpl implements CoffeeTableService {
    
    @Autowired
    private CoffeeTableRepository coffeeTableRepository;

    @Override
    public List<CoffeeTable> getAllTables() {
        return coffeeTableRepository.findAll();
    }

    @Override
    public Optional<CoffeeTable> getTableById(Integer id) {
        return coffeeTableRepository.findById(id);
    }

    @Override
    public CoffeeTable saveTable(CoffeeTable table) {
        return coffeeTableRepository.save(table);
    }

    @Override
    public void deleteTable(Integer id) {
        coffeeTableRepository.deleteById(id);
    }

    @Override
    public List<CoffeeTable> getTablesByStatus(String status) {
        List<CoffeeTable> allTables = coffeeTableRepository.findAll();
        return allTables.stream()
                .filter(table -> table.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public void updateTableStatus(Integer tableId, String status) {
        Optional<CoffeeTable> tableOpt = coffeeTableRepository.findById(tableId);
        if (tableOpt.isPresent()) {
            CoffeeTable table = tableOpt.get();
            table.setStatus(status);
            coffeeTableRepository.save(table);
        }
    }

    @Override
    public long countTables() {
        return coffeeTableRepository.count();
    }

    @Override
    public long countTablesByStatus(String status) {
        List<CoffeeTable> allTables = coffeeTableRepository.findAll();
        return allTables.stream()
                .filter(table -> table.getStatus().equals(status))
                .count();
    }
} 