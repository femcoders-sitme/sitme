package com.femcoders.sitme.user.dtos.user;

import com.femcoders.sitme.user.Role;
import com.femcoders.sitme.user.User;

public interface UserMapper {
    User dtoToEntity (UserRequest userRequest, Role role);
    UserResponse entityToDto (User user);
}
