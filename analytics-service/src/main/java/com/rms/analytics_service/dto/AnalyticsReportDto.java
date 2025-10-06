package com.rms.analytics_service.dto;

import java.time.LocalDate;

public record AnalyticsReportDto(
    Integer restaurantId,
    LocalDate startDate,
    LocalDate endDate,
    long totalViews,
    long totalReviews,
    long totalRatings,
    long totalReservations
) {}
