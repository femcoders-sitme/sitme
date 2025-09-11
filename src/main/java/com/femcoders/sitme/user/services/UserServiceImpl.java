package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.register.UserMapper;
import com.femcoders.sitme.user.dtos.register.UserRequest;
import com.femcoders.sitme.user.dtos.register.UserResponse;
import com.femcoders.sitme.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends UserService{
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserResponse addUser(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.username())) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(userRequest.email())) {
            throw new IllegalArgumentException("Email already registered");
        }

        User newUser = userMapper.dtoToEntity(userRequest);
        newUser.setPassword(bCryptPasswordEncoder.encode(userRequest.password()));
        User savedUser = userRepository.save(newUser);
        return userMapper.entityToDto(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        return null;
    }
}
