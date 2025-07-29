package com.coffeeshop.coffeeshopmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/test/database")
    public Map<String, Object> testDatabase() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test connection
            Connection connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            
            result.put("status", "SUCCESS");
            result.put("database", metaData.getDatabaseProductName());
            result.put("version", metaData.getDatabaseProductVersion());
            result.put("url", metaData.getURL());
            result.put("username", metaData.getUserName());
            
            // Test query
            String dbName = jdbcTemplate.queryForObject("SELECT DB_NAME()", String.class);
            result.put("currentDatabase", dbName);
            
            // Check if Employee table exists
            try {
                int employeeCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Employee", Integer.class);
                result.put("employeeCount", employeeCount);
                
                // Get sample employee data
                List<Map<String, Object>> employees = jdbcTemplate.queryForList(
                    "SELECT TOP 3 id, username, password, role, active FROM Employee"
                );
                result.put("sampleEmployees", employees);
                
            } catch (Exception e) {
                result.put("employeeTableError", e.getMessage());
            }
            
            connection.close();
            
        } catch (SQLException e) {
            result.put("status", "ERROR");
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    @GetMapping("/test/connection-string")
    public Map<String, Object> testConnectionString() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // Test basic connection
            Connection connection = dataSource.getConnection();
            result.put("connectionStatus", "SUCCESS");
            result.put("connectionUrl", connection.getMetaData().getURL());
            
            // Test if we can access CoffeeShopManagement database
            try {
                jdbcTemplate.execute("USE CoffeeShopManagement");
                result.put("databaseAccess", "SUCCESS");
                
                // Test Employee table
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM Employee", Integer.class);
                result.put("employeeTableCount", count);
                
            } catch (Exception e) {
                result.put("databaseAccess", "FAILED");
                result.put("databaseError", e.getMessage());
            }
            
            connection.close();
            
        } catch (Exception e) {
            result.put("connectionStatus", "FAILED");
            result.put("connectionError", e.getMessage());
        }
        
        return result;
    }
} 