package com.femcoders.sitme.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserUpdateRequest;
import com.femcoders.sitme.user.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {
        UserResponse userResponse = userService.getUserById(id);
        return ResponseEntity.ok(userResponse);
    }

    @PostMapping("/{id}/image")
    @Operation(summary = "Upload profile image for a user")
    @ApiResponse(responseCode = "200", description = "Image uploaded successfully")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> uploadUserImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {

        UserResponse userResponse = userService.uploadUserImage(id, file);
        return ResponseEntity.ok(userResponse);
    }

    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user profile",
            description = "Allows an authenticated user to update their profile or admin to update any user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> updateUser(
            @PathVariable Long id,
            @Valid @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        UserUpdateRequest request = mapper.readValue(userJson, UserUpdateRequest.class);

        UserResponse updatedUser = userService.updateUser(id, request, file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile updated successfully", updatedUser));
    }

    @DeleteMapping("/{id}/image")
    @Operation(summary = "Delete user's profile image",
            description = "Deletes the profile image from Cloudinary and clears it in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<SuccessResponse> deleteUserImage(@PathVariable Long id) {
        userService.deleteUserImage(id);

        return ResponseEntity.ok(
                new SuccessResponse(true, "User image deleted successfully", LocalDateTime.now())
        );
    }
}
