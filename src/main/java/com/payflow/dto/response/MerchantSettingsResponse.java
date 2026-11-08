package com.payflow.dto.response;

public class MerchantSettingsResponse {

    private Long id;
    private String businessName;
    private String email;
    private String defaultCurrency;
    private String timezone;
    private Integer maxReminders;
    private Integer cooldownDays;

    public MerchantSettingsResponse() {}

    public MerchantSettingsResponse(Long id, String businessName, String email, String defaultCurrency,
                                    String timezone, Integer maxReminders, Integer cooldownDays) {
        this.id = id;
        this.businessName = businessName;
        this.email = email;
        this.defaultCurrency = defaultCurrency;
        this.timezone = timezone;
        this.maxReminders = maxReminders;
        this.cooldownDays = cooldownDays;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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
