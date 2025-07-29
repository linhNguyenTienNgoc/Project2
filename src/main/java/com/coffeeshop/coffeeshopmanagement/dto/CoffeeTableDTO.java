package com.coffeeshop.coffeeshopmanagement.dto;


public class CoffeeTableDTO {
    private Long id;
    private String tableNumber;
    private int capacity;
    private String status;
    private String location;
    private boolean active;
    
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
} 