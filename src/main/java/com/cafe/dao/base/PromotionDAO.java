package com.cafe.dao.base;

import com.cafe.model.entity.Promotion;
import java.util.List;

/**
 * DAO interface for Promotion operations
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public interface PromotionDAO {

    /**
     * Get all promotions from database
     * @return List of all promotions
     */
    List<Promotion> getAllPromotions();

    /**
     * Get promotion by ID
     * @param promotionId Promotion ID
     * @return Promotion if found, null otherwise
     */
    Promotion getPromotionById(int promotionId);

    /**
     * Get all active promotions
     * @return List of active promotions
     */
    List<Promotion> getActivePromotions();

    /**
     * Insert new promotion
     * @param promotion Promotion to insert
     * @return true if successful
     */
    boolean insertPromotion(Promotion promotion);

    /**
     * Update existing promotion
     * @param promotion Promotion to update
     * @return true if successful
     */
    boolean updatePromotion(Promotion promotion);

    /**
     * Delete promotion (soft delete - set inactive)
     * @param promotionId Promotion ID to delete
     * @return true if successful
     */
    boolean deletePromotion(int promotionId);

    /**
     * Get promotions applicable to order amount
     * @param orderAmount Order total amount
     * @return List of applicable promotions
     */
    List<Promotion> getPromotionsForOrderAmount(double orderAmount);

    /**
     * Increment promotion usage count
     * @param promotionId Promotion ID
     * @return true if successful
     */
    boolean incrementUsageCount(int promotionId);
}
