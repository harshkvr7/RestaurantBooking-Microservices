package com.rms.reservation_service.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rms.reservation_service.dto.ReservationRequestDto;
import com.rms.reservation_service.dto.ReservationResponseDto;
import com.rms.reservation_service.dto.ReservationSlotsResponseDto;
import com.rms.reservation_service.model.ReservationStatus;
import com.rms.reservation_service.service.ReservationService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/reservations")
@AllArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @PostMapping
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<ReservationResponseDto> createReservation(
            @RequestBody ReservationRequestDto requestDto,
            @RequestHeader("X-User-Id") Integer userId) {
        try {
            ReservationResponseDto createdReservation = reservationService
                    .createReservation(requestDto, userId);
            return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null);
        }
    }

    @PutMapping("/{reservationId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<ReservationResponseDto> updateReservationStatus(
            @PathVariable Integer reservationId,
            @RequestParam ReservationStatus status,
            @RequestHeader("X-User-Id") Integer ownerId) {
        try {
            ReservationResponseDto updatedReservation = reservationService
                    .updateReservationStatus(reservationId, status, ownerId);
            return ResponseEntity.ok(updatedReservation);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/restaurant/{restaurantId}")
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<List<ReservationResponseDto>> getAllReservationsByRestaurantIdAndStatus(
            @PathVariable Integer restaurantId,
            @RequestHeader("X-User-Id") Integer ownerId,
            @RequestParam ReservationStatus status
    ) {
        try {
            List<ReservationResponseDto> reservations = reservationService.getAllReservationsByRestaurantIdAndStatus(
                                                                restaurantId,
                                                                ownerId,
                                                                status
                                                            );
            return ResponseEntity.ok(reservations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PreAuthorize("hasAuthority('USER')")
    @PostMapping("/cancel/{reservationId}")
    public ResponseEntity<ReservationResponseDto> cancelUserReservation(
        @PathVariable Integer reservationId,
        @RequestHeader("X-User-Id") Integer userId
    ) {
        try {
            ReservationResponseDto updatedReservation = reservationService.cancelUserReservation(reservationId, userId);
            return ResponseEntity.ok(updatedReservation);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<List<ReservationResponseDto>> getUserReservations(
        @RequestHeader("X-User-Id") Integer userId
    ) {
        try {
            List<ReservationResponseDto> reservations = reservationService.getAllReservationsByUser(userId);
            return ResponseEntity.ok(reservations);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/restaurants/{restaurantId}/slots")
    public ResponseEntity<ReservationSlotsResponseDto> getAvailableSlots(
            @PathVariable Integer restaurantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ReservationSlotsResponseDto availableSlots = reservationService
                .getAvailableSlots(restaurantId, date);
        return ResponseEntity.ok(availableSlots);
    }
}
