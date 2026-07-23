package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.InvoiceItemRequest;
import com.thejas.ca_billing_system.dto.InvoiceItemResponse;
import com.thejas.ca_billing_system.entity.Invoice;
import com.thejas.ca_billing_system.entity.InvoiceItem;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.InvoiceItemRepository;
import com.thejas.ca_billing_system.repository.InvoiceRepository;
import com.thejas.ca_billing_system.service.InvoiceItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceItemServiceImpl implements InvoiceItemService {

    private final InvoiceItemRepository invoiceItemRepository;
    private final InvoiceRepository invoiceRepository;

    @Override
    public InvoiceItemResponse addInvoiceItem(InvoiceItemRequest request) {

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with ID: " + request.getInvoiceId()));

        InvoiceItem item = new InvoiceItem();

        item.setInvoice(invoice);
        item.setServiceName(request.getServiceName());
        item.setQuantity(request.getQuantity());
        item.setRate(request.getRate());
        item.setGstPercentage(request.getGstPercentage());

        BigDecimal amount = request.getRate()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        item.setAmount(amount);

        InvoiceItem savedItem = invoiceItemRepository.save(item);

        recalculateInvoiceTotals(invoice);

        return mapToResponse(savedItem);
    }
    @Override
    public List<InvoiceItemResponse> getItemsByInvoice(Long invoiceId) {

        invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceInvoiceId(invoiceId);

        return items.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceItemResponse updateInvoiceItem(Long itemId, InvoiceItemRequest request) {

        InvoiceItem item = invoiceItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice Item not found with ID: " + itemId));

        Invoice invoice = invoiceRepository.findById(request.getInvoiceId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with ID: " + request.getInvoiceId()));

        item.setInvoice(invoice);
        item.setServiceName(request.getServiceName());
        item.setQuantity(request.getQuantity());
        item.setRate(request.getRate());
        item.setGstPercentage(request.getGstPercentage());

        BigDecimal amount = request.getRate()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        item.setAmount(amount);

        InvoiceItem updatedItem = invoiceItemRepository.save(item);

        recalculateInvoiceTotals(invoice);

        return mapToResponse(updatedItem);
    }
    @Override
    public void deleteInvoiceItem(Long itemId) {

        InvoiceItem item = invoiceItemRepository.findById(itemId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice Item not found with ID: " + itemId));

        Invoice invoice = item.getInvoice();

        invoiceItemRepository.delete(item);

        recalculateInvoiceTotals(invoice);
    }

    private InvoiceItemResponse mapToResponse(InvoiceItem item) {

        InvoiceItemResponse response = new InvoiceItemResponse();

        response.setItemId(item.getItemId());
        response.setServiceName(item.getServiceName());
        response.setQuantity(item.getQuantity());
        response.setRate(item.getRate());
        response.setGstPercentage(item.getGstPercentage());
        response.setAmount(item.getAmount());

        return response;
    }
    private void recalculateInvoiceTotals(Invoice invoice) {

        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceInvoiceId(invoice.getInvoiceId());

        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal gstAmount = BigDecimal.ZERO;

        for (InvoiceItem item : items) {

            subtotal = subtotal.add(item.getAmount());

            BigDecimal itemGst = item.getAmount()
                    .multiply(item.getGstPercentage())
                    .divide(BigDecimal.valueOf(100));

            gstAmount = gstAmount.add(itemGst);
        }

        BigDecimal totalAmount = subtotal.add(gstAmount);

        invoice.setSubtotal(subtotal);
        invoice.setGstAmount(gstAmount);
        invoice.setTotalAmount(totalAmount);

        invoiceRepository.save(invoice);
    }
}