package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {
    UserResponse getUserById(Long id);
    List<UserResponse> getAllUsers();
    UserResponse updateProfile(Long id, UserUpdateRequest request);
    void deleteUser(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest, MultipartFile file);
    UserResponse uploadUserImage(Long id, MultipartFile file);
    void deleteUserImage(Long id);
}
