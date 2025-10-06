package com.rms.restaurant_service.dto.request;

public record RestaurantReviewRequestDto (
    Integer restaurantId,
    String reviewText
) {}
