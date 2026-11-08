package com.payflow.controller;

import com.payflow.dto.request.PaypalWebhookRequest;
import com.payflow.dto.response.PaypalWebhookResponse;
import com.payflow.service.PaypalWebhookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhooks/paypal")
public class PaypalWebhookController {

    private final PaypalWebhookService paypalWebhookService;

    public PaypalWebhookController(PaypalWebhookService paypalWebhookService) {
        this.paypalWebhookService = paypalWebhookService;
    }

    @PostMapping
    public ResponseEntity<PaypalWebhookResponse> handleWebhook(@RequestBody PaypalWebhookRequest request) {
        String result = paypalWebhookService.processWebhook(request);
        String eventId = (request.getId() != null) ? request.getId() : "";
        PaypalWebhookResponse response = new PaypalWebhookResponse(result, eventId);
        return ResponseEntity.ok(response);
    }
}
