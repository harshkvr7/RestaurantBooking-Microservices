package com.rms.auth_service.dto;

public record AuthRequestDto(
    String email,
    String password
) {}
