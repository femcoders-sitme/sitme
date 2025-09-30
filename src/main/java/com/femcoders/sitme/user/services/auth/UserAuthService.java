package com.femcoders.sitme.user.services.auth;

import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.dtos.register.RegisterResponse;

public interface UserAuthService {

    RegisterResponse addUser(RegisterRequest registerRequest);
    LoginResponse login(LoginRequest loginRequest);
}
