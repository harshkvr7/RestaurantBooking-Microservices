package com.rms.notification_service.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record ReservationRequestEventDto (
    String ownerEmail,
    String restaurantName,
    String userEmail,
    LocalDate reservationDate,
    LocalTime reservationTime
) {}
