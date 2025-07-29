package com.coffeeshop.shopcoffeemanagement.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "/config/database.properties";
    private static Properties properties;
    private static DatabaseConnection instance;
    
    private DatabaseConnection() {
        loadProperties();
    }
    
    public static DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Không thể tìm thấy file " + CONFIG_FILE + ". Sử dụng cấu hình mặc định.");
                // Sử dụng cấu hình mặc định
                properties.setProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true");
                properties.setProperty("db.username", "sa");
                properties.setProperty("db.password", "your_password_here");
                properties.setProperty("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Lỗi khi đọc file cấu hình database: " + e.getMessage() + ". Sử dụng cấu hình mặc định.");
            // Sử dụng cấu hình mặc định
            properties.setProperty("db.url", "jdbc:sqlserver://localhost:1433;databaseName=CoffeeShopManagement;encrypt=true;trustServerCertificate=true");
            properties.setProperty("db.username", "sa");
            properties.setProperty("db.password", "your_password_here");
            properties.setProperty("db.driver", "com.microsoft.sqlserver.jdbc.SQLServerDriver");
        }
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Class.forName(properties.getProperty("db.driver"));
            return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password")
            );
        } catch (ClassNotFoundException e) {
            throw new SQLException("Không thể tìm thấy driver database", e);
        }
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                System.err.println("Lỗi khi đóng kết nối database: " + e.getMessage());
            }
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
} 