package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.InvoiceRequest;
import com.thejas.ca_billing_system.dto.InvoiceResponse;
import com.thejas.ca_billing_system.enums.InvoiceStatus;

import java.util.List;

public interface InvoiceService {
    InvoiceResponse createInvoice(InvoiceRequest request);
    List<InvoiceResponse> getAllInvoices();
    InvoiceResponse getInvoiceById(Long invoiceId);
    InvoiceResponse updateInvoice(Long invoiceId, InvoiceRequest request);
    InvoiceResponse updateStatus(Long invoiceId, InvoiceStatus status);
    void deleteInvoice(Long invoiceId);
}
