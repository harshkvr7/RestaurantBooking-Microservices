package com.rms.restaurant_service.quadtree;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Point {

    private double x; // longitude
    private double y; // latitude
    private Integer restaurantId;

}
