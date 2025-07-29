package com.coffeeshop.coffeeshopmanagement.service;

import com.coffeeshop.coffeeshopmanagement.entity.Invoice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface InvoiceService {
    List<Invoice> getAllInvoices();
    Optional<Invoice> getInvoiceById(Integer id);
    Invoice saveInvoice(Invoice invoice);
    void deleteInvoice(Integer id);
    List<Invoice> getInvoicesByStatus(String status);
    List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    List<Invoice> getInvoicesByEmployee(Integer employeeId);
    List<Invoice> getInvoicesByTable(Integer tableId);
    Double getTotalRevenue();
    Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    long countInvoices();
    long countInvoicesByStatus(String status);
} 