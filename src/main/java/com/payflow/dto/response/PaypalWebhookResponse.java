package com.payflow.dto.response;

public class PaypalWebhookResponse {

    private String status;
    private String eventId;

    public PaypalWebhookResponse() {}

    public PaypalWebhookResponse(String status, String eventId) {
        this.status = status;
        this.eventId = eventId;
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }
}
