package com.cafe.demo;

import com.cafe.util.CredentialManager;

/**
 * Demo class để test chức năng Remember Me (bao gồm cả password)
 * 
 * @author Team 2_C2406L  
 * @version 2.0.0
 */
public class RememberMeDemo {
    
    public static void main(String[] args) {
        System.out.println("🧪 TESTING FULL REMEMBER ME FUNCTIONALITY");
        System.out.println("==========================================");
        
        // Test 1: Save full credentials (username + password)
        System.out.println("\n📝 Test 1: Saving full credentials (username + password)");
        CredentialManager.saveFullCredentials("admin", "123456", true);
        
        // Test 2: Load full credentials
        System.out.println("\n📂 Test 2: Loading full credentials");
        CredentialManager.FullCredentials fullCreds = CredentialManager.loadFullCredentials();
        System.out.println("Loaded: " + fullCreds);
        if (fullCreds.hasFullCredentials()) {
            System.out.println("✅ Username: " + fullCreds.getUsername());
            System.out.println("✅ Password: " + fullCreds.getPassword());
        }
        
        // Test 3: Save only username (old method)
        System.out.println("\n📝 Test 3: Saving only username (old method)");
        CredentialManager.saveCredentials("testuser", true);
        
        CredentialManager.SavedCredentials savedCreds = CredentialManager.loadSavedCredentials();
        System.out.println("Loaded (username only): " + savedCreds);
        
        // Test 4: Load with fallback
        System.out.println("\n📂 Test 4: Load with fallback logic");
        fullCreds = CredentialManager.loadFullCredentials();
        System.out.println("Full credentials: " + fullCreds);
        
        // Test 5: Save without remember me
        System.out.println("\n🚫 Test 5: Save without remember me");
        CredentialManager.saveFullCredentials("", "", false);
        fullCreds = CredentialManager.loadFullCredentials();
        System.out.println("After disable: " + fullCreds);
        
        // Test 6: Clear all credentials
        System.out.println("\n🗑️ Test 6: Clear all credentials");
        CredentialManager.clearSavedCredentials();
        fullCreds = CredentialManager.loadFullCredentials();
        System.out.println("After clear: " + fullCreds);
        
        System.out.println("\n✅ All tests completed!");
        System.out.println("💡 Now username AND password will be saved when 'Remember Me' is checked!");
    }
}
