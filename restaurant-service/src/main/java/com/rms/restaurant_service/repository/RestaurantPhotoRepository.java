package com.rms.restaurant_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.restaurant_service.models.RestaurantPhoto;

public interface RestaurantPhotoRepository extends JpaRepository<RestaurantPhoto, Integer> {
    
    List<RestaurantPhoto> findByRestaurantId(Integer restaurantId);
}
