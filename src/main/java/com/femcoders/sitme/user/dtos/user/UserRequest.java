package com.femcoders.sitme.user.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 10, message = "Name must contain min 2 and max 10 characters")
        String username,
        @NotBlank(message = "Email is required")
        @Size(min = 10, max = 60, message = "Email must contain min 2 and max 60 characters")
        String email,
        @NotBlank(message = "Password is required")
        String password
) {
}
