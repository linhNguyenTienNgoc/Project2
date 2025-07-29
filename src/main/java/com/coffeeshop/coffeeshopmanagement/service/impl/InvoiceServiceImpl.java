package com.coffeeshop.coffeeshopmanagement.service.impl;

import com.coffeeshop.coffeeshopmanagement.entity.Invoice;
import com.coffeeshop.coffeeshopmanagement.repository.InvoiceRepository;
import com.coffeeshop.coffeeshopmanagement.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceServiceImpl implements InvoiceService {
    
    @Autowired
    private InvoiceRepository invoiceRepository;

    @Override
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    @Override
    public Optional<Invoice> getInvoiceById(Integer id) {
        return invoiceRepository.findById(id);
    }

    @Override
    public Invoice saveInvoice(Invoice invoice) {
        return invoiceRepository.save(invoice);
    }

    @Override
    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }

    @Override
    public List<Invoice> getInvoicesByStatus(String status) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> invoice.getStatus().equals(status))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Invoice> getInvoicesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> !invoice.getCreatedAt().isBefore(startDate) && 
                                 !invoice.getCreatedAt().isAfter(endDate))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Invoice> getInvoicesByEmployee(Integer employeeId) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> invoice.getEmployee().getId().equals(employeeId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Invoice> getInvoicesByTable(Integer tableId) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> invoice.getCoffeeTable().getId().equals(tableId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public Double getTotalRevenue() {
        List<Invoice> paidInvoices = getInvoicesByStatus("Paid");
        return paidInvoices.stream()
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    @Override
    public Double getTotalRevenueByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Invoice> invoicesInRange = getInvoicesByDateRange(startDate, endDate);
        return invoicesInRange.stream()
                .filter(invoice -> "Paid".equals(invoice.getStatus()))
                .mapToDouble(Invoice::getTotalAmount)
                .sum();
    }

    @Override
    public long countInvoices() {
        return invoiceRepository.count();
    }

    @Override
    public long countInvoicesByStatus(String status) {
        List<Invoice> allInvoices = invoiceRepository.findAll();
        return allInvoices.stream()
                .filter(invoice -> invoice.getStatus().equals(status))
                .count();
    }
} 