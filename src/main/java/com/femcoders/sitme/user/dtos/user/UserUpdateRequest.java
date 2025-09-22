package com.femcoders.sitme.user.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 10, message = "Name must contain min 2 and max 10 characters")
        String username,

        @NotBlank(message = "Email is required")
        @Size(min = 10, max = 60, message = "Email must contain min 10 and max 60 characters")
        String email,

        @NotBlank(message = "Password is required")
        @Pattern(message = "Password must contain a minimum of 8 characters, including a number, one uppercase letter, one lowercase letter and one special character",
                regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&+=.])(?=\\S+$).{8,}$")
        String password) {
}
