package com.payflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {

    private Long id;
    private Long invoiceId;
    private BigDecimal amount;
    private String currency;
    private String status;
    private String paypalOrderId;
    private String paypalPaymentId;
    private LocalDateTime paidAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PaymentResponse() {
    }

    public PaymentResponse(
            Long id,
            Long invoiceId,
            BigDecimal amount,
            String currency,
            String status,
            String paypalOrderId,
            String paypalPaymentId,
            LocalDateTime paidAt,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.invoiceId = invoiceId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.paypalOrderId = paypalOrderId;
        this.paypalPaymentId = paypalPaymentId;
        this.paidAt = paidAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getInvoiceId() {
        return invoiceId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getPaypalOrderId() {
        return paypalOrderId;
    }

    public String getPaypalPaymentId() {
        return paypalPaymentId;
    }

    public LocalDateTime getPaidAt() {
        return paidAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}