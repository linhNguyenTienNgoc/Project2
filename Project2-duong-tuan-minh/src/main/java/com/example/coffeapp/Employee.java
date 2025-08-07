package com.example.coffeapp;

public class Employee {
    private int userId;
    private String username;
    private String fullName;
    private String dob;
    private String gender;
    private String shift;

    public Employee(int userId, String username, String fullName, String dob, String gender, String shift) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.dob = dob;
        this.gender = gender;
        this.shift = shift;
    }

    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getShift() { return shift; }

    public void setUserId(int userId) { this.userId = userId; }
    public void setUsername(String username) { this.username = username; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public void setDob(String dob) { this.dob = dob; }
    public void setGender(String gender) { this.gender = gender; }
    public void setShift(String shift) { this.shift = shift; }
}