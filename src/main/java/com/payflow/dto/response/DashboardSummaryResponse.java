package com.payflow.dto.response;

import java.math.BigDecimal;

public class DashboardSummaryResponse {

    private String currency;
    private BigDecimal totalOutstandingAmount;
    private BigDecimal totalOverdueAmount;
    private BigDecimal totalPaidAmount;
    private long totalInvoices;
    private long draftCount;
    private long sentCount;
    private long paidCount;
    private long overdueCount;

    public DashboardSummaryResponse() {}

    public DashboardSummaryResponse(String currency, BigDecimal totalOutstandingAmount, BigDecimal totalOverdueAmount,
                                    BigDecimal totalPaidAmount, long totalInvoices, long draftCount,
                                    long sentCount, long paidCount, long overdueCount) {
        this.currency = currency;
        this.totalOutstandingAmount = totalOutstandingAmount;
        this.totalOverdueAmount = totalOverdueAmount;
        this.totalPaidAmount = totalPaidAmount;
        this.totalInvoices = totalInvoices;
        this.draftCount = draftCount;
        this.sentCount = sentCount;
        this.paidCount = paidCount;
        this.overdueCount = overdueCount;
    }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public BigDecimal getTotalOutstandingAmount() { return totalOutstandingAmount; }
    public void setTotalOutstandingAmount(BigDecimal totalOutstandingAmount) { this.totalOutstandingAmount = totalOutstandingAmount; }

    public BigDecimal getTotalOverdueAmount() { return totalOverdueAmount; }
    public void setTotalOverdueAmount(BigDecimal totalOverdueAmount) { this.totalOverdueAmount = totalOverdueAmount; }

    public BigDecimal getTotalPaidAmount() { return totalPaidAmount; }
    public void setTotalPaidAmount(BigDecimal totalPaidAmount) { this.totalPaidAmount = totalPaidAmount; }

    public long getTotalInvoices() { return totalInvoices; }
    public void setTotalInvoices(long totalInvoices) { this.totalInvoices = totalInvoices; }

    public long getDraftCount() { return draftCount; }
    public void setDraftCount(long draftCount) { this.draftCount = draftCount; }

    public long getSentCount() { return sentCount; }
    public void setSentCount(long sentCount) { this.sentCount = sentCount; }

    public long getPaidCount() { return paidCount; }
    public void setPaidCount(long paidCount) { this.paidCount = paidCount; }

    public long getOverdueCount() { return overdueCount; }
    public void setOverdueCount(long overdueCount) { this.overdueCount = overdueCount; }
}
