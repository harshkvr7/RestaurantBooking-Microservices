package com.rms.restaurant_service.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rms.restaurant_service.models.RestaurantPhoto;
import com.rms.restaurant_service.service.RestaurantPhotoService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/restaurants/{restaurantId}/photos")
@AllArgsConstructor
public class RestaurantPhotoController {

    private final RestaurantPhotoService restaurantPhotoService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<RestaurantPhoto> addPhoto(
            @PathVariable Integer restaurantId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestHeader("X-User-Role") String userRole) {
                
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            
            RestaurantPhoto savedPhoto = restaurantPhotoService.addPhoto(restaurantId, file, userId, userRole);
            return new ResponseEntity<>(savedPhoto, HttpStatus.CREATED);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{photoId}")
    @PreAuthorize("hasAnyAuthority('OWNER', 'ADMIN')")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Integer restaurantId,
            @PathVariable Integer photoId,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestHeader("X-User-Role") String userRole) {
        try {
            
            restaurantPhotoService.deletePhoto(photoId, userId, userRole);
            return ResponseEntity.noContent().build();
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<RestaurantPhoto>> getAllPhotosForRestaurant(@PathVariable Integer restaurantId) {
        try {
            List<RestaurantPhoto> photos = restaurantPhotoService.getAllPhotosForRestaurant(restaurantId);
            return ResponseEntity.ok(photos);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
