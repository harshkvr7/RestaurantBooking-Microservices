package com.rms.reservation_service.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.rms.reservation_service.model.Reservation;
import com.rms.reservation_service.model.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Integer>{
    
    @Query("SELECT r.slot.id, COUNT(r) FROM Reservation r " +
           "WHERE r.restaurantId = :restaurantId AND r.reservationDate = :date AND r.status = 'CONFIRMED' " +
           "GROUP BY r.slot.id")
    List<Object[]> countReservationsBySlot(Integer restaurantId, LocalDate date);

    List<Reservation> findAllByrestaurantId(Integer id);

    List<Reservation> findAllByRestaurantIdAndStatus(Integer restaurantId, ReservationStatus status);

    List<Reservation> findAllByUserId(Integer userId);
}
