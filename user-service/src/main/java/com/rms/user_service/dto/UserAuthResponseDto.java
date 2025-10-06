package com.rms.user_service.dto;

public record UserAuthResponseDto (
    Integer id,
    String username,
    String email,
    String password,
    boolean verified,
    String role
) {}
