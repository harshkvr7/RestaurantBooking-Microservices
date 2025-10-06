package com.rms.menu_item_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.rms.menu_item_service.model.MenuItem;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer>, JpaSpecificationExecutor<MenuItem>{
    
    public List<MenuItem> findAllByRestaurantId(Integer restaurantId);
}
