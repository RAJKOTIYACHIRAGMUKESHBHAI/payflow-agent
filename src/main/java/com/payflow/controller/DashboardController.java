package com.payflow.controller;

import com.payflow.dto.response.DashboardSummaryResponse;
import com.payflow.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getSummary(@RequestParam(defaultValue = "USD") String currency) {
        DashboardSummaryResponse summary = dashboardService.getSummary(currency);
        return ResponseEntity.ok(summary);
    }
}
