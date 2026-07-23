package com.thejas.ca_billing_system.service;

import java.io.ByteArrayInputStream;

public interface PdfService {

    ByteArrayInputStream generateInvoicePdf(Long invoiceId);

}