package com.rms.restaurant_service.dto.response;

import java.time.LocalTime;

public record RestaurantReservationDetailsDto (
    Integer reservationCapacity,
    LocalTime openingTime,
    LocalTime closingTime
) {}
