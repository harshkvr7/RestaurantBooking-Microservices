package com.rms.reservation_service.dto;

import java.time.LocalDate;

import com.rms.reservation_service.model.ReservationSlot;
import com.rms.reservation_service.model.ReservationStatus;

public record ReservationResponseDto (
    Integer reservationId,
    Integer userId,
    Integer restaurantId,
    ReservationSlot slot,
    ReservationStatus status,
    LocalDate reservationDate
) {}
