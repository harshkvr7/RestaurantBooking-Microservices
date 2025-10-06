package com.rms.restaurant_service.service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.rms.restaurant_service.dto.RatingAddedEvent;
import com.rms.restaurant_service.dto.ReviewAddedEvent;
import com.rms.restaurant_service.dto.request.RestaurantRatingRequestDto;
import com.rms.restaurant_service.dto.request.RestaurantReviewRequestDto;
import com.rms.restaurant_service.dto.response.RestaurantRatingResponseDto;
import com.rms.restaurant_service.dto.response.RestaurantReviewResponseDto;
import com.rms.restaurant_service.models.Restaurant;
import com.rms.restaurant_service.models.RestaurantRating;
import com.rms.restaurant_service.models.RestaurantReview;
import com.rms.restaurant_service.repository.RestaurantRatingRepository;
import com.rms.restaurant_service.repository.RestaurantRepository;
import com.rms.restaurant_service.repository.RestaurantReviewRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewAndRatingService {

    private final RestaurantRepository restaurantRepository;
    private final RestaurantReviewRepository restaurantReviewRepository;
    private final RestaurantRatingRepository restaurantRatingRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Transactional
    public RestaurantReviewResponseDto addReview(Integer restaurantId, Integer userId,
            RestaurantReviewRequestDto requestDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));

        boolean hasReviewed = restaurantReviewRepository.existsByRestaurantIdAndUserId(restaurantId, userId);
        if (hasReviewed) {
            throw new IllegalStateException("User has already reviewed this restaurant.");
        }

        RestaurantReview review = new RestaurantReview();
        review.setRestaurant(restaurant);
        review.setUserId(userId);
        review.setReviewText(requestDto.reviewText());
        restaurant.addReview(review);

        RestaurantReview savedReview = restaurantReviewRepository.save(review);

        kafkaTemplate.send("restaurant-review-added-topic", new ReviewAddedEvent(restaurantId));

        return new RestaurantReviewResponseDto(
                savedReview.getId(),
                savedReview.getRestaurant().getId(),
                savedReview.getUserId(),
                savedReview.getReviewText(),
                savedReview.getCreatedAt());
    }

    @Transactional
    public RestaurantReviewResponseDto updateReview(Integer reviewId, Integer userId, RestaurantReviewRequestDto requestDto){
        RestaurantReview review = restaurantReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));

        if (!Objects.equals(review.getUserId(), userId)) {
            throw new SecurityException("You do not have permission to delete this review.");
        }

        review.setReviewText(requestDto.reviewText());

        return new RestaurantReviewResponseDto(
            review.getId(),
            review.getRestaurant().getId(),
            review.getUserId(),
            review.getReviewText(),
            review.getCreatedAt()
        );
    }

    @Transactional
    public void deleteReview(Integer reviewId, Integer userId) {
        RestaurantReview review = restaurantReviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found with id: " + reviewId));
        
        if (!Objects.equals(review.getUserId(), userId)) {
            throw new SecurityException("You do not have permission to delete this review.");
        }
        
        restaurantReviewRepository.delete(review);
    }

    public List<RestaurantReviewResponseDto> getReviewsForRestaurant(Integer restaurantId) {
        return restaurantReviewRepository.findByRestaurantId(restaurantId).stream()
            .map(review -> new RestaurantReviewResponseDto(review.getId(), review.getRestaurant().getId(), review.getUserId(), review.getReviewText(), review.getCreatedAt()))
            .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantRatingResponseDto addRating(Integer restaurantId, Integer userId, RestaurantRatingRequestDto requestDto) {
        Restaurant restaurant = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + restaurantId));
        
        boolean hasRated = restaurant.getRatings().stream().anyMatch(r -> r.getUserId().equals(userId));
        if (hasRated) {
            throw new IllegalStateException("User has already rated this restaurant.");
        }

        RestaurantRating rating = new RestaurantRating();
        rating.setUserId(userId);
        rating.setRating(requestDto.rating());
        restaurant.addRating(rating);
        
        RestaurantRating savedRating = restaurantRatingRepository.save(rating);

        updateAverageRatingAndPublishEvent(restaurant);

        kafkaTemplate.send("restaurant-rating-added-topic", new RatingAddedEvent(restaurantId, rating.getRating()));

        return new RestaurantRatingResponseDto(savedRating.getId(), savedRating.getUserId(), savedRating.getRating(), savedRating.getCreatedAt());
    }

    private void updateAverageRatingAndPublishEvent(Restaurant restaurant) {
        double average = restaurant.getRatings().stream()
                .mapToDouble(RestaurantRating::getRating)
                .average()
                .orElse(0.0);
        
        restaurant.setRating((float) average);
        restaurantRepository.save(restaurant);
    }

}
