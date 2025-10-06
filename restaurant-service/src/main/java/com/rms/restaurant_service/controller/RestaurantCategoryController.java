package com.rms.restaurant_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rms.restaurant_service.dto.request.RestaurantCategoryRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantCategoryResponseDto;
import com.rms.restaurant_service.service.RestaurantCategoryService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/restaurants/categories")
public class RestaurantCategoryController {

    private final RestaurantCategoryService restaurantCategoryService;

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantCategoryResponseDto> createCategory(@RequestBody RestaurantCategoryRequestDto categoryRequestDto) {
        return new ResponseEntity<>(restaurantCategoryService.createCategory(categoryRequestDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantCategoryResponseDto>> getAllCategories() {
        return ResponseEntity.ok(restaurantCategoryService.getAllRestaurantCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantCategoryResponseDto> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.ok(restaurantCategoryService.getRestaurantCategoryById(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<RestaurantCategoryResponseDto> updateCategory(@PathVariable Integer id, @RequestBody RestaurantCategoryRequestDto categoryRequestDto) {
        return ResponseEntity.ok(restaurantCategoryService.updateRestaurantCategory(id, categoryRequestDto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        restaurantCategoryService.deleteRestaurantCategory(id);
        return ResponseEntity.noContent().build();
    }
}
