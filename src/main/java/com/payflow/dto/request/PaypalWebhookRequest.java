package com.payflow.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

public class PaypalWebhookRequest {

    private String id;

    @JsonProperty("event_type")
    private String eventType;

    private String summary;

    private Map<String, Object> resource;

    public PaypalWebhookRequest() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }

    public Map<String, Object> getResource() { return resource; }
    public void setResource(Map<String, Object> resource) { this.resource = resource; }
}
