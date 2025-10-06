package com.rms.reservation_service.dto;

import java.time.LocalTime;

public record AvailableSlotDto(
    Integer slotId,
    LocalTime startTime,
    LocalTime endTime,
    Integer availableCapacity
) {}