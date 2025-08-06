package com.cafe.model.entity;

import java.sql.Timestamp;

public class TableCafe {
    private int tableId;
    private String tableName;
    private int areaId;
    private int capacity;
    private String status; // "available", "occupied", "reserved", "cleaning"
    private boolean isActive;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public TableCafe() {}

    public TableCafe(String tableName, int areaId, int capacity, String status, boolean isActive) {
        this.tableName = tableName;
        this.areaId = areaId;
        this.capacity = capacity;
        this.status = status;
        this.isActive = isActive;
    }

    public TableCafe(int tableId, String tableName, int areaId, int capacity, String status,
                     boolean isActive, Timestamp createdAt, Timestamp updatedAt) {
        this.tableId = tableId;
        this.tableName = tableName;
        this.areaId = areaId;
        this.capacity = capacity;
        this.status = status;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getTableId() { return tableId; }
    public void setTableId(int tableId) { this.tableId = tableId; }

    public String getTableName() { return tableName; }
    public void setTableName(String tableName) { this.tableName = tableName; }

    public int getAreaId() { return areaId; }
    public void setAreaId(int areaId) { this.areaId = areaId; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }

}
