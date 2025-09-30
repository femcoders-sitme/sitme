package com.femcoders.sitme.user.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.services.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Users", description = "Endpoints for managing users")
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // GET ALL
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get all users",
            description = "Returns a list of all users. Only accessible to admins."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<List<UserResponse>>> getAllUsers() {

        List<UserResponse> users = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Users list retrieved successfully", users));
    }

    // GET USER BY ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "Get user by id",
            description = "Returns a user profile by its id. Only accessible to admins."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @Parameter(description = "ID of the user profile", required = true)
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> getUserById(
            @PathVariable Long id) {

        UserResponse userResponse = userService.getUserById(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile retrieved successfully", userResponse));
    }

    // UPDATE USER
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user",
            description = "Allows an authenticated admin to update any user profile")
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
        UserRequest request = mapper.readValue(userJson, UserRequest.class);

        UserResponse updatedUser = userService.updateUser(id, request, file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile updated successfully", updatedUser));
    }

    // UPLOAD IMAGE
    @PostMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload profile image for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> uploadUserImage(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file
    ) {

        UserResponse userResponse = userService.uploadUserImage(id, file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile updated successfully", userResponse));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Allows an admin to delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<Void>> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User deleted successfully"));
    }

    @DeleteMapping("/{id}/image")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user's profile image",
            description = "Deletes the profile image from Cloudinary and clears it in the database.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<SuccessResponse<Void>> deleteUserImage(@PathVariable Long id) {

        userService.deleteUserImage(id);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User image deleted successfully"));
    }
}
