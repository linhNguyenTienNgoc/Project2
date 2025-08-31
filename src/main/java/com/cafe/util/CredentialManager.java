package com.cafe.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Properties;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Utility class để quản lý việc lưu trữ và tải credentials
 * với mã hóa cơ bản cho bảo mật
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class CredentialManager {
    
    private static final String CREDENTIALS_DIR = System.getProperty("user.home") + "/.cafe_management";
    private static final String CREDENTIALS_FILE = CREDENTIALS_DIR + "/credentials.properties";
    private static final String KEY_FILE = CREDENTIALS_DIR + "/app.key";
    private static final String ALGORITHM = "AES";
    
    /**
     * Lưu thông tin đăng nhập với mã hóa (chỉ username)
     * @param username Tên đăng nhập
     * @param rememberUsername Có lưu username không
     */
    public static void saveCredentials(String username, boolean rememberUsername) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            createCredentialsDirectory();
            
            Properties props = new Properties();
            
            if (rememberUsername && username != null && !username.trim().isEmpty()) {
                // Mã hóa username trước khi lưu
                String encryptedUsername = encryptData(username);
                props.setProperty("username", encryptedUsername);
                props.setProperty("remember", "true");
            } else {
                // Xóa thông tin đã lưu
                props.setProperty("username", "");
                props.setProperty("remember", "false");
            }
            
            props.setProperty("lastLogin", String.valueOf(System.currentTimeMillis()));
            
            // Lưu vào file
            try (FileOutputStream fos = new FileOutputStream(CREDENTIALS_FILE)) {
                props.store(fos, "Cafe Management System - Remember Me Settings");
                System.out.println("💾 Credentials saved successfully");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error saving credentials: " + e.getMessage());
        }
    }
    
    /**
     * Lưu thông tin đăng nhập bao gồm cả password (mã hóa)
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @param rememberCredentials Có lưu thông tin không
     */
    public static void saveFullCredentials(String username, String password, boolean rememberCredentials) {
        try {
            // Tạo thư mục nếu chưa tồn tại
            createCredentialsDirectory();
            
            Properties props = new Properties();
            
            if (rememberCredentials && username != null && !username.trim().isEmpty()) {
                // Mã hóa username trước khi lưu
                String encryptedUsername = encryptData(username);
                props.setProperty("username", encryptedUsername);
                
                // Mã hóa password trước khi lưu
                if (password != null && !password.isEmpty()) {
                    String encryptedPassword = encryptData(password);
                    props.setProperty("password", encryptedPassword);
                    System.out.println("💾 Password saved (encrypted)");
                }
                
                props.setProperty("remember", "true");
                props.setProperty("fullCredentials", "true");
            } else {
                // Xóa thông tin đã lưu
                props.setProperty("username", "");
                props.setProperty("password", "");
                props.setProperty("remember", "false");
                props.setProperty("fullCredentials", "false");
            }
            
            props.setProperty("lastLogin", String.valueOf(System.currentTimeMillis()));
            
            // Lưu vào file
            try (FileOutputStream fos = new FileOutputStream(CREDENTIALS_FILE)) {
                props.store(fos, "Cafe Management System - Full Credentials (Username + Password)");
                System.out.println("💾 Full credentials saved successfully");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error saving full credentials: " + e.getMessage());
        }
    }
    
    /**
     * Tải thông tin đăng nhập đã lưu (chỉ username)
     * @return SavedCredentials object hoặc null nếu không có
     */
    public static SavedCredentials loadSavedCredentials() {
        try {
            File credentialsFile = new File(CREDENTIALS_FILE);
            if (!credentialsFile.exists()) {
                return new SavedCredentials("", false);
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(CREDENTIALS_FILE)) {
                props.load(fis);
            }
            
            boolean remember = Boolean.parseBoolean(props.getProperty("remember", "false"));
            String username = "";
            
            if (remember) {
                String encryptedUsername = props.getProperty("username", "");
                if (!encryptedUsername.isEmpty()) {
                    username = decryptData(encryptedUsername);
                }
            }
            
            System.out.println("📂 Credentials loaded: remember=" + remember + ", username=" + 
                             (username.isEmpty() ? "none" : "***"));
            
            return new SavedCredentials(username, remember);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading credentials: " + e.getMessage());
            return new SavedCredentials("", false);
        }
    }
    
    /**
     * Tải thông tin đăng nhập đầy đủ (username + password)
     * @return FullCredentials object
     */
    public static FullCredentials loadFullCredentials() {
        try {
            File credentialsFile = new File(CREDENTIALS_FILE);
            if (!credentialsFile.exists()) {
                return new FullCredentials("", "", false);
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(CREDENTIALS_FILE)) {
                props.load(fis);
            }
            
            boolean remember = Boolean.parseBoolean(props.getProperty("remember", "false"));
            boolean fullCredentials = Boolean.parseBoolean(props.getProperty("fullCredentials", "false"));
            String username = "";
            String password = "";
            
            if (remember && fullCredentials) {
                String encryptedUsername = props.getProperty("username", "");
                if (!encryptedUsername.isEmpty()) {
                    username = decryptData(encryptedUsername);
                }
                
                String encryptedPassword = props.getProperty("password", "");
                if (!encryptedPassword.isEmpty()) {
                    password = decryptData(encryptedPassword);
                    System.out.println("📂 Password loaded from storage");
                }
            }
            
            System.out.println("📂 Full credentials loaded: remember=" + remember + 
                             ", username=" + (username.isEmpty() ? "none" : "***") +
                             ", password=" + (password.isEmpty() ? "none" : "***"));
            
            return new FullCredentials(username, password, remember);
            
        } catch (Exception e) {
            System.err.println("❌ Error loading full credentials: " + e.getMessage());
            return new FullCredentials("", "", false);
        }
    }
    
    /**
     * Xóa thông tin đăng nhập đã lưu
     */
    public static void clearSavedCredentials() {
        try {
            File credentialsFile = new File(CREDENTIALS_FILE);
            if (credentialsFile.exists()) {
                credentialsFile.delete();
                System.out.println("🗑️ Saved credentials cleared");
            }
            
            File keyFile = new File(KEY_FILE);
            if (keyFile.exists()) {
                keyFile.delete();
                System.out.println("🗑️ Encryption key cleared");
            }
            
        } catch (Exception e) {
            System.err.println("❌ Error clearing credentials: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra xem có credentials đã lưu không
     */
    public static boolean hasSavedCredentials() {
        try {
            File credentialsFile = new File(CREDENTIALS_FILE);
            if (!credentialsFile.exists()) {
                return false;
            }
            
            Properties props = new Properties();
            try (FileInputStream fis = new FileInputStream(CREDENTIALS_FILE)) {
                props.load(fis);
            }
            
            return Boolean.parseBoolean(props.getProperty("remember", "false"));
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Tạo thư mục lưu trữ credentials
     */
    private static void createCredentialsDirectory() {
        try {
            Path dirPath = Paths.get(CREDENTIALS_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
                System.out.println("📁 Created credentials directory: " + CREDENTIALS_DIR);
            }
        } catch (Exception e) {
            System.err.println("❌ Error creating credentials directory: " + e.getMessage());
        }
    }
    
    /**
     * Tạo hoặc tải encryption key
     */
    private static SecretKey getOrCreateKey() {
        try {
            File keyFile = new File(KEY_FILE);
            
            if (keyFile.exists()) {
                // Tải key từ file
                byte[] keyBytes = Files.readAllBytes(keyFile.toPath());
                return new SecretKeySpec(keyBytes, ALGORITHM);
            } else {
                // Tạo key mới
                KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
                keyGen.init(128);
                SecretKey key = keyGen.generateKey();
                
                // Lưu key vào file
                Files.write(keyFile.toPath(), key.getEncoded());
                System.out.println("🔐 Created new encryption key");
                
                return key;
            }
        } catch (Exception e) {
            System.err.println("❌ Error with encryption key: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Mã hóa dữ liệu
     */
    private static String encryptData(String data) {
        try {
            SecretKey key = getOrCreateKey();
            if (key == null) {
                return data; // Fallback to plain text
            }
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedData = cipher.doFinal(data.getBytes());
            
            return Base64.getEncoder().encodeToString(encryptedData);
        } catch (Exception e) {
            System.err.println("❌ Error encrypting data: " + e.getMessage());
            return data; // Fallback to plain text
        }
    }
    
    /**
     * Giải mã dữ liệu
     */
    private static String decryptData(String encryptedData) {
        try {
            SecretKey key = getOrCreateKey();
            if (key == null) {
                return encryptedData; // Fallback to plain text
            }
            
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedData = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedData = cipher.doFinal(decodedData);
            
            return new String(decryptedData);
        } catch (Exception e) {
            System.err.println("❌ Error decrypting data: " + e.getMessage());
            return ""; // Return empty string on error
        }
    }
    
    /**
     * Inner class để lưu trữ thông tin credentials (chỉ username)
     */
    public static class SavedCredentials {
        private final String username;
        private final boolean rememberMe;
        
        public SavedCredentials(String username, boolean rememberMe) {
            this.username = username != null ? username : "";
            this.rememberMe = rememberMe;
        }
        
        public String getUsername() {
            return username;
        }
        
        public boolean isRememberMe() {
            return rememberMe;
        }
        
        public boolean hasUsername() {
            return !username.isEmpty();
        }
        
        @Override
        public String toString() {
            return "SavedCredentials{" +
                    "username='" + (username.isEmpty() ? "none" : "***") + '\'' +
                    ", rememberMe=" + rememberMe +
                    '}';
        }
    }
    
    /**
     * Inner class để lưu trữ thông tin credentials đầy đủ (username + password)
     */
    public static class FullCredentials {
        private final String username;
        private final String password;
        private final boolean rememberMe;
        
        public FullCredentials(String username, String password, boolean rememberMe) {
            this.username = username != null ? username : "";
            this.password = password != null ? password : "";
            this.rememberMe = rememberMe;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public boolean isRememberMe() {
            return rememberMe;
        }
        
        public boolean hasUsername() {
            return !username.isEmpty();
        }
        
        public boolean hasPassword() {
            return !password.isEmpty();
        }
        
        public boolean hasFullCredentials() {
            return hasUsername() && hasPassword();
        }
        
        @Override
        public String toString() {
            return "FullCredentials{" +
                    "username='" + (username.isEmpty() ? "none" : "***") + '\'' +
                    ", password='" + (password.isEmpty() ? "none" : "***") + '\'' +
                    ", rememberMe=" + rememberMe +
                    '}';
        }
    }
}
