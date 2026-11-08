package com.payflow.dto.response;

import java.time.LocalDateTime;

public class ReminderSendResponse {

    private Long invoiceId;
    private String status;
    private String message;
    private int currentReminderCount;
    private LocalDateTime sentAt;

    public ReminderSendResponse() {}

    public ReminderSendResponse(Long invoiceId, String status, String message, int currentReminderCount, LocalDateTime sentAt) {
        this.invoiceId = invoiceId;
        this.status = status;
        this.message = message;
        this.currentReminderCount = currentReminderCount;
        this.sentAt = sentAt;
    }

    public Long getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Long invoiceId) { this.invoiceId = invoiceId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getCurrentReminderCount() { return currentReminderCount; }
    public void setCurrentReminderCount(int currentReminderCount) { this.currentReminderCount = currentReminderCount; }

    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
}
