package com.rms.api_gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import com.rms.api_gateway.config.AuthenticationGatewayFilterFactory;

@SpringBootApplication
public class ApiGatewayApplication {

        @Value("${service.urls.auth-service}")
        private String authServiceUrl;

        @Value("${service.urls.user-service}")
        private String userServiceUrl;

        @Value("${service.urls.restaurant-service}")
        private String restaurantServiceUrl;

        @Value("${service.urls.media-service}")
        private String mediaServiceUrl;

        @Value("${service.urls.reservation-service}")
        private String reservationServiceUrl;

        @Value("${service.urls.menu-item-service}")
        private String menuItemServiceUrl;

        @Value("${service.urls.analytics-service}")
        private String analyticsServiceUrl;

        public static void main(String[] args) {
                SpringApplication.run(ApiGatewayApplication.class, args);
        }

        @Bean
        public RouteLocator routerBuilder(RouteLocatorBuilder routeLocatorBuilder,
                        AuthenticationGatewayFilterFactory authFilter) {
                return routeLocatorBuilder.routes()
                                // Public Route: No filter applied
                                .route("auth-service", r -> r.path("/auth/**")
                                                .uri(authServiceUrl))

                                // Secured Route
                                .route("user-service", r -> r.path("/users/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(userServiceUrl))

                                // restaurant service public routes
                                .route("restaurant-service-public", r -> r.path("/restaurants/**")
                                                .and().method(HttpMethod.GET) // This route only matches GET requests
                                                .uri(restaurantServiceUrl))

                                // restaurant service secured routes
                                .route("restaurant-service-secured", r -> r.path("/restaurants/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(restaurantServiceUrl))

                                // Secured Route
                                .route("media-service", r -> r.path("/media/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(mediaServiceUrl))

                                //public reservation route
                                .route("reservation-service", r -> r.path("/reservations/restaurants/{restaurantId}/slots")
                                                .uri(reservationServiceUrl))

                                // Secured Route
                                .route("reservation-service", r -> r.path("/reservations/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(reservationServiceUrl))

                                .route("menu-item-service-public", r -> r.path("/menu-items/**")
                                                .and().method(HttpMethod.GET)
                                                .uri(menuItemServiceUrl))

                                .route("menu-item-service-secured", r -> r.path("/menu-items/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(menuItemServiceUrl))

                                .route("analytics-service", r -> r.path("/analytics/**")
                                                .filters(f -> f.filter(authFilter.apply(
                                                                new AuthenticationGatewayFilterFactory.Config())))
                                                .uri(analyticsServiceUrl))

                                .build();
        }

        @Bean
        public RestTemplate restTemplate() {
                return new RestTemplate();
        }
}
