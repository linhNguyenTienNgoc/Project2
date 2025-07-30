package com.coffeeshop.shopcoffeemanagement.service;

import com.coffeeshop.shopcoffeemanagement.model.Invoice;
import com.coffeeshop.shopcoffeemanagement.model.Employee;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

public class PaymentService {
    
    public enum PaymentMethod {
        CASH("Tiền mặt"),
        CARD("Thẻ"),
        TRANSFER("Chuyển khoản"),
        QR_CODE("QR Code"),
        E_WALLET("Ví điện tử");
        
        private final String displayName;
        
        PaymentMethod(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PaymentStatus {
        PENDING("Chờ thanh toán"),
        PROCESSING("Đang xử lý"),
        COMPLETED("Hoàn thành"),
        FAILED("Thất bại"),
        CANCELLED("Đã hủy"),
        REFUNDED("Đã hoàn tiền");
        
        private final String displayName;
        
        PaymentStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public static class PaymentResult {
        private boolean success;
        private String message;
        private String transactionId;
        private PaymentStatus status;
        private LocalDateTime processedAt;
        
        public PaymentResult(boolean success, String message) {
            this.success = success;
            this.message = message;
            this.processedAt = LocalDateTime.now();
        }
        
        public PaymentResult(boolean success, String message, String transactionId, PaymentStatus status) {
            this(success, message);
            this.transactionId = transactionId;
            this.status = status;
        }
        
        // Getters
        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public String getTransactionId() { return transactionId; }
        public PaymentStatus getStatus() { return status; }
        public LocalDateTime getProcessedAt() { return processedAt; }
    }
    
    public static class SplitBillRequest {
        private Invoice originalInvoice;
        private List<SplitBillItem> splitItems;
        private List<PaymentMethod> paymentMethods;
        
        public SplitBillRequest(Invoice originalInvoice) {
            this.originalInvoice = originalInvoice;
            this.splitItems = new ArrayList<>();
            this.paymentMethods = new ArrayList<>();
        }
        
        public void addSplitItem(String customerName, BigDecimal amount, PaymentMethod paymentMethod) {
            splitItems.add(new SplitBillItem(customerName, amount, paymentMethod));
            if (!paymentMethods.contains(paymentMethod)) {
                paymentMethods.add(paymentMethod);
            }
        }
        
        // Getters
        public Invoice getOriginalInvoice() { return originalInvoice; }
        public List<SplitBillItem> getSplitItems() { return splitItems; }
        public List<PaymentMethod> getPaymentMethods() { return paymentMethods; }
        
        public BigDecimal getTotalAmount() {
            return splitItems.stream()
                .map(SplitBillItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        
        public boolean isValid() {
            return getTotalAmount().compareTo(originalInvoice.getTotalAmount()) == 0;
        }
    }
    
    public static class SplitBillItem {
        private String customerName;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private PaymentResult paymentResult;
        
        public SplitBillItem(String customerName, BigDecimal amount, PaymentMethod paymentMethod) {
            this.customerName = customerName;
            this.amount = amount;
            this.paymentMethod = paymentMethod;
        }
        
        // Getters and Setters
        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }
        
        public BigDecimal getAmount() { return amount; }
        public void setAmount(BigDecimal amount) { this.amount = amount; }
        
        public PaymentMethod getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
        
        public PaymentResult getPaymentResult() { return paymentResult; }
        public void setPaymentResult(PaymentResult paymentResult) { this.paymentResult = paymentResult; }
    }
    
    public PaymentResult processPayment(Invoice invoice, PaymentMethod method, BigDecimal amount, Employee cashier) {
        try {
            // Validate payment
            if (amount.compareTo(invoice.getTotalAmount()) < 0) {
                return new PaymentResult(false, "Số tiền thanh toán không đủ");
            }
            
            // Process payment based on method
            switch (method) {
                case CASH:
                    return processCashPayment(invoice, amount, cashier);
                case CARD:
                    return processCardPayment(invoice, amount, cashier);
                case TRANSFER:
                    return processTransferPayment(invoice, amount, cashier);
                case QR_CODE:
                    return processQRPayment(invoice, amount, cashier);
                case E_WALLET:
                    return processEWalletPayment(invoice, amount, cashier);
                default:
                    return new PaymentResult(false, "Phương thức thanh toán không được hỗ trợ");
            }
            
        } catch (Exception e) {
            return new PaymentResult(false, "Lỗi xử lý thanh toán: " + e.getMessage());
        }
    }
    
    public PaymentResult processSplitBill(SplitBillRequest request, Employee cashier) {
        try {
            // Validate split bill
            if (!request.isValid()) {
                return new PaymentResult(false, "Tổng tiền chia bill không khớp với hóa đơn gốc");
            }
            
            List<PaymentResult> results = new ArrayList<>();
            
            // Process each split item
            for (SplitBillItem item : request.getSplitItems()) {
                PaymentResult result = processPayment(
                    request.getOriginalInvoice(), 
                    item.getPaymentMethod(), 
                    item.getAmount(), 
                    cashier
                );
                
                item.setPaymentResult(result);
                results.add(result);
                
                if (!result.isSuccess()) {
                    return new PaymentResult(false, 
                        "Lỗi thanh toán cho " + item.getCustomerName() + ": " + result.getMessage());
                }
            }
            
            return new PaymentResult(true, "Chia bill thành công", 
                generateTransactionId(), PaymentStatus.COMPLETED);
            
        } catch (Exception e) {
            return new PaymentResult(false, "Lỗi xử lý chia bill: " + e.getMessage());
        }
    }
    
    public PaymentResult applyDiscount(Invoice invoice, BigDecimal discountAmount, String discountReason) {
        try {
            if (discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return new PaymentResult(false, "Số tiền giảm giá phải lớn hơn 0");
            }
            
            if (discountAmount.compareTo(invoice.getTotalAmount()) >= 0) {
                return new PaymentResult(false, "Số tiền giảm giá không được lớn hơn hoặc bằng tổng hóa đơn");
            }
            
            // Apply discount
            BigDecimal newTotal = invoice.getTotalAmount().subtract(discountAmount);
            invoice.setTotalAmount(newTotal);
            
            // Add discount note
            String currentNotes = invoice.getNotes() != null ? invoice.getNotes() : "";
            String discountNote = String.format("\nGiảm giá: %,.0f VNĐ (%s)", discountAmount, discountReason);
            invoice.setNotes(currentNotes + discountNote);
            
            return new PaymentResult(true, 
                String.format("Đã giảm giá %,.0f VNĐ", discountAmount));
            
        } catch (Exception e) {
            return new PaymentResult(false, "Lỗi áp dụng giảm giá: " + e.getMessage());
        }
    }
    
    public PaymentResult addTip(Invoice invoice, BigDecimal tipAmount, String tipReason) {
        try {
            if (tipAmount.compareTo(BigDecimal.ZERO) <= 0) {
                return new PaymentResult(false, "Số tiền tip phải lớn hơn 0");
            }
            
            // Add tip
            BigDecimal newTotal = invoice.getTotalAmount().add(tipAmount);
            invoice.setTotalAmount(newTotal);
            
            // Add tip note
            String currentNotes = invoice.getNotes() != null ? invoice.getNotes() : "";
            String tipNote = String.format("\nTip: %,.0f VNĐ (%s)", tipAmount, tipReason);
            invoice.setNotes(currentNotes + tipNote);
            
            return new PaymentResult(true, 
                String.format("Đã thêm tip %,.0f VNĐ", tipAmount));
            
        } catch (Exception e) {
            return new PaymentResult(false, "Lỗi thêm tip: " + e.getMessage());
        }
    }
    
    private PaymentResult processCashPayment(Invoice invoice, BigDecimal amount, Employee cashier) {
        // Simulate cash payment processing
        String transactionId = generateTransactionId();
        
        // Update invoice
        invoice.setPaymentMethod("CASH");
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());
        invoice.setTransactionId(transactionId);
        
        return new PaymentResult(true, 
            String.format("Thanh toán tiền mặt thành công. Số tiền: %,.0f VNĐ", amount),
            transactionId, PaymentStatus.COMPLETED);
    }
    
    private PaymentResult processCardPayment(Invoice invoice, BigDecimal amount, Employee cashier) {
        // Simulate card payment processing
        String transactionId = generateTransactionId();
        
        // Update invoice
        invoice.setPaymentMethod("CARD");
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());
        invoice.setTransactionId(transactionId);
        
        return new PaymentResult(true, 
            String.format("Thanh toán thẻ thành công. Số tiền: %,.0f VNĐ", amount),
            transactionId, PaymentStatus.COMPLETED);
    }
    
    private PaymentResult processTransferPayment(Invoice invoice, BigDecimal amount, Employee cashier) {
        // Simulate transfer payment processing
        String transactionId = generateTransactionId();
        
        // Update invoice
        invoice.setPaymentMethod("TRANSFER");
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());
        invoice.setTransactionId(transactionId);
        
        return new PaymentResult(true, 
            String.format("Chuyển khoản thành công. Số tiền: %,.0f VNĐ", amount),
            transactionId, PaymentStatus.COMPLETED);
    }
    
    private PaymentResult processQRPayment(Invoice invoice, BigDecimal amount, Employee cashier) {
        // Simulate QR payment processing
        String transactionId = generateTransactionId();
        
        // Update invoice
        invoice.setPaymentMethod("QR_CODE");
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());
        invoice.setTransactionId(transactionId);
        
        return new PaymentResult(true, 
            String.format("Thanh toán QR thành công. Số tiền: %,.0f VNĐ", amount),
            transactionId, PaymentStatus.COMPLETED);
    }
    
    private PaymentResult processEWalletPayment(Invoice invoice, BigDecimal amount, Employee cashier) {
        // Simulate e-wallet payment processing
        String transactionId = generateTransactionId();
        
        // Update invoice
        invoice.setPaymentMethod("E_WALLET");
        invoice.setStatus("PAID");
        invoice.setPaymentTime(LocalDateTime.now());
        invoice.setTransactionId(transactionId);
        
        return new PaymentResult(true, 
            String.format("Thanh toán ví điện tử thành công. Số tiền: %,.0f VNĐ", amount),
            transactionId, PaymentStatus.COMPLETED);
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis();
    }
    
    public static String formatCurrency(BigDecimal amount) {
        return String.format("%,.0f VNĐ", amount);
    }
} 