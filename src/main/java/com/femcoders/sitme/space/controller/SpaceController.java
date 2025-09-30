package com.femcoders.sitme.space.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.femcoders.sitme.shared.responses.SuccessResponse;
import com.femcoders.sitme.space.SpaceType;
import com.femcoders.sitme.space.dto.SpaceRequest;
import com.femcoders.sitme.space.dto.SpaceResponse;
import com.femcoders.sitme.space.services.SpaceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
@Tag(name = "Spaces", description = "Endpoints for managing spaces")
@RequestMapping("/api/spaces")
public class SpaceController {

    private final SpaceServiceImpl spaceService;

    @Operation(
            summary = "Get all spaces",
            description = "Returns a list of all registered spaces."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spaces retrieved successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceResponse.class))))
    })
    @GetMapping
    public ResponseEntity<List<SpaceResponse>> getAllSpaces() {
        return ResponseEntity.ok(spaceService.getAllSpaces());
    }
    @Operation(
            summary = "Filter spaces by type",
            description = "Returns a list of spaces filtered by type (ROOM, TABLE)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Spaces filtered successfully",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = SpaceResponse.class))))
    })
    @GetMapping("/filter/type")
    public ResponseEntity<List<SpaceResponse>> getSpacesByType(@RequestParam SpaceType type) {
        return ResponseEntity.ok(spaceService.getSpacesByType(type));
    }

    @Operation(
            summary = "Get a space by id",
            description = "Returns details of a specific space."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Space retrieved successfully",
                    content = @Content(schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "404", description = "Space not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpaceResponse> getSpaceById(@PathVariable Long id) {
        SpaceResponse space = spaceService.getSpaceById(id);
        return ResponseEntity.ok(space);
    }

    @Operation(
            summary = "Create a new space",
            description = "Adds a new space to the system. Only users with ADMIN role can perform this action."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Space created successfully",
                    content = @Content(schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Space already exists",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role",
                    content = @Content)
    })
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<SpaceResponse>> addSpace(
            @Valid @RequestPart("space") String spaceJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        SpaceRequest request = mapper.readValue(spaceJson, SpaceRequest.class);

        SpaceResponse newSpace = spaceService.addSpace(request, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SuccessResponse.of("Space created successfully", newSpace));
    }

    @Operation(
            summary = "Update a space",
            description = "Allows you to make changes to the existing space. Only users with the ADMIN role can perform this action"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Space updated successfully",
                    content = @Content(schema = @Schema(implementation = SpaceResponse.class))),
            @ApiResponse(responseCode = "400", description = "Space already exists",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "User does not have ADMIN role",
                    content = @Content)
    })
    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SuccessResponse<SpaceResponse>> updateSpace(
            @PathVariable Long id,
            @Valid @RequestPart("space") String spaceJson,
            @RequestPart(value = "file", required = false) MultipartFile file) throws JsonProcessingException {

        ObjectMapper mapper = new ObjectMapper();
        SpaceRequest request = mapper.readValue(spaceJson, SpaceRequest.class);

        SpaceResponse updateSpace = spaceService.updateSpace(id, request, file);

        return ResponseEntity.status(HttpStatus.OK)
                .body(SuccessResponse.of("Space updated successfully", updateSpace));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSpace(@PathVariable Long id) {
        spaceService.deleteSpace(id);
        return ResponseEntity.noContent().build();
    }
}