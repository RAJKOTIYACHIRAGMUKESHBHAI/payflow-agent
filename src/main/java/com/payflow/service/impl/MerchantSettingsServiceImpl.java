package com.payflow.service.impl;

import com.payflow.dto.request.MerchantSettingsRequest;
import com.payflow.dto.response.MerchantSettingsResponse;
import com.payflow.entity.MerchantSettings;
import com.payflow.repository.MerchantSettingsRepository;
import com.payflow.service.MerchantSettingsService;
import org.springframework.stereotype.Service;

@Service
public class MerchantSettingsServiceImpl implements MerchantSettingsService {

    private final MerchantSettingsRepository repository;

    public MerchantSettingsServiceImpl(MerchantSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public MerchantSettingsResponse getSettings() {
        MerchantSettings settings = repository.findAll().stream().findFirst().orElseGet(this::createDefaultSettings);
        return mapToResponse(settings);
    }

    @Override
    public MerchantSettingsResponse updateSettings(MerchantSettingsRequest request) {
        MerchantSettings settings = repository.findAll().stream().findFirst().orElseGet(MerchantSettings::new);
        settings.setBusinessName(request.getBusinessName());
        settings.setEmail(request.getEmail());
        settings.setDefaultCurrency(request.getDefaultCurrency().toUpperCase());
        settings.setTimezone(request.getTimezone());
        settings.setMaxReminders(request.getMaxReminders());
        settings.setCooldownDays(request.getCooldownDays());

        MerchantSettings saved = repository.save(settings);
        return mapToResponse(saved);
    }

    private MerchantSettings createDefaultSettings() {
        MerchantSettings defaults = new MerchantSettings(
                null,
                "PayFlow Merchant Co.",
                "merchant@payflow.com",
                "USD",
                "UTC",
                3,
                3
        );
        return repository.save(defaults);
    }

    private MerchantSettingsResponse mapToResponse(MerchantSettings settings) {
        return new MerchantSettingsResponse(
                settings.getId(),
                settings.getBusinessName(),
                settings.getEmail(),
                settings.getDefaultCurrency(),
                settings.getTimezone(),
                settings.getMaxReminders(),
                settings.getCooldownDays()
        );
    }
}
