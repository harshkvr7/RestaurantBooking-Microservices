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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rms.restaurant_service.dto.request.RestaurantRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantReservationDetailsDto;
import com.rms.restaurant_service.dto.response.RestaurantResponseDto;
import com.rms.restaurant_service.service.RestaurantService;

import lombok.AllArgsConstructor;


@RestController
@RequestMapping("/restaurants")
@AllArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    @PreAuthorize("hasAuthority('OWNER')")
    public ResponseEntity<RestaurantResponseDto> createRestaurant(
            @RequestBody RestaurantRequestDto requestDto,
            @RequestHeader("X-User-Id") Integer ownerId) {

        RestaurantResponseDto createdRestaurant = restaurantService.createRestaurant(requestDto, ownerId);
        return new ResponseEntity<>(createdRestaurant, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantResponseDto> updateRestaurant(
            @PathVariable Integer id, 
            @RequestBody RestaurantRequestDto requestDto,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestHeader("X-User-Role") String userRole) {
        
        RestaurantResponseDto updatedRestaurant = restaurantService.updateRestaurant(id, requestDto, userId, userRole);
        return ResponseEntity.ok(updatedRestaurant);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Integer id) {
        restaurantService.deleteRestaurant(id);
        return ResponseEntity.noContent().build();
    }

    // --- Publicly Accessible Endpoints ---

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponseDto> getRestaurantById(@PathVariable Integer id) {
        RestaurantResponseDto restaurant = restaurantService.getRestaurantById(id);
        return ResponseEntity.ok(restaurant);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<RestaurantReservationDetailsDto> getReservationDetails(@PathVariable Integer id) {
        return ResponseEntity.ok(restaurantService.getRestaurantReservationDetails(id));
    }
    
    @GetMapping
    public ResponseEntity<List<RestaurantResponseDto>> getAllRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String category) {
                
        List<RestaurantResponseDto> restaurants = restaurantService.getAllRestaurants(name, city, category);
        return ResponseEntity.ok(restaurants);
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantResponseDto>> getNearbyRestaurants(
            @RequestParam double lon,
            @RequestParam double lat,
            @RequestParam(defaultValue = "5.0") double radius) {
        
        List<RestaurantResponseDto> restaurants = restaurantService.findNearbyRestaurants(lon, lat, radius);
        return ResponseEntity.ok(restaurants);
    }
}
