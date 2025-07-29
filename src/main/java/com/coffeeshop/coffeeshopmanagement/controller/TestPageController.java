package com.coffeeshop.coffeeshopmanagement.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestPageController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("message", "Coffee Shop Management System");
        model.addAttribute("status", "Running successfully!");
        return "test/home";
    }

    @GetMapping("/test")
    public String test(Model model) {
        model.addAttribute("message", "Test page working!");
        model.addAttribute("javaVersion", System.getProperty("java.version"));
        model.addAttribute("timestamp", java.time.LocalDateTime.now());
        return "test/test";
    }

    @GetMapping("/simple-menu")
    public String simpleMenu(Model model) {
        model.addAttribute("message", "Simple Menu Page");
        model.addAttribute("items", java.util.Arrays.asList("Coffee", "Tea", "Cake"));
        return "test/simple-menu";
    }
} 