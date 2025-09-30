package com.femcoders.sitme.user.services.user;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.repository.UserRepository;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;


    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
        return UserMapper.entityToDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserRequest userRequest, MultipartFile file) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));

        existingUser.setUsername(userRequest.username());
        existingUser.setEmail(userRequest.email());

        if (userRequest.password() != null && !userRequest.password().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userRequest.password()));
        }

        if (file != null && !file.isEmpty()) {
            cloudinaryService.uploadEntityImage(existingUser, file, "sitme/users");
            cloudinaryService.deleteEntityImage(existingUser);
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.entityToDto(updatedUser);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse uploadUserImage(Long id, MultipartFile file) {
        User user = userRepository.findById(id)
                    .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        cloudinaryService.uploadEntityImage(user, file, "sitme/users");

        userRepository.save(user);

        return UserMapper.entityToDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public void deleteUserImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        cloudinaryService.deleteEntityImage(user);

        userRepository.save(user);
    }
}

