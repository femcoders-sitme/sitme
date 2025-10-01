package com.femcoders.sitme.user.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.security.userdetails.CustomUserDetails;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import com.femcoders.sitme.user.dtos.user.UserRequest;
import com.femcoders.sitme.user.dtos.user.UserResponse;
import com.femcoders.sitme.user.services.userprofile.UserProfileServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserProfileServiceImpl userProfileService;

    @GetMapping
    @Operation(summary = "Get my profile",
            description = "Allows an authenticated user (admin or regular) to retrieve her/his own profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserResponse userProfile = userProfileService.getMyProfile(userDetails);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile retrieved successfully", userProfile));
    }

    @PutMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Update my profile",
            description = "Allows an authenticated user (admin or regular) to update her own profile information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<SuccessResponse<UserResponse>> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestPart("user") String userJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        UserRequest request = mapper.readValue(userJson, UserRequest.class);

        UserResponse updatedProfile = userProfileService.updateMyProfile(userDetails, request, file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("User profile updated successfully", updatedProfile));
    }
}
