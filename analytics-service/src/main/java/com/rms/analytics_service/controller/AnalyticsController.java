package com.rms.analytics_service.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rms.analytics_service.dto.AnalyticsReportDto;
import com.rms.analytics_service.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/restaurants/{restaurantId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<AnalyticsReportDto> getRestaurantAnalytics(
            @PathVariable Integer restaurantId,
            @RequestHeader("X-User-Id") Integer ownerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        AnalyticsReportDto report = analyticsService.getAnalyticsForDateRange(restaurantId, startDate, endDate);
        return ResponseEntity.ok(report);
    }
}