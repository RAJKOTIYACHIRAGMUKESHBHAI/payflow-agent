package com.payflow.service;

import com.payflow.dto.request.MerchantSettingsRequest;
import com.payflow.dto.response.MerchantSettingsResponse;

public interface MerchantSettingsService {
    MerchantSettingsResponse getSettings();
    MerchantSettingsResponse updateSettings(MerchantSettingsRequest request);
}
