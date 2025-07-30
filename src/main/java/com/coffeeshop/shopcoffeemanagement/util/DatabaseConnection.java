package com.coffeeshop.shopcoffeemanagement.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class DatabaseConnection {
    private static final String CONFIG_FILE = "/config/database.properties";
    private static Properties properties;
    private static DatabaseConnection instance;
    
    // Connection Pool Configuration
    private static final int POOL_SIZE = 10;
    private static final BlockingQueue<Connection> connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
    private static final AtomicInteger activeConnections = new AtomicInteger(0);
    private static final int MAX_CONNECTIONS = 20;
    
    private DatabaseConnection() {
        loadProperties();
        initializeConnectionPool();
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
    
    private void initializeConnectionPool() {
        try {
            Class.forName(properties.getProperty("db.driver"));
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = createNewConnection();
                if (conn != null) {
                    connectionPool.offer(conn);
                }
            }
            System.out.println("Connection pool initialized with " + connectionPool.size() + " connections");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error initializing connection pool: " + e.getMessage());
        }
    }
    
    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(
            properties.getProperty("db.url"),
            properties.getProperty("db.username"),
            properties.getProperty("db.password")
        );
    }
    
    public Connection getConnection() throws SQLException {
        // Try to get connection from pool first
        Connection conn = connectionPool.poll();
        if (conn != null) {
            // Check if connection is still valid
            if (isConnectionValid(conn)) {
                activeConnections.incrementAndGet();
                return conn;
            } else {
                // Connection is invalid, create new one
                try {
                    conn.close();
                } catch (SQLException e) {
                    // Ignore close errors
                }
            }
        }
        
        // Create new connection if pool is empty and under limit
        if (activeConnections.get() < MAX_CONNECTIONS) {
            Connection newConn = createNewConnection();
            activeConnections.incrementAndGet();
            return newConn;
        }
        
        // Wait for available connection
        try {
            conn = connectionPool.take();
            activeConnections.incrementAndGet();
            return conn;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for connection", e);
        }
    }
    
    private boolean isConnectionValid(Connection conn) {
        try {
            return conn != null && !conn.isClosed() && conn.isValid(1);
        } catch (SQLException e) {
            return false;
        }
    }
    
    public void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                // Reset connection state
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                    connection.setAutoCommit(true);
                }
                
                // Return to pool if it's not full
                if (connectionPool.size() < POOL_SIZE) {
                    connectionPool.offer(connection);
                } else {
                    // Close connection if pool is full
                    connection.close();
                }
            } catch (SQLException e) {
                // Close connection if there's an error
                try {
                    connection.close();
                } catch (SQLException ex) {
                    // Ignore close errors
                }
            } finally {
                activeConnections.decrementAndGet();
            }
        }
    }
    
    public void closeAllConnections() {
        Connection conn;
        while ((conn = connectionPool.poll()) != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                // Ignore close errors
            }
        }
        activeConnections.set(0);
    }
    
    public int getActiveConnections() {
        return activeConnections.get();
    }
    
    public int getPoolSize() {
        return connectionPool.size();
    }
    
        public String getProperty(String key) {
        return properties.getProperty(key);
    }
} 