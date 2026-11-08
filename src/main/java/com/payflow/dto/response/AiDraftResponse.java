package com.payflow.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class AiDraftResponse {

    private String clientName;
    private String clientEmail;
    private String description;
    private BigDecimal amount;
    private String currency;
    private LocalDate dueDate;
    private boolean readyForApproval;
    private List<String> missingOrAmbiguousFields;

    public AiDraftResponse() {}

    public AiDraftResponse(String clientName, String clientEmail, String description,
                           BigDecimal amount, String currency, LocalDate dueDate,
                           boolean readyForApproval, List<String> missingOrAmbiguousFields) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.description = description;
        this.amount = amount;
        this.currency = currency;
        this.dueDate = dueDate;
        this.readyForApproval = readyForApproval;
        this.missingOrAmbiguousFields = missingOrAmbiguousFields;
    }

    public String getClientName() { return clientName; }
    public void setClientName(String clientName) { this.clientName = clientName; }

    public String getClientEmail() { return clientEmail; }
    public void setClientEmail(String clientEmail) { this.clientEmail = clientEmail; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public boolean isReadyForApproval() { return readyForApproval; }
    public void setReadyForApproval(boolean readyForApproval) { this.readyForApproval = readyForApproval; }

    public List<String> getMissingOrAmbiguousFields() { return missingOrAmbiguousFields; }
    public void setMissingOrAmbiguousFields(List<String> missingOrAmbiguousFields) { this.missingOrAmbiguousFields = missingOrAmbiguousFields; }
}
