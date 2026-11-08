package com.payflow.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MerchantSettingsRequest {

    @NotBlank
    private String businessName;

    @NotBlank
    private String email;

    @NotBlank
    private String defaultCurrency;

    @NotBlank
    private String timezone;

    @NotNull
    private Integer maxReminders;

    @NotNull
    private Integer cooldownDays;

    public MerchantSettingsRequest() {}

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDefaultCurrency() { return defaultCurrency; }
    public void setDefaultCurrency(String defaultCurrency) { this.defaultCurrency = defaultCurrency; }

    public String getTimezone() { return timezone; }
    public void setTimezone(String timezone) { this.timezone = timezone; }

    public Integer getMaxReminders() { return maxReminders; }
    public void setMaxReminders(Integer maxReminders) { this.maxReminders = maxReminders; }

    public Integer getCooldownDays() { return cooldownDays; }
    public void setCooldownDays(Integer cooldownDays) { this.cooldownDays = cooldownDays; }
}
