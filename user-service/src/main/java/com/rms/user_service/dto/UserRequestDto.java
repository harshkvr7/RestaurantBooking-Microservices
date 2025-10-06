package com.rms.user_service.dto;

public record UserRequestDto (
    String username,
    String email,
    String password,
    String role
) {}
