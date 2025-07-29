package com.coffeeshop.shopcoffeemanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private Long id;
    private String invoiceNumber;
    private CoffeeTable table;
    private Employee employee;
    private LocalDateTime orderTime;
    private LocalDateTime paymentTime;
    private String status; // PENDING, PAID, CANCELLED
    private BigDecimal totalAmount;
    private String paymentMethod; // CASH, CARD, TRANSFER
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<InvoiceDetail> details;

    public Invoice() {
        this.details = new ArrayList<>();
        this.totalAmount = BigDecimal.ZERO;
        this.status = "PENDING";
    }

    public Invoice(String invoiceNumber, CoffeeTable table, Employee employee) {
        this();
        this.invoiceNumber = invoiceNumber;
        this.table = table;
        this.employee = employee;
        this.orderTime = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public CoffeeTable getTable() { return table; }
    public void setTable(CoffeeTable table) { this.table = table; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDateTime getOrderTime() { return orderTime; }
    public void setOrderTime(LocalDateTime orderTime) { this.orderTime = orderTime; }

    public LocalDateTime getPaymentTime() { return paymentTime; }
    public void setPaymentTime(LocalDateTime paymentTime) { this.paymentTime = paymentTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<InvoiceDetail> getDetails() { return details; }
    public void setDetails(List<InvoiceDetail> details) { this.details = details; }
    
    // Convenience methods for InvoiceController
    public String getTableNumber() { 
        return table != null ? table.getTableNumber() : "N/A"; 
    }
    
    public void setTableNumber(String tableNumber) {
        // This method is for demo purposes only
        // In real implementation, you would update the table object
    }
    
    public String getCustomerName() { 
        return "Khách hàng"; // Default customer name for demo
    }
    
    public void setCustomerName(String customerName) {
        // This method is for demo purposes only
    }

    public void addDetail(InvoiceDetail detail) {
        this.details.add(detail);
        calculateTotal();
    }

    public void removeDetail(InvoiceDetail detail) {
        this.details.remove(detail);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = details.stream()
                .map(InvoiceDetail::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getFormattedTotal() {
        return String.format("%,.0f VNĐ", totalAmount);
    }

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isPaid() {
        return "PAID".equals(status);
    }

    @Override
    public String toString() {
        return "Hóa đơn " + invoiceNumber + " - " + getFormattedTotal();
    }
} 