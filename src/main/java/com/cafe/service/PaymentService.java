package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDAO;
import com.cafe.dao.base.OrderDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.enums.PaymentMethod;
import com.cafe.model.enums.PaymentStatus;
import com.cafe.model.dto.PaymentRequest;
import com.cafe.model.dto.PaymentResponse;
import com.cafe.util.PaymentValidator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Payment Service - Complete Payment Processing System
 * Xử lý tất cả các nghiệp vụ liên quan đến thanh toán
 * 
 * @author Team 2_C2406L
 * @version 3.0.0 (Enhanced Payment System)
 */
public class PaymentService {
    
    private final ReceiptService receiptService;
    private final PaymentValidator validator;
    
    public PaymentService() {
        this.receiptService = new ReceiptService();
        this.validator = new PaymentValidator();
    }

    // =====================================================
    // ENHANCED PAYMENT PROCESSING
    // =====================================================
    
    /**
     * Process payment with complete validation and business logic
     */
    public PaymentResponse processPayment(PaymentRequest request) {
        System.out.println("🔍 PaymentService.processPayment(request) called");
        System.out.println("🔍 Request Order ID: " + request.getOrderId());
        System.out.println("🔍 Request Payment Method: " + request.getPaymentMethod());
        System.out.println("🔍 Request Amount: " + request.getAmountReceived());
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            // 1. Validate payment request
            System.out.println("🔍 Step 1: Validating payment request...");
            if (!validator.validatePaymentRequest(request)) {
                System.out.println("🔍 ❌ Payment request validation failed");
                return PaymentResponse.failure("Invalid payment request");
            }
            System.out.println("🔍 ✅ Payment request validation passed");
            
            // 2. Get order from database
            System.out.println("🔍 Step 2: Getting order from database...");
            OrderDAO orderDAO = new OrderDAOImpl(connection);
            Optional<Order> orderOpt = orderDAO.findById(request.getOrderId());
            if (!orderOpt.isPresent()) {
                System.out.println("🔍 ❌ Order not found: " + request.getOrderId());
                return PaymentResponse.failure("Order not found");
            }
            System.out.println("🔍 ✅ Order found: " + orderOpt.get().getOrderNumber());
            
            Order order = orderOpt.get();
            
            // 3. Validate order can be paid
            System.out.println("🔍 Step 3: Validating order can be paid...");
            System.out.println("🔍 Order status: " + order.getOrderStatus());
            System.out.println("🔍 Order can be paid: " + order.canBePaid());
            if (!order.canBePaid()) {
                System.out.println("🔍 ❌ Order cannot be paid. Status: " + order.getOrderStatus());
                return PaymentResponse.failure("Order cannot be paid. Status: " + order.getOrderStatus());
            }
            System.out.println("🔍 ✅ Order can be paid");
            
            // 4. Validate payment amount
            System.out.println("🔍 Step 4: Validating payment amount...");
            System.out.println("🔍 Amount received: " + request.getAmountReceived());
            System.out.println("🔍 Order final amount: " + order.getFinalAmount());
            
            // For electronic payments, amountReceived already includes VAT and promotions
            // So we should compare with the actual amount to be paid
            double amountToValidate = request.getAmountReceived();
            double requiredAmount = order.getFinalAmount();
            
            // If this is an electronic payment, the amountReceived is the final amount after VAT/promotions
            // So we should validate against itself (exact match)
            if (request.getPaymentMethod().equals("CASH")) {
                // For cash, validate against order amount (customer can pay more)
                if (!validator.validateAmount(amountToValidate, requiredAmount, request.getPaymentMethod())) {
                    System.out.println("🔍 ❌ Payment amount validation failed");
                    return PaymentResponse.failure("Insufficient payment amount");
                }
            } else {
                // For electronic payments, amountReceived is already the final amount
                // Just validate it's reasonable (not negative, not zero)
                if (amountToValidate <= 0) {
                    System.out.println("🔍 ❌ Invalid payment amount: " + amountToValidate);
                    return PaymentResponse.failure("Invalid payment amount");
                }
            }
            System.out.println("🔍 ✅ Payment amount validation passed");
            
            // 5. Process payment by method
            System.out.println("🔍 Step 5: Processing payment by method...");
            PaymentResponse response = processPaymentByMethod(order, request);
            
            // 6. Update order if successful
            if (response.isSuccess()) {
                updateOrderAfterPayment(order, request, orderDAO);
                response.setOrder(order);
                response.setReceiptPath(receiptService.generateReceipt(order, request));
            }
            
            return response;
            
        } catch (Exception e) {
            System.err.println("❌ Payment processing error: " + e.getMessage());
            return PaymentResponse.failure("Payment processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Process payment based on method type
     */
    private PaymentResponse processPaymentByMethod(Order order, PaymentRequest request) {
        PaymentMethod method = PaymentMethod.fromString(request.getPaymentMethod());
        
        switch (method) {
            case CASH:
                return processCashPayment(order, request);
            case CARD:
                return processCardPayment(order, request);
            case MOMO:
            case VNPAY:
            case ZALOPAY:
                return processElectronicPayment(order, request);
            case BANK_TRANSFER:
                return processBankTransferPayment(order, request);
            default:
                return PaymentResponse.failure("Unsupported payment method");
        }
    }
    
    /**
     * Process cash payment
     */
    private PaymentResponse processCashPayment(Order order, PaymentRequest request) {
        double change = request.getAmountReceived() - order.getFinalAmount();
        if (change < 0) {
            return PaymentResponse.failure("Insufficient cash amount");
        }
        
        return PaymentResponse.success("Cash payment processed successfully")
                .setChangeAmount(change)
                .setTransactionId("CASH_" + System.currentTimeMillis());
    }
    
    /**
     * Process card payment
     */
    private PaymentResponse processCardPayment(Order order, PaymentRequest request) {
        // For educational project - no transaction code validation needed
        String transactionId = "EDU_CARD_" + System.currentTimeMillis();
        
        // Simulate card processing
        boolean processed = simulateCardProcessing(transactionId, order.getFinalAmount());
        
        if (processed) {
            return PaymentResponse.success("Card payment processed successfully")
                    .setTransactionId(transactionId);
        } else {
            return PaymentResponse.failure("Card payment failed");
        }
    }
    
    /**
     * Process electronic wallet payment
     */
    private PaymentResponse processElectronicPayment(Order order, PaymentRequest request) {
        // For educational project - no transaction code validation needed
        String transactionId = "EDU_" + request.getPaymentMethod() + "_" + System.currentTimeMillis();
        
        // Simulate e-wallet processing
        boolean processed = simulateEWalletProcessing(request.getPaymentMethod(), 
                transactionId, order.getFinalAmount());
        
        if (processed) {
            return PaymentResponse.success("Electronic payment processed successfully")
                    .setTransactionId(transactionId);
        } else {
            return PaymentResponse.failure("Electronic payment failed");
        }
    }
    
    /**
     * Process bank transfer payment
     */
    private PaymentResponse processBankTransferPayment(Order order, PaymentRequest request) {
        // For educational project - no transaction code validation needed
        String transactionId = "EDU_BANK_" + System.currentTimeMillis();
        
        // Simulate bank transfer processing
        boolean processed = simulateBankTransferProcessing(transactionId, order.getFinalAmount());
        
        if (processed) {
            return PaymentResponse.success("Bank transfer processed successfully")
                    .setTransactionId(transactionId);
        } else {
            return PaymentResponse.failure("Bank transfer processing failed");
        }
    }
    
    /**
     * Update order after successful payment
     */
    private void updateOrderAfterPayment(Order order, PaymentRequest request, OrderDAO orderDAO) {
        order.setPaymentMethod(request.getPaymentMethod());
        order.setPaymentStatus("paid");
        order.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        
        if (request.getNotes() != null && !request.getNotes().trim().isEmpty()) {
            String existingNotes = order.getNotes() != null ? order.getNotes() : "";
            order.setNotes(existingNotes + "\nPayment: " + request.getNotes());
        }
        
        orderDAO.update(order);
    }
    
    // Simulation methods (replace with real integrations in production)
    private boolean simulateCardProcessing(String transactionCode, double amount) {
        return transactionCode.length() >= 6; // Simple simulation
    }
    
    private boolean simulateEWalletProcessing(String method, String transactionCode, double amount) {
        return transactionCode.length() >= 8; // Simple simulation
    }
    
    private boolean simulateBankTransferVerification(String transactionCode, double amount) {
        return transactionCode.length() >= 10; // Simple simulation
    }
    
    private boolean simulateBankTransferProcessing(String transactionId, double amount) {
        return transactionId.length() >= 8; // Simple simulation for educational project
    }

    // =====================================================
    // CORE PAYMENT PROCESSING (Legacy Methods)
    // =====================================================

    /**
     * Legacy method for backward compatibility
     */
    public boolean processPayment(Order order, String paymentMethod, double amountReceived) {
        System.out.println("🔍 PaymentService.processPayment() called");
        System.out.println("🔍 Order ID: " + order.getOrderId());
        System.out.println("🔍 Payment Method: " + paymentMethod);
        System.out.println("🔍 Amount Received: " + amountReceived);
        
        PaymentRequest request = new PaymentRequest()
                .setOrderId(order.getOrderId())
                .setPaymentMethod(paymentMethod)
                .setAmountReceived(amountReceived)
                .setTransactionCode(null); // For educational project
        
        System.out.println("🔍 PaymentRequest created, calling processPayment(request)...");
        PaymentResponse response = processPayment(request);
        System.out.println("🔍 PaymentResponse received: " + response.isSuccess());
        if (!response.isSuccess()) {
            System.out.println("🔍 Payment failed: " + response.getMessage());
        }
        return response.isSuccess();
    }
    
    /**
     * Legacy method - Process payment for an order (Original implementation)
     * @param order Order to process payment for
     * @param paymentMethod Payment method (cash, card, momo, vnpay, zalopay, bank_transfer)
     * @param amountReceived Amount received from customer
     * @return true if payment successful
     */
    public boolean processPaymentLegacy(Order order, String paymentMethod, double amountReceived) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            // Validate payment
            if (!validatePayment(order, paymentMethod, amountReceived)) {
                return false;
            }

            // Update order with payment information
            String updateOrderSQL = """
                UPDATE orders 
                SET payment_method = ?, 
                    payment_status = 'paid',
                    order_status = 'completed',
                    updated_at = CURRENT_TIMESTAMP 
                WHERE order_id = ?
            """;

            try (PreparedStatement stmt = connection.prepareStatement(updateOrderSQL)) {
                stmt.setString(1, paymentMethod);
                stmt.setInt(2, order.getOrderId());
                
                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    // Update local order object
                    order.setPaymentMethod(paymentMethod);
                    order.setPaymentStatus("paid");
                    order.setOrderStatus("completed");
                    
                    // Log payment transaction
                    logPaymentTransaction(order, paymentMethod, amountReceived);
                    
                    System.out.println("✅ Payment processed successfully for order: " + order.getOrderNumber());
                    return true;
                } else {
                    System.err.println("❌ Failed to update order payment status");
                    return false;
                }
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error processing payment: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Calculate change amount
     * @param order Order
     * @param amountReceived Amount received from customer
     * @return Change amount (0 if electronic payment or exact amount)
     */
    public double calculateChange(Order order, double amountReceived) {
        if (order == null) return 0;
        
        double change = amountReceived - order.getFinalAmount();
        return Math.max(0, change); // Never negative change
    }

    /**
     * Validate payment before processing
     * @param order Order to validate
     * @param paymentMethod Payment method
     * @param amountReceived Amount received
     * @return true if payment is valid
     */
    private boolean validatePayment(Order order, String paymentMethod, double amountReceived) {
        if (order == null) {
            System.err.println("❌ Order is null");
            return false;
        }

        if (order.getFinalAmount() <= 0) {
            System.err.println("❌ Invalid order amount: " + order.getFinalAmount());
            return false;
        }

        PaymentMethod method = PaymentMethod.fromString(paymentMethod);
        if (method == null) {
            System.err.println("❌ Invalid payment method: " + paymentMethod);
            return false;
        }

        // For cash payments, amount received must be >= final amount
        if (method == PaymentMethod.CASH && amountReceived < order.getFinalAmount()) {
            System.err.println("❌ Insufficient cash amount: " + amountReceived + " < " + order.getFinalAmount());
            return false;
        }

        // For electronic payments, amount should match exactly (usually)
        if (method.isElectronic() && Math.abs(amountReceived - order.getFinalAmount()) > 0.01) {
            System.out.println("⚠️ Electronic payment amount mismatch, proceeding with order amount");
        }

        return true;
    }

    /**
     * Log payment transaction for audit trail
     */
    private void logPaymentTransaction(Order order, String paymentMethod, double amountReceived) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            String logSQL = """
                INSERT INTO payment_transactions 
                (order_id, payment_method, amount_received, final_amount, change_amount, transaction_date, status)
                VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 'completed')
            """;
            
            try (PreparedStatement stmt = connection.prepareStatement(logSQL)) {
                double changeAmount = calculateChange(order, amountReceived);
                
                stmt.setInt(1, order.getOrderId());
                stmt.setString(2, paymentMethod);
                stmt.setDouble(3, amountReceived);
                stmt.setDouble(4, order.getFinalAmount());
                stmt.setDouble(5, changeAmount);
                
                stmt.executeUpdate();
                System.out.println("💾 Payment transaction logged for order: " + order.getOrderNumber());
            }
        } catch (Exception e) {
            // Payment transaction logging failure shouldn't fail the main payment
            System.err.println("⚠️ Failed to log payment transaction: " + e.getMessage());
        }
    }

