package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    boolean existsByInvoiceNumber(String invoiceNumber);

}