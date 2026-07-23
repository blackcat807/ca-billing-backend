package com.thejas.ca_billing_system.controller;

import com.thejas.ca_billing_system.dto.InvoiceItemRequest;
import com.thejas.ca_billing_system.dto.InvoiceItemResponse;
import com.thejas.ca_billing_system.service.InvoiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoice-items")
@RequiredArgsConstructor
public class InvoiceItemController {

    private final InvoiceItemService invoiceItemService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InvoiceItemResponse addInvoiceItem(@RequestBody InvoiceItemRequest request) {
        return invoiceItemService.addInvoiceItem(request);
    }

    @GetMapping("/invoice/{invoiceId}")
    public List<InvoiceItemResponse> getItemsByInvoice(@PathVariable Long invoiceId) {
        return invoiceItemService.getItemsByInvoice(invoiceId);
    }

    @PutMapping("/{itemId}")
    public InvoiceItemResponse updateInvoiceItem(
            @PathVariable Long itemId,
            @RequestBody InvoiceItemRequest request) {

        return invoiceItemService.updateInvoiceItem(itemId, request);
    }

    @DeleteMapping("/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteInvoiceItem(@PathVariable Long itemId) {
        invoiceItemService.deleteInvoiceItem(itemId);
    }
}