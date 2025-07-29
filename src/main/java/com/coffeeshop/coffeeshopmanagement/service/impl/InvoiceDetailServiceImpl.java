package com.coffeeshop.coffeeshopmanagement.service.impl;

import com.coffeeshop.coffeeshopmanagement.entity.InvoiceDetail;
import com.coffeeshop.coffeeshopmanagement.repository.InvoiceDetailRepository;
import com.coffeeshop.coffeeshopmanagement.service.InvoiceDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceDetailServiceImpl implements InvoiceDetailService {
    
    @Autowired
    private InvoiceDetailRepository invoiceDetailRepository;

    @Override
    public List<InvoiceDetail> getAllInvoiceDetails() {
        return invoiceDetailRepository.findAll();
    }

    @Override
    public Optional<InvoiceDetail> getInvoiceDetailById(Integer id) {
        return invoiceDetailRepository.findById(id);
    }

    @Override
    public InvoiceDetail saveInvoiceDetail(InvoiceDetail invoiceDetail) {
        return invoiceDetailRepository.save(invoiceDetail);
    }

    @Override
    public void deleteInvoiceDetail(Integer id) {
        invoiceDetailRepository.deleteById(id);
    }

    @Override
    public List<InvoiceDetail> getInvoiceDetailsByInvoiceId(Integer invoiceId) {
        List<InvoiceDetail> allDetails = invoiceDetailRepository.findAll();
        return allDetails.stream()
                .filter(detail -> detail.getInvoice().getId().equals(invoiceId))
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<InvoiceDetail> getInvoiceDetailsByMenuId(Integer menuId) {
        List<InvoiceDetail> allDetails = invoiceDetailRepository.findAll();
        return allDetails.stream()
                .filter(detail -> detail.getMenu().getId().equals(menuId))
                .collect(java.util.stream.Collectors.toList());
    }
} 