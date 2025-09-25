package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    List<UserResponse> getAllUsers();
}
