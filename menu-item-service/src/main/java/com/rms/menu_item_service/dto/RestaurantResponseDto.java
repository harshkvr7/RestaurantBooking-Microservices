package com.rms.menu_item_service.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

public record RestaurantResponseDto(
    Integer id,
    Integer ownerId,
    String category, 
    String name,
    String address,
    String city,
    Double latitude,
    Double longitude,
    String coverPhotoUrl,
    Integer reservationCapacity,
    Float rating,
    Integer discountRate,
    LocalTime openingTime,
    LocalTime closingTime,
    List<String> photoUrls, 
    LocalDateTime createdAt
) {}