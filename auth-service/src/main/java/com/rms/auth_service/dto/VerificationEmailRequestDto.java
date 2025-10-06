package com.rms.auth_service.dto;

public record VerificationEmailRequestDto (
    String to,
    String username,
    String token
) {}
