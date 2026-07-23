package com.thejas.ca_billing_system.dto;

import com.thejas.ca_billing_system.enums.InvoiceStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceResponse {

    private Long invoiceId;

    private String invoiceNumber;

    private String clientName;

    private LocalDate invoiceDate;

    private BigDecimal subtotal;

    private BigDecimal gstAmount;

    private BigDecimal totalAmount;

    private InvoiceStatus status;

}