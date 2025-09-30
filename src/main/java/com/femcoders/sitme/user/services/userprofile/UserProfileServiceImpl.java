package com.femcoders.sitme.user.services.userprofile;

import com.femcoders.sitme.cloudinary.service.CloudinaryService;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.exceptions.EntityNotFoundException;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CloudinaryService cloudinaryService;

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Override
    public UserResponse getMyProfile(CustomUserDetails userDetails) {

        User userProfile = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDetails.getId()));

        return UserMapper.entityToDto(userProfile);
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Override
    @Transactional
    public UserResponse updateMyProfile(CustomUserDetails userDetails, UserRequest userRequest, MultipartFile file) {

        User userProfile = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new EntityNotFoundException("User", userDetails.getId()));

        if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && !userProfile.getId().equals(userDetails.getId())) {
            throw new AccessDeniedException("You are not allowed to update this profile");
        }

        userProfile.setUsername(userRequest.username());
        userProfile.setEmail(userRequest.email());

        if (userRequest.password() != null && !userRequest.password().isBlank()) {
            userProfile.setPassword(passwordEncoder.encode(userRequest.password()));
        }

        if (file != null && !file.isEmpty()) {
            cloudinaryService.uploadEntityImage(userProfile, file, "sitme/users");
            cloudinaryService.deleteEntityImage(userProfile);
        }

        User updatedProfile = userRepository.save(userProfile);

        return UserMapper.entityToDto(updatedProfile);
    }
}
