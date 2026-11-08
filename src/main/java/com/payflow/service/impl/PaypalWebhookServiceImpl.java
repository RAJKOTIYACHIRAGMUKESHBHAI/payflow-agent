package com.payflow.service.impl;

import com.payflow.dto.request.PaypalWebhookRequest;
import com.payflow.entity.Invoice;
import com.payflow.entity.Payment;
import com.payflow.entity.WebhookEvent;
import com.payflow.repository.InvoiceRepository;
import com.payflow.repository.PaymentRepository;
import com.payflow.repository.WebhookEventRepository;
import com.payflow.service.AuditLogService;
import com.payflow.service.PaypalWebhookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class PaypalWebhookServiceImpl implements PaypalWebhookService {

    private final WebhookEventRepository webhookEventRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final AuditLogService auditLogService;

    public PaypalWebhookServiceImpl(WebhookEventRepository webhookEventRepository,
                                     PaymentRepository paymentRepository,
                                     InvoiceRepository invoiceRepository,
                                     AuditLogService auditLogService) {
        this.webhookEventRepository = webhookEventRepository;
        this.paymentRepository = paymentRepository;
        this.invoiceRepository = invoiceRepository;
        this.auditLogService = auditLogService;
    }

    @Override
    @Transactional
    public String processWebhook(PaypalWebhookRequest request) {
        if (request == null || request.getId() == null || request.getId().isBlank()) {
            return "IGNORED_INVALID_EVENT";
        }

        if (webhookEventRepository.existsByEventId(request.getId())) {
            return "DUPLICATE_EVENT_SKIPPED";
        }

        String eventType = request.getEventType();
        Map<String, Object> resource = request.getResource();

        if ("PAYMENT.CAPTURE.COMPLETED".equalsIgnoreCase(eventType) || "INVOICING.INVOICE.PAID".equalsIgnoreCase(eventType)) {
            String paypalOrderId = extractPaypalOrderId(resource);
            if (paypalOrderId != null && !paypalOrderId.isBlank()) {
                reconcilePayment(paypalOrderId, request.getId());
            }
        }

        webhookEventRepository.save(new WebhookEvent(request.getId(), eventType != null ? eventType : "UNKNOWN", LocalDateTime.now()));

        return "PROCESSED_SUCCESSFULLY";
    }

    private String extractPaypalOrderId(Map<String, Object> resource) {
        if (resource == null) return null;
        if (resource.containsKey("supplementary_data")) {
            Object supp = resource.get("supplementary_data");
            if (supp instanceof Map) {
                Object related = ((Map<?, ?>) supp).get("related_ids");
                if (related instanceof Map) {
                    Object orderId = ((Map<?, ?>) related).get("order_id");
                    if (orderId != null) return orderId.toString();
                }
            }
        }
        if (resource.containsKey("id") && resource.get("id") != null) {
            return resource.get("id").toString();
        }
        return null;
    }

    private void reconcilePayment(String paypalOrderId, String webhookEventId) {
        if (paypalOrderId == null || paypalOrderId.isBlank()) return;

        Optional<Payment> optionalPayment = paymentRepository.findAll().stream()
                .filter(p -> p.getPaypalOrderId() != null && paypalOrderId.equalsIgnoreCase(p.getPaypalOrderId()))
                .findFirst();

        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            if (!"COMPLETED".equalsIgnoreCase(payment.getStatus())) {
                payment.setStatus("COMPLETED");
                payment.setPaypalPaymentId("WEBHOOK-" + webhookEventId);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);

                Invoice invoice = invoiceRepository.findById(payment.getInvoiceId()).orElse(null);
                if (invoice != null) {
                    invoice.setStatus("PAID");
                    invoice.setPaidAt(LocalDateTime.now());
                    invoiceRepository.save(invoice);

                    auditLogService.logAction(
                            "SYSTEM",
                            "WEBHOOK_RECONCILE_PAYMENT",
                            "INVOICE",
                            invoice.getId(),
                            "Reconciled invoice " + invoice.getInvoiceNumber() + " to PAID via Webhook (" + webhookEventId + ")",
                            "SUCCESS"
                    );
                }
            }
        } else {
            Optional<Invoice> optionalInvoice = invoiceRepository.findAll().stream()
                    .filter(i -> i.getPaypalOrderId() != null && paypalOrderId.equalsIgnoreCase(i.getPaypalOrderId()))
                    .findFirst();

            if (optionalInvoice.isPresent()) {
                Invoice invoice = optionalInvoice.get();
                invoice.setStatus("PAID");
                invoice.setPaidAt(LocalDateTime.now());
                invoiceRepository.save(invoice);

                auditLogService.logAction(
                        "SYSTEM",
                        "WEBHOOK_RECONCILE_INVOICE",
                        "INVOICE",
                        invoice.getId(),
                        "Automated Webhook marked invoice " + invoice.getInvoiceNumber() + " as PAID",
                        "SUCCESS"
                );
            }
        }
    }
}
