package com.rms.user_service.mapper;

import org.springframework.stereotype.Service;

import com.rms.user_service.dto.UserAuthResponseDto;
import com.rms.user_service.dto.UserRequestDto;
import com.rms.user_service.dto.UserResponseDto;
import com.rms.user_service.model.User;

@Service
public class UserMapper {
    
    public User toUser(UserRequestDto userDto) {
        User user = new User();

        user.setUsername(userDto.username());
        user.setEmail(userDto.email());
        user.setPassword(userDto.password());
        user.setRole(userDto.role());

        return user;
    }

    public UserResponseDto toUserResponseDto(User user) {
        return new UserResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getVerified(),
            user.getRole()
        );
    }

    public UserAuthResponseDto toUserAuthResponseDto(User user) {
        return new UserAuthResponseDto(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            user.getVerified(),
            user.getRole()
        );
    }
}
