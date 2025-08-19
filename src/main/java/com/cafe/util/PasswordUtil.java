package com.cafe.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class để xử lý mật khẩu
 */
public class PasswordUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16;

    /**
     * Hash mật khẩu với salt
     */
    public static String hashPassword(String password) {
        try {
            // Tạo salt ngẫu nhiên
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);

            // Kết hợp password và salt
            String saltedPassword = password + Base64.getEncoder().encodeToString(salt);

            // Hash password
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());

            // Kết hợp salt và hash
            return Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hashedBytes);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }

    /**
     * Kiểm tra mật khẩu có khớp không
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Tách salt và hash từ storedHash
            String[] parts = storedHash.split(":");
            if (parts.length != 2) {
                return false;
            }

            String salt = parts[0];
            String hash = parts[1];

            // Hash password với salt đã lưu
            String saltedPassword = password + salt;
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedBytes = md.digest(saltedPassword.getBytes());
            String newHash = Base64.getEncoder().encodeToString(hashedBytes);

            // So sánh hash
            return hash.equals(newHash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }

    /**
     * Tạo mật khẩu ngẫu nhiên
     */
    public static String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * Kiểm tra độ mạnh của mật khẩu
     */
    public static boolean isStrongPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    /**
     * Hash mật khẩu đơn giản (không có salt - chỉ để tương thích)
     */
    public static String simpleHash(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashedBytes = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Hash algorithm not available", e);
        }
    }
}
