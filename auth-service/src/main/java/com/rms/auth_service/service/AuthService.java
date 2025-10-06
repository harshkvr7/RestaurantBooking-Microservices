package com.rms.auth_service.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.rms.auth_service.dto.AuthRequestDto;
import com.rms.auth_service.dto.AuthResponseDto;
import com.rms.auth_service.dto.ClaimsResponseDto;
import com.rms.auth_service.dto.UserRequestDto;
import com.rms.auth_service.dto.UserResponseDto;
import com.rms.auth_service.dto.VerificationEmailRequestDto;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final RestTemplate restTemplate;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${service.urls.user-service}")
    private String userServiceUrl;

    public UserResponseDto signup(UserRequestDto userRequestDto) {
        try {
            restTemplate.getForObject(userServiceUrl + "/users/email/" +
                    userRequestDto.email(), UserResponseDto.class);

            throw new RuntimeException("Email is already in use.");
        } catch (HttpClientErrorException.NotFound e) {
        }

        String hashedPassword = passwordEncoder.encode(userRequestDto.password());
        UserRequestDto requestToSend = new UserRequestDto(
                userRequestDto.username(),
                userRequestDto.email(),
                hashedPassword,
                userRequestDto.role());

        UserResponseDto createdUser = restTemplate.postForObject(userServiceUrl + "/users", requestToSend,
                UserResponseDto.class);

        if (createdUser != null) {
            String token = restTemplate.postForObject(userServiceUrl + "/users" + "/" + createdUser.id() + "/verification-token",
                    null, String.class);

            VerificationEmailRequestDto emailRequest = new VerificationEmailRequestDto(createdUser.email(),
                    createdUser.username(), token);
            kafkaTemplate.send("verification-email-topic", emailRequest);
        }

        return createdUser;
    }

    public AuthResponseDto login(AuthRequestDto authRequestDto) {
        UserResponseDto user = restTemplate.getForObject(userServiceUrl + "/users" + "/email/" + authRequestDto.email(),
                UserResponseDto.class);

        if (user == null) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.verified()) {
            throw new RuntimeException("Please verify your email before logging in.");
        }

        if (passwordEncoder.matches(authRequestDto.password(), user.password())) {
            String token = jwtService.generateToken(user.id(), user.email(), user.role());
            return new AuthResponseDto(token);
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    public String verifyEmail(String token) {
        return restTemplate.getForObject(userServiceUrl + "/users" + "/verify?token=" + token, String.class);
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }

    public ClaimsResponseDto getClaims(String token) {
        jwtService.validateToken(token); 
        Integer userId = jwtService.extractUserId(token);
        String role = jwtService.extractUserRole(token);
        return new ClaimsResponseDto(userId, role);
    }
}
