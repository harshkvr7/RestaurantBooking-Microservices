package com.rms.menu_item_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.ItemDescriptionResponseDto;
import com.rms.menu_item_service.model.ItemDescription;

@Service
public class ItemDescriptionMapper {
    
    public ItemDescriptionResponseDto toItemDescriptionResponseDto(ItemDescription itemDescription) {
        return new ItemDescriptionResponseDto(
            itemDescription.getId(),
            itemDescription.getName(),
            itemDescription.getDescription()
        );
    }
}
