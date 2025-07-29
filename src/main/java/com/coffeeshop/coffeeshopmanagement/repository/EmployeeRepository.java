package com.coffeeshop.coffeeshopmanagement.repository;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {
    Optional<Employee> findByUsername(String username);
} 