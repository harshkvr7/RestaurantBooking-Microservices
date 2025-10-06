package com.rms.menu_item_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.menu_item_service.dto.MenuItemRequestDto;
import com.rms.menu_item_service.dto.MenuItemResponseDto;
import com.rms.menu_item_service.model.ItemCategory;
import com.rms.menu_item_service.model.ItemDescription;
import com.rms.menu_item_service.model.MenuItem;
import com.rms.menu_item_service.repository.ItemCategoryRepository;
import com.rms.menu_item_service.repository.ItemDescriptionRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MenuItemMapper {

    private final ItemCategoryRepository itemCategoryRepository;

    private final ItemDescriptionRepository itemDescriptionRepository;
    
    public MenuItem toMenuItem(MenuItemRequestDto requestDto) {
        MenuItem menuItem = new MenuItem();

        menuItem.setName(requestDto.name());
        menuItem.setRestaurantId(requestDto.restaurantId());

        ItemCategory itemCategory = itemCategoryRepository.findById(requestDto.categoryId()).orElse(null);
        menuItem.setCategory(itemCategory);

        ItemDescription itemDescription = itemDescriptionRepository.findById(requestDto.descriptionId()).orElse(null);
        menuItem.setDescription(itemDescription);

        menuItem.setPrice(requestDto.price());

        return menuItem;
    }

    public MenuItemResponseDto toMenuItemResponseDto(MenuItem menuItem) {
        return new MenuItemResponseDto(
            menuItem.getId(),
            menuItem.getName(),
            menuItem.getRestaurantId(),
            menuItem.getCategory().getCategory(),
            menuItem.getDescription().getDescription(),
            menuItem.getPrice(),
            menuItem.getImageUrl(),
            menuItem.getRating()
        );
    }
}
