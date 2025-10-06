package com.rms.restaurant_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rms.restaurant_service.models.Restaurant;

public interface RestaurantRepository extends JpaRepository<Restaurant, Integer> , JpaSpecificationExecutor<Restaurant>{
    
}
