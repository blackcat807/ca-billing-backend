package com.thejas.ca_billing_system.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InvoiceRequest {

    private Long clientId;

    private LocalDate invoiceDate;

    private String notes;

}