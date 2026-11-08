package com.payflow.service;

import com.payflow.dto.request.AiDraftRequest;
import com.payflow.dto.request.ReminderSendRequest;
import com.payflow.dto.response.AgentRecoveryResponse;
import com.payflow.dto.response.AiDraftResponse;
import com.payflow.dto.response.InvoiceResponse;
import com.payflow.dto.response.ReminderSendResponse;

import java.util.List;

public interface AgentService {
    AiDraftResponse draftInvoice(AiDraftRequest request);
    List<AgentRecoveryResponse> analyzePortfolio();
    InvoiceResponse approveDraft(AiDraftResponse draft);
    ReminderSendResponse sendReminder(ReminderSendRequest request);
}
