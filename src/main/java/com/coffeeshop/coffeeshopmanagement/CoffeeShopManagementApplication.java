package com.coffeeshop.coffeeshopmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.coffeeshop.coffeeshopmanagement.entity")
@EnableJpaRepositories("com.coffeeshop.coffeeshopmanagement.repository")
public class CoffeeShopManagementApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(CoffeeShopManagementApplication.class, args);
        
        String status = "default";
        if (application.getEnvironment().getActiveProfiles().length == 1) {
            status = application.getEnvironment().getActiveProfiles()[0];
        } else if (application.getEnvironment().getActiveProfiles().length > 1) {
            status = "multiple";
        }
        
        System.out.println("🚀 Coffee Shop Management System started successfully!");
        System.out.println("🌐 Access the application at: http://localhost:8080");
        System.out.println("👤 Login with: admin/admin123 or staff1/staff123");
        System.out.println("📊 Profile: " + status);
        System.out.println("☕ Java Version: " + System.getProperty("java.version"));
    }
} 