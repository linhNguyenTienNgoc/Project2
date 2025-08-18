package com.cafe.util;

import com.cafe.model.dto.PaymentRequest;
import com.cafe.model.enums.PaymentMethod;

/**
 * Payment Validator
 * Handles all payment validation logic
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PaymentValidator {
    
    /**
     * Validate payment request
     */
    public boolean validatePaymentRequest(PaymentRequest request) {
        if (request == null) {
            System.err.println("❌ Payment request is null");
            return false;
        }
        
        if (request.getOrderId() == null || request.getOrderId() <= 0) {
            System.err.println("❌ Invalid order ID");
            return false;
        }
        
        if (request.getPaymentMethod() == null || request.getPaymentMethod().trim().isEmpty()) {
            System.err.println("❌ Payment method is required");
            return false;
        }
        
        if (request.getAmountReceived() < 0) {
            System.err.println("❌ Amount received cannot be negative");
            return false;
        }
        
        return validatePaymentMethodSpecificRules(request);
    }
    
    /**
     * Validate amount based on payment method
     */
    public boolean validateAmount(double amountReceived, double requiredAmount, String paymentMethod) {
        PaymentMethod method = PaymentMethod.fromString(paymentMethod);
        
        switch (method) {
            case CASH:
                return amountReceived >= requiredAmount;
            case CARD:
            case MOMO:
            case VNPAY:
            case ZALOPAY:
            case BANK_TRANSFER:
                // For electronic payments, exact amount is expected
                return Math.abs(amountReceived - requiredAmount) < 0.01;
            default:
                return false;
        }
    }
    
    /**
     * Validate payment method specific rules
     */
    private boolean validatePaymentMethodSpecificRules(PaymentRequest request) {
        PaymentMethod method = PaymentMethod.fromString(request.getPaymentMethod());
        
        switch (method) {
            case CASH:
                return true; // No additional validation for cash
                
            case CARD:
            case MOMO:
            case VNPAY:
            case ZALOPAY:
            case BANK_TRANSFER:
                if (request.getTransactionCode() == null || request.getTransactionCode().trim().isEmpty()) {
                    System.err.println("❌ Transaction code required for " + method.getDisplayName());
                    return false;
                }
                return validateTransactionCode(request.getTransactionCode(), method);
                
            default:
                System.err.println("❌ Unsupported payment method: " + request.getPaymentMethod());
                return false;
        }
    }
    
    /**
     * Validate transaction code format
     */
    private boolean validateTransactionCode(String transactionCode, PaymentMethod method) {
        if (transactionCode == null || transactionCode.trim().isEmpty()) {
            return false;
        }
        
        switch (method) {
            case CARD:
                return transactionCode.length() >= 6 && transactionCode.length() <= 20;
            case MOMO:
                return transactionCode.length() >= 8 && transactionCode.length() <= 15;
            case VNPAY:
            case ZALOPAY:
                return transactionCode.length() >= 10 && transactionCode.length() <= 25;
            case BANK_TRANSFER:
                return transactionCode.length() >= 10 && transactionCode.length() <= 30;
            default:
                return true;
        }
    }
    
    /**
     * Validate VAT percentage
     */
    public boolean validateVATPercent(double vatPercent) {
        return vatPercent >= 0 && vatPercent <= 30; // 0-30% VAT range
    }
    
    /**
     * Validate discount amount
     */
    public boolean validateDiscountAmount(double discountAmount, double subtotal) {
        return discountAmount >= 0 && discountAmount <= subtotal;
    }
}