    // =====================================================
    // PAYMENT METHODS SUPPORT
    // =====================================================

    /**
     * Get available payment methods with their display information
     * @return Map of payment methods with display data
     */
    public Map<String, Map<String, Object>> getAvailablePaymentMethods() {
        Map<String, Map<String, Object>> methods = new HashMap<>();
        
        for (PaymentMethod method : PaymentMethod.values()) {
            Map<String, Object> methodInfo = new HashMap<>();
            methodInfo.put("displayName", method.getDisplayName());
            methodInfo.put("isElectronic", method.isElectronic());
            methodInfo.put("requiresOnlineVerification", method.requiresOnlineVerification());
            methodInfo.put("icon", getPaymentMethodIcon(method));
            methodInfo.put("description", getPaymentMethodDescription(method));
            
            methods.put(method.getValue(), methodInfo);
        }
        
        return methods;
    }

    /**
     * Get icon for payment method
     */
    private String getPaymentMethodIcon(PaymentMethod method) {
        switch (method) {
            case CASH: return "💵";
            case CARD: return "💳";
            case MOMO: return "📱";
            case VNPAY: return "🏛️";
            case ZALOPAY: return "💙";
            case BANK_TRANSFER: return "🏦";
            default: return "💰";
        }
    }

    /**
     * Get description for payment method
     */
    private String getPaymentMethodDescription(PaymentMethod method) {
        switch (method) {
            case CASH: return "Thanh toán bằng tiền mặt";
            case CARD: return "Thanh toán bằng thẻ tín dụng/ghi nợ";
            case MOMO: return "Thanh toán qua ví điện tử MoMo";
            case VNPAY: return "Thanh toán qua cổng VNPay";
            case ZALOPAY: return "Thanh toán qua ví ZaloPay";
            case BANK_TRANSFER: return "Chuyển khoản ngân hàng";
            default: return "Phương thức thanh toán";
        }
    }

