package com.rms.restaurant_service.dto.response;

import java.time.LocalDateTime;

public record RestaurantRatingResponseDto (
    Integer id, 
    Integer userId, 
    Float rating, 
    LocalDateTime createdAt
) {}
