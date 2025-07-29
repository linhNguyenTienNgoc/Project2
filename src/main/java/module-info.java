module com.coffeeshop.shopcoffeemanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;
    requires javafx.media;

    // Database dependencies
    requires java.sql;
    
    // UI Enhancement Libraries
    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

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