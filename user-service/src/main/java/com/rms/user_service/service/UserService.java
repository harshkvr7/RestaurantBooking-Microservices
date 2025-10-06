package com.rms.user_service.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.rms.user_service.dto.UserAuthResponseDto;
import com.rms.user_service.dto.UserRequestDto;
import com.rms.user_service.dto.UserResponseDto;
import com.rms.user_service.exception.UserNotFoundException;
import com.rms.user_service.mapper.UserMapper;
import com.rms.user_service.model.User;
import com.rms.user_service.model.VerificationToken;
import com.rms.user_service.repository.UserRepository;
import com.rms.user_service.repository.VerificationTokenRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final VerificationTokenRepository tokenRepository;

    public UserResponseDto createUser(UserRequestDto userRequestDto) {
        User user = userMapper.toUser(userRequestDto);

        userRepository.save(user);

        return userMapper.toUserResponseDto(user);
    }

    public String createVerificationToken(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = new VerificationToken(token, user);

        tokenRepository.save(verificationToken);

        return token;
    }

    public UserResponseDto getUserById(Integer userId) {
        return userMapper.toUserResponseDto(
                userRepository.findById(userId).orElse(null));
    }

    public UserAuthResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return userMapper.toUserAuthResponseDto(user);
    }

    @Transactional
    public UserResponseDto updateUser(Integer userId, UserRequestDto userRequestDto) {
        User user = userRepository.findById(userId).orElse(null);

        user.setUsername(userRequestDto.username());

        return userMapper.toUserResponseDto(
                userRepository.save(user));
    }

    public void deleteUser(Integer userId) {
        userRepository.deleteById(userId);
    }

    @Transactional
    public String confirmVerification(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token);

        if (verificationToken == null || verificationToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Invalid or expired verification token.");
        }

        User user = verificationToken.getUser();
        user.setVerified(true);
        userRepository.save(user);

        tokenRepository.delete(verificationToken);

        return "Email verified successfully.";
    }
}
