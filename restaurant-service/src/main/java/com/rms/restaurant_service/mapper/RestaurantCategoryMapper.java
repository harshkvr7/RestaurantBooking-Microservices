package com.rms.restaurant_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.restaurant_service.dto.request.RestaurantCategoryRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantCategoryResponseDto;
import com.rms.restaurant_service.models.RestaurantCategory;

@Service
public class RestaurantCategoryMapper {
    
    public RestaurantCategory toRestaurantCategory(RestaurantCategoryRequestDto categoryRequestDto) {
        RestaurantCategory restaurantCategory = new RestaurantCategory();

        restaurantCategory.setCategory(categoryRequestDto.category());

        return restaurantCategory;
    }

    public RestaurantCategoryResponseDto toRestaurantCategoryResponseDto(RestaurantCategory restaurantCategory) {
        return new RestaurantCategoryResponseDto(
            restaurantCategory.getId(),
            restaurantCategory.getCategory()
        );
    }
}
