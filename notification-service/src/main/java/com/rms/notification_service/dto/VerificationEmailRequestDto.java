package com.rms.notification_service.dto;

public record VerificationEmailRequestDto (
    String to,
    String username,
    String token
) {}
