package com.cafe.util;

import javafx.scene.image.Image;
import java.io.File;
import java.io.InputStream;

/**
 * Utility class for loading images
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class ImageLoader {
    
    private static final String DEFAULT_PRODUCT_IMAGE = "/images/placeholders/no-image.png";
    private static final String PRODUCT_IMAGES_PATH = "/images/products/";
    
    /**
     * Load product image by URL
     */
    public static Image loadProductImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return loadDefaultProductImage();
        }
        
        try {
            // Try to load from resources first
            if (imageUrl.startsWith("/")) {
                InputStream stream = ImageLoader.class.getResourceAsStream(imageUrl);
                if (stream != null) {
                    return new Image(stream);
                }
            }
            
            // Try to load from file system
            File imageFile = new File(imageUrl);
            if (imageFile.exists()) {
                return new Image(imageFile.toURI().toString());
            }
            
            // Try to load from product images directory
            String resourcePath = PRODUCT_IMAGES_PATH + imageUrl;
            InputStream stream = ImageLoader.class.getResourceAsStream(resourcePath);
            if (stream != null) {
                return new Image(stream);
            }
            
            // If all fails, return default image
            return loadDefaultProductImage();
            
        } catch (Exception e) {
            System.err.println("Error loading image: " + imageUrl + " - " + e.getMessage());
            return loadDefaultProductImage();
        }
    }
    
    /**
     * Load default product image
     */
    public static Image loadDefaultProductImage() {
        try {
            InputStream stream = ImageLoader.class.getResourceAsStream(DEFAULT_PRODUCT_IMAGE);
            if (stream != null) {
                return new Image(stream);
            } else {
                // Create a simple placeholder if default image is not found
                return createPlaceholderImage();
            }
        } catch (Exception e) {
            return createPlaceholderImage();
        }
    }
    
    /**
     * Create a simple placeholder image
     */
    private static Image createPlaceholderImage() {
        // This would create a simple colored rectangle as placeholder
        // For now, we'll try to load a basic icon
        try {
            InputStream stream = ImageLoader.class.getResourceAsStream("/images/icons/default-product-icon.png");
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            // If all else fails, return null - JavaFX will handle this gracefully
        }
        return null;
    }
    
    /**
     * Load icon image
     */
    public static Image loadIcon(String iconName) {
        try {
            String iconPath = "/images/icons/" + iconName;
            InputStream stream = ImageLoader.class.getResourceAsStream(iconPath);
            if (stream != null) {
                return new Image(stream);
            }
        } catch (Exception e) {
            System.err.println("Error loading icon: " + iconName + " - " + e.getMessage());
        }
        return null;
    }
}
