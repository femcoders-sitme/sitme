package com.femcoders.sitme.user.services;

import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.UserRequest;
import com.femcoders.sitme.user.dtos.register.UserResponse;

public interface UserService {

    UserResponse addUser(UserRequest userRequest);
    LoginResponse login(LoginRequest loginRequest);
}
