package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import com.femcoders.sitme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));
        return userMapper.entityToDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNameNotFoundException("with id " + id));

        existingUser.setUsername(userUpdateRequest.username());
        existingUser.setEmail(userUpdateRequest.email());
        existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.password()));

        User updatedUser = userRepository.save(existingUser);
        return userMapper.entityToDto(updatedUser);
    }
}
