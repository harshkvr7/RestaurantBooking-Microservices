package com.rms.restaurant_service.service;

import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.rms.restaurant_service.dto.RestaurantViewedEvent;
import com.rms.restaurant_service.dto.request.RestaurantRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantReservationDetailsDto;
import com.rms.restaurant_service.dto.response.RestaurantResponseDto;
import com.rms.restaurant_service.mapper.RestaurantMapper;
import com.rms.restaurant_service.models.Restaurant;
import com.rms.restaurant_service.models.RestaurantCategory;
import com.rms.restaurant_service.repository.RestaurantCategoryRepository;
import com.rms.restaurant_service.repository.RestaurantRepository;
import com.rms.restaurant_service.repository.RestaurantSpecification;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestTemplate restTemplate;
    private final RestaurantRepository restaurantRepository;
    private final RestaurantMapper restaurantMapper;
    private final RestaurantCategoryRepository categoryRepository;
    private final LocationService locationService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${service.urls.media-service}")
    private String mediaServiceUrl;
    
    //private static final String TOPIC = "restaurant-events";

    @Transactional
    @CacheEvict(value = "restaurants_list", allEntries = true)
    public RestaurantResponseDto createRestaurant(RestaurantRequestDto requestDto, Integer ownerId) {
        RestaurantCategory category = categoryRepository.findById(requestDto.categoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.categoryId()));

        Restaurant restaurant = new Restaurant();
        restaurant.setOwnerId(ownerId);
        restaurant.setName(requestDto.name());
        restaurant.setAddress(requestDto.address());
        restaurant.setCity(requestDto.city());
        restaurant.setLatitude(requestDto.latitude());
        restaurant.setLongitude(requestDto.longitude());
        restaurant.setReservationCapacity(requestDto.reservationCapacity());
        restaurant.setCategory(category);
        restaurant.setDiscountRate(requestDto.discountRate());
        restaurant.setOpeningTime(requestDto.openingTime());
        restaurant.setClosingTime(requestDto.closingTime());

        Restaurant savedRestaurant = restaurantRepository.save(restaurant);
        locationService.insertRestaurant(savedRestaurant);
        
        RestaurantResponseDto responseDto = restaurantMapper.toResponseDto(savedRestaurant);
        //kafkaTemplate.send(TOPIC, new RestaurantEvent(RestaurantEvent.EventType.CREATED, responseDto, null));
        
        return responseDto;
    }

    public RestaurantResponseDto getRestaurantById(Integer id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        kafkaTemplate.send("restaurant-view-topic", new RestaurantViewedEvent(id));
        
        return restaurantMapper.toResponseDto(restaurant);
    }

    @Cacheable(value = "restaurants_list", key = "'all'")
    public List<RestaurantResponseDto> getAllRestaurants(String name, String city, String category) {
        Specification<Restaurant> spec = Specification.unrestricted();

        if (name != null && !name.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasName(name));
        }
        if (city != null && !city.isEmpty()) {
            spec = spec.and(RestaurantSpecification.inCity(city));
        }
        if (category != null && !category.isEmpty()) {
            spec = spec.and(RestaurantSpecification.hasCategory(category));
        }

        return restaurantRepository.findAll(spec).stream()
                .map(restaurantMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Caching(
        put = { @CachePut(value = "restaurants", key = "#id") },
        evict = { @CacheEvict(value = "restaurants_list", allEntries = true) }
    )
    public RestaurantResponseDto updateRestaurant(Integer id, RestaurantRequestDto requestDto, Integer userId, String userRole) {
        Restaurant existingRestaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        if (userRole.equals("OWNER") && !Objects.equals(existingRestaurant.getOwnerId(), userId)) {
            throw new SecurityException("You do not have permission to update this restaurant.");
        }

        if (!existingRestaurant.getCategory().getId().equals(requestDto.categoryId())) {
            RestaurantCategory newCategory = categoryRepository.findById(requestDto.categoryId())
                    .orElseThrow(() -> new RuntimeException("Category not found with id: " + requestDto.categoryId()));
            existingRestaurant.setCategory(newCategory);
        }
        
        locationService.deleteRestaurant(existingRestaurant);

        existingRestaurant.setName(requestDto.name());
        existingRestaurant.setAddress(requestDto.address());
        existingRestaurant.setCity(requestDto.city());
        existingRestaurant.setLatitude(requestDto.latitude());
        existingRestaurant.setLongitude(requestDto.longitude());
        existingRestaurant.setReservationCapacity(requestDto.reservationCapacity());
        existingRestaurant.setDiscountRate(requestDto.discountRate());
        existingRestaurant.setOpeningTime(requestDto.openingTime());
        existingRestaurant.setClosingTime(requestDto.closingTime());

        Restaurant updatedRestaurant = restaurantRepository.save(existingRestaurant);
        
        locationService.insertRestaurant(updatedRestaurant);

        RestaurantResponseDto responseDto = restaurantMapper.toResponseDto(updatedRestaurant);
        //kafkaTemplate.send(TOPIC, new RestaurantEvent(RestaurantEvent.EventType.UPDATED, responseDto, null));

        return responseDto;
    }

    public RestaurantReservationDetailsDto getRestaurantReservationDetails(Integer restaurantId) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        return new RestaurantReservationDetailsDto(
                restaurant.getReservationCapacity(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime());
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(value = "restaurants", key = "#id"),
        @CacheEvict(value = "restaurants_list", allEntries = true)
    })
    public void deleteRestaurant(Integer id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));

        if (restaurant.getPhotos() != null && !restaurant.getPhotos().isEmpty()) {
            final String MEDIA_SERVICE_DELETE_URL = mediaServiceUrl + "/delete"; // Assumes base URL is in properties
            restaurant.getPhotos().forEach(photo -> {
                try {
                    String encodedUrl = Base64.getEncoder().encodeToString(photo.getPhotoUrl().getBytes());
                    restTemplate.delete(MEDIA_SERVICE_DELETE_URL + "/" + encodedUrl);
                } catch (RestClientException e) {
                    System.err.println("Failed to delete photo " + photo.getPhotoUrl() + ": " + e.getMessage());
                }
            });
        }
        
        locationService.deleteRestaurant(restaurant);
        restaurantRepository.deleteById(id);
        //kafkaTemplate.send(TOPIC, new RestaurantEvent(RestaurantEvent.EventType.DELETED, null, id));
    }

    public List<RestaurantResponseDto> findNearbyRestaurants(double lon, double lat, double radiusKm) {
        List<Integer> nearbyIds = locationService.findNearbyRestaurantIds(lon, lat, radiusKm);
        if (nearbyIds.isEmpty()) {
            return Collections.emptyList();
        }

        return restaurantRepository.findAllById(nearbyIds).stream()
                .map(restaurantMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}

