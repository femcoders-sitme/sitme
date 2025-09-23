package com.femcoders.sitme.user.services;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
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

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public UserResponse uploadUserImage(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        cloudinaryService.uploadEntityImage(user, file, "sitme/users");

        userRepository.save(user);

        return userMapper.entityToDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public void deleteUserImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        cloudinaryService.deleteEntityImage(user);

        userRepository.save(user);
    }
}
