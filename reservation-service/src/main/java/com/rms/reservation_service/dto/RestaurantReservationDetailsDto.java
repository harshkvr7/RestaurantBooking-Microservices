package com.rms.reservation_service.dto;

import java.time.LocalTime;

public record RestaurantReservationDetailsDto (
    Integer reservationCapacity,
    LocalTime openingTime,
    LocalTime closingTime
) {}
