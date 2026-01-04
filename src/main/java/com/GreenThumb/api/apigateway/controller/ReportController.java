package com.GreenThumb.api.apigateway.controller;

import com.GreenThumb.api.apigateway.dto.CreateReportRequest;
import com.GreenThumb.api.apigateway.service.TokenExtractor;
import com.GreenThumb.api.forum.application.service.ReportService;
import com.GreenThumb.api.forum.domain.entity.Report;
import com.GreenThumb.api.infrastructure.service.TokenService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/reports")
public class ReportController {
    private final ReportService reportService;
    private final TokenExtractor tokenExtractor;
    private final TokenService tokenService;

    public ReportController(ReportService reportService, TokenExtractor tokenExtractor, TokenService tokenService) {
        this.reportService = reportService;
        this.tokenExtractor = tokenExtractor;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader("Authorization") String authHeader,
            @Valid @RequestBody CreateReportRequest request
    ) {
        try {
            String token = tokenExtractor.extractToken(authHeader);
            String username = tokenService.extractUsername(token);

            Report report = reportService.createReport(
                    request.messageId(),
                    username,
                    request.reason()
            );

            return ResponseEntity.ok(Map.of(
                    "message", "Message reported successfully",
                    "reportId", report.id()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/{messageId}/count")
    public ResponseEntity<Map<String, Long>> countReports(@PathVariable Long messageId) {
        long count = reportService.countPendingReports(messageId);
        return ResponseEntity.ok(Map.of("count", count));
    }
}
