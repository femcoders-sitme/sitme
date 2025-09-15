package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.dtos.user.UserResponse;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserService {
    UserResponse getUserById(Long id);
}
