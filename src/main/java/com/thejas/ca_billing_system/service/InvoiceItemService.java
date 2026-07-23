package com.thejas.ca_billing_system.service;

import com.thejas.ca_billing_system.dto.InvoiceItemRequest;
import com.thejas.ca_billing_system.dto.InvoiceItemResponse;

import java.util.List;

public interface InvoiceItemService {

    InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request);

    List<InvoiceItemResponse> getItemsByInvoice(Long invoiceId);

    InvoiceItemResponse updateInvoiceItem(Long itemId, InvoiceItemRequest request);

    void deleteInvoiceItem(Long itemId);

}