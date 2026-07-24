package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.InvoiceRequest;
import com.thejas.ca_billing_system.dto.InvoiceResponse;
import com.thejas.ca_billing_system.entity.Client;
import com.thejas.ca_billing_system.entity.Invoice;
import com.thejas.ca_billing_system.enums.InvoiceStatus;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ClientRepository;
import com.thejas.ca_billing_system.repository.InvoiceRepository;
import com.thejas.ca_billing_system.security.SecurityHelper;
import com.thejas.ca_billing_system.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository  clientRepository;
    private final SecurityHelper    securityHelper;

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        String username = securityHelper.currentUsername();

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found: " + request.getClientId()));

        if (!client.getUser().getUsername().equals(username))
            throw new ResourceNotFoundException("Client not found: " + request.getClientId());

        Invoice invoice = new Invoice();
        invoice.setClient(client);
        invoice.setInvoiceDate(request.getInvoiceDate());
        invoice.setInvoiceNumber(generateInvoiceNumber(username));
        invoice.setSubtotal(BigDecimal.ZERO);
        invoice.setGstAmount(BigDecimal.ZERO);
        invoice.setTotalAmount(BigDecimal.ZERO);
        invoice.setStatus(InvoiceStatus.PENDING);

        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository
                .findByClientUserUsername(securityHelper.currentUsername())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Long invoiceId) {
        return mapToResponse(findOwnedInvoice(invoiceId));
    }

    @Override
    public InvoiceResponse updateInvoice(Long invoiceId, InvoiceRequest request) {
        Invoice invoice = findOwnedInvoice(invoiceId);
        String  username = securityHelper.currentUsername();

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Client not found: " + request.getClientId()));

        if (!client.getUser().getUsername().equals(username))
            throw new ResourceNotFoundException("Client not found: " + request.getClientId());

        invoice.setClient(client);
        invoice.setInvoiceDate(request.getInvoiceDate());
        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    public InvoiceResponse updateStatus(Long invoiceId, InvoiceStatus status) {
        Invoice invoice = findOwnedInvoice(invoiceId);
        invoice.setStatus(status);
        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    public void deleteInvoice(Long invoiceId) {
        invoiceRepository.delete(findOwnedInvoice(invoiceId));
    }

    private Invoice findOwnedInvoice(Long invoiceId) {
        String username = securityHelper.currentUsername();
        return invoiceRepository.findByInvoiceIdAndClientUserUsername(invoiceId, username)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Invoice not found: " + invoiceId));
    }

    private InvoiceResponse mapToResponse(Invoice i) {
        InvoiceResponse r = new InvoiceResponse();
        r.setInvoiceId(i.getInvoiceId());
        r.setInvoiceNumber(i.getInvoiceNumber());
        r.setClientName(i.getClient().getClientName());
        r.setInvoiceDate(i.getInvoiceDate());
        r.setSubtotal(i.getSubtotal());
        r.setGstAmount(i.getGstAmount());
        r.setTotalAmount(i.getTotalAmount());
        r.setStatus(i.getStatus());
        return r;
    }

    private String generateInvoiceNumber(String username) {
        Long maxId = invoiceRepository.findMaxInvoiceIdByUsername(username);
        long next  = (maxId != null ? maxId : 0L) + 1;
        return String.format("%04d", next);
    }
}
