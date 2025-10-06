package com.rms.menu_item_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rms.menu_item_service.model.ItemCategory;


public interface  ItemCategoryRepository extends JpaRepository<ItemCategory, Integer> {
    
}
