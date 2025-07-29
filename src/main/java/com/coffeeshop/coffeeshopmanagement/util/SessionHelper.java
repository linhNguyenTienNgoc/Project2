package com.coffeeshop.coffeeshopmanagement.util;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;

public class SessionHelper {
    
    public static Employee getDefaultEmployee() {
        Employee employee = new Employee();
        employee.setId(1);
        employee.setUsername("admin");
        employee.setName("Administrator");
        employee.setRole("ADMIN");
        employee.setActive(true);
        employee.setEmail("admin@coffeeshop.com");
        employee.setPhone("0901234567");
        return employee;
    }
    
    public static Employee getCurrentEmployee(jakarta.servlet.http.HttpSession session) {
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            employee = getDefaultEmployee();
        }
        return employee;
    }
} 