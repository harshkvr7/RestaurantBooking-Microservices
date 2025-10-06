package com.rms.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.restaurant_service.models.RestaurantRating;

public interface RestaurantRatingRepository extends JpaRepository<RestaurantRating, Integer> {
    
}
