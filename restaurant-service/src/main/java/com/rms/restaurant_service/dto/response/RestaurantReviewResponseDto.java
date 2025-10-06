package com.rms.restaurant_service.dto.response;

import java.time.LocalDateTime;

public record RestaurantReviewResponseDto (
    Integer id, 
    Integer restaurantId,
    Integer userId, 
    String reviewText, 
    LocalDateTime createdAt
) {}
