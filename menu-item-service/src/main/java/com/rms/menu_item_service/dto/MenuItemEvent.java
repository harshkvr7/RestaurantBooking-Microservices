package com.rms.menu_item_service.dto;

public record  MenuItemEvent (
    EventType eventType,
    MenuItemResponseDto menuItem,
    Integer menuItemId
) {
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