    // =====================================================
    // ELECTRONIC PAYMENT PROCESSING
    // =====================================================

    /**
     * Process electronic payment (MoMo, VNPay, ZaloPay)
     * This is a simulation - in real implementation, integrate with actual payment gateways
     */
    public boolean processElectronicPayment(Order order, PaymentMethod method, double amount) {
        System.out.println("🔄 Processing " + method.getDisplayName() + " payment for: " + amount);
        
        try {
            // Simulate payment gateway call
            Thread.sleep(1000); // Simulate network delay
            
            // Simulate success/failure (90% success rate)
            boolean success = Math.random() > 0.1;
            
            if (success) {
                System.out.println("✅ " + method.getDisplayName() + " payment successful");
                return processPayment(order, method.getValue(), amount);
            } else {
                System.err.println("❌ " + method.getDisplayName() + " payment failed");
                return false;
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error processing electronic payment: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // RECEIPT & REPORTING
    // =====================================================

    /**
     * Generate receipt data for printing
     * @param order Order to generate receipt for
     * @return Receipt data as formatted string
     */
    public String generateReceiptData(Order order) {
        if (order == null) return "";
        
        StringBuilder receipt = new StringBuilder();
        
        // Header
        receipt.append("=====================================\n");
        receipt.append("           CAFE PROJECT2\n");
        receipt.append("      Hệ thống quản lý quán cà phê\n");
        receipt.append("=====================================\n\n");
        
        // Order info
        receipt.append("Mã đơn hàng: ").append(order.getOrderNumber()).append("\n");
        receipt.append("Bàn số: ").append(order.getTableId()).append("\n");
        receipt.append("Thời gian: ").append(order.getOrderDate()).append("\n");
        receipt.append("Nhân viên: User ").append(order.getUserId()).append("\n\n");
        
        receipt.append("-------------------------------------\n");
        receipt.append("Chi tiết đơn hàng:\n");
        receipt.append("-------------------------------------\n");
        
        // Order details would be loaded separately - placeholder for now
        receipt.append("(Chi tiết sản phẩm sẽ được load riêng)\n");
        
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("Tạm tính:%20.0f đ\n", order.getTotalAmount()));
        receipt.append(String.format("Giảm giá:%20.0f đ\n", order.getDiscountAmount()));
        receipt.append(String.format("TỔNG TIỀN:%18.0f đ\n", order.getFinalAmount()));
        receipt.append("-------------------------------------\n\n");
        
        // Payment info
        receipt.append("Thanh toán: ").append(getPaymentMethodName(order.getPaymentMethod())).append("\n");
        receipt.append("Trạng thái: ").append(getPaymentStatusName(order.getPaymentStatus())).append("\n\n");
        
        // Footer
        receipt.append("=====================================\n");
        receipt.append("     Cảm ơn quý khách đã sử dụng!\n");
        receipt.append("       Hẹn gặp lại quý khách!\n");
        receipt.append("=====================================\n");
        
        return receipt.toString();
    }

    /**
     * Get display name for payment method
     */
    private String getPaymentMethodName(String method) {
        PaymentMethod paymentMethod = PaymentMethod.fromString(method);
        return paymentMethod.getDisplayName();
    }

    /**
     * Get display name for payment status
     */
    private String getPaymentStatusName(String status) {
        PaymentStatus paymentStatus = PaymentStatus.fromString(status);
        return paymentStatus.getDisplayName();
    }

    // =====================================================
    // PAYMENT STATISTICS & REPORTING
    // =====================================================

    /**
     * Get payment statistics for reporting
     * @param startDate Start date for statistics
     * @param endDate End date for statistics
     * @return Payment statistics
     */
    public Map<String, Object> getPaymentStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        Map<String, Object> stats = new HashMap<>();
        
        try (Connection connection = DatabaseConfig.getConnection()) {
            // Total payments
            String totalSQL = """
                SELECT 
                    COUNT(*) as total_transactions,
                    SUM(final_amount) as total_amount,
                    AVG(final_amount) as average_amount
                FROM orders 
                WHERE payment_status = 'paid' 
                AND order_date BETWEEN ? AND ?
            """;
            
            try (PreparedStatement stmt = connection.prepareStatement(totalSQL)) {
                stmt.setTimestamp(1, Timestamp.valueOf(startDate));
                stmt.setTimestamp(2, Timestamp.valueOf(endDate));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.put("totalTransactions", rs.getInt("total_transactions"));
                        stats.put("totalAmount", rs.getDouble("total_amount"));
                        stats.put("averageAmount", rs.getDouble("average_amount"));
                    }
                }
            }
            
            // Payment methods breakdown
            String methodSQL = """
                SELECT 
                    payment_method,
                    COUNT(*) as count,
                    SUM(final_amount) as amount
                FROM orders 
                WHERE payment_status = 'paid' 
                AND order_date BETWEEN ? AND ?
                GROUP BY payment_method
            """;
            
            Map<String, Map<String, Object>> methodStats = new HashMap<>();
            try (PreparedStatement stmt = connection.prepareStatement(methodSQL)) {
                stmt.setTimestamp(1, Timestamp.valueOf(startDate));
                stmt.setTimestamp(2, Timestamp.valueOf(endDate));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String method = rs.getString("payment_method");
                        Map<String, Object> methodData = new HashMap<>();
                        methodData.put("count", rs.getInt("count"));
                        methodData.put("amount", rs.getDouble("amount"));
                        methodStats.put(method, methodData);
                    }
                }
            }
            stats.put("paymentMethods", methodStats);
            
        } catch (Exception e) {
            System.err.println("❌ Error getting payment statistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        return stats;
    }
}
