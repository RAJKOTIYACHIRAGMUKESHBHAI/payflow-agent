package com.payflow.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "invoice_number", nullable = false, unique = true)
    private String invoiceNumber;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(name = "issue_date", nullable = false)
    private LocalDate issueDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "paypal_order_id")
    private String paypalOrderId;

    @Column(name = "paypal_payment_link")
    private String paypalPaymentLink;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    @Column(name = "reminder_count")
    private Integer reminderCount = 0;

    @Column(name = "last_reminder_sent_at")
    private LocalDateTime lastReminderSentAt;

    public Invoice() {}

    public Invoice(Long id, String invoiceNumber, Long clientId, BigDecimal amount, String currency,
                   LocalDate issueDate, LocalDate dueDate, String status, String description,
                   String paypalOrderId, String paypalPaymentLink, LocalDateTime paidAt,
                   Integer reminderCount, LocalDateTime lastReminderSentAt) {
        this.id = id;
        this.invoiceNumber = invoiceNumber;
        this.clientId = clientId;
        this.amount = amount;
        this.currency = currency;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.status = status;
        this.description = description;
        this.paypalOrderId = paypalOrderId;
        this.paypalPaymentLink = paypalPaymentLink;
        this.reminderCount = reminderCount;
        this.lastReminderSentAt = lastReminderSentAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getInvoiceNumber() { return invoiceNumber; }
    public void setInvoiceNumber(String invoiceNumber) { this.invoiceNumber = invoiceNumber; }

    public Long getClientId() { return clientId; }
    public void setClientId(Long clientId) { this.clientId = clientId; }

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

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public int getReminderCount() { return reminderCount != null ? reminderCount : 0; }
    public void setReminderCount(Integer reminderCount) { this.reminderCount = reminderCount; }

    public LocalDateTime getLastReminderSentAt() { return lastReminderSentAt; }
    public void setLastReminderSentAt(LocalDateTime lastReminderSentAt) { this.lastReminderSentAt = lastReminderSentAt; }
}
