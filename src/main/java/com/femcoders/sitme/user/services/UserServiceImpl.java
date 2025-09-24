package com.femcoders.sitme.user.services;

import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.User;
import com.femcoders.sitme.user.dtos.user.UserMapper;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import com.femcoders.sitme.user.exceptions.UserNameNotFoundException;
import com.femcoders.sitme.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

  /*  @Override
    @Transactional
    public UserResponse updateProfile(UserUpdateRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getUserName();

        User profileUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNameNotFoundException(username));

        profileUser.setUsername(request.username());
        profileUser.setEmail(request.email());
        profileUser.setPassword(passwordEncoder.encode(request.password()));

        User updateProfile =  userRepository.save(profileUser);
        return userMapper.entityToDto(updateProfile);
    }*/
  @PreAuthorize("hasAnyRole('ADMIN','USER')")
  @Override
  @Transactional
  public UserResponse updateProfile(Long id, UserUpdateRequest request) {

      User profileUser = userRepository.findById(id)
              .orElseThrow(() -> new UsernameNotFoundException("User not found with id " + id));

      // Obtener el usuario autenticado
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

      // Validación: si no es admin, solo puede actualizar su propio perfil
      if (!userDetails.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
              && !profileUser.getId().equals(userDetails.getId())) {
          throw new AccessDeniedException("You are not allowed to update this user");
      }

      profileUser.setUsername(request.username());
      profileUser.setEmail(request.email());
      profileUser.setPassword(passwordEncoder.encode(request.password()));

      User updatedProfile = userRepository.save(profileUser);
      return userMapper.entityToDto(updatedProfile);
  }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public UserResponse deleteUser(Long id) {
        User eliminateUser = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id " + id));

        UserResponse deletedUser = userMapper.entityToDto(eliminateUser);
        userRepository.delete(eliminateUser);
        return deletedUser;
    }
}

