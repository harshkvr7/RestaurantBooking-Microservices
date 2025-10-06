package com.rms.user_service.dto;

public record UserResponseDto (
    Integer id,
    String username,
    String email,
    boolean verified,
    String role
) {}
