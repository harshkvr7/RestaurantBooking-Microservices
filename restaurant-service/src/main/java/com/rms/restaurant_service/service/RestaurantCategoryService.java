package com.rms.restaurant_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rms.restaurant_service.dto.request.RestaurantCategoryRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantCategoryResponseDto;
import com.rms.restaurant_service.mapper.RestaurantCategoryMapper;
import com.rms.restaurant_service.models.RestaurantCategory;
import com.rms.restaurant_service.repository.RestaurantCategoryRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class RestaurantCategoryService {

    private final RestaurantCategoryRepository restaurantCategoryRepository;

    private final RestaurantCategoryMapper restaurantCategoryMapper;
    
    public RestaurantCategoryResponseDto createCategory(RestaurantCategoryRequestDto categoryRequestDto) {
        return restaurantCategoryMapper.toRestaurantCategoryResponseDto(
            restaurantCategoryRepository.save(
                restaurantCategoryMapper.toRestaurantCategory(categoryRequestDto)
            )
        );
    }

    public RestaurantCategoryResponseDto getRestaurantCategoryById(Integer id) {
        RestaurantCategory category = restaurantCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RestaurantCategory not found with id: " + id));
        return restaurantCategoryMapper.toRestaurantCategoryResponseDto(category);
    }

    public List<RestaurantCategoryResponseDto> getAllRestaurantCategories() {
        return restaurantCategoryRepository.findAll()
                .stream()
                .map(restaurantCategoryMapper::toRestaurantCategoryResponseDto)
                .collect(Collectors.toList());
    }

    public RestaurantCategoryResponseDto updateRestaurantCategory(Integer id, RestaurantCategoryRequestDto categoryRequestDto) {
        RestaurantCategory existingCategory = restaurantCategoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("RestaurantCategory not found with id: " + id));

        existingCategory.setCategory(categoryRequestDto.category());

        RestaurantCategory updatedCategory = restaurantCategoryRepository.save(existingCategory);
        return restaurantCategoryMapper.toRestaurantCategoryResponseDto(updatedCategory);
    }

    public void deleteRestaurantCategory(Integer id) {
        if (!restaurantCategoryRepository.existsById(id)) {
            throw new RuntimeException("RestaurantCategory not found with id: " + id);
        }
        restaurantCategoryRepository.deleteById(id);
    }
}
