package com.rms.restaurant_service.service;

import java.io.IOException;
import java.util.Base64;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.rms.restaurant_service.models.Restaurant;
import com.rms.restaurant_service.models.RestaurantPhoto;
import com.rms.restaurant_service.repository.RestaurantPhotoRepository;
import com.rms.restaurant_service.repository.RestaurantRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestaurantPhotoService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantPhotoRepository restaurantPhotoRepository;
    private final RestTemplate restTemplate;

    @Value("${service.urls.api-gateway}")
    private String mediaServiceUrl;
    
    

    @Transactional
    public RestaurantPhoto addPhoto(Integer restaurantId, MultipartFile file, Integer userId, String userRole)
            throws IOException {
        System.out.println("here1");

        String MEDIA_SERVICE_UPLOAD_URL = mediaServiceUrl + "/media/upload";

        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        System.out.println("here2");

        // --- AUTHORIZATION CHECK ---
        if (userRole.equals("OWNER") && !Objects.equals(restaurant.getOwnerId(), userId)) {
            throw new SecurityException("You do not have permission to add photos to this restaurant.");
        }

        System.out.println("here3");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        body.add("file", new ByteArrayResource(file.getBytes()) {
            @Override
            public String getFilename() {
                return file.getOriginalFilename();
            }
        });

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        System.out.println(MEDIA_SERVICE_UPLOAD_URL);

        ResponseEntity<String> response = restTemplate.postForEntity(MEDIA_SERVICE_UPLOAD_URL, requestEntity,
                String.class);
        String photoUrl = response.getBody();

        System.out.println("here5");

        RestaurantPhoto newPhoto = new RestaurantPhoto();
        newPhoto.setPhotoUrl(photoUrl);
        restaurant.addPhoto(newPhoto);

        System.out.println("here6");
        return restaurantPhotoRepository.save(newPhoto);
    }

    @Transactional
    public void deletePhoto(Integer photoId, Integer userId, String userRole) {
        RestaurantPhoto photo = restaurantPhotoRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found with id: " + photoId));

        String MEDIA_SERVICE_DELETE_URL = mediaServiceUrl + "/media/delete";

        // --- AUTHORIZATION CHECK ---
        if (userRole.equals("OWNER") && !Objects.equals(photo.getRestaurant().getOwnerId(), userId)) {
            throw new SecurityException("You do not have permission to delete photos from this restaurant.");
        }

        String encodedUrl = Base64.getEncoder().encodeToString(photo.getPhotoUrl().getBytes());

        try {
            restTemplate.delete(MEDIA_SERVICE_DELETE_URL + "/" + encodedUrl);
        } catch (RestClientException e) {
            System.err.println(
                    "Failed to delete photo from media-service: " + photo.getPhotoUrl() + ". Error: " + e.getMessage());
        }

        restaurantPhotoRepository.delete(photo);
    }

    public List<RestaurantPhoto> getAllPhotosForRestaurant(Integer restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }
        return restaurantPhotoRepository.findByRestaurantId(restaurantId);
    }
}
