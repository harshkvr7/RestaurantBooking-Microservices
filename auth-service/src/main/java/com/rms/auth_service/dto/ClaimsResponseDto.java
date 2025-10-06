package com.rms.auth_service.dto;

public record ClaimsResponseDto (
    Integer userId,
    String role
) {}
