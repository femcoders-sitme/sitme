package com.femcoders.sitme.user.dtos.login;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "Username or e-mail is required")
        String identifier,

        @NotBlank(message = "Password is required")
        String password
) {
}
