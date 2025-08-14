package com.cafe.exception;

/**
 * Custom exception cho các lỗi kết nối database
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class DatabaseConnectionException extends Exception {
    
    private static final long serialVersionUID = 1L;
    
    public DatabaseConnectionException(String message) {
        super(message);
    }
    
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseConnectionException(Throwable cause) {
        super(cause);
    }
}