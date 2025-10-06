package com.rms.menu_item_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.ItemCategoryResponseDto;
import com.rms.menu_item_service.model.ItemCategory;

@Service
public class ItemCategoryMapper {
    
    public ItemCategoryResponseDto toItemCategoryResponseDto(ItemCategory category) {
        return new ItemCategoryResponseDto(
            category.getId(),
            category.getCategory()
        );
    }
}
