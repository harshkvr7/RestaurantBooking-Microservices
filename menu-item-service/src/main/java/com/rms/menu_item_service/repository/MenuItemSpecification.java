package com.rms.menu_item_service.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.rms.menu_item_service.model.ItemCategory;
import com.rms.menu_item_service.model.MenuItem;

import jakarta.persistence.criteria.Join;

public class MenuItemSpecification {

    public static Specification<MenuItem> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null; 
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<MenuItem> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(category)) {
                return null;
            }

            Join<MenuItem, ItemCategory> categoryJoin = root.join("category");
            
            return criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("category")), "%" + category.toLowerCase() + "%");
        };
    }
}
