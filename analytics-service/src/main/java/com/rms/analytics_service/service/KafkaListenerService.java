package com.rms.analytics_service.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rms.analytics_service.dto.RatingAddedEvent;
import com.rms.analytics_service.dto.ReservationRequestCreatedEvent;
import com.rms.analytics_service.dto.RestaurantViewedEvent;
import com.rms.analytics_service.dto.ReviewAddedEvent;
import com.rms.analytics_service.model.AnalyticsId;
import com.rms.analytics_service.model.DailyRestaurantAnalytics;
import com.rms.analytics_service.repository.AnalyticsRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class KafkaListenerService {

    private final AnalyticsRepository analyticsRepository;

    @KafkaListener(topics = "restaurant-view-topic", groupId = "analytics-group")
    @Transactional
    public void listenForRestaurantView(RestaurantViewedEvent event) {
        System.out.println("Analytics Service: Received restaurant-view event for ID: " + event.restaurantId());
        DailyRestaurantAnalytics analytics = getOrCreateDailyAnalytics(event.restaurantId());
        analytics.incrementViewCount();
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    @KafkaListener(topics = "restaurant-review-added-topic", groupId = "analytics-group")
    @Transactional
    public void listenForReviewAdded(ReviewAddedEvent event) {
        System.out.println("Analytics Service: Received review-added event for ID: " + event.restaurantId());
        DailyRestaurantAnalytics analytics = getOrCreateDailyAnalytics(event.restaurantId());
        analytics.incrementReviewCount();
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    @KafkaListener(topics = "restaurant-rating-added-topic", groupId = "analytics-group")
    @Transactional
    public void listenForRatingAdded(RatingAddedEvent event) {
        System.out.println("Analytics Service: Received rating-added event for ID: " + event.restaurantId());
        DailyRestaurantAnalytics analytics = getOrCreateDailyAnalytics(event.restaurantId());
        analytics.incrementRatingCount();
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    @KafkaListener(topics = "reservation-created-topic", groupId = "analytics-group")
    @Transactional
    public void listenForReservationRequest(ReservationRequestCreatedEvent event) {
        System.out.println("Analytics Service: Received reservation-request event for ID: " + event.restaurantId());
        DailyRestaurantAnalytics analytics = getOrCreateDailyAnalytics(event.restaurantId());
        analytics.incrementReservationCount();
        analytics.setLastUpdated(LocalDateTime.now());
        analyticsRepository.save(analytics);
    }

    private DailyRestaurantAnalytics getOrCreateDailyAnalytics(Integer restaurantId) {
        LocalDate today = LocalDate.now();
        AnalyticsId analyticsId = new AnalyticsId(restaurantId, today);
        
        return analyticsRepository.findById(analyticsId)
                .orElse(new DailyRestaurantAnalytics(restaurantId, today));
    }
}

