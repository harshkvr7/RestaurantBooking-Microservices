package com.rms.restaurant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.restaurant_service.models.RestaurantReview;

public interface RestaurantReviewRepository extends JpaRepository<RestaurantReview, Integer> {
     List<RestaurantReview> findByRestaurantId(Integer restaurantId);

     boolean existsByRestaurantIdAndUserId(Integer restaurantId, Integer userId);
}

