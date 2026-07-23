package com.thejas.ca_billing_system.service.impl;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.thejas.ca_billing_system.entity.Invoice;
import com.thejas.ca_billing_system.entity.InvoiceItem;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.InvoiceItemRepository;
import com.thejas.ca_billing_system.repository.InvoiceRepository;
import com.thejas.ca_billing_system.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceItemRepository invoiceItemRepository;

    @Override
    public ByteArrayInputStream generateInvoicePdf(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Invoice not found with ID: " + invoiceId));

        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceInvoiceId(invoiceId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4);

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            // ==========================
            // Fonts
            // ==========================

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 11);

            // ==========================
            // Company Name
            // ==========================

            Paragraph title = new Paragraph("BHAT & ASSOCIATES", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph subtitle = new Paragraph("Chartered Accountants", headingFont);
            subtitle.setAlignment(Element.ALIGN_CENTER);
            document.add(subtitle);

            document.add(new Paragraph(" "));

            // ==========================
            // Invoice Details
            // ==========================

            document.add(new Paragraph("Invoice Number : " + invoice.getInvoiceNumber(), normalFont));
            document.add(new Paragraph("Invoice Date   : " + invoice.getInvoiceDate(), normalFont));
            document.add(new Paragraph("Client Name    : " + invoice.getClient().getClientName(), normalFont));

            document.add(new Paragraph(" "));

            // ==========================
            // Table
            // ==========================

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            table.addCell(new PdfPCell(new Phrase("Service")));
            table.addCell(new PdfPCell(new Phrase("Qty")));
            table.addCell(new PdfPCell(new Phrase("Rate")));
            table.addCell(new PdfPCell(new Phrase("Amount")));

            for (InvoiceItem item : items) {

                table.addCell(item.getServiceName());
                table.addCell(String.valueOf(item.getQuantity()));
                table.addCell(item.getRate().toString());
                table.addCell(item.getAmount().toString());

            }

            document.add(table);

            document.add(new Paragraph(" "));

            // ==========================
            // Totals
            // ==========================

            document.add(new Paragraph("Subtotal : ₹ " + invoice.getSubtotal(), headingFont));
            document.add(new Paragraph("GST      : ₹ " + invoice.getGstAmount(), headingFont));
            document.add(new Paragraph("Total    : ₹ " + invoice.getTotalAmount(), headingFont));

            document.add(new Paragraph(" "));

            // ==========================
            // Footer
            // ==========================

            Paragraph thankYou = new Paragraph("Thank you for your business!", headingFont);
            thankYou.setAlignment(Element.ALIGN_CENTER);
            document.add(thankYou);

            document.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}