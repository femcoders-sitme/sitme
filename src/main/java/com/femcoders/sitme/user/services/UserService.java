package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

public interface UserService {
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserUpdateRequest userUpdateRequest);
    UserResponse uploadUserImage(Long id, MultipartFile file);
    void deleteUserImage(Long id);
}
