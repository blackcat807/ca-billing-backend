package com.thejas.ca_billing_system.repository;

import com.thejas.ca_billing_system.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByClientUserUsername(String username);
    Optional<Invoice> findByInvoiceIdAndClientUserUsername(Long id, String username);

    @Query("SELECT MAX(i.invoiceId) FROM Invoice i WHERE i.client.user.username = :username")
    Long findMaxInvoiceIdByUsername(String username);
}
