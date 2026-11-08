package com.payflow.controller;

import com.payflow.dto.request.AiDraftRequest;
import com.payflow.dto.request.ReminderSendRequest;
import com.payflow.dto.response.AgentRecoveryResponse;
import com.payflow.dto.response.AiDraftResponse;
import com.payflow.dto.response.InvoiceResponse;
import com.payflow.dto.response.ReminderSendResponse;
import com.payflow.service.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/draft-invoice")
    public ResponseEntity<AiDraftResponse> draftInvoice(@RequestBody AiDraftRequest request) {
        AiDraftResponse draft = agentService.draftInvoice(request);
        return ResponseEntity.ok(draft);
    }

    @GetMapping("/analyze")
    public ResponseEntity<List<AgentRecoveryResponse>> analyzePortfolio() {
        List<AgentRecoveryResponse> analysis = agentService.analyzePortfolio();
        return ResponseEntity.ok(analysis);
    }

    @PostMapping("/approve-draft")
    public ResponseEntity<InvoiceResponse> approveDraft(@RequestBody AiDraftResponse draft) {
        InvoiceResponse response = agentService.approveDraft(draft);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/send-reminder")
    public ResponseEntity<ReminderSendResponse> sendReminder(@Valid @RequestBody ReminderSendRequest request) {
        ReminderSendResponse response = agentService.sendReminder(request);
        return ResponseEntity.ok(response);
    }
}
