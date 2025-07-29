package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.*;
import com.coffeeshop.coffeeshopmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RestApiController {
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private CoffeeTableService coffeeTableService;
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private InvoiceDetailService invoiceDetailService;

    // Employee APIs
    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/employees")
    public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
        if (employeeService.existsByUsername(employee.getUsername())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employeeService.saveEmployee(employee));
    }

    // Menu APIs
    @GetMapping("/menu")
    public ResponseEntity<List<Menu>> getAllMenuItems() {
        return ResponseEntity.ok(menuService.getAllMenuItems());
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<Menu> getMenuItemById(@PathVariable Integer id) {
        Optional<Menu> menuItem = menuService.getMenuItemById(id);
        return menuItem.map(ResponseEntity::ok)
                      .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/menu/search")
    public ResponseEntity<List<Menu>> searchMenuItems(@RequestParam String keyword) {
        return ResponseEntity.ok(menuService.searchMenuItems(keyword));
    }

    @PostMapping("/menu")
    public ResponseEntity<Menu> createMenuItem(@RequestBody Menu menuItem) {
        return ResponseEntity.ok(menuService.saveMenuItem(menuItem));
    }

    // Table APIs
    @GetMapping("/tables")
    public ResponseEntity<List<CoffeeTable>> getAllTables() {
        return ResponseEntity.ok(coffeeTableService.getAllTables());
    }

    @GetMapping("/tables/{id}")
    public ResponseEntity<CoffeeTable> getTableById(@PathVariable Integer id) {
        Optional<CoffeeTable> table = coffeeTableService.getTableById(id);
        return table.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tables/status/{status}")
    public ResponseEntity<List<CoffeeTable>> getTablesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(coffeeTableService.getTablesByStatus(status));
    }

    @PutMapping("/tables/{id}/status")
    public ResponseEntity<String> updateTableStatus(@PathVariable Integer id, 
                                                   @RequestParam String status) {
        coffeeTableService.updateTableStatus(id, status);
        return ResponseEntity.ok("Status updated successfully");
    }

    // Invoice APIs
    @GetMapping("/invoices")
    public ResponseEntity<List<Invoice>> getAllInvoices() {
        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/invoices/{id}")
    public ResponseEntity<Invoice> getInvoiceById(@PathVariable Integer id) {
        Optional<Invoice> invoice = invoiceService.getInvoiceById(id);
        return invoice.map(ResponseEntity::ok)
                     .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/invoices")
    public ResponseEntity<Invoice> createInvoice(@RequestBody Map<String, Object> request) {
        try {
            Integer tableId = (Integer) request.get("tableId");
            Integer employeeId = (Integer) request.get("employeeId");
            List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");

            CoffeeTable table = coffeeTableService.getTableById(tableId).orElse(null);
            Employee employee = employeeService.getEmployeeById(employeeId).orElse(null);

            if (table == null || employee == null) {
                return ResponseEntity.badRequest().build();
            }

            Invoice invoice = new Invoice();
            invoice.setCoffeeTable(table);
            invoice.setEmployee(employee);
            invoice.setCreatedAt(LocalDateTime.now());
            invoice.setStatus("Unpaid");
            invoice.setTotalAmount(0.0);

            double totalAmount = 0.0;
            for (Map<String, Object> item : items) {
                Integer menuId = (Integer) item.get("menuId");
                Integer quantity = (Integer) item.get("quantity");
                
                Menu menu = menuService.getMenuItemById(menuId).orElse(null);
                if (menu != null && quantity > 0) {
                    totalAmount += menu.getPrice() * quantity;
                }
            }

            invoice.setTotalAmount(totalAmount);
            Invoice savedInvoice = invoiceService.saveInvoice(invoice);

            // Cập nhật trạng thái bàn
            coffeeTableService.updateTableStatus(tableId, "Occupied");

            return ResponseEntity.ok(savedInvoice);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/invoices/{id}/pay")
    public ResponseEntity<String> payInvoice(@PathVariable Integer id) {
        Optional<Invoice> invoiceOpt = invoiceService.getInvoiceById(id);
        if (invoiceOpt.isPresent()) {
            Invoice invoice = invoiceOpt.get();
            invoice.setStatus("Paid");
            invoiceService.saveInvoice(invoice);
            
            // Cập nhật trạng thái bàn
            coffeeTableService.updateTableStatus(invoice.getCoffeeTable().getId(), "Available");
            
            return ResponseEntity.ok("Invoice paid successfully");
        }
        return ResponseEntity.notFound().build();
    }

    // Statistics APIs
    @GetMapping("/stats/overview")
    public ResponseEntity<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalEmployees", employeeService.getAllEmployees().size());
        stats.put("totalTables", coffeeTableService.getAllTables().size());
        stats.put("totalMenuItems", menuService.getAllMenuItems().size());
        stats.put("totalInvoices", invoiceService.getAllInvoices().size());
        stats.put("totalRevenue", invoiceService.getTotalRevenue());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/tables")
    public ResponseEntity<Map<String, Object>> getTableStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("available", coffeeTableService.getTablesByStatus("Available").size());
        stats.put("occupied", coffeeTableService.getTablesByStatus("Occupied").size());
        stats.put("reserved", coffeeTableService.getTablesByStatus("Reserved").size());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/stats/invoices")
    public ResponseEntity<Map<String, Object>> getInvoiceStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("paid", invoiceService.getInvoicesByStatus("Paid").size());
        stats.put("unpaid", invoiceService.getInvoicesByStatus("Unpaid").size());
        
        return ResponseEntity.ok(stats);
    }
} 