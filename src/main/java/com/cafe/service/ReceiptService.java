package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.OrderDetailDAO;
import com.cafe.dao.base.OrderDetailDAOImpl;
import com.cafe.model.entity.Order;
import com.cafe.model.entity.OrderDetail;
import com.cafe.model.dto.PaymentRequest;
import com.cafe.util.PriceFormatter;
import com.cafe.util.PDFExporter;
import com.cafe.util.SessionManager;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Receipt Service - Complete Receipt Generation System
 * Xử lý tạo và in hóa đơn cho đơn hàng đã thanh toán
 * 
 * @author Team 2_C2406L
 * @version 2.0.0 (Enhanced Receipt System)
 */
public class ReceiptService {
    
    private final String RECEIPT_DIR = System.getProperty("user.home") + "/Downloads/receipts/";

    private static final String RECEIPT_HEADER = "CAFE PROJECT2";
    private static final String RECEIPT_SUBTITLE = "Hệ thống quản lý quán cà phê";
    private static final String RECEIPT_FOOTER = "Cảm ơn quý khách đã sử dụng dịch vụ!";
    private static final String RECEIPT_GOODBYE = "Hẹn gặp lại quý khách!";
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    public ReceiptService() {
        createReceiptDirectory();
    }
    
    /**
     * Generate receipt and return file path (Enhanced version)
     */
    public String generateReceipt(Order order, PaymentRequest payment) {
        try (Connection connection = DatabaseConfig.getConnection()) {
            OrderDetailDAO orderDetailDAO = new OrderDetailDAOImpl(connection);
            List<OrderDetail> details = orderDetailDAO.findByOrderId(order.getOrderId());
            
            String fileName = String.format("receipt_%s_%s.txt", 
                    order.getOrderNumber(), 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")));
            
            String filePath = RECEIPT_DIR + fileName;
            
            generateTextReceipt(order, details, payment, filePath);
            
            System.out.println("✅ Receipt generated: " + filePath);
            return filePath;
            
        } catch (Exception e) {
            System.err.println("❌ Receipt generation failed: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Generate text format receipt (Enhanced version)
     */
    private void generateTextReceipt(Order order, List<OrderDetail> details, PaymentRequest payment, String filePath) 
            throws IOException {
        
        try (FileWriter writer = new FileWriter(filePath)) {
            // Header
            writer.write("=====================================\n");
            writer.write("          CAFE PROJECT2\n");
            writer.write("      123 Đường ABC, Quận XYZ\n");
            writer.write("         Tel: 0123456789\n");
            writer.write("=====================================\n\n");
            
            // Order info
            writer.write("Hóa đơn: " + order.getOrderNumber() + "\n");
            writer.write("Bàn: " + order.getTableId() + "\n");
            writer.write("Ngày: " + order.getOrderDate().toString() + "\n");
            // Get cashier info from session
            String cashierName = getCashierNameFromSession();
            writer.write("Thu ngân: " + cashierName + "\n");
            writer.write("-------------------------------------\n\n");
            
            // Items
            writer.write("STT  Tên món                   SL    Đơn giá      Thành tiền\n");
            writer.write("-------------------------------------\n");
            
            int stt = 1;
            for (OrderDetail detail : details) {
                writer.write(String.format("%-3d  %-20s  %3d  %10s  %12s\n",
                        stt++,
                        detail.getProductName() != null ? detail.getProductName() : "Sản phẩm " + detail.getProductId(),
                        detail.getQuantity(),
                        PriceFormatter.formatVND(detail.getUnitPrice()),
                        PriceFormatter.formatVND(detail.getTotalPrice())
                ));
            }
            
            writer.write("-------------------------------------\n");
            
            // Totals
            writer.write(String.format("Tạm tính:               %15s\n", PriceFormatter.formatVND(order.getTotalAmount())));
            writer.write(String.format("VAT (8%%):               %15s\n", PriceFormatter.formatVND(order.getTotalAmount() * 0.08)));
            if (order.getDiscountAmount() > 0) {
                writer.write(String.format("Giảm giá:              -%15s\n", PriceFormatter.formatVND(order.getDiscountAmount())));
            }
            writer.write("-------------------------------------\n");
            writer.write(String.format("TỔNG CỘNG:              %15s\n", PriceFormatter.formatVND(order.getFinalAmount())));
            
            // Payment info
            writer.write("\n");
            writer.write("Phương thức: " + getPaymentMethodDisplay(payment.getPaymentMethod()) + "\n");
            
            if ("cash".equals(payment.getPaymentMethod())) {
                writer.write(String.format("Tiền nhận:              %15s\n", PriceFormatter.formatVND(payment.getAmountReceived())));
                double change = payment.getAmountReceived() - order.getFinalAmount();
                if (change > 0) {
                    writer.write(String.format("Tiền thối:              %15s\n", PriceFormatter.formatVND(change)));
                }
            } else if (payment.getTransactionCode() != null) {
                writer.write("Mã GD: " + payment.getTransactionCode() + "\n");
            }
            
            // Footer
            writer.write("\n=====================================\n");
            writer.write("     Cảm ơn quý khách đã sử dụng\n");
            writer.write("         dịch vụ của chúng tôi!\n");
            writer.write("=====================================\n");
        }
    }
    
    private String getPaymentMethodDisplay(String method) {
        switch (method.toLowerCase()) {
            case "cash": return "Tiền mặt";
            case "card": return "Thẻ tín dụng/ghi nợ";
            case "momo": return "Ví MoMo";
            case "vnpay": return "VNPay";
            case "zalopay": return "ZaloPay";
            case "bank_transfer": return "Chuyển khoản";
            default: return method;
        }
    }
    
    private void createReceiptDirectory() {
        File dir = new File(RECEIPT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Generate receipt content as formatted string for printing
     * @param order Paid order
     * @param orderDetails Order details
     * @return Formatted receipt string
     */
    public String generateReceiptContent(Order order, List<OrderDetail> orderDetails) {
        if (order == null) {
            throw new IllegalArgumentException("Order cannot be null");
        }
        
        StringBuilder receipt = new StringBuilder();
        
        // Header
        appendReceiptHeader(receipt);
        
        // Order information
        appendOrderInfo(receipt, order);
        
        // Order details
        appendOrderDetails(receipt, orderDetails);
        
        // Totals
        appendTotals(receipt, order);
        
        // Payment information
        appendPaymentInfo(receipt, order);
        
        // Footer
        appendReceiptFooter(receipt);
        
        return receipt.toString();
    }

    /**
     * Generate receipt header
     */
    private void appendReceiptHeader(StringBuilder receipt) {
        receipt.append("=====================================\n");
        receipt.append("           ").append(RECEIPT_HEADER).append("\n");
        receipt.append("      ").append(RECEIPT_SUBTITLE).append("\n");
        receipt.append("=====================================\n\n");
    }

    /**
     * Generate order information section
     */
    private void appendOrderInfo(StringBuilder receipt, Order order) {
        receipt.append("Mã đơn hàng: ").append(order.getOrderNumber()).append("\n");
        receipt.append("Bàn số: ").append(order.getTableId()).append("\n");
        
        if (order.getOrderDate() != null) {
            String formattedDate = order.getOrderDate().toLocalDateTime().format(DATE_FORMAT);
            receipt.append("Thời gian: ").append(formattedDate).append("\n");
        }
        
        receipt.append("Nhân viên: User ").append(order.getUserId()).append("\n");
        
        if (order.getCustomerId() != null && order.getCustomerId() > 0) {
            receipt.append("Khách hàng: KH").append(order.getCustomerId()).append("\n");
        }
        
        receipt.append("\n");
    }

    /**
     * Generate order details section
     */
    private void appendOrderDetails(StringBuilder receipt, List<OrderDetail> orderDetails) {
        receipt.append("-------------------------------------\n");
        receipt.append("Chi tiết đơn hàng:\n");
        receipt.append("-------------------------------------\n");
        
        if (orderDetails == null || orderDetails.isEmpty()) {
            receipt.append("(Không có chi tiết sản phẩm)\n");
        } else {
            for (OrderDetail detail : orderDetails) {
                String productName = detail.getProductName() != null ? 
                    detail.getProductName() : "Sản phẩm " + detail.getProductId();
                
                // Format: Product Name x Quantity
                receipt.append(String.format("%-20s x%-3d %10s\n", 
                    truncateString(productName, 20),
                    detail.getQuantity(),
                    PriceFormatter.formatNumber(detail.getTotalPrice())
                ));
                
                // Unit price on separate line if different from total
                if (detail.getQuantity() > 1) {
                    receipt.append(String.format("  @%s/món\n", 
                        PriceFormatter.formatNumber(detail.getUnitPrice())
                    ));
                }
            }
        }
    }

    /**
     * Generate totals section
     */
    private void appendTotals(StringBuilder receipt, Order order) {
        receipt.append("-------------------------------------\n");
        receipt.append(String.format("Tạm tính:%20s\n", 
            PriceFormatter.formatNumber(order.getTotalAmount())));
        
        if (order.getDiscountAmount() > 0) {
            receipt.append(String.format("Giảm giá:%20s\n", 
                PriceFormatter.formatNumber(order.getDiscountAmount())));
        }
        
        receipt.append("=====================================\n");
        receipt.append(String.format("TỔNG TIỀN:%18s đ\n", 
            PriceFormatter.formatNumber(order.getFinalAmount())));
        receipt.append("=====================================\n\n");
    }

    /**
     * Generate payment information section
     */
    private void appendPaymentInfo(StringBuilder receipt, Order order) {
        String paymentMethodName = getPaymentMethodDisplayName(order.getPaymentMethod());
        receipt.append("Thanh toán: ").append(paymentMethodName).append("\n");
        
        String paymentStatusName = getPaymentStatusDisplayName(order.getPaymentStatus());
        receipt.append("Trạng thái: ").append(paymentStatusName).append("\n\n");
        
        // Show timestamp for payment
        receipt.append("Thanh toán lúc: ").append(LocalDateTime.now().format(DATE_FORMAT)).append("\n\n");
    }

    /**
     * Generate receipt footer
     */
    private void appendReceiptFooter(StringBuilder receipt) {
        receipt.append("=====================================\n");
        receipt.append("     ").append(RECEIPT_FOOTER).append("\n");
        receipt.append("       ").append(RECEIPT_GOODBYE).append("\n");
        receipt.append("=====================================\n");
        receipt.append("\n");
        receipt.append("* Hóa đơn được tạo tự động bởi hệ thống *\n");
        receipt.append("* Vui lòng giữ hóa đơn để đối chiếu    *\n");
    }

    /**
     * Generate receipt for PDF export
     * @param order Paid order
     * @param orderDetails Order details
     * @return PDF file path
     */
    public String generateReceiptPDF(Order order, List<OrderDetail> orderDetails) {
        try {
            String receiptContent = generateReceiptContent(order, orderDetails);
            String fileName = "receipt_" + order.getOrderNumber() + "_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            
            // Use PDFExporter to create PDF
            String filePath = "receipts/" + fileName;
            PDFExporter.exportReceiptToPDF(receiptContent, filePath);
            
            return filePath;
        } catch (Exception e) {
            System.err.println("❌ Error generating receipt PDF: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Generate simple receipt for thermal printer (58mm width)
     * @param order Paid order
     * @param orderDetails Order details
     * @return Thermal printer formatted string
     */
    public String generateThermalReceipt(Order order, List<OrderDetail> orderDetails) {
        StringBuilder receipt = new StringBuilder();
        
        // Thermal printer header (32 chars width)
        receipt.append("================================\n");
        receipt.append("        CAFE PROJECT2\n");
        receipt.append("   He thong quan ly quan ca phe\n");
        receipt.append("================================\n\n");
        
        // Order info (shortened for thermal)
        receipt.append("Order: ").append(order.getOrderNumber()).append("\n");
        receipt.append("Ban: ").append(order.getTableId()).append("\n");
        receipt.append("Time: ").append(LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("dd/MM HH:mm"))).append("\n\n");
        
        // Items (compact format)
        receipt.append("--------------------------------\n");
        if (orderDetails != null && !orderDetails.isEmpty()) {
            for (OrderDetail detail : orderDetails) {
                String productName = detail.getProductName() != null ? 
                    detail.getProductName() : "SP" + detail.getProductId();
                
                receipt.append(String.format("%-15s x%d %8s\n", 
                    truncateString(productName, 15),
                    detail.getQuantity(),
                    PriceFormatter.formatNumber(detail.getTotalPrice())
                ));
            }
        }
        
        // Totals (compact)
        receipt.append("--------------------------------\n");
        receipt.append(String.format("TONG: %22s d\n", 
            PriceFormatter.formatNumber(order.getFinalAmount())));
        receipt.append("================================\n");
        receipt.append("TT: ").append(getPaymentMethodDisplayName(order.getPaymentMethod())).append("\n");
        receipt.append("================================\n");
        receipt.append("    Cam on quy khach!\n");
        receipt.append("   Hen gap lai quy khach!\n");
        receipt.append("================================\n\n\n");
        
        return receipt.toString();
    }

    /**
     * Print receipt to default printer (simulation)
     * In real implementation, this would send to actual printer
     */
    public boolean printReceipt(Order order, List<OrderDetail> orderDetails) {
        try {
            String receiptContent = generateThermalReceipt(order, orderDetails);
            
            // Simulate printing (in real implementation, send to printer)
            System.out.println("🖨️ Printing receipt for order: " + order.getOrderNumber());
            System.out.println("Receipt content:");
            System.out.println(receiptContent);
            
            // In real implementation:
            // PrintService printService = PrintServiceLookup.lookupDefaultPrintService();
            // Send receiptContent to printer...
            
            return true;
        } catch (Exception e) {
            System.err.println("❌ Error printing receipt: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // UTILITY METHODS
    // =====================================================

    /**
     * Truncate string to fit receipt width
     */
    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    /**
     * Get display name for payment method
     */
    private String getPaymentMethodDisplayName(String method) {
        if (method == null) return "Không xác định";
        
        switch (method.toLowerCase()) {
            case "cash": return "Tiền mặt";
            case "card": return "Thẻ tín dụng/ghi nợ";
            case "momo": return "Ví MoMo";
            case "vnpay": return "VNPay";
            case "zalopay": return "ZaloPay";
            case "bank_transfer": return "Chuyển khoản";
            default: return "Khác";
        }
    }

    /**
     * Get display name for payment status
     */
    private String getPaymentStatusDisplayName(String status) {
        if (status == null) return "Không xác định";
        
        switch (status.toLowerCase()) {
            case "pending": return "Chờ thanh toán";
            case "paid": return "Đã thanh toán";
            case "cancelled": return "Đã hủy";
            default: return "Không xác định";
        }
    }
    
    /**
     * Get cashier name from session
     */
    private String getCashierNameFromSession() {
        try {
            if (SessionManager.isLoggedIn()) {
                String fullName = SessionManager.getCurrentUserFullName();
                if (fullName != null && !fullName.trim().isEmpty()) {
                    return fullName;
                }
                
                // Fallback to username if full name is not available
                String username = SessionManager.getCurrentUsername();
                if (username != null && !username.trim().isEmpty()) {
                    return username;
                }
            }
            
            // Default fallback
            return "Nhân viên";
            
        } catch (Exception e) {
            System.err.println("❌ Error getting cashier name from session: " + e.getMessage());
            return "Nhân viên";
        }
    }
}
