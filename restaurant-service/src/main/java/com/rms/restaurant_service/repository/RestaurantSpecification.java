package com.rms.restaurant_service.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.rms.restaurant_service.models.Restaurant;
import com.rms.restaurant_service.models.RestaurantCategory;

import jakarta.persistence.criteria.Join;

public class RestaurantSpecification {

    public static Specification<Restaurant> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(name)) {
                return null; 
            }
            
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<Restaurant> inCity(String city) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(city)) {
                return null;
            }

            return criteriaBuilder.like(criteriaBuilder.lower(root.get("city")), "%" + city.toLowerCase() + "%");
        };
    }

    public static Specification<Restaurant> hasCategory(String category) {
        return (root, query, criteriaBuilder) -> {
            if (!StringUtils.hasText(category)) {
                return null;
            }

            Join<Restaurant, RestaurantCategory> categoryJoin = root.join("category");
           
            return criteriaBuilder.like(criteriaBuilder.lower(categoryJoin.get("category")), "%" + category.toLowerCase() + "%");
        };
    }
}
