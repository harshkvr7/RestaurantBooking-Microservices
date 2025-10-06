package com.rms.reservation_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.reservation_service.model.ReservationSlot;

public interface ReservationSlotRepository extends JpaRepository<ReservationSlot, Integer> {
    
}
