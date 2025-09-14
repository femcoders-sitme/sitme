package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserResponse getUserById(Long id, UserDetails userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return userMapper.entityToDto(user);
    }

}
