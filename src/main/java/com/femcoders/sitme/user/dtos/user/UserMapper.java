package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;

public class UserMapper {

    public static User dtoToEntity(UserRequest userRequestDTO, Role role) {
        return User.builder()
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password(userRequestDTO.password())
                .role(role)
                .build();
    }

    public static UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getImageUrl(),
                user.getCloudinaryImageId()
        );
    }
}
