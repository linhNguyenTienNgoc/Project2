package com.coffeeshop.shopcoffeemanagement.model;

import java.time.LocalDateTime;

public class CoffeeTable {
    private Long id;
    private String tableNumber;
    private int capacity;
    private String status; // AVAILABLE, OCCUPIED, RESERVED, CLEANING
    private String location;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CoffeeTable() {}

    public CoffeeTable(String tableNumber, int capacity, String location) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.location = location;
        this.status = "AVAILABLE";
        this.active = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTableNumber() { return tableNumber; }
    public void setTableNumber(String tableNumber) { this.tableNumber = tableNumber; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public boolean isAvailable() {
        return "AVAILABLE".equals(status);
    }

    public boolean isOccupied() {
        return "OCCUPIED".equals(status);
    }

    @Override
    public String toString() {
        return "Bàn " + tableNumber + " (" + status + ")";
    }
} 