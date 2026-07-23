package com.thejas.ca_billing_system.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceItemResponse {

    private Long itemId;

    private String serviceName;

    private Integer quantity;

    private BigDecimal rate;

    private BigDecimal gstPercentage;

    private BigDecimal amount;

}