package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.InvoiceRequest;
import com.thejas.ca_billing_system.dto.InvoiceResponse;

import java.util.List;

public interface InvoiceService {

    InvoiceResponse createInvoice(InvoiceRequest request);

    List<InvoiceResponse> getAllInvoices();

    InvoiceResponse getInvoiceById(Long invoiceId);

    InvoiceResponse updateInvoice(Long invoiceId, InvoiceRequest request);

    void deleteInvoice(Long invoiceId);

}