package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public User dtoToEntity(UserRequest userRequestDTO, Role role) {
        return User.builder()
                .username(userRequestDTO.username())
                .email(userRequestDTO.email())
                .password(userRequestDTO.password())
                .role(role)
                .build();
    }

    @Override
    public UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getImageUrl(),
                user.getCloudinaryImageId(),
                user.getCreatedAt(),
                user.getReservations()
        );
    }
}
