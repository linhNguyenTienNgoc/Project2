package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.service.*;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.math.BigDecimal;

@Controller
public class DashboardController {
    
    private final EmployeeService employeeService;
    private final CoffeeTableService coffeeTableService;
    private final MenuService menuService;
    private final InvoiceService invoiceService;
    
    public DashboardController(EmployeeService employeeService, 
                             CoffeeTableService coffeeTableService,
                             MenuService menuService,
                             InvoiceService invoiceService) {
        this.employeeService = employeeService;
        this.coffeeTableService = coffeeTableService;
        this.menuService = menuService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        Employee employee = (Employee) session.getAttribute("employee");
        if (employee == null) {
            return "redirect:/login";
        }

        // Thống kê tổng quan - Sử dụng count thay vì load tất cả
        long totalEmployees = employeeService.countEmployees();
        long totalTables = coffeeTableService.countTables();
        long totalMenuItems = menuService.countMenuItems();
        long totalInvoices = invoiceService.countInvoices();
        
        // Thống kê bàn theo trạng thái
        long availableTables = coffeeTableService.countTablesByStatus("AVAILABLE");
        long occupiedTables = coffeeTableService.countTablesByStatus("OCCUPIED");
        long reservedTables = coffeeTableService.countTablesByStatus("RESERVED");
        
        // Thống kê hóa đơn theo trạng thái
        long paidInvoices = invoiceService.countInvoicesByStatus("PAID");
        long unpaidInvoices = invoiceService.countInvoicesByStatus("PENDING");
        
        // Date handling
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        Double todayRevenue = invoiceService.getTotalRevenueByDateRange(todayStart, todayEnd);
        Double totalRevenue = invoiceService.getTotalRevenue();
        
        // Revenue formatting
        String formattedTodayRevenue = "₫0";
        if (todayRevenue != null) {
            formattedTodayRevenue = "₫" + todayRevenue.toString();
        }
        
        String formattedTotalRevenue = "₫0";
        if (totalRevenue != null) {
            formattedTotalRevenue = "₫" + totalRevenue.toString();
        }

        model.addAttribute("employee", employee);
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("totalTables", totalTables);
        model.addAttribute("totalMenuItems", totalMenuItems);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("availableTables", availableTables);
        model.addAttribute("occupiedTables", occupiedTables);
        model.addAttribute("reservedTables", reservedTables);
        model.addAttribute("paidInvoices", paidInvoices);
        model.addAttribute("unpaidInvoices", unpaidInvoices);
        model.addAttribute("todayRevenue", formattedTodayRevenue);
        model.addAttribute("totalRevenue", formattedTotalRevenue);
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("timePeriod", "Current Time");

        return "dashboard/index";
    }
} 