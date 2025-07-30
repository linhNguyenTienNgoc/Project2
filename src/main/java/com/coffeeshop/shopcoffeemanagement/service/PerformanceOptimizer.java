package com.coffeeshop.shopcoffeemanagement.service;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.util.Duration;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Service để tối ưu hiệu suất ứng dụng
 */
public class PerformanceOptimizer {
    
    private static PerformanceOptimizer instance;
    private final ExecutorService backgroundExecutor;
    private final ExecutorService uiExecutor;
    
    private PerformanceOptimizer() {
        // Background thread pool cho các tác vụ nặng
        this.backgroundExecutor = Executors.newFixedThreadPool(
            Math.max(2, Runtime.getRuntime().availableProcessors() - 1),
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r, "Background-" + counter.getAndIncrement());
                    thread.setDaemon(true);
                    thread.setPriority(Thread.MIN_PRIORITY);
                    return thread;
                }
            }
        );
        
        // UI thread pool cho các tác vụ UI nhẹ
        this.uiExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread thread = new Thread(r, "UI-Thread");
            thread.setDaemon(true);
            thread.setPriority(Thread.NORM_PRIORITY);
            return thread;
        });
    }
    
    public static PerformanceOptimizer getInstance() {
        if (instance == null) {
            instance = new PerformanceOptimizer();
        }
        return instance;
    }
    
    /**
     * Chạy tác vụ nặng trong background thread
     */
    public <T> CompletableFuture<T> runInBackground(java.util.function.Supplier<T> task) {
        return CompletableFuture.supplyAsync(task, backgroundExecutor);
    }
    
    /**
     * Chạy tác vụ UI trong UI thread
     */
    public CompletableFuture<Void> runInUI(Runnable task) {
        return CompletableFuture.runAsync(() -> {
            if (Platform.isFxApplicationThread()) {
                task.run();
            } else {
                Platform.runLater(task);
            }
        }, uiExecutor);
    }
    
    /**
     * Tối ưu TableView performance
     */
    public void optimizeTableView(TableView<?> tableView) {
        // Tắt auto resize columns
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        // Tắt fixed cell size để tối ưu memory
        tableView.setFixedCellSize(-1);
        
        // Tối ưu scroll performance
        tableView.setCacheShape(false);
        tableView.setCache(true);
    }
    
    /**
     * Tối ưu Region performance
     */
    public void optimizeRegion(Region region) {
        // Tắt cache shape nếu không cần thiết
        region.setCacheShape(false);
        
        // Bật cache cho regions lớn
        if (region.getPrefWidth() > 200 || region.getPrefHeight() > 200) {
            region.setCache(true);
        }
        
        // Tối ưu layout
        region.setManaged(true);
        region.setVisible(true);
    }
    
    /**
     * Tối ưu Node performance
     */
    public void optimizeNode(Node node) {
        // Bật cache cho nodes phức tạp
        if (node.getStyleClass().size() > 3) {
            node.setCache(true);
        }
    }
    
    /**
     * Debounce function để tránh gọi quá nhiều lần
     */
    public static class Debouncer {
        private final long delay;
        private java.util.Timer timer;
        
        public Debouncer(long delay) {
            this.delay = delay;
        }
        
        public void debounce(Runnable task) {
            if (timer != null) {
                timer.cancel();
            }
            timer = new java.util.Timer();
            timer.schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(task);
                }
            }, delay);
        }
    }
    
    /**
     * Throttle function để giới hạn tần suất gọi
     */
    public static class Throttler {
        private final long interval;
        private long lastExecution = 0;
        
        public Throttler(long interval) {
            this.interval = interval;
        }
        
        public void throttle(Runnable task) {
            long now = System.currentTimeMillis();
            if (now - lastExecution >= interval) {
                lastExecution = now;
                Platform.runLater(task);
            }
        }
    }
    
    /**
     * Tối ưu animation performance
     */
    public void optimizeAnimation(Animation animation) {
        // Giảm duration cho animations
        if (animation instanceof FadeTransition) {
            FadeTransition fade = (FadeTransition) animation;
            if (fade.getDuration().toMillis() > 300) {
                fade.setDuration(Duration.millis(300));
            }
        } else if (animation instanceof ScaleTransition) {
            ScaleTransition scale = (ScaleTransition) animation;
            if (scale.getDuration().toMillis() > 200) {
                scale.setDuration(Duration.millis(200));
            }
        }
    }
    
    /**
     * Lazy loading cho large lists
     */
    public <T> java.util.List<T> createLazyList(java.util.List<T> fullList, int pageSize) {
        return new java.util.AbstractList<T>() {
            private int loadedSize = 0;
            
            @Override
            public T get(int index) {
                if (index >= loadedSize) {
                    loadedSize = Math.min(index + pageSize, fullList.size());
                }
                return fullList.get(index);
            }
            
            @Override
            public int size() {
                return fullList.size();
            }
        };
    }
    
    /**
     * Memory cleanup
     */
    public void cleanup() {
        System.gc();
    }
    
    /**
     * Shutdown executors
     */
    public void shutdown() {
        if (backgroundExecutor != null && !backgroundExecutor.isShutdown()) {
            backgroundExecutor.shutdown();
        }
        if (uiExecutor != null && !uiExecutor.isShutdown()) {
            uiExecutor.shutdown();
        }
    }
    
    /**
     * Performance monitoring
     */
    public static class PerformanceMonitor {
        private final long startTime;
        private final String operation;
        
        public PerformanceMonitor(String operation) {
            this.operation = operation;
            this.startTime = System.currentTimeMillis();
        }
        
        public void end() {
            long duration = System.currentTimeMillis() - startTime;
            if (duration > 100) { // Log slow operations
                System.out.println("Performance Warning: " + operation + " took " + duration + "ms");
            }
        }
    }
} 