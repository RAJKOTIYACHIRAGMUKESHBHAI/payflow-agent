package com.payflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Map;

@Service
public class PayPalService {

    private final WebClient webClient;

    @Value("${paypal.base-url}")
    private String baseUrl;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    public PayPalService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public String getAccessToken() {

        String credentials = clientId + ":" + clientSecret;

        String encodedCredentials = Base64.getEncoder()
                .encodeToString(credentials.getBytes());

        Map<String, Object> response = webClient
                .post()
                .uri(baseUrl + "/v1/oauth2/token")
                .header("Authorization", "Basic " + encodedCredentials)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .bodyValue("grant_type=client_credentials")
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        if (response == null || response.get("access_token") == null) {
            throw new RuntimeException("Failed to get PayPal access token");
        }

        return response.get("access_token").toString();
    }

    public String createOrder(BigDecimal amount, String currency) {

        String accessToken = getAccessToken();

        Map<String, Object> orderRequest = Map.of(
                "intent", "CAPTURE",
                "purchase_units", new Object[]{
                        Map.of(
                                "amount", Map.of(
                                        "currency_code", currency,
                                        "value", amount.toPlainString()
                                )
                        )
                }
        );

        try {

            Map<String, Object> response = webClient
                    .post()
                    .uri(baseUrl + "/v2/checkout/orders")
                    .header("Authorization", "Bearer " + accessToken)
                    .header(
                            "PayPal-Request-Id",
                            "PAYFLOW-" + System.currentTimeMillis()
                    )
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(orderRequest)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            if (response == null || response.get("id") == null) {
                throw new RuntimeException("Failed to create PayPal order");
            }

            return response.get("id").toString();

        } catch (
                org.springframework.web.reactive.function.client.WebClientResponseException e) {

            throw new RuntimeException(
                    "PayPal API Error "
                            + e.getStatusCode()
                            + ": "
                            + e.getResponseBodyAsString()
            );
        }
    }
}