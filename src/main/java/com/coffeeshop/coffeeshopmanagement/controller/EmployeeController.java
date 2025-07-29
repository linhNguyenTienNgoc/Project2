package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String listEmployees(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }
        
        // Chỉ admin mới được xem danh sách nhân viên
        if (!"admin".equals(currentEmployee.getRole())) {
            return "redirect:/dashboard";
        }

        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee/list";
    }

    @GetMapping("/add")
    public String addEmployeeForm(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("employee", new Employee());
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee/form";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute Employee employee, 
                            HttpSession session, 
                            Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        if (employeeService.existsByUsername(employee.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            model.addAttribute("employee", employee);
            model.addAttribute("currentEmployee", currentEmployee);
            return "employee/form";
        }

        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Integer id, 
                                 HttpSession session, 
                                 Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        Employee employee = employeeService.getEmployeeById(id).orElse(null);
        if (employee == null) {
            return "redirect:/employees";
        }

        model.addAttribute("employee", employee);
        model.addAttribute("currentEmployee", currentEmployee);
        return "employee/form";
    }

    @PostMapping("/edit/{id}")
    public String editEmployee(@PathVariable Integer id,
                             @ModelAttribute Employee employee,
                             HttpSession session,
                             Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        Employee existingEmployee = employeeService.getEmployeeById(id).orElse(null);
        if (existingEmployee == null) {
            return "redirect:/employees";
        }

        // Kiểm tra username trùng lặp (trừ chính nó)
        if (!existingEmployee.getUsername().equals(employee.getUsername()) &&
            employeeService.existsByUsername(employee.getUsername())) {
            model.addAttribute("error", "Tên đăng nhập đã tồn tại!");
            model.addAttribute("employee", employee);
            model.addAttribute("currentEmployee", currentEmployee);
            return "employee/form";
        }

        employee.setId(id);
        employeeService.saveEmployee(employee);
        return "redirect:/employees";
    }

    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Integer id, HttpSession session) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        // Không cho phép xóa chính mình
        if (currentEmployee.getId().equals(id)) {
            return "redirect:/employees";
        }

        employeeService.deleteEmployee(id);
        return "redirect:/employees";
    }
} 