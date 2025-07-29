package com.coffeeshop.shopcoffeemanagement.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceDetail {
    private Long id;
    private Invoice invoice;
    private Menu menu;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String notes;
    private LocalDateTime createdAt;

    public InvoiceDetail() {}

    public InvoiceDetail(Invoice invoice, Menu menu, int quantity) {
        this.invoice = invoice;
        this.menu = menu;
        this.quantity = quantity;
        this.unitPrice = menu.getPrice();
        calculateTotal();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }

    public Menu getMenu() { return menu; }
    public void setMenu(Menu menu) { this.menu = menu; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { 
        this.quantity = quantity; 
        calculateTotal();
    }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { 
        this.unitPrice = unitPrice; 
        calculateTotal();
    }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public void calculateTotal() {
        if (unitPrice != null && quantity > 0) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }

    public String getFormattedTotal() {
        return String.format("%,.0f VNĐ", totalPrice);
    }

    public String getFormattedUnitPrice() {
        return String.format("%,.0f VNĐ", unitPrice);
    }
    
    // Convenience methods for InvoiceController
    public String getMenuItemName() { 
        return menu != null ? menu.getName() : "N/A"; 
    }
    
    public BigDecimal getPrice() { 
        return unitPrice != null ? unitPrice : BigDecimal.ZERO; 
    }

    @Override
    public String toString() {
        return menu.getName() + " x" + quantity + " - " + getFormattedTotal();
    }
} 