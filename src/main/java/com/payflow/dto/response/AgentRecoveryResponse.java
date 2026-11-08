package com.payflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AgentRecoveryResponse {

    private Long invoiceId;
    private String invoiceNumber;
    private String clientName;
    private String clientEmail;
    private BigDecimal amount;
    private String currency;
    private LocalDate dueDate;
    private long daysOverdue;
    private String urgencyLevel;
    private String recommendedStrategy;
    private String generatedEmailSubject;
    private String generatedEmailBody;
    private String paypalPaymentLink;

    public AgentRecoveryResponse() {}

    public AgentRecoveryResponse(Long invoiceId, String invoiceNumber, String clientName, String clientEmail,
                                 BigDecimal amount, String currency, LocalDate dueDate, long daysOverdue,
                                 String urgencyLevel, String recommendedStrategy, String generatedEmailSubject,
                                 String generatedEmailBody, String paypalPaymentLink) {
        this.invoiceId = invoiceId;
        this.invoiceNumber = invoiceNumber;
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.amount = amount;
        this.currency = currency;
        this.dueDate = dueDate;
        this.daysOverdue = daysOverdue;
        this.urgencyLevel = urgencyLevel;
        this.recommendedStrategy = recommendedStrategy;
        this.generatedEmailSubject = generatedEmailSubject;
        this.generatedEmailBody = generatedEmailBody;
        this.paypalPaymentLink = paypalPaymentLink;
    }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public long getDaysOverdue() { return daysOverdue; }
    public void setDaysOverdue(long daysOverdue) { this.daysOverdue = daysOverdue; }

    public String getUrgencyLevel() { return urgencyLevel; }
    public void setUrgencyLevel(String urgencyLevel) { this.urgencyLevel = urgencyLevel; }

    public String getRecommendedStrategy() { return recommendedStrategy; }
    public void setRecommendedStrategy(String recommendedStrategy) { this.recommendedStrategy = recommendedStrategy; }

    public String getGeneratedEmailSubject() { return generatedEmailSubject; }
    public void setGeneratedEmailSubject(String generatedEmailSubject) { this.generatedEmailSubject = generatedEmailSubject; }

    public String getGeneratedEmailBody() { return generatedEmailBody; }
    public void setGeneratedEmailBody(String generatedEmailBody) { this.generatedEmailBody = generatedEmailBody; }

    public String getPaypalPaymentLink() { return paypalPaymentLink; }
    public void setPaypalPaymentLink(String paypalPaymentLink) { this.paypalPaymentLink = paypalPaymentLink; }
}
