package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<InputStreamResource> generateInvoicePdf(
            @PathVariable Long invoiceId) {

        InputStreamResource file =
                new InputStreamResource(pdfService.generateInvoicePdf(invoiceId));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=invoice_" + invoiceId + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}