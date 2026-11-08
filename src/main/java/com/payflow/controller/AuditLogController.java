package com.payflow.controller;

import com.payflow.entity.AuditLog;
import com.payflow.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
public class AuditLogController {

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public ResponseEntity<List<AuditLog>> getAllLogs() {
        return ResponseEntity.ok(auditLogService.getAllLogs());
    }

    @GetMapping("/{targetType}/{targetId}")
    public ResponseEntity<List<AuditLog>> getLogsForTarget(
            @PathVariable String targetType,
            @PathVariable Long targetId) {
        return ResponseEntity.ok(auditLogService.getLogsForTarget(targetType, targetId));
    }
}
