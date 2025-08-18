package com.cafe.dao.base;

import com.cafe.model.entity.Promotion;
import com.cafe.model.entity.Promotion.DiscountType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of PromotionDAO for database operations
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class PromotionDAOImpl implements PromotionDAO {

    private final Connection connection;

    public PromotionDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public List<Promotion> getAllPromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = "SELECT * FROM promotions ORDER BY created_at DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting all promotions: " + e.getMessage());
        }
        
        return promotions;
    }

    @Override
    public Promotion getPromotionById(int promotionId) {
        String sql = "SELECT * FROM promotions WHERE promotion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPromotion(rs);
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting promotion by ID: " + e.getMessage());
        }
        
        return null;
    }

    @Override
    public List<Promotion> getActivePromotions() {
        List<Promotion> promotions = new ArrayList<>();
        String sql = """
            SELECT * FROM promotions 
            WHERE is_active = 1 
            AND (start_date IS NULL OR start_date <= CURRENT_TIMESTAMP)
            AND (end_date IS NULL OR end_date >= CURRENT_TIMESTAMP)
            ORDER BY created_at DESC
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting active promotions: " + e.getMessage());
        }
        
        return promotions;
    }

    @Override
    public boolean insertPromotion(Promotion promotion) {
        String sql = """
            INSERT INTO promotions (
                promotion_name, description, discount_type, discount_value, 
                min_order_amount, max_discount_amount, start_date, end_date, 
                is_active, usage_limit, usage_count, created_at, updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setPromotionParameters(stmt, promotion);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    promotion.setPromotionId(generatedKeys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ Error inserting promotion: " + e.getMessage());
        }
        
        return false;
    }

    @Override
    public boolean updatePromotion(Promotion promotion) {
        String sql = """
            UPDATE promotions SET 
                promotion_name = ?, description = ?, discount_type = ?, 
                discount_value = ?, min_order_amount = ?, max_discount_amount = ?, 
                start_date = ?, end_date = ?, is_active = ?, usage_limit = ?, 
                usage_count = ?, updated_at = ?
            WHERE promotion_id = ?
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            setPromotionParameters(stmt, promotion);
            stmt.setInt(13, promotion.getPromotionId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error updating promotion: " + e.getMessage());
        }
        
        return false;
    }

    @Override
    public boolean deletePromotion(int promotionId) {
        // Soft delete - set inactive
        String sql = "UPDATE promotions SET is_active = 0, updated_at = CURRENT_TIMESTAMP WHERE promotion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error deleting promotion: " + e.getMessage());
        }
        
        return false;
    }

    @Override
    public List<Promotion> getPromotionsForOrderAmount(double orderAmount) {
        List<Promotion> promotions = new ArrayList<>();
        String sql = """
            SELECT * FROM promotions 
            WHERE is_active = 1 
            AND min_order_amount <= ?
            AND (start_date IS NULL OR start_date <= CURRENT_TIMESTAMP)
            AND (end_date IS NULL OR end_date >= CURRENT_TIMESTAMP)
            AND (usage_limit = 0 OR usage_count < usage_limit)
            ORDER BY discount_value DESC
            """;
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, orderAmount);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                promotions.add(mapResultSetToPromotion(rs));
            }
        } catch (SQLException e) {
            System.err.println("❌ Error getting promotions for order amount: " + e.getMessage());
        }
        
        return promotions;
    }

    @Override
    public boolean incrementUsageCount(int promotionId) {
        String sql = "UPDATE promotions SET usage_count = usage_count + 1, updated_at = CURRENT_TIMESTAMP WHERE promotion_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, promotionId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("❌ Error incrementing usage count: " + e.getMessage());
        }
        
        return false;
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Map ResultSet to Promotion object
     */
    private Promotion mapResultSetToPromotion(ResultSet rs) throws SQLException {
        Promotion promotion = new Promotion();
        
        promotion.setPromotionId(rs.getInt("promotion_id"));
        promotion.setPromotionName(rs.getString("promotion_name"));
        promotion.setDescription(rs.getString("description"));
        
        // Map discount type
        String discountTypeStr = rs.getString("discount_type");
        promotion.setDiscountType(DiscountType.valueOf(discountTypeStr));
        
        promotion.setDiscountValue(rs.getDouble("discount_value"));
        promotion.setMinOrderAmount(rs.getDouble("min_order_amount"));
        promotion.setMaxDiscountAmount(rs.getDouble("max_discount_amount"));
        promotion.setStartDate(rs.getTimestamp("start_date"));
        promotion.setEndDate(rs.getTimestamp("end_date"));
        promotion.setActive(rs.getBoolean("is_active"));
        promotion.setUsageLimit(rs.getInt("usage_limit"));
        promotion.setUsageCount(rs.getInt("usage_count"));
        promotion.setCreatedAt(rs.getTimestamp("created_at"));
        promotion.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        return promotion;
    }

    /**
     * Set parameters for PreparedStatement
     */
    private void setPromotionParameters(PreparedStatement stmt, Promotion promotion) throws SQLException {
        stmt.setString(1, promotion.getPromotionName());
        stmt.setString(2, promotion.getDescription());
        stmt.setString(3, promotion.getDiscountType().name());
        stmt.setDouble(4, promotion.getDiscountValue());
        stmt.setDouble(5, promotion.getMinOrderAmount());
        stmt.setDouble(6, promotion.getMaxDiscountAmount());
        stmt.setTimestamp(7, promotion.getStartDate());
        stmt.setTimestamp(8, promotion.getEndDate());
        stmt.setBoolean(9, promotion.isActive());
        stmt.setInt(10, promotion.getUsageLimit());
        stmt.setInt(11, promotion.getUsageCount());
        stmt.setTimestamp(12, promotion.getUpdatedAt());
    }
}
