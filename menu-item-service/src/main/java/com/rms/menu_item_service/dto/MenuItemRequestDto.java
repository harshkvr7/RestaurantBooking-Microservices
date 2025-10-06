package com.rms.menu_item_service.dto;

public record MenuItemRequestDto (
    String name,
    Integer restaurantId,
    Integer categoryId,
    Integer descriptionId,
    Float price
) {}

