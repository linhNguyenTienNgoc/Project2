package com.cafe.model.entity;
public class Product {
    private int productId;
    private String productName;
    private int categoryId;
    private double price;
    private double costPrice;
    private String description;
    private String imageUrl;
    private boolean isAvailable;
    private boolean isActive;
    private Integer stockQuantity; // Thêm field cho stock quantity
    private String sku; // Thêm field cho SKU

    public Product() {
    }

    public Product(int productId, String productName, int categoryId, double price, double costPrice, String description, String imageUrl, boolean isAvailable, boolean isActive) {
        this.productId = productId;
        this.productName = productName;
        this.categoryId = categoryId;
        this.price = price;
        this.costPrice = costPrice;
        this.description = description;
        this.imageUrl = imageUrl;
        this.isAvailable = isAvailable;
        this.isActive = isActive;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
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

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    // Getter methods cho compatibility với view components
    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    // Business logic methods
    public String getFormattedPrice() {
        return String.format("%,.0f VNĐ", price);
    }

    public boolean isOutOfStock() {
        return stockQuantity == null || stockQuantity <= 0;
    }

    public boolean isLowStock() {
        return stockQuantity != null && stockQuantity > 0 && stockQuantity <= 5;
    }

    public boolean canBeOrdered() {
        // ✅ HỦY CHECK HẾT HÀNG - Luôn trả về true
        return true;
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", productName='" + productName + '\'' +
                ", categoryId=" + categoryId +
                ", price=" + price +
                ", costPrice=" + costPrice +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", isAvailable=" + isAvailable +
                ", isActive=" + isActive +
                '}';
    }
}
