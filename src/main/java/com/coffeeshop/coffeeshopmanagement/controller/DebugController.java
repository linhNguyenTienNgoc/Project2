package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class DebugController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private UserDetailsService userDetailsService;

    @GetMapping("/debug/employees")
    public String debugEmployees() {
        try {
            List<Employee> employees = employeeService.getAllEmployees();
            StringBuilder result = new StringBuilder();
            result.append("=== DEBUG EMPLOYEES ===\n");
            result.append("Total employees: ").append(employees.size()).append("\n\n");
            
            for (Employee emp : employees) {
                result.append("ID: ").append(emp.getId()).append("\n");
                result.append("Username: ").append(emp.getUsername()).append("\n");
                result.append("Password: ").append(emp.getPassword()).append("\n");
                result.append("Role: ").append(emp.getRole()).append("\n");
                result.append("Active: ").append(emp.isActive()).append("\n");
                result.append("Name: ").append(emp.getName()).append("\n");
                result.append("---\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage() + "\n" + e.getStackTrace();
        }
    }

    @GetMapping("/debug/user")
    public String debugUser(@RequestParam String username) {
        try {
            StringBuilder result = new StringBuilder();
            result.append("=== DEBUG USER: ").append(username).append(" ===\n");
            
            // Kiểm tra Employee Service
            Optional<Employee> employee = employeeService.getEmployeeByUsername(username);
            if (employee.isPresent()) {
                Employee emp = employee.get();
                result.append("Found in Employee Service:\n");
                result.append("ID: ").append(emp.getId()).append("\n");
                result.append("Username: ").append(emp.getUsername()).append("\n");
                result.append("Password: ").append(emp.getPassword()).append("\n");
                result.append("Role: ").append(emp.getRole()).append("\n");
                result.append("Active: ").append(emp.isActive()).append("\n");
            } else {
                result.append("NOT FOUND in Employee Service\n");
            }
            
            // Kiểm tra UserDetails Service
            try {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                result.append("\nFound in UserDetails Service:\n");
                result.append("Username: ").append(userDetails.getUsername()).append("\n");
                result.append("Password: ").append(userDetails.getPassword()).append("\n");
                result.append("Authorities: ").append(userDetails.getAuthorities()).append("\n");
                result.append("Enabled: ").append(userDetails.isEnabled()).append("\n");
                result.append("Account Non Expired: ").append(userDetails.isAccountNonExpired()).append("\n");
                result.append("Account Non Locked: ").append(userDetails.isAccountNonLocked()).append("\n");
                result.append("Credentials Non Expired: ").append(userDetails.isCredentialsNonExpired()).append("\n");
            } catch (Exception e) {
                result.append("\nERROR in UserDetails Service: ").append(e.getMessage()).append("\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage() + "\n" + e.getStackTrace();
        }
    }

    @GetMapping("/debug/test-login")
    public String testLogin(@RequestParam String username, @RequestParam String password) {
        try {
            StringBuilder result = new StringBuilder();
            result.append("=== TEST LOGIN ===\n");
            result.append("Username: ").append(username).append("\n");
            result.append("Password: ").append(password).append("\n\n");
            
            // Kiểm tra user có tồn tại không
            Optional<Employee> employee = employeeService.getEmployeeByUsername(username);
            if (employee.isPresent()) {
                Employee emp = employee.get();
                result.append("User found in database:\n");
                result.append("DB Password: ").append(emp.getPassword()).append("\n");
                result.append("Input Password: ").append(password).append("\n");
                result.append("Password match: ").append(emp.getPassword().equals(password)).append("\n");
                result.append("User active: ").append(emp.isActive()).append("\n");
            } else {
                result.append("User NOT found in database\n");
            }
            
            return result.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage() + "\n" + e.getStackTrace();
        }
    }
} 