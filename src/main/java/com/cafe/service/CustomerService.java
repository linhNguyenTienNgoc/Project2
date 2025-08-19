package com.cafe.service;

import com.cafe.config.DatabaseConfig;
import com.cafe.dao.base.CustomerDAO;
import com.cafe.dao.base.CustomerDAOImpl;
import com.cafe.model.entity.Customer;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Customer Service - Complete Customer Management System
 * Xử lý tất cả các nghiệp vụ liên quan đến khách hàng
 * 
 * @author Team 2_C2406L
 * @version 1.0.0
 */
public class CustomerService {
    
    public CustomerService() {
        // Connections will be managed per operation using try-with-resources
    }

    /**
     * Lấy tất cả khách hàng
     */
    public List<Customer> getAllCustomers() {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.getAllCustomers();
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy khách hàng theo ID
     */
    public Optional<Customer> getCustomerById(int customerId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            Customer customer = customerDAO.getCustomerById(customerId);
            return Optional.ofNullable(customer);
        } catch (Exception e) {
            System.err.println("Error loading customer " + customerId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Tìm khách hàng theo số điện thoại
     */
    public Optional<Customer> findCustomerByPhone(String phone) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            List<Customer> customers = customerDAO.getAllCustomers();
            return customers.stream()
                    .filter(c -> phone.equals(c.getPhone()))
                    .findFirst();
        } catch (Exception e) {
            System.err.println("Error searching customer by phone: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Tạo khách hàng mới
     */
    public boolean createCustomer(Customer customer) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.insertCustomer(customer);
        } catch (Exception e) {
            System.err.println("Error creating customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật thông tin khách hàng
     */
    public boolean updateCustomer(Customer customer) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.updateCustomer(customer);
        } catch (Exception e) {
            System.err.println("Error updating customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Xóa khách hàng
     */
    public boolean deleteCustomer(int customerId) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.deleteCustomer(customerId);
        } catch (Exception e) {
            System.err.println("Error deleting customer: " + e.getMessage());
            return false;
        }
    }

    /**
     * Cập nhật điểm tích lũy
     */
    public boolean updateLoyaltyPoints(int customerId, int points) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            Customer customer = customerDAO.getCustomerById(customerId);
            if (customer != null) {
                customer.setLoyaltyPoints(customer.getLoyaltyPoints() + points);
                return customerDAO.updateCustomer(customer);
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error updating loyalty points: " + e.getMessage());
            return false;
        }
    }

    /**
     * Tìm kiếm khách hàng theo tên
     */
    public List<Customer> searchCustomersByName(String name) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.getAllCustomers().stream()
                    .filter(c -> c.getFullName().toLowerCase().contains(name.toLowerCase()))
                    .toList();
        } catch (Exception e) {
            System.err.println("Error searching customers by name: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Lấy top khách hàng VIP (theo điểm tích lũy)
     */
    public List<Customer> getVIPCustomers(int limit) {
        try (Connection conn = DatabaseConfig.getConnection()) {
            CustomerDAO customerDAO = new CustomerDAOImpl(conn);
            return customerDAO.getAllCustomers().stream()
                    .sorted((c1, c2) -> Integer.compare(c2.getLoyaltyPoints(), c1.getLoyaltyPoints()))
                    .limit(limit)
                    .toList();
        } catch (Exception e) {
            System.err.println("Error loading VIP customers: " + e.getMessage());
            return List.of();
        }
    }
}


