package com.coffeeshop.shopcoffeemanagement.util;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Label;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Monitor hiệu suất ứng dụng
 */
public class PerformanceMonitor {
    
    private static PerformanceMonitor instance;
    private final ScheduledExecutorService scheduler;
    private Label fpsLabel;
    private AnimationTimer fpsTimer;
    private long frameCount = 0;
    private long lastTime = 0;
    
    private PerformanceMonitor() {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public static PerformanceMonitor getInstance() {
        if (instance == null) {
            instance = new PerformanceMonitor();
        }
        return instance;
    }
    
    public void setFpsLabel(Label label) {
        this.fpsLabel = label;
        startFPSTimer();
    }
    
    private void startFPSTimer() {
        if (fpsTimer != null) {
            fpsTimer.stop();
        }
        
        fpsTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                frameCount++;
                if (now - lastTime >= 1_000_000_000) { // 1 second
                    if (fpsLabel != null) {
                        Platform.runLater(() -> {
                            fpsLabel.setText(String.format("FPS: %d", frameCount));
                            
                            // Change color based on FPS
                            if (frameCount < 30) {
                                fpsLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                            } else if (frameCount < 50) {
                                fpsLabel.setStyle("-fx-text-fill: orange; -fx-font-weight: bold;");
                            } else {
                                fpsLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                            }
                        });
                    }
                    frameCount = 0;
                    lastTime = now;
                }
            }
        };
        fpsTimer.start();
    }
    
    public void logPerformance(String operation, long startTime) {
        long duration = System.currentTimeMillis() - startTime;
        if (duration > 100) { // Log slow operations
            System.out.println("Performance Warning: " + operation + " took " + duration + "ms");
        }
    }
    
    public void cleanup() {
        if (fpsTimer != null) {
            fpsTimer.stop();
        }
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    
    public static class PerformanceTimer {
        private final long startTime;
        private final String operation;
        
        public PerformanceTimer(String operation) {
            this.operation = operation;
            this.startTime = System.currentTimeMillis();
        }
        
        public void end() {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 100) {
                System.out.println("Performance Warning: " + operation + " took " + duration + "ms");
            }
        }
    }
} 