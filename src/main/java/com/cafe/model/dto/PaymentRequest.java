package com.cafe.model.dto;

/**
 * Payment Request DTO
 * Encapsulates all payment request data
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PaymentRequest {
    private Integer orderId;
    private String paymentMethod;
    private double amountReceived;
    private String transactionCode;
    private String notes;
    private double vatPercent = 8.0; // Default VAT
    private double discountAmount = 0.0;
    
    // Constructors
    public PaymentRequest() {}
    
    public PaymentRequest(Integer orderId, String paymentMethod, double amountReceived) {
        this.orderId = orderId;
        this.paymentMethod = paymentMethod;
        this.amountReceived = amountReceived;
    }
    
    // Getters and Setters with fluent interface
    public Integer getOrderId() { return orderId; }
    public PaymentRequest setOrderId(Integer orderId) { 
        this.orderId = orderId; 
        return this; 
    }
    
    public String getPaymentMethod() { return paymentMethod; }
    public PaymentRequest setPaymentMethod(String paymentMethod) { 
        this.paymentMethod = paymentMethod; 
        return this; 
    }
    
    public double getAmountReceived() { return amountReceived; }
    public PaymentRequest setAmountReceived(double amountReceived) { 
        this.amountReceived = amountReceived; 
        return this; 
    }
    
    public String getTransactionCode() { return transactionCode; }
    public PaymentRequest setTransactionCode(String transactionCode) { 
        this.transactionCode = transactionCode; 
        return this; 
    }
    
    public String getNotes() { return notes; }
    public PaymentRequest setNotes(String notes) { 
        this.notes = notes; 
        return this; 
    }
    
    public double getVatPercent() { return vatPercent; }
    public PaymentRequest setVatPercent(double vatPercent) { 
        this.vatPercent = vatPercent; 
        return this; 
    }
    
    public double getDiscountAmount() { return discountAmount; }
    public PaymentRequest setDiscountAmount(double discountAmount) { 
        this.discountAmount = discountAmount; 
        return this; 
    }
}
