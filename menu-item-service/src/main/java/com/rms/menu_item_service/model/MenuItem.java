package com.rms.menu_item_service.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer restaurantId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ItemCategory category;

    @ManyToOne
    @JoinColumn(name = "description_id", nullable = false)
    private ItemDescription description;

    @Column(nullable = false)
    private Float price;

    private String imageUrl;

    private Float rating = 0.0f;

    @Column(updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}
