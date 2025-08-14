package com.cafe.model.entity;

import java.sql.Timestamp;

public class Category {
    private int categoryId;
    private String categoryName;
    private String description;
    private boolean isActive;
    private java.sql.Timestamp createdAt;
    private java.sql.Timestamp updatedAt;

    // Constructors
    public Category() {}

    public Category(String categoryName, String description, boolean isActive) {
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive;
    }

    public Category(int categoryId, String categoryName, String description, boolean isActive,
                    java.sql.Timestamp createdAt, java.sql.Timestamp updatedAt) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    @Override
    public String toString() {
        return "Category{" +
                "categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", description='" + description + '\'' +
                ", isActive=" + isActive +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
