package com.rms.restaurant_service.dto;

import com.rms.restaurant_service.dto.response.RestaurantResponseDto;

public record RestaurantEvent(
    EventType eventType,
    RestaurantResponseDto restaurant,
    Integer restaurantId 
) {
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
