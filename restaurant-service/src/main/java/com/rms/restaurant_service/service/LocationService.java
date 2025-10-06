package com.rms.restaurant_service.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.rms.restaurant_service.models.Restaurant;

import lombok.RequiredArgsConstructor;

//GeoHash Version
@Service
@RequiredArgsConstructor
public class LocationService {
    private static final String GEO_KEY = "restaurants:location";

    private final RedisTemplate<String, String> redisTemplate;

    public void insertRestaurant(Restaurant restaurant) {
        Point point = new Point(restaurant.getLongitude(), restaurant.getLatitude());
        
        String member = restaurant.getId().toString();
        
        redisTemplate.opsForGeo().add(GEO_KEY, point, member);
    }

    public void deleteRestaurant(Restaurant restaurant) {
        String member = restaurant.getId().toString();
        redisTemplate.opsForGeo().remove(GEO_KEY, member);
    }

    public List<Integer> findNearbyRestaurantIds(double lon, double lat, double radiusKm) {
        Point center = new Point(lon, lat);
        Distance radius = new Distance(radiusKm, RedisGeoCommands.DistanceUnit.KILOMETERS);
        Circle circle = new Circle(center, radius);

        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().radius(GEO_KEY, circle);

        return results != null ? results.getContent().stream()
                .map(result -> Integer.parseInt(result.getContent().getName()))
                .collect(Collectors.toList()) : null;
    }
}


//Quadtree Version
// @Service
// @RequiredArgsConstructor
// public class LocationService {

//     private final RestaurantRepository restaurantRepository;
//     private QuadTree quadTree;

//     public void insertRestaurant(Restaurant restaurant) {
//         if (restaurant.getLongitude() != null && restaurant.getLatitude() != null) {
//             Point point = new Point(restaurant.getLongitude(), restaurant.getLatitude(), restaurant.getId());
//             quadTree.insert(point);
//         }
//     }

//     public List<Integer> findNearbyRestaurantIds(double lon, double lat, double radiusKm) {
//         // rectangular search 
//         double latRadius = radiusKm / 111.0; // Approx. 111 km per degree of latitude
//         double lonRadius = radiusKm / (111.0 * Math.cos(Math.toRadians(lat)));
//         Rectangle searchArea = new Rectangle(lon, lat, lonRadius, latRadius);

//         List<Point> pointsInRect = quadTree.query(searchArea);

//         // circular search
//         return pointsInRect.stream()
//                 .filter(p -> distance(lat, lon, p.getY(), p.getX()) <= radiusKm)
//                 .map(Point::getRestaurantId)
//                 .collect(Collectors.toList());
//     }

//     // Using Haversine Formula
//     private double distance(double lat1, double lon1, double lat2, double lon2) {
//         double R = 6371; // Radius of the Earth in kilometers

//         double latDistance = Math.toRadians(lat2 - lat1);
//         double lonDistance = Math.toRadians(lon2 - lon1);

//         double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
//                 + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
//                 * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

//         double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//         return R * c;
//     }

//     public void deleteRestaurant(Restaurant restaurant) {
//         quadTree.remove(new Point(
//             restaurant.getLongitude(), 
//             restaurant.getLatitude(), 
//             restaurant.getId()));
//     }
// }
