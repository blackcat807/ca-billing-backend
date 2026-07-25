package com.thejas.ca_billing_system.service.impl;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.thejas.ca_billing_system.entity.Invoice;
import com.thejas.ca_billing_system.entity.InvoiceItem;
import com.thejas.ca_billing_system.entity.PracticeProfile;
import com.thejas.ca_billing_system.exception.ResourceNotFoundException;
import com.thejas.ca_billing_system.repository.InvoiceItemRepository;
import com.thejas.ca_billing_system.repository.InvoiceRepository;
import com.thejas.ca_billing_system.repository.PracticeProfileRepository;
import com.thejas.ca_billing_system.security.SecurityHelper;
import com.thejas.ca_billing_system.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private static final String RS = "Rs.";
    private static final Color BLACK      = Color.BLACK;
    private static final Color LIGHT_GREY = new Color(230, 230, 230);
    private static final Color MID_GREY   = new Color(160, 160, 160);

    private final InvoiceRepository         invoiceRepository;
    private final InvoiceItemRepository     invoiceItemRepository;
    private final PracticeProfileRepository practiceProfileRepository;
    private final SecurityHelper            securityHelper;

    private BaseFont bf, bfBold, bfItalic;
    private Font fName, fQual, fAddr, fBillHd, fBillSub, fMs, fThHead, fTbBody,
            fTotal, fWords, fLbl, fBank, fBankLbl, fSig, fSigLbl, fCopyLbl;

    @Override
    public ByteArrayInputStream generateInvoicePdf(Long invoiceId) {

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found: " + invoiceId));

        List<InvoiceItem> items = invoiceItemRepository.findByInvoiceInvoiceId(invoiceId);

        PracticeProfile profile = practiceProfileRepository
                .findByUserId(securityHelper.currentUser().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Set up your Practice Profile before generating invoices."));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Document doc = new Document(PageSize.A4, 30, 30, 25, 25);

        try {
            PdfWriter.getInstance(doc, out);
            doc.open();

            bf       = BaseFont.createFont(BaseFont.HELVETICA,         BaseFont.CP1252, false);
            bfBold   = BaseFont.createFont(BaseFont.HELVETICA_BOLD,    BaseFont.CP1252, false);
            bfItalic = BaseFont.createFont(BaseFont.HELVETICA_OBLIQUE, BaseFont.CP1252, false);

            fName    = new Font(bfBold,    15, Font.NORMAL, BLACK);
            fQual    = new Font(bf,          9, Font.NORMAL, BLACK);
            fAddr    = new Font(bf,          8, Font.NORMAL, BLACK);
            fBillHd  = new Font(bfBold,     9, Font.NORMAL, BLACK);
            fBillSub = new Font(bf,          8, Font.NORMAL, BLACK);
            fMs      = new Font(bfBold,     9, Font.NORMAL, BLACK);
            fThHead  = new Font(bfBold,     8, Font.NORMAL, BLACK);
            fTbBody  = new Font(bf,          8, Font.NORMAL, BLACK);
            fTotal   = new Font(bfBold,     9, Font.NORMAL, BLACK);
            fWords   = new Font(bfBold,     8, Font.NORMAL, BLACK);
            fLbl     = new Font(bf,          7, Font.NORMAL, BLACK);
            fBank    = new Font(bf,          7, Font.NORMAL, BLACK);
            fBankLbl = new Font(bfBold,     7, Font.NORMAL, BLACK);
            fSig     = new Font(bfItalic,   8, Font.NORMAL, BLACK);
            fSigLbl  = new Font(bf,          7, Font.NORMAL, BLACK);
            fCopyLbl = new Font(bfItalic,   8, Font.NORMAL, MID_GREY);

            PdfPTable page = new PdfPTable(1);
            page.setWidthPercentage(100);

            PdfPCell copy1 = new PdfPCell(buildInvoiceBlock(invoice, items, profile, "Original Copy"));
            copy1.setBorder(Rectangle.NO_BORDER);
            copy1.setPadding(0f);
            page.addCell(copy1);

            PdfPCell dividerCell = new PdfPCell(new Phrase(" "));
            dividerCell.setBorder(Rectangle.TOP);
            dividerCell.setBorderColor(MID_GREY);
            dividerCell.setBorderWidthTop(1f);
            dividerCell.setFixedHeight(18f);
            page.addCell(dividerCell);

            PdfPCell copy2 = new PdfPCell(buildInvoiceBlock(invoice, items, profile, "Duplicate Copy"));
            copy2.setBorder(Rectangle.NO_BORDER);
            copy2.setPadding(0f);
            page.addCell(copy2);

            doc.add(page);
            doc.close();

        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private PdfPTable buildInvoiceBlock(Invoice invoice, List<InvoiceItem> items,
                                        PracticeProfile profile, String copyLabel) {

        PdfPTable wrapper = new PdfPTable(1);
        wrapper.setWidthPercentage(100);

        PdfPCell outerCell = new PdfPCell();
        outerCell.setBorder(Rectangle.BOX);
        outerCell.setBorderColor(BLACK);
        outerCell.setBorderWidth(1f);
        outerCell.setPadding(0f);

        Paragraph copyTag = new Paragraph(copyLabel, fCopyLbl);
        copyTag.setAlignment(Element.ALIGN_RIGHT);
        PdfPTable copyTagTable = new PdfPTable(1);
        copyTagTable.setWidthPercentage(100);
        PdfPCell copyTagCell = new PdfPCell(copyTag);
        copyTagCell.setBorder(Rectangle.NO_BORDER);
        copyTagCell.setPadding(4f);
        copyTagCell.setPaddingRight(10f);
        copyTagTable.addCell(copyTagCell);
        outerCell.addElement(copyTagTable);

        PdfPTable hdr = new PdfPTable(2);
        hdr.setWidthPercentage(100);
        hdr.setWidths(new float[]{ 60f, 40f });

        PdfPCell lc = new PdfPCell();
        lc.setBorder(Rectangle.TOP | Rectangle.BOTTOM | Rectangle.RIGHT);
        lc.setBorderColor(BLACK);
        lc.setPadding(10f);
        lc.addElement(new Paragraph(profile.getFirmName(), fName));
        lc.addElement(gap(2f));
        if (notBlank(profile.getAddressLine1()))
            lc.addElement(new Paragraph(profile.getAddressLine1(), fAddr));
        if (notBlank(profile.getAddressLine2()))
            lc.addElement(new Paragraph(profile.getAddressLine2(), fAddr));
        if (notBlank(profile.getPhone()))
            lc.addElement(new Paragraph("Phone : " + profile.getPhone(), fAddr));
        if (notBlank(profile.getEmail()))
            lc.addElement(new Paragraph("Email : " + profile.getEmail(), fAddr));
        hdr.addCell(lc);

        PdfPCell rc = new PdfPCell();
        rc.setBorder(Rectangle.TOP | Rectangle.BOTTOM);
        rc.setBorderColor(BLACK);
        rc.setPadding(10f);
        rc.setHorizontalAlignment(Element.ALIGN_CENTER);
        rc.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph pBill = new Paragraph("BILL", fBillHd);
        pBill.setAlignment(Element.ALIGN_CENTER);
        rc.addElement(pBill);
        rc.addElement(gap(4f));

        String rawNum = invoice.getInvoiceNumber() != null
                ? invoice.getInvoiceNumber().replace("INV-", "") : "";
        Paragraph pNo = new Paragraph("No.   " + rawNum, fBillSub);
        pNo.setAlignment(Element.ALIGN_CENTER);
        rc.addElement(pNo);

        String dateStr = formatDate(invoice.getInvoiceDate() != null
                ? invoice.getInvoiceDate().toString() : "");
        Paragraph pDate = new Paragraph("Date.   " + dateStr, fBillSub);
        pDate.setAlignment(Element.ALIGN_CENTER);
        rc.addElement(pDate);

        hdr.addCell(rc);
        outerCell.addElement(hdr);

        PdfPTable msTable = new PdfPTable(1);
        msTable.setWidthPercentage(100);
        PdfPCell msCell = new PdfPCell();
        msCell.setBorder(Rectangle.BOTTOM);
        msCell.setBorderColor(BLACK);
        msCell.setPadding(10f);
        msCell.addElement(new Paragraph("M/s.   " + invoice.getClient().getClientName(), fMs));
        msTable.addCell(msCell);
        outerCell.addElement(msTable);

        if (!items.isEmpty()) {
            PdfPTable table = new PdfPTable(2);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{ 75f, 25f });
            table.setHeaderRows(1);

            PdfPCell th1 = headerCell("DESCRIPTION OF SERVICE", fThHead, Element.ALIGN_LEFT);
            PdfPCell th2 = headerCell("AMOUNT", fThHead, Element.ALIGN_RIGHT);
            table.addCell(th1);
            table.addCell(th2);

            int num = 1;
            for (InvoiceItem item : items) {
                String label  = num++ + ".   " + item.getServiceName();
                String amount = RS + "  " + formatAmount(item.getAmount());
                table.addCell(bodyCell(label,  fTbBody, Element.ALIGN_LEFT));
                table.addCell(bodyCell(amount, fTbBody, Element.ALIGN_RIGHT));
            }
            outerCell.addElement(table);
        } else {
            PdfPTable emptyTable = new PdfPTable(1);
            emptyTable.setWidthPercentage(100);
            PdfPCell emptyCell = new PdfPCell(new Phrase("No services listed", fTbBody));
            emptyCell.setBorder(Rectangle.BOX);
            emptyCell.setBorderColor(MID_GREY);
            emptyCell.setPadding(10f);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            emptyTable.addCell(emptyCell);
            outerCell.addElement(emptyTable);
        }

        BigDecimal total = invoice.getTotalAmount() != null
                ? invoice.getTotalAmount() : BigDecimal.ZERO;
        String totalStr = RS + "  " + formatAmount(total);
        String inWords  = toWords(total) + " Only";

        PdfPTable totals = new PdfPTable(2);
        totals.setWidthPercentage(100);
        totals.setWidths(new float[]{ 58f, 42f });

        PdfPCell wordsCell = new PdfPCell();
        wordsCell.setBorder(Rectangle.BOX);
        wordsCell.setBorderColor(BLACK);
        wordsCell.setPadding(10f);
        wordsCell.addElement(new Paragraph("Rs. in Words :", fLbl));
        wordsCell.addElement(gap(2f));
        wordsCell.addElement(new Paragraph(inWords, fWords));
        totals.addCell(wordsCell);

        PdfPCell totalCell = new PdfPCell();
        totalCell.setBorder(Rectangle.BOX);
        totalCell.setBorderColor(BLACK);
        totalCell.setPadding(10f);
        totalCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        Paragraph pTotal = new Paragraph("TOTAL :   " + totalStr, fTotal);
        pTotal.setAlignment(Element.ALIGN_RIGHT);
        totalCell.addElement(pTotal);
        totals.addCell(totalCell);

        outerCell.addElement(totals);

        PdfPTable bankTable = new PdfPTable(1);
        bankTable.setWidthPercentage(100);

        PdfPCell bankOuter = new PdfPCell();
        bankOuter.setBorder(Rectangle.BOX);
        bankOuter.setBorderColor(BLACK);
        bankOuter.setPadding(10f);

        bankOuter.addElement(new Paragraph("Bank Account Details", fBankLbl));
        bankOuter.addElement(gap(3f));

        PdfPTable bankGrid = new PdfPTable(1);
        bankGrid.setWidthPercentage(100);
        bankGrid.addCell(bankDetailCell("Account Name", nz(profile.getBankAccountName()), fBankLbl, fBank));
        bankOuter.addElement(bankGrid);

        bankTable.addCell(bankOuter);
        outerCell.addElement(bankTable);

        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{ 50f, 50f });

        PdfPCell leftBlank = new PdfPCell(new Phrase(" "));
        leftBlank.setBorder(Rectangle.NO_BORDER);
        leftBlank.setMinimumHeight(55f);
        sigTable.addCell(leftBlank);

        PdfPCell sigCell = new PdfPCell();
        sigCell.setBorder(Rectangle.BOX);
        sigCell.setBorderColor(BLACK);
        sigCell.setPadding(10f);
        sigCell.setMinimumHeight(55f);
        sigCell.setVerticalAlignment(Element.ALIGN_BOTTOM);

        Paragraph sigLabel = new Paragraph("Authorised Signatory", fSigLbl);
        sigLabel.setAlignment(Element.ALIGN_CENTER);
        sigCell.addElement(sigLabel);

        sigTable.addCell(sigCell);
        outerCell.addElement(sigTable);

        wrapper.addCell(outerCell);
        return wrapper;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }

    private String nz(String s) {
        return s == null ? "" : s;
    }

    private PdfPCell headerCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(LIGHT_GREY);
        cell.setPadding(5f);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(BLACK);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private PdfPCell bodyCell(String text, Font font, int align) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5f);
        cell.setBorder(Rectangle.BOX);
        cell.setBorderColor(MID_GREY);
        cell.setHorizontalAlignment(align);
        return cell;
    }

    private PdfPCell bankDetailCell(String label, String value, Font labelFont, Font valFont) {
        PdfPCell cell = new PdfPCell();
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        cell.addElement(new Paragraph(label, labelFont));
        cell.addElement(new Paragraph(value, valFont));
        return cell;
    }

    private Paragraph gap(float height) {
        Paragraph p = new Paragraph(" ");
        p.setLeading(height);
        return p;
    }

    private String formatDate(String iso) {
        try {
            String[] p = iso.split("-");
            return String.format("%02d/%02d/%s",
                    Integer.parseInt(p[2]), Integer.parseInt(p[1]), p[0]);
        } catch (Exception e) { return iso; }
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%.2f", amount);
    }

    private static final String[] ONES = {
            "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
            "Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen",
            "Seventeen", "Eighteen", "Nineteen"
    };
    private static final String[] TENS = {
            "", "", "Twenty", "Thirty", "Forty", "Fifty",
            "Sixty", "Seventy", "Eighty", "Ninety"
    };

    private String toWords(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) return "Zero Rupees";
        long rupees = amount.longValue();
        int  paise  = amount.remainder(BigDecimal.ONE)
                .multiply(new BigDecimal(100)).intValue();
        StringBuilder sb = new StringBuilder(convertIndian(rupees)).append(" Rupees");
        if (paise > 0) sb.append(" and ").append(convertBelow100(paise)).append(" Paise");
        return sb.toString();
    }

    private String convertIndian(long n) {
        if (n == 0) return "Zero";
        StringBuilder r = new StringBuilder();
        long cr = n / 10_000_000; n %= 10_000_000;
        long lk = n / 100_000;   n %= 100_000;
        long th = n / 1_000;     n %= 1_000;
        long hu = n / 100;       n %= 100;
        if (cr > 0) r.append(convertBelow100((int) cr)).append(" Crore ");
        if (lk > 0) r.append(convertBelow100((int) lk)).append(" Lakh ");
        if (th > 0) r.append(convertBelow100((int) th)).append(" Thousand ");
        if (hu > 0) r.append(ONES[(int) hu]).append(" Hundred ");
        if (n  > 0) r.append(convertBelow100((int) n));
        return r.toString().trim();
    }

    private String convertBelow100(int n) {
        if (n < 20) return ONES[n];
        return TENS[n / 10] + (n % 10 != 0 ? " " + ONES[n % 10] : "");
    }
}