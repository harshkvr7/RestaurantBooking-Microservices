package com.rms.auth_service.dto;

public record UserResponseDto(
    Integer id,
    String username,
    String email,
    String password,
    boolean verified,
    String role
) {}
