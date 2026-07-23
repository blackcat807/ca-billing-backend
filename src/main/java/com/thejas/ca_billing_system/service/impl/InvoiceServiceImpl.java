package com.thejas.ca_billing_system.service.impl;

import com.thejas.ca_billing_system.dto.InvoiceRequest;
import com.thejas.ca_billing_system.dto.InvoiceResponse;
import com.thejas.ca_billing_system.entity.Client;
import com.thejas.ca_billing_system.entity.Invoice;
import com.thejas.ca_billing_system.enums.InvoiceStatus;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.ClientRepository;
import com.thejas.ca_billing_system.repository.InvoiceRepository;
import com.thejas.ca_billing_system.service.InvoiceService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final ClientRepository clientRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              ClientRepository clientRepository) {
        this.invoiceRepository = invoiceRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    public InvoiceResponse createInvoice(InvoiceRequest request) {

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with id : "
                                + request.getClientId()));

        Invoice invoice = new Invoice();

        invoice.setClient(client);
        invoice.setInvoiceDate(request.getInvoiceDate());

        invoice.setInvoiceNumber(generateInvoiceNumber());

        invoice.setSubtotal(java.math.BigDecimal.ZERO);
        invoice.setGstAmount(java.math.BigDecimal.ZERO);
        invoice.setTotalAmount(java.math.BigDecimal.ZERO);

        invoice.setStatus(InvoiceStatus.PENDING);

        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice);
    }

    @Override
    public List<InvoiceResponse> getAllInvoices() {
        return invoiceRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public InvoiceResponse getInvoiceById(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with id : "
                                + invoiceId));

        return mapToResponse(invoice);
    }

    @Override
    public InvoiceResponse updateInvoice(Long invoiceId,
                                         InvoiceRequest request) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with id : "
                                + invoiceId));

        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Client not found with id : "
                                + request.getClientId()));

        invoice.setClient(client);
        invoice.setInvoiceDate(request.getInvoiceDate());

        Invoice updatedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(updatedInvoice);
    }

    @Override
    public void deleteInvoice(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with id : "
                                + invoiceId));

        invoiceRepository.delete(invoice);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {

        InvoiceResponse response = new InvoiceResponse();

        response.setInvoiceId(invoice.getInvoiceId());
        response.setInvoiceNumber(invoice.getInvoiceNumber());
        response.setClientName(invoice.getClient().getClientName());
        response.setInvoiceDate(invoice.getInvoiceDate());
        response.setSubtotal(invoice.getSubtotal());
        response.setGstAmount(invoice.getGstAmount());
        response.setTotalAmount(invoice.getTotalAmount());
        response.setStatus(invoice.getStatus());

        return response;
    }

    private String generateInvoiceNumber() {

        long count = invoiceRepository.count() + 1;

        return String.format("INV-%04d", count);
    }
}