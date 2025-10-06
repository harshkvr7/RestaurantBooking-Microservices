package com.rms.restaurant_service.models;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer ownerId;

    @Column(nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private RestaurantCategory category;

    private String address;
    private String city;
    private Double latitude;
    private Double longitude;
    private Integer coverPhotoId;
    private Integer reservationCapacity;
    private Float rating = 0.0f;
    private Integer discountRate = 0;

    private LocalTime openingTime;
    private LocalTime closingTime;

    @OneToMany(
        mappedBy = "restaurant",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    @JsonManagedReference
    private List<RestaurantPhoto> photos = new ArrayList<>();

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public void addPhoto(RestaurantPhoto photo) {
        photos.add(photo);
        photo.setRestaurant(this);
    }

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantReview> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RestaurantRating> ratings = new ArrayList<>();

    public void addReview(RestaurantReview review) {
        reviews.add(review);
        review.setRestaurant(this);
    }

    public void addRating(RestaurantRating rating) {
        ratings.add(rating);
        rating.setRestaurant(this);
    }
}
