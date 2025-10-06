package com.rms.reservation_service.dto;

import java.util.List;

public record ReservationSlotsResponseDto (
    List<AvailableSlotDto> reservationSlots
) {}
