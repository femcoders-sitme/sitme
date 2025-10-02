package com.femcoders.sitme.user.services.user;

import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest userRequest, MultipartFile file);
    UserResponse uploadUserImage(Long id, MultipartFile file);
    void deleteUser(Long id);
    void deleteUserImage(Long id);
}
