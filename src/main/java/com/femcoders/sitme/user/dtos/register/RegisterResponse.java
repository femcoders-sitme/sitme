package com.femcoders.sitme.user.dtos.register;

import com.femcoders.sitme.user.Role;

public record RegisterResponse(
        String username,
        String email,
        Role role
) {
}
