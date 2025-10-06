package com.rms.analytics_service.dto;

public record RatingAddedEvent(Integer restaurantId, float rating) {}
