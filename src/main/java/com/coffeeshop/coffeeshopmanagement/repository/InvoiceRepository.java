package com.coffeeshop.coffeeshopmanagement.repository;

import com.coffeeshop.coffeeshopmanagement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
}
