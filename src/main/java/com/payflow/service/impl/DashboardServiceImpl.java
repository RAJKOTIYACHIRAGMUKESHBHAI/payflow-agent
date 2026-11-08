package com.payflow.service.impl;

import com.payflow.dto.response.DashboardSummaryResponse;
import com.payflow.entity.Invoice;
import com.payflow.repository.InvoiceRepository;
import com.payflow.service.DashboardService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final InvoiceRepository invoiceRepository;

    public DashboardServiceImpl(InvoiceRepository invoiceRepository) {
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public DashboardSummaryResponse getSummary(String currencyFilter) {
        String targetCurrency = (currencyFilter != null && !currencyFilter.isBlank()) ? currencyFilter.toUpperCase() : "USD";
        List<Invoice> invoices = invoiceRepository.findAll();

        BigDecimal outstanding = BigDecimal.ZERO;
        BigDecimal overdue = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;

        long total = 0;
        long draft = 0;
        long sent = 0;
        long paidCnt = 0;
        long overdueCnt = 0;

        LocalDate today = LocalDate.now();

        for (Invoice inv : invoices) {
            if (!targetCurrency.equalsIgnoreCase(inv.getCurrency())) {
                continue;
            }

            total++;
            String status = inv.getStatus() != null ? inv.getStatus().toUpperCase() : "";

            if ("DRAFT".equals(status)) {
                draft++;
            } else if ("SENT".equals(status)) {
                sent++;
                outstanding = outstanding.add(inv.getAmount());
                if (inv.getDueDate() != null && inv.getDueDate().isBefore(today)) {
                    overdueCnt++;
                    overdue = overdue.add(inv.getAmount());
                }
            } else if ("PAID".equals(status)) {
                paidCnt++;
                paid = paid.add(inv.getAmount());
            }
        }

        return new DashboardSummaryResponse(
                targetCurrency,
                outstanding,
                overdue,
                paid,
                total,
                draft,
                sent,
                paidCnt,
                overdueCnt
        );
    }
}
