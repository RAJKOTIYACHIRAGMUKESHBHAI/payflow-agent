package com.payflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InvoiceResponse {

    private Long id;
    private String invoiceNumber;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private BigDecimal amount;
    private String currency;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private String status;
    private String description;
    private String paypalOrderId;
    private String paypalPaymentLink;

    public InvoiceResponse() {}

    public InvoiceResponse(Long id, String invoiceNumber, Long clientId, String clientName, String clientEmail,
                           BigDecimal amount, String currency, LocalDate issueDate, LocalDate dueDate,
                           String status, String description, String paypalOrderId, String paypalPaymentLink) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.clientId = clientId;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.amount = amount;
        this.currency = currency;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
        this.paypalOrderId = paypalOrderId;
        this.paypalPaymentLink = paypalPaymentLink;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getIssueDate() { return issueDate; }
    public void setIssueDate(LocalDate issueDate) { this.issueDate = issueDate; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPaypalOrderId() { return paypalOrderId; }
    public void setPaypalOrderId(String paypalOrderId) { this.paypalOrderId = paypalOrderId; }

    public String getPaypalPaymentLink() { return paypalPaymentLink; }
    public void setPaypalPaymentLink(String paypalPaymentLink) { this.paypalPaymentLink = paypalPaymentLink; }
}
