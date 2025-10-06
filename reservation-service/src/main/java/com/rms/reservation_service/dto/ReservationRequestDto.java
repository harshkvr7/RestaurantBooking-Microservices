package com.rms.reservation_service.dto;

import java.time.LocalDate;

public record ReservationRequestDto (
    Integer restaurantId,
    Integer slotId,
    LocalDate reservationDate
) {}
