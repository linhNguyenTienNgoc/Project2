package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.entity.CoffeeTable;
import com.coffeeshop.coffeeshopmanagement.service.CoffeeTableService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/tables")
public class CoffeeTableController {
    
    private final CoffeeTableService coffeeTableService;
    
    public CoffeeTableController(CoffeeTableService coffeeTableService) {
        this.coffeeTableService = coffeeTableService;
    }

    @GetMapping
    public String listTables(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<CoffeeTable> tables = coffeeTableService.getAllTables();
        model.addAttribute("tables", tables);
        model.addAttribute("currentEmployee", currentEmployee);
        return "table/list";
    }

    @GetMapping("/add")
    public String addTableForm(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("table", new CoffeeTable());
        model.addAttribute("currentEmployee", currentEmployee);
        return "table/form";
    }

    @PostMapping("/add")
    public String addTable(@ModelAttribute CoffeeTable table,
                         HttpSession session,
                         Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        // Mặc định trạng thái Available khi tạo bàn mới
        if (table.getStatus() == null || table.getStatus().trim().isEmpty()) {
            table.setStatus("Available");
        }

        coffeeTableService.saveTable(table);
        return "redirect:/tables";
    }

    @GetMapping("/edit/{id}")
    public String editTableForm(@PathVariable Integer id,
                              HttpSession session,
                              Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        CoffeeTable table = coffeeTableService.getTableById(id).orElse(null);
        if (table == null) {
            return "redirect:/tables";
        }

        model.addAttribute("table", table);
        model.addAttribute("currentEmployee", currentEmployee);
        return "table/form";
    }

    @PostMapping("/edit/{id}")
    public String editTable(@PathVariable Integer id,
                          @ModelAttribute CoffeeTable table,
                          HttpSession session,
                          Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        table.setId(id);
        coffeeTableService.saveTable(table);
        return "redirect:/tables";
    }

    @GetMapping("/delete/{id}")
    public String deleteTable(@PathVariable Integer id, HttpSession session) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        coffeeTableService.deleteTable(id);
        return "redirect:/tables";
    }

    @PostMapping("/{id}/status")
    @ResponseBody
    public String updateTableStatus(@PathVariable Integer id,
                                  @RequestParam String status,
                                  HttpSession session) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "error";
        }

        coffeeTableService.updateTableStatus(id, status);
        return "success";
    }
} 