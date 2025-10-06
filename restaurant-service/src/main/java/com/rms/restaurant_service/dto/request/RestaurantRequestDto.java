package com.rms.restaurant_service.dto.request;

import java.time.LocalTime;

public record RestaurantRequestDto(
    Integer ownerId,
    Integer categoryId,
    String name,
    String address,
    String city,
    Double latitude,
    Double longitude,
    Integer reservationCapacity,
    Integer discountRate,
    LocalTime openingTime,
    LocalTime closingTime
) {}
