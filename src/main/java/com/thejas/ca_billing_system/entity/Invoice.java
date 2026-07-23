package com.thejas.ca_billing_system.entity;
import com.thejas.ca_billing_system.enums.InvoiceStatus;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long invoiceId;
    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)

    private Client client;

    private String invoiceNumber;

    private LocalDate invoiceDate;

    private BigDecimal subtotal;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;

    private InvoiceStatus status;

}