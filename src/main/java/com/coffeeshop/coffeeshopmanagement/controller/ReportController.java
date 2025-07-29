package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.entity.Invoice;
import com.coffeeshop.coffeeshopmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/reports")
public class ReportController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private EmployeeService employeeService;
    
    @Autowired
    private CoffeeTableService coffeeTableService;
    
    @Autowired
    private MenuService menuService;

    @GetMapping
    public String reportsPage(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        // Chỉ admin mới được xem báo cáo
        if (!"admin".equals(currentEmployee.getRole())) {
            return "redirect:/dashboard";
        }

        // Thống kê tổng quan
        Double totalRevenue = invoiceService.getTotalRevenue();
        long totalInvoices = invoiceService.getAllInvoices().size();
        long paidInvoices = invoiceService.getInvoicesByStatus("Paid").size();
        long unpaidInvoices = invoiceService.getInvoicesByStatus("Unpaid").size();

        // Doanh thu hôm nay
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        Double todayRevenue = invoiceService.getTotalRevenueByDateRange(todayStart, todayEnd);

        // Doanh thu tuần này
        LocalDateTime weekStart = LocalDate.now().minusDays(7).atStartOfDay();
        Double weekRevenue = invoiceService.getTotalRevenueByDateRange(weekStart, todayEnd);

        // Doanh thu tháng này
        LocalDateTime monthStart = LocalDate.now().minusDays(30).atStartOfDay();
        Double monthRevenue = invoiceService.getTotalRevenueByDateRange(monthStart, todayEnd);

        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        model.addAttribute("totalInvoices", totalInvoices);
        model.addAttribute("paidInvoices", paidInvoices);
        model.addAttribute("unpaidInvoices", unpaidInvoices);
        model.addAttribute("todayRevenue", todayRevenue != null ? todayRevenue : 0.0);
        model.addAttribute("weekRevenue", weekRevenue != null ? weekRevenue : 0.0);
        model.addAttribute("monthRevenue", monthRevenue != null ? monthRevenue : 0.0);

        return "report/index";
    }

    @GetMapping("/revenue")
    public String revenueReport(@RequestParam(required = false) String startDate,
                              @RequestParam(required = false) String endDate,
                              HttpSession session,
                              Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        // TODO: Implement date parsing and filtering
        List<Invoice> invoices = invoiceService.getAllInvoices();
        Double totalRevenue = invoiceService.getTotalRevenue();

        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("invoices", invoices);
        model.addAttribute("totalRevenue", totalRevenue != null ? totalRevenue : 0.0);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);

        return "report/revenue";
    }

    @GetMapping("/sales")
    public String salesReport(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        // Lấy hóa đơn đã thanh toán
        List<Invoice> paidInvoices = invoiceService.getInvoicesByStatus("Paid");

        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("paidInvoices", paidInvoices);

        return "report/sales";
    }

    @GetMapping("/employee-performance")
    public String employeePerformanceReport(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"admin".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        List<Employee> employees = employeeService.getAllEmployees();

        model.addAttribute("currentEmployee", currentEmployee);
        model.addAttribute("employees", employees);

        return "report/employee-performance";
    }
} 