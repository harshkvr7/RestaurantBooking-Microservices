package com.rms.notification_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rms.notification_service.dto.ReservationRequestEventDto;
import com.rms.notification_service.dto.ReservationStatusUpdateEventDto;
import com.rms.notification_service.dto.VerificationEmailRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${service.urls.api-gateway}")
    private String apiGatewayUrl;
    
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(VerificationEmailRequestDto requestDto) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(requestDto.to());
        message.setSubject("Verify Your Email for RMS");
        
        String verificationUrl = apiGatewayUrl + "/auth/verify?token=" + requestDto.token();
        
        message.setText("Hello " + requestDto.username() + ",\n\nPlease click the link below to verify your email address:\n" + verificationUrl);
        
        mailSender.send(message);
    }

    public void sendReservationRequestEmailToOwner(ReservationRequestEventDto event) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.ownerEmail());

        message.setSubject("New Reservation Request for " + event.restaurantName());

        message.setText("Hello,\n\nYou have a new reservation request from " + event.userEmail() +
                        " for " + event.reservationDate() + " at " + event.reservationTime() + ".\n\n" +
                        "Please log in to your dashboard to confirm or decline this request.");
                        
        mailSender.send(message);
    }

    public void sendReservationStatusUpdateEmailToUser(ReservationStatusUpdateEventDto event) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(event.userEmail());

        message.setSubject("Update on your reservation at " + event.restaurantName());

        message.setText("Hello,\n\nYour reservation for " + event.restaurantName() +
                        " on " + event.reservationDate() + " at " + event.reservationTime() +
                        " has been updated to: " + event.newStatus() + ".\n\nThank you!");
                        
        mailSender.send(message);
    }
}
