package com.rms.notification_service.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.rms.notification_service.dto.ReservationRequestEventDto;
import com.rms.notification_service.dto.ReservationStatusUpdateEventDto;
import com.rms.notification_service.dto.VerificationEmailRequestDto;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class KafkaListenerService {
    
    private final EmailService emailService;

    @KafkaListener(topics = "verification-email-topic", groupId = "notification-group")
    public void listenForVerificationEmail(VerificationEmailRequestDto request) {
        System.out.println("Received verification email request for: " + request.to());
        emailService.sendVerificationEmail(request);
    }

    @KafkaListener(topics = "reservation-request-topic", groupId = "notification-group")
    public void listenForReservationRequest(ReservationRequestEventDto event) {
        System.out.println("Received reservation request event for: " + event.restaurantName());
        emailService.sendReservationRequestEmailToOwner(event);
    }

    @KafkaListener(topics = "reservation-status-update-topic", groupId = "notification-group")
    public void listenForStatusUpdate(ReservationStatusUpdateEventDto event) {
        System.out.println("Received reservation status update event for: " + event.userEmail());
        emailService.sendReservationStatusUpdateEmailToUser(event);
    }
}
