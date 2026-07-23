package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.InvoiceItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemRepository extends JpaRepository<InvoiceItem, Long> {

    List<InvoiceItem> findByInvoiceInvoiceId(Long invoiceId);

}