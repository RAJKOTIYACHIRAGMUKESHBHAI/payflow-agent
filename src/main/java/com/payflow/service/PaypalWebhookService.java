package com.payflow.service;

import com.payflow.dto.request.PaypalWebhookRequest;

public interface PaypalWebhookService {
    String processWebhook(PaypalWebhookRequest request);
}
