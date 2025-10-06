package com.rms.reservation_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.reservation_service.dto.ReservationResponseDto;
import com.rms.reservation_service.model.Reservation;

@Service
public class ReservationMapper {
    
    public ReservationResponseDto toReservationResponseDto(Reservation reservation) {
        return new ReservationResponseDto(
            reservation.getId(),
            reservation.getUserId(),
            reservation.getRestaurantId(),
            reservation.getSlot(),
            reservation.getStatus(),
            reservation.getReservationDate()
        );
    }
}
