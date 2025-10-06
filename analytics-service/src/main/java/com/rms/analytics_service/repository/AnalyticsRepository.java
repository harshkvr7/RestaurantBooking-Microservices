package com.rms.analytics_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rms.analytics_service.model.AnalyticsId;
import com.rms.analytics_service.model.DailyRestaurantAnalytics;

public interface AnalyticsRepository extends JpaRepository<DailyRestaurantAnalytics, AnalyticsId> {
    
    @Query("SELECT a FROM DailyRestaurantAnalytics a WHERE a.id.restaurantId = :restaurantId AND a.id.date BETWEEN :startDate AND :endDate")
    List<DailyRestaurantAnalytics> findByRestaurantIdAndDateBetween(Integer restaurantId, LocalDate startDate, LocalDate endDate);
}
