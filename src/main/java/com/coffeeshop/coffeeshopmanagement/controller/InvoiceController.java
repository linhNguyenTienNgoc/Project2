package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.*;
import com.coffeeshop.coffeeshopmanagement.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {
    
    @Autowired
    private InvoiceService invoiceService;
    
    @Autowired
    private InvoiceDetailService invoiceDetailService;
    
    @Autowired
    private CoffeeTableService coffeeTableService;
    
    @Autowired
    private MenuService menuService;
    
    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String listInvoices(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<Invoice> invoices = invoiceService.getAllInvoices();
        model.addAttribute("invoices", invoices);
        model.addAttribute("currentEmployee", currentEmployee);
        return "invoice/list";
    }

    @GetMapping("/create")
    public String createInvoiceForm(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<CoffeeTable> availableTables = coffeeTableService.getTablesByStatus("Available");
        List<Menu> menuItems = menuService.getAllMenuItems();

        model.addAttribute("availableTables", availableTables);
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("currentEmployee", currentEmployee);
        return "invoice/create";
    }

    @PostMapping("/create")
    public String createInvoice(@RequestParam Integer tableId,
                              @RequestParam Integer[] menuIds,
                              @RequestParam Integer[] quantities,
                              HttpSession session,
                              Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        CoffeeTable table = coffeeTableService.getTableById(tableId).orElse(null);
        if (table == null) {
            return "redirect:/invoices";
        }

        // Tạo hóa đơn mới
        Invoice invoice = new Invoice();
        invoice.setCoffeeTable(table);
        invoice.setEmployee(currentEmployee);
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setStatus("Unpaid");
        invoice.setTotalAmount(0.0);

        // Tính tổng tiền và tạo chi tiết hóa đơn
        double totalAmount = 0.0;
        for (int i = 0; i < menuIds.length; i++) {
            Menu menu = menuService.getMenuItemById(menuIds[i]).orElse(null);
            if (menu != null && quantities[i] > 0) {
                InvoiceDetail detail = new InvoiceDetail();
                detail.setInvoice(invoice);
                detail.setMenu(menu);
                detail.setQuantity(quantities[i]);
                detail.setPrice(menu.getPrice());
                totalAmount += menu.getPrice() * quantities[i];
            }
        }

        invoice.setTotalAmount(totalAmount);
        invoiceService.saveInvoice(invoice);

        // Cập nhật trạng thái bàn
        coffeeTableService.updateTableStatus(tableId, "Occupied");

        return "redirect:/invoices";
    }

    @GetMapping("/{id}")
    public String viewInvoice(@PathVariable Integer id, HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        Invoice invoice = invoiceService.getInvoiceById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/invoices";
        }

        List<InvoiceDetail> details = invoiceDetailService.getInvoiceDetailsByInvoiceId(id);

        model.addAttribute("invoice", invoice);
        model.addAttribute("details", details);
        model.addAttribute("currentEmployee", currentEmployee);
        return "invoice/view";
    }

    @PostMapping("/{id}/pay")
    public String payInvoice(@PathVariable Integer id, HttpSession session) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        Invoice invoice = invoiceService.getInvoiceById(id).orElse(null);
        if (invoice == null) {
            return "redirect:/invoices";
        }

        invoice.setStatus("Paid");
        invoiceService.saveInvoice(invoice);

        // Cập nhật trạng thái bàn về Available
        coffeeTableService.updateTableStatus(invoice.getCoffeeTable().getId(), "Available");

        return "redirect:/invoices";
    }

    @GetMapping("/search")
    public String searchInvoices(@RequestParam(required = false) String status,
                               @RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate,
                               HttpSession session,
                               Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<Invoice> invoices = invoiceService.getAllInvoices();

        // Lọc theo trạng thái
        if (status != null && !status.trim().isEmpty()) {
            invoices = invoices.stream()
                    .filter(invoice -> invoice.getStatus().equals(status))
                    .collect(java.util.stream.Collectors.toList());
        }

        // Lọc theo ngày (có thể mở rộng thêm)
        // TODO: Implement date filtering

        model.addAttribute("invoices", invoices);
        model.addAttribute("status", status);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("currentEmployee", currentEmployee);
        return "invoice/list";
    }
} 