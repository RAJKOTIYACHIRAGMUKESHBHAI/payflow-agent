package com.payflow.controller;

import com.payflow.service.PayPalService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
public class PayPalController {

    private final PayPalService payPalService;

    public PayPalController(PayPalService payPalService) {
        this.payPalService = payPalService;
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, String>> testPayPalConnection() {

        payPalService.getAccessToken();

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "PayPal Sandbox connection successful"
                )
        );
    }

    @PostMapping("/orders")
    public ResponseEntity<Map<String, String>> createOrder(
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "INR") String currency) {

        String orderId = payPalService.createOrder(amount, currency);

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "orderId", orderId
                )
        );
    }
}