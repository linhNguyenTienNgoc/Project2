package com.cafe.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Cấu hình Database với Connection Pooling
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class DatabaseConfig {
    
    private static DatabaseConfig instance;
    private static HikariDataSource dataSource;
    private static Properties properties;
    
    // Database connection properties
    private String url;
    private String username;
    private String password;
    private String driver;
    
    // Connection pool properties
    private int maxActive;
    private int minIdle;
    private long connectionTimeout;
    private long idleTimeout;
    private long maxLifetime;
    
    private DatabaseConfig() {
        loadProperties();
        initializeConnectionPool();
    }
    
    /**
     * Singleton pattern - lấy instance duy nhất
     */
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
    
    /**
     * Tải properties từ file config
     */
    private void loadProperties() {
        properties = new Properties();
        
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("properties/database_config.properties")) {
            
            if (input != null) {
                properties.load(input);
            } else {
                loadDefaultProperties();
            }
            
            // Load database properties
            url = properties.getProperty("database.url", 
                "jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh");
            username = properties.getProperty("database.username", "root");
            password = properties.getProperty("database.password", "12345678");
            driver = properties.getProperty("database.driver", "com.mysql.cj.jdbc.Driver");
            
            // Load connection pool properties
            maxActive = Integer.parseInt(properties.getProperty("database.maxActive", "50")); // Increased from 20 to 50
            minIdle = Integer.parseInt(properties.getProperty("database.minIdle", "5"));
            connectionTimeout = Long.parseLong(properties.getProperty("database.connectionTimeout", "10000")); // Reduced from 30000 to 10000
            idleTimeout = Long.parseLong(properties.getProperty("database.idleTimeout", "600000"));
            maxLifetime = Long.parseLong(properties.getProperty("database.maxLifetime", "1800000"));
            
        } catch (IOException e) {
            System.err.println("❌ Error loading database configuration: " + e.getMessage());
            loadDefaultProperties();
        }
    }
    
    /**
     * Tải cấu hình mặc định nếu không tìm thấy file properties
     */
    private void loadDefaultProperties() {
        url = "jdbc:mysql://localhost:3306/cafe_management?useUnicode=true&characterEncoding=UTF-8&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh";
        username = "root";
        password = "12345678";
        driver = "com.mysql.cj.jdbc.Driver";
        
        maxActive = 50;  // Increased from 20 to 50
        minIdle = 5;
        connectionTimeout = 10000; // Reduced from 30000 to 10000 (10 seconds)
        idleTimeout = 600000;
        maxLifetime = 1800000;
    }
    
    /**
     * Khởi tạo Connection Pool với HikariCP
     */
    private void initializeConnectionPool() {
        try {
            HikariConfig config = new HikariConfig();
            
            // Database connection settings
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(password);
            config.setDriverClassName(driver);
            
            // Connection pool settings
            config.setMinimumIdle(minIdle);
            config.setMaximumPoolSize(maxActive);
            config.setConnectionTimeout(connectionTimeout);
            config.setIdleTimeout(idleTimeout);
            config.setMaxLifetime(maxLifetime);
            
            // Pool name
            config.setPoolName("CafeManagementPool");
            
            // Connection test query
            config.setConnectionTestQuery("SELECT 1");
            
            // Auto commit
            config.setAutoCommit(true);
            
            // Connection leak detection
            config.setLeakDetectionThreshold(60000); // 60 seconds - increased for better detection
            
            // Additional properties for MySQL
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");
            config.addDataSourceProperty("useLocalSessionState", "true");
            config.addDataSourceProperty("rewriteBatchedStatements", "true");
            config.addDataSourceProperty("cacheResultSetMetadata", "true");
            config.addDataSourceProperty("cacheServerConfiguration", "true");
            config.addDataSourceProperty("elideSetAutoCommits", "true");
            config.addDataSourceProperty("maintainTimeStats", "false");
            
            dataSource = new HikariDataSource(config);
            
            System.out.println("✅ HikariCP connection pool initialized successfully");
            System.out.println("🔗 Database URL: " + url);
            System.out.println("👤 Username: " + username);
            System.out.println("🏊 Pool Size: " + minIdle + " - " + maxActive);
            
        } catch (Exception e) {
            System.err.println("❌ Error initializing connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Lấy connection từ pool
     */
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            getInstance(); // Initialize if not already done
        }
        
        if (dataSource != null) {
            return dataSource.getConnection();
        } else {
            throw new SQLException("DataSource is not initialized");
        }
    }
    
    /**
     * Kiểm tra kết nối database
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            return connection != null && !connection.isClosed();
        } catch (SQLException e) {
            System.err.println("❌ Database connection test failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Lấy thông tin database
     */
    public String getDatabaseInfo() {
        try (Connection connection = getConnection()) {
            String dbProduct = connection.getMetaData().getDatabaseProductName();
            String dbVersion = connection.getMetaData().getDatabaseProductVersion();
            String driverName = connection.getMetaData().getDriverName();
            String driverVersion = connection.getMetaData().getDriverVersion();
            
            return String.format(
                "Database: %s %s\nDriver: %s %s\nURL: %s",
                dbProduct, dbVersion, driverName, driverVersion, url
            );
        } catch (SQLException e) {
            return "Unable to get database info: " + e.getMessage();
        }
    }
    
    /**
     * Lấy thông tin connection pool
     */
    public String getPoolInfo() {
        if (dataSource != null) {
            return String.format(
                "Pool Status:\n" +
                "- Active Connections: %d\n" +
                "- Idle Connections: %d\n" +
                "- Total Connections: %d\n" +
                "- Threads Awaiting: %d",
                dataSource.getHikariPoolMXBean().getActiveConnections(),
                dataSource.getHikariPoolMXBean().getIdleConnections(),
                dataSource.getHikariPoolMXBean().getTotalConnections(),
                dataSource.getHikariPoolMXBean().getThreadsAwaitingConnection()
            );
        }
        return "Connection pool not initialized";
    }
    
    /**
     * Đóng connection pool
     */
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Connection pool closed");
        }
    }
    
    /**
     * Đóng connection pool (static method)
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔒 Connection pool closed");
        }
    }
    
    /**
     * Kiểm tra xem connection pool có đang hoạt động không
     */
    public boolean isPoolActive() {
        return dataSource != null && !dataSource.isClosed();
    }
    
    /**
     * Reset connection pool (đóng và khởi tạo lại)
     */
    public void resetPool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("🔄 Connection pool closed for reset");
        }
        dataSource = null;
        initializeConnectionPool();
        System.out.println("🔄 Connection pool reset completed");
    }
    
    /**
     * Kiểm tra sức khỏe của connection pool (chỉ đọc, không reset)
     */
    public void checkPoolHealth() {
        if (dataSource != null && !dataSource.isClosed()) {
            try {
                HikariPoolMXBean poolMXBean = dataSource.getHikariPoolMXBean();
                int activeConnections = poolMXBean.getActiveConnections();
                int totalConnections = poolMXBean.getTotalConnections();
                int threadsAwaiting = poolMXBean.getThreadsAwaitingConnection();
                
                // Chỉ log thông tin, không tự động reset
                if (activeConnections > maxActive * 0.8) {
                    System.out.println("⚠️ Pool health check: High connection usage detected");
                    System.out.println("   Active: " + activeConnections + ", Total: " + totalConnections + ", Waiting: " + threadsAwaiting);
                }
            } catch (Exception e) {
                System.err.println("❌ Error checking pool health: " + e.getMessage());
            }
        }
    }
    
    // Getters
    public String getUrl() { return url; }
    public String getUsername() { return username; }
    public String getDriver() { return driver; }
    public int getMaxActive() { return maxActive; }
    public int getMinIdle() { return minIdle; }
    
    /**
     * Lấy property value
     */
    public static String getProperty(String key, String defaultValue) {
        return properties != null ? properties.getProperty(key, defaultValue) : defaultValue;
    }
    
    /**
     * Lấy property value as int
     */
    public static int getPropertyAsInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(getProperty(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
    
    /**
     * Lấy property value as boolean
     */
    public static boolean getPropertyAsBoolean(String key, boolean defaultValue) {
        return Boolean.parseBoolean(getProperty(key, String.valueOf(defaultValue)));
    }
}