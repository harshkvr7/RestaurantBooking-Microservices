package com.rms.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.restaurant_service.models.RestaurantCategory;

public interface RestaurantCategoryRepository extends JpaRepository<RestaurantCategory, Integer> {
    
}
