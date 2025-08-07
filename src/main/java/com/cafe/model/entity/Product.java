package com.cafe.model.entity;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Entity class cho bảng products
 * Cải thiện với đầy đủ fields, validation và business methods
 */
public class Product {
    private Integer productId;
    private String productName;
    private Integer categoryId;
    private String sku; // Stock Keeping Unit
    private Double price;
    private Double costPrice;
    private String description;
    private String imageUrl;
    private Integer stockQuantity;
    private Integer minStockLevel;
    private Boolean isAvailable;
    private Boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    // Relationship object
    private Category category;

    // Constructors
    public Product() {
        this.stockQuantity = 0;
        this.minStockLevel = 10;
        this.isAvailable = true;
        this.isActive = true;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Product(String productName, Integer categoryId, String sku, Double price, String description) {
        this();
        this.productName = productName;
        this.categoryId = categoryId;
        this.sku = sku;
        this.price = price;
        this.description = description;
    }

    public Product(Integer productId, String productName, Integer categoryId, String sku, Double price, 
                   Double costPrice, String description, String imageUrl, Integer stockQuantity, 
                   Integer minStockLevel, Boolean isAvailable, Boolean isActive) {
        this();
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.sku = sku;
        this.price = price;
        this.costPrice = costPrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
        this.minStockLevel = minStockLevel;
        this.isAvailable = isAvailable;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(Double costPrice) {
        this.costPrice = costPrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    // Business methods
    public boolean isInStock() {
        return stockQuantity != null && stockQuantity > 0;
    }
    
    public boolean isLowStock() {
        return stockQuantity != null && minStockLevel != null && stockQuantity <= minStockLevel;
    }
    
    public boolean isOutOfStock() {
        return stockQuantity != null && stockQuantity <= 0;
    }
    
    public boolean canBeOrdered() {
        return isAvailable && isActive && isInStock();
    }
    
    public Double getProfit() {
        if (price != null && costPrice != null) {
            return price - costPrice;
        }
        return null;
    }
    
    public Double getProfitMargin() {
        if (price != null && costPrice != null && price > 0) {
            return ((price - costPrice) / price) * 100;
        }
        return null;
    }
    
    public void decreaseStock(int quantity) {
        if (stockQuantity != null && stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            updateTimestamp();
        }
    }
    
    public void increaseStock(int quantity) {
        if (stockQuantity != null && quantity > 0) {
            this.stockQuantity += quantity;
            updateTimestamp();
        }
    }
    
    public String getStockStatus() {
        if (isOutOfStock()) return "Hết hàng";
        if (isLowStock()) return "Sắp hết";
        return "Còn hàng";
    }
    
    public String getCategoryName() {
        return category != null ? category.getCategoryName() : "Không xác định";
    }

    // Validation methods
    public boolean isValidProductName() {
        return productName != null && productName.length() >= 2 && productName.length() <= 150;
    }
    
    public boolean isValidPrice() {
        return price != null && price >= 0;
    }
    
    public boolean isValidCostPrice() {
        return costPrice == null || costPrice >= 0;
    }
    
    public boolean isValidStockQuantity() {
        return stockQuantity != null && stockQuantity >= 0;
    }
    
    public boolean isValidMinStockLevel() {
        return minStockLevel != null && minStockLevel >= 0;
    }
    
    public boolean isValidSku() {
        return sku != null && !sku.trim().isEmpty();
    }
    
    public boolean isValid() {
        return isValidProductName() && isValidPrice() && isValidCostPrice() && 
               isValidStockQuantity() && isValidMinStockLevel() && categoryId != null;
    }

    // Utility methods
    public void updateTimestamp() {
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }
    
    public String getFormattedPrice() {
        if (price != null) {
            return String.format("%,.0f VNĐ", price);
        }
        return "0 VNĐ";
    }
    
    public String getFormattedCostPrice() {
        if (costPrice != null) {
            return String.format("%,.0f VNĐ", costPrice);
        }
        return "0 VNĐ";
    }
    
    public String getShortDescription() {
        if (description != null && description.length() > 100) {
            return description.substring(0, 97) + "...";
        }
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId) && 
               Objects.equals(sku, product.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, sku);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", categoryId=" + categoryId +
                ", sku='" + sku + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", isAvailable=" + isAvailable +
                ", isActive=" + isActive +
                ", categoryName='" + getCategoryName() + '\'' +
                '}';
    }
}
