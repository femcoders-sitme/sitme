package com.femcoders.sitme.user.services;

public interface UserService {

    UserResponse addUser(UserRequest userRequest);
    LoginResponse login(LoginRequest loginRequest);
}
