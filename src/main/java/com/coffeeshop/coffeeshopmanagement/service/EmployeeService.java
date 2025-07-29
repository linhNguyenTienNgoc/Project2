package com.coffeeshop.coffeeshopmanagement.service;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    List<Employee> getAllEmployees();
    Optional<Employee> getEmployeeById(Integer id);
    Optional<Employee> getEmployeeByUsername(String username);
    Employee saveEmployee(Employee employee);
    void deleteEmployee(Integer id);
    boolean existsByUsername(String username);
    long countEmployees();
} 