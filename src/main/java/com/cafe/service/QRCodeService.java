package com.cafe.service;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for generating QR codes for various payment methods
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class QRCodeService {

    // =====================================================
    // QR CODE GENERATION
    // =====================================================

    /**
     * Generate QR code for payment
     * @param paymentMethod Payment method (momo, vnpay, zalopay, bank_transfer)
     * @param amount Payment amount
     * @param orderNumber Order number
     * @return QR code image
     */
    public Image generatePaymentQRCode(String paymentMethod, double amount, String orderNumber) {
        String qrContent = generateQRContent(paymentMethod, amount, orderNumber);
        return generateQRCodeImage(qrContent);
    }

    /**
     * Generate QR content string based on payment method
     */
    private String generateQRContent(String paymentMethod, double amount, String orderNumber) {
        switch (paymentMethod.toLowerCase()) {
            case "momo":
                return generateMoMoQRContent(amount, orderNumber);
            case "vnpay":
                return generateVNPayQRContent(amount, orderNumber);
            case "zalopay":
                return generateZaloPayQRContent(amount, orderNumber);
            case "bank_transfer":
                return generateBankTransferQRContent(amount, orderNumber);
            default:
                return generateGenericQRContent(paymentMethod, amount, orderNumber);
        }
    }

    /**
     * Generate MoMo QR content
     */
    private String generateMoMoQRContent(double amount, String orderNumber) {
        // MoMo QR format: 2|99|phone|orderNumber|description|0|0|amount
        return String.format("2|99|%s|%s|%s|0|0|%.0f", 
            "0123456789", // Merchant phone (should be from config)
            orderNumber, 
            "Thanh toán đơn hàng " + orderNumber, 
            amount);
    }

    /**
     * Generate VNPay QR content
     */
    private String generateVNPayQRContent(double amount, String orderNumber) {
        // VNPay QR format
        return String.format("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html?vnp_Amount=%.0f&vnp_TxnRef=%s&vnp_OrderInfo=%s", 
            amount * 100, // VNPay uses amount in cents
            orderNumber, 
            "Thanh toán cafe " + orderNumber);
    }

    /**
     * Generate ZaloPay QR content
     */
    private String generateZaloPayQRContent(double amount, String orderNumber) {
        // ZaloPay QR format
        return String.format("zalopay://pay?amount=%.0f&order=%s&description=%s&merchant=%s", 
            amount, 
            orderNumber, 
            "Thanh toán quán café", 
            "CAFE001");
    }

    /**
     * Generate Bank Transfer QR content
     */
    private String generateBankTransferQRContent(double amount, String orderNumber) {
        // Bank transfer format (VietQR standard)
        return String.format("00020101021238570010A00000072701270006970454011234567890208QRIBFTTA53037045405%.0f5802VN6304%s", 
            amount, 
            generateChecksum(orderNumber));
    }

    /**
     * Generate generic QR content
     */
    private String generateGenericQRContent(String paymentMethod, double amount, String orderNumber) {
        return String.format("Payment: %s\nAmount: %.0f VND\nOrder: %s\nMerchant: Cafe Management System", 
            paymentMethod, amount, orderNumber);
    }

    /**
     * Generate simple checksum for QR content
     */
    private String generateChecksum(String data) {
        int checksum = 0;
        for (char c : data.toCharArray()) {
            checksum += c;
        }
        return String.format("%04d", checksum % 10000);
    }

    // =====================================================
    // QR CODE IMAGE GENERATION
    // =====================================================

    /**
     * Generate QR code image from content string
     * This is a simplified implementation - in production, use ZXing library
     */
    public Image generateQRCodeImage(String content) {
        try {
            // In a real implementation, you would use:
            // com.google.zxing.qrcode.QRCodeWriter
            // For now, we'll create a simple pattern-based image
            
            return createSimpleQRPattern(content);
            
        } catch (Exception e) {
            System.err.println("❌ Error generating QR code: " + e.getMessage());
            return createPlaceholderImage();
        }
    }

    /**
     * Create a simple QR-like pattern for demonstration
     * In production, replace with actual QR code library
     */
    private Image createSimpleQRPattern(String content) {
        int size = 200;
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();
        
        // Create a simple pattern based on content hash
        int hash = Math.abs(content.hashCode());
        
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                // Create a pattern based on position and content hash
                boolean isBlack = ((x + y + hash) % 3 == 0) || 
                                 ((x * y + hash) % 7 == 0) ||
                                 (x % 20 < 3 && y % 20 < 3) || // Corner squares
                                 (x % 20 > 16 && y % 20 < 3) || 
                                 (x % 20 < 3 && y % 20 > 16);
                
                writer.setColor(x, y, isBlack ? Color.BLACK : Color.WHITE);
            }
        }
        
        return image;
    }

    /**
     * Create placeholder image when QR generation fails
     */
    private Image createPlaceholderImage() {
        int size = 200;
        WritableImage image = new WritableImage(size, size);
        PixelWriter writer = image.getPixelWriter();
        
        // Create a simple "QR" text pattern
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                // Border
                if (x < 5 || x > size - 5 || y < 5 || y > size - 5) {
                    writer.setColor(x, y, Color.BLACK);
                } else {
                    writer.setColor(x, y, Color.WHITE);
                }
            }
        }
        
        return image;
    }

    // =====================================================
    // PAYMENT METHOD HELPERS
    // =====================================================

    /**
     * Get instruction text for payment method
     */
    public String getPaymentInstructions(String paymentMethod) {
        Map<String, String> instructions = new HashMap<>();
        instructions.put("momo", "1. Mở ứng dụng MoMo\n2. Chọn 'Quét mã QR'\n3. Quét mã để thanh toán\n4. Xác nhận giao dịch");
        instructions.put("vnpay", "1. Mở ứng dụng ngân hàng\n2. Chọn 'Quét QR Pay'\n3. Quét mã QR\n4. Nhập mã PIN để xác nhận");
        instructions.put("zalopay", "1. Mở ứng dụng ZaloPay\n2. Chọn 'Quét mã'\n3. Quét mã QR\n4. Xác nhận thanh toán");
        instructions.put("bank_transfer", "1. Mở ứng dụng ngân hàng\n2. Chọn 'Chuyển khoản QR'\n3. Quét mã hoặc nhập thông tin\n4. Xác nhận chuyển khoản");
        
        return instructions.getOrDefault(paymentMethod.toLowerCase(), "Quét mã QR để thanh toán");
    }

    /**
     * Get display name for payment method
     */
    public String getPaymentMethodDisplayName(String paymentMethod) {
        Map<String, String> displayNames = new HashMap<>();
        displayNames.put("momo", "Ví MoMo");
        displayNames.put("vnpay", "VNPay");
        displayNames.put("zalopay", "ZaloPay");
        displayNames.put("bank_transfer", "Chuyển khoản ngân hàng");
        
        return displayNames.getOrDefault(paymentMethod.toLowerCase(), paymentMethod);
    }

    /**
     * Check if payment method supports QR code
     */
    public boolean supportsQRCode(String paymentMethod) {
        return !paymentMethod.equalsIgnoreCase("cash") && 
               !paymentMethod.equalsIgnoreCase("card");
    }

    // =====================================================
    // DEMO/TESTING METHODS
    // =====================================================

    /**
     * Generate sample QR codes for testing
     */
    public void generateSampleQRCodes() {
        String[] methods = {"momo", "vnpay", "zalopay", "bank_transfer"};
        double amount = 150000;
        String orderNumber = "ORD-TEST-001";
        
        System.out.println("🔧 Generating sample QR codes...");
        
        for (String method : methods) {
            try {
                generatePaymentQRCode(method, amount, orderNumber);
                String instructions = getPaymentInstructions(method);
                
                System.out.println("✅ Generated QR for " + getPaymentMethodDisplayName(method));
                System.out.println("📱 Instructions: " + instructions.replaceAll("\n", " | "));
                
            } catch (Exception e) {
                System.err.println("❌ Failed to generate QR for " + method + ": " + e.getMessage());
            }
        }
        
        System.out.println("✅ Sample QR code generation completed");
    }
}
