package com.femcoders.sitme.user.services.userprofile;

import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileService {

    UserResponse getMyProfile(CustomUserDetails userDetails);
    UserResponse updateMyProfile(CustomUserDetails userDetails, UserRequest userRequest, MultipartFile file);
}
