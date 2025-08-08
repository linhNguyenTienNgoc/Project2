package com.cafe.util;

import javafx.scene.image.Image;

import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Utility class cho loading và caching images
 * Hỗ trợ lazy loading và memory management
 */
public class ImageLoader {
    
    private static final Map<String, Image> imageCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 100;
    
    /**
     * Load product image với caching
     */
    public static Image loadProductImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return getDefaultProductImage();
        }
        
        return imageCache.computeIfAbsent(imageUrl, url -> {
            try {
                // Try to load from URL first
                if (url.startsWith("http://") || url.startsWith("https://")) {
                    return new Image(url, 150, 150, true, true);
                }
                
                // Try to load from file system
                if (url.startsWith("file://")) {
                    return new Image(url, 150, 150, true, true);
                }
                
                // Try to load from resources
                InputStream resourceStream = ImageLoader.class.getResourceAsStream(url);
                if (resourceStream != null) {
                    return new Image(resourceStream, 150, 150, true, true);
                }
                
                // Try to load from images folder
                String resourcePath = "/images/products/" + url;
                resourceStream = ImageLoader.class.getResourceAsStream(resourcePath);
                if (resourceStream != null) {
                    return new Image(resourceStream, 150, 150, true, true);
                }
                
                // Fallback to default image
                return getDefaultProductImage();
                
            } catch (Exception e) {
                System.err.println("Error loading image: " + url + " - " + e.getMessage());
                return getDefaultProductImage();
            }
        });
    }
    
    /**
     * Load category image
     */
    public static Image loadCategoryImage(String imageUrl) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return getDefaultCategoryImage();
        }
        
        return imageCache.computeIfAbsent("category_" + imageUrl, url -> {
            try {
                // Try to load from resources
                String resourcePath = "/images/categories/" + imageUrl;
                InputStream resourceStream = ImageLoader.class.getResourceAsStream(resourcePath);
                if (resourceStream != null) {
                    return new Image(resourceStream, 80, 80, true, true);
                }
                
                // Fallback to default image
                return getDefaultCategoryImage();
                
            } catch (Exception e) {
                System.err.println("Error loading category image: " + imageUrl + " - " + e.getMessage());
                return getDefaultCategoryImage();
            }
        });
    }
    
    /**
     * Load icon image
     */
    public static Image loadIcon(String iconName) {
        if (iconName == null || iconName.trim().isEmpty()) {
            return getDefaultIcon();
        }
        
        return imageCache.computeIfAbsent("icon_" + iconName, url -> {
            try {
                // Try to load from resources
                String resourcePath = "/images/icons/" + iconName;
                InputStream resourceStream = ImageLoader.class.getResourceAsStream(resourcePath);
                if (resourceStream != null) {
                    return new Image(resourceStream, 24, 24, true, true);
                }
                
                // Fallback to default icon
                return getDefaultIcon();
                
            } catch (Exception e) {
                System.err.println("Error loading icon: " + iconName + " - " + e.getMessage());
                return getDefaultIcon();
            }
        });
    }
    
    /**
     * Get default product image
     */
    public static Image getDefaultProductImage() {
        return imageCache.computeIfAbsent("default_product", url -> {
            try {
                InputStream resourceStream = ImageLoader.class.getResourceAsStream("/images/default-product.png");
                if (resourceStream != null) {
                    return new Image(resourceStream, 150, 150, true, true);
                }
                
                // Create a simple placeholder image
                return createPlaceholderImage(150, 150, "Product");
                
            } catch (Exception e) {
                return createPlaceholderImage(150, 150, "Product");
            }
        });
    }
    
    /**
     * Get default category image
     */
    public static Image getDefaultCategoryImage() {
        return imageCache.computeIfAbsent("default_category", url -> {
            try {
                InputStream resourceStream = ImageLoader.class.getResourceAsStream("/images/default-category.png");
                if (resourceStream != null) {
                    return new Image(resourceStream, 80, 80, true, true);
                }
                
                return createPlaceholderImage(80, 80, "Category");
                
            } catch (Exception e) {
                return createPlaceholderImage(80, 80, "Category");
            }
        });
    }
    
    /**
     * Get default icon
     */
    public static Image getDefaultIcon() {
        return imageCache.computeIfAbsent("default_icon", url -> {
            try {
                InputStream resourceStream = ImageLoader.class.getResourceAsStream("/images/icons/default-icon.png");
                if (resourceStream != null) {
                    return new Image(resourceStream, 24, 24, true, true);
                }
                
                return createPlaceholderImage(24, 24, "Icon");
                
            } catch (Exception e) {
                return createPlaceholderImage(24, 24, "Icon");
            }
        });
    }
    
    /**
     * Create placeholder image
     */
    private static Image createPlaceholderImage(int width, int height, String text) {
        // This is a simplified placeholder
        // In a real implementation, you might want to create a proper placeholder image
        try {
            // Try to load a generic placeholder
            InputStream resourceStream = ImageLoader.class.getResourceAsStream("/images/placeholder.png");
            if (resourceStream != null) {
                return new Image(resourceStream, width, height, true, true);
            }
        } catch (Exception e) {
            // Ignore and continue with null
        }
        
        // Return null if no placeholder is available
        return null;
    }
    
    /**
     * Clear image cache
     */
    public static void clearCache() {
        imageCache.clear();
    }
    
    /**
     * Remove specific image from cache
     */
    public static void removeFromCache(String imageUrl) {
        imageCache.remove(imageUrl);
    }
    
    /**
     * Get cache size
     */
    public static int getCacheSize() {
        return imageCache.size();
    }
    
    /**
     * Check if image is cached
     */
    public static boolean isCached(String imageUrl) {
        return imageCache.containsKey(imageUrl);
    }
    
    /**
     * Preload images
     */
    public static void preloadImages(String... imageUrls) {
        for (String url : imageUrls) {
            if (url != null && !url.trim().isEmpty()) {
                loadProductImage(url);
            }
        }
    }
    
    /**
     * Clean up old cache entries if cache is too large
     */
    public static void cleanupCache() {
        if (imageCache.size() > MAX_CACHE_SIZE) {
            // Remove oldest entries (simple implementation)
            // In a real implementation, you might want to use LRU cache
            int toRemove = imageCache.size() - MAX_CACHE_SIZE + 10;
            imageCache.keySet().stream()
                    .limit(toRemove)
                    .forEach(imageCache::remove);
        }
    }
}
