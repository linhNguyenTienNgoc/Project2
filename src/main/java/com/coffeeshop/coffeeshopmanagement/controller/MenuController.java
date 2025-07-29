package com.coffeeshop.coffeeshopmanagement.controller;

import com.coffeeshop.coffeeshopmanagement.entity.Employee;
import com.coffeeshop.coffeeshopmanagement.entity.Menu;
import com.coffeeshop.coffeeshopmanagement.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/menu")
public class MenuController {
    
    @Autowired
    private MenuService menuService;

    @GetMapping
    public String listMenu(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<Menu> menuItems = menuService.getAllMenuItems();
        model.addAttribute("menuItems", menuItems);
        model.addAttribute("currentEmployee", currentEmployee);
        return "menu/list";
    }

    @GetMapping("/search")
    public String searchMenu(@RequestParam(required = false) String keyword,
                           HttpSession session,
                           Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null) {
            return "redirect:/login";
        }

        List<Menu> menuItems;
        if (keyword != null && !keyword.trim().isEmpty()) {
            menuItems = menuService.searchMenuItems(keyword);
        } else {
            menuItems = menuService.getAllMenuItems();
        }

        model.addAttribute("menuItems", menuItems);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentEmployee", currentEmployee);
        return "menu/list";
    }

    @GetMapping("/add")
    public String addMenuItemForm(HttpSession session, Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"ADMIN".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        model.addAttribute("menuItem", new Menu());
        model.addAttribute("currentEmployee", currentEmployee);
        return "menu/form";
    }

    @PostMapping("/add")
    public String addMenuItem(@ModelAttribute Menu menuItem,
                            HttpSession session,
                            Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"ADMIN".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        menuService.saveMenuItem(menuItem);
        return "redirect:/menu";
    }

    @GetMapping("/edit/{id}")
    public String editMenuItemForm(@PathVariable Integer id,
                                 HttpSession session,
                                 Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"ADMIN".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        Menu menuItem = menuService.getMenuItemById(id).orElse(null);
        if (menuItem == null) {
            return "redirect:/menu";
        }

        model.addAttribute("menuItem", menuItem);
        model.addAttribute("currentEmployee", currentEmployee);
        return "menu/form";
    }

    @PostMapping("/edit/{id}")
    public String editMenuItem(@PathVariable Integer id,
                             @ModelAttribute Menu menuItem,
                             HttpSession session,
                             Model model) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"ADMIN".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        menuItem.setId(id);
        menuService.saveMenuItem(menuItem);
        return "redirect:/menu";
    }

    @GetMapping("/delete/{id}")
    public String deleteMenuItem(@PathVariable Integer id, HttpSession session) {
        Employee currentEmployee = (Employee) session.getAttribute("employee");
        if (currentEmployee == null || !"ADMIN".equals(currentEmployee.getRole())) {
            return "redirect:/login";
        }

        menuService.deleteMenuItem(id);
        return "redirect:/menu";
    }
} 