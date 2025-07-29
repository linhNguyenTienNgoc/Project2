package com.coffeeshop.shopcoffeemanagement.service;

import com.coffeeshop.shopcoffeemanagement.model.Employee;
import com.coffeeshop.shopcoffeemanagement.dao.EmployeeDAO;

public class EmployeeService {
    
    private EmployeeDAO employeeDAO;
    
    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }
    
    public Employee authenticate(String username, String password) {
        try {
            Employee employee = employeeDAO.findByUsername(username);
            
            if (employee != null && employee.isActive()) {
                // Kiểm tra mật khẩu (trong thực tế nên hash password)
                if (password.equals(employee.getPassword())) {
                    return employee;
                }
            }
            
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi xác thực: " + e.getMessage());
        }
    }
    
    public Employee findByUsername(String username) {
        try {
            return employeeDAO.findByUsername(username);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi tìm kiếm nhân viên: " + e.getMessage());
        }
    }
    
    public boolean changePassword(Long employeeId, String oldPassword, String newPassword) {
        try {
            Employee employee = employeeDAO.findById(employeeId);
            if (employee != null && oldPassword.equals(employee.getPassword())) {
                // Trong thực tế sẽ hash password mới
                employee.setPassword(newPassword);
                return employeeDAO.update(employee);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi đổi mật khẩu: " + e.getMessage());
        }
    }
} 