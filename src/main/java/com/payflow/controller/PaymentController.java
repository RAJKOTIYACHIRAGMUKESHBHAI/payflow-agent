package com.payflow.controller;

import com.payflow.dto.request.PaymentRequest;
import com.payflow.dto.response.PaymentResponse;
import com.payflow.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(
            @Valid @RequestBody PaymentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(paymentService.createPayment(request));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/invoice/{invoiceId}")
    public ResponseEntity<PaymentResponse> getPaymentByInvoiceId(
            @PathVariable Long invoiceId) {

        return ResponseEntity.ok(
                paymentService.getPaymentByInvoiceId(invoiceId)
        );
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<PaymentResponse> markAsPaid(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {

        return ResponseEntity.ok(
                paymentService.markAsPaid(
                        id,
                        request.get("paypalPaymentId")
                )
        );
    }
}