package com.femcoders.sitme.user.dtos.register;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import org.springframework.stereotype.Component;

@Component
public class RegisterMapper {
    public static User dtoToEntity(RegisterRequest registerRequest) {
        return User.builder()
                .username(registerRequest.username())
                .email(registerRequest.email())
                .password(registerRequest.password())
                .role(Role.USER)
                .build();
    }
    public static RegisterResponse entityToDto(User user){
        return new RegisterResponse(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}
