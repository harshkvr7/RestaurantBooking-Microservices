package com.rms.analytics_service.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "daily_restaurant_analytics")
public class DailyRestaurantAnalytics {

    @EmbeddedId
    private AnalyticsId id; 

    private long viewCount = 0;
    private long reviewCount = 0;
    private long ratingCount = 0;
    private long reservationCount = 0;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    public DailyRestaurantAnalytics(Integer restaurantId, LocalDate date) {
        this.id = new AnalyticsId(restaurantId, date);
    }

    public void incrementViewCount() { this.viewCount++; }
    public void incrementReviewCount() { this.reviewCount++; }
    public void incrementRatingCount() { this.ratingCount++; }
    public void incrementReservationCount() { this.reservationCount++; }
}

