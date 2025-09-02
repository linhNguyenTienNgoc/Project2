package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.PromotionDAO;
import com.cafe.dao.base.PromotionDAOImpl;
import com.cafe.model.entity.Promotion;
import com.cafe.model.entity.Order;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service class for managing promotions and discounts
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PromotionService {

    // =====================================================
    // CORE PROMOTION METHODS
    // =====================================================

    /**
     * Get all promotions from database
     * @return List of all promotions
     */
    public List<Promotion> getAllPromotions() {
        List<Promotion> allPromotions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            allPromotions = promotionDAO.getAllPromotions();
            
            System.out.println("✅ Retrieved " + allPromotions.size() + " promotions");
            
        } catch (Exception e) {
            System.err.println("❌ Error getting all promotions: " + e.getMessage());
            // Return sample data if database fails
            return getSamplePromotions();
        }
        
        return allPromotions;
    }

    /**
     * Get all active promotions that can be applied now
     * @return List of active promotions
     */
    public List<Promotion> getActivePromotions() {
        List<Promotion> activePromotions = new ArrayList<>();
        
        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            
            // Get all promotions from database
            List<Promotion> allPromotions = promotionDAO.getAllPromotions();
            
            // Filter active promotions
            for (Promotion promotion : allPromotions) {
                if (promotion.isValidPromotion() && promotion.isInValidPeriod()) {
                    activePromotions.add(promotion);
                }
            }
            
            System.out.println("✅ Retrieved " + activePromotions.size() + " active promotions");
            
        } catch (Exception e) {
            System.err.println("❌ Error getting active promotions: " + e.getMessage());
            // Return sample data if database fails
            return getSamplePromotions();
        }
        
        return activePromotions;
    }

    /**
     * Get promotions applicable to a specific order amount
     * @param orderAmount Order total amount
     * @return List of applicable promotions
     */
    public List<Promotion> getApplicablePromotions(double orderAmount) {
        List<Promotion> applicablePromotions = new ArrayList<>();
        List<Promotion> activePromotions = getActivePromotions();
        
        for (Promotion promotion : activePromotions) {
            if (promotion.canApplyToOrder(orderAmount)) {
                applicablePromotions.add(promotion);
            }
        }
        
        System.out.println("✅ Found " + applicablePromotions.size() + 
            " promotions applicable to order amount: " + orderAmount);
        
        return applicablePromotions;
    }

    /**
     * Apply promotion to an order
     * @param order Order to apply promotion to
     * @param promotion Promotion to apply
     * @return Applied discount amount
     */
    public double applyPromotionToOrder(Order order, Promotion promotion) {
        if (order == null || promotion == null) {
            System.err.println("❌ Cannot apply promotion: order or promotion is null");
            return 0;
        }

        double orderAmount = order.getTotalAmount();
        
        if (!promotion.canApplyToOrder(orderAmount)) {
            System.err.println("❌ Promotion cannot be applied to this order");
            return 0;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            // Calculate discount amount
            double discountAmount = promotion.calculateDiscountAmount(orderAmount);
            
            // Update order with discount
            order.setDiscountAmount(discountAmount);
            order.setFinalAmount(orderAmount + order.getTotalAmount() * 0.08 - discountAmount); // Including VAT
            
            // Log promotion usage
            logPromotionUsage(conn, order.getOrderId(), promotion.getPromotionId(), discountAmount);
            
            // Update promotion usage count
            promotion.applyPromotion();
            updatePromotionUsage(conn, promotion);
            
            System.out.println("✅ Applied promotion: " + promotion.getPromotionName() + 
                " - Discount: " + discountAmount);
            
            return discountAmount;
            
        } catch (Exception e) {
            System.err.println("❌ Error applying promotion: " + e.getMessage());
            return 0;
        }
    }

    /**
     * Get promotion by ID
     * @param promotionId Promotion ID
     * @return Promotion if found
     */
    public Optional<Promotion> getPromotionById(int promotionId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            return Optional.ofNullable(promotion);
        } catch (Exception e) {
            System.err.println("❌ Error getting promotion by ID: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Create new promotion
     * @param promotion Promotion to create
     * @return true if successful
     */
    public boolean createPromotion(Promotion promotion) {
        if (promotion == null || !promotion.isValidPromotion()) {
            System.err.println("❌ Invalid promotion data");
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            
            // Set timestamps
            promotion.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean success = promotionDAO.insertPromotion(promotion);
            
            if (success) {
                System.out.println("✅ Created promotion: " + promotion.getPromotionName());
            } else {
                System.err.println("❌ Failed to create promotion");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ Error creating promotion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update existing promotion
     * @param promotion Promotion to update
     * @return true if successful
     */
    public boolean updatePromotion(Promotion promotion) {
        if (promotion == null || promotion.getPromotionId() <= 0) {
            System.err.println("❌ Invalid promotion for update");
            return false;
        }

        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            
            // Update timestamp
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean success = promotionDAO.updatePromotion(promotion);
            
            if (success) {
                System.out.println("✅ Updated promotion: " + promotion.getPromotionName());
            } else {
                System.err.println("❌ Failed to update promotion");
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ Error updating promotion: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deactivate promotion (soft delete)
     * @param promotionId Promotion ID to deactivate
     * @return true if successful
     */
    public boolean deactivatePromotion(int promotionId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            
            // Get promotion first
            Promotion promotion = promotionDAO.getPromotionById(promotionId);
            if (promotion == null) {
                System.err.println("❌ Promotion not found: " + promotionId);
                return false;
            }
            
            // Deactivate
            promotion.setActive(false);
            promotion.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
            
            boolean success = promotionDAO.updatePromotion(promotion);
            
            if (success) {
                System.out.println("✅ Deactivated promotion: " + promotion.getPromotionName());
            }
            
            return success;
            
        } catch (Exception e) {
            System.err.println("❌ Error deactivating promotion: " + e.getMessage());
            return false;
        }
    }

    // =====================================================
    // PROMOTION ANALYTICS
    // =====================================================

    /**
     * Get promotion usage statistics
     * @param promotionId Promotion ID
     * @return Usage statistics
     */
    public PromotionStats getPromotionStats(int promotionId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            // Implement promotion statistics query
            // This joins order_promotions table to get usage data
            
            String sql = """
                SELECT 
                    COUNT(op.order_promotion_id) as total_usage,
                    COUNT(CASE WHEN o.payment_status = 'paid' THEN 1 END) as successful_usage,
                    COALESCE(SUM(CASE WHEN o.payment_status = 'paid' THEN op.discount_amount END), 0) as total_discount_given
                FROM order_promotions op
                LEFT JOIN orders o ON op.order_id = o.order_id
                WHERE op.promotion_id = ?
                """;
            
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, promotionId);
                
                try (var rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        int totalUsage = rs.getInt("total_usage");
                        int successfulUsage = rs.getInt("successful_usage");
                        double totalDiscountGiven = rs.getDouble("total_discount_given");
                        
                        System.out.println("📊 Promotion stats for ID " + promotionId + ": " +
                                         "Total: " + totalUsage + ", Successful: " + successfulUsage + 
                                         ", Discount: " + String.format("%.0f VND", totalDiscountGiven));
                        
                        return new PromotionStats(promotionId, totalUsage, totalDiscountGiven, successfulUsage);
                    }
                }
            }
            
            // If no results found, return zero stats
            return new PromotionStats(promotionId, 0, 0.0, 0);
            
        } catch (Exception e) {
            System.err.println("❌ Error getting promotion stats: " + e.getMessage());
            e.printStackTrace();
            return new PromotionStats(promotionId, 0, 0.0, 0);
        }
    }

    /**
     * Get promotions expiring soon (within 7 days)
     * @return List of expiring promotions
     */
    public List<Promotion> getExpiringSoonPromotions() {
        List<Promotion> expiringSoon = new ArrayList<>();
        List<Promotion> activePromotions = getActivePromotions();
        
        for (Promotion promotion : activePromotions) {
            if (promotion.isExpiringSoon()) {
                expiringSoon.add(promotion);
            }
        }
        
        System.out.println("⚠️ Found " + expiringSoon.size() + " promotions expiring soon");
        
        return expiringSoon;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Log promotion usage in order_promotions table
     */
    private void logPromotionUsage(Connection conn, int orderId, int promotionId, double discountAmount) {
        try {
            String sql = "INSERT INTO order_promotions (order_id, promotion_id, discount_amount) VALUES (?, ?, ?)";
            try (var stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                stmt.setInt(2, promotionId);
                stmt.setDouble(3, discountAmount);
                stmt.executeUpdate();
                
                System.out.println("✅ Logged promotion usage for order " + orderId);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error logging promotion usage: " + e.getMessage());
        }
    }

    /**
     * Update promotion usage count
     */
    private void updatePromotionUsage(Connection conn, Promotion promotion) {
        try {
            PromotionDAO promotionDAO = new PromotionDAOImpl(conn);
            promotionDAO.updatePromotion(promotion);
        } catch (Exception e) {
            System.err.println("❌ Error updating promotion usage: " + e.getMessage());
        }
    }

    /**
     * Get sample promotions for demo/testing
     */
    private List<Promotion> getSamplePromotions() {
        List<Promotion> samplePromotions = new ArrayList<>();
        
        // Create sample promotions
        Promotion promo1 = new Promotion("Giảm 10%", "Giảm giá 10% cho đơn hàng từ 100,000đ", 
            Promotion.DiscountType.PERCENTAGE, 10, 100000);
        promo1.setPromotionId(1);
        
        Promotion promo2 = new Promotion("Giảm 20,000đ", "Giảm 20,000đ cho đơn hàng từ 150,000đ", 
            Promotion.DiscountType.FIXED_AMOUNT, 20000, 150000);
        promo2.setPromotionId(2);
        
        Promotion promo3 = new Promotion("Giảm 15% VIP", "Giảm giá 15% cho khách hàng VIP", 
            Promotion.DiscountType.PERCENTAGE, 15, 50000);
        promo3.setPromotionId(3);
        
        samplePromotions.add(promo1);
        samplePromotions.add(promo2);
        samplePromotions.add(promo3);
        
        System.out.println("✅ Loaded " + samplePromotions.size() + " sample promotions");
        
        return samplePromotions;
    }

    // =====================================================
    // INNER CLASSES
    // =====================================================

    /**
     * Promotion statistics data class
     */
    public static class PromotionStats {
        private final int promotionId;
        private final int usageCount;
        private final double totalDiscountAmount;
        private final int affectedOrders;
        
        public PromotionStats(int promotionId, int usageCount, double totalDiscountAmount, int affectedOrders) {
            this.promotionId = promotionId;
            this.usageCount = usageCount;
            this.totalDiscountAmount = totalDiscountAmount;
            this.affectedOrders = affectedOrders;
        }
        
        // Getters
        public int getPromotionId() { return promotionId; }
        public int getUsageCount() { return usageCount; }
        public double getTotalDiscountAmount() { return totalDiscountAmount; }
        public int getAffectedOrders() { return affectedOrders; }
        
        @Override
        public String toString() {
            return String.format("PromotionStats{id=%d, usage=%d, totalDiscount=%.2f, orders=%d}", 
                promotionId, usageCount, totalDiscountAmount, affectedOrders);
        }
    }
}
