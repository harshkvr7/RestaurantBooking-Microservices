package com.rms.analytics_service.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.rms.analytics_service.dto.AnalyticsReportDto;
import com.rms.analytics_service.model.DailyRestaurantAnalytics;
import com.rms.analytics_service.repository.AnalyticsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsReportDto getAnalyticsForDateRange(Integer restaurantId, LocalDate startDate, LocalDate endDate) {

        List<DailyRestaurantAnalytics> dailyRecords = analyticsRepository.findByRestaurantIdAndDateBetween(restaurantId,
                startDate, endDate);

        if (dailyRecords.isEmpty()) {
            return new AnalyticsReportDto(
                    restaurantId,
                    startDate,
                    endDate,
                    0,
                    0,
                    0,
                    0);
        }

        long totalViews = dailyRecords.stream().mapToLong(DailyRestaurantAnalytics::getViewCount).sum();
        long totalReviews = dailyRecords.stream().mapToLong(DailyRestaurantAnalytics::getReviewCount).sum();
        long totalRatings = dailyRecords.stream().mapToLong(DailyRestaurantAnalytics::getRatingCount).sum();
        long totalReservations = dailyRecords.stream().mapToLong(DailyRestaurantAnalytics::getReservationCount).sum();

        return new AnalyticsReportDto(
                restaurantId,
                startDate,
                endDate,
                totalViews,
                totalReviews,
                totalRatings,
                totalReservations);
    }
}