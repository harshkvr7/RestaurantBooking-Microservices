package com.rms.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.rms.api_gateway.dto.ClaimsResponseDto;

@Component
public class AuthenticationGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthenticationGatewayFilterFactory.Config> {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${service.urls.auth-service}")
    private String authServiceUrl;

    public AuthenticationGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {

            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                throw new RuntimeException("Missing authorization header");
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    ClaimsResponseDto claims = restTemplate
                            .getForObject(authServiceUrl + "/auth/claims?token=" + token, ClaimsResponseDto.class);

                    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                            .header("X-User-Id", String.valueOf(claims.userId()))
                            .header("X-User-Role", claims.role())
                            .build();

                    return chain.filter(exchange.mutate().request(mutatedRequest).build());

                } catch (RestClientException e) {
                    System.err.println("Invalid access... Token validation failed.");
                    throw new RuntimeException("Unauthorized access to application");
                }
            }

            return chain.filter(exchange);
        });
    }

    public static class Config {

    }
}
