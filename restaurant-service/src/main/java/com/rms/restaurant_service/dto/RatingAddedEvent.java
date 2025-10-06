package com.rms.restaurant_service.dto;

public record RatingAddedEvent(Integer restaurantId, float rating) {}
