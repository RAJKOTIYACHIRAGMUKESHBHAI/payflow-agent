package com.payflow.service;

import com.payflow.dto.response.DashboardSummaryResponse;

public interface DashboardService {
    DashboardSummaryResponse getSummary(String currency);
}
