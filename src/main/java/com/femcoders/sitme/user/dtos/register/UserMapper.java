package com.femcoders.sitme.user.dtos.register;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public User dtoToEntity(UserRequest userRequest) {
        return User.builder()
                .username(userRequest.username())
                .email(userRequest.email())
                .password(userRequest.password())
                .role(Role.USER)
                .build();
    }
    public UserResponse entityToDto(User user){
        return new UserResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
