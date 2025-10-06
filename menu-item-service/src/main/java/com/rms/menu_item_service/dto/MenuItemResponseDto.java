package com.rms.menu_item_service.dto;

public record MenuItemResponseDto (
    Integer id,
    String name,
    Integer restaurantId,
    String category,
    String description,
    Float price,
    String imageUrl,
    Float rating
) {}
