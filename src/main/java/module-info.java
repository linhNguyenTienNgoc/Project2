module com.coffeeshop.shopcoffeemanagement {
    requires javafx.controls;
    requires javafx.fxml;

    // Database dependencies
    requires java.sql;
    
    // UI Enhancement Libraries - Optimized for Performance
    requires org.controlsfx.controls;

    // Open packages for FXML
    opens com.coffeeshop.shopcoffeemanagement to javafx.fxml;
    opens com.coffeeshop.shopcoffeemanagement.controller to javafx.fxml;
    
    // Export packages
    exports com.coffeeshop.shopcoffeemanagement;
    exports com.coffeeshop.shopcoffeemanagement.controller;
    exports com.coffeeshop.shopcoffeemanagement.model;
    exports com.coffeeshop.shopcoffeemanagement.service;
    exports com.coffeeshop.shopcoffeemanagement.dao;
    exports com.coffeeshop.shopcoffeemanagement.util;
} 