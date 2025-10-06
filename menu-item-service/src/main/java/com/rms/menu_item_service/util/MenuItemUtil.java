package com.rms.menu_item_service.util;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.rms.menu_item_service.dto.RestaurantResponseDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MenuItemUtil {

    @Value("${service.urls.restaurant-service}")
    private String restaurantServiceUrl;

    private final RestTemplate restTemplate;

    public void verifyRestaurantOwner(Integer restaurantId, Integer ownerId) {
        System.out.println(restaurantId + " " + ownerId);
        RestaurantResponseDto restaurantDetails = restTemplate.getForObject(restaurantServiceUrl + "/restaurants/" + restaurantId,
                RestaurantResponseDto.class);

        if (restaurantDetails == null || !Objects.equals(restaurantDetails.ownerId(), ownerId)) {
            throw new SecurityException("User is not the owner of this restaurant.");
        }
    }
}
