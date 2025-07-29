package com.coffeeshop.coffeeshopmanagement.service;

import com.coffeeshop.coffeeshopmanagement.entity.InvoiceDetail;
import java.util.List;
import java.util.Optional;

public interface InvoiceDetailService {
    List<InvoiceDetail> getAllInvoiceDetails();
    Optional<InvoiceDetail> getInvoiceDetailById(Integer id);
    InvoiceDetail saveInvoiceDetail(InvoiceDetail invoiceDetail);
    void deleteInvoiceDetail(Integer id);
    List<InvoiceDetail> getInvoiceDetailsByInvoiceId(Integer invoiceId);
    List<InvoiceDetail> getInvoiceDetailsByMenuId(Integer menuId);
} 