package com.rms.reservation_service.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.rms.reservation_service.dto.AvailableSlotDto;
import com.rms.reservation_service.dto.ReservationRequestCreatedEvent;
import com.rms.reservation_service.dto.ReservationRequestDto;
import com.rms.reservation_service.dto.ReservationRequestEventDto;
import com.rms.reservation_service.dto.ReservationResponseDto;
import com.rms.reservation_service.dto.ReservationSlotsResponseDto;
import com.rms.reservation_service.dto.ReservationStatusUpdateEventDto;
import com.rms.reservation_service.dto.RestaurantResponseDto;
import com.rms.reservation_service.dto.UserResponseDto;
import com.rms.reservation_service.mapper.ReservationMapper;
import com.rms.reservation_service.model.Reservation;
import com.rms.reservation_service.model.ReservationSlot;
import com.rms.reservation_service.model.ReservationStatus;
import com.rms.reservation_service.repository.ReservationRepository;
import com.rms.reservation_service.repository.ReservationSlotRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationSlotRepository slotRepository;
    private final RestTemplate restTemplate;
    private final ReservationMapper reservationMapper;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${service.urls.user-service}")
    private String userServiceUrl;

    @Value("${service.urls.restaurant-service}")
    private String restaurantServiceUrl;

    @Transactional
    public ReservationResponseDto createReservation(ReservationRequestDto requestDto, Integer userId) {
        ReservationSlot slot = slotRepository.findById(requestDto.slotId())
                .orElseThrow(() -> new RuntimeException("Slot not found with id: " + requestDto.slotId()));

        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setRestaurantId(requestDto.restaurantId());
        reservation.setReservationDate(requestDto.reservationDate());
        reservation.setSlot(slot);
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation savedReservation = reservationRepository.save(reservation);

        RestaurantResponseDto restaurantDetails = getRestaurantDetails(savedReservation.getRestaurantId());
        String userEmail = getUserEmail(savedReservation.getUserId());
        String ownerEmail = getUserEmail(restaurantDetails.ownerId());

        ReservationRequestEventDto event = new ReservationRequestEventDto(
                ownerEmail,
                restaurantDetails.name(),
                userEmail,
                savedReservation.getReservationDate(),
                savedReservation.getSlot().getStartTime());

        kafkaTemplate.send("reservation-request-topic", event);
        kafkaTemplate.send("reservation-created-topic", new ReservationRequestCreatedEvent(requestDto.restaurantId()));

        return reservationMapper.toReservationResponseDto(savedReservation);
    }

    @Transactional
    public ReservationResponseDto updateReservationStatus(Integer reservationId, ReservationStatus newStatus,
            Integer ownerId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        verifyRestaurantOwner(reservation.getRestaurantId(), ownerId);

        reservation.setStatus(newStatus);
        Reservation updatedReservation = reservationRepository.save(reservation);

        // --- Fetch details needed for the notification event ---
        RestaurantResponseDto restaurantDetails = getRestaurantDetails(updatedReservation.getRestaurantId());
        String userEmail = getUserEmail(updatedReservation.getUserId());

        // --- Publish event to Kafka ---
        ReservationStatusUpdateEventDto event = new ReservationStatusUpdateEventDto(
                userEmail,
                restaurantDetails.name(),
                updatedReservation.getReservationDate(),
                updatedReservation.getSlot().getStartTime(),
                newStatus.toString());
        kafkaTemplate.send("reservation-status-update-topic", event);

        return reservationMapper.toReservationResponseDto(updatedReservation);
    }

    @Transactional
    public ReservationResponseDto cancelUserReservation(
            Integer reservationId,
            Integer userId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found with id: " + reservationId));

        if (!Objects.equals(reservation.getUserId(), userId)) {
            throw new SecurityException("You do not have permission to cancel this reservation.");
        }

        // Users can only cancel if the reservation is still pending
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalStateException("Only pending reservations can be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation updatedReservation = reservationRepository.save(reservation);
        return reservationMapper.toReservationResponseDto(updatedReservation);
    }

    public ReservationSlotsResponseDto getAvailableSlots(Integer restaurantId, LocalDate reservationDate) {
        RestaurantResponseDto restaurantDetails = getRestaurantDetails(restaurantId);

        if (restaurantDetails == null || restaurantDetails.reservationCapacity() == null) {
            throw new RuntimeException("Could not fetch details for restaurant with id: " + restaurantId);
        }
        int capacity = restaurantDetails.reservationCapacity();
        LocalTime openingTime = restaurantDetails.openingTime();
        LocalTime closingTime = restaurantDetails.closingTime();

        Map<Integer, Long> bookedCounts = reservationRepository
                .countReservationsBySlot(restaurantId, reservationDate)
                .stream()
                .collect(Collectors.toMap(
                        result -> (Integer) result[0],
                        result -> (Long) result[1]));

        List<ReservationSlot> allSlots = slotRepository.findAll();

        List<AvailableSlotDto> availableSlots = allSlots.stream()
                .filter(slot -> !slot.getStartTime().isBefore(openingTime)
                        && !slot.getEndTime().isAfter(closingTime))
                .map(slot -> {
                    long currentBookings = bookedCounts.getOrDefault(slot.getId(), 0L);
                    int availableCapacity = capacity - (int) currentBookings;
                    return new AvailableSlotDto(slot.getId(), slot.getStartTime(),
                            slot.getEndTime(),
                            availableCapacity);
                })
                .filter(dto -> dto.availableCapacity() > 0)
                .collect(Collectors.toList());

        return new ReservationSlotsResponseDto(availableSlots);
    }

    public List<ReservationResponseDto> getAllReservationsByRestaurantIdAndStatus(
            Integer restaurantId,
            Integer ownerId,
            ReservationStatus status) {
        verifyRestaurantOwner(restaurantId, ownerId);

        return reservationRepository
                .findAllByRestaurantIdAndStatus(restaurantId, status)
                .stream()
                .map(reservationMapper::toReservationResponseDto)
                .collect(Collectors.toList());
    }

    public List<ReservationResponseDto> getAllReservationsByUser(
            Integer userId) {
        return reservationRepository
                .findAllByUserId(userId)
                .stream()
                .map(reservationMapper::toReservationResponseDto)
                .collect(Collectors.toList());
    }

    // --- Private Helper Methods ---

    private void verifyRestaurantOwner(Integer restaurantId, Integer ownerId) {
        RestaurantResponseDto restaurantDetails = getRestaurantDetails(restaurantId);
        if (restaurantDetails == null || !Objects.equals(restaurantDetails.ownerId(), ownerId)) {
            throw new SecurityException("User is not the owner of this restaurant.");
        }
    }

    private RestaurantResponseDto getRestaurantDetails(Integer restaurantId) {
        return restTemplate.getForObject(restaurantServiceUrl + "/restaurants/" + restaurantId, RestaurantResponseDto.class);
    }

    private String getUserEmail(Integer userId) {
        try {
            UserResponseDto userDetails = restTemplate.getForObject(userServiceUrl + "/users/" + userId,
                    UserResponseDto.class);
            return (userDetails != null && userDetails.email() != null) ? userDetails.email()
                    : "email.not.found@rms.com";
        } catch (RestClientException e) {
            System.err.println("Failed to fetch email for user " + userId + ". Error: " + e.getMessage());
            return "email.not.found@rms.com";
        }
    }
}
