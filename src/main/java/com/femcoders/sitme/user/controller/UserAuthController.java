package com.femcoders.sitme.user.controller;

import com.femcoders.sitme.shared.responses.SuccessResponse;
import com.femcoders.sitme.user.dtos.login.LoginRequest;
import com.femcoders.sitme.user.dtos.login.LoginResponse;
import com.femcoders.sitme.user.dtos.register.RegisterRequest;
import com.femcoders.sitme.user.dtos.register.RegisterResponse;
import com.femcoders.sitme.user.services.UserAuthServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class UserAuthController {

    private final UserAuthServiceImpl userService;

    @PostMapping("/register")
    @Operation(summary = "User register",
            description = "Registers a new user in the system"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User registered successfully"),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "409", description = "Conflict (username or email already exists)")
    })
    public ResponseEntity<SuccessResponse<RegisterResponse>> register(@RequestBody @Valid RegisterRequest registerRequest) {
        RegisterResponse registerResponse = userService.addUser(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("User registered successfully", registerResponse)
                );
    }

    @PostMapping("/login")
    @Operation(summary = "User login",
            description = "Authenticates user and returns JWT token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid/Bad credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<SuccessResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {

        LoginResponse loginResponse = userService.login(loginRequest);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Login successful", loginResponse)
                );
    }
}
