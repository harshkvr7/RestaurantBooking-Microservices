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
import org.springframework.web.bind.annotation.RestController;

import com.rms.restaurant_service.dto.request.RestaurantRatingRequestDto;
import com.rms.restaurant_service.dto.request.RestaurantReviewRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantRatingResponseDto;
import com.rms.restaurant_service.dto.response.RestaurantReviewResponseDto;
import com.rms.restaurant_service.service.ReviewAndRatingService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/restaurants/{restaurantId}")
public class ReviewAndRatingController {

    private final ReviewAndRatingService reviewAndRatingService;

    @PostMapping("/reviews")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RestaurantReviewResponseDto> addReview(
            @PathVariable Integer restaurantId,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody RestaurantReviewRequestDto requestDto) {
        RestaurantReviewResponseDto createdReview = reviewAndRatingService.addReview(restaurantId, userId, requestDto);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<RestaurantReviewResponseDto>> getReviews(@PathVariable Integer restaurantId) {
        List<RestaurantReviewResponseDto> reviews = reviewAndRatingService.getReviewsForRestaurant(restaurantId);
        return ResponseEntity.ok(reviews);
    }

    @PutMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RestaurantReviewResponseDto> updateReview(
            @PathVariable Integer reviewId,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody RestaurantReviewRequestDto requestDto) {
        RestaurantReviewResponseDto updatedReview = reviewAndRatingService.updateReview(reviewId, userId, requestDto);
        return ResponseEntity.ok(updatedReview);
    }

    @DeleteMapping("/reviews/{reviewId}")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<Void> deleteReview(
            @PathVariable Integer reviewId,
            @RequestHeader("X-User-Id") Integer userId) {
        reviewAndRatingService.deleteReview(reviewId, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/ratings")
    @PreAuthorize("hasAuthority('USER')")
    public ResponseEntity<RestaurantRatingResponseDto> addRating(
            @PathVariable Integer restaurantId,
            @RequestHeader("X-User-Id") Integer userId,
            @RequestBody RestaurantRatingRequestDto requestDto) {
        RestaurantRatingResponseDto createdRating = reviewAndRatingService.addRating(restaurantId, userId, requestDto);
        return new ResponseEntity<>(createdRating, HttpStatus.CREATED);
    }
}
