package com.femcoders.sitme.user.services;

import com.femcoders.sitme.security.userdetails.CustomUserDetails;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
        return UserMapper.entityToDto(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest, MultipartFile file) {

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNameNotFoundException("with id " + id));

        existingUser.setUsername(userUpdateRequest.username());
        existingUser.setEmail(userUpdateRequest.email());
        existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.password()));

        if (file != null && !file.isEmpty()) {
            cloudinaryService.deleteEntityImage(existingUser);
            cloudinaryService.uploadEntityImage(existingUser, file, "sitme/users");
        }

        User updatedUser = userRepository.save(existingUser);
        return UserMapper.entityToDto(updatedUser);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Override
    @Transactional
    public UserResponse updateProfile(Long id, UserUpdateRequest request) {

        User profileUser = userRepository.findById(id)
          .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
              && !profileUser.getId().equals(userDetails.getId())) {
          throw new AccessDeniedException("You are not allowed to update this user");
        }

        profileUser.setUsername(request.username());
        profileUser.setEmail(request.email());
        profileUser.setPassword(passwordEncoder.encode(request.password()));

        User updatedProfile = userRepository.save(profileUser);
        return UserMapper.entityToDto(updatedProfile);
    }

    @PreAuthorize("hasRole('ADMIN')")
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
    public void deleteUserImage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException(User.class.getSimpleName(), id));

        cloudinaryService.deleteEntityImage(user);

        userRepository.save(user);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id));
        userRepository.deleteById(id);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(UserMapper::entityToDto)
                .collect(Collectors.toList());
    }
}

