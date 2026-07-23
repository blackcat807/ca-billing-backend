package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.InvoiceRequest;
import com.thejas.ca_billing_system.dto.InvoiceResponse;
import com.thejas.ca_billing_system.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createInvoice(
            @RequestBody InvoiceRequest request) {

        return new ResponseEntity<>(
                invoiceService.createInvoice(request),
                HttpStatus.CREATED
        );
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> getAllInvoices() {

        return ResponseEntity.ok(invoiceService.getAllInvoices());
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> getInvoiceById(
            @PathVariable Long invoiceId) {

        return ResponseEntity.ok(
                invoiceService.getInvoiceById(invoiceId)
        );
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceResponse> updateInvoice(
            @PathVariable Long invoiceId,
            @RequestBody InvoiceRequest request) {

        return ResponseEntity.ok(
                invoiceService.updateInvoice(invoiceId, request)
        );
    }

    @DeleteMapping("/{invoiceId}")
    public ResponseEntity<Void> deleteInvoice(
            @PathVariable Long invoiceId) {

        invoiceService.deleteInvoice(invoiceId);

        return ResponseEntity.noContent().build();
    }
}