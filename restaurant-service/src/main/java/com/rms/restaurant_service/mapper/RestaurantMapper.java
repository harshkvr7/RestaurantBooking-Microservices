package com.rms.restaurant_service.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.rms.restaurant_service.dto.response.RestaurantResponseDto;
import com.rms.restaurant_service.models.Restaurant;
import com.rms.restaurant_service.models.RestaurantPhoto;

@Service
public class RestaurantMapper {

    public RestaurantResponseDto toResponseDto(Restaurant restaurant) {
        if (restaurant == null) {
            return null;
        }

        List<String> photoUrls = (restaurant.getPhotos() == null) ? Collections.emptyList() :
                restaurant.getPhotos().stream()
                        .map(RestaurantPhoto::getPhotoUrl)
                        .collect(Collectors.toList());

        String coverPhotoUrl = findCoverPhotoUrl(restaurant);

        String categoryName = (restaurant.getCategory() != null) ? restaurant.getCategory().getCategory() : null;

        return new RestaurantResponseDto(
                restaurant.getId(),
                restaurant.getOwnerId(),
                categoryName,
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getCity(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                coverPhotoUrl, 
                restaurant.getReservationCapacity(),
                restaurant.getRating(),
                restaurant.getDiscountRate(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                photoUrls, 
                restaurant.getCreatedAt()
        );
    }

    private String findCoverPhotoUrl(Restaurant restaurant) {
        if (restaurant.getCoverPhotoId() == null || restaurant.getPhotos() == null || restaurant.getPhotos().isEmpty()) {
            return null;
        }

        return restaurant.getPhotos().stream()
                .filter(photo -> restaurant.getCoverPhotoId().equals(photo.getId()))
                .map(RestaurantPhoto::getPhotoUrl)
                .findFirst()
                .orElse(null);
    }
}
