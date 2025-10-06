package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;

public class UserMapper {

    public static User dtoToEntity(UserRequest request, Role role) {
        return User.builder()
                .username(request.username())
                .email(request.email())
                .password(request.password())
                .role(role)
                .build();
    }

    public static UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getImageUrl(),
                user.getCloudinaryImageId()
        );
    }
}
