package com.payflow.controller;

import com.payflow.dto.request.MerchantSettingsRequest;
import com.payflow.dto.response.MerchantSettingsResponse;
import com.payflow.service.MerchantSettingsService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/settings")
public class MerchantSettingsController {

    private final MerchantSettingsService service;

    public MerchantSettingsController(MerchantSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<MerchantSettingsResponse> getSettings() {
        return ResponseEntity.ok(service.getSettings());
    }

    @PutMapping
    public ResponseEntity<MerchantSettingsResponse> updateSettings(@Valid @RequestBody MerchantSettingsRequest request) {
        return ResponseEntity.ok(service.updateSettings(request));
    }
}
