package com.cafe.model.dto;

import com.cafe.model.entity.Order;

/**
 * Payment Response DTO
 * Encapsulates payment processing results
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PaymentResponse {
    private boolean success;
    private String message;
    private String transactionId;
    private double changeAmount;
    private Order order;
    private String receiptPath;
    private long timestamp;
    
    public PaymentResponse() {
        this.timestamp = System.currentTimeMillis();
    }
    
    // Static factory methods
    public static PaymentResponse success(String message) {
        PaymentResponse response = new PaymentResponse();
        response.success = true;
        response.message = message;
        return response;
    }
    
    public static PaymentResponse failure(String message) {
        PaymentResponse response = new PaymentResponse();
        response.success = false;
        response.message = message;
        return response;
    }
    
    // Getters and Setters with fluent interface
    public boolean isSuccess() { return success; }
    public PaymentResponse setSuccess(boolean success) { 
        this.success = success; 
        return this; 
    }
    
    public String getMessage() { return message; }
    public PaymentResponse setMessage(String message) { 
        this.message = message; 
        return this; 
    }
    
    public String getTransactionId() { return transactionId; }
    public PaymentResponse setTransactionId(String transactionId) { 
        this.transactionId = transactionId; 
        return this; 
    }
    
    public double getChangeAmount() { return changeAmount; }
    public PaymentResponse setChangeAmount(double changeAmount) { 
        this.changeAmount = changeAmount; 
        return this; 
    }
    
    public Order getOrder() { return order; }
    public PaymentResponse setOrder(Order order) { 
        this.order = order; 
        return this; 
    }
    
    public String getReceiptPath() { return receiptPath; }
    public PaymentResponse setReceiptPath(String receiptPath) { 
        this.receiptPath = receiptPath; 
        return this; 
    }
    
    public long getTimestamp() { return timestamp; }
}
