package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class TestController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/api/test/users")
    public String testUsers() {
        List<Employee> employees = employeeService.getAllEmployees();
        StringBuilder result = new StringBuilder();
        result.append("Total employees: ").append(employees.size()).append("\n");
        
        for (Employee emp : employees) {
            result.append("ID: ").append(emp.getId())
                  .append(", Username: ").append(emp.getUsername())
                  .append(", Password: ").append(emp.getPassword())
                  .append(", Role: ").append(emp.getRole())
                  .append(", Active: ").append(emp.isActive())
                  .append("\n");
        }
        
        return result.toString();
    }

    @GetMapping("/api/test/user")
    public String testUser(@RequestParam String username) {
        Optional<Employee> employee = employeeService.getEmployeeByUsername(username);
        if (employee.isPresent()) {
            Employee emp = employee.get();
            return "Found user: " + emp.getUsername() + 
                   ", Password: " + emp.getPassword() + 
                   ", Role: " + emp.getRole() + 
                   ", Active: " + emp.isActive();
        } else {
            return "User not found: " + username;
        }
    }
} 