package com.rms.reservation_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationStatusUpdateEventDto (
    String userEmail,
    String restaurantName,
    LocalDate reservationDate,
    LocalTime reservationTime,
    String newStatus
) {}
